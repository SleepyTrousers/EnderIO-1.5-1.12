package crazypants.enderio.teleport.telepad;

import java.util.EnumSet;
import java.util.List;
import java.util.Queue;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.util.ForgeDirection;
import cofh.api.energy.EnergyStorage;

import com.google.common.collect.Lists;
import com.google.common.collect.Queues;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import crazypants.enderio.api.teleport.ITelePad;
import crazypants.enderio.api.teleport.TravelSource;
import crazypants.enderio.machine.AbstractMachineEntity;
import crazypants.enderio.machine.MachineSound;
import crazypants.enderio.machine.PacketPowerStorage;
import crazypants.enderio.machine.PacketProgress;
import crazypants.enderio.network.PacketHandler;
import crazypants.enderio.power.IInternalPowerReceiver;
import crazypants.enderio.rail.PacketTeleportEffects;
import crazypants.enderio.rail.TeleporterEIO;
import crazypants.enderio.teleport.TravelController;
import crazypants.enderio.teleport.anchor.TileTravelAnchor;
import crazypants.enderio.teleport.packet.PacketTravelEvent;
import crazypants.enderio.teleport.telepad.PacketTeleport.Type;
import crazypants.util.BlockCoord;
import crazypants.util.IProgressTile;
import crazypants.util.Util;

public class TileTelePad extends TileTravelAnchor implements IInternalPowerReceiver, ITelePad, IProgressTile {
  
  private boolean inNetwork;

  private EnumSet<ForgeDirection> connections = EnumSet.noneOf(ForgeDirection.class);

  private EnergyStorage energy = new EnergyStorage(100000, 1000, 1000);

  private TileTelePad master = null;

  private boolean autoUpdate = false;

  private BlockCoord target = new BlockCoord();
  private int targetDim = Integer.MIN_VALUE;
  
  private int lastSyncPowerStored;

  private Queue<Entity> toTeleport = Queues.newArrayDeque();
  private int powerUsed;
  private int maxPower;
  private int lastSyncPowerUsed;

  private static final ResourceLocation activeRes = AbstractMachineEntity.getSoundFor("telepad.active");
  private MachineSound activeSound = null;
  
  private boolean redstoneActivePrev;
  
  public static final String TELEPORTING_KEY = "eio:teleporting";
  public static final String PROGRESS_KEY = "teleportprogress";

  @Override
  public void updateEntity() {
    super.updateEntity();
    // my master is gone!
    if(master != null && master.isInvalid()) {
      master.breakNetwork();
    }
    
    if (worldObj == null) {
      return;
    }

    if(autoUpdate) {
      updateConnectedState(true);
      autoUpdate = false;
    }

    if(targetDim == Integer.MIN_VALUE) {
      targetDim = worldObj.provider.dimensionId;
    }
    
    if(worldObj.isRemote) {
      if(active()) {
        if(activeSound == null) {
          activeSound = new MachineSound(activeRes, xCoord, yCoord, zCoord, 0.01f, 1);
          playSound();
        }
        activeSound.setVolume(Math.min(activeSound.getVolume() + 0.1f, 0.5f));
        activeSound.setPitch(1 + getProgress());
        updateQueuedEntities();
      } else if(!active() && activeSound != null) {
        if(activeSound.getVolume() > 0) {
          activeSound.setVolume(activeSound.getVolume() - 0.1f);
        } else {
          activeSound.endPlaying();
          activeSound = null;
        }
      }
    } else {
      if(active()) {
        if(powerUsed >= maxPower) {
          teleport(toTeleport.poll());
          powerUsed = 0;
        } else {
          powerUsed += energy.extractEnergy(getUsage(), false);
        }
        if(worldObj.getTotalWorldTime() % 5 == 0) {
          updateQueuedEntities();
        }
      }

      boolean powerChanged = (lastSyncPowerStored != getEnergyStored() && worldObj.getTotalWorldTime() % 5 == 0);
      if(powerChanged) {
        lastSyncPowerStored = getEnergyStored();
        PacketHandler.sendToAllAround(new PacketPowerStorage(this), this);
      }
      boolean progressChanged = (lastSyncPowerUsed != powerUsed);
      if(progressChanged) {
        lastSyncPowerUsed = getEnergyStored();
        PacketHandler.sendToAllAround(new PacketProgress(this), this);
      }
    }
  }

  @SideOnly(Side.CLIENT)
  private void playSound() {
    FMLClientHandler.instance().getClient().getSoundHandler().playSound(activeSound);
  }

