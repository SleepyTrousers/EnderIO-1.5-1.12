package crazypants.enderio.conduit.power;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;

import net.minecraft.world.World;
import buildcraft.api.power.PowerHandler.PowerReceiver;
import buildcraft.api.power.PowerHandler.Type;
import crazypants.enderio.Config;
import crazypants.enderio.conduit.power.PowerConduitNetwork.ReceptorEntry;
import crazypants.enderio.machine.power.TileCapacitorBank;
import crazypants.enderio.power.IPowerInterface;
import crazypants.enderio.power.PowerInterfaceRF;
import crazypants.util.BlockCoord;

public class NetworkPowerManager {

  private PowerConduitNetwork network;

  int maxEnergyStored;
  float energyStored;
  private float reserved;

  private int updateRenderTicks = 10;
  private int inactiveTicks = 100;

  private final List<ReceptorEntry> receptors = new ArrayList<PowerConduitNetwork.ReceptorEntry>();
  private ListIterator<ReceptorEntry> receptorIterator = receptors.listIterator();

  private final List<ReceptorEntry> storageReceptors = new ArrayList<ReceptorEntry>();

  private boolean lastActiveValue = false;
  private int ticksWithNoPower = 0;

  private final Map<BlockCoord, StarveBuffer> starveBuffers = new HashMap<BlockCoord, NetworkPowerManager.StarveBuffer>();

  private final Map<IPowerConduit, PowerTracker> powerTrackers = new HashMap<IPowerConduit, PowerTracker>();

  private PowerTracker networkPowerTracker = new PowerTracker();

  private final CapBankSupply capSupply = new CapBankSupply();

  public NetworkPowerManager(PowerConduitNetwork netowrk, World world) {
    this.network = netowrk;
    maxEnergyStored = 64;
  }

  public PowerTracker getTracker(IPowerConduit conduit) {
    return powerTrackers.get(conduit);
  }

  public PowerTracker getNetworkPowerTracker() {
    return networkPowerTracker;
  }

  public float getPowerInConduits() {
    return energyStored;
  }

  public float getMaxPowerInConduits() {
    return maxEnergyStored;
  }

  public float getPowerInCapacitorBanks() {
    if(capSupply == null) {
      return 0;
    }
    return capSupply.stored;
  }

  public float getMaxPowerInCapacitorBanks() {
    if(capSupply == null) {
      return 0;
    }
    return capSupply.maxCap;
  }

  public float getPowerInReceptors() {
    float result = 0;
    Set<IPowerInterface> done = new HashSet<IPowerInterface>();
    for (ReceptorEntry re : receptors) {
      IPowerInterface powerReceptor = re.powerInterface;
      if(!done.contains(powerReceptor)) {
        done.add(powerReceptor);
        result += powerReceptor.getEnergyStored(re.direction);
      }
    }
    return result;
  }

  public float getMaxPowerInReceptors() {
    float result = 0;
    Set<IPowerInterface> done = new HashSet<IPowerInterface>();
    for (ReceptorEntry re : receptors) {
      IPowerInterface powerReceptor = re.powerInterface;
      if(!done.contains(powerReceptor)) {
        done.add(powerReceptor);
        result += powerReceptor.getMaxEnergyStored(re.direction);
      }
    }
    return result;
  }

