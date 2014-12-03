package crazypants.enderio.machine.capbank.network;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.util.MathHelper;
import crazypants.enderio.machine.RedstoneControlMode;
import crazypants.enderio.machine.capbank.TileCapBank;

public class CapBankClientNetwork implements ICapBankNetwork {

  private final int id;
  private final List<TileCapBank> members = new ArrayList<TileCapBank>();
  private int maxEnergySent;
  private int maxEnergyRecieved;

  private int stateUpdateCount = 0;
  private int maxIO;
  private long maxEnergyStored;
  private long energyStored;

  private RedstoneControlMode inputControlMode = RedstoneControlMode.IGNORE;
  private RedstoneControlMode outputControlMode = RedstoneControlMode.IGNORE;

  public CapBankClientNetwork(int id) {
    this.id = id;
  }

  @Override
  public int getId() {
    return id;
  }

  public void setState(NetworkState state) {
    maxEnergyRecieved = state.getMaxInput();
    maxEnergySent = state.getMaxOutput();
    maxIO = state.getMaxIO();
    maxEnergyStored = state.getMaxEnergyStored();
    energyStored = state.getEnergyStored();
    inputControlMode = state.getInputMode();
    outputControlMode = state.getOutputMode();
    stateUpdateCount++;
  }

  public int getStateUpdateCount() {
    return stateUpdateCount;
  }

  public void setStateUpdateCount(int stateUpdateCount) {
    this.stateUpdateCount = stateUpdateCount;
  }

  public void addMember(TileCapBank capBank) {
    members.add(capBank);
  }

  @Override
  public List<TileCapBank> getMembers() {
    return members;
  }

  @Override
  public void destroyNetwork() {
    for (TileCapBank cb : members) {
      cb.setNetworkId(-1);
    }
  }

  @Override
  public int getMaxIO() {
    return maxIO;
  }

  @Override
  public long getMaxEnergyStored() {
    return maxEnergyStored;
  }

  public void setEnergyStored(long energyStored) {
    this.energyStored = energyStored;
  }

  @Override
  public long getEnergyStored() {
    return energyStored;
  }

  @Override
  public int getMaxEnergySent() {
    return maxEnergySent;
  }

  @Override
  public void setMaxEnergySend(int max) {
    maxEnergySent = MathHelper.clamp_int(max, 0, maxIO);
  }

  @Override
  public int getMaxEnergyRecieved() {
    return maxEnergyRecieved;
  }

  @Override
  public void setMaxEnergyReccieved(int max) {
    maxEnergyRecieved = MathHelper.clamp_int(max, 0, maxIO);
  }

  public double getEnergyStoredRatio() {
    if(getMaxEnergyStored() <= 0) {
      return 0;
    }
    return (double) getEnergyStored() / getMaxEnergyStored();
  }

  @Override
  public RedstoneControlMode getInputControlMode() {
    return inputControlMode;
  }

  @Override
  public void setInputControlMode(RedstoneControlMode inputControlMode) {
    this.inputControlMode = inputControlMode;
  }

  @Override
  public RedstoneControlMode getOutputControlMode() {
    return outputControlMode;
  }

  @Override
  public void setOutputControlMode(RedstoneControlMode outputControlMode) {
    this.outputControlMode = outputControlMode;
  }

}
