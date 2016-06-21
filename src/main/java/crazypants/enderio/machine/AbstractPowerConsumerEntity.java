package crazypants.enderio.machine;

import net.minecraftforge.common.util.ForgeDirection;
import crazypants.enderio.power.IInternalPowerReceiver;
import crazypants.enderio.power.PowerHandlerUtil;

public abstract class AbstractPowerConsumerEntity extends AbstractPoweredMachineEntity implements IInternalPowerReceiver {

  public AbstractPowerConsumerEntity(SlotDefinition slotDefinition) {
    super(slotDefinition);
  }

  @Override
  public int receiveEnergy(ForgeDirection from, int maxReceive, boolean simulate) {
    if(isSideDisabled(from.ordinal())) {
      return 0;
    }
    return PowerHandlerUtil.recieveInternal(this, maxReceive, from, simulate);
  }

  @Override
  public int getEnergyStored(ForgeDirection from) {
    return getEnergyStored();
  }

  @Override
  public int getMaxEnergyStored(ForgeDirection from) {
    return getMaxEnergyStored();
  }

}
