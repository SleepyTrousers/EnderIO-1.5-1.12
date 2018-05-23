package crazypants.enderio.base.machine.base.te;

import javax.annotation.Nonnull;

import com.enderio.core.common.inventory.EnderInventory;

import crazypants.enderio.base.capacitor.CapacitorKey;
import crazypants.enderio.base.capacitor.ICapacitorKey;

public abstract class AbstractCapabilityGeneratorEntity extends AbstractCapabilityPoweredMachineEntity {

  protected AbstractCapabilityGeneratorEntity(EnderInventory subclassInventory, @Nonnull ICapacitorKey maxEnergyStored, @Nonnull ICapacitorKey maxEnergyUsed) {
    super(subclassInventory, CapacitorKey.NO_POWER, maxEnergyStored, maxEnergyUsed);
  }

}
