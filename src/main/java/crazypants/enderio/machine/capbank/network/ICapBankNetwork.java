package crazypants.enderio.machine.capbank.network;

import java.util.Collection;
import java.util.List;

import crazypants.enderio.machine.RedstoneControlMode;
import crazypants.enderio.machine.capbank.TileCapBank;

public interface ICapBankNetwork {

  //------ Network

  int getId();

  void addMember(TileCapBank cap);

  List<TileCapBank> getMembers();

  void destroyNetwork();

  NetworkState getState();

  void onUpdateEntity(TileCapBank tileCapBank);


  //------ Energy

  long getEnergyStored();

  void addEnergy(int energy);

  int recieveEnergy(int maxReceive, boolean simulate);

  long getMaxEnergyStored();

  int getMaxIO();

  int getMaxEnergySent();

  void setMaxEnergySend(int max);

  int getMaxEnergyRecieved();

  void setMaxEnergyReccieved(int max);

  void removeReceptors(Collection<EnergyReceptor> receptors);

  void addReceptors(Collection<EnergyReceptor> receptors);


  //------ Inventory

  InventoryImpl getInventory();


  //------ Redstone

  void setOutputControlMode(RedstoneControlMode outputControlMode);

  RedstoneControlMode getOutputControlMode();

  void setInputControlMode(RedstoneControlMode inputControlMode);

  RedstoneControlMode getInputControlMode();

  void updateRedstoneSignal(TileCapBank tileCapBank, boolean recievingSignal);

  public abstract float getAverageChangePerTick();



}
