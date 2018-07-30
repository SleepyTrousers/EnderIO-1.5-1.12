package crazypants.enderio.machines.machine.buffer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import crazypants.enderio.api.capacitor.ICapacitorKey;
import crazypants.enderio.base.capacitor.DefaultCapacitorData;
import crazypants.enderio.base.machine.baselegacy.AbstractPowerConsumerEntity;
import crazypants.enderio.base.machine.baselegacy.SlotDefinition;
import crazypants.enderio.base.machine.modes.IoMode;
import crazypants.enderio.base.paint.IPaintable;
import crazypants.enderio.base.power.ILegacyPowerReceiver;
import crazypants.enderio.base.power.PowerDistributor;
import crazypants.enderio.base.power.forge.InternalRecieverTileWrapper;
import info.loenwind.autosave.annotations.Storable;
import info.loenwind.autosave.annotations.Store;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;

import static crazypants.enderio.machines.capacitor.CapacitorKey.BUFFER_POWER_BUFFER;
import static crazypants.enderio.machines.capacitor.CapacitorKey.BUFFER_POWER_INTAKE;
import static crazypants.enderio.machines.capacitor.CapacitorKey.BUFFER_POWER_USE;
import static crazypants.enderio.machines.capacitor.CapacitorKey.CREATIVE_BUFFER_POWER_BUFFER;
import static crazypants.enderio.machines.capacitor.CapacitorKey.CREATIVE_BUFFER_POWER_INTAKE;
import static crazypants.enderio.machines.capacitor.CapacitorKey.CREATIVE_BUFFER_POWER_USE;

@Storable
public abstract class TileBuffer extends AbstractPowerConsumerEntity implements ILegacyPowerReceiver, IPaintable.IPaintableTileEntity {

  @Storable
  public static class TileBufferItem extends TileBuffer {
    public TileBufferItem() {
      super(new SlotDefinition(9), BufferType.ITEM, BUFFER_POWER_INTAKE, BUFFER_POWER_BUFFER, BUFFER_POWER_USE);
    }
  }

  @Storable
  public static class TileBufferPower extends TileBuffer {
    public TileBufferPower() {
      super(new SlotDefinition(0, 0, 1), BufferType.POWER, BUFFER_POWER_INTAKE, BUFFER_POWER_BUFFER, BUFFER_POWER_USE);
    }
  }

  @Storable
  public static class TileBufferOmni extends TileBuffer {
    public TileBufferOmni() {
      super(new SlotDefinition(9, 0, 1), BufferType.OMNI, BUFFER_POWER_INTAKE, BUFFER_POWER_BUFFER, BUFFER_POWER_USE);
    }
  }

  @Storable
  public static class TileBufferCreative extends TileBuffer {
    public TileBufferCreative() {
      super(new SlotDefinition(9), BufferType.CREATIVE, CREATIVE_BUFFER_POWER_INTAKE, CREATIVE_BUFFER_POWER_BUFFER, CREATIVE_BUFFER_POWER_USE);
    }
  }

  private final @Nonnull BufferType type;
  private transient PowerDistributor dist;
  @Store
  private int maxOut;
  @Store
  private int maxIn;
  private int maxOutIsMax = 0, maxInIsMax = 0;

  public TileBuffer(@Nonnull SlotDefinition slotDefinition, @Nonnull BufferType type, @Nonnull ICapacitorKey maxEnergyRecieved,
      @Nonnull ICapacitorKey maxEnergyStored, @Nonnull ICapacitorKey maxEnergyUsed) {
    super(slotDefinition, maxEnergyRecieved, maxEnergyStored, maxEnergyUsed);
    this.type = type;
  }

  protected void initMaxIO() {
    if (type.isCreative) {
      setEnergyStored(getMaxEnergyStored() / 2);
      markDirty();
    }
    if (getCapacitorData() != DefaultCapacitorData.NONE || maxOut < 0 || maxIn < 0) {
      final int max = getMaxIO();
      if (maxOutIsMax == maxOut && maxOutIsMax != max) {
        // on the last check, the value was on the max and now the max has changed...keep the value on the max
        maxOut = max;
      }
      if (maxInIsMax == maxIn && maxInIsMax != max) {
        maxIn = max;
      }
      maxIn = MathHelper.clamp(maxIn, 0, max);
      maxOut = MathHelper.clamp(maxOut, 0, max);
      maxOutIsMax = maxOut == max ? max : -9999;
      maxInIsMax = maxIn == max ? max : -9999;
      markDirty();
    }
  }

  public int getMaxIO() {
    return maxEnergyRecieved.get(getCapacitorData());
  }

  @Override
  public void onCapacitorDataChange() {
    initMaxIO();
    super.onCapacitorDataChange();
  }

