package crazypants.enderio.machine.capbank.network;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import crazypants.enderio.EnderIO;
import crazypants.enderio.machine.RedstoneControlMode;
import crazypants.enderio.machine.capbank.TileCapBank;
import crazypants.enderio.machine.capbank.packet.PacketNetworkEnergyRequest;
import crazypants.enderio.network.PacketHandler;
import crazypants.enderio.power.IPowerStorage;
import crazypants.util.BlockCoord;

public class CapBankClientNetwork implements ICapBankNetwork {

  private final int id;
  private final List<TileCapBank> members = new ArrayList<TileCapBank>();
  private int maxEnergySent;
  private int maxEnergyRecieved;

  private int stateUpdateCount;
  private int maxIO;
  private long maxEnergyStored;
  private long energyStored;

  private RedstoneControlMode inputControlMode = RedstoneControlMode.IGNORE;
  private RedstoneControlMode outputControlMode = RedstoneControlMode.IGNORE;

  private final InventoryImpl inventory = new InventoryImpl();

  private float aveChange;

  private long lastPowerRequestTick = -1;

  public CapBankClientNetwork(int id) {
    this.id = id;
  }

  @Override
  public int getId() {
    return id;
  }

  public void requestPowerUpdate(TileCapBank capBank, int interval) {
    long curTick = EnderIO.proxy.getTickCount();
    if(lastPowerRequestTick == -1 || curTick - lastPowerRequestTick >= interval) {
      PacketHandler.INSTANCE.sendToServer(new PacketNetworkEnergyRequest(capBank));
      lastPowerRequestTick = curTick;
    }
  }

  public void setState(World world, NetworkState state) {
    maxEnergyRecieved = state.getMaxInput();
    maxEnergySent = state.getMaxOutput();
    maxIO = state.getMaxIO();
    maxEnergyStored = state.getMaxEnergyStored();
    energyStored = state.getEnergyStored();
    inputControlMode = state.getInputMode();
    outputControlMode = state.getOutputMode();

    BlockCoord bc = state.getInventoryImplLocation();
    if(bc == null) {
      inventory.setCapBank(null);
    } else {
      TileEntity te = world.getTileEntity(bc.x, bc.y, bc.z);
      if(te instanceof TileCapBank) {
        inventory.setCapBank((TileCapBank) te);
      }
    }
    aveChange = state.getAverageChange();
    
    stateUpdateCount++;
  }

  public int getStateUpdateCount() {
    return stateUpdateCount;
  }

  public void setStateUpdateCount(int stateUpdateCount) {
    this.stateUpdateCount = stateUpdateCount;
  }

  @Override
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
      cb.setNetwork(null);
    }
  }

  @Override
  public int getMaxIO() {
    return maxIO;
  }

  @Override
  public long getMaxEnergyStoredL() {
    return maxEnergyStored;
  }

  public void setMaxEnergyStoredL(long maxEnergyStored) {
    this.maxEnergyStored = maxEnergyStored;
  }

  public void setEnergyStored(long energyStored) {
    this.energyStored = energyStored;
  }

  @Override
  public long getEnergyStoredL() {
    return energyStored;
  }

  @Override
  public int getMaxOutput() {
    return maxEnergySent;
  }

  @Override
  public void setMaxOutput(int max) {
    maxEnergySent = MathHelper.clamp_int(max, 0, maxIO);
  }

  @Override
  public int getMaxInput() {
    return maxEnergyRecieved;
  }

  @Override
  public void setMaxInput(int max) {
    maxEnergyRecieved = MathHelper.clamp_int(max, 0, maxIO);
  }

  public double getEnergyStoredRatio() {
    if(getMaxEnergyStoredL() <= 0) {
      return 0;
    }
    return (double) getEnergyStoredL() / getMaxEnergyStoredL();
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

  @Override
  public InventoryImpl getInventory() {
    return inventory;
  }

  @Override
  public float getAverageChangePerTick() {
    return aveChange;
  }

  public void setAverageChangePerTick(float aveChange) {
    this.aveChange = aveChange;
  }

  @Override
  public NetworkState getState() {
    return new NetworkState(this);
  }

  @Override
  public void onUpdateEntity(TileCapBank tileCapBank) {
  }

  @Override
  public void addEnergy(int energy) {
  }

  @Override
  public int recieveEnergy(int maxReceive, boolean simulate) {
    return 0;
  }

  @Override
  public void removeReceptors(Collection<EnergyReceptor> receptors) {
  }

  @Override
  public void addReceptors(Collection<EnergyReceptor> receptors) {
  }

  @Override
  public void updateRedstoneSignal(TileCapBank tileCapBank, boolean recievingSignal) {
  }

  @Override
  public boolean isOutputEnabled() {
    return true;
  }

  @Override
  public boolean isInputEnabled() {
    return true;
  }

  @Override
  public IPowerStorage getController() {
    return this;
  }

  @Override
  public boolean isOutputEnabled(ForgeDirection direction) {
    return isOutputEnabled();
  }

  @Override
  public boolean isInputEnabled(ForgeDirection direction) {
    return isInputEnabled();
  }

  @Override
  public boolean isCreative() {
    return false;
  }

}
