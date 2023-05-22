package crazypants.enderio.machine.power;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Map.Entry;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraftforge.common.util.ForgeDirection;

import com.enderio.core.common.util.BlockCoord;
import com.enderio.core.common.util.Util;
import com.enderio.core.common.vecmath.VecmathUtil;

import cofh.api.energy.IEnergyContainerItem;
import crazypants.enderio.EnderIO;
import crazypants.enderio.TileEntityEio;
import crazypants.enderio.conduit.ConnectionMode;
import crazypants.enderio.conduit.IConduitBundle;
import crazypants.enderio.conduit.power.IPowerConduit;
import crazypants.enderio.config.Config;
import crazypants.enderio.machine.IIoConfigurable;
import crazypants.enderio.machine.IoMode;
import crazypants.enderio.machine.RedstoneControlMode;
import crazypants.enderio.network.PacketHandler;
import crazypants.enderio.power.BasicCapacitor;
import crazypants.enderio.power.IInternalPowerHandler;
import crazypants.enderio.power.IInternalPoweredTile;
import crazypants.enderio.power.IPowerInterface;
import crazypants.enderio.power.IPowerStorage;
import crazypants.enderio.power.PowerHandlerUtil;

public class TileCapacitorBank extends TileEntityEio
        implements IInternalPowerHandler, IInventory, IIoConfigurable, IPowerStorage {

    static final BasicCapacitor BASE_CAP = new BasicCapacitor(
            Config.capacitorBankMaxIoRF,
            Config.capacitorBankMaxStorageRF);

    private static final int MAX_SIZE = Integer.MAX_VALUE / Config.capacitorBankMaxStorageRF;

    BlockCoord[] multiblock = null;

    private int lastSyncPowerStored;

    int storedEnergyRF;

    private int maxStoredEnergy;

    private int maxIO;

    private int maxInput;

    private int maxOutput;

    private boolean multiblockDirty = false;

    private RedstoneControlMode inputControlMode;

    private RedstoneControlMode outputControlMode;

    private boolean outputEnabled;

    private boolean inputEnabled;

    private boolean isRecievingRedstoneSignal;

    private boolean redstoneStateDirty = true;
    private int lastComparatorState = 0;

    private List<Receptor> masterReceptors;
    private ListIterator<Receptor> receptorIterator;

    private List<Receptor> localReceptors;
    private boolean receptorsDirty = true;

    private final ItemStack[] inventory;

    private List<GaugeBounds> gaugeBounds;

    private Map<ForgeDirection, IoMode> faceModes;

    private boolean render = false;

    private boolean masterReceptorsDirty;

    private boolean notifyNeighbours = false;

    int energyAtLastRender = -1;

    private boolean isCreative = false;

    double lastRenderStoredRatio;

    public TileCapacitorBank() {
        inventory = new ItemStack[4];
        storedEnergyRF = 0;
        inputControlMode = RedstoneControlMode.IGNORE;
        outputControlMode = RedstoneControlMode.IGNORE;
        maxStoredEnergy = BASE_CAP.getMaxEnergyStored();
        maxIO = BASE_CAP.getMaxEnergyExtracted();
        maxInput = maxIO;
        maxOutput = maxIO;
    }

    @Override
    public IoMode toggleIoModeForFace(ForgeDirection faceHit) {
        IPowerInterface rec = getReceptorForFace(faceHit);
        IoMode curMode = getIoMode(faceHit);
        if (curMode == IoMode.PULL) {
            setIoMode(faceHit, IoMode.PUSH, true);
            return IoMode.PUSH;
        }
        if (curMode == IoMode.PUSH) {
            setIoMode(faceHit, IoMode.DISABLED, true);
            return IoMode.DISABLED;
        }
        if (curMode == IoMode.DISABLED) {
            if (rec == null || rec.getDelegate() instanceof IConduitBundle) {
                setIoMode(faceHit, IoMode.NONE, true);
                return IoMode.NONE;
            }
        }
        setIoMode(faceHit, IoMode.PULL, true);
        return IoMode.PULL;
    }

    @Override
    public boolean supportsMode(ForgeDirection faceHit, IoMode mode) {
        IPowerInterface rec = getReceptorForFace(faceHit);
        if (mode == IoMode.NONE) {
            return rec == null || rec.getDelegate() instanceof IConduitBundle;
        }
        return true;
    }

    @Override
    public void setIoMode(ForgeDirection faceHit, IoMode mode) {
        setIoMode(faceHit, mode, true);
    }

    @Override
    public void clearAllIoModes() {
        if (faceModes != null) {
            faceModes = null;
            receptorsDirty = true;
            getController().masterReceptorsDirty = true;
            notifyNeighbours = true;
            render = true;
            updateBlock();
        }
    }

    public void setIoMode(ForgeDirection faceHit, IoMode mode, boolean updateReceptors) {
        if (mode == IoMode.NONE && faceModes == null) {
            return;
        }
        if (faceModes == null) {
            faceModes = new EnumMap<ForgeDirection, IoMode>(ForgeDirection.class);
        }
        faceModes.put(faceHit, mode);
        if (updateReceptors) {
            receptorsDirty = true;
            getController().masterReceptorsDirty = true;
            notifyNeighbours = true;
        }
        render = true;

        updateBlock();
    }

    @Override
    public IoMode getIoMode(ForgeDirection face) {
        if (faceModes == null) {
            return IoMode.NONE;
        }
        IoMode res = faceModes.get(face);
        if (res == null) {
            return IoMode.NONE;
        }
        return res;
    }

    private IPowerInterface getReceptorForFace(ForgeDirection faceHit) {
        BlockCoord checkLoc = new BlockCoord(this).getLocation(faceHit);
        TileEntity te = worldObj.getTileEntity(checkLoc.x, checkLoc.y, checkLoc.z);
        if (!(te instanceof TileCapacitorBank)) {
            return PowerHandlerUtil.create(te);
        }
        return null;
    }

    @Override
    public void doUpdate() {
        if (blockMetadata == -1) {
            isCreative = getBlockMetadata() == 1;
        }

        if (worldObj.isRemote) {
            if (render) {
                worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
                render = false;
            }
            return;
        } // else is server, do all logic only on the server

        if (multiblockDirty) {
            formMultiblock();
            multiblockDirty = false;
        }

        // update any comparators, since they don't check themselves
        int comparatorState = getComparatorOutput();
        if (lastComparatorState != comparatorState) {
            worldObj.func_147453_f(xCoord, yCoord, zCoord, getBlockType());
            lastComparatorState = comparatorState;
        }

        if (!isContoller()) {
            if (notifyNeighbours) {
                worldObj.notifyBlocksOfNeighborChange(xCoord, yCoord, zCoord, getBlockType());
                notifyNeighbours = false;
            }
            return;
        }

        chargeItems();

        boolean hasSignal = isRecievingRedstoneSignal();
        if (inputControlMode == RedstoneControlMode.IGNORE) {
            inputEnabled = true;
        } else if (inputControlMode == RedstoneControlMode.NEVER) {
            inputEnabled = false;
        } else {
            inputEnabled = (inputControlMode == RedstoneControlMode.ON && hasSignal)
                    || (inputControlMode == RedstoneControlMode.OFF && !hasSignal);
        }
        if (outputControlMode == RedstoneControlMode.IGNORE) {
            outputEnabled = true;
        } else if (outputControlMode == RedstoneControlMode.NEVER) {
            outputEnabled = false;
        } else {
            outputEnabled = (outputControlMode == RedstoneControlMode.ON && hasSignal)
                    || (outputControlMode == RedstoneControlMode.OFF && !hasSignal);
        }

        updateMasterReceptors();
        if (outputEnabled) {
            transmitEnergy();
        }

        if (isCreative) {
            setEnergyStored(getMaxEnergyStored() / 2);
        }

        if (lastSyncPowerStored != getEnergyStored() && shouldDoWorkThisTick(10)) {
            lastSyncPowerStored = getEnergyStored();
            PacketHandler.sendToAllAround(new PacketPowerStorage(this), this, 64);
        }

        if (notifyNeighbours) {
            worldObj.notifyBlocksOfNeighborChange(xCoord, yCoord, zCoord, getBlockType());
            notifyNeighbours = false;
        }
    }

    public int getComparatorOutput() {
        double stored = getEnergyStored();
        return stored == 0 ? 0 : (int) (1 + stored / getMaxEnergyStored() * 14);
    }

    public List<GaugeBounds> getGaugeBounds() {
        if (gaugeBounds == null) {
            gaugeBounds = GaugeBounds.calculateGaugeBounds(new BlockCoord(this), multiblock);
        }
        return gaugeBounds;
    }

    public boolean chargeItems(ItemStack[] items) {
        boolean chargedItem = false;
        int available = Math.min(maxIO, getEnergyStored());
        for (ItemStack item : items) {
            if (item != null && available > 0) {
                int used = 0;
                if (item.getItem() instanceof IEnergyContainerItem) {
                    IEnergyContainerItem chargable = (IEnergyContainerItem) item.getItem();

                    int max = chargable.getMaxEnergyStored(item);
                    int cur = chargable.getEnergyStored(item);
                    int canUse = Math.min(available, max - cur);
                    if (cur < max) {
                        used = chargable.receiveEnergy(item, canUse, false);
                    }
                }
                if (used > 0) {
                    setEnergyStored(getEnergyStored() - used);
                    chargedItem = true;
                    available -= used;
                }
            }
        }
        return chargedItem;
    }

    private void chargeItems() {
        boolean chargedItem = chargeItems(inventory);
        if (chargedItem) {
            markDirty();
        }
    }

    public boolean isOutputEnabled() {
        return getController().outputEnabled;
    }

    @Override
    public boolean isOutputEnabled(ForgeDirection direction) {
        IoMode mode = getIoMode(direction);
        return mode == IoMode.PUSH || mode == IoMode.NONE && isOutputEnabled();
    }

    public boolean isInputEnabled() {
        return getController().inputEnabled;
    }

    @Override
    public boolean isInputEnabled(ForgeDirection direction) {
        IoMode mode = getIoMode(direction);
        return mode == IoMode.PULL || mode == IoMode.NONE && isInputEnabled();
    }

    @Override
    public boolean isNetworkControlledIo(ForgeDirection direction) {
        IoMode mode = getIoMode(direction);
        return mode == IoMode.NONE || mode == IoMode.PULL;
    }

    private boolean transmitEnergy() {

        if (getEnergyStored() <= 0) {
            return false;
        }
        int canTransmit = Math.min(getEnergyStored(), maxOutput);
        int transmitted = 0;

        if (!masterReceptors.isEmpty() && !receptorIterator.hasNext()) {
            receptorIterator = masterReceptors.listIterator();
        }

        int appliedCount = 0;
        int numReceptors = masterReceptors.size();
        while (receptorIterator.hasNext() && canTransmit > 0 && appliedCount < numReceptors) {

            Receptor receptor = receptorIterator.next();
            IPowerInterface powerInterface = receptor.receptor;
            IoMode mode = receptor.mode;
            if (powerInterface != null && mode != IoMode.PULL
                    && mode != IoMode.DISABLED
                    && powerInterface.getMinEnergyReceived(receptor.fromDir.getOpposite()) <= canTransmit) {
                double used;
                if (receptor.receptor.getDelegate() instanceof IConduitBundle && !isCreative) {
                    // All other power transfer is handled by the conduit network
                    IConduitBundle bundle = (IConduitBundle) receptor.receptor.getDelegate();
                    IPowerConduit conduit = bundle.getConduit(IPowerConduit.class);
                    if (conduit != null
                            && conduit.getConnectionMode(receptor.fromDir.getOpposite()) == ConnectionMode.INPUT) {
                        used = powerInterface.recieveEnergy(receptor.fromDir.getOpposite(), canTransmit);
                    } else {
                        used = 0;
                    }
                } else {
                    used = powerInterface.recieveEnergy(receptor.fromDir.getOpposite(), canTransmit);
                }

                transmitted += used;
                canTransmit -= used;
            }

            if (canTransmit <= 0) {
                break;
            }

            if (!masterReceptors.isEmpty() && !receptorIterator.hasNext()) {
                receptorIterator = masterReceptors.listIterator();
            }
            appliedCount++;
        }
        setEnergyStored(getEnergyStored() - transmitted);

        return transmitted > 0;
    }

    private void updateMasterReceptors() {
        if (!masterReceptorsDirty && masterReceptors != null) {
            return;
        }

        if (masterReceptors == null) {
            masterReceptors = new ArrayList<Receptor>();
        }
        masterReceptors.clear();

        if (multiblock == null) {
            updateReceptors();
            if (localReceptors != null) {
                masterReceptors.addAll(localReceptors);
            }
        } else {
            // TODO: Performance warning??
            for (BlockCoord bc : multiblock) {
                TileEntity te = worldObj.getTileEntity(bc.x, bc.y, bc.z);
                if (te instanceof TileCapacitorBank) {
                    TileCapacitorBank cb = ((TileCapacitorBank) te);
                    cb.updateReceptors();
                    if (cb.localReceptors != null) {
                        masterReceptors.addAll(cb.localReceptors);
                    }
                }
            }
        }

        receptorIterator = masterReceptors.listIterator();
        masterReceptorsDirty = false;
    }

    private void updateReceptors() {

        if (!receptorsDirty) {
            return;
        }
        if (localReceptors != null) {
            localReceptors.clear();
        }

        BlockCoord bc = new BlockCoord(this);
        for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
            IoMode mode = getIoMode(dir);
            if (mode != IoMode.DISABLED) {
                BlockCoord checkLoc = bc.getLocation(dir);
                TileEntity te = worldObj.getTileEntity(checkLoc.x, checkLoc.y, checkLoc.z);
                if (!(te instanceof TileCapacitorBank)) {
                    IPowerInterface ph = PowerHandlerUtil.create(te);
                    if (ph != null && ph.canConduitConnect(dir)) {
                        if (localReceptors == null) {
                            localReceptors = new ArrayList<Receptor>();
                        }
                        Receptor r = new Receptor(ph, dir, mode);
                        localReceptors.add(r);
                        if (mode == IoMode.NONE && !(ph.getDelegate() instanceof IInternalPoweredTile)) {
                            setIoMode(dir, IoMode.PULL, false);
                            r.mode = IoMode.PULL;
                            render = true;
                        }
                    }
                }
            }
        }

        receptorsDirty = false;
    }

    // ------------ Multiblock overrides

    public int getEnergyStoredScaled(int scale) {
        return getController().doGetEnergyStoredScaled(scale);
    }

    @Override
    public int getMaxInput() {
        return maxInput;
    }

    public void setMaxInput(int maxInput) {
        getController().doSetMaxInput(maxInput);
        updateBlock();
    }

    @Override
    public int getMaxOutput() {
        return maxOutput;
    }

    public void setMaxOutput(int maxOutput) {
        getController().doSetMaxOutput(maxOutput);
        updateBlock();
    }

    @Override
    public int getEnergyStored() {
        return getController().doGetEnergyStored();
    }

    @Override
    public long getEnergyStoredL() {
        return getEnergyStored();
    }

    public double getEnergyStoredRatio() {
        return getController().doGetEnergyStoredRatio();
    }

    @Override
    public int getMaxEnergyStored() {
        return getController().doGetMaxEnergyStored();
    }

    @Override
    public long getMaxEnergyStoredL() {
        return getMaxEnergyStored();
    }

    public int getMaxIO() {
        return getController().doGetMaxIO();
    }

    // RF Power

    @Override
    public int getMaxEnergyRecieved(ForgeDirection dir) {
        return getMaxEnergyStored();
    }

    @Override
    public void setEnergyStored(int stored) {
        getController().doSetEnergyStored(stored);
    }

    @Override
    public int extractEnergy(ForgeDirection from, int maxExtract, boolean simulate) {
        return 0;
    }

    @Override
    public boolean canConnectEnergy(ForgeDirection from) {
        return getIoMode(from) != IoMode.DISABLED;
    }

    @Override
    public int receiveEnergy(ForgeDirection from, int maxReceive, boolean simulate) {
        IoMode mode = getIoMode(from);
        if (mode == IoMode.DISABLED || mode == IoMode.PUSH) {
            return 0;
        }
        return getController().doReceiveEnergy(from, maxReceive, simulate);
    }

    @Override
    public int getEnergyStored(ForgeDirection from) {
        return getController().doGetEnergyStored(from);
    }

    @Override
    public int getMaxEnergyStored(ForgeDirection from) {
        return getController().doGetMaxEnergyStored();
    }

    public int doReceiveEnergy(ForgeDirection from, int maxReceive, boolean simulate) {
        return PowerHandlerUtil.recieveInternal(this, maxReceive, from, simulate);
    }

    public int doGetEnergyStored(ForgeDirection from) {
        return doGetEnergyStored();
    }

    public int doGetMaxEnergyStored(ForgeDirection from) {
        return doGetMaxEnergyStored();
    }

    // end rf power

    @Override
    public void addEnergy(int add) {
        getController().doAddEnergy(add);
    }

    private boolean isRecievingRedstoneSignal() {
        if (!redstoneStateDirty) {
            return isRecievingRedstoneSignal;
        }

        isRecievingRedstoneSignal = false;
        redstoneStateDirty = false;

        if (!isMultiblock()) {
            isRecievingRedstoneSignal = worldObj.getStrongestIndirectPower(xCoord, yCoord, zCoord) > 0;
        } else {
            for (BlockCoord bc : multiblock) {
                if (worldObj.getStrongestIndirectPower(bc.x, bc.y, bc.z) > 0) {
                    isRecievingRedstoneSignal = true;
                    break;
                }
            }
        }
        return isRecievingRedstoneSignal;
    }

    public RedstoneControlMode getInputControlMode() {
        return inputControlMode;
    }

    public void setInputControlMode(RedstoneControlMode inputControlMode) {
        if (!isMultiblock()) {
            this.inputControlMode = inputControlMode;
        } else {
            for (BlockCoord bc : multiblock) {
                TileCapacitorBank cp = getCapBank(bc);
                if (cp != null) {
                    cp.inputControlMode = inputControlMode;
                }
            }
        }
    }

    public RedstoneControlMode getOutputControlMode() {
        return outputControlMode;
    }

    public void setOutputControlMode(RedstoneControlMode outputControlMode) {
        if (!isMultiblock()) {
            this.outputControlMode = outputControlMode;
        } else {
            for (BlockCoord bc : multiblock) {
                TileCapacitorBank cp = getCapBank(bc);
                if (cp != null) {
                    cp.outputControlMode = outputControlMode;
                }
            }
        }
    }

    // ------------ Multiblock implementations

    int doGetMaxIO() {
        return maxIO;
    }

    int doGetMaxEnergyStored() {
        return maxStoredEnergy;
    }

    int doGetEnergyStoredScaled(int scale) {
        return (int) VecmathUtil.clamp(Math.round(scale * doGetEnergyStoredRatio()), 0, scale);
    }

    int doGetEnergyStored() {
        return storedEnergyRF;
    }

    void doSetEnergyStored(int stored) {
        storedEnergyRF = MathHelper.clamp_int(stored, 0, getMaxEnergyStored());
    }

    double doGetEnergyStoredRatio() {
        return (double) doGetEnergyStored() / maxStoredEnergy;
    }

    void doAddEnergy(int add) {
        doSetEnergyStored(doGetEnergyStored() + add);
    }

    void doSetMaxInput(int in) {
        maxInput = Math.min(in, maxIO);
        maxInput = Math.max(0, maxInput);
        if (isMultiblock()) {
            for (BlockCoord bc : multiblock) {
                TileCapacitorBank cp = getCapBank(bc);
                if (cp != null) {
                    cp.maxInput = maxInput;
                }
            }
        }
    }

    void doSetMaxOutput(int out) {
        maxOutput = Math.min(out, maxIO);
        maxOutput = Math.max(0, maxOutput);
        if (isMultiblock()) {
            for (BlockCoord bc : multiblock) {
                TileCapacitorBank cp = getCapBank(bc);
                if (cp != null) {
                    cp.maxOutput = maxOutput;
                }
            }
        }
    }

    // ------------ Multiblock management

    public void onBlockAdded() {
        multiblockDirty = true;
    }

    public void onNeighborBlockChange(Block block) {
        if (block != EnderIO.blockCapacitorBank) {
            receptorsDirty = true;
            getController().masterReceptorsDirty = true;
            getController().redstoneStateDirty = true;
        }
        redstoneStateDirty = true;
    }

    public void onBreakBlock() {
        TileCapacitorBank controller = getController();
        controller.clearCurrentMultiblock();
    }

    private void clearCurrentMultiblock() {
        if (multiblock == null) {
            return;
        }
        for (BlockCoord bc : multiblock) {
            TileCapacitorBank res = getCapBank(bc);
            if (res != null) {
                res.setMultiblock(null);
            }
        }
        multiblock = null;
        redstoneStateDirty = true;
    }

    private void formMultiblock() {

        if (isCreative || isMaxSize()) {
            return;
        }
        List<TileCapacitorBank> blocks = new ArrayList<TileCapacitorBank>();
        blocks.add(this);
        findNighbouringBanks(this, blocks);

        if (blocks.size() < 2) {
            return;
        }
        for (TileCapacitorBank cb : blocks) {
            cb.clearCurrentMultiblock();
        }

        BlockCoord[] mb = new BlockCoord[blocks.size()];
        for (int i = 0; i < blocks.size(); i++) {
            mb[i] = new BlockCoord(blocks.get(i));
        }

        TileCapacitorBank secondary = blocks.get(1);
        maxInput = maxOutput = -1;

        if (secondary.maxInput != secondary.maxIO) {
            maxInput = secondary.maxInput;
        }

        if (secondary.maxOutput != secondary.maxIO) {
            maxOutput = secondary.maxOutput;
        }

        for (TileCapacitorBank cb : blocks) {
            cb.setMultiblock(mb);
        }
    }

    private void findNighbouringBanks(TileCapacitorBank tileCapacitorBank, List<TileCapacitorBank> blocks) {
        if (isCreative || blocks.size() >= MAX_SIZE) {
            return;
        }
        BlockCoord bc = new BlockCoord(tileCapacitorBank);
        for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
            TileCapacitorBank cb = getCapBank(bc.getLocation(dir));
            if (cb != null && !blocks.contains(cb) && !cb.isCreative && !cb.isMaxSize()) {
                if (blocks.size() >= MAX_SIZE) {
                    return;
                }
                blocks.add(cb);
                findNighbouringBanks(cb, blocks);
            }
        }
    }

    private void setMultiblock(BlockCoord[] mb) {

        if (multiblock != null && isMaster()) {

            // split up current multiblock and reconfigure all the internal capacitors
            int powerPerBlock = storedEnergyRF / multiblock.length;
            int remaining = storedEnergyRF % multiblock.length;
            for (BlockCoord bc : multiblock) {
                TileCapacitorBank cb = getCapBank(bc);
                if (cb != null) {
                    cb.maxStoredEnergy = BASE_CAP.getMaxEnergyStored();
                    cb.maxIO = BASE_CAP.getMaxEnergyExtracted();
                    cb.maxInput = Math.min(cb.maxInput, cb.maxIO);
                    cb.maxOutput = Math.min(cb.maxOutput, cb.maxIO);
                    cb.doSetEnergyStored(powerPerBlock);
                    cb.multiblockDirty = true;
                }
            }
            doAddEnergy(remaining);
        }
        multiblock = mb;
        if (isMaster()) {

            List<ItemStack> invItems = new ArrayList<ItemStack>();

            int totalStored = 0;
            int totalCap = multiblock.length * BASE_CAP.getMaxEnergyStored();
            int totalIO = multiblock.length * BASE_CAP.getMaxEnergyExtracted();
            for (BlockCoord bc : multiblock) {
                TileCapacitorBank cb = getCapBank(bc);
                if (cb != null) {
                    totalStored += cb.doGetEnergyStored();
                }
                ItemStack[] inv = cb.inventory;
                for (int i = 0; i < inv.length; i++) {
                    if (inv[i] != null) {
                        invItems.add(inv[i]);
                        inv[i] = null;
                    }
                }
                cb.multiblockDirty = false;
            }
            maxStoredEnergy = totalCap;
            doSetEnergyStored(totalStored);
            maxIO = totalIO;
            maxInput = maxInput < 0 ? maxIO : Math.min(maxInput, maxIO);
            maxOutput = maxOutput < 0 ? maxIO : Math.min(maxOutput, maxIO);
            for (BlockCoord bc : multiblock) {
                TileCapacitorBank cb = getCapBank(bc);
                if (cb != null && cb != this) {
                    cb.maxIO = totalIO;
                    cb.maxInput = maxInput;
                    cb.maxOutput = maxOutput;
                }
            }

            if (invItems.size() > inventory.length) {
                for (int i = inventory.length; i < invItems.size(); i++) {
                    Util.dropItems(worldObj, invItems.get(i), xCoord, yCoord, zCoord, true);
                }
            }
            for (int i = 0; i < inventory.length && i < invItems.size(); i++) {
                inventory[i] = invItems.get(i);
            }
        }
        receptorsDirty = true;
        getController().masterReceptorsDirty = true;
        redstoneStateDirty = true;

        // Forces an update
        worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
        render = true;
    }

    @Override
    public TileCapacitorBank getController() {
        if (isMaster() || !isMultiblock()) {
            return this;
        }
        TileCapacitorBank res = getCapBank(multiblock[0]);
        return res != null ? res : this;
    }

    boolean isContoller() {
        return multiblock == null ? true : isMaster();
    }

    boolean isMaster() {
        if (multiblock != null) {
            return multiblock[0].equals(xCoord, yCoord, zCoord);
        }
        return false;
    }

    public boolean isMultiblock() {
        return multiblock != null;
    }

    private boolean isCurrentMultiblockValid() {
        if (multiblock == null) {
            return false;
        }
        for (BlockCoord bc : multiblock) {
            TileCapacitorBank res = getCapBank(bc);
            if (res == null || !res.isMultiblock()) {
                return false;
            }
        }
        return true;
    }

    private TileCapacitorBank getCapBank(BlockCoord bc) {
        return getCapBank(bc.x, bc.y, bc.z);
    }

    private TileCapacitorBank getCapBank(int x, int y, int z) {
        if (worldObj == null) {
            return null;
        }
        TileEntity te = worldObj.getTileEntity(x, y, z);
        if (te instanceof TileCapacitorBank) {
            return (TileCapacitorBank) te;
        }
        return null;
    }

    // ------------- Inventory

    @Override
    public int getSizeInventory() {
        return getController().doGetSizeInventory();
    }

    @Override
    public ItemStack getStackInSlot(int i) {
        return getController().doGetStackInSlot(i);
    }

    @Override
    public ItemStack decrStackSize(int i, int j) {
        return getController().doDecrStackSize(i, j);
    }

    @Override
    public void setInventorySlotContents(int i, ItemStack itemstack) {
        getController().doSetInventorySlotContents(i, itemstack);
    }

    public ItemStack doGetStackInSlot(int i) {
        if (i < 0 || i >= inventory.length) {
            return null;
        }
        return inventory[i];
    }

    public int doGetSizeInventory() {
        return inventory.length;
    }

    public ItemStack doDecrStackSize(int fromSlot, int amount) {
        if (fromSlot < 0 || fromSlot >= inventory.length) {
            return null;
        }
        ItemStack item = inventory[fromSlot];
        if (item == null) {
            return null;
        }
        if (item.stackSize <= amount) {
            ItemStack result = item.copy();
            inventory[fromSlot] = null;
            return result;
        }
        item.stackSize -= amount;
        return item.copy();
    }

    public void doSetInventorySlotContents(int i, ItemStack itemstack) {
        if (i < 0 || i >= inventory.length) {
            return;
        }
        inventory[i] = itemstack;
    }

    @Override
    public ItemStack getStackInSlotOnClosing(int i) {
        return null;
    }

    @Override
    public String getInventoryName() {
        return EnderIO.blockCapacitorBank.getUnlocalizedName() + ".name";
    }

    @Override
    public int getInventoryStackLimit() {
        return 1;
    }

    @Override
    public boolean isUseableByPlayer(EntityPlayer entityplayer) {
        return true;
    }

    @Override
    public void openInventory() {}

    @Override
    public void closeInventory() {}

    @Override
    public boolean isItemValidForSlot(int i, ItemStack itemstack) {
        if (itemstack == null) {
            return false;
        }
        return itemstack.getItem() instanceof IEnergyContainerItem;
    }

    @Override
    public void readCustomNBT(NBTTagCompound nbtRoot) {

        if (nbtRoot.hasKey("maxStoredEnergy")) {
            nbtRoot.setInteger("maxStoredEnergyRF", nbtRoot.getInteger("maxStoredEnergy") * 10);
        }
        maxStoredEnergy = nbtRoot.getInteger("maxStoredEnergyRF");

        double oldEnergy = storedEnergyRF;
        if (nbtRoot.hasKey("storedEnergyD")) {
            nbtRoot.setInteger("storedEnergyRF", (int) (nbtRoot.getDouble("storedEnergyD") * 10));
        }
        doSetEnergyStored(nbtRoot.getInteger("storedEnergyRF"));

        double newEnergy = storedEnergyRF;
        if (maxStoredEnergy != 0 && Math.abs(oldEnergy - newEnergy) / maxStoredEnergy > 0.05
                || nbtRoot.hasKey("render")) {
            render = true;
        }
        if (energyAtLastRender != -1 && maxStoredEnergy != 0) {
            double change = Math.abs(energyAtLastRender - storedEnergyRF) / (double) maxStoredEnergy;
            if (change > 0.05) {
                render = true;
            }
        }

        if (nbtRoot.hasKey("maxIO")) {
            nbtRoot.setInteger("maxIoRF", nbtRoot.getInteger("maxIO") * 10);
        }
        maxIO = nbtRoot.getInteger("maxIoRF");

        if (nbtRoot.hasKey("maxInput")) {
            nbtRoot.setInteger("maxInputRF", nbtRoot.getInteger("maxInput") * 10);
        }
        if (nbtRoot.hasKey("maxInputRF")) {
            maxInput = nbtRoot.getInteger("maxInputRF");
        } else {
            maxOutput = maxIO;
        }

        if (nbtRoot.hasKey("maxOutput")) {
            nbtRoot.setInteger("maxOuputRF", nbtRoot.getInteger("maxOuput") * 10);
        }
        if (nbtRoot.hasKey("maxOutputRF")) {
            maxOutput = nbtRoot.getInteger("maxOutputRF");
        } else {
            maxInput = maxIO;
        }

        inputControlMode = RedstoneControlMode.values()[nbtRoot.getShort("inputControlMode")];
        outputControlMode = RedstoneControlMode.values()[nbtRoot.getShort("outputControlMode")];

        boolean wasMulti = isMultiblock();
        if (nbtRoot.getBoolean("isMultiblock")) {
            int[] coords = nbtRoot.getIntArray("multiblock");
            multiblock = new BlockCoord[coords.length / 3];
            int c = 0;
            for (int i = 0; i < multiblock.length; i++) {
                multiblock[i] = new BlockCoord(coords[c++], coords[c++], coords[c++]);
            }

        } else {
            multiblock = null;
        }

        for (int i = 0; i < inventory.length; i++) {
            inventory[i] = null;
        }

        NBTTagList itemList = (NBTTagList) nbtRoot.getTag("Items");
        if (itemList != null) {
            for (int i = 0; i < itemList.tagCount(); i++) {
                NBTTagCompound itemStack = itemList.getCompoundTagAt(i);
                byte slot = itemStack.getByte("Slot");
                if (slot >= 0 && slot < inventory.length) {
                    inventory[slot] = ItemStack.loadItemStackFromNBT(itemStack);
                }
            }
        }

        if (nbtRoot.hasKey("hasFaces")) {
            for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
                if (nbtRoot.hasKey("face" + dir.ordinal())) {
                    setIoMode(dir, IoMode.values()[nbtRoot.getShort("face" + dir.ordinal())], false);
                }
            }
        }
        gaugeBounds = null;
    }

    @Override
    public void writeCustomNBT(NBTTagCompound nbtRoot) {

        nbtRoot.setDouble("storedEnergyRF", storedEnergyRF);
        nbtRoot.setInteger("maxStoredEnergyRF", maxStoredEnergy);
        nbtRoot.setInteger("maxIoRF", maxIO);
        nbtRoot.setInteger("maxInputRF", maxInput);
        nbtRoot.setInteger("maxOutputRF", maxOutput);
        nbtRoot.setShort("inputControlMode", (short) inputControlMode.ordinal());
        nbtRoot.setShort("outputControlMode", (short) outputControlMode.ordinal());

        nbtRoot.setBoolean("isMultiblock", isMultiblock());
        if (isMultiblock()) {
            int[] vals = new int[multiblock.length * 3];
            int i = 0;
            for (BlockCoord bc : multiblock) {
                vals[i++] = bc.x;
                vals[i++] = bc.y;
                vals[i++] = bc.z;
            }
            nbtRoot.setIntArray("multiblock", vals);
        }

        // write inventory list
        NBTTagList itemList = new NBTTagList();
        for (int i = 0; i < inventory.length; i++) {
            if (inventory[i] != null) {
                NBTTagCompound itemStackNBT = new NBTTagCompound();
                itemStackNBT.setByte("Slot", (byte) i);
                inventory[i].writeToNBT(itemStackNBT);
                itemList.appendTag(itemStackNBT);
            }
        }
        nbtRoot.setTag("Items", itemList);

        // face modes
        if (faceModes != null) {
            nbtRoot.setByte("hasFaces", (byte) 1);
            for (Entry<ForgeDirection, IoMode> e : faceModes.entrySet()) {
                nbtRoot.setShort("face" + e.getKey().ordinal(), (short) e.getValue().ordinal());
            }
        }

        if (render) {
            nbtRoot.setBoolean("render", true);
            render = false;
        }
    }

    static class Receptor {

        IPowerInterface receptor;
        ForgeDirection fromDir;
        IoMode mode;

        private Receptor(IPowerInterface rec, ForgeDirection fromDir, IoMode mode) {
            receptor = rec;
            this.fromDir = fromDir;
            this.mode = mode;
        }

        @Override
        public String toString() {
            return "Receptor [receptor=" + receptor + ", fromDir=" + fromDir + ", mode=" + mode + "]";
        }
    }

    @Override
    public boolean hasCustomInventoryName() {

        return false;
    }

    public void setCreativeMode() {
        isCreative = true;
        maxIO = maxIO * 1000;
        maxInput = maxIO;
        maxOutput = maxIO;
    }

    @Override
    public boolean isCreative() {
        return isCreative;
    }

    public boolean isMaxSize() {
        return isMultiblock() && multiblock.length >= MAX_SIZE;
    }

    @Override
    public boolean displayPower() {
        return true;
    }
}
