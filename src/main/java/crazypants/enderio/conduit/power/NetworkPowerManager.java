package crazypants.enderio.conduit.power;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;

import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import cpw.mods.fml.common.gameevent.TickEvent.ServerTickEvent;
import crazypants.enderio.Log;
import crazypants.enderio.conduit.ConduitNetworkTickHandler;
import crazypants.enderio.conduit.ConduitNetworkTickHandler.TickListener;
import crazypants.enderio.conduit.ConnectionMode;
import crazypants.enderio.conduit.power.PowerConduitNetwork.ReceptorEntry;
import crazypants.enderio.config.Config;
import crazypants.enderio.machine.power.TileCapacitorBank;
import crazypants.enderio.power.IPowerInterface;
import crazypants.enderio.power.PowerInterfaceRF;
import crazypants.util.BlockCoord;

public class NetworkPowerManager {

  private PowerConduitNetwork network;

  int maxEnergyStored;
  int energyStored;
  private int reserved;

  private int updateRenderTicks = 10;
  private int inactiveTicks = 100;

  private final List<ReceptorEntry> receptors = new ArrayList<PowerConduitNetwork.ReceptorEntry>();
  private ListIterator<ReceptorEntry> receptorIterator = receptors.listIterator();

  private final List<ReceptorEntry> storageReceptors = new ArrayList<ReceptorEntry>();

  private boolean receptorsDirty = true;

  private boolean lastActiveValue = false;
  private int ticksWithNoPower = 0;

  private final Map<IPowerConduit, PowerTracker> powerTrackers = new HashMap<IPowerConduit, PowerTracker>();

  private PowerTracker networkPowerTracker = new PowerTracker();

  private final CapBankSupply capSupply = new CapBankSupply();

  private InnerTickHandler applyPowerCallback = new InnerTickHandler();

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

  public int getPowerInConduits() {
    return energyStored;
  }

  public int getMaxPowerInConduits() {
    return maxEnergyStored;
  }

  public int getPowerInCapacitorBanks() {
    if(capSupply == null) {
      return 0;
    }
    return capSupply.stored;
  }

  public int getMaxPowerInCapacitorBanks() {
    if(capSupply == null) {
      return 0;
    }
    return capSupply.maxCap;
  }

  public int getPowerInReceptors() {
    int result = 0;
    Set<Object> done = new HashSet<Object>();
    for (ReceptorEntry re : receptors) {
      if(!re.emmiter.getConnectionsDirty()) {
        IPowerInterface powerReceptor = re.powerInterface;
        if(!done.contains(powerReceptor.getDelegate())) {
          done.add(powerReceptor.getDelegate());
          result += powerReceptor.getEnergyStored(re.direction);
        }
      }
    }
    return result;
  }

  public int getMaxPowerInReceptors() {
    int result = 0;
    Set<Object> done = new HashSet<Object>();
    for (ReceptorEntry re : receptors) {
      if(!re.emmiter.getConnectionsDirty()) {
        IPowerInterface powerReceptor = re.powerInterface;
        if(!done.contains(powerReceptor.getDelegate())) {
          done.add(powerReceptor.getDelegate());
          result += powerReceptor.getMaxEnergyStored(re.direction);
        }
      }
    }
    return result;
  }

  public void applyRecievedPower() {
    //want to do this after all conduits have updated so all connections have been checked etc
    ConduitNetworkTickHandler.instance.addListener(applyPowerCallback);
  }

