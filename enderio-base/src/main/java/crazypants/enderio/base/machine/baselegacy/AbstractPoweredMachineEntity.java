package crazypants.enderio.base.machine.baselegacy;

import java.util.Random;

import javax.annotation.Nonnull;

import info.loenwind.autosave.util.NBTAction;
import com.enderio.core.common.util.NullHelper;
import com.enderio.core.common.vecmath.VecmathUtil;

import crazypants.enderio.api.capacitor.ICapacitorData;
import crazypants.enderio.api.capacitor.ICapacitorKey;
import crazypants.enderio.base.capacitor.CapacitorHelper;
import crazypants.enderio.base.capacitor.CapacitorKey;
import crazypants.enderio.base.capacitor.DefaultCapacitorData;
import crazypants.enderio.base.machine.gui.IPowerBarData;
import crazypants.enderio.base.network.PacketHandler;
import crazypants.enderio.base.power.forge.tile.ILegacyPoweredTile;
import crazypants.enderio.util.NbtValue;
import info.loenwind.autosave.annotations.Storable;
import info.loenwind.autosave.annotations.Store;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.MathHelper;

@Storable
public abstract class AbstractPoweredMachineEntity extends AbstractInventoryMachineEntity implements ILegacyPoweredTile, IPowerBarData {

  // Power
  private @Nonnull ICapacitorData capacitorData = DefaultCapacitorData.NONE;
  protected final @Nonnull ICapacitorKey maxEnergyRecieved, maxEnergyStored, maxEnergyUsed;
  private ICapacitorKey energyLoss = null;
  private @Nonnull ICapacitorKey energyEfficiency = CapacitorKey.LEGACY_ENERGY_EFFICIENCY;

  @Store({ NBTAction.SAVE, NBTAction.CLIENT })
  // Not NBTAction.ITEM to keep the storedEnergy tag out in the open
  private int storedEnergyRF;
  private float partialEnergyLoss = 0f; // no need to store this
  protected float lastSyncPowerStored = -1;

  @Store({ NBTAction.SAVE, NBTAction.CLIENT })
  protected boolean isCapacitorDamageable = false;

