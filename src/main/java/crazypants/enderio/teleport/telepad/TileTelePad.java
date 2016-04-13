package crazypants.enderio.teleport.telepad;

import java.util.EnumSet;
import java.util.List;
import java.util.Queue;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;
import net.minecraft.world.Teleporter;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import cofh.api.energy.EnergyStorage;

import com.enderio.core.api.common.util.IProgressTile;
import com.enderio.core.common.util.BlockCoord;
import com.enderio.core.common.util.Util;
import com.google.common.base.Throwables;
import com.google.common.collect.Lists;
import com.google.common.collect.Queues;

import crazypants.enderio.api.teleport.ITelePad;
import crazypants.enderio.api.teleport.TravelSource;
import crazypants.enderio.config.Config;
import crazypants.enderio.machine.AbstractMachineEntity;
import crazypants.enderio.machine.MachineSound;
import crazypants.enderio.machine.PacketPowerStorage;
import crazypants.enderio.network.PacketHandler;
import crazypants.enderio.power.IInternalPowerReceiver;
import crazypants.enderio.rail.TeleporterEIO;
import crazypants.enderio.teleport.TravelController;
import crazypants.enderio.teleport.anchor.TileTravelAnchor;
import crazypants.enderio.teleport.packet.PacketTravelEvent;
import crazypants.enderio.teleport.telepad.PacketTeleport.Type;

public class TileTelePad extends TileTravelAnchor implements IInternalPowerReceiver, ITelePad, IProgressTile {

  private boolean inNetwork;
  private boolean isMaster;

  private EnergyStorage energy = new EnergyStorage(100000, 1000, 1000);

  private TileTelePad master = null;

  private boolean autoUpdate = false;

  private boolean coordsChanged = false;

  private BlockCoord target = new BlockCoord();
  private int targetDim = Integer.MIN_VALUE;

  private int lastSyncPowerStored;

  private Queue<Entity> toTeleport = Queues.newArrayDeque();
  private int powerUsed;
  private int maxPower;

  private static final ResourceLocation activeRes = AbstractMachineEntity.getSoundFor("telepad.active");
  private MachineSound activeSound = null;

  private boolean redstoneActivePrev;

  public static final String TELEPORTING_KEY = "eio:teleporting";
  public static final String PROGRESS_KEY = "teleportprogress";

  boolean wasBlocked = false;

  // Clientside rendering data
  public float[] bladeRots = new float[3];
  public float spinSpeed = 0;
  public float speedMult = 2.5f;

  @Override
  public void doUpdate() {
    super.doUpdate();

    // my master is gone!
    if (master != null && master.isInvalid()) {
      master.breakNetwork();
    }

    if (autoUpdate) {
      updateConnectedState(true);
      autoUpdate = false;
    }

    if (targetDim == Integer.MIN_VALUE) {
      targetDim = worldObj.provider.getDimensionId();
    }

    if (worldObj.isRemote && isMaster()) {
      updateRotations();
      if (activeSound != null) {
        activeSound.setPitch(MathHelper.clamp_float(0.5f + (spinSpeed / 1.5f), 0.5f, 2));
      }
      if (active()) {
        if (activeSound == null) {
          BlockPos p = getPos();
          activeSound = new MachineSound(activeRes, p.getX(), p.getY(), p.getZ(), 0.3f, 1);
          playSound();
        }
        updateQueuedEntities();
      } else if (!active() && activeSound != null) {
        if (activeSound.getPitch() <= 0.5f) {
          activeSound.endPlaying();
          activeSound = null;
        }
      }
    } else if (!worldObj.isRemote) {
      if (active()) {
        if (powerUsed >= maxPower) {
          teleport(toTeleport.poll());
          powerUsed = 0;
        } else {
          powerUsed += energy.extractEnergy(Math.min(getUsage(), maxPower), false);
        }
        if (shouldDoWorkThisTick(5)) {
          updateQueuedEntities();
        }
      }

      boolean powerChanged = (lastSyncPowerStored != getEnergyStored() && shouldDoWorkThisTick(5));
      if (powerChanged) {
        lastSyncPowerStored = getEnergyStored();
        PacketHandler.sendToAllAround(new PacketPowerStorage(this), this);
      }
      if (coordsChanged && inNetwork() && master != null && isMaster()) {
        coordsChanged = false;
        PacketHandler.sendToAllAround(new PacketUpdateCoords(master, master.getX(), master.getY(), master.getZ(), master.getTargetDim()), master);
      }
    }
  }

