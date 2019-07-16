package crazypants.enderio.base.machine.base.te;

import javax.annotation.Nonnull;

import com.enderio.core.common.inventory.EnderInventory;

import crazypants.enderio.api.capacitor.ICapacitorKey;
import crazypants.enderio.base.capacitor.CapacitorKey;
import info.loenwind.autosave.annotations.Storable;

@Storable
public abstract class AbstractCapabilityGeneratorEntity extends AbstractCapabilityMachineEntity {

  protected AbstractCapabilityGeneratorEntity(EnderInventory subclassInventory, @Nonnull ICapacitorKey maxEnergyStored, @Nonnull ICapacitorKey maxEnergyUsed) {
    super(subclassInventory, CapacitorKey.NO_POWER, maxEnergyStored, maxEnergyUsed);
  }

  protected AbstractCapabilityGeneratorEntity(@Nonnull ICapacitorKey maxEnergyStored, @Nonnull ICapacitorKey maxEnergyUsed) {
    super(CapacitorKey.NO_POWER, maxEnergyStored, maxEnergyUsed);
  }

}
