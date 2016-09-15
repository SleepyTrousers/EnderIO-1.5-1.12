package crazypants.enderio.power;

import net.minecraftforge.energy.IEnergyStorage;

public class PowerHandlerPoweredTile implements IEnergyStorage {

  private final IInternalPoweredTile tile;
  
  public PowerHandlerPoweredTile(IInternalPoweredTile tile) {
    this.tile = tile;
  }

  @Override
  public int getEnergyStored() {
    return tile.getEnergyStored();
  }

  @Override
  public int getMaxEnergyStored() {
    return tile.getEnergyStored();
  }
  
  @Override
  public int receiveEnergy(int maxReceive, boolean simulate) {
    return 0;
  }

  @Override
  public int extractEnergy(int maxExtract, boolean simulate) {
    return 0;
  }

  @Override
  public boolean canExtract() {
    return false;
  }

  @Override
  public boolean canReceive() {
    return false;
  }
  
}
