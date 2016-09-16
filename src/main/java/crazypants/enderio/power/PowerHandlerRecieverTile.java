package crazypants.enderio.power;

import net.minecraft.util.EnumFacing;

public class PowerHandlerRecieverTile extends PowerHandlerPoweredTile {
  
  private final IInternalPowerReceiver tile;

  public PowerHandlerRecieverTile(IInternalPowerReceiver tile, EnumFacing facing) {
    super(tile, facing);
    this.tile = tile;
  }

  @Override
  public int receiveEnergy(int maxReceive, boolean simulate) {
    return tile.receiveEnergy(from, maxReceive, simulate);
  }

  @Override
  public int extractEnergy(int maxExtract, boolean simulate) {
    return 0;
  }

  @Override
  public boolean canReceive() {
    return true;
  }

}