  protected @Nonnull Random random = new Random();

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
    losePower(getPowerLossPerTick());
    final int scaledPower = scaledPower();
    if ((lastSyncPowerStored != scaledPower && (lastSyncPowerStored == 0 || scaledPower == 0 || shouldDoWorkThisTick(20)))) {
      lastSyncPowerStored = scaledPower;
      PacketHandler.sendToAllAround(new PacketLegacyPowerStorage(this), this);
    }
  }

  protected int scaledPower() {
    if (storedEnergyRF == 0) {
      return 0;
    } else {
      return 1 + storedEnergyRF / 1000;
    }
  }

  @Override
  public float getPowerLossPerTick() {
    return energyLoss != null ? energyLoss.getFloat(getCapacitorData()) : 0;
  }

  // RF API Power

  @Override
  public boolean canConnectEnergy(@Nonnull EnumFacing from) {
    return !isSideDisabled(from);
  }

  @Override
  public int getMaxEnergyStored() {
    return maxEnergyStored.get(getCapacitorData());
  }

  @Override
  public void setEnergyStored(int stored) {
    // Don't clamp this on the client, as the client may not know about the capacitor (inventory data is only synced while the GUI is open, while energy is
    // synced more often as the rendering depends on it)
    storedEnergyRF = world.isRemote ? stored : MathHelper.clamp(stored, 0, getMaxEnergyStored());
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

  @Override
  public @Nonnull ICapacitorData getCapacitorData() {
    if (slotDefinition.getNumUpgradeSlots() <= 0) {
      return DefaultCapacitorData.BASIC_CAPACITOR;
    } else {
      return capacitorData;
    }
  }

  public int getEnergyStoredScaled(int scale) {
    // NB: called on the client so can't use the power provider
    final int maxEnergyStored2 = getMaxEnergyStored();
    return maxEnergyStored2 == 0 ? 0 : VecmathUtil.clamp(Math.round(scale * ((float) storedEnergyRF / maxEnergyStored2)), 0, scale);
  }

  public void onCapacitorDataChange() {
    // Force a check that the new value is in bounds
    setEnergyStored(getEnergyStored());
  }

  public int getPowerUsePerTick() {
    return getMaxUsage(maxEnergyUsed);
  }

  @Override
  public void setInventorySlotContents(int slot, @Nonnull ItemStack contents) {
    super.setInventorySlotContents(slot, contents);
    if (slotDefinition.isUpgradeSlot(slot)) {
      updateCapacitorFromSlot();
    }
  }

  private void updateCapacitorFromSlot() {
    if (slotDefinition.getNumUpgradeSlots() <= 0) {
      capacitorData = DefaultCapacitorData.BASIC_CAPACITOR;
    } else {
      final ItemStack stack = inventory[slotDefinition.minUpgradeSlot];
      final ICapacitorData capacitorDataFromItemStack = NullHelper
          .first(CapacitorHelper.getCapacitorDataFromItemStack(NullHelper.first(stack, ItemStack.EMPTY)), DefaultCapacitorData.NONE);
      if (!capacitorData.equals(capacitorDataFromItemStack)) {
        capacitorData = capacitorDataFromItemStack;
        isCapacitorDamageable = stack.isItemStackDamageable();
        onCapacitorDataChange();
      }
    }
  }

  protected void damageCapacitor() {
    if (isCapacitorDamageable) {
      ItemStack cap = inventory[slotDefinition.minUpgradeSlot];
      if (cap.attemptDamageItem(1, random, null)) {
        this.setInventorySlotContents(slotDefinition.minUpgradeSlot, ItemStack.EMPTY);
      }
    }
  }

  // --------- NBT

  @Override
  protected void onAfterNbtRead() {
    super.onAfterNbtRead();
    updateCapacitorFromSlot();
  }

  @Override
  public void readCustomNBT(@Nonnull ItemStack stack) {
    super.readCustomNBT(stack);
    setEnergyStored(NbtValue.ENERGY.getInt(stack));
  }

  @Override
  public void writeCustomNBT(@Nonnull ItemStack stack) {
    super.writeCustomNBT(stack);
    NbtValue.ENERGY.setInt(stack, getEnergyStored());
    NbtValue.ENERGY_BUFFER.setInt(stack, getMaxEnergyStored());
  }

  // Power use

  protected final boolean tryToUsePower() {
    if (getCapacitorData() == DefaultCapacitorData.NONE) {
      return false;
    }
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

  // extra method because task machines use usePower() to advance their tasks
  protected float losePower(float wantToUse) {
    if (wantToUse < 1) {
      if (partialEnergyLoss < wantToUse) {
        if (getEnergyStored() > 0) {
          setEnergyStored(Math.max(0, getEnergyStored() - 1));
          partialEnergyLoss++;
        } else {
          partialEnergyLoss = 0;
          return 0;
        }
      }
      partialEnergyLoss -= wantToUse;
      return wantToUse;
    }

    int used = (int) Math.min(getEnergyStored(), wantToUse);
    if (used > 0) {
      setEnergyStored(Math.max(0, getEnergyStored() - used));
    }
    return used;
  }

  public ICapacitorKey getEnergyLoss() {
    return energyLoss;
  }

  public void setEnergyLoss(ICapacitorKey energyLoss) {
    this.energyLoss = energyLoss;
  }

  @Override
  public int getMaxUsage() {
    return getMaxUsage(maxEnergyUsed);
  }

  public int getMaxUsage(@Nonnull ICapacitorKey key) {
    return key.get(capacitorData);
  }

  protected float getEfficiencyMultiplier() {
    return energyEfficiency.getFloat(getCapacitorData());
  }

  protected void setEfficiencyMultiplier(@Nonnull ICapacitorKey key) {
    energyEfficiency = key;
  }

}
