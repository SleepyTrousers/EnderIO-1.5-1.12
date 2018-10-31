package crazypants.enderio.machines.machine.teleport.telepad;

import java.util.Collections;
import java.util.List;
import java.util.Queue;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.api.common.util.IProgressTile;
import com.enderio.core.api.common.util.ITankAccess;
import com.enderio.core.common.fluid.SmartTank;
import com.enderio.core.common.inventory.EnderInventory.Type;
import com.enderio.core.common.inventory.Filters.PredicateItemStack;
import com.enderio.core.common.inventory.InventorySlot;
import com.enderio.core.common.util.NullHelper;
import com.google.common.base.Predicate;
import com.google.common.collect.Lists;
import com.google.common.collect.Queues;

import crazypants.enderio.api.ILocalizable;
import crazypants.enderio.api.teleport.ITelePad;
import crazypants.enderio.api.teleport.TravelSource;
import crazypants.enderio.base.EnderIO;
import crazypants.enderio.base.capacitor.DefaultCapacitorData;
import crazypants.enderio.base.init.ModObject;
import crazypants.enderio.base.item.coordselector.TelepadTarget;
import crazypants.enderio.base.machine.interfaces.INotifier;
import crazypants.enderio.base.machine.sound.MachineSound;
import crazypants.enderio.base.teleport.TeleportUtil;
import crazypants.enderio.machines.capacitor.CapacitorKey;
import crazypants.enderio.machines.config.config.TelePadConfig;
import crazypants.enderio.machines.lang.Lang;
import crazypants.enderio.machines.machine.teleport.anchor.TileTravelAnchor;
import crazypants.enderio.machines.machine.teleport.telepad.packet.PacketSetTarget;
import crazypants.enderio.machines.machine.teleport.telepad.packet.PacketTelePadFluidLevel;
import crazypants.enderio.machines.machine.teleport.telepad.packet.PacketTeleport;
import crazypants.enderio.machines.machine.teleport.telepad.packet.PacketTeleportTrigger;
import crazypants.enderio.machines.machine.teleport.telepad.render.BlockType;
import crazypants.enderio.machines.network.PacketHandler;
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
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.CapabilityItemHandler;

public class TileTelePad extends TileTravelAnchor implements ITelePad, IProgressTile, ITankAccess.IExtendedTankAccess, INotifier {

  public static final @Nonnull Predicate<ItemStack> LOCATION_PRINTOUTS = new PredicateItemStack() {
    @Override
    public boolean doApply(@Nonnull ItemStack input) {
      return TelepadTarget.readFromNBT(input) != null;
    }
  };

  private TileTelePad masterTile = null;

  private boolean coordsChanged = false;

  @Store
  private @Nonnull TelepadTarget target = new TelepadTarget(new BlockPos(0, 0, 0), Integer.MIN_VALUE);

  private Queue<Entity> toTeleport = Queues.newArrayDeque();
  private int powerUsed;
  private int requiredPower;

  public static final @Nonnull ResourceLocation ACTIVE_RES = new ResourceLocation(EnderIO.DOMAIN, "telepad.active");
  @SideOnly(Side.CLIENT)
  private MachineSound activeSound;

  @Store
  private boolean redstoneActivePrev;

  private final Fluid fluidType;

  @Store
  protected final @Nonnull SmartTank tank;

  private boolean tankDirty = false;

  // Used on non-ported TESR
  @Nonnull
  public static final String TELEPORTING_KEY = "eio:teleporting";
  @Nonnull
  public static final String PROGRESS_KEY = "teleportprogress";

  boolean wasBlocked = false;

  // Clientside rendering data
  public float[] bladeRots = new float[3];
  public float spinSpeed = 0;
  public float speedMult = 2.5f;

  public TileTelePad() {
    super(CapacitorKey.TELEPAD_POWER_INTAKE, CapacitorKey.TELEPAD_POWER_BUFFER, CapacitorKey.TELEPAD_POWER_USE);

    getInventory().add(Type.INPUT, "INPUT", new InventorySlot(LOCATION_PRINTOUTS, 1));
    getInventory().add(Type.OUTPUT, "OUTPUT", new InventorySlot(1));

    fluidType = TelePadConfig.telepadFluidType.get();

    int tankCap = TelePadConfig.telepadFluidUse.get() * 10;
    tank = new SmartTank(fluidType, tankCap);
    if (tankCap <= 0) {
      tank.setCanFill(false);
    }
    tank.setCanDrain(false);
    tank.setTileEntity(this);

    getInventory().getSlot(CAPSLOT).set(new ItemStack(ModObject.itemBasicCapacitor.getItemNN(), 1, DefaultCapacitorData.ENDER_CAPACITOR.ordinal()));
  }

