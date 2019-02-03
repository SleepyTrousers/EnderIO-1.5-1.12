package crazypants.enderio.base.power.forge.tile;

import javax.annotation.Nonnull;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;

public interface ILegacyPoweredTile {

  boolean canConnectEnergy(@Nonnull EnumFacing from);

  int getEnergyStored();

  int getMaxEnergyStored();

  void setEnergyStored(int storedEnergy);

  @Nonnull
  BlockPos getLocation();

  /**
   * Should the power be displayed in WAILA or other places
   */
  boolean displayPower();

  public interface Generator extends ILegacyPoweredTile {

  }

  public interface Receiver extends ILegacyPoweredTile {

    int getMaxEnergyRecieved(EnumFacing dir);

    int receiveEnergy(EnumFacing from, int maxReceive, boolean simulate);

  }

}
