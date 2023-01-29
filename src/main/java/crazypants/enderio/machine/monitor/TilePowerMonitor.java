package crazypants.enderio.machine.monitor;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;

import com.enderio.core.common.util.DyeColor;

import crazypants.enderio.EnderIO;
import crazypants.enderio.ModObject;
import crazypants.enderio.conduit.AbstractConduitNetwork;
import crazypants.enderio.conduit.ConduitUtil;
import crazypants.enderio.conduit.power.IPowerConduit;
import crazypants.enderio.conduit.power.NetworkPowerManager;
import crazypants.enderio.conduit.power.PowerConduitNetwork;
import crazypants.enderio.conduit.power.PowerTracker;
import crazypants.enderio.conduit.redstone.Signal;
import crazypants.enderio.machine.AbstractPowerConsumerEntity;
import crazypants.enderio.machine.IoMode;
import crazypants.enderio.machine.SlotDefinition;
import crazypants.enderio.network.PacketHandler;
import crazypants.enderio.power.IInternalPoweredTile;

public class TilePowerMonitor extends AbstractPowerConsumerEntity implements IInternalPoweredTile {

    int energyPerTick = 1;

    int powerInConduits;
    int maxPowerInConduits;
    long powerInCapBanks;
    long maxPowerInCapBanks;
    long powerInMachines;
    long maxPowerInMachines;
    float aveRfSent;
    float aveRfReceived;

    boolean engineControlEnabled = false;
    float startLevel = 0.75f;
    float stopLevel = 0.99f;

    private Signal currentlyEmmittedSignal;

    public TilePowerMonitor() {
        super(new SlotDefinition(0, 0));
    }

    @Override
    public boolean supportsMode(ForgeDirection faceHit, IoMode mode) {
        return mode == IoMode.NONE;
    }

    public int[] getRednetOutputValues(ForgeDirection side) {
        if (currentlyEmmittedSignal == null) {
            return new int[16];
        }
        int[] res = new int[DyeColor.values().length];
        for (DyeColor col : DyeColor.values()) {
            res[col.ordinal()] = currentlyEmmittedSignal.color == col ? 15 : 0;
        }
        return res;
    }

    public int getRednetOutputValue(ForgeDirection side, int subnet) {
        if (currentlyEmmittedSignal != null) {
            return 15;
        }
        return 0;
    }

    public boolean isEngineControlEnabled() {
        return this.engineControlEnabled;
    }

    public void setEngineControlEnabled(boolean control) {
        this.engineControlEnabled = control;
    }

    public float getStartLevel() {
        return this.startLevel;
    }

    public void setStartLevel(float start) {
        this.startLevel = start;
    }

    public float getStopLevel() {
        return this.stopLevel;
    }

    public void setStopLevel(float stop) {
        this.stopLevel = stop;
    }

    int asPercentInt(float val) {
        return Math.round(val * 100);
    }

    float asPercentFloat(int val) {
        return val / 100f;
    }

