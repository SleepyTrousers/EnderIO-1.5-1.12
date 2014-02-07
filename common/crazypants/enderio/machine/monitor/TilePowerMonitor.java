package crazypants.enderio.machine.monitor;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.ForgeDirection;
import powercrystals.minefactoryreloaded.api.rednet.IRedNetNetworkContainer;
import crazypants.enderio.ModObject;
import crazypants.enderio.conduit.AbstractConduitNetwork;
import crazypants.enderio.conduit.ConduitUtil;
import crazypants.enderio.conduit.power.IPowerConduit;
import crazypants.enderio.conduit.power.NetworkPowerManager;
import crazypants.enderio.conduit.power.PowerConduitNetwork;
import crazypants.enderio.conduit.power.PowerTracker;
import crazypants.enderio.conduit.redstone.Signal;
import crazypants.enderio.machine.AbstractMachineEntity;
import crazypants.enderio.machine.SlotDefinition;
import crazypants.enderio.power.IInternalPowerReceptor;
import crazypants.util.BlockCoord;
import crazypants.util.DyeColor;
import crazypants.util.Util;

public class TilePowerMonitor extends AbstractMachineEntity implements IInternalPowerReceptor {

  float energyPerTick = 0.05f;

  float powerInConduits;
  float maxPowerInCoduits;
  float powerInCapBanks;
  float maxPowerInCapBanks;
  float powerInMachines;
  float maxPowerInMachines;
  float aveMjSent;
  float aveMjRecieved;

  boolean engineControlEnabled = false;
  float startLevel = 0.75f;
  float stopLevel = 0.99f;
  DyeColor signalColor = DyeColor.RED;

  Signal currentlyEmmittedSignal;

  public TilePowerMonitor() {
    super(new SlotDefinition(0, 0));
  }

  public int[] getRednetOutputValues(ForgeDirection side) {
    if(currentlyEmmittedSignal == null) {
      return new int[16];
    }
    int[] res = new int[DyeColor.values().length];
    for (DyeColor col : DyeColor.values()) {
      res[col.ordinal()] = currentlyEmmittedSignal.color == col ? 15 : 0;
    }
    return res;
  }

  public int getRednetOutputValue(ForgeDirection side, int subnet) {
    if(currentlyEmmittedSignal != null) {
      return 15;
    }
    return 0;
  }

  int asPercentInt(float val) {
    return Math.round(val * 100);
  }

  float asPercentFloat(int val) {
    return val / 100f;
  }

  @Override
  public String getInvName() {
    return ModObject.blockPowerMonitor.name;
  }

  @Override
  public String getMachineName() {
    return ModObject.blockPowerMonitor.unlocalisedName;
  }

  @Override
  protected boolean isMachineItemValidForSlot(int i, ItemStack itemstack) {
    return false;
  }

  @Override
  public boolean isActive() {
    return false;
  }

  @Override
  public float getProgress() {
    return 0;
  }

  public float getEnergyPerTick() {
    return energyPerTick;
  }

  public float getPowerInConduits() {
    return powerInConduits;
  }

  public float getMaxPowerInCoduits() {
    return maxPowerInCoduits;
  }

  public float getPowerInCapBanks() {
    return powerInCapBanks;
  }

  public float getMaxPowerInCapBanks() {
    return maxPowerInCapBanks;
  }

  public float getPowerInMachines() {
    return powerInMachines;
  }

  public float getMaxPowerInMachines() {
    return maxPowerInMachines;
  }

  public float getAveMjSent() {
    return aveMjSent;
  }

  public float getAveMjRecieved() {
    return aveMjRecieved;
  }

  @Override
  protected boolean processTasks(boolean redstoneCheckPassed) {
    powerHandler.setEnergy(powerHandler.getEnergyStored() - energyPerTick);
    boolean update = worldObj.getWorldInfo().getWorldTotalTime() % 20 == 0;

    NetworkPowerManager pm = getPowerManager();
    if(pm != null) {
      update(pm);
    }

    Signal sig = null;
    if(!engineControlEnabled) {
      sig = null;
    } else {
      float percentFull = getPercentFull();
      if(currentlyEmmittedSignal == null) {
        if(percentFull <= startLevel) {
          sig = new Signal(xCoord, yCoord, zCoord, ForgeDirection.UNKNOWN, 15, signalColor);
        }
      } else {
        if(percentFull >= stopLevel) {
          sig = null;
        } else {
          sig = currentlyEmmittedSignal;
        }
      }
    }
    if(currentlyEmmittedSignal != sig) {
      currentlyEmmittedSignal = sig;
      broadcastSignal();
    }

    return update;
  }