  public boolean isFluidEnabled() {
    return tank.getCapacity() > 0;
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
    if (BlockType.getType(getBlockMetadata()) == BlockType.MASTER) {
      return this;
    }
    BlockPos offset = BlockType.getType(getBlockMetadata()).getOffsetToMaster();
    if (offset == null) {
      return null;
    }
    BlockPos materPos = getPos().add(offset.getX(), offset.getY(), offset.getZ());
    if (!world.isBlockLoaded(materPos)) {
      return null;
    }
    TileEntity res = world.getTileEntity(materPos);
    if (res instanceof TileTelePad) {
      return (TileTelePad) res;
    }
    return null;
  }

  @Override
  public boolean inNetwork() {
    return getMaster() != null;
  }

  @Override
  protected boolean processTasks(boolean redstoneCheck) {
    if (!isMaster()) {
      return false;
    }

    if (target.getDimension() == Integer.MIN_VALUE) {
      target.setDimension(world.provider.getDimension());
    }

    if (!getInventory().getSlot("INPUT").isEmpty() && getInventory().getSlot("OUTPUT").isEmpty()) {
      ItemStack stack = getInventory().getSlot("INPUT").get();
      setTarget(TelepadTarget.readFromNBT(stack));
      getInventory().getSlot("INPUT").clear();
      getInventory().getSlot("OUTPUT").set(stack);
    }

    if (tankDirty && shouldDoWorkThisTick(10)) {
      PacketHandler.sendToAllAround(new PacketTelePadFluidLevel(this), this);
      tankDirty = false;
    }

    if (active()) {
      if (powerUsed >= requiredPower) {
        teleport(toTeleport.poll());
        powerUsed = 0;
      } else {
        int usable = Math.min(Math.min(getUsage(), requiredPower), getEnergy().getEnergyStored());
        getEnergy().setEnergyStored(getEnergy().getEnergyStored() - usable);
        powerUsed += usable;
      }
      if (shouldDoWorkThisTick(5)) {
        updateQueuedEntities();
      }
      // we have a very smooth block animation, so all clients need very detailed progress data
      // TODO: check if this is really needed for the TelePad
      PacketHandler.INSTANCE.sendToAllAround(getProgressPacket(), this);
    }

    if (coordsChanged) {
      coordsChanged = false;
      PacketHandler.sendToAllAround(new PacketSetTarget(this, target), this);
    }

    return false;
  }

  @Override
  @SideOnly(Side.CLIENT)
  protected void updateEntityClient() {
    updateRotations();
    if (activeSound != null) {
      activeSound.setPitch(MathHelper.clamp(0.5f + (spinSpeed / 1.5f), 0.5f, 2));
    }
    if (active()) {
      if (activeSound == null) {
        BlockPos p = getPos();
        FMLClientHandler.instance().getClient().getSoundHandler().playSound(activeSound = new MachineSound(ACTIVE_RES, p.getX(), p.getY(), p.getZ(), 0.3f, 1));
      }
      updateQueuedEntities();
    } else if (!active() && activeSound != null) {
      if (activeSound.getPitch() <= 0.5f) {
        activeSound.endPlaying();
        activeSound = null;
      }
    }
  }

