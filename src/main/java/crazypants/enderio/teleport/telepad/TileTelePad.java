package crazypants.enderio.teleport.telepad;

import java.util.List;
import java.util.Queue;

import com.enderio.core.api.common.util.IProgressTile;
import com.google.common.collect.Lists;
import com.google.common.collect.Queues;

import crazypants.enderio.EnderIO;
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
import crazypants.enderio.power.IInternalPowerReceiver;
import crazypants.enderio.teleport.TeleportUtil;
import crazypants.enderio.teleport.anchor.TileTravelAnchor;
import crazypants.enderio.teleport.telepad.packet.PacketSetTarget;
import crazypants.enderio.teleport.telepad.packet.PacketTeleport;
import crazypants.enderio.teleport.telepad.render.BlockType;
import info.loenwind.autosave.annotations.Store;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;

public class TileTelePad extends TileTravelAnchor implements IInternalPowerReceiver, ITelePad, IProgressTile, IItemHandlerModifiable  {

  private ICapacitorData capacitorData = DefaultCapacitorData.BASIC_CAPACITOR;
  private final ICapacitorKey maxEnergyRecieved = new DefaultCapacitorKey(ModObject.blockTelePad, CapacitorKeyType.ENERGY_INTAKE, Scaler.Factory.POWER, Config.telepadEnergyUsePerTickRF);
  private final ICapacitorKey maxEnergyStored = new DefaultCapacitorKey(ModObject.blockTelePad, CapacitorKeyType.ENERGY_BUFFER, Scaler.Factory.POWER, Config.telepadEnergyBufferRF);
  private final ICapacitorKey maxEnergyUsed = new DefaultCapacitorKey(ModObject.blockTelePad, CapacitorKeyType.ENERGY_USE, Scaler.Factory.POWER, Config.telepadEnergyUsePerTickRF);

  @Store
  private int storedEnergyRF;

  private TileTelePad masterTile = null;

  private boolean coordsChanged = false;

  @Store
  private TelepadTarget target = new TelepadTarget(new BlockPos(0,0,0), Integer.MIN_VALUE);

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

  
  @Store
  protected ItemStack[] inventory = new ItemStack[2];
  
  public TileTelePad() {    
  }
  
  public boolean wasBlocked() {
    return wasBlocked;
  }
  
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

    if (target.getDimension() == Integer.MIN_VALUE) {      
      target.setDimension(worldObj.provider.getDimension());
    }
    
    if (worldObj.isRemote) {      
      updateEntityClient();
      return;
    }

    if(inventory[0] != null) {
      ItemStack stack = inventory[0];
      TelepadTarget newTarg = TelepadTarget.readFromNBT(stack);
      setTarget(newTarg);
      inventory[0] = null;
      inventory[1] = stack;
      markDirty();      
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
      PacketHandler.sendToAllAround(new PacketSetTarget(this, target), this);
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
  
  public int getPowerScaled(int scale) {
    return (int) ((((float) getEnergyStored()) / ((float) getMaxEnergyStored())) * scale);
  }

  private int calculateTeleportPower() {
    if (worldObj.provider.getDimension() == target.getDimension()) {
      int distance = (int)Math.ceil(pos.getDistance(target.getLocation().getX(), target.getLocation().getY(), target.getLocation().getZ()));
      double base = Math.log((0.005 * distance) + 1);
      maxPower = (int) (base * Config.telepadPowerCoefficient);      
    } else {
      maxPower = Config.telepadPowerInterdimensional;
    }    
    //Max out at the inter dim. value
    int res = MathHelper.clamp_int(maxPower, 5000, Config.telepadPowerInterdimensional);
    return res;
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
      return getMasterTile().target.getX();
    }
    return target.getX();
  }

  @Override
  public int getY() {
    if (inNetwork()) {
      return getMasterTile().target.getY();
    }
    return target.getY();
  }

  @Override
  public int getZ() {
    if (inNetwork()) {
      return getMasterTile().target.getZ();
    }
    return target.getZ();
  }

  @Override
  public int getTargetDim() {
    if (inNetwork()) {
      return getMasterTile().target.getDimension();
    }
    return target.getDimension();
  }

  @Override
  public void setX(int x) {
    if(Config.telepadLockCoords) {
      return;
    }
    setTarget(getTarget().setX(x));    
  }

  @Override
  public void setY(int y) {
    if(Config.telepadLockCoords) {
      return;
    }
    setTarget(getTarget().setY(y));
  }

