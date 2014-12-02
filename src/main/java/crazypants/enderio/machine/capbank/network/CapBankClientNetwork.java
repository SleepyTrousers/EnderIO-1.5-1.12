package crazypants.enderio.machine.capbank.network;

import java.util.ArrayList;
import java.util.List;

import crazypants.enderio.machine.capbank.TileCapBank;

public class CapBankClientNetwork {

  private final int id;
  private final List<TileCapBank> members = new ArrayList<TileCapBank>();
  private NetworkClientState state;

  public CapBankClientNetwork(int id) {
    this.id = id;
  }

  public void setState(NetworkClientState state) {
    this.state = state;
  }

  public void addMember(TileCapBank capBank) {
    members.add(capBank);
  }

  public void destroy() {
    for (TileCapBank cb : members) {
      cb.setNetworkId(-1);
    }
  }

  public NetworkClientState getState() {
    return state;
  }

  public List<TileCapBank> getMembers() {
    return members;
  }

}
