package crazypants.enderio.base.machine.baselegacy;

import javax.annotation.Nonnull;

import crazypants.enderio.base.capacitor.ICapacitorKey;
import crazypants.enderio.base.init.IModObject;
import crazypants.enderio.base.power.ILegacyPowerReceiver;
import crazypants.enderio.base.power.PowerHandlerUtil;
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

  protected AbstractPowerConsumerEntity(@Nonnull SlotDefinition slotDefinition, IModObject modObject) {
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