    @Override
    public String getInventoryName() {
        return EnderIO.blockPowerMonitor.getUnlocalizedName();
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

    public float getEnergyPerTick() {
        return energyPerTick;
    }

    public float getPowerInConduits() {
        return powerInConduits;
    }

    public float getMaxPowerInConduits() {
        return maxPowerInConduits;
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

    public float getAveRfSent() {
        return aveRfSent;
    }

    public float getAveRfReceived() {
        return aveRfReceived;
    }

    @Override
    protected boolean processTasks(boolean redstoneCheckPassed) {
        setEnergyStored(getEnergyStored() - energyPerTick);

        boolean update = worldObj.getWorldInfo().getWorldTotalTime() % 10 == 0;
        NetworkPowerManager pm = getPowerManager();
        if (pm != null && update) {
            update(pm);
            Signal sig = null;
            if (!engineControlEnabled) {
                sig = null;
            } else {
                float percentFull = getPercentFull();
                if (currentlyEmmittedSignal == null) {
                    if (percentFull <= startLevel) {
                        sig = new Signal(xCoord, yCoord, zCoord, ForgeDirection.UNKNOWN, 15, DyeColor.RED);
                    }
                } else {
                    if (percentFull >= stopLevel) {
                        sig = null;
                    } else {
                        sig = currentlyEmmittedSignal;
                    }
                }
            }
            if (currentlyEmmittedSignal != sig) {
                currentlyEmmittedSignal = sig;
                broadcastSignal();
            }
        }
        if (update) {
            PacketHandler.sendToAllAround(new PacketPowerInfo(this), this);
        }
        return false;
    }

    private void broadcastSignal() {
        worldObj.notifyBlocksOfNeighborChange(xCoord, yCoord, zCoord, EnderIO.blockPowerMonitor);
    }

    private float getPercentFull() {
        return (float) (powerInConduits + powerInCapBanks) / (maxPowerInConduits + maxPowerInCapBanks);
    }

    private void update(NetworkPowerManager pm) {
        powerInConduits = pm.getPowerInConduits();
        maxPowerInConduits = pm.getMaxPowerInConduits();
        powerInCapBanks = pm.getPowerInCapacitorBanks();
        maxPowerInCapBanks = pm.getMaxPowerInCapacitorBanks();
        powerInMachines = pm.getPowerInReceptors();
        maxPowerInMachines = pm.getMaxPowerInReceptors();
        PowerTracker tracker = pm.getNetworkPowerTracker();
        aveRfSent = tracker.getAverageRfTickSent();
        aveRfReceived = tracker.getAverageRfTickRecieved();
    }

    private NetworkPowerManager getPowerManager() {
        for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
            IPowerConduit con = ConduitUtil.getConduit(worldObj, this, dir, IPowerConduit.class);
            if (con != null) {
                AbstractConduitNetwork<?, ?> n = con.getNetwork();
                if (n instanceof PowerConduitNetwork) {
                    NetworkPowerManager pm = ((PowerConduitNetwork) n).getPowerManager();
                    if (pm != null) {
                        return pm;
                    }
                }
            }
        }
        return null;
    }

    @Override
    public void readCustomNBT(NBTTagCompound nbtRoot) {
        super.readCustomNBT(nbtRoot);
        readPowerInfoFromNBT(nbtRoot);
    }

    public void readPowerInfoFromNBT(NBTTagCompound nbtRoot) {
        powerInConduits = nbtRoot.getInteger("powerInConduits");
        maxPowerInConduits = nbtRoot.getInteger("maxPowerInConduits");
        if (nbtRoot.hasKey("powerInCapBanks")) {
            powerInCapBanks = nbtRoot.getInteger("powerInCapBanks");
            maxPowerInCapBanks = nbtRoot.getInteger("maxPowerInCapBanks");
        } else {
            powerInCapBanks = nbtRoot.getLong("powerInCapBanksL");
            maxPowerInCapBanks = nbtRoot.getLong("maxPowerInCapBanksL");
        }
        powerInMachines = nbtRoot.getLong("powerInMachines");
        maxPowerInMachines = nbtRoot.getLong("maxPowerInMachines");
        aveRfSent = nbtRoot.getFloat("aveRfSent");
        aveRfReceived = nbtRoot.getFloat("aveRfReceived");

        engineControlEnabled = nbtRoot.getBoolean("engineControlEnabled");
        startLevel = nbtRoot.getFloat("startLevel");
        stopLevel = nbtRoot.getFloat("stopLevel");
    }

    @Override
    public void writeCustomNBT(NBTTagCompound nbtRoot) {
        super.writeCustomNBT(nbtRoot);
        writePowerInfoToNBT(nbtRoot);
    }

    public void writePowerInfoToNBT(NBTTagCompound nbtRoot) {
        nbtRoot.setInteger("powerInConduits", powerInConduits);
        nbtRoot.setInteger("maxPowerInConduits", maxPowerInConduits);
        nbtRoot.setLong("powerInCapBanksL", powerInCapBanks);
        nbtRoot.setLong("maxPowerInCapBanksL", maxPowerInCapBanks);
        nbtRoot.setLong("powerInMachines", powerInMachines);
        nbtRoot.setLong("maxPowerInMachines", maxPowerInMachines);
        nbtRoot.setFloat("aveRfSent", aveRfSent);
        nbtRoot.setFloat("aveRfReceived", aveRfReceived);

        nbtRoot.setBoolean("engineControlEnabled", engineControlEnabled);
        nbtRoot.setFloat("startLevel", startLevel);
        nbtRoot.setFloat("stopLevel", stopLevel);
    }

    @Override
    public boolean hasCustomInventoryName() {
        return false;
    }
}