  public void applyRecievedPower() {

    trackerStartTick();

    // Update our energy stored based on what's in our conduits
    updateNetorkStorage();
    networkPowerTracker.tickStart(energyStored);

    checkReserves();
    updateActiveState();

    capSupply.init();

    int appliedCount = 0;
    int numReceptors = receptors.size();
    float available = energyStored + capSupply.canExtract;
    float wasAvailable = available;

    if(available <= 0 || (receptors.isEmpty() && storageReceptors.isEmpty())) {
      trackerEndTick();
      return;
    }

    while (available > 0 && appliedCount < numReceptors) {

      if(!receptors.isEmpty() && !receptorIterator.hasNext()) {
        receptorIterator = receptors.listIterator();
      }

      ReceptorEntry r = receptorIterator.next();
      if(r.emmiter.getPowerHandler().isPowerSource(r.direction)) {

        // do a summy recieve or recieve energy counter will never tick down
        float es = r.emmiter.getPowerHandler().getEnergyStored();
        PowerReceiver pr = r.emmiter.getPowerReceiver(r.direction.getOpposite());
        pr.receiveEnergy(Type.STORAGE, 0, null);
        r.emmiter.getPowerHandler().setEnergy(es);

      } else {

        IPowerInterface pp = r.powerInterface;

        if(pp != null) {

          float used = 0;

          if(pp.getClass() == PowerInterfaceRF.class) {

            used = pp.recieveEnergy(r.direction.getOpposite(), available);
            trackerSend(r.emmiter, used, false);

          } else {

            float reservedForEntry = removeReservedEnergy(r);
            available += reservedForEntry;
            float canOffer = Math.min(r.emmiter.getMaxEnergyExtracted(r.direction), available);
            float requested = pp.getPowerRequest(r.direction.getOpposite());

            // If it is possible to supply the minimum amount of energy
            if(pp.getMinEnergyReceived(r.direction) <= r.emmiter.getMaxEnergyExtracted(r.direction) && requested > 0) {
              // Buffer energy if we can't meet it now
              if(pp.getMinEnergyReceived(r.direction) > canOffer) {
                reserveEnergy(r, canOffer);
                used += canOffer;
              } else {
                used = pp.recieveEnergy(r.direction.getOpposite(), canOffer);
                trackerSend(r.emmiter, used, false);
              }
            }
          }
          available -= used;
          if(available <= 0) {
            break;
          }
        }

      }
      appliedCount++;
    }

    float used = wasAvailable - available;
    // use all the capacator storage first
    energyStored -= used;

    if(!capSupply.capBanks.isEmpty()) {
      float capBankChange = 0;
      if(energyStored < 0) {
        // not enough so get the rest from the capacitor bank
        capBankChange = energyStored;
        energyStored = 0;
      } else if(energyStored > 0) {
        // push as much as we can back to the cap banks
        capBankChange = Math.min(energyStored, capSupply.canFill);
        energyStored -= capBankChange;
      }

      if(capBankChange < 0) {
        capSupply.remove(Math.abs(capBankChange));
      } else if(capBankChange > 0) {
        capSupply.add(capBankChange);
      }

      capSupply.balance();
    }

    distributeStorageToConduits();

    trackerEndTick();

    networkPowerTracker.tickEnd(energyStored);
  }

  private void trackerStartTick() {

    if(!Config.detailedPowerTrackingEnabled) {
      return;
    }
    for (IPowerConduit con : network.getConduits()) {
      if(con.hasExternalConnections()) {
        PowerTracker tracker = getOrCreateTracker(con);
        tracker.tickStart(con.getPowerHandler().getEnergyStored());
      }
    }
  }

  private void trackerSend(IPowerConduit con, float sent, boolean fromBank) {
    if(!fromBank) {
      networkPowerTracker.powerSent(sent);
    }
    if(!Config.detailedPowerTrackingEnabled) {
      return;
    }
    getOrCreateTracker(con).powerSent(sent);
  }

  private void trackerRecieve(IPowerConduit con, float recieved, boolean fromBank) {
    if(!fromBank) {
      networkPowerTracker.powerRecieved(recieved);
    }
    if(!Config.detailedPowerTrackingEnabled) {
      return;
    }
    getOrCreateTracker(con).powerRecieved(recieved);
  }

  private void trackerEndTick() {
    if(!Config.detailedPowerTrackingEnabled) {
      return;
    }
    for (IPowerConduit con : network.getConduits()) {
      if(con.hasExternalConnections()) {
        PowerTracker tracker = getOrCreateTracker(con);
        tracker.tickEnd(con.getPowerHandler().getEnergyStored());
      }
    }
  }

  private PowerTracker getOrCreateTracker(IPowerConduit con) {
    PowerTracker result = powerTrackers.get(con);
    if(result == null) {
      result = new PowerTracker();
      powerTrackers.put(con, result);
    }
    return result;
  }