  private void updateQueuedEntities() {
    if(worldObj.isRemote) {
      if(active()) {
        getCurrentTarget().getEntityData().setFloat(PROGRESS_KEY, getProgress());
      }
    }
    List<Entity> toRemove = Lists.newArrayList();
    for (Entity e : toTeleport) {
      if(!isEntityInRange(e) || e.isDead) {
        toRemove.add(e);
      }
    }
    for (Entity e : toRemove) {
      dequeueTeleport(e, true);
    }
  }

  public void updateConnectedState(boolean fromBlock) {

    for (BlockCoord bc : getSurroundingCoords()) {
      TileEntity te = bc.getTileEntity(worldObj);
      ForgeDirection con = Util.getDirFromOffset(xCoord - bc.x, 0, zCoord - bc.z);
      if(te instanceof TileTelePad) {
        // let's find the master and let him do the work
        if(((TileTelePad) te).isMaster() && fromBlock) {
          ((TileTelePad) te).updateConnectedState(false);
          return;
        }
        // otherwise we either are the master or this is a secondary call, so update connections
        if(con != ForgeDirection.UNKNOWN && !((TileTelePad) te).inNetwork) {
          connections.add(con);
        }
      } else {
        connections.remove(con);
        if(master == this) {
          breakNetwork();
          updateBlock();
        } else if(con != ForgeDirection.UNKNOWN) {
          if(inNetwork && master != null && fromBlock) {
            master.updateConnectedState(false);
          }
        }
      }
    }
    if(isMaster() && !inNetwork) {
      inNetwork = formNetwork();
      updateBlock();
      if(inNetwork) {
        if(target.equals(new BlockCoord())) {
          target = new BlockCoord(this);
        }
      }
    }
  }

  public void updateRedstoneState() {
    if(!inNetwork()) {
      return;
    }

    boolean redstone = isPoweredRedstone();
    if(!master.redstoneActivePrev && redstone) {
      teleportAll();
    }
    master.redstoneActivePrev = redstone;
  }

  private boolean formNetwork() {
    List<TileTelePad> temp = Lists.newArrayList();
    if(isMaster()) {
      for (BlockCoord c : getSurroundingCoords()) {
        TileEntity te = c.getTileEntity(worldObj);
        if(!(te instanceof TileTelePad)) {
          return false;
        }
        temp.add((TileTelePad) te);
      }
      for (TileTelePad te : temp) {
        te.master = this;
        te.inNetwork = true;
        te.updateBlock();
        te.updateNeighborTEs();
      }
      this.master = this;
      return true;
    }
    return false;
  }

  private void breakNetwork() {
    master = null;
    inNetwork = false;
    for (BlockCoord c : getSurroundingCoords()) {
      TileEntity te = c.getTileEntity(worldObj);
      if(te instanceof TileTelePad) {
        TileTelePad telepad = (TileTelePad) te;
        telepad.master = null;
        telepad.inNetwork = false;
        telepad.updateBlock();
        telepad.updateNeighborTEs();
      }
    }
  }

  private List<BlockCoord> getSurroundingCoords() {
    List<BlockCoord> ret = Lists.newArrayList();
    for (int x = -1; x <= 1; x++) {
      for (int z = -1; z <= 1; z++) {
        if(x != 0 || z != 0) {
          ret.add(new BlockCoord(xCoord + x, yCoord, zCoord + z));
        }
      }
    }
    return ret;
  }