  @SideOnly(Side.CLIENT)
  private void playSound() {
    FMLClientHandler.instance().getClient().getSoundHandler().playSound(activeSound);
  }

  private void updateQueuedEntities() {
    if (worldObj.isRemote) {
      if (active()) {
        getCurrentTarget().getEntityData().setFloat(PROGRESS_KEY, getProgress());
      }
    }
    List<Entity> toRemove = Lists.newArrayList();
    for (Entity e : toTeleport) {
      if (!isEntityInRange(e) || e.isDead) {
        toRemove.add(e);
      }
    }
    for (Entity e : toRemove) {
      dequeueTeleport(e, true);
    }
  }

  public void updateConnectedState(boolean fromBlock) {

    EnumSet<EnumFacing> connections = EnumSet.noneOf(EnumFacing.class);
    for (BlockCoord bc : getSurroundingCoords()) {
      if (worldObj.isBlockLoaded(bc.getBlockPos())) {
        TileEntity te = bc.getTileEntity(worldObj); 
        EnumFacing con = Util.getDirFromOffset(bc.x - getPos().getX(), 0, bc.z - getPos().getZ());
        if (te instanceof TileTelePad && te.hasWorldObj() && isPainted() == ((TileTelePad) te).isPainted()) {
          // let's find the master and let him do the work
          if (fromBlock) {
            // Recurse to all adjacent (diagonal and axis-aligned) telepads, but only 1 deep.
            ((TileTelePad) te).updateConnectedState(false);
            // If that telepad turned into a master, we can stop our search, as
            // we were added to its network
            if (((TileTelePad) te).inNetwork() && !inNetwork) {
              return;
            }
          }
          // otherwise we either are the master or this is a secondary call, so update connections
          if (con != null && !((TileTelePad) te).inNetwork()) {
            connections.add(con);
          }
        } else {
          connections.remove(con);
          if (master == this) {
            breakNetwork();
            updateBlock();
          } else if (con != null) {
            if (inNetwork() && master != null && fromBlock) {
              master.updateConnectedState(false);
            }
          }
        }
      }
      if (connections.size() == 4 && !inNetwork()) {
        inNetwork = formNetwork();
        updateBlock();
        if (inNetwork()) {
          if (target.equals(new BlockCoord())) {
            target = new BlockCoord(this);
          }
        }
      }
    }
  }

  public void updateRedstoneState() {
    if (!inNetwork()) {
      return;
    }

    boolean redstone = isPoweredRedstone();
    if (!master.redstoneActivePrev && redstone) {
      teleportAll();
    }
    master.redstoneActivePrev = redstone;
  }

  public boolean isPainted() {
    return sourceBlock != null;
  }