  public void doApplyRecievedPower() {

    trackerStartTick();

    checkReceptors();

    // Update our energy stored based on what's in our conduits
    updateNetorkStorage();
    networkPowerTracker.tickStart(energyStored);

    updateActiveState();

    capSupply.init();

    int appliedCount = 0;
    int numReceptors = receptors.size();
    int available = energyStored + capSupply.canExtract;
    int wasAvailable = available;

    if(available <= 0 || (receptors.isEmpty() && storageReceptors.isEmpty())) {
      trackerEndTick();
      networkPowerTracker.tickEnd(energyStored);
      return;
    }

    while (available > 0 && appliedCount < numReceptors) {

      if(!receptors.isEmpty() && !receptorIterator.hasNext()) {
        receptorIterator = receptors.listIterator();
      }
      ReceptorEntry r = receptorIterator.next();
      IPowerInterface pp = r.powerInterface;
      if(pp != null) {        
        int canOffer = Math.min(r.emmiter.getMaxEnergyExtracted(r.direction), available);
        int used = pp.recieveEnergy(r.direction.getOpposite(), canOffer);
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
        tracker.tickStart(con.getEnergyStored());
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
        tracker.tickEnd(con.getEnergyStored());
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
    int given = 0;
    for (IPowerConduit con : network.getConduits()) {
      if(energyLeft > 0) {
        // NB: use ceil to ensure we dont through away any energy due to
        // rounding
        // errors
        int give = (int) Math.ceil(con.getMaxEnergyStored() * filledRatio);
        give = Math.min(give, con.getMaxEnergyStored());
        give = Math.min(give, energyLeft);
        con.setEnergyStored(give);
        given += give;
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
      maxEnergyStored += con.getMaxEnergyStored();
      con.onTick();
      energyStored += con.getEnergyStored();
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
      if(rec.powerInterface.getDelegate() != null &&
          rec.powerInterface.getDelegate().getClass() == TileCapacitorBank.class &&
          !((TileCapacitorBank) rec.powerInterface.getDelegate()).isCreative()) {
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

  private static class StarveBuffer {

    int stored;

    public StarveBuffer(int stored) {
      this.stored = stored;
    }

    void addToStore(float val) {
      stored += val;
    }

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
    Set<TileCapacitorBank> capBanks = new HashSet<TileCapacitorBank>();

    float filledRatio;
    int stored = 0;
    int maxCap = 0;

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

      float toBalance = 0;
      float maxToBalance = 0;

      for (ReceptorEntry rec : storageReceptors) {
        TileCapacitorBank cb = (TileCapacitorBank) rec.powerInterface.getDelegate();

        boolean processed = capBanks.contains(cb.getController());

        if(!processed) {
          stored += cb.getEnergyStored();
          maxCap += cb.getMaxEnergyStored();
          capBanks.add(cb.getController());
        }

        if(rec.emmiter.getConnectionMode(rec.direction) == ConnectionMode.IN_OUT) {
          toBalance += cb.getEnergyStored();
          maxToBalance += cb.getMaxEnergyStored();
        }

        int canGet = 0;
        if(cb.isOutputEnabled(rec.direction.getOpposite())) {
          canGet = Math.min(cb.getEnergyStored(), cb.getMaxOutput());
          canGet = Math.min(canGet, rec.emmiter.getMaxEnergyRecieved(rec.direction));
          canExtract += canGet;
        }
        int canFill = 0;
        if(cb.isInputEnabled(rec.direction.getOpposite())) {
          canFill = Math.min(cb.getMaxEnergyStored() - cb.getEnergyStored(), cb.getMaxInput());
          canFill = Math.min(canFill, rec.emmiter.getMaxEnergyExtracted(rec.direction));
          this.canFill += canFill;
        }
        enteries.add(new CapBankSupplyEntry(cb, canGet, canFill, rec.emmiter, rec.direction));

      }

      filledRatio = 0;
      if(maxToBalance > 0) {
        filledRatio = (float) toBalance / maxToBalance;
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
        int use = (int) Math.ceil(ratio * entry.canExtract);
        use = Math.min(use, amount);
        use = Math.min(use, entry.canExtract);
        entry.capBank.addEnergy(-use);
        trackerRecieve(entry.emmiter, use, true);
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
        int add = (int) Math.ceil(ratio * entry.canFill);
        add = Math.min(add, entry.canFill);
        add = Math.min(add, amount);
        entry.capBank.addEnergy(add);
        trackerSend(entry.emmiter, add, true);
        amount -= add;
        if(amount == 0) {
          return;
        }
      }
    }

  }

  private static class CapBankSupplyEntry {

    final TileCapacitorBank capBank;
    final int canExtract;
    final int canFill;
    int toBalance;
    IPowerConduit emmiter;
    ForgeDirection direction;

    private CapBankSupplyEntry(TileCapacitorBank capBank, int available, int canFill, IPowerConduit emmiter, ForgeDirection direction) {
      this.capBank = capBank;
      this.canExtract = available;
      this.canFill = canFill;
      this.emmiter = emmiter;
      this.direction = direction;
    }

    void calcToBalance(float targetRatio) {
      int targetAmount = (int) Math.floor(capBank.getMaxEnergyStored() * targetRatio);
      toBalance = targetAmount - capBank.getEnergyStored();
      if(toBalance < 0) {
        toBalance = Math.max(toBalance, -canExtract);
      } else {
        toBalance = Math.min(toBalance, canFill);
      }

    }

  }

  private class InnerTickHandler implements TickListener {

    @Override
    public void tickStart(ServerTickEvent evt) {
    }

    @Override
    public void tickEnd(ServerTickEvent evt) {
      try {
        doApplyRecievedPower();
      } catch (Exception e) {
        Log.warn("NetworkPowerManager: Exception thrown when updating power network " + e);
      }
    }
  }

}
