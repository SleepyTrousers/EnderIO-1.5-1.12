package crazypants.enderio.power.forge;

import javax.annotation.Nonnull;

import crazypants.enderio.power.IPowerInterface;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.energy.IEnergyStorage;

public class PowerInterfaceForge implements IPowerInterface {

  private final @Nonnull IEnergyStorage delegate;
  private final @Nonnull ICapabilityProvider provider;

  public PowerInterfaceForge(@Nonnull ICapabilityProvider provider, @Nonnull IEnergyStorage delegate) {
    this.provider = provider;
    this.delegate = delegate;
  }

  @Override
  public int receiveEnergy(int maxReceive, boolean simulate) {
    return delegate.receiveEnergy(maxReceive, simulate);
  }

  @Override
  public @Nonnull Object getProvider() {
    return provider;
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
