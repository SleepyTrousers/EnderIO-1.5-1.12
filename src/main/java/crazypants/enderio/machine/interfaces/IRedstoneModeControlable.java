package crazypants.enderio.machine.interfaces;

import crazypants.enderio.machine.modes.RedstoneControlMode;

public interface IRedstoneModeControlable {

  RedstoneControlMode getRedstoneControlMode();

  void setRedstoneControlMode(RedstoneControlMode mode);

  boolean getRedstoneControlStatus();

}
