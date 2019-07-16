package crazypants.enderio.base.machine.base.te;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.common.inventory.EnderInventory;

import crazypants.enderio.api.capacitor.ICapacitorData;
import crazypants.enderio.api.capacitor.ICapacitorKey;
import crazypants.enderio.base.power.EnergyTank;
import crazypants.enderio.base.power.IEnergyTank;
import crazypants.enderio.base.power.NullEnergyTank;
import info.loenwind.autosave.annotations.Storable;
import info.loenwind.autosave.annotations.Store;
import info.loenwind.autosave.util.NBTAction;
import net.minecraft.item.ItemStack;

@Storable
public abstract class AbstractCapabilityPoweredMachineEntity extends AbstractCapabilityMachineEntity {

  @Store({ NBTAction.SAVE, NBTAction.CLIENT })
  // Not NBTAction.ITEM to keep the storedEnergy tag out in the open
  // TODO 1.14: remove here and store to nbt in EnergyLogic
  private final @Nonnull IEnergyTank energy;
  private final @Nonnull IEnergyLogic energyLogic;

  protected AbstractCapabilityPoweredMachineEntity(@Nonnull ICapacitorKey maxEnergyRecieved, @Nonnull ICapacitorKey maxEnergyStored,
      @Nonnull ICapacitorKey maxEnergyUsed) {
    this(null, maxEnergyRecieved, maxEnergyStored, maxEnergyUsed);
  }

  protected AbstractCapabilityPoweredMachineEntity(@Nullable EnderInventory subclassInventory, @Nonnull ICapacitorKey maxEnergyRecieved,
      @Nonnull ICapacitorKey maxEnergyStored, @Nonnull ICapacitorKey maxEnergyUsed) {
    super(subclassInventory);
    energy = new EnergyTank(this, maxEnergyRecieved, maxEnergyStored, maxEnergyUsed);
    energyLogic = new EnergyLogic(this, energy);
  }

  protected AbstractCapabilityPoweredMachineEntity() {
    this(null);
  }

  protected AbstractCapabilityPoweredMachineEntity(@Nullable EnderInventory subclassInventory) {
    super(subclassInventory);
    energy = NullEnergyTank.INSTANCE;
    energyLogic = NullEnergyLogic.INSTANCE;
  }

  // ----- Common Machine Functions

  @Override
  public void doUpdate() {
    super.doUpdate();
    getEnergyLogic().serverTick();
  }

  public boolean displayPower() {
    return getEnergyLogic().displayPower();
  }

  public boolean hasPower() {
    return getEnergyLogic().hasPower();
  }

  public @Nonnull ICapacitorData getCapacitorData() {
    return getEnergyLogic().getCapacitorData();
  }

  public @Nonnull IEnergyTank getEnergy() {
    return getEnergyLogic().getEnergy();
  }

  public IEnergyLogic getEnergyLogic() {
    return energyLogic;
  }

  protected void onCapacitorDataChange() {
  };

  @Override
  protected boolean processTasks(boolean redstoneCheck) {
    getEnergyLogic().processTasks(redstoneCheck);
    return false;
  }

  // --------- NBT

  @Override
  public void readCustomNBT(@Nonnull ItemStack stack) {
    super.readCustomNBT(stack);
    getEnergyLogic().readCustomNBT(stack);
  }

  @Override
  public void writeCustomNBT(@Nonnull ItemStack stack) {
    super.writeCustomNBT(stack);
    getEnergyLogic().writeCustomNBT(stack);
  }

  @Override
  protected void onAfterNbtRead() {
    super.onAfterNbtRead();
    getEnergyLogic().updateCapacitorFromSlot(); // TODO trace out if we need this or if the inventory calls onChange() for this
  }

}
