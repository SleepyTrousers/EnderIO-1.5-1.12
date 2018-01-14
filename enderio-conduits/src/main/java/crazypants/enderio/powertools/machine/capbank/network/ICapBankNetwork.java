package crazypants.enderio.powertools.machine.capbank.network;

import java.util.Collection;

import javax.annotation.Nonnull;

import crazypants.enderio.base.machine.modes.RedstoneControlMode;
import crazypants.enderio.base.power.IPowerStorage;
import crazypants.enderio.powertools.machine.capbank.TileCapBank;

public interface ICapBankNetwork extends IPowerStorage {

  // ------ Network

  int getId();

  void addMember(@Nonnull TileCapBank cap);

  @Nonnull
  Collection<TileCapBank> getMembers();

  void destroyNetwork();

  @Nonnull
  NetworkState getState();

  // ------ Energy

  @Override
  long getEnergyStoredL();

  @Override
  void addEnergy(int energy);

  int receiveEnergy(int maxReceive, boolean simulate);

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

  float getAverageInputPerTick();

  float getAverageOutputPerTick();

  void removeReceptors(@Nonnull Collection<EnergyReceptor> receptors);

  void addReceptors(@Nonnull Collection<EnergyReceptor> receptors);

  // ------ Inventory

  @Nonnull
  InventoryImpl getInventory();

  // ------ Redstone

  void setOutputControlMode(@Nonnull RedstoneControlMode outputControlMode);

  @Nonnull
  RedstoneControlMode getOutputControlMode();

  void setInputControlMode(@Nonnull RedstoneControlMode inputControlMode);

  @Nonnull
  RedstoneControlMode getInputControlMode();

  void updateRedstoneSignal(@Nonnull TileCapBank tileCapBank, boolean recievingSignal);

  boolean isOutputEnabled();

  boolean isInputEnabled();

  // ------- rendering info caching

  void invalidateDisplayInfoCache();

}