  private boolean formNetwork() {
    List<TileTelePad> temp = Lists.newArrayList();

    for (BlockCoord c : getSurroundingCoords()) {
      TileEntity te = c.getTileEntity(worldObj);
      if (!(te instanceof TileTelePad) || ((TileTelePad) te).inNetwork() || isPainted() != ((TileTelePad) te).isPainted()) {
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
    this.isMaster = true;
    return true;
  }

  private void breakNetwork() {
    master = null;
    inNetwork = false;
    isMaster = false;
    for (BlockCoord c : getSurroundingCoords()) {
      TileEntity te = c.getTileEntity(worldObj);
      if (te instanceof TileTelePad) {
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
        if (x != 0 || z != 0) {
          ret.add(new BlockCoord(getPos().getX() + x, getPos().getY(), getPos().getZ() + z));
        }
      }
    }
    return ret;
  }

  private void updateNeighborTEs() {
    BlockCoord bc = new BlockCoord(this);
    for (EnumFacing dir : EnumFacing.VALUES) {
      BlockCoord neighbor = bc.getLocation(dir);
      Block block = neighbor.getBlock(worldObj);
      if (!(block instanceof BlockTelePad)) {
        block.onNeighborChange(worldObj, neighbor.getBlockPos(), getPos());
      }
    }
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
  public void invalidate() {
    super.invalidate();
    if (worldObj.isRemote) {
      stopPlayingSound();
    }
  }

  @Override
  public void onChunkUnload() {
    super.onChunkUnload();
    if (worldObj.isRemote) {
      stopPlayingSound();
    }
  }

  private void stopPlayingSound() {
    if (activeSound != null) {
      activeSound.endPlaying();
      activeSound = null;
    }
  }

  public int getPowerScaled(int scale) {
    return (int) ((((float) getEnergyStored()) / ((float) getMaxEnergyStored())) * scale);
  }

  private int calculateTeleportPower() {
    if (worldObj.provider.getDimensionId() == targetDim) {
      int distance = new BlockCoord(this).getDist(target);
      double base = Math.log((0.005 * distance) + 1);
      this.maxPower = (int) (base * Config.telepadPowerCoefficient);
      if (this.maxPower <= 0) {
        this.maxPower = 1;
      }
    } else {
      this.maxPower = Config.telepadPowerInterdimensional;
    }
    return this.maxPower;
  }

  public boolean active() {
    return !toTeleport.isEmpty();
  }

  public Entity getCurrentTarget() {
    return toTeleport.peek();
  }

  public AxisAlignedBB getBoundingBox() {
    BlockPos p = getPos();
    if (!inNetwork()) {
      return new AxisAlignedBB(p, p.offset(EnumFacing.UP).offset(EnumFacing.SOUTH).offset(EnumFacing.EAST));
    }
    p = getMaster().getPos();
    return new AxisAlignedBB(p.getX() - 1, p.getY(), p.getZ() - 1, p.getX() + 2, p.getY() + 1, p.getZ() + 2);
  }

  @Override
  public AxisAlignedBB getRenderBoundingBox() {
    return getBoundingBox();
  }

  public void updateRotations() {
    if (active()) {
      spinSpeed = getProgress() * 2;
    } else {
      spinSpeed = Math.max(0, spinSpeed - 0.025f);
    }

    for (int i = 0; i < bladeRots.length; i++) {
      bladeRots[i] += spinSpeed * ((i * 2) + 20);
      bladeRots[i] %= 360;
    }
  }

  /* IProgressTile */

  @Override
  public float getProgress() {
    return ((float) powerUsed) / ((float) maxPower);
  }

  @Override
  protected int getProgressUpdateFreq() {
    return 1;
  }

  @Override
  public void setProgress(float progress) {
    this.powerUsed = progress < 0 ? 0 : (int) ((maxPower) * progress);
  }

  @Override
  public TileEntity getTileEntity() {
    return this;
  }

  /* ITelePad */

  @Override
  public boolean isMaster() {
    return isMaster;
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
    if (inNetwork()) {
      return master.target.x;
    }
    return target.x;
  }

  @Override
  public int getY() {
    if (inNetwork()) {
      return master.target.y;
    }
    return target.y;
  }

  @Override
  public int getZ() {
    if (inNetwork()) {
      return master.target.z;
    }
    return target.z;
  }

  @Override
  public int getTargetDim() {
    if (inNetwork()) {
      return master.targetDim;
    }
    return targetDim;
  }

  @Override
  public ITelePad setX(int x) {
    return Config.telepadLockCoords ? null : setX_internal(x);
  }

  @Override
  public ITelePad setY(int y) {
    return Config.telepadLockCoords ? null : setY_internal(y);
  }

  @Override
  public ITelePad setZ(int z) {
    return Config.telepadLockCoords ? null : setZ_internal(z);
  }

  @Override
  public ITelePad setTargetDim(int dimID) {
    return Config.telepadLockDimension ? null : setTargetDim_internal(dimID);
  }

  @Override
  public void setCoords(BlockCoord coords) {
    if (!Config.telepadLockCoords) {
      setCoords_internal(coords);
    }
  }

  ITelePad setX_internal(int x) {
    if (inNetwork()) {
      setCoords(new BlockCoord(x, target.y, target.z));
      return master;
    }
    return null;
  }

  ITelePad setY_internal(int y) {
    if (inNetwork()) {
      setCoords(new BlockCoord(target.x, y, target.z));
      return master;
    }
    return null;
  }

  ITelePad setZ_internal(int z) {
    if (inNetwork()) {
      setCoords(new BlockCoord(target.x, target.y, z));
      return master;
    }
    return null;
  }

  ITelePad setTargetDim_internal(int dimID) {
    if (inNetwork()) {
      master.targetDim = dimID;
      coordsChanged = true;
      return master;
    }
    return null;
  }

  void setCoords_internal(BlockCoord coords) {
    if (inNetwork()) {
      if (isMaster()) {
        this.target = coords;
        this.coordsChanged = true;
      } else {
        this.master.setCoords_internal(coords);
      }
    }
  }

  @Override
  public void teleportSpecific(Entity entity) {
    if (!inNetwork()) {
      return;
    }
    if (isMaster()) {
      if (isEntityInRange(entity)) {
        enqueueTeleport(entity, true);
      }
    } else {
      master.teleportSpecific(entity);
    }
  }

  @Override
  public void teleportAll() {
    if (!inNetwork()) {
      return;
    }
    if (isMaster()) {
      for (Entity e : getEntitiesInRange()) {
        enqueueTeleport(e, true);
      }
    } else {
      master.teleportAll();
    }
  }

  private List<Entity> getEntitiesInRange() {
    return worldObj.getEntitiesWithinAABB(Entity.class, getRange());
  }

  private boolean isEntityInRange(Entity entity) {
    return getRange().isVecInside(new Vec3(entity.posX, entity.posY, entity.posZ));
  }

  private AxisAlignedBB getRange() {
    BlockPos p = getPos();
    return new AxisAlignedBB(p.getX() - 1, p.getY(), p.getZ() - 1, p.getX() + 2, p.getY() + 3, p.getZ() + 2);
  }

  void enqueueTeleport(Entity entity, boolean sendUpdate) {
    if (entity == null || toTeleport.contains(entity)) {
      return;
    }

    calculateTeleportPower();
    entity.getEntityData().setBoolean(TELEPORTING_KEY, true);
    toTeleport.add(entity);
    if (sendUpdate) {
      if (entity.worldObj.isRemote) {
        PacketHandler.INSTANCE.sendToServer(new PacketTeleport(Type.BEGIN, this, entity.getEntityId()));
      } else {
        PacketHandler.INSTANCE.sendToAll(new PacketTeleport(Type.BEGIN, this, entity.getEntityId()));
      }
    }
  }

  void dequeueTeleport(Entity entity, boolean sendUpdate) {
    if (entity == null) {
      return;
    }
    toTeleport.remove(entity);
    entity.getEntityData().setBoolean(TELEPORTING_KEY, false);
    if (sendUpdate) {
      if (worldObj.isRemote) {
        PacketHandler.INSTANCE.sendToServer(new PacketTeleport(Type.END, this, entity.getEntityId()));
      } else {
        PacketHandler.INSTANCE.sendToAll(new PacketTeleport(Type.END, this, entity.getEntityId()));
      }
    }
    if (!active()) {
      powerUsed = 0;
    }
  }

  private boolean teleport(Entity entity) {
    if (maxPower > 0) {
      entity.getEntityData().setBoolean(TELEPORTING_KEY, false);
      wasBlocked = !(entity.worldObj.isRemote ? clientTeleport(entity) : serverTeleport(entity));
      PacketHandler.INSTANCE.sendToAll(new PacketTeleport(Type.TELEPORT, this, wasBlocked));
      return !wasBlocked;
    }
    return false;
  }

  private boolean clientTeleport(Entity entity) {
    if (entity.worldObj.provider.getDimensionId() == targetDim) {
      return TravelController.instance.doClientTeleport(entity, target, TravelSource.TELEPAD, 0, false);
    }
    return true;
  }

  private boolean serverTeleport(Entity entity) {
    dequeueTeleport(entity, true);
    int from = entity.dimension;
    if (from != targetDim) {
      MinecraftServer server = MinecraftServer.getServer();
      WorldServer fromDim = server.worldServerForDimension(from);
      WorldServer toDim = server.worldServerForDimension(targetDim);
      Teleporter teleporter = new TeleporterEIO(toDim);
      server.worldServerForDimension(entity.dimension).playSoundEffect(entity.posX, entity.posY, entity.posZ, TravelSource.TELEPAD.sound, 1.0F, 1.0F);
      if (entity instanceof EntityPlayer) {
        EntityPlayerMP player = (EntityPlayerMP) entity;
        server.getConfigurationManager().transferPlayerToDimension(player, targetDim, teleporter);
        if (from == 1 && entity.isEntityAlive()) { // get around vanilla End
                                                   // hacks
          toDim.spawnEntityInWorld(entity);
          toDim.updateEntityWithOptionalForce(entity, false);
        }
      } else {
        NBTTagCompound tagCompound = new NBTTagCompound();
        float rotationYaw = entity.rotationYaw;
        float rotationPitch = entity.rotationPitch;
        entity.writeToNBT(tagCompound);
        Class<? extends Entity> entityClass = entity.getClass();
        fromDim.removeEntity(entity);

        try {
          Entity newEntity = entityClass.getConstructor(World.class).newInstance(toDim);
          newEntity.readFromNBT(tagCompound);
          newEntity.setLocationAndAngles(target.x, target.y, target.z, rotationYaw, rotationPitch);
          newEntity.forceSpawn = true;
          toDim.spawnEntityInWorld(newEntity);
          newEntity.forceSpawn = false; // necessary?
        } catch (Exception e) {
          Throwables.propagate(e);
        }
      }
    }
    return PacketTravelEvent.doServerTeleport(entity, target.x, target.y, target.z, 0, false, TravelSource.TELEPAD);
  }

  /* ITravelAccessable overrides */

  @Override
  public boolean canSeeBlock(EntityPlayer playerName) {
    return isMaster() && inNetwork();
  }

  /* IInternalPowerReceiver */

  @Override
  public int getMaxEnergyRecieved(EnumFacing dir) {
    return inNetwork() && master != null ? master == this ? energy.getMaxReceive() : master.getMaxEnergyRecieved(dir) : 0;
  }

  @Override
  public int getMaxEnergyStored() {
    return inNetwork() && master != null ? master == this ? energy.getMaxEnergyStored() : master.getMaxEnergyStored() : 0;
  }

  @Override
  public boolean displayPower() {
    return inNetwork() && master != null;
  }

  @Override
  public int getEnergyStored() {
    return inNetwork() && master != null ? master == this ? energy.getEnergyStored() : master.getEnergyStored() : 0;
  }

  @Override
  public void setEnergyStored(int storedEnergy) {
    if (inNetwork() && master != null) {
      if (master == this) {
        energy.setEnergyStored(storedEnergy);
      } else {
        master.setEnergyStored(storedEnergy);
      }
    }
  }

  @Override
  public boolean canConnectEnergy(EnumFacing from) {
    return inNetwork() && master != null;
  }

  @Override
  public int receiveEnergy(EnumFacing from, int maxReceive, boolean simulate) {
    return inNetwork() && master != null ? master == this ? energy.receiveEnergy(maxReceive, simulate) : master.receiveEnergy(from, maxReceive, simulate) : 0;
  }

  @Override
  public int getEnergyStored(EnumFacing from) {
    return inNetwork() && master != null ? master == this ? energy.getEnergyStored() : master.getEnergyStored() : 0;
  }

  @Override
  public int getMaxEnergyStored(EnumFacing from) {
    return inNetwork() && master != null ? master == this ? energy.getMaxEnergyStored() : master.getMaxEnergyStored() : 0;
  }

  public int getUsage() {
    return energy.getMaxReceive();
  }
}