  private void updateActiveState() {
    boolean active;
    if(energyStored > 0) {
      ticksWithNoPower = 0;
      active = true;
    } else {
      ticksWithNoPower++;
      active = false;
    }

    boolean doRender = active != lastActiveValue && (active || (!active && ticksWithNoPower > updateRenderTicks));
    if(doRender) {
      lastActiveValue = active;
      //for (IPowerConduit con : network.getConduits()) {
      //con.setActive(active);
      //}
    }
  }

  private float removeReservedEnergy(ReceptorEntry r) {
    StarveBuffer starveBuf = starveBuffers.remove(r.coord);
    if(starveBuf == null) {
      return 0;
    }
    float result = starveBuf.stored;
    reserved -= result;
    return result;
  }

  private void reserveEnergy(ReceptorEntry r, float amount) {
    starveBuffers.put(r.coord, new StarveBuffer(amount));
    reserved += amount;
  }

  private void checkReserves() {
    if(reserved > maxEnergyStored * 0.9) {
      starveBuffers.clear();
      reserved = 0;
    }
  }

  private void distributeStorageToConduits() {
    if(maxEnergyStored <= 0 || energyStored <= 0) {
      for (IPowerConduit con : network.getConduits()) {
        con.getPowerHandler().setEnergy(0);
      }
      return;
    }
    if(energyStored > maxEnergyStored) {
      energyStored = maxEnergyStored;
    }

    float filledRatio = energyStored / maxEnergyStored;
    float energyLeft = energyStored;
    float given = 0;
    for (IPowerConduit con : network.getConduits()) {
      if(energyLeft >= 0) {
        // NB: use ceil to ensure we dont through away any energy due to
        // rounding
        // errors
        float give = (float) Math.ceil(con.getCapacitor().getMaxEnergyStored() * filledRatio);
        give = Math.min(give, con.getCapacitor().getMaxEnergyStored());
        give = Math.min(give, energyLeft);
        con.getPowerHandler().setEnergy(give);
        given += give;
        energyLeft -= give;
      } else {
        con.getPowerHandler().setEnergy(0);
      }
    }
  }

  boolean isActive() {
    return energyStored > 0;
  }

  private void updateNetorkStorage() {
    maxEnergyStored = 0;
    energyStored = 0;
    for (IPowerConduit con : network.getConduits()) {
      maxEnergyStored += con.getCapacitor().getMaxEnergyStored();
      energyStored += con.getPowerHandler().getEnergyStored();
    }

    if(energyStored > maxEnergyStored) {
      energyStored = maxEnergyStored;
    }

  }

  public void receptorsChanged() {
    receptors.clear();
    storageReceptors.clear();
    for (ReceptorEntry rec : network.getPowerReceptors()) {
      if(rec.powerInterface.getDelegate() instanceof TileCapacitorBank) {
        storageReceptors.add(rec);
      } else {
        receptors.add(rec);
      }
    }
    receptorIterator = receptors.listIterator();
  }

  void onNetworkDestroyed() {
  }

  private static class StarveBuffer {

    float stored;

    public StarveBuffer(float stored) {
      this.stored = stored;
    }

    void addToStore(float val) {
      stored += val;
    }

  }

  private float minAbs(float amount, float limit) {
    if(amount < 0) {
      return Math.max(amount, -limit);
    } else {
      return Math.min(amount, limit);
    }
  }

  private class CapBankSupply {

    float canExtract;
    float canFill;
    Set<TileCapacitorBank> capBanks = new HashSet<TileCapacitorBank>();

    float filledRatio;
    float stored = 0;
    float maxCap = 0;

    List<CapBankSupplyEntry> enteries = new ArrayList<NetworkPowerManager.CapBankSupplyEntry>();

    CapBankSupply() {
    }

