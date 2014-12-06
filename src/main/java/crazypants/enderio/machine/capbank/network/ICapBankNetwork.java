package crazypants.enderio.machine.capbank.network;

import java.util.Collection;
import java.util.List;

import crazypants.enderio.machine.RedstoneControlMode;
import crazypants.enderio.machine.capbank.TileCapBank;
import crazypants.enderio.power.IPowerStorage;

public interface ICapBankNetwork extends IPowerStorage {

  //------ Network

  int getId();

  void addMember(TileCapBank cap);

  List<TileCapBank> getMembers();

  void destroyNetwork();

  NetworkState getState();

  void onUpdateEntity(TileCapBank tileCapBank);


  //------ Energy

  @Override
  long getEnergyStoredL();

  @Override
  void addEnergy(int energy);

  int recieveEnergy(int maxReceive, boolean simulate);

  @Override
  long getMaxEnergyStoredL();

  int getMaxIO();

  @Override
  int getMaxOutput();

  void setMaxOutput(int max);

  @Override
  int getMaxInput();

  void setMaxInput(int max);

  float getAverageChangePerTick();

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

  boolean isOutputEnabled();

  boolean isInputEnabled();



}
