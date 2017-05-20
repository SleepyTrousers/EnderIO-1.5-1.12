package crazypants.enderio.machine.generator;

import crazypants.enderio.ModObject;
import crazypants.enderio.capacitor.ICapacitorKey;
import crazypants.enderio.machine.AbstractPoweredMachineEntity;
import crazypants.enderio.machine.SlotDefinition;
import crazypants.enderio.power.ILegacyPoweredTile;
import info.loenwind.autosave.annotations.Storable;

@Storable
public abstract class AbstractGeneratorEntity extends AbstractPoweredMachineEntity implements ILegacyPoweredTile {

  //RF API Power

  @Deprecated
  public AbstractGeneratorEntity(SlotDefinition slotDefinition) {
    super(slotDefinition);
  }

  protected AbstractGeneratorEntity(SlotDefinition slotDefinition, ICapacitorKey maxEnergyRecieved, ICapacitorKey maxEnergyStored, ICapacitorKey maxEnergyUsed) {
    super(slotDefinition, maxEnergyRecieved, maxEnergyStored, maxEnergyUsed);
  }

  protected AbstractGeneratorEntity(SlotDefinition slotDefinition, ModObject modObject) {
    super(slotDefinition, modObject);
  }

}
