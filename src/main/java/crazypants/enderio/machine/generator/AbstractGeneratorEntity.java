package crazypants.enderio.machine.generator;

import crazypants.enderio.capacitor.ICapacitorKey;
import crazypants.enderio.machine.MachineObject;
import crazypants.enderio.machine.baselegacy.AbstractPoweredMachineEntity;
import crazypants.enderio.machine.baselegacy.SlotDefinition;
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

  protected AbstractGeneratorEntity(SlotDefinition slotDefinition, MachineObject modObject) {
    super(slotDefinition, modObject);
  }

  public int getPowerLossPerTick() {
    return maxEnergyRecieved.get(capacitorData);
  }

}
