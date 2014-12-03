package crazypants.enderio.machine.capbank.network;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.minecraft.world.World;
import crazypants.enderio.EnderIO;
import crazypants.enderio.machine.RedstoneControlMode;
import crazypants.enderio.machine.capbank.CapBankType;
import crazypants.enderio.machine.capbank.TileCapBank;
import crazypants.enderio.machine.capbank.packet.PacketNetworkStateResponse;
import crazypants.util.BlockCoord;

public class CapBankNetwork implements ICapBankNetwork {

  private static final int IO_CAP = 2000000000;

  protected final List<TileCapBank> capBanks = new ArrayList<TileCapBank>();

  private final int id;

  private int maxIO;

  private int maxInput = -1;

  private int maxOutput = -1;

  private long timeAtLastApply;

  private long energyStored;
  private long prevEnergyStored = -1;

  private long maxEnergyStored;

  private CapBankType type;

  private Set<BlockCoord> redstoneRecievers = new HashSet<BlockCoord>();

  private RedstoneControlMode inputControlMode = RedstoneControlMode.IGNORE;
  private RedstoneControlMode outputControlMode = RedstoneControlMode.IGNORE;

  private boolean inputRedstoneConditionMet = true;
  private boolean outputRedstoneConditionMet = true;


  public CapBankNetwork(int id) {
    this.id = id;
  }

  //--------- Network Management 

  public void init(TileCapBank cap, Collection<TileCapBank> neighbours, World world) {
    if(world.isRemote) {
      throw new UnsupportedOperationException();
    }

    type = cap.getType();
    inputControlMode = cap.getInputControlMode();
    outputControlMode = cap.getOutputControlMode();
    for (TileCapBank con : neighbours) {
      ICapBankNetwork network = con.getNetwork();
      if(network != null) {
        network.destroyNetwork();
      }
    }
    setNetwork(world, cap);
    //notifyNetworkOfUpdate();
    EnderIO.packetPipeline.sendToAllAround(new PacketNetworkStateResponse(this), cap);
  }

  //public void notifyNetworkOfUpdate() {
  //    for (TileCapBank cb : capBanks) {
  //      cb.getWorldObj().markBlockForUpdate(cb.xCoord, cb.yCoord, cb.zCoord);
  //    }
  //  }

  protected void setNetwork(World world, TileCapBank cap) {
    if(cap != null && cap.setNetwork(this)) {
      addCapBank(cap);
      Collection<TileCapBank> neighbours = NetworkUtil.getNeigbours(cap);
      for (TileCapBank neighbour : neighbours) {
        if(neighbour.getNetwork() == null) {
          setNetwork(world, neighbour);
        } else if(neighbour.getNetwork() != this) {
          neighbour.getNetwork().destroyNetwork();
          setNetwork(world, neighbour);
        }
      }
    }
  }

  @Override
  public void destroyNetwork() {
    distributeEnergy();
    TileCapBank cap = null;
    for (TileCapBank cb : capBanks) {
      cb.setNetwork(null);
      if(cap == null) {
        cap = cb;
      }
    }
    capBanks.clear();
    if(cap != null) {
      EnderIO.packetPipeline.INSTANCE.sendToAll(new PacketNetworkStateResponse(this, true));
    }
  }

  @Override
  public List<TileCapBank> getMembers() {
    return capBanks;
  }

  public void addCapBank(TileCapBank cap) {
    if(!capBanks.contains(cap)) {
      capBanks.add(cap);
      long newIO = maxIO + cap.getType().getMaxIO();
      if(newIO > IO_CAP) {
        newIO = IO_CAP;
      }
      maxIO = (int) newIO;

      energyStored += cap.getEnergyStored();
      maxEnergyStored += cap.getMaxEnergyStored();
      if(maxInput == -1) {
        maxInput = cap.getMaxEnergyRecievedOverride();
      }
      if(maxOutput == -1) {
        maxOutput = cap.getMaxEnergySentOverride();
      }
      cap.setInputControlMode(inputControlMode);
      cap.setOutputControlMode(outputControlMode);
    }
  }

  @Override
  public int getId() {
    return id;
  }

  public NetworkClientState getClientState() {
    return new NetworkClientState(this);
  }

  //--------- Tick Handling 

  public void onUpdateEntity(TileCapBank tileCapBank) {
    World world = tileCapBank.getWorldObj();
    if(world == null) {
      return;
    }
    if(world.isRemote) {
      return;
    }
    long curTime = world.getTotalWorldTime();
    if(curTime != timeAtLastApply) {
      timeAtLastApply = curTime;
      doNetworkTick();
    }
  }

