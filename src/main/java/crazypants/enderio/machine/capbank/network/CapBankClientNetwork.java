package crazypants.enderio.machine.capbank.network;

import com.enderio.core.common.util.BlockCoord;
import crazypants.enderio.EnderIO;
import crazypants.enderio.machine.RedstoneControlMode;
import crazypants.enderio.machine.capbank.CapBankType;
import crazypants.enderio.machine.capbank.InfoDisplayType;
import crazypants.enderio.machine.capbank.TileCapBank;
import crazypants.enderio.machine.capbank.packet.PacketNetworkEnergyRequest;
import crazypants.enderio.machine.capbank.packet.PacketNetworkStateRequest;
import crazypants.enderio.network.PacketHandler;
import crazypants.enderio.power.IPowerStorage;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class CapBankClientNetwork implements ICapBankNetwork {

    private final int id;
    private final Map<BlockCoord, TileCapBank> members = new HashMap<BlockCoord, TileCapBank>();
    private int maxEnergySent;
    private int maxEnergyRecieved;

    private int stateUpdateCount;
    private int maxIO;
    private long maxEnergyStored;
    private long energyStored;

    private RedstoneControlMode inputControlMode = RedstoneControlMode.IGNORE;
    private RedstoneControlMode outputControlMode = RedstoneControlMode.IGNORE;

    private final InventoryImpl inventory = new InventoryImpl();

    private float avgInput;
    private float avgOutput;

    private long lastPowerRequestTick = -1;

    private Map<DisplayInfoKey, IOInfo> ioDisplayInfoCache;

    public CapBankClientNetwork(int id) {
        this.id = id;
    }

    @Override
    public int getId() {
        return id;
    }

    public void requestPowerUpdate(TileCapBank capBank, int interval) {
        long curTick = EnderIO.proxy.getTickCount();
        if (lastPowerRequestTick == -1 || curTick - lastPowerRequestTick >= interval) {
            if (stateUpdateCount == 0) {
                PacketHandler.INSTANCE.sendToServer(new PacketNetworkStateRequest(capBank));
                // the network state also contains the energy data
            } else {
                PacketHandler.INSTANCE.sendToServer(new PacketNetworkEnergyRequest(capBank));
            }
            lastPowerRequestTick = curTick;
        }
    }

    public void setState(World world, NetworkState state) {
        maxEnergyRecieved = state.getMaxInput();
        maxEnergySent = state.getMaxOutput();
        maxIO = state.getMaxIO();
        maxEnergyStored = state.getMaxEnergyStored();
        energyStored = state.getEnergyStored();
        inputControlMode = state.getInputMode();
        outputControlMode = state.getOutputMode();

        BlockCoord bc = state.getInventoryImplLocation();
        if (bc == null) {
            inventory.setCapBank(null);
        } else if (world != null) {
            TileEntity te = world.getTileEntity(bc.x, bc.y, bc.z);
            if (te instanceof TileCapBank) {
                inventory.setCapBank((TileCapBank) te);
            }
        }
        avgInput = state.getAverageInput();
        avgOutput = state.getAverageOutput();

        stateUpdateCount++;
    }

    public int getStateUpdateCount() {
        return stateUpdateCount;
    }

    public void setStateUpdateCount(int stateUpdateCount) {
        this.stateUpdateCount = stateUpdateCount;
    }

    @Override
    public void addMember(TileCapBank capBank) {
        members.put(capBank.getLocation(), capBank);
        invalidateDisplayInfoCache();
    }

    @Override
    public Collection<TileCapBank> getMembers() {
        return members.values();
    }

    @Override
    public void destroyNetwork() {
        for (TileCapBank cb : members.values()) {
            cb.setNetworkId(-1);
            cb.setNetwork(null);
        }
        invalidateDisplayInfoCache();
    }

    @Override
    public int getMaxIO() {
        return maxIO;
    }

    @Override
    public long getMaxEnergyStoredL() {
        return maxEnergyStored;
    }

    public void setMaxEnergyStoredL(long maxEnergyStored) {
        this.maxEnergyStored = maxEnergyStored;
    }

    public void setEnergyStored(long energyStored) {
        this.energyStored = energyStored;
    }

    @Override
    public long getEnergyStoredL() {
        return energyStored;
    }

    @Override
    public int getMaxOutput() {
        return maxEnergySent;
    }

    @Override
    public void setMaxOutput(int max) {
        maxEnergySent = MathHelper.clamp_int(max, 0, maxIO);
    }

    @Override
    public int getMaxInput() {
        return maxEnergyRecieved;
    }

    @Override
    public void setMaxInput(int max) {
        maxEnergyRecieved = MathHelper.clamp_int(max, 0, maxIO);
    }

    public double getEnergyStoredRatio() {
        if (getMaxEnergyStoredL() <= 0) {
            return 0;
        }
        return (double) getEnergyStoredL() / getMaxEnergyStoredL();
    }

    @Override
    public RedstoneControlMode getInputControlMode() {
        return inputControlMode;
    }

    @Override
    public void setInputControlMode(RedstoneControlMode inputControlMode) {
        this.inputControlMode = inputControlMode;
    }

    @Override
    public RedstoneControlMode getOutputControlMode() {
        return outputControlMode;
    }

    @Override
    public void setOutputControlMode(RedstoneControlMode outputControlMode) {
        this.outputControlMode = outputControlMode;
    }

    @Override
    public InventoryImpl getInventory() {
        return inventory;
    }

    @Override
    public float getAverageChangePerTick() {
        return avgInput - avgOutput;
    }

    @Override
    public float getAverageInputPerTick() {
        return avgInput;
    }

    @Override
    public float getAverageOutputPerTick() {
        return avgOutput;
    }

    public void setAverageIOPerTick(float input, float output) {
        this.avgInput = input;
        this.avgOutput = output;
    }

    @Override
    public NetworkState getState() {
        return new NetworkState(this);
    }

    @Override
    public void onUpdateEntity(TileCapBank tileCapBank) {}

    @Override
    public void addEnergy(int energy) {}

    @Override
    public int receiveEnergy(int maxReceive, boolean simulate) {
        return 0;
    }

    @Override
    public void removeReceptors(Collection<EnergyReceptor> receptors) {}

    @Override
    public void addReceptors(Collection<EnergyReceptor> receptors) {}

    @Override
    public void updateRedstoneSignal(TileCapBank tileCapBank, boolean recievingSignal) {}

    @Override
    public boolean isOutputEnabled() {
        return true;
    }

    @Override
    public boolean isInputEnabled() {
        return true;
    }

    @Override
    public IPowerStorage getController() {
        return this;
    }

    @Override
    public boolean isOutputEnabled(ForgeDirection direction) {
        return isOutputEnabled();
    }

    @Override
    public boolean isInputEnabled(ForgeDirection direction) {
        return isInputEnabled();
    }

    @Override
    public boolean isCreative() {
        return false;
    }

    @Override
    public boolean isNetworkControlledIo(ForgeDirection direction) {
        return true;
    }

    @Override
    public void invalidateDisplayInfoCache() {
        ioDisplayInfoCache = null;
    }

    public IOInfo getIODisplayInfo(int x, int y, int z, ForgeDirection face) {
        DisplayInfoKey key = new DisplayInfoKey(x, y, z, face);
        if (ioDisplayInfoCache == null) {
            ioDisplayInfoCache = new HashMap<DisplayInfoKey, IOInfo>();
        }
        IOInfo value = ioDisplayInfoCache.get(key);
        if (value == null) {
            value = computeIODisplayInfo(x, y, z, face);
            ioDisplayInfoCache.put(key, value);
        }
        return value;
    }

    private IOInfo computeIODisplayInfo(int xOrg, int yOrg, int zOrg, ForgeDirection dir) {
        if (dir.offsetY != 0) {
            return IOInfo.SINGLE;
        }

        TileCapBank cb = getCapBankAt(xOrg, yOrg, zOrg);
        if (cb == null) {
            return IOInfo.SINGLE;
        }

        CapBankType type = cb.getType();
        ForgeDirection left = dir.getRotation(ForgeDirection.DOWN);
        ForgeDirection right = left.getOpposite();

        int hOff = 0;
        int vOff = 0;

        // step 1: find top left
        while (isIOType(xOrg + left.offsetX, yOrg, zOrg + left.offsetZ, dir, type)) {
            xOrg += left.offsetX;
            zOrg += left.offsetZ;
            hOff++;
        }

        while (isIOType(xOrg, yOrg + 1, zOrg, dir, type)) {
            yOrg++;
            vOff++;
        }

        if (isIOType(xOrg + left.offsetX, yOrg, zOrg + left.offsetZ, dir, type)) {
            // not a rectangle
            return IOInfo.SINGLE;
        }

        // step 2: find width
        int width = 1;
        int height = 1;
        int xTmp = xOrg;
        int yTmp = yOrg;
        int zTmp = zOrg;
        while (isIOType(xTmp + right.offsetX, yTmp, zTmp + right.offsetZ, dir, type)) {
            if (isIOType(xTmp + right.offsetX, yTmp + 1, zTmp + right.offsetZ, dir, type)) {
                // not a rectangle
                return IOInfo.SINGLE;
            }
            xTmp += right.offsetX;
            zTmp += right.offsetZ;
            width++;
        }

        // step 3: find height
        while (isIOType(xOrg, yTmp - 1, zOrg, dir, type)) {
            xTmp = xOrg;
            yTmp--;
            zTmp = zOrg;

            if (isIOType(xTmp + left.offsetX, yTmp, zTmp + left.offsetZ, dir, type)) {
                // not a rectangle
                return IOInfo.SINGLE;
            }

            for (int i = 1; i < width; i++) {
                xTmp += right.offsetX;
                zTmp += right.offsetZ;

                if (!isIOType(xTmp, yTmp, zTmp, dir, type)) {
                    // not a rectangle
                    return IOInfo.SINGLE;
                }
            }

            if (isIOType(xTmp + right.offsetX, yTmp, zTmp + right.offsetZ, dir, type)) {
                // not a rectangle
                return IOInfo.SINGLE;
            }

            height++;
        }

        xTmp = xOrg;
        yTmp--;
        zTmp = zOrg;

        for (int i = 0; i < width; i++) {
            if (isIOType(xTmp, yTmp, zTmp, dir, type)) {
                // not a rectangle
                return IOInfo.SINGLE;
            }

            xTmp += right.offsetX;
            zTmp += right.offsetZ;
        }

        if (width == 1 && height == 1) {
            return IOInfo.SINGLE;
        }

        if (hOff > 0 || vOff > 0) {
            return IOInfo.INSIDE;
        }

        return new IOInfo(width, height);
    }

    private boolean isIOType(int x, int y, int z, ForgeDirection face, CapBankType type) {
        TileCapBank cb = getCapBankAt(x, y, z);
        return cb != null && type == cb.getType() && cb.getDisplayType(face) == InfoDisplayType.IO;
    }

    private TileCapBank getCapBankAt(int x, int y, int z) {
        return members.get(new BlockCoord(x, y, z));
    }

    public static final class DisplayInfoKey {
        final int x;
        final int y;
        final int z;
        final ForgeDirection face;

        public DisplayInfoKey(int x, int y, int z, ForgeDirection face) {
            this.x = x;
            this.y = y;
            this.z = z;
            this.face = face;
        }

        @Override
        public int hashCode() {
            int hash = 7;
            hash = 97 * hash + this.x;
            hash = 97 * hash + this.y;
            hash = 97 * hash + this.z;
            hash = 97 * hash + this.face.hashCode();
            return hash;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof DisplayInfoKey)) {
                return false;
            }
            final DisplayInfoKey other = (DisplayInfoKey) obj;
            return (this.x == other.x) && (this.y == other.y) && (this.z == other.z) && (this.face == other.face);
        }
    }

    public static class IOInfo {
        public final int width;
        public final int height;

        static final IOInfo SINGLE = new IOInfo(1, 1);
        static final IOInfo INSIDE = new IOInfo(0, 0);

        IOInfo(int width, int height) {
            this.width = width;
            this.height = height;
        }

        public boolean isInside() {
            return width == 0;
        }
    }
}
