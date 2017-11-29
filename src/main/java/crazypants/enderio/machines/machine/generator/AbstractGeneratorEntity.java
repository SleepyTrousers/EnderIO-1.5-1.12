package crazypants.enderio.machines.machine.generator;

import crazypants.enderio.base.capacitor.ICapacitorKey;
import crazypants.enderio.base.machine.baselegacy.AbstractPoweredMachineEntity;
import crazypants.enderio.base.machine.baselegacy.SlotDefinition;
import crazypants.enderio.base.power.ILegacyPoweredTile;
import info.loenwind.autosave.annotations.Storable;

@Storable
public abstract class AbstractGeneratorEntity extends AbstractPoweredMachineEntity implements ILegacyPoweredTile {

  //RF API Power

  protected AbstractGeneratorEntity(SlotDefinition slotDefinition, ICapacitorKey maxEnergyRecieved, ICapacitorKey maxEnergyStored, ICapacitorKey maxEnergyUsed) {
    super(slotDefinition, maxEnergyRecieved, maxEnergyStored, maxEnergyUsed);
  }

  public int getPowerLossPerTick() {
    return maxEnergyRecieved.get(capacitorData);
  }

}
