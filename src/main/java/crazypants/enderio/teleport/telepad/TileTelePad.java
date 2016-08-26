package crazypants.enderio.teleport.telepad;

import java.util.List;
import java.util.Queue;

import com.enderio.core.common.util.BlockCoord;
import com.google.common.collect.Lists;
import com.google.common.collect.Queues;

import crazypants.enderio.ModObject;
import crazypants.enderio.api.teleport.ITelePad;
import crazypants.enderio.api.teleport.TravelSource;
import crazypants.enderio.capacitor.CapacitorKeyType;
import crazypants.enderio.capacitor.DefaultCapacitorData;
import crazypants.enderio.capacitor.DefaultCapacitorKey;
import crazypants.enderio.capacitor.ICapacitorData;
import crazypants.enderio.capacitor.ICapacitorKey;
import crazypants.enderio.capacitor.Scaler;
import crazypants.enderio.config.Config;
import crazypants.enderio.machine.AbstractMachineEntity;
import crazypants.enderio.machine.MachineSound;
import crazypants.enderio.machine.PacketPowerStorage;
import crazypants.enderio.network.PacketHandler;
import crazypants.enderio.teleport.TeleportUtil;
import crazypants.enderio.teleport.anchor.TileTravelAnchor;
import crazypants.enderio.teleport.telepad.packet.PacketTeleport;
import crazypants.enderio.teleport.telepad.packet.PacketUpdateCoords;
import crazypants.enderio.teleport.telepad.render.BlockType;
import info.loenwind.autosave.annotations.Store;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class TileTelePad extends TileTravelAnchor implements ITileTelePad {

  private ICapacitorData capacitorData = DefaultCapacitorData.BASIC_CAPACITOR;
  private final ICapacitorKey maxEnergyRecieved = new DefaultCapacitorKey(ModObject.blockTelePad, CapacitorKeyType.ENERGY_INTAKE, Scaler.Factory.POWER, 1000);
  private final ICapacitorKey maxEnergyStored = new DefaultCapacitorKey(ModObject.blockTelePad, CapacitorKeyType.ENERGY_BUFFER, Scaler.Factory.POWER, 100000);
  private final ICapacitorKey maxEnergyUsed = new DefaultCapacitorKey(ModObject.blockTelePad, CapacitorKeyType.ENERGY_USE, Scaler.Factory.POWER, 1000);

  @Store
  private int storedEnergyRF;

  private TileTelePad masterTile = null;

  private boolean coordsChanged = false;

  @Store
  private BlockCoord target = new BlockCoord();
  @Store
  private int targetDim = Integer.MIN_VALUE;

  private int lastSyncPowerStored;

  private Queue<Entity> toTeleport = Queues.newArrayDeque();
  private int powerUsed;
  private int maxPower;

  public static final ResourceLocation ACTIVE_RES = AbstractMachineEntity.getSoundFor("telepad.active");
  @SideOnly(Side.CLIENT)
  private MachineSound activeSound;

  @Store
  private boolean redstoneActivePrev;

  
  //Used on non-ported TESR
  public static final String TELEPORTING_KEY = "eio:teleporting";
  public static final String PROGRESS_KEY = "teleportprogress";

  boolean wasBlocked = false;

  // Clientside rendering data
  public float[] bladeRots = new float[3];
  public float spinSpeed = 0;
  public float speedMult = 2.5f;

  
  @Override
  public boolean wasBlocked() {
    return wasBlocked;
  }

  @Override
  public void setBlocked(boolean blocked) {
    wasBlocked = blocked;    
  }
  
  @Override
  public boolean isMaster() {    
    return BlockType.getType(getBlockMetadata()) == BlockType.MASTER;
  }

  @Override
  public TileTelePad getMaster() {    
    if(BlockType.getType(getBlockMetadata()) == BlockType.MASTER) {
      return this;
    }    
    BlockPos offset = BlockType.getType(getBlockMetadata()).getOffsetToMaster();
    if(offset == null) {
      return null;
    }
    BlockPos materPos = getPos().add(offset.getX(), offset.getY(), offset.getZ());
    if(!worldObj.isBlockLoaded(materPos)) {
      return null;
    }
    TileEntity res = worldObj.getTileEntity(materPos);
    if(res instanceof TileTelePad) {
      return (TileTelePad)res;
    }
    return null;
  }

  @Override
  public boolean inNetwork() {
    return getMaster() != null;
  }
  
  @Override
  public void doUpdate() {
    super.doUpdate();
    
    if (!isMaster()) {
      return;
    }

    if (targetDim == Integer.MIN_VALUE) {
      targetDim = worldObj.provider.getDimension();
    }

    if (worldObj.isRemote) {
      updateEntityClient();
      return;
    }

    if (active()) {
      if (powerUsed >= maxPower) {
        teleport(toTeleport.poll());
        powerUsed = 0;
      } else {
        int usable = Math.min(Math.min(getUsage(), maxPower), getEnergyStored());
        setEnergyStored(getEnergyStored() - usable);
        powerUsed += usable;
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
    if (coordsChanged ) {
      coordsChanged = false;
      PacketHandler.sendToAllAround(new PacketUpdateCoords(this, getX(), getY(), getZ(), getTargetDim()), this);
    }
  }

  @SideOnly(Side.CLIENT)
  protected void updateEntityClient() {    
    updateRotations();
    if (activeSound != null) {
      activeSound.setPitch(MathHelper.clamp_float(0.5f + (spinSpeed / 1.5f), 0.5f, 2));
    }
    if (active()) {
      if (activeSound == null) {
        BlockPos p = getPos();
        activeSound = new MachineSound(ACTIVE_RES, p.getX(), p.getY(), p.getZ(), 0.3f, 1);
        playSound();
      }
      updateQueuedEntities();
    } else if (!active() && activeSound != null) {
      if (activeSound.getPitch() <= 0.5f) {
        activeSound.endPlaying();
        activeSound = null;
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

  public void updateRedstoneState() {
    if (!inNetwork()) {
      return;
    }

    boolean redstone = isPoweredRedstone();
    if (!getMasterTile().redstoneActivePrev && redstone) {
      teleportAll();
    }
    getMasterTile().redstoneActivePrev = redstone;
  }

  public boolean isPainted() {
    return sourceBlock != null;
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

  @SideOnly(Side.CLIENT)
  private void stopPlayingSound() {
    if (activeSound != null) {
      activeSound.endPlaying();
      activeSound = null;
    }
  }

  @Override
  public int getPowerScaled(int scale) {
    return (int) ((((float) getEnergyStored()) / ((float) getMaxEnergyStored())) * scale);
  }

  private int calculateTeleportPower() {
    if (worldObj.provider.getDimension() == targetDim) {
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

  @Override
  public Entity getCurrentTarget() {
    return toTeleport.peek();
  }

  public AxisAlignedBB getBoundingBox() {
    BlockPos p = getPos();
    if (!inNetwork()) {
      return new AxisAlignedBB(p, p.offset(EnumFacing.UP).offset(EnumFacing.SOUTH).offset(EnumFacing.EAST));
    }
    p = getMaster().getLocation().getBlockPos();
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

  @Override
  public int getX() {
    if (inNetwork()) {
      return getMasterTile().target.x;
    }
    return target.x;
  }

  @Override
  public int getY() {
    if (inNetwork()) {
      return getMasterTile().target.y;
    }
    return target.y;
  }

  @Override
  public int getZ() {
    if (inNetwork()) {
      return getMasterTile().target.z;
    }
    return target.z;
  }

  @Override
  public int getTargetDim() {
    if (inNetwork()) {
      return getMasterTile().targetDim;
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
      return getMasterTile();
    }
    return null;
  }

  ITelePad setY_internal(int y) {
    if (inNetwork()) {
      setCoords(new BlockCoord(target.x, y, target.z));
      return getMasterTile();
    }
    return null;
  }

  ITelePad setZ_internal(int z) {
    if (inNetwork()) {
      setCoords(new BlockCoord(target.x, target.y, z));
      return getMasterTile();
    }
    return null;
  }

  @Override
  public ITelePad setTargetDim_internal(int dimID) {
    if (inNetwork()) {
      getMasterTile().targetDim = dimID;
      coordsChanged = true;
      return getMasterTile();
    }
    return null;
  }

  @Override
  public void setCoords_internal(BlockCoord coords) {
    if (inNetwork()) {
      if (isMaster()) {
        this.target = coords;
        this.coordsChanged = true;
        markDirty();
      } else {
        this.getMasterTile().setCoords_internal(coords);
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
      getMasterTile().teleportSpecific(entity);
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
      getMasterTile().teleportAll();
    }
  }

  private List<Entity> getEntitiesInRange() {
    return worldObj.getEntitiesWithinAABB(Entity.class, getRange());
  }

  private boolean isEntityInRange(Entity entity) {
    return getRange().isVecInside(new Vec3d(entity.posX, entity.posY, entity.posZ));
  }

  private AxisAlignedBB getRange() {
    BlockPos p = getPos();
    return new AxisAlignedBB(p.getX() - 1, p.getY(), p.getZ() - 1, p.getX() + 2, p.getY() + 3, p.getZ() + 2);
  }

  @Override
  public void enqueueTeleport(Entity entity, boolean sendUpdate) {    
    if (entity == null || toTeleport.contains(entity)) {
      return;
    }

    calculateTeleportPower();
    entity.getEntityData().setBoolean(TELEPORTING_KEY, true);
    toTeleport.add(entity);
    if (sendUpdate) {
      if (entity.worldObj.isRemote) {
        PacketHandler.INSTANCE.sendToServer(new PacketTeleport(PacketTeleport.Type.BEGIN, this, entity.getEntityId()));
      } else {
        PacketHandler.INSTANCE.sendToAll(new PacketTeleport(PacketTeleport.Type.BEGIN, this, entity.getEntityId()));
      }
    }
  }

  @Override
  public void dequeueTeleport(Entity entity, boolean sendUpdate) {
    if (entity == null) {
      return;
    }
    toTeleport.remove(entity);
    entity.getEntityData().setBoolean(TELEPORTING_KEY, false);
    if (sendUpdate) {
      if (worldObj.isRemote) {
        PacketHandler.INSTANCE.sendToServer(new PacketTeleport(PacketTeleport.Type.END, this, entity.getEntityId()));
      } else {
        PacketHandler.INSTANCE.sendToAll(new PacketTeleport(PacketTeleport.Type.END, this, entity.getEntityId()));
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
      PacketHandler.INSTANCE.sendToAll(new PacketTeleport(PacketTeleport.Type.TELEPORT, this, wasBlocked));
      return !wasBlocked;
    }
    return false;
  }
  
  private boolean clientTeleport(Entity entity) {
    return TeleportUtil.clientTeleport(entity, target.getBlockPos(), targetDim, TravelSource.TELEPAD);
        
  }

  private boolean serverTeleport(Entity entity) {    
    dequeueTeleport(entity, true);        
    return TeleportUtil.serverTeleport(entity, target.getBlockPos(), targetDim, false, TravelSource.TELEPAD);    
  }

  /* ITravelAccessable overrides */

  @Override
  public boolean canSeeBlock(EntityPlayer playerName) {
    return isMaster() && inNetwork();
  }

  /* IInternalPowerReceiver */

  @Override
  public int getMaxEnergyRecieved(EnumFacing dir) {
    return inNetwork() && getMasterTile() != null ? getMasterTile() == this ? maxEnergyRecieved.get(capacitorData) : getMasterTile().getMaxEnergyRecieved(dir) : 0;
  }

  @Override
  public int getMaxEnergyStored() {
    return inNetwork() && getMasterTile() != null ? getMasterTile() == this ? maxEnergyStored.get(capacitorData) : getMasterTile().getMaxEnergyStored() : 0;
  }

  @Override
  public boolean displayPower() {
    return inNetwork() && getMasterTile() != null;
  }

  @Override
  public int getEnergyStored() {
    return inNetwork() && getMasterTile() != null ? getMasterTile() == this ? storedEnergyRF : getMasterTile().getEnergyStored() : 0;
  }

  @Override
  public void setEnergyStored(int storedEnergy) {
    if (inNetwork() && getMasterTile() != null) {
      if (getMasterTile() == this) {
        storedEnergyRF = Math.min(getMaxEnergyStored(), storedEnergy);
      } else {
        getMasterTile().setEnergyStored(storedEnergy);
      }
    }
  }

  @Override
  public boolean canConnectEnergy(EnumFacing from) {
    return inNetwork() && getMasterTile() != null;
  }

  @Override
  public int receiveEnergy(EnumFacing from, int maxReceive, boolean simulate) {
    if (!inNetwork()) {
      return 0;
    }
    int max = Math.max(0, Math.min(Math.min(getMaxEnergyRecieved(from), maxReceive), getMaxEnergyStored() - getEnergyStored()));
    if (!simulate) {
      setEnergyStored(getEnergyStored() + max);
    }
    return max;
  }

  @Override
  public int getEnergyStored(EnumFacing from) {
    return getEnergyStored();
  }

  @Override
  public int getMaxEnergyStored(EnumFacing from) {
    return getMaxEnergyStored();
  }

  @Override
  public int getUsage() {
    return maxEnergyUsed.get(capacitorData);
  }

  private TileTelePad getMasterTile() {
    if(masterTile != null) {
      return masterTile;
    }
    
    masterTile = getMaster();    
    return masterTile;
  }
  
  @Override
  public boolean shouldRenderInPass(int pass) {
    return true;
  }

}
