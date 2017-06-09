package crazypants.enderio.machine.baselegacy;

import javax.annotation.Nonnull;

import crazypants.enderio.capacitor.ICapacitorKey;
import crazypants.enderio.init.ModObject;
import crazypants.enderio.power.ILegacyPowerReceiver;
import crazypants.enderio.power.PowerHandlerUtil;
import info.loenwind.autosave.annotations.Storable;
import net.minecraft.util.EnumFacing;

@Storable
public abstract class AbstractPowerConsumerEntity extends AbstractPoweredMachineEntity implements ILegacyPowerReceiver {

  @Deprecated
  protected AbstractPowerConsumerEntity(@Nonnull SlotDefinition slotDefinition) {
    super(slotDefinition);
  }

  protected AbstractPowerConsumerEntity(@Nonnull SlotDefinition slotDefinition, @Nonnull ICapacitorKey maxEnergyRecieved,
      @Nonnull ICapacitorKey maxEnergyStored, @Nonnull ICapacitorKey maxEnergyUsed) {
    super(slotDefinition, maxEnergyRecieved, maxEnergyStored, maxEnergyUsed);
  }

  protected AbstractPowerConsumerEntity(@Nonnull SlotDefinition slotDefinition, ModObject modObject) {
    super(slotDefinition, modObject);
  }

  @Override
  public int receiveEnergy(EnumFacing from, int maxReceive, boolean simulate) {
    if (isSideDisabled(from)) {
      return 0;
    }
    return PowerHandlerUtil.recieveInternal(this, maxReceive, from, simulate);
  }

  @Override
  public int getMaxEnergyRecieved(EnumFacing dir) {
    if (isSideDisabled(dir)) {
      return 0;
    }
    return maxEnergyRecieved.get(capacitorData);
  }

}
