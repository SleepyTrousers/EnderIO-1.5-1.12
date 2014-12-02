package crazypants.enderio.machine.capbank.network;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import net.minecraft.world.World;
import crazypants.enderio.EnderIO;
import crazypants.enderio.machine.capbank.TileCapBank;
import crazypants.enderio.machine.capbank.packet.PacketClientStateResponse;

public class CapBankNetwork {

  protected final List<TileCapBank> capBanks = new ArrayList<TileCapBank>();

  private final int id;

  private int maxIO;

  private long timeAtLastApply;

  private long energyStored;

  private long maxEnergyStored;

  public CapBankNetwork(int id) {
    this.id = id;
  }

  public void init(TileCapBank cap, Collection<TileCapBank> neighbours, World world) {
    if(world.isRemote) {
      throw new UnsupportedOperationException();
    }

    for (TileCapBank con : neighbours) {
      CapBankNetwork network = con.getNetwork();
      if(network != null) {
        network.destroyNetwork();
      }
    }
    setNetwork(world, cap);
    notifyNetworkOfUpdate();

    EnderIO.packetPipeline.sendToAllAround(new PacketClientStateResponse(this), cap);

  }

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
      EnderIO.packetPipeline.INSTANCE.sendToAll(new PacketClientStateResponse(this, true));
    }
  }

  public void addCapBank(TileCapBank cap) {
    if(!capBanks.contains(cap)) {
      capBanks.add(cap);
      maxIO += cap.getType().getMaxIO();
      energyStored += cap.getEnergyStored();
      maxEnergyStored += cap.getMaxEnergyStored();
    }
  }

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

  public void notifyNetworkOfUpdate() {
    for (TileCapBank cb : capBanks) {
      cb.getWorldObj().markBlockForUpdate(cb.xCoord, cb.yCoord, cb.zCoord);
    }
  }

  private void doNetworkTick() {


    distributeEnergy();
  }

  public int recieveEnergy(int maxReceive, boolean simulate) {
    if(maxReceive <= 0) {
      return 0;
    }

    long spaceAvailable = maxEnergyStored - energyStored;
    if(spaceAvailable > Integer.MAX_VALUE) {
      spaceAvailable = Integer.MAX_VALUE;
    }
    int res = Math.min(maxReceive, (int) spaceAvailable);
    if(!simulate) {
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

  public int getId() {
    return id;
  }

  public NetworkClientState getClientState() {
    return new NetworkClientState(this);
  }

  public int getMaxEnergyRecieved() {
    //TODO
    return maxIO;
  }

  public int getMaxEnergySent() {
    //TODO
    return maxIO;
  }

  public long getEnergyStored() {
    return energyStored;
  }

  public long getMaxEnergyStored() {
    return maxEnergyStored;
  }

  public int getMaxIO() {
    return maxIO;
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

}
