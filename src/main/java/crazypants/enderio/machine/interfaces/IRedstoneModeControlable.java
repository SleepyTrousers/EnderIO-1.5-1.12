package crazypants.enderio.machine;

public interface IRedstoneModeControlable {

  RedstoneControlMode getRedstoneControlMode();

  void setRedstoneControlMode(RedstoneControlMode mode);

  boolean getRedstoneControlStatus();

}