    void init() {
      capBanks.clear();
      enteries.clear();
      canExtract = 0;
      canFill = 0;
      stored = 0;
      maxCap = 0;
      for (ReceptorEntry rec : storageReceptors) {
        TileCapacitorBank cb = (TileCapacitorBank) rec.powerInterface.getDelegate();

        boolean processed = capBanks.contains(cb);

        if(!processed) {
          stored += cb.getEnergyStored();
          maxCap += cb.getMaxEnergyStored();
          capBanks.add(cb);

          float canGet = 0;

          if(cb.isOutputEnabled(rec.direction.getOpposite())) {
            canGet = Math.min(cb.getEnergyStored(), cb.getMaxOutput());
            canGet = Math.min(canGet, rec.emmiter.getMaxEnergyRecieved(rec.direction));
            canExtract += canGet;
          }
          float canFill = 0;
          if(cb.isInputEnabled(rec.direction.getOpposite())) {
            canFill = Math.min(cb.getMaxEnergyStored() - cb.getEnergyStored(), cb.getMaxInput());
            canFill = Math.min(canFill, rec.emmiter.getMaxEnergyExtracted(rec.direction));
            this.canFill += canFill;
          }
          enteries.add(new CapBankSupplyEntry(cb, canGet, canFill, rec.emmiter));
        }

      }

      filledRatio = 0;
      if(maxCap > 0) {
        filledRatio = stored / maxCap;
      }
    }

    void balance() {
      if(enteries.size() < 2) {
        return;
      }
      init();
      int canRemove = 0;
      int canAdd = 0;
      for (CapBankSupplyEntry entry : enteries) {
        entry.calcToBalance(filledRatio);
        if(entry.toBalance < 0) {
          canRemove += -entry.toBalance;
        } else {
          canAdd += entry.toBalance;
        }
      }

      float toalTransferAmount = Math.min(canAdd, canRemove);

      for (int i = 0; i < enteries.size() && toalTransferAmount > 0; i++) {
        CapBankSupplyEntry from = enteries.get(i);
        float amount = from.toBalance;
        amount = minAbs(amount, toalTransferAmount);
        from.capBank.addEnergy(amount);
        toalTransferAmount -= Math.abs(amount);
        float toTranfser = Math.abs(amount);

        for (int j = i + 1; j < enteries.size() && toTranfser > 0; j++) {
          CapBankSupplyEntry to = enteries.get(j);
          if(Math.signum(amount) != Math.signum(to.toBalance)) {
            float toAmount = Math.min(toTranfser, Math.abs(to.toBalance));
            to.capBank.addEnergy(toAmount * Math.signum(to.toBalance));
            toTranfser -= toAmount;
          }
        }

      }

    }

    void remove(float amount) {
      if(canExtract <= 0 || amount <= 0) {
        return;
      }
      float ratio = amount / canExtract;

      for (CapBankSupplyEntry entry : enteries) {
        double use = Math.ceil(ratio * entry.canExtract);
        use = Math.min(use, amount);
        use = Math.min(use, entry.canExtract);
        entry.capBank.addEnergy(-(float) use);
        trackerRecieve(entry.emmiter, (float) use, true);
        amount -= use;
        if(amount == 0) {
          return;
        }
      }
    }

    void add(float amount) {
      if(canFill <= 0 || amount <= 0) {
        return;
      }
      float ratio = amount / canFill;

      for (CapBankSupplyEntry entry : enteries) {
        double add = (int) Math.ceil(ratio * entry.canFill);
        add = Math.min(add, entry.canFill);
        add = Math.min(add, amount);
        entry.capBank.addEnergy((float) add);
        trackerSend(entry.emmiter, (float) add, true);
        amount -= add;
        if(amount == 0) {
          return;
        }
      }
    }

  }

  private static class CapBankSupplyEntry {

    final TileCapacitorBank capBank;
    final float canExtract;
    final float canFill;
    float toBalance;
    IPowerConduit emmiter;

    private CapBankSupplyEntry(TileCapacitorBank capBank, float available, float canFill, IPowerConduit emmiter) {
      this.capBank = capBank;
      this.canExtract = available;
      this.canFill = canFill;
    }

    void calcToBalance(float targetRatio) {
      float targetAmount = capBank.getMaxEnergyStored() * targetRatio;
      toBalance = targetAmount - capBank.getEnergyStored();
      if(toBalance < 0) {
        toBalance = Math.max(toBalance, -canExtract);
      } else {
        toBalance = Math.min(toBalance, canFill);
      }

    }

  }

}