  @Override
  public @Nonnull String getMachineName() {
    return getType().getUnlocalizedName();
  }

  @Override
  public boolean isMachineItemValidForSlot(int i, @Nonnull ItemStack itemstack) {
    return true;
  }

  @Override
  public boolean isActive() {
    return false;
  }

  @Override
  protected boolean processTasks(boolean redstoneCheck) {
    if (!redstoneCheck || getEnergyStored() <= 0 || !tryToUsePower()) {
      return false;
    }
    if (dist == null) {
      dist = new PowerDistributor(getPos());
    }
    int transmitted = dist.transmitEnergy(world, Math.min(getMaxOutput(), getEnergyStored()));
    if (!isCreative()) {
      setEnergyStored(getEnergyStored() - transmitted);
    }
    return false;
  }

  @Override
  public void setIoMode(@Nullable EnumFacing faceHit, @Nullable IoMode mode) {
    super.setIoMode(faceHit, mode);
    if (dist != null) {
      dist.neighboursChanged();
    }
  }

  @Override
  public void clearAllIoModes() {
    super.clearAllIoModes();
    if (dist != null) {
      dist.neighboursChanged();
    }
  }

  @Override
  public void onNeighborBlockChange(@Nonnull IBlockState state, @Nonnull World worldIn, @Nonnull BlockPos posIn, @Nonnull Block blockIn,
      @Nonnull BlockPos fromPos) {
    super.onNeighborBlockChange(state, worldIn, posIn, blockIn, fromPos);
    if (dist != null) {
      dist.neighboursChanged();
    }
  }

  @Override
  public void writeCustomNBT(@Nonnull ItemStack stack) {
    super.writeCustomNBT(stack);
    stack.setItemDamage(getType().ordinal());
  }

  @Override
  public void readCustomNBT(@Nonnull ItemStack stack) {
    super.readCustomNBT(stack);
    if (type.isCreative) {
      setEnergyStored(getMaxEnergyStored() / 2);
    }
    initMaxIO();
  }

  @Override
  public boolean canConnectEnergy(@Nonnull EnumFacing from) {
    return hasPower();
  }

  @Override
  public int getMaxEnergyRecieved(EnumFacing dir) {
    return getMaxInput();
  }

  @Override
  public int receiveEnergy(EnumFacing from, int maxReceive, boolean simulate) {
    return hasPower() && getIoMode(from).canRecieveInput() ? super.receiveEnergy(from, maxReceive, simulate || isCreative()) : 0;
  }

  @Override
  protected boolean doPull(@Nullable EnumFacing dir) {
    if (isCreative()) {
      ItemStack[] invCopy = new ItemStack[inventory.length];
      for (int i = 0; i < inventory.length; i++) {
        invCopy[i] = inventory[i] == null ? null : inventory[i].copy();
      }

      boolean ret = super.doPull(dir);

      inventory = invCopy;
      return ret;
    } else {
      return super.doPull(dir);
    }
  }

  @Override
  protected boolean doPush(@Nullable EnumFacing dir) {
    if (dir == null || !shouldDoWorkThisTick(20)) {
      return false;
    }

    if (isCreative()) {
      ItemStack[] invCopy = new ItemStack[inventory.length];
      for (int i = 0; i < inventory.length; i++) {
        invCopy[i] = inventory[i] == null ? null : inventory[i].copy();
      }
      boolean ret = super.doPush(dir);

      inventory = invCopy;
      return ret;
    } else {
      return super.doPush(dir);
    }
  }

  public boolean hasInventory() {
    return getType().hasInventory;
  }

  @Override
  public boolean hasPower() {
    return getType().hasPower;
  }

  public boolean isCreative() {
    return getType().isCreative;
  }

  @Override
  public boolean displayPower() {
    return hasPower();
  }

  public void setIO(int in, int out) {
    if (getCapacitorData() != DefaultCapacitorData.NONE) {
      this.maxIn = in;
      this.maxOut = out;
      initMaxIO();
    }
    markDirty();
  }

  public int getMaxInput() {
    return getCapacitorData() == DefaultCapacitorData.NONE ? 0 : maxIn;
  }

  public int getMaxOutput() {
    return getCapacitorData() == DefaultCapacitorData.NONE ? 0 : maxOut;
  }

  @Override
  public int getMaxEnergyStored() {
    return hasPower() ? super.getMaxEnergyStored() : 0;
  }

  @SuppressWarnings("unchecked")
  @Override
  public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facingIn) {
    if (capability == CapabilityEnergy.ENERGY) {
      return hasPower() ? (T) new InternalRecieverTileWrapper(this, facingIn) : null;
    }
    return super.getCapability(capability, facingIn);
  }

  private BufferType getType() {
    return type;
  }

}
