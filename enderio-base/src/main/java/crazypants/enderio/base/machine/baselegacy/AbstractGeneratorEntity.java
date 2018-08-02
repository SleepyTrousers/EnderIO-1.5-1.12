package crazypants.enderio.base.machine.baselegacy;

import javax.annotation.Nonnull;

import crazypants.enderio.api.capacitor.ICapacitorKey;
import crazypants.enderio.base.power.ILegacyPoweredTile;
import info.loenwind.autosave.annotations.Storable;

import static crazypants.enderio.base.capacitor.CapacitorKey.NO_POWER;

@Storable
public abstract class AbstractGeneratorEntity extends AbstractPoweredMachineEntity implements ILegacyPoweredTile {

  protected AbstractGeneratorEntity(@Nonnull SlotDefinition slotDefinition, @Nonnull ICapacitorKey maxEnergyStored, @Nonnull ICapacitorKey maxEnergyUsed) {
    super(slotDefinition, NO_POWER, maxEnergyStored, maxEnergyUsed);
  }

}
