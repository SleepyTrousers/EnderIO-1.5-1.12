package crazypants.enderio.power.tesla;

import net.darkhax.tesla.api.ITeslaConsumer;
import net.darkhax.tesla.api.ITeslaHolder;
import net.darkhax.tesla.api.ITeslaProducer;
import net.minecraftforge.energy.IEnergyStorage;

public class TeslaCapabilityWrapper implements IEnergyStorage {
  
  private ITeslaHolder capHolder;
  private ITeslaConsumer capConsumer;
  private ITeslaProducer capProducer;

  public TeslaCapabilityWrapper(ITeslaHolder capHolder, ITeslaConsumer capConsumer, ITeslaProducer capProducer) {
    this.capHolder = capHolder;
    this.capConsumer = capConsumer;
    this.capProducer = capProducer;
  }

  @Override
  public int receiveEnergy(int maxReceive, boolean simulate) {
    if(capConsumer != null) {
      return (int)capConsumer.givePower(maxReceive, simulate);
    }
    return 0;
  }

  @Override
  public int extractEnergy(int maxExtract, boolean simulate) {
    if(capProducer != null) {
      return (int)capProducer.takePower(maxExtract, simulate);
    }
    return 0;
  }

  @Override
  public int getEnergyStored() {
    if(capHolder != null) {
      return (int)capHolder.getStoredPower();
    }
    return 0;
  }

  @Override
  public int getMaxEnergyStored() {
    if(capHolder != null) {
      return (int)capHolder.getCapacity();
    }
    return 0;
  }

  @Override
  public boolean canExtract() {
    return capProducer != null;
  }

  @Override
  public boolean canReceive() {
    return capConsumer != null;
  }

}
