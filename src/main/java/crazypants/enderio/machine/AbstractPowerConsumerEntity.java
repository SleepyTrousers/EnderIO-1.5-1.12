package crazypants.enderio.machine;

import crazypants.enderio.ModObject;
import crazypants.enderio.capacitor.ICapacitorKey;
import crazypants.enderio.power.IInternalPowerReceiver;
import crazypants.enderio.power.PowerHandlerRecieverTile;
import crazypants.enderio.power.PowerHandlerUtil;
import info.loenwind.autosave.annotations.Storable;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;

@Storable
public abstract class AbstractPowerConsumerEntity extends AbstractPoweredMachineEntity implements IInternalPowerReceiver {

  @Deprecated
  protected AbstractPowerConsumerEntity(SlotDefinition slotDefinition) {
    super(slotDefinition);
  }

  protected AbstractPowerConsumerEntity(SlotDefinition slotDefinition, ICapacitorKey maxEnergyRecieved, ICapacitorKey maxEnergyStored,
      ICapacitorKey maxEnergyUsed) {
    super(slotDefinition, maxEnergyRecieved, maxEnergyStored, maxEnergyUsed);
  }

  protected AbstractPowerConsumerEntity(SlotDefinition slotDefinition, ModObject modObject) {
    super(slotDefinition, modObject);
  }

  @Override
  public int receiveEnergy(EnumFacing from, int maxReceive, boolean simulate) {
    if(isSideDisabled(from)) {
      return 0;
    }
    return PowerHandlerUtil.recieveInternal(this, maxReceive, from, simulate);
  }
  
  @Override
  public int getMaxEnergyRecieved(EnumFacing dir) {
    if (isSideDisabled(dir) || maxEnergyRecieved == null) {
      return 0;
    }
    return maxEnergyRecieved.get(capacitorData);
  }
  
  @Override
  public boolean hasCapability(Capability<?> capability, EnumFacing facingIn) {
    if (capability == CapabilityEnergy.ENERGY) {
      return true;
    }
    return super.hasCapability(capability, facingIn);
  }

  @SuppressWarnings("unchecked")
  @Override
  public <T> T getCapability(Capability<T> capability, EnumFacing facingIn) {
    if (capability == CapabilityEnergy.ENERGY) {
      return (T) new PowerHandlerRecieverTile(this, facingIn);
    }
    return super.getCapability(capability, facingIn);
  }

}
