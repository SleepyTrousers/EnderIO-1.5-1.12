package crazypants.enderio.machines.machine.generator;

import crazypants.enderio.base.capacitor.ICapacitorKey;
import crazypants.enderio.base.machine.baselegacy.AbstractPoweredMachineEntity;
import crazypants.enderio.base.machine.baselegacy.SlotDefinition;
import crazypants.enderio.base.power.ILegacyPoweredTile;
import crazypants.enderio.machines.init.MachineObject;
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