  private void updateQueuedEntities() {
    if (world.isRemote) {
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
    if (world.isRemote) {
      stopPlayingSound();
    }
  }

  @Override
  public void onChunkUnload() {
    super.onChunkUnload();
    if (world.isRemote) {
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
    return (int) ((((float) getEnergy().getEnergyStored()) / (getEnergy().getMaxEnergyStored())) * scale);
  }

  private int calculateTeleportPower() {
    if (world.provider.getDimension() == target.getDimension()) {
      int distance = (int) Math.ceil(pos.getDistance(target.getLocation().getX(), target.getLocation().getY(), target.getLocation().getZ()));
      double base = Math.log((0.005 * distance) + 1);
      requiredPower = (int) (base * TelePadConfig.telepadPowerCoefficient.get());
    } else {
      requiredPower = TelePadConfig.telepadPowerInterdimensional.get();
    }
    // Max out at the inter dim. value
    int res = MathHelper.clamp(requiredPower, 5000, TelePadConfig.telepadPowerInterdimensional.get());
    return res;
  }

  public boolean active() {
    return !toTeleport.isEmpty();
  }

  public Entity getCurrentTarget() {
    return toTeleport.peek();
  }

  public @Nonnull AxisAlignedBB getBoundingBox() {
    BlockPos p = getPos();
    if (!inNetwork()) {
      return new AxisAlignedBB(p, p.offset(EnumFacing.UP).offset(EnumFacing.SOUTH).offset(EnumFacing.EAST));
    }
    p = getMaster().getLocation();
    return new AxisAlignedBB(p.getX() - 1, p.getY(), p.getZ() - 1, p.getX() + 2, p.getY() + 1, p.getZ() + 2);
  }

  @Override
  public @Nonnull AxisAlignedBB getRenderBoundingBox() {
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
    return ((float) powerUsed) / ((float) requiredPower);
  }

  @Override
  public void setProgress(float progress) {
    this.powerUsed = progress < 0 ? 0 : (int) ((requiredPower) * progress);
  }

  @Override
  public @Nonnull TileEntity getTileEntity() {
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
    if (TelePadConfig.telepadLockCoords.get()) {
      return;
    }
    setTarget(getTarget().setX(x));
  }

  @Override
  public void setY(int y) {
    if (TelePadConfig.telepadLockCoords.get()) {
      return;
    }
    setTarget(getTarget().setY(y));
  }

  @Override
  public void setZ(int z) {
    if (TelePadConfig.telepadLockCoords.get()) {
      return;
    }
    setTarget(getTarget().setZ(z));
  }

  @Override
  public void setTargetDim(int dimID) {
    if (TelePadConfig.telepadLockCoords.get()) {
      return;
    }
    setTarget(getTarget().setDimension(dimID));
  }

  @Override
  public void setCoords(@Nonnull BlockPos coords) {
    if (TelePadConfig.telepadLockCoords.get()) {
      return;
    }
    setTarget(getTarget().setLocation(coords));
  }

  public void setTarget(@Nullable TelepadTarget newTarget) {
    if (inNetwork() && !isMaster()) {
      getMaster().setTarget(newTarget);
      return;
    }
    if (newTarget == null) {
      newTarget = new TelepadTarget();
    }
    target = new TelepadTarget(newTarget);
    coordsChanged = true;
    markDirty();
  }

  public @Nonnull TelepadTarget getTarget() {
    if (!inNetwork() || isMaster()) {
      return target;
    }
    return getMaster().getTarget();
  }

  @Override
  public void teleportSpecific(@Nonnull Entity entity) {
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
    if (m == null) {
      return;
    }
    if (target.getY() <= 0) {
      // coords have not yet been set or have been set to a very unhealthy location
      return;
    }
    if (m.world.isRemote) {
      PacketHandler.INSTANCE.sendToServer(new PacketTeleportTrigger(m));
    } else {
      for (Entity e : m.getEntitiesInRange()) {
        m.enqueueTeleport(e, true);
      }
    }
  }

  private @Nonnull List<Entity> getEntitiesInRange() {
    return world.getEntitiesWithinAABB(Entity.class, getRange());
  }

  private boolean isEntityInRange(Entity entity) {
    return getRange().contains(new Vec3d(entity.posX, entity.posY, entity.posZ));
  }

  private @Nonnull AxisAlignedBB getRange() {
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
      if (entity.world.isRemote) {
        // NOP
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
      if (world.isRemote) {
        // NOP
      } else {
        PacketHandler.INSTANCE.sendToAll(new PacketTeleport(PacketTeleport.Type.END, this, entity));
      }
    }
    if (!active()) {
      powerUsed = 0;
    }
  }

  private boolean teleport(Entity entity) {
    if (requiredPower <= 0) {
      return false;
    }

    if (TelePadConfig.telepadFluidUse.get() > 0) {
      if (tank.getFluidAmount() < TelePadConfig.telepadFluidUse.get()) {
        tank.drain(TelePadConfig.telepadFluidUse.get(), true);
        if (entity instanceof EntityPlayer) {
          ((EntityPlayer) entity).sendMessage(Lang.GUI_TELEPAD_NOFLUID.toChatServer(new FluidStack(fluidType, 1).getLocalizedName()));
        }
        wasBlocked = true;
        return true;
      }
      tank.drainInternal(TelePadConfig.telepadFluidUse.get(), true);
    }

    entity.getEntityData().setBoolean(TELEPORTING_KEY, false);
    wasBlocked = !(entity.world.isRemote ? clientTeleport(entity) : serverTeleport(entity));
    PacketHandler.INSTANCE.sendToAll(new PacketTeleport(PacketTeleport.Type.TELEPORT, this, wasBlocked));
    if (entity instanceof EntityPlayer) {
      ((EntityPlayer) entity).closeScreen();
    }
    return !wasBlocked;
  }

  private boolean clientTeleport(@Nonnull Entity entity) {
    return TeleportUtil.checkClientTeleport(entity, target.getLocation(), target.getDimension(), TravelSource.TELEPAD);
  }

  private boolean serverTeleport(@Nonnull Entity entity) {
    dequeueTeleport(entity, true);
    return TeleportUtil.serverTeleport(entity, target.getLocation(), target.getDimension(), false, TravelSource.TELEPAD);
  }

  /* ITravelAccessable overrides */

  @Override
  public boolean canSeeBlock(@Nonnull EntityPlayer playerName) {
    return isMaster() && inNetwork();
  }

  public int getUsage() {
    return getEnergy().getMaxUsage();
  }

  private TileTelePad getMasterTile() {
    if (masterTile != null) {
      return masterTile;
    }

    masterTile = getMaster();
    return masterTile;
  }

  @Override
  public boolean shouldRenderInPass(int pass) {
    return true;
  }

  // Inventory

  @SuppressWarnings("unchecked")
  @Override
  public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facingIn) {
    if (isMaster()) {
      if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
        return CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY.cast(tank);
      }
      return super.getCapability(capability, facingIn);
    } else if (inNetwork()) {
      return NullHelper.notnull(getMaster(), "Telepad master is null while in network!").getCapability(capability, facingIn);
    } else if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY || capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY
        || capability == CapabilityEnergy.ENERGY) {
      return null;
    } else {
      return super.getCapability(capability, facingIn);
    }
  }

  // Fluids

  @Override
  public FluidTank getInputTank(FluidStack forFluidType) {
    if (forFluidType == null || forFluidType.getFluid() != fluidType) {
      return null;
    }
    TileTelePad master = getMaster();
    if (master == null) {
      return null;
    }
    return master.tank;
  }

  @Override
  public @Nonnull FluidTank[] getOutputTanks() {
    return new FluidTank[0];
  }

  @Override
  public void setTanksDirty() {
    tankDirty = true;
    markDirty();
  }

  public int getFluidAmount() {
    return tank.getFluidAmount();
  }

  public void setFluidAmount(int level) {
    tank.setFluidAmount(level);
  }

  public @Nonnull FluidTank getTank() {
    return tank;
  }

  public Fluid getFluidType() {
    return fluidType;
  }

  @SuppressWarnings("null")
  @Override
  public @Nonnull List<ITankData> getTankDisplayData() {
    if (inNetwork()) {
      return getMaster().createDisplayData();
    }
    return Collections.emptyList();
  }

  @SuppressWarnings("null")
  private @Nonnull List<ITankData> createDisplayData() {
    ITankData data = new TankData();
    return Collections.singletonList(data);
  }

  private class TankData implements ITankData {

    @Override
    public @Nonnull EnumTankType getTankType() {
      return EnumTankType.INPUT;
    }

    @Override
    public FluidStack getContent() {
      return getTank().getFluid();
    }

    @Override
    public int getCapacity() {
      return tank.getCapacity();
    }

  }

  @Override
  public boolean isTravelSource() {
    return isMaster() && inNetwork() && TelePadConfig.telepadIsTravelAnchor.get();
  }

  @Override
  public boolean isVisible() {
    return super.isVisible() && isTravelSource();
  }

  @Override
  @Nonnull
  public Set<? extends ILocalizable> getNotification() {
    return inNetwork() ? Collections.emptySet() : Collections.singleton(new ILocalizable() {
      @Override
      @Nonnull
      public String getUnlocalizedName() {
        return Lang.STATUS_TELEPAD_UNFORMED.getKey();
      }
    });
  }

  @Override
  protected void onAfterNbtRead() {
    getInventory().getSlot(CAPSLOT).set(new ItemStack(ModObject.itemBasicCapacitor.getItemNN(), 1, DefaultCapacitorData.ENDER_CAPACITOR.ordinal()));
    super.onAfterNbtRead();
  }

}
