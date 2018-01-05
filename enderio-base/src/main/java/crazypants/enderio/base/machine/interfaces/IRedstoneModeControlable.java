package crazypants.enderio.base.machine.interfaces;

import javax.annotation.Nonnull;

import crazypants.enderio.base.machine.modes.RedstoneControlMode;

public interface IRedstoneModeControlable {

  @Nonnull
  RedstoneControlMode getRedstoneControlMode();

  void setRedstoneControlMode(@Nonnull RedstoneControlMode mode);

  boolean getRedstoneControlStatus();

}
