package crazypants.enderio.conduit.power;

import java.util.*;

import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import buildcraft.api.power.IPowerReceptor;
import crazypants.enderio.conduit.power.PowerConduitNetwork.ReceptorEntry;
import crazypants.enderio.power.*;
import crazypants.util.BlockCoord;

public class NetworkPowerManager {

  private PowerConduitNetwork network;
  
  private int maxEnergyStored;
  private float energyStored;
  private float reserved;

  private int updateRenderTicks = 10;
  private int inactiveTicks = 100;

  private final List<ReceptorEntry> receptors = new ArrayList<PowerConduitNetwork.ReceptorEntry>();
  private ListIterator<ReceptorEntry> receptorIterator = receptors.listIterator();

  private boolean lastActiveValue = false;
  private int ticksWithNoPower = 0;
  
  private final Map<BlockCoord, StarveBuffer> starveBuffers = new HashMap<BlockCoord, NetworkPowerManager.StarveBuffer>();
  
  public NetworkPowerManager(PowerConduitNetwork netowrk, World world) {
    this.network = netowrk;
    maxEnergyStored = 64;
  }

  
  public float addEnergy(float quantity) {
    float used = quantity;
    energyStored += quantity;    
    if (energyStored > maxEnergyStored) {
      used -= energyStored - maxEnergyStored;
      energyStored = maxEnergyStored;
    } else if (energyStored < 0) {
      used -= energyStored;
      energyStored = 0;
    }    
    updateConduitStorage();
    return used;
  }   
  
  public void applyRecievedPower() {
   
    updateStorage();
    
    float extracted = extractRecievedEnergy();    
    addEnergy(extracted);
    checkReserves();
    float quantity = energyStored;
    boolean active;
    if (quantity > 0) {
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

    if (quantity <= 0 || receptors.isEmpty()) {
      return;
    }


    int appliedCount = 0;
    int numReceptors = receptors.size();
    int numEngines = 0;
    while (quantity > 0 && appliedCount < numReceptors) {

      if (!receptors.isEmpty() && !receptorIterator.hasNext()) {
        receptorIterator = receptors.listIterator();
      }

      ReceptorEntry r = receptorIterator.next();
      float reservedForEntry = removeReservedEnergy(r);

      for (ForgeDirection dir : r.directions) {

        IPowerReceptor pp = r.powerReceptor;
        if (pp != null) {

            
          float used = 0;
          float nonReservedPower = quantity - reserved;
          float available = nonReservedPower + reservedForEntry;
          float canOffer = Math.min(r.emmiter.getCapacitor().getMaxEnergyExtracted(), available);
          float requested = pp.powerRequest(dir);

          // If it is possible to supply the minimum amount of energy
          if (pp.getPowerProvider() != null && pp.getPowerProvider().getMinEnergyReceived() <= r.emmiter.getCapacitor().getMaxEnergyExtracted()) {
            // Buffer energy if we can't meet it now
            if (pp.getPowerProvider().getMinEnergyReceived() > canOffer && requested > 0) {
              reserveEnergy(r, canOffer);
              used += canOffer;
            } else if (r.powerReceptor instanceof IInternalPowerReceptor) {
              used = PowerHandlerUtil.transmitInternal((IInternalPowerReceptor) r.powerReceptor, canOffer, dir);
            } else {
              used = Math.min(requested, canOffer);
              pp.getPowerProvider().receiveEnergy(used, dir);
            }

          }
          quantity -= used;

        }

        if (quantity <= 0) {
          break;
        }

      }
      appliedCount++;
    }

    energyStored = Math.min(quantity, maxEnergyStored);
    updateConduitStorage();
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


  private void updateConduitStorage() {
    float energyLeft = energyStored;
    for (IPowerConduit con : network.getConduits()) {
      float give = con.getCapacitor().getMaxEnergyStored();
      if (give > energyLeft) {
        give = energyLeft;
      }
      con.setEnergyStored(give);
      energyLeft -= give;
    }
  }


  boolean isActive() {
    return energyStored > 0;
  }

  private void updateStorage() {
    maxEnergyStored = 0;
    energyStored = 0;
    for (IPowerConduit con : network.getConduits()) {
      maxEnergyStored += con.getCapacitor().getMaxEnergyStored();
      energyStored += con.getEnergyStored();
    }
    if (energyStored > maxEnergyStored) {
      energyStored = maxEnergyStored;
    }

  }

  private float extractRecievedEnergy() {
    float extracted = 0;
    for (IPowerConduit conduit : network.getConduits()) {
      EnderPowerProvider ph = conduit.getPowerHandler();
      extracted += ph.getEnergyStored();
      // ph.update();
      ph.setEnergy(0);
    }
    return extracted;
  }

  public void receptorsChanged() {
    receptors.clear();
    receptors.addAll(network.getPowerReceptors());
    receptorIterator = receptors.listIterator();
  }

  void onNetworkDestroyed() {
    // Pass out all the stored energy to the conduits
    for (IPowerConduit con : network.getConduits()) {
      EnderPowerProvider ph = con.getPowerHandler();
      float give = ph.getMaxEnergyStored() - ph.getEnergyStored();
      give = Math.min(give, energyStored);
      ph.setEnergy(ph.getEnergyStored() + give);
      energyStored -= give;
      if (energyStored <= 0) {
        return;
      }
    }

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