  private void doNetworkTick() {
    if(energyStored != prevEnergyStored) {
      distributeEnergy();
    }
    prevEnergyStored = energyStored;
  }

  private void distributeEnergy() {
    if(capBanks.isEmpty()) {
      return;
    }
    int energyPerCapBank = (int) (energyStored / capBanks.size());
    int remaining = (int) (energyStored % capBanks.size());
    for (TileCapBank cb : capBanks) {
      cb.setEnergyStored(energyPerCapBank);
    }
    TileCapBank cb = capBanks.get(0);
    cb.setEnergyStored(cb.getEnergyStored() + remaining);
  }

  //------ Power 

  public int recieveEnergy(int maxReceive, boolean simulate) {
    if(maxReceive <= 0 || !inputRedstoneConditionMet) {
      return 0;
    }

    long spaceAvailable = maxEnergyStored - energyStored;
    if(spaceAvailable > Integer.MAX_VALUE) {
      spaceAvailable = Integer.MAX_VALUE;
    }
    int res = Math.min(maxReceive, (int) spaceAvailable);
    res = Math.min(maxReceive, getMaxEnergyRecieved());
    if(!simulate && !type.isCreative()) {
      addEnergy(res);
    }
    return res;
  }

  public void addEnergy(int energy) {
    energyStored += energy;
    if(energyStored > maxEnergyStored) {
      energyStored = maxEnergyStored;
    } else if(energyStored < 0) {
      energyStored = 0;
    }
  }


  @Override
  public long getEnergyStored() {
    return energyStored;
  }

  @Override
  public long getMaxEnergyStored() {
    return maxEnergyStored;
  }

  @Override
  public int getMaxIO() {
    return maxIO;
  }

  //----- IO overrides

  @Override
  public int getMaxEnergyRecieved() {
    if(maxInput == -1) {
      return maxIO;
    }
    return Math.min(maxInput, maxIO);
  }

  @Override
  public int getMaxEnergySent() {
    if(maxOutput == -1) {
      return maxIO;
    }
    return Math.min(maxOutput, maxIO);
  }

  @Override
  public void setMaxEnergyReccieved(int max) {
    if(max >= maxIO) {
      maxInput = -1;
    } else if(max < 0) {
      maxInput = 0;
    } else {
      maxInput = max;
    }
    for (TileCapBank cb : capBanks) {
      cb.setMaxEnergyRecieved(maxInput);
    }
  }

  @Override
  public void setMaxEnergySend(int max) {
    if(max >= maxIO) {
      maxOutput = -1;
    } else if(max < 0) {
      maxOutput = 0;
    } else {
      maxOutput = max;
    }
    for (TileCapBank cb : capBanks) {
      cb.setMaxEnergySend(maxOutput);
    }
  }



  //----------- Redstone

  @Override
  public RedstoneControlMode getInputControlMode() {
    return inputControlMode;
  }

  @Override
  public void setInputControlMode(RedstoneControlMode inputControlMode) {
    if(this.inputControlMode == inputControlMode) {
      return;
    }
    this.inputControlMode = inputControlMode;
    for (TileCapBank capBank : capBanks) {
      capBank.setInputControlMode(inputControlMode);
    }
    updateRedstoneConditions();
  }

  @Override
  public RedstoneControlMode getOutputControlMode() {
    return outputControlMode;
  }

  @Override
  public void setOutputControlMode(RedstoneControlMode outputControlMode) {
    if(this.outputControlMode == outputControlMode) {
      return;
    }
    this.outputControlMode = outputControlMode;
    for (TileCapBank capBank : capBanks) {
      capBank.setOutputControlMode(outputControlMode);
    }
    updateRedstoneConditions();
  }

  public void updateRedstoneSignal(TileCapBank tileCapBank, boolean recievingSignal) {
    if(recievingSignal) {
      redstoneRecievers.add(tileCapBank.getLocation());
    } else {
      redstoneRecievers.remove(tileCapBank.getLocation());
    }
    updateRedstoneConditions();
  }

  private void updateRedstoneConditions() {
    int powerLevel = redstoneRecievers.isEmpty() ? 0 : 15;
    inputRedstoneConditionMet = RedstoneControlMode.isConditionMet(inputControlMode, powerLevel);
    outputRedstoneConditionMet = RedstoneControlMode.isConditionMet(outputControlMode, powerLevel);

  }

}
