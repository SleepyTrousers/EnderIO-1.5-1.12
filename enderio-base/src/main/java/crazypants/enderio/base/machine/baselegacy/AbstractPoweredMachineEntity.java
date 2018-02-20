package crazypants.enderio.base.machine.baselegacy;

import javax.annotation.Nonnull;

import com.enderio.core.common.NBTAction;
import com.enderio.core.common.vecmath.VecmathUtil;

import crazypants.enderio.base.capacitor.CapacitorHelper;
import crazypants.enderio.base.capacitor.DefaultCapacitorData;
import crazypants.enderio.base.capacitor.ICapacitorData;
import crazypants.enderio.base.capacitor.ICapacitorKey;
import crazypants.enderio.base.network.PacketHandler;
import crazypants.enderio.base.power.ILegacyPoweredTile;
import crazypants.enderio.util.NbtValue;
import info.loenwind.autosave.annotations.Storable;
import info.loenwind.autosave.annotations.Store;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.MathHelper;

@Storable
public abstract class AbstractPoweredMachineEntity extends AbstractInventoryMachineEntity implements ILegacyPoweredTile {

  // Power
  private @Nonnull ICapacitorData capacitorData = DefaultCapacitorData.NONE;
  protected final @Nonnull ICapacitorKey maxEnergyRecieved, maxEnergyStored, maxEnergyUsed;
  private ICapacitorKey energyLoss = null;

  @Store({ NBTAction.SAVE, NBTAction.CLIENT })
  // Not NBTAction.ITEM to keep the storedEnergy tag out in the open
  private int storedEnergyRF;
  protected float lastSyncPowerStored = -1;

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
    boolean powerChanged = (lastSyncPowerStored != storedEnergyRF && shouldDoWorkThisTick(5));
    if (powerChanged) {
      lastSyncPowerStored = storedEnergyRF;
      PacketHandler.sendToAllAround(new PacketLegacyPowerStorage(this), this);
    }
  }

  public int getPowerLossPerTick() {
    return energyLoss != null ? energyLoss.get(getCapacitorData()) : 0;
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
    forceClientUpdate.set();
  }

  public int getPowerUsePerTick() {
    return maxEnergyUsed.get(getCapacitorData());
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
  protected int losePower(int wantToUse) {
    int used = Math.min(getEnergyStored(), wantToUse);
    setEnergyStored(Math.max(0, getEnergyStored() - used));
    return used;
  }

  public ICapacitorKey getEnergyLoss() {
    return energyLoss;
  }

  public void setEnergyLoss(ICapacitorKey energyLoss) {
    this.energyLoss = energyLoss;
  }

}