  @Override
  public void setZ(int z) {
    if(Config.telepadLockCoords) {
      return;
    }
    setTarget(getTarget().setZ(z));
  }

  @Override
  public void setTargetDim(int dimID) {
    if(Config.telepadLockCoords) {
      return;
    }
    setTarget(getTarget().setDimension(dimID));
  }

  @Override
  public void setCoords(BlockPos coords) {
    if(Config.telepadLockCoords) {
      return;
    }
    setTarget(getTarget().setLocation(coords));
  }

  public void setTarget(TelepadTarget newTarget) {    
    if(inNetwork() && !isMaster()) {
      getMaster().setTarget(newTarget);
      return;
    }    
    if(newTarget == null) {
      newTarget = new TelepadTarget();
    }
    target = new TelepadTarget(newTarget);
    coordsChanged = true;
    markDirty();    
  }
  
  public TelepadTarget getTarget() {
    if (!inNetwork() || isMaster()) {      
      return target;
    }
    return getMaster().getTarget();
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
    TileTelePad m = getMasterTile();
    if(m == null) {
      return;
    }
    for (Entity e : m.getEntitiesInRange()) {      
      m.enqueueTeleport(e, true);
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
  
  public void enqueueTeleport(Entity entity, boolean sendUpdate) {
    if (entity == null || toTeleport.contains(entity)) {
      return;
    }
    calculateTeleportPower();
    entity.getEntityData().setBoolean(TELEPORTING_KEY, true);
    toTeleport.add(entity);
    if (sendUpdate) {
      if (entity.worldObj.isRemote) {        
        PacketHandler.INSTANCE.sendToServer(new PacketTeleport(PacketTeleport.Type.BEGIN, this, entity));
      } else {
        PacketHandler.INSTANCE.sendToAll(new PacketTeleport(PacketTeleport.Type.BEGIN, this, entity));
      }
    }
  }

  public void dequeueTeleport(Entity entity, boolean sendUpdate) {
    if (entity == null) {
      return;
    }
    toTeleport.remove(entity);
    entity.getEntityData().setBoolean(TELEPORTING_KEY, false);
    if (sendUpdate) {
      if (worldObj.isRemote) {
        PacketHandler.INSTANCE.sendToServer(new PacketTeleport(PacketTeleport.Type.END, this, entity));
      } else {
        PacketHandler.INSTANCE.sendToAll(new PacketTeleport(PacketTeleport.Type.END, this, entity));
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
      if(entity instanceof EntityPlayer) {
        ((EntityPlayer)entity).closeScreen();
      }
      return !wasBlocked;
    }
    return false;
  }
  
  private boolean clientTeleport(Entity entity) {
    return TeleportUtil.checkClientTeleport(entity, target.getLocation(), target.getDimension(), TravelSource.TELEPAD);          
  }

  private boolean serverTeleport(Entity entity) {    
    dequeueTeleport(entity, true);        
    return TeleportUtil.serverTeleport(entity, target.getLocation(), target.getDimension(), false, TravelSource.TELEPAD);    
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
  
  //Inventory
  
  @Override
  public boolean hasCapability(Capability<?> capability, EnumFacing facing) {    
    if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
      return true;
    }
    return super.hasCapability(capability, facing);
  }
  
  @SuppressWarnings("unchecked")
  @Override
  public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
    if (inNetwork() && capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {      
      return (T) getMaster();
    }
    return super.getCapability(capability, facing);
  }

  @Override
  public int getSlots() {
    return 2;
  }

  @Override
  public ItemStack getStackInSlot(int slot) {
    if(slot < 0 || slot >= inventory.length) {
      return null;
    }
    return inventory[slot];    
  }
  
  @Override
  public void setStackInSlot(int slot, ItemStack stack) {
    if(slot < 0 || slot >= inventory.length) {
      return;
    }
    inventory[slot] = stack;    
  }

  @Override
  public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
    if(slot != 0 || inventory[0] != null || stack == null || stack.getItem() != EnderIO.itemlocationPrintout) {
      return stack;
    }
    if(!simulate) {
      inventory[0] = stack.copy();
      markDirty();
    }    
    return null;
  }

  @Override
  public ItemStack extractItem(int slot, int amount, boolean simulate) {
    if(slot != 1 || amount < 1 || inventory[1] == null) {
      return null;
    }
    ItemStack res = inventory[1].copy();
    if(!simulate) {
      markDirty();
      inventory[1] = null;
    }
    return res;
  }

}
