package crazypants.enderio.machine.capbank;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import net.minecraft.world.World;

public class CapBankNetwork {

  protected final List<TileCapBank> capBanks = new ArrayList<TileCapBank>();

  private int ioCap;

  private long timeAtLastApply;

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
  }

  protected void setNetwork(World world, TileCapBank cap) {
    if(cap != null && cap.setNetwork(this)) {
      addCapBank(cap);
      Collection<TileCapBank> neighbours = NetworkManager.getNeigbours(cap);
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
    for (TileCapBank cb : capBanks) {
      cb.setNetwork(null);
    }
    capBanks.clear();
  }

  public void addCapBank(TileCapBank cap) {
    if(!capBanks.contains(cap)) {
      capBanks.add(cap);
      ioCap += cap.getType().getMaxIO();
    }
  }

  public int getMaxEnergyRecieved() {
    return ioCap;
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
      powerManager.applyRecievedPower();
    }
  }

  public void notifyNetworkOfUpdate() {
    for (TileCapBank cb : capBanks) {
      cb.getWorldObj().markBlockForUpdate(cb.xCoord, cb.yCoord, cb.zCoord);
    }
  }

}