  private void updateNeighborTEs() {
    BlockCoord bc = new BlockCoord(this);
    for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
      BlockCoord neighbor = bc.getLocation(dir);
      Block block = neighbor.getBlock(worldObj);
      if(!(block instanceof BlockTelePad)) {
        block.onNeighborChange(worldObj, neighbor.x, neighbor.y, neighbor.z, xCoord, yCoord, zCoord);
      }
    }
  }

  @Override
  public boolean canUpdate() {
    return true;
  }

  @Override
  protected void writeCustomNBT(NBTTagCompound root) {
    super.writeCustomNBT(root);
    energy.writeToNBT(root);
    target.writeToNBT(root);
    root.setInteger("targetDim", targetDim);
    root.setBoolean("redstoneActive", redstoneActivePrev);
  }

  @Override
  protected void readCustomNBT(NBTTagCompound root) {
    super.readCustomNBT(root);
    energy.readFromNBT(root);
    target = BlockCoord.readFromNBT(root);
    targetDim = root.getInteger("targetDim");
    redstoneActivePrev = root.getBoolean("redstoneActive");
    autoUpdate = true;
  }

  @Override
  public Packet getDescriptionPacket() {
    S35PacketUpdateTileEntity pkt = (S35PacketUpdateTileEntity) super.getDescriptionPacket();
//    pkt.func_148857_g().setBoolean("inNetwork", inNetwork);
    return pkt;
  }

  @Override
  public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt) {
    super.onDataPacket(net, pkt);
//    this.inNetwork = pkt.func_148857_g().getBoolean("inNetwork");
  }

  public int getPowerScaled(int scale) {
    return (int) ((((float) getEnergyStored()) / ((float) getMaxEnergyStored())) * scale);
  }

  public int getProgressScaled(int scale) {
    return (int) (getProgress() * scale);
  }

  private int calculateTeleportPower() {
    if (worldObj.provider.dimensionId == targetDim) {
      this.maxPower = new BlockCoord(this).distance(target) * 1000;
    } else {
      this.maxPower = 100000;
    }
    return this.maxPower;
  }

  public boolean active() {
    return !toTeleport.isEmpty();
  }

  public Entity getCurrentTarget() {
    return toTeleport.peek();
  }

  /* IProgressTile */

  @Override
  public float getProgress() {
    return ((float) powerUsed) / ((float) maxPower);
  }

  @Override
  public void setProgress(float progress) {
    this.powerUsed = progress < 0 ? 0 : (int) (((float) maxPower) * progress);
  }

  @Override
  public TileEntity getTileEntity() {
    return this;
  }

  /* ITelePad */

  @Override
  public boolean isMaster() {
    return connections.size() == 4;
  }

  @Override
  public TileTelePad getMaster() {
    return master;
  }

  @Override
  public boolean inNetwork() {
    return inNetwork;
  }

  @Override
  public int getX() {
    if(inNetwork) {
      return master.target.x;
    }
    return target.x;
  }

  @Override
  public int getY() {
    if(inNetwork) {
      return master.target.y;
    }
    return target.y;
  }

  @Override
  public int getZ() {
    if(inNetwork) {
      return master.target.z;
    }
    return target.z;
  }
  
  @Override
  public int getTargetDim() {
    return targetDim;
  }

  @Override
  public ITelePad setX(int x) {
    if(inNetwork()) {
      setCoords(new BlockCoord(x, target.y, target.z));
      return master;
    }
    return null;
  }

  @Override
  public ITelePad setY(int y) {
    if(inNetwork()) {
      setCoords(new BlockCoord(target.x, y, target.z));
      return master;
    }
    return null;
  }

  @Override
  public ITelePad setZ(int z) {
    if(inNetwork()) {
      setCoords(new BlockCoord(target.x, target.y, z));
      return master;
    }
    return null;
  }
  
  @Override
  public ITelePad setTargetDim(int dimID) {
    if (inNetwork()) {
      targetDim = dimID;
      return master;
    }
    return null;
  }

  @Override
  public void setCoords(BlockCoord coords) {
    if(inNetwork()) {
      if(isMaster()) {
        this.target = coords;
      } else {
        this.master.setCoords(coords);
      }
    }
  }

  @Override
  public void teleportSpecific(Entity entity) {
    if(!inNetwork()) {
      return;
    }
    if(isMaster()) {
      if(isEntityInRange(entity)) {
        enqueueTeleport(entity, true);
      }
    } else {
      master.teleportSpecific(entity);
    }
  }

  @Override
  public void teleportAll() {
    if(!inNetwork()) {
      return;
    }
    if(isMaster()) {
      for (Entity e : getEntitiesInRange()) {
        enqueueTeleport(e, true);
      }
    } else {
      master.teleportAll();
    }
  }

  @SuppressWarnings("unchecked")
  private List<Entity> getEntitiesInRange() {
    return worldObj.getEntitiesWithinAABB(Entity.class, getRange());
  }

  private boolean isEntityInRange(Entity entity) {
    return getRange().isVecInside(Vec3.createVectorHelper(entity.posX, entity.posY, entity.posZ));
  }

  private AxisAlignedBB getRange() {
    return AxisAlignedBB.getBoundingBox(xCoord - 1, yCoord, zCoord - 1, xCoord + 2, yCoord + 3, zCoord + 2);
  }

  void enqueueTeleport(Entity entity, boolean sendUpdate) {
    if(toTeleport.contains(entity)) {
      return;
    }

    calculateTeleportPower();
    entity.getEntityData().setBoolean(TELEPORTING_KEY, true);
    toTeleport.add(entity);
    if(sendUpdate) {
      if(entity.worldObj.isRemote) {
        PacketHandler.INSTANCE.sendToServer(new PacketTeleport(Type.BEGIN, this, entity.getEntityId()));
      } else {
        PacketHandler.INSTANCE.sendToAll(new PacketTeleport(Type.BEGIN, this, entity.getEntityId()));
      }
    }
  }

  void dequeueTeleport(Entity entity, boolean sendUpdate) {
    toTeleport.remove(entity);
    entity.getEntityData().setBoolean(TELEPORTING_KEY, false);
    if(sendUpdate) {
      if(worldObj.isRemote) {
        PacketHandler.INSTANCE.sendToServer(new PacketTeleport(Type.END, this, entity.getEntityId()));
      } else {
        PacketHandler.INSTANCE.sendToAll(new PacketTeleport(Type.END, this, entity.getEntityId()));
      }
    }
    if(!active()) {
      powerUsed = 0;
    }
  }

  private boolean teleport(Entity entity) {
    entity.getEntityData().setBoolean(TELEPORTING_KEY, false);
    return entity.worldObj.isRemote ? clientTeleport(entity) : serverTeleport(entity);
  }
  
  private boolean clientTeleport(Entity entity) {
    if(entity.worldObj.provider.dimensionId == targetDim) {
      return TravelController.instance.doClientTeleport(entity, target, TravelSource.TELEPAD, 0, false);
    }
    return true;
  }

  private boolean serverTeleport(Entity entity) {
    dequeueTeleport(entity, true);
    if(entity.worldObj.provider.dimensionId != targetDim) {
      MinecraftServer server = MinecraftServer.getServer();
      int currentDim = entity.worldObj.provider.dimensionId;
      if(entity instanceof EntityPlayer) {
        EntityPlayerMP player = (EntityPlayerMP) entity;
        server.getConfigurationManager().transferPlayerToDimension(player, targetDim, new TeleporterEIO(server.worldServerForDimension(targetDim)));
      } else {
        WorldServer toDim = server.worldServerForDimension(targetDim);
        server.getConfigurationManager().transferEntityToWorld(entity, 0, server.worldServerForDimension(currentDim), toDim, new TeleporterEIO(toDim));
      }
    }
    PacketTravelEvent.doServerTeleport(entity, target.x, target.y, target.z, 0, false, TravelSource.TELEPAD);
    return true;
  }

  /* ITravelAccessable overrides */

  @Override
  public boolean canSeeBlock(EntityPlayer playerName) {
    return isMaster() && inNetwork && getEnergyStored() > 0;
  }

  /* IInternalPowerReceiver */

  @Override
  public int getMaxEnergyRecieved(ForgeDirection dir) {
    return inNetwork && master != null ? master == this ? energy.getMaxReceive() : master.getMaxEnergyRecieved(dir) : 0;
  }

  @Override
  public int getMaxEnergyStored() {
    return inNetwork && master != null ? master == this ? energy.getMaxEnergyStored() : master.getMaxEnergyStored() : 0;
  }

  @Override
  public boolean displayPower() {
    return inNetwork && master != null;
  }

  @Override
  public int getEnergyStored() {
    return inNetwork && master != null ? master == this ? energy.getEnergyStored() : master.getEnergyStored() : 0;
  }

  @Override
  public void setEnergyStored(int storedEnergy) {
    if(inNetwork && master != null) {
      if(master == this) {
        energy.setEnergyStored(storedEnergy);
      } else {
        master.setEnergyStored(storedEnergy);
      }
    }
  }

  @Override
  public boolean canConnectEnergy(ForgeDirection from) {
    return inNetwork && master != null;
  }

  @Override
  public int receiveEnergy(ForgeDirection from, int maxReceive, boolean simulate) {
    return inNetwork && master != null ? master == this ? energy.receiveEnergy(maxReceive, simulate) : master.receiveEnergy(from, maxReceive, simulate) : 0;
  }

  @Override
  public int getEnergyStored(ForgeDirection from) {
    return inNetwork && master != null ? master == this ? energy.getEnergyStored() : master.getEnergyStored() : 0;
  }

  @Override
  public int getMaxEnergyStored(ForgeDirection from) {
    return inNetwork && master != null ? master == this ? energy.getMaxEnergyStored() : master.getMaxEnergyStored() : 0;
  }

  public int getUsage() {
    return energy.getMaxReceive();
  }
}
