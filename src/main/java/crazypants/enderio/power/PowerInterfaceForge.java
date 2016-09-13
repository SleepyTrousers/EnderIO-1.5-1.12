package crazypants.enderio.power;

import net.minecraftforge.energy.IEnergyStorage;

public class PowerInterfaceForge implements IPowerInterface {

  private final IEnergyStorage delegate;

  public PowerInterfaceForge(IEnergyStorage delegate) {
    this.delegate = delegate;
  }

  @Override
  public int receiveEnergy(int maxReceive, boolean simulate) {
    return delegate.receiveEnergy(maxReceive, simulate);
  }

  @Override
  public Object getDelegate() {
    return delegate;
  }

  @Override
  public int extractEnergy(int maxExtract, boolean simulate) {
    return delegate.extractEnergy(maxExtract, simulate);
  }

  @Override
  public int getEnergyStored() {
    return delegate.getEnergyStored();
  }

  @Override
  public int getMaxEnergyStored() {
    return delegate.getMaxEnergyStored();
  }

  @Override
  public boolean canExtract() {
    return delegate.canExtract();
  }

  @Override
  public boolean canReceive() {
    return delegate.canReceive();
  }

}
