package crazypants.enderio.power;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.util.EnumFacing;

public interface IPowerStorage {

  @Nullable
  IPowerStorage getController();

  long getEnergyStoredL();

  long getMaxEnergyStoredL();

  /**
   * If false this connection will be treated the same a regular powered block. No power will be drawn over the connection and it will not be used to balance
   * capacitor bank levels
   * 
   * @param direction
   * @return
   */
  boolean isNetworkControlledIo(@Nonnull EnumFacing direction);

  boolean isOutputEnabled(@Nonnull EnumFacing direction);

  boolean isInputEnabled(@Nonnull EnumFacing direction);

  int getMaxOutput();

  int getMaxInput();

  int getAverageIOPerTick();

  void addEnergy(int amount);

  boolean isCreative();

}