  private void broadcastSignal() {

    for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
      BlockCoord to = getLocation().getLocation(dir);
      Block b = Util.getBlock(worldObj.getBlockId(to.x, to.y, to.z));
      if(b instanceof IRedNetNetworkContainer) {
        ((IRedNetNetworkContainer) b).updateNetwork(worldObj, to.x, to.y, to.z);
      }
    }
    worldObj.notifyBlocksOfNeighborChange(xCoord, yCoord, zCoord, ModObject.blockPowerMonitor.actualId);

  }

  private float getPercentFull() {
    return (powerInConduits + powerInCapBanks) / (maxPowerInCoduits + maxPowerInCapBanks);
  }

  private void update(NetworkPowerManager pm) {
    powerInConduits = pm.getPowerInConduits();
    maxPowerInCoduits = pm.getMaxPowerInConduits();
    powerInCapBanks = pm.getPowerInCapacitorBanks();
    maxPowerInCapBanks = pm.getMaxPowerInCapacitorBanks();
    powerInMachines = pm.getPowerInReceptors();
    maxPowerInMachines = pm.getMaxPowerInReceptors();
    PowerTracker tracker = pm.getNetworkPowerTracker();
    aveMjSent = tracker.getAverageMjTickSent();
    aveMjRecieved = tracker.getAverageMjTickRecieved();

  }

  private NetworkPowerManager getPowerManager() {
    for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
      IPowerConduit con = ConduitUtil.getConduit(worldObj, this, dir, IPowerConduit.class);
      if(con != null) {
        AbstractConduitNetwork<?, ?> n = con.getNetwork();
        if(n instanceof PowerConduitNetwork) {
          NetworkPowerManager pm = ((PowerConduitNetwork) n).getPowerManager();
          if(pm != null) {
            return pm;
          }
        }
      }
    }
    return null;
  }

  @Override
  public void readFromNBT(NBTTagCompound nbtRoot) {
    super.readFromNBT(nbtRoot);

    powerInConduits = nbtRoot.getFloat("powerInConduits");
    maxPowerInCoduits = nbtRoot.getFloat("maxPowerInCoduits");
    powerInCapBanks = nbtRoot.getFloat("powerInCapBanks");
    maxPowerInCapBanks = nbtRoot.getFloat("maxPowerInCapBanks");
    powerInMachines = nbtRoot.getFloat("powerInMachines");
    maxPowerInMachines = nbtRoot.getFloat("maxPowerInMachines");
    aveMjSent = nbtRoot.getFloat("aveMjSent");
    aveMjRecieved = nbtRoot.getFloat("aveMjRecieved");

    engineControlEnabled = nbtRoot.getBoolean("engineControlEnabled");
    startLevel = nbtRoot.getFloat("startLevel");
    stopLevel = nbtRoot.getFloat("stopLevel");
    signalColor = DyeColor.fromIndex(nbtRoot.getShort("signalColor"));
  }

  @Override
  public void writeToNBT(NBTTagCompound nbtRoot) {
    super.writeToNBT(nbtRoot);

    nbtRoot.setFloat("powerInConduits", powerInConduits);
    nbtRoot.setFloat("maxPowerInCoduits", maxPowerInCoduits);
    nbtRoot.setFloat("powerInCapBanks", powerInCapBanks);
    nbtRoot.setFloat("maxPowerInCapBanks", maxPowerInCapBanks);
    nbtRoot.setFloat("powerInMachines", powerInMachines);
    nbtRoot.setFloat("maxPowerInMachines", maxPowerInMachines);
    nbtRoot.setFloat("aveMjSent", aveMjSent);
    nbtRoot.setFloat("aveMjRecieved", aveMjRecieved);

    nbtRoot.setBoolean("engineControlEnabled", engineControlEnabled);
    nbtRoot.setFloat("startLevel", startLevel);
    nbtRoot.setFloat("stopLevel", stopLevel);
    nbtRoot.setShort("signalColor", (short) signalColor.ordinal());

    ;

  }

}
