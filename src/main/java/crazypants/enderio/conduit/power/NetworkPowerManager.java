package crazypants.enderio.conduit.power;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;

import crazypants.enderio.Log;
import crazypants.enderio.conduit.ConnectionMode;
import crazypants.enderio.conduit.power.PowerConduitNetwork.ReceptorEntry;
import crazypants.enderio.config.Config;
import crazypants.enderio.power.IPowerInterface;
import crazypants.enderio.power.IPowerStorage;
import net.minecraft.profiler.Profiler;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class NetworkPowerManager {

  private final PowerConduitNetwork network;

  int maxEnergyStored;
  int energyStored;

  private final List<ReceptorEntry> receptors = new ArrayList<PowerConduitNetwork.ReceptorEntry>();
  private ListIterator<ReceptorEntry> receptorIterator = receptors.listIterator();

  private final List<ReceptorEntry> storageReceptors = new ArrayList<ReceptorEntry>();

  private boolean receptorsDirty = true;

  private final Map<IPowerConduit, PowerTracker> powerTrackers = new HashMap<IPowerConduit, PowerTracker>();

  private final PowerTracker networkPowerTracker = new PowerTracker();

  private final CapBankSupply capSupply = new CapBankSupply();

  public NetworkPowerManager(PowerConduitNetwork netowrk, World world) {
    network = netowrk;
    maxEnergyStored = 64;
  }

  public PowerTracker getTracker(IPowerConduit conduit) {
    return powerTrackers.get(conduit);
  }

  public PowerTracker getNetworkPowerTracker() {
    return networkPowerTracker;
  }

  public int getPowerInConduits() {
    return energyStored;
  }

  public int getMaxPowerInConduits() {
    return maxEnergyStored;
  }

  public long getPowerInCapacitorBanks() {
    if(capSupply == null) {
      return 0;
    }
    return capSupply.stored;
  }

  public long getMaxPowerInCapacitorBanks() {
    if(capSupply == null) {
      return 0;
    }
    return capSupply.maxCap;
  }

  public long getPowerInReceptors() {
    long result = 0;
    Set<Object> done = new HashSet<Object>();
    for (ReceptorEntry re : receptors) {
      if(!re.emmiter.getConnectionsDirty()) {
        IPowerInterface powerReceptor = re.powerInterface;
        if(!done.contains(powerReceptor.getProvider())) {
          done.add(powerReceptor.getProvider());
          result += powerReceptor.getEnergyStored();
        }
      }
    }
    return result;
  }

  public long getMaxPowerInReceptors() {
    long result = 0;
    Set<Object> done = new HashSet<Object>();
    for (ReceptorEntry re : receptors) {
      if(!re.emmiter.getConnectionsDirty()) {
        IPowerInterface powerReceptor = re.powerInterface;
        if(!done.contains(powerReceptor.getProvider())) {
          done.add(powerReceptor.getProvider());
          result += powerReceptor.getMaxEnergyStored();
        }
      }
    }
    return result;
  }

  private int errorSupressionA = 0;
  private int errorSupressionB = 0;

  public void applyRecievedPower(Profiler theProfiler) {
    try {
      doApplyRecievedPower(theProfiler);
    } catch (Exception e) {
      if (errorSupressionA-- <= 0) {
        Log.warn("NetworkPowerManager: Exception thrown when updating power network " + e);
        e.printStackTrace();
        errorSupressionA = 200;
        errorSupressionB = 20;
      } else if (errorSupressionB-- <= 0) {
        Log.warn("NetworkPowerManager: Exception thrown when updating power network " + e);
        errorSupressionB = 20;
      }
    }
  }

  public void doApplyRecievedPower(Profiler theProfiler) {

    trackerStartTick();

    theProfiler.startSection("checkReceptors");
    checkReceptors();

    // Update our energy stored based on what's in our conduits
    theProfiler.endStartSection("updateNetorkStorage");
    updateNetorkStorage();
    networkPowerTracker.tickStart(energyStored);

    theProfiler.endStartSection("capSupplyInit");
    capSupply.init();

    int appliedCount = 0;
    int numReceptors = receptors.size();
    int available = energyStored + capSupply.canExtract;
    int wasAvailable = available;

    if(available <= 0 || (receptors.isEmpty() && storageReceptors.isEmpty())) {
      trackerEndTick();
      networkPowerTracker.tickEnd(energyStored);
      theProfiler.endSection();
      return;
    }

    theProfiler.endStartSection("sendEnergy");
    while (available > 0 && appliedCount < numReceptors) {

      if(!receptors.isEmpty() && !receptorIterator.hasNext()) {
        receptorIterator = receptors.listIterator();
      }
      ReceptorEntry r = receptorIterator.next();
      IPowerInterface pp = r.powerInterface;
      if(pp != null) {
        int canOffer = Math.min(r.emmiter.getMaxEnergyExtracted(r.direction), available);
        theProfiler.startSection("otherMod_receiveEnergy");
        int used = pp.receiveEnergy(canOffer, false);
        theProfiler.endSection();
        used = Math.max(0, used);
        trackerSend(r.emmiter, used, false);
        available -= used;
        if(available <= 0) {
          break;
        }
      }
      appliedCount++;
    }

    int used = wasAvailable - available;
    // use all the capacator storage first
    energyStored -= used;

    theProfiler.endStartSection("capBankUpdate");
    if(!capSupply.capBanks.isEmpty()) {
      int capBankChange = 0;
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

    theProfiler.endStartSection("conduitUpdate");
    distributeStorageToConduits();

    trackerEndTick();

    networkPowerTracker.tickEnd(energyStored);
    theProfiler.endSection();
  }

  private void trackerStartTick() {

    if(!Config.detailedPowerTrackingEnabled) {
      return;
    }
    for (IPowerConduit con : network.getConduits()) {
      if(con.hasExternalConnections()) {
        PowerTracker tracker = getOrCreateTracker(con);
        tracker.tickStart(con.getEnergyStored(null));
      }
    }
  }

  private void trackerSend(IPowerConduit con, int sent, boolean fromBank) {
    if(!fromBank) {
      networkPowerTracker.powerSent(sent);
    }
    if(!Config.detailedPowerTrackingEnabled) {
      return;
    }
    getOrCreateTracker(con).powerSent(sent);
  }

  private void trackerRecieve(IPowerConduit con, int recieved, boolean fromBank) {
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
        tracker.tickEnd(con.getEnergyStored(null));
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

  private void distributeStorageToConduits() {
    if(maxEnergyStored <= 0 || energyStored <= 0) {
      for (IPowerConduit con : network.getConduits()) {
        con.setEnergyStored(0);
      }
      return;
    }
    energyStored = MathHelper.clamp_int(energyStored, 0, maxEnergyStored);

    float filledRatio = (float) energyStored / maxEnergyStored;
    int energyLeft = energyStored;
    
    for (IPowerConduit con : network.getConduits()) {
      if(energyLeft > 0) {
        // NB: use ceil to ensure we dont through away any energy due to
        // rounding
        // errors
        int give = (int) Math.ceil(con.getMaxEnergyStored(null) * filledRatio);
        give = Math.min(give, con.getMaxEnergyStored(null));
        give = Math.min(give, energyLeft);
        con.setEnergyStored(give);
        energyLeft -= give;
      } else {
        con.setEnergyStored(0);
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
      maxEnergyStored += con.getMaxEnergyStored(null);
      con.onTick();
      energyStored += con.getEnergyStored(null);
    }
    energyStored = MathHelper.clamp_int(energyStored, 0, maxEnergyStored);
  }

  public void receptorsChanged() {
    receptorsDirty = true;
  }

  private void checkReceptors() {
    if(!receptorsDirty) {
      return;
    }
    receptors.clear();
    storageReceptors.clear();
    for (ReceptorEntry rec : network.getPowerReceptors()) {
      if(rec.powerInterface.getProvider() != null &&
          rec.powerInterface.getProvider() instanceof IPowerStorage) {
        storageReceptors.add(rec);
      } else {
        receptors.add(rec);
      }
    }
    receptorIterator = receptors.listIterator();

    receptorsDirty = false;
  }

  void onNetworkDestroyed() {
  }

  private int minAbs(int amount, int limit) {
    if(amount < 0) {
      return Math.max(amount, -limit);
    } else {
      return Math.min(amount, limit);
    }
  }

  private class CapBankSupply {

    int canExtract;
    int canFill;
    Set<IPowerStorage> capBanks = new HashSet<IPowerStorage>();

    double filledRatio;
    long stored = 0;
    long maxCap = 0;

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

      double toBalance = 0;
      double maxToBalance = 0;

      for (ReceptorEntry rec : storageReceptors) {
        IPowerStorage cb = (IPowerStorage) rec.powerInterface.getProvider();

        boolean processed = capBanks.contains(cb.getController());

        if(!processed) {
          stored += cb.getEnergyStoredL();
          maxCap += cb.getMaxEnergyStoredL();
          capBanks.add(cb.getController());
        }

        if(rec.emmiter.getConnectionMode(rec.direction) == ConnectionMode.IN_OUT) {
          toBalance += cb.getEnergyStoredL();
          maxToBalance += cb.getMaxEnergyStoredL();
        }

        long canGet = 0;
        long canFill = 0;
        if(cb.isNetworkControlledIo(rec.direction.getOpposite())) {
          if(cb.isOutputEnabled(rec.direction.getOpposite())) {
            canGet = Math.min(cb.getEnergyStoredL(), cb.getMaxOutput());
            canGet = Math.min(canGet, rec.emmiter.getMaxEnergyRecieved(rec.direction));
            canExtract += canGet;
          }

          if(cb.isInputEnabled(rec.direction.getOpposite())) {
            canFill = Math.min(cb.getMaxEnergyStoredL() - cb.getEnergyStoredL(), cb.getMaxInput());
            canFill = Math.min(canFill, rec.emmiter.getMaxEnergyExtracted(rec.direction));
            this.canFill += canFill;
          }
          enteries.add(new CapBankSupplyEntry(cb, (int) canGet, (int) canFill, rec.emmiter, rec.direction));
        }

      }

      filledRatio = 0;
      if(maxToBalance > 0) {
        filledRatio = toBalance / maxToBalance;
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
        if(entry.emmiter.getConnectionMode(entry.direction) == ConnectionMode.IN_OUT) {
          entry.calcToBalance(filledRatio);
          if(entry.toBalance < 0) {
            canRemove += -entry.toBalance;
          } else {
            canAdd += entry.toBalance;
          }
        }
      }

      int toalTransferAmount = Math.min(canAdd, canRemove);

      for (int i = 0; i < enteries.size() && toalTransferAmount > 0; i++) {
        CapBankSupplyEntry from = enteries.get(i);
        if(from.emmiter.getConnectionMode(from.direction) == ConnectionMode.IN_OUT) {

          int amount = from.toBalance;
          amount = minAbs(amount, toalTransferAmount);
          from.capBank.addEnergy(amount);
          toalTransferAmount -= Math.abs(amount);
          int toTranfser = Math.abs(amount);

          for (int j = i + 1; j < enteries.size() && toTranfser > 0; j++) {
            CapBankSupplyEntry to = enteries.get(j);
            if(Math.signum(amount) != Math.signum(to.toBalance)) {
              int toAmount = Math.min(toTranfser, Math.abs(to.toBalance));
              to.capBank.addEnergy(toAmount * (int) Math.signum(to.toBalance));
              toTranfser -= toAmount;
            }
          }
        }
      }

    }

    void remove(int amount) {
      if(canExtract <= 0 || amount <= 0) {
        return;
      }
      double ratio = (double) amount / canExtract;

      for (CapBankSupplyEntry entry : enteries) {
        long use = (int) Math.ceil(ratio * entry.canExtract);
        use = Math.min(use, amount);
        use = Math.min(use, entry.canExtract);
        entry.capBank.addEnergy((int) -use);
        trackerRecieve(entry.emmiter, (int) use, true);
        amount -= use;
        if(amount == 0) {
          return;
        }
      }
    }

    void add(int amount) {
      if(canFill <= 0 || amount <= 0) {
        return;
      }
      double ratio = (double) amount / canFill;

      for (CapBankSupplyEntry entry : enteries) {
        long add = (int) Math.ceil(ratio * entry.canFill);
        add = Math.min(add, entry.canFill);
        add = Math.min(add, amount);
        entry.capBank.addEnergy((int) add);
        trackerSend(entry.emmiter, (int) add, true);
        amount -= add;
        if(amount == 0) {
          return;
        }
      }
    }

  }

  private static class CapBankSupplyEntry {

    final IPowerStorage capBank;
    final int canExtract;
    final int canFill;
    int toBalance;
    IPowerConduit emmiter;
    EnumFacing direction;

    private CapBankSupplyEntry(IPowerStorage capBank, int available, int canFill, IPowerConduit emmiter, EnumFacing direction) {
      this.capBank = capBank;
      canExtract = available;
      this.canFill = canFill;
      this.emmiter = emmiter;
      this.direction = direction;
    }

    void calcToBalance(double targetRatio) {
      if(capBank.isCreative()) {
        toBalance = 0;
        return;
      }

      long targetAmount = (long) Math.floor(capBank.getMaxEnergyStoredL() * targetRatio);
      long b = targetAmount - capBank.getEnergyStoredL();
      if(b < 0) {
        toBalance = -canExtract;
      } else {
        toBalance = canFill;
      }

    }

  }

}
