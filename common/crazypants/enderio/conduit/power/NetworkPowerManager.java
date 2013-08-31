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

  private int maxEnergyStored;
  private float energyStored;
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

    validateStorage();

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
    storageReceptors.clear();

    while (quantity > 0 && appliedCount < numReceptors) {

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
          float nonReservedPower = quantity - reserved;
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
          quantity -= used;

        } 
        if (quantity <= 0) {
          break;
        }

      }
      appliedCount++;
    }

    //send any energy left over to the storage
    if (quantity > 0 && !storageReceptors.isEmpty()) {
      for (ReceptorEntry r : storageReceptors) {
        PowerReceiver pp = r.powerReceptor.getPowerReceiver(r.direction);
        if (pp != null) {
          float used = 0;
          float available = quantity - reserved;
          //NB: We are using the max energy the the conduit can receive here rather than the amount they can output
          //as we always want to be able to give back as much as we received if we haven't used it or we will
          //leak energy
          float canOffer = Math.min(r.emmiter.getCapacitor().getMaxEnergyReceived(), available);
          float requested = pp.powerRequest();
          if (r.powerReceptor instanceof IInternalPowerReceptor) {            
            used = PowerHandlerUtil.transmitInternal((IInternalPowerReceptor) r.powerReceptor, pp, canOffer, Type.PIPE, r.direction);            
          } else {
            used = pp.receiveEnergy(Type.PIPE, Math.min(requested, canOffer), r.direction);
          }  
          quantity -= used;
        }
        if (quantity <= 0) {
          break;
        }
      }
    }
    energyStored = quantity;
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
    //TODO: Even this out or we end up with some blocked up conduits!!
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

  private void validateStorage() {
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
      PowerHandler ph = conduit.getPowerHandler();
      extracted += ph.getEnergyStored();
      ph.update();
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
      PowerHandler ph = con.getPowerHandler();
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
