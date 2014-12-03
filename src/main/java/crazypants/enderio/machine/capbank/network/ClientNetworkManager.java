package crazypants.enderio.machine.capbank.network;

import java.util.HashMap;
import java.util.Map;

import crazypants.enderio.machine.capbank.TileCapBank;

public class ClientNetworkManager {

  private static final ClientNetworkManager instance = new ClientNetworkManager();

  public static ClientNetworkManager getInstance() {
    return instance;
  }

  private Map<Integer, CapBankClientNetwork> networks = new HashMap<Integer, CapBankClientNetwork>();

  ClientNetworkManager() {
  }

  public void destroyNetwork(int id) {
    CapBankClientNetwork res = networks.get(id);
    if(res != null) {
      res.destroyNetwork();
    }
  }

  public void updateState(int id, NetworkState state) {
    CapBankClientNetwork network = getOrCreateNetwork(id);
    network.setState(state);
  }

  public void updateEnergyStored(int id, long energyStored) {
    CapBankClientNetwork res = networks.get(id);
    if(res == null) {
      return;
    }
    res.setEnergyStored(energyStored);
  }

  public CapBankClientNetwork getOrCreateNetwork(int id) {
    CapBankClientNetwork res = networks.get(id);
    if(res == null) {
      res = new CapBankClientNetwork(id);
      networks.put(id, res);
    }
    return res;
  }

  public void addToNetwork(int id, TileCapBank tileCapBank) {
    CapBankClientNetwork network = getOrCreateNetwork(id);
    network.addMember(tileCapBank);
  }

  public CapBankClientNetwork getNetwork(TileCapBank tr) {
    if(tr == null) {
      return null;
    }
    return networks.get(tr.getNetworkId());
  }

}
