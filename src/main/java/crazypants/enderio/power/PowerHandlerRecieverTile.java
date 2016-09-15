package crazypants.enderio.power;

import net.minecraft.util.EnumFacing;

public class PowerHandlerRecieverTile extends PowerHandlerPoweredTile {

  private final EnumFacing facing;
  private final IInternalPowerReceiver tile;

  public PowerHandlerRecieverTile(IInternalPowerReceiver tile, EnumFacing facing) {
    super(tile);
    this.tile = tile;
    this.facing = facing;
  }

  @Override
  public int receiveEnergy(int maxReceive, boolean simulate) {
    return tile.receiveEnergy(facing, maxReceive, simulate);
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
