package crazypants.enderio.conduit.power;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;

import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import buildcraft.api.power.PowerHandler;
import buildcraft.api.power.PowerHandler.PowerReceiver;
import buildcraft.api.power.PowerHandler.Type;
import crazypants.enderio.conduit.power.PowerConduitNetwork.ReceptorEntry;
import crazypants.enderio.power.IInternalPowerReceptor;
import crazypants.enderio.power.PowerHandlerUtil;
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

  public NetworkPowerManager(PowerConduitNetwork netowrk, World world) {
    this.network = netowrk;
    maxEnergyStored = 64;
  }

  public void applyRecievedPower() {

    //Update our energy stored based on what's in our conduits
    updateNetorkStorage();    
    checkReserves();    
    updateActiveState();
    if (energyStored <= 0 || receptors.isEmpty()) {
      return;
    }

    int appliedCount = 0;
    int numReceptors = receptors.size();
    storageReceptors.clear();
    while (energyStored > 0 && appliedCount < numReceptors) {

      if (!receptors.isEmpty() && !receptorIterator.hasNext()) {
        receptorIterator = receptors.listIterator();
      }

      ReceptorEntry r = receptorIterator.next();
      float reservedForEntry = removeReservedEnergy(r);

      PowerReceiver pp = r.powerReceptor.getPowerReceiver(r.direction);
      if (pp != null) {
        if (pp.getType() == Type.STORAGE) {
          // only apply energy to these guys if there is any left over
          storageReceptors.add(r);
        } else if (pp.getType() != Type.ENGINE) {

          float used = 0;
          float nonReservedPower = energyStored - reserved;
          float available = nonReservedPower + reservedForEntry;
          float canOffer = Math.min(r.emmiter.getCapacitor().getMaxEnergyExtracted(), available);
          float requested = pp.powerRequest();

          // If it is possible to supply the minimum amount of energy
          if (pp.getMinEnergyReceived() <= r.emmiter.getCapacitor().getMaxEnergyExtracted()) {
            // Buffer energy if we can't meet it now
            if (pp.getMinEnergyReceived() > canOffer && requested > 0) {
              reserveEnergy(r, canOffer);
              used += canOffer;
            } else if (r.powerReceptor instanceof IInternalPowerReceptor) {
              used = PowerHandlerUtil.transmitInternal((IInternalPowerReceptor) r.powerReceptor, pp, canOffer, Type.PIPE, r.direction);
            } else {
              float offer = Math.min(requested, canOffer);                            
              used = pp.receiveEnergy(Type.PIPE, offer, r.direction);              
            }

          }
          energyStored -= used;

        } 
        if (energyStored <= 0) {
          break;
        }

      }
      appliedCount++;
    }

    //send any energy left over to the storage once we are more than 90% full internallly
    if (energyStored > 0 && !storageReceptors.isEmpty() && (energyStored / maxEnergyStored > 0.9)) {
      for (ReceptorEntry r : storageReceptors) {
        PowerReceiver pp = r.powerReceptor.getPowerReceiver(r.direction);
        if (pp != null) {
          float used = 0;
          float available = energyStored - reserved;
          float canOffer = Math.min(r.emmiter.getCapacitor().getMaxEnergyExtracted(), available);
          float requested = pp.powerRequest();
          if (r.powerReceptor instanceof IInternalPowerReceptor) {            
            used = PowerHandlerUtil.transmitInternal((IInternalPowerReceptor) r.powerReceptor, pp, canOffer, Type.PIPE, r.direction);            
          } else {
            used = pp.receiveEnergy(Type.PIPE, Math.min(requested, canOffer), r.direction);
          }  
          energyStored -= used;
        }
        if (energyStored <= 0) {
          break;
        }
      }
    }
    distributeStorageToConduits();
  }

  private void updateActiveState() {
    boolean active;
    if (energyStored > 0) {
      ticksWithNoPower = 0;
      active = true;
    } else {
      ticksWithNoPower++;
      active = false;
    }

    boolean doRender = active != lastActiveValue && (active || (!active && ticksWithNoPower > updateRenderTicks));
    if (doRender) {
      lastActiveValue = active;
      for (IPowerConduit con : network.getConduits()) {
        con.setActive(active);
      }
    }
  }

  private float removeReservedEnergy(ReceptorEntry r) {
    StarveBuffer starveBuf = starveBuffers.remove(r.coord);
    if (starveBuf == null) {
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
    if (reserved > maxEnergyStored * 0.9) {
      starveBuffers.clear();
      reserved = 0;
    }
  }

  private void distributeStorageToConduits() {
    if(maxEnergyStored <= 0 ||  energyStored <= 0) {
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
      //NB: use ceil to ensure we dont through away any energy due to rounding errors
      float give = (float)Math.ceil(con.getCapacitor().getMaxEnergyStored() * filledRatio);
      give = Math.min(give,  con.getCapacitor().getMaxEnergyStored());
      give = Math.min(give,  energyLeft);
      con.getPowerHandler().setEnergy(give);
      given += give;
      energyLeft -= give;
      if(energyLeft <= 0) {        
        return;
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

    if (energyStored > maxEnergyStored) {
      energyStored = maxEnergyStored;
    }

  }

  public void receptorsChanged() {
    receptors.clear();
    receptors.addAll(network.getPowerReceptors());
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

}
