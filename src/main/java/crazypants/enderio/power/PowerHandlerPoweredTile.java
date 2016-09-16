package crazypants.enderio.power;

import net.minecraft.util.EnumFacing;
import net.minecraftforge.energy.IEnergyStorage;

public class PowerHandlerPoweredTile implements IEnergyStorage {

  private final IInternalPoweredTile tile;
  protected final EnumFacing from;
  
  public PowerHandlerPoweredTile(IInternalPoweredTile tile, EnumFacing from) {
    this.tile = tile;
    this.from = from;
  }

  @Override
  public int getEnergyStored() {
    return tile.getEnergyStored(from);
  }

  @Override
  public int getMaxEnergyStored() {
    return tile.getEnergyStored(from);
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
