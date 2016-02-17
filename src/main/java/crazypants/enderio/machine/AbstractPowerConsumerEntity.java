package crazypants.enderio.machine;

import crazypants.enderio.power.IInternalPowerReceiver;
import crazypants.enderio.power.PowerHandlerUtil;
import net.minecraft.util.EnumFacing;

public abstract class AbstractPowerConsumerEntity extends AbstractPoweredMachineEntity implements IInternalPowerReceiver {

  public AbstractPowerConsumerEntity(SlotDefinition slotDefinition) {
    super(slotDefinition);
  }

  @Override
  public int receiveEnergy(EnumFacing from, int maxReceive, boolean simulate) {
    if(isSideDisabled(from)) {
      return 0;
    }
    return PowerHandlerUtil.recieveInternal(this, maxReceive, from, simulate);
  }

  @Override
  public int getEnergyStored(EnumFacing from) {
    return getEnergyStored();
  }

  @Override
  public int getMaxEnergyStored(EnumFacing from) {
    return getMaxEnergyStored();
  }

}
