package crazypants.enderio.machine.baselegacy;

import javax.annotation.Nonnull;

import com.enderio.core.common.NBTAction;
import com.enderio.core.common.vecmath.VecmathUtil;

import crazypants.enderio.capacitor.CapacitorHelper;
import crazypants.enderio.capacitor.CapacitorKey;
import crazypants.enderio.capacitor.CapacitorKeyType;
import crazypants.enderio.capacitor.DefaultCapacitorData;
import crazypants.enderio.capacitor.DefaultCapacitorKey;
import crazypants.enderio.capacitor.ICapacitorData;
import crazypants.enderio.capacitor.ICapacitorKey;
import crazypants.enderio.capacitor.Scaler;
import crazypants.enderio.init.IModObject;
import crazypants.enderio.network.PacketHandler;
import crazypants.enderio.power.ILegacyPoweredTile;
import crazypants.util.NbtValue;
import info.loenwind.autosave.annotations.Storable;
import info.loenwind.autosave.annotations.Store;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.MathHelper;

@Storable
public abstract class AbstractPoweredMachineEntity extends AbstractInventoryMachineEntity implements ILegacyPoweredTile {

  // Power
  protected @Nonnull ICapacitorData capacitorData = DefaultCapacitorData.NONE;
  protected final @Nonnull ICapacitorKey maxEnergyRecieved, maxEnergyStored, maxEnergyUsed;

  @Store({ NBTAction.SAVE, NBTAction.SYNC, NBTAction.UPDATE })
  // Not NBTAction.ITEM to keep the storedEnergy tag out in the open
  private int storedEnergyRF;
  protected float lastSyncPowerStored = -1;

  @Deprecated
  protected AbstractPoweredMachineEntity(@Nonnull SlotDefinition slotDefinition) {
    this(slotDefinition, null);
  }

  protected AbstractPoweredMachineEntity(@Nonnull SlotDefinition slotDefinition, IModObject modObject) {
    super(slotDefinition);
    if (modObject == null) {
      this.maxEnergyRecieved = CapacitorKey.LEGACY_ENERGY_INTAKE;
      this.maxEnergyStored = CapacitorKey.LEGACY_ENERGY_BUFFER;
      this.maxEnergyUsed = CapacitorKey.LEGACY_ENERGY_USE;
    } else {
      this.maxEnergyRecieved = new DefaultCapacitorKey(modObject, CapacitorKeyType.ENERGY_INTAKE, Scaler.Factory.POWER, 80);
      this.maxEnergyStored = new DefaultCapacitorKey(modObject, CapacitorKeyType.ENERGY_BUFFER, Scaler.Factory.POWER, 100000);
      this.maxEnergyUsed = new DefaultCapacitorKey(modObject, CapacitorKeyType.ENERGY_USE, Scaler.Factory.POWER, 20);
    }
  }

  public AbstractPoweredMachineEntity(@Nonnull SlotDefinition slotDefinition, @Nonnull ICapacitorKey maxEnergyRecieved, @Nonnull ICapacitorKey maxEnergyStored,
      @Nonnull ICapacitorKey maxEnergyUsed) {
    super(slotDefinition);
    this.maxEnergyRecieved = maxEnergyRecieved;
    this.maxEnergyStored = maxEnergyStored;
    this.maxEnergyUsed = maxEnergyUsed;
  }

  @Override
  public void init() {
    super.init();
    onCapacitorDataChange();
  }

  @Override
  public void doUpdate() {

    super.doUpdate();

    if (world.isRemote) {
      return;
    }
    boolean powerChanged = (lastSyncPowerStored != storedEnergyRF && shouldDoWorkThisTick(5));
    if (powerChanged) {
      lastSyncPowerStored = storedEnergyRF;
      PacketHandler.sendToAllAround(new PacketLegacyPowerStorage(this), this);
    }
  }

  // RF API Power

  @Override
  public boolean canConnectEnergy(@Nonnull EnumFacing from) {
    return !isSideDisabled(from);
  }

  @Override
  public int getMaxEnergyStored() {
    return maxEnergyStored.get(capacitorData);
  }

  @Override
  public void setEnergyStored(int stored) {
    storedEnergyRF = MathHelper.clamp(stored, 0, getMaxEnergyStored());
  }

  @Override
  public int getEnergyStored() {
    return storedEnergyRF;
  }

  // ----- Common Machine Functions

  @Override
  public boolean displayPower() {
    return true;
  }

  public boolean hasPower() {
    return storedEnergyRF > 0;
  }

  public @Nonnull ICapacitorData getCapacitorData() {
    return capacitorData;
  }

  public int getEnergyStoredScaled(int scale) {
    // NB: called on the client so can't use the power provider
    final int maxEnergyStored2 = getMaxEnergyStored();
    return maxEnergyStored2 == 0 ? 0 : VecmathUtil.clamp(Math.round(scale * ((float) storedEnergyRF / maxEnergyStored2)), 0, scale);
  }

  public void onCapacitorDataChange() {
    // Force a check that the new value is in bounds
    setEnergyStored(getEnergyStored());
    forceClientUpdate.set();
  }

  public int getPowerUsePerTick() {
    return maxEnergyUsed.get(capacitorData);
  }

  @Override
  public void setInventorySlotContents(int slot, @Nonnull ItemStack contents) {
    super.setInventorySlotContents(slot, contents);
    if (slotDefinition.isUpgradeSlot(slot)) {
      updateCapacitorFromSlot();
    }
  }

  @Override
  public @Nonnull ItemStack decrStackSize(int fromSlot, int amount) {
    ItemStack res = super.decrStackSize(fromSlot, amount);
    if (slotDefinition.isUpgradeSlot(fromSlot)) {
      updateCapacitorFromSlot();
    }
    return res;
  }

  private void updateCapacitorFromSlot() {
    if (slotDefinition.getNumUpgradeSlots() <= 0) {
      capacitorData = DefaultCapacitorData.BASIC_CAPACITOR;
    } else {
      final ItemStack stack = inventory[slotDefinition.minUpgradeSlot];
      final ICapacitorData capacitorDataFromItemStack = stack == null ? null : CapacitorHelper.getCapacitorDataFromItemStack(stack);
      if (capacitorDataFromItemStack == null) {
        capacitorData = DefaultCapacitorData.NONE;
      } else {
        capacitorData = capacitorDataFromItemStack;
      }
    }
    onCapacitorDataChange();
  }

  // --------- NBT

  @Override
  protected void onAfterNbtRead() {
    updateCapacitorFromSlot();
  }

  @Override
  public void readFromItemStack(@Nonnull ItemStack stack) {
    super.readFromItemStack(stack);
    setEnergyStored(NbtValue.ENERGY.getInt(stack));
  }

  @Override
  public void writeToItemStack(@Nonnull ItemStack stack) {
    super.writeToItemStack(stack);
    NbtValue.ENERGY.setInt(stack, storedEnergyRF);
  }

  // Power use

  protected final boolean tryToUsePower() {
    int powerUsePerTick = getPowerUsePerTick();
    if (powerUsePerTick <= 0) {
      return true;
    } else if (powerUsePerTick <= getEnergyStored()) {
      usePower(powerUsePerTick);
      return true;
    }
    return false;
  }

  protected final int usePower() {
    return usePower(getPowerUsePerTick());
  }

  protected int usePower(int wantToUse) {
    int used = Math.min(getEnergyStored(), wantToUse);
    setEnergyStored(Math.max(0, getEnergyStored() - used));
    return used;
  }

}
