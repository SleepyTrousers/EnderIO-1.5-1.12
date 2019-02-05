package crazypants.enderio.base.power.forge.tile;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.util.EnumFacing;
import net.minecraftforge.energy.IEnergyStorage;

public class InternalPoweredTileWrapper implements IEnergyStorage {

  public static @Nullable IEnergyStorage get(@Nonnull ILegacyPoweredTile tile, @Nullable EnumFacing facing) {
    if (facing != null && tile.canConnectEnergy(facing)) {
      return new InternalPoweredTileWrapper(tile, facing);
    }
    return null;
  }

  private final @Nonnull ILegacyPoweredTile tile;
  protected final @Nonnull EnumFacing from;

  public InternalPoweredTileWrapper(@Nonnull ILegacyPoweredTile tile, @Nonnull EnumFacing from) {
    this.tile = tile;
    this.from = from;
  }

  @Override
  public int getEnergyStored() {
    return tile.getEnergyStored();
  }

  @Override
  public int getMaxEnergyStored() {
    return tile.getMaxEnergyStored();
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
