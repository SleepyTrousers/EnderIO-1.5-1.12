package crazypants.enderio.machine.capbank.network;

import java.util.List;

import crazypants.enderio.machine.RedstoneControlMode;
import crazypants.enderio.machine.capbank.TileCapBank;

public interface ICapBankNetwork {

  int getId();

  int getMaxIO();

  long getMaxEnergyStored();

  long getEnergyStored();

  int getMaxEnergySent();

  void setMaxEnergySend(int max);

  int getMaxEnergyRecieved();

  void setMaxEnergyReccieved(int max);

  void destroyNetwork();

  List<TileCapBank> getMembers();

  public abstract void setOutputControlMode(RedstoneControlMode outputControlMode);

  public abstract RedstoneControlMode getOutputControlMode();

  public abstract void setInputControlMode(RedstoneControlMode inputControlMode);

  public abstract RedstoneControlMode getInputControlMode();

}
