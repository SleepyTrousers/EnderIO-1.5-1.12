package crazypants.enderio.machine.hypercube;

import com.enderio.core.common.util.BlockCoord;
import com.enderio.core.common.util.ItemUtil;
import com.enderio.core.common.util.PlayerUtil;
import com.enderio.core.common.vecmath.VecmathUtil;
import crazypants.enderio.EnderIO;
import crazypants.enderio.ModObject;
import crazypants.enderio.TileEntityEio;
import crazypants.enderio.config.Config;
import crazypants.enderio.machine.IRedstoneModeControlable;
import crazypants.enderio.machine.RedstoneControlMode;
import crazypants.enderio.power.BasicCapacitor;
import crazypants.enderio.power.IInternalPowerHandler;
import crazypants.enderio.power.IPowerInterface;
import crazypants.enderio.power.PowerHandlerUtil;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.ListIterator;
import java.util.UUID;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;

public class TileHyperCube extends TileEntityEio
        implements IInternalPowerHandler, IFluidHandler, ISidedInventory, IRedstoneModeControlable {

    private static final double ENERGY_LOSS = Config.transceiverEnergyLoss;

    private static final int ENERGY_UPKEEP = Config.transceiverUpkeepCostRF;

    private static final float MILLIBUCKET_TRANSMISSION_COST = Config.transceiverBucketTransmissionCostRF / 1000F;

    public static enum IoMode {
        SEND("gui.send"),
        RECIEVE("gui.receive"),
        BOTH("gui.sendReceive"),
        NEITHER("gui.disabled");

        public static IoMode next(IoMode mode) {
            int index = mode.ordinal() + 1;
            if (index >= values().length) {
                index = 0;
            }
            return values()[index];
        }

        public static boolean isRecieveEnabled(IoMode mode) {
            return mode == RECIEVE || mode == BOTH;
        }

        public static boolean isSendEnabled(IoMode mode) {
            return mode == SEND || mode == BOTH;
        }

        private final String unlocalisedName;

        private IoMode(String unlocalisedName) {
            this.unlocalisedName = unlocalisedName;
        }

        public boolean isRecieveEnabled() {
            return isRecieveEnabled(this);
        }

        public boolean isSendEnabled() {
            return isSendEnabled(this);
        }

        public IoMode next() {
            return next(this);
        }

        public String getUnlocalisedName() {
            return unlocalisedName;
        }

        public String getLocalisedName() {
            return EnderIO.lang.localize(unlocalisedName);
        }
    }

    public static enum SubChannel {
        POWER,
        FLUID,
        ITEM
    }

    private final BasicCapacitor internalCapacitor = new BasicCapacitor(Config.transceiverMaxIoRF, 25000);

    private int lastSyncPowerStored = 0;
    private int storedEnergyRF;

    private final List<Receptor> receptors = new ArrayList<Receptor>();
    private ListIterator<Receptor> receptorIterator = receptors.listIterator();
    private boolean receptorsDirty = true;

    private final List<NetworkFluidHandler> fluidHandlers = new ArrayList<NetworkFluidHandler>();
    private boolean fluidHandlersDirty = true;

    private CompositeInventory localInventory = new CompositeInventory();
    private boolean inventoriesDirty = true;

    private Channel channel = null;
    private Channel registeredChannel = null;
    private UUID owner;

    private boolean init = true;

    private float milliBucketsTransfered = 0;

    private EnumMap<SubChannel, IoMode> ioModes =
            new EnumMap<TileHyperCube.SubChannel, TileHyperCube.IoMode>(SubChannel.class);

    private ItemRecieveBuffer recieveBuffer;

    protected RedstoneControlMode redstoneControlMode = RedstoneControlMode.IGNORE;
    protected boolean redstoneCheckPassed;
    private boolean redstoneStateDirty = true;
    private boolean isConnected = false;

    public TileHyperCube() {
        redstoneControlMode = RedstoneControlMode.IGNORE;
        recieveBuffer = new ItemRecieveBuffer(this);
    }

    @Override
    public RedstoneControlMode getRedstoneControlMode() {
        return redstoneControlMode;
    }

    @Override
    public void setRedstoneControlMode(RedstoneControlMode redstoneControlMode) {
        this.redstoneControlMode = redstoneControlMode;
        redstoneStateDirty = true;
        updateBlock();
    }

    public IoMode getModeForChannel(SubChannel channel) {
        IoMode mode = ioModes.get(channel);
        if (mode == null) {
            return IoMode.NEITHER;
        }
        return mode;
    }

    public void setModeForChannel(SubChannel channel, IoMode mode) {
        ioModes.put(channel, mode);
    }

    public Channel getChannel() {
        return channel;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }

    int getEnergyStoredScaled(int scale) {
        return (int)
                VecmathUtil.clamp(Math.round(scale * ((double) getEnergyStored() / getMaxEnergyStored())), 0, scale);
    }

    public void onBreakBlock() {
        HyperCubeRegister.instance.deregister(this);
    }

    public void onBlockAdded() {
        HyperCubeRegister.instance.register(this);
    }

    public void setOwner(UUID owner) {
        this.owner = owner;
    }

    private boolean canMaintainConnection() {
        if (channel == null || HyperCubeRegister.instance == null || !redstoneCheckPassed) {
            return false;
        }
        List<TileHyperCube> cons = HyperCubeRegister.instance.getCubesForChannel(channel);
        for (TileHyperCube cube : cons) {
            if (cube != this && cube.getEnergyStored() <= 0) {
                return false;
            }
        }
        return cons != null && cons.size() > 1 && getEnergyStored() > 0;
    }

    private void sendEnergyToOtherNodes() {

        List<TileHyperCube> cubes = HyperCubeRegister.instance.getCubesForChannel(channel);
        if (cubes == null || cubes.isEmpty()) {
            return;
        }

        if (canSendPower()) {

            for (TileHyperCube cube : cubes) {
                int stored = getEnergyStored();
                if (stored > 0 && cube != null && cube != this && cube.canRecievePower()) {
                    int curPower = cube.getEnergyStored();
                    int requires = cube.getMaxEnergyStored() - curPower;
                    int transfer = Math.min(requires, stored);
                    transfer = Math.min(transfer, Config.transceiverMaxIoRF);
                    cube.setEnergyStored(curPower + (int) Math.round((1 - ENERGY_LOSS) * transfer));
                    setEnergyStored(getEnergyStored() - transfer);
                }
            }
        }
    }

    @Override
    public void onChunkUnload() {
        if (HyperCubeRegister.instance != null) {
            HyperCubeRegister.instance.deregister(this);
        }
        fluidHandlersDirty = true;
        receptorsDirty = true;
        inventoriesDirty = true;
    }

    public void onNeighborBlockChange() {
        receptorsDirty = true;
        fluidHandlersDirty = true;
        inventoriesDirty = true;
        redstoneStateDirty = true;
        updateInventories();
    }

    @Override
    public void doUpdate() {
        if (worldObj.isRemote) {
            return;
        } // else is server, do all logic only on the server

        // Pay upkeep cost
        storedEnergyRF -= ENERGY_UPKEEP;
        // Pay fluid transmission cost
        storedEnergyRF -= (MILLIBUCKET_TRANSMISSION_COST * milliBucketsTransfered);

        // update power status
        storedEnergyRF = Math.max(storedEnergyRF, 0);

        milliBucketsTransfered = 0;

        boolean prevRedCheck = redstoneCheckPassed;
        if (redstoneStateDirty) {
            redstoneCheckPassed = RedstoneControlMode.isConditionMet(redstoneControlMode, this);
            redstoneStateDirty = false;
        }

        if (!redstoneCheckPassed) {
            if (registeredChannel != null) {
                HyperCubeRegister.instance.deregister(this, registeredChannel);
                registeredChannel = null;
            }
        }

        if (storedEnergyRF > 0) {
            transmitEnergy();
            sendEnergyToOtherNodes();
        }

        updateInventories();
        pushRecieveBuffer();

        // check we are still connected (i.e. we haven't run out of power or started
        // receiving power)
        boolean requiresClientSync = false;

        boolean stillConnected = canMaintainConnection();
        if (isConnected != stillConnected) {
            fluidHandlersDirty = true;
            isConnected = stillConnected;
            requiresClientSync = true;
        }
        updateFluidHandlers();

        if (redstoneCheckPassed && (registeredChannel == null ? channel != null : !registeredChannel.equals(channel))) {
            if (registeredChannel != null) {
                HyperCubeRegister.instance.deregister(this, registeredChannel);
            }
            HyperCubeRegister.instance.register(this);
            registeredChannel = channel;
        }

        requiresClientSync |= prevRedCheck != redstoneCheckPassed;

        boolean powerChanged = lastSyncPowerStored != storedEnergyRF && shouldDoWorkThisTick(21);
        if (powerChanged) {
            lastSyncPowerStored = storedEnergyRF;
            EnderIO.packetPipeline.sendToAllAround(new PacketStoredPower(this), this);
        }

        if (requiresClientSync) {

            // this will cause 'getPacketDescription()' to be called and its result
            // will be sent to the PacketHandler on the other end of
            // client/server connection
            worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
            // And this will make sure our current tile entity state is saved
            markDirty();
        }
    }

    private boolean canSendFluid() {
        return getModeForChannel(SubChannel.FLUID).isSendEnabled();
    }

    private boolean canSendPower() {
        return getModeForChannel(SubChannel.POWER).isSendEnabled();
    }

    private boolean canSendItems() {
        return getModeForChannel(SubChannel.ITEM).isSendEnabled() && redstoneCheckPassed;
    }

    private boolean canRecieveFluid() {
        return getModeForChannel(SubChannel.FLUID).isRecieveEnabled();
    }

    private boolean canRecievePower() {
        return getModeForChannel(SubChannel.POWER).isRecieveEnabled();
    }

    private boolean canRecieveItems() {
        return getModeForChannel(SubChannel.ITEM).isRecieveEnabled();
    }

    // -------------------------- Power -----------------------------------------------

    private boolean transmitEnergy() {

        if (!getModeForChannel(SubChannel.POWER).isRecieveEnabled() || !redstoneCheckPassed || getEnergyStored() <= 0) {
            return false;
        }

        int canTransmit = Math.min(getEnergyStored(), internalCapacitor.getMaxEnergyExtracted());
        int transmitted = 0;

        updatePowersReceptors();

        if (!receptors.isEmpty() && !receptorIterator.hasNext()) {
            receptorIterator = receptors.listIterator();
        }

        int appliedCount = 0;
        int numReceptors = receptors.size();
        while (receptorIterator.hasNext() && canTransmit > 0 && appliedCount < numReceptors) {
            Receptor receptor = receptorIterator.next();
            IPowerInterface pp = receptor.receptor;
            if (pp != null && pp.getMinEnergyReceived(receptor.fromDir.getOpposite()) <= canTransmit) {
                float used = pp.recieveEnergy(receptor.fromDir.getOpposite(), canTransmit);
                transmitted += used;
                canTransmit -= used;
            }
            if (canTransmit <= 0) {
                break;
            }

            if (!receptors.isEmpty() && !receptorIterator.hasNext()) {
                receptorIterator = receptors.listIterator();
            }
            appliedCount++;
        }
        setEnergyStored(getEnergyStored() - transmitted);

        return transmitted > 0;
    }

    // RF Power

    @Override
    public int receiveEnergy(ForgeDirection from, int maxReceive, boolean simulate) {
        if (getModeForChannel(SubChannel.POWER) != IoMode.RECIEVE) {
            return PowerHandlerUtil.recieveInternal(this, maxReceive, from, simulate);
        }
        return 0;
    }

    @Override
    public int extractEnergy(ForgeDirection from, int maxExtract, boolean simulate) {
        return 0;
    }

    @Override
    public boolean canConnectEnergy(ForgeDirection from) {
        return true;
    }

    @Override
    public int getEnergyStored(ForgeDirection from) {
        return storedEnergyRF;
    }

    @Override
    public int getMaxEnergyStored(ForgeDirection from) {
        return getMaxEnergyStored();
    }

    @Override
    public int getMaxEnergyRecieved(ForgeDirection dir) {
        if (getModeForChannel(SubChannel.POWER) == IoMode.RECIEVE) {
            return 0;
        }
        return internalCapacitor.getMaxEnergyReceived();
    }

    @Override
    public int getEnergyStored() {
        return storedEnergyRF;
    }

    @Override
    public int getMaxEnergyStored() {
        return internalCapacitor.getMaxEnergyStored();
    }

    @Override
    public void setEnergyStored(int stored) {
        storedEnergyRF = MathHelper.clamp_int(stored, 0, getMaxEnergyStored());
    }

    @Override
    public int fill(ForgeDirection from, FluidStack resource, boolean doFill) {
        if (!canSendFluid()) {
            return 0;
        }
        FluidStack in = resource.copy();
        int result = 0;
        for (NetworkFluidHandler h : getNetworkHandlers()) {
            if (h.node.canRecieveFluid() && h.handler.canFill(h.dirOp, in.getFluid())) {
                int filled = h.handler.fill(h.dirOp, in, doFill);
                in.amount -= filled;
                result += filled;
            }
        }
        if (doFill) {
            milliBucketsTransfered += result;
        }
        return result;
    }

    private void updatePowersReceptors() {
        if (!receptorsDirty) {
            return;
        }
        receptors.clear();
        BlockCoord myLoc = new BlockCoord(this);
        for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
            BlockCoord checkLoc = myLoc.getLocation(dir);
            TileEntity te = worldObj.getTileEntity(checkLoc.x, checkLoc.y, checkLoc.z);
            IPowerInterface pi = PowerHandlerUtil.create(te);
            if (pi != null) {
                receptors.add(new Receptor(pi, dir));
            }
        }
        receptorIterator = receptors.listIterator();
        receptorsDirty = false;
    }

    // ----------------------- Fluids -----------------------------------------------

    @Override
    public FluidStack drain(ForgeDirection from, FluidStack resource, boolean doDrain) {
        if (!canRecieveFluid() || resource == null) {
            return null;
        }

        FluidStack in = resource.copy();
        FluidStack result = null;
        for (NetworkFluidHandler h : getNetworkHandlers()) {
            if (h.node.canSendFluid() && h.handler.canDrain(h.dirOp, in.getFluid())) {
                FluidStack res = h.handler.drain(h.dirOp, in, false);
                if (res != null) {
                    if (result == null) {
                        result = res.copy();
                        if (doDrain) {
                            h.handler.drain(h.dirOp, in, true);
                        }

                    } else if (result.isFluidEqual(res)) {
                        result.amount += res.amount;
                        if (doDrain) {
                            h.handler.drain(h.dirOp, in, true);
                        }
                        in.amount -= res.amount;
                    }
                }
            }
        }
        return result;
    }

    @Override
    public FluidStack drain(ForgeDirection from, int maxDrainIn, boolean doDrain) {
        if (!canRecieveFluid()) {
            return null;
        }
        int maxDrain = maxDrainIn;
        FluidStack result = null;
        for (NetworkFluidHandler h : getNetworkHandlers()) {
            if (h.node.canSendFluid()) {
                FluidStack res = h.handler.drain(h.dirOp, maxDrain, false);
                if (res != null) {
                    if (result == null) {
                        result = res.copy();
                        if (doDrain) {
                            h.handler.drain(h.dirOp, maxDrain, true);
                        }
                        maxDrain -= res.amount;
                    } else if (result.isFluidEqual(res)) {
                        result.amount += res.amount;
                        if (doDrain) {
                            h.handler.drain(h.dirOp, maxDrain, true);
                        }
                        maxDrain -= res.amount;
                    }
                }
            }
        }
        return result;
    }

    @Override
    public boolean canFill(ForgeDirection from, Fluid fluid) {
        if (!canSendFluid()) {
            return false;
        }
        for (NetworkFluidHandler h : getNetworkHandlers()) {
            if (h.node.canRecieveFluid()) {
                if (h.handler.canFill(h.dirOp, fluid)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean canDrain(ForgeDirection from, Fluid fluid) {
        if (!canRecieveFluid()) {
            return false;
        }
        for (NetworkFluidHandler h : getNetworkHandlers()) {
            if (h.node.canSendFluid()) {
                if (h.handler.canDrain(h.dirOp, fluid)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public FluidTankInfo[] getTankInfo(ForgeDirection from) {
        List<FluidTankInfo> res = new ArrayList<FluidTankInfo>();
        for (NetworkFluidHandler h : getNetworkHandlers()) {
            FluidTankInfo[] ti = h.handler.getTankInfo(h.dirOp);
            if (ti != null) {
                for (FluidTankInfo t : ti) {
                    if (t != null) {
                        res.add(t);
                    }
                }
            }
        }
        if (res.isEmpty()) {
            return new FluidTankInfo[] {new FluidTankInfo(null, 0)};
        } else {
            return res.toArray(new FluidTankInfo[res.size()]);
        }
    }

    private List<NetworkFluidHandler> getNetworkHandlers() {
        if (HyperCubeRegister.instance == null || !redstoneCheckPassed) {
            return Collections.emptyList();
        }
        List<TileHyperCube> cubes = HyperCubeRegister.instance.getCubesForChannel(channel);
        if (cubes == null || cubes.isEmpty()) {
            return Collections.emptyList();
        }
        List<NetworkFluidHandler> result = new ArrayList<NetworkFluidHandler>();
        for (TileHyperCube cube : cubes) {
            if (cube != this && cube != null) {
                List<NetworkFluidHandler> handlers = cube.fluidHandlers;
                if (handlers != null && !handlers.isEmpty()) {
                    result.addAll(handlers);
                }
            }
        }
        return result;
    }

    private void updateFluidHandlers() {
        if (!fluidHandlersDirty) {
            return;
        }
        fluidHandlers.clear();
        if (isConnected) {
            BlockCoord myLoc = new BlockCoord(this);
            for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
                BlockCoord checkLoc = myLoc.getLocation(dir);
                TileEntity te = worldObj.getTileEntity(checkLoc.x, checkLoc.y, checkLoc.z);
                if (te instanceof IFluidHandler && !(te instanceof TileHyperCube)) {
                    IFluidHandler fh = (IFluidHandler) te;
                    fluidHandlers.add(new NetworkFluidHandler(this, fh, dir));
                }
            }
            fluidHandlersDirty = false;
        }
    }

    // ------- Item / Inventory ---------------------------------------------------------------------

    public ItemRecieveBuffer getRecieveBuffer() {
        return recieveBuffer;
    }

    private void updateInventories() {

        recieveBuffer.setRecieveEnabled(canSendItems());

        if (!inventoriesDirty) {
            return;
        }

        localInventory = new CompositeInventory();

        BlockCoord myLoc = new BlockCoord(this);
        for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
            BlockCoord checkLoc = myLoc.getLocation(dir);
            TileEntity te = worldObj.getTileEntity(checkLoc.x, checkLoc.y, checkLoc.z);
            if (te instanceof IInventory && !(te instanceof TileHyperCube)) {
                localInventory.addInventory((IInventory) te, dir);
            }
        }
        inventoriesDirty = false;
    }

    void pushRecieveBuffer() {

        if (recieveBuffer.isEmpty()) {
            return;
        }
        List<TileHyperCube> cubes = HyperCubeRegister.instance.getCubesForChannel(channel);
        if (cubes == null || cubes.isEmpty()) {
            return;
        }

        for (int i = 0; i < recieveBuffer.getSizeInventory(); i++) {
            ItemStack toPush = recieveBuffer.getStackInSlot(i);
            if (toPush != null) {
                for (TileHyperCube cube : cubes) {
                    if (toPush != null && cube != this && cube != null && cube.canRecieveItems()) {
                        toPush = cube.recieveItems(toPush);
                        recieveBuffer.getItems()[i] = toPush;
                    }
                }
            }
        }
    }

    private ItemStack recieveItems(ItemStack toPush) {
        if (toPush == null) {
            return null;
        }
        ItemStack result = toPush.copy();
        // TODO: need to cache this
        BlockCoord myLoc = new BlockCoord(this);
        for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
            BlockCoord checkLoc = myLoc.getLocation(dir);
            TileEntity te = worldObj.getTileEntity(checkLoc.x, checkLoc.y, checkLoc.z);
            result.stackSize -= ItemUtil.doInsertItem(te, result, dir.getOpposite());
            if (result.stackSize <= 0) {
                return null;
            }
        }
        return result;
    }

    private ISidedInventory getRemoteInventory() {

        CompositeInventory res = new CompositeInventory();
        res.addInventory(recieveBuffer, ForgeDirection.UNKNOWN);

        if (!canSendItems()) {
            return res;
        }

        if (HyperCubeRegister.instance == null) {
            return res;
        }
        List<TileHyperCube> cubes = HyperCubeRegister.instance.getCubesForChannel(channel);
        if (cubes == null || cubes.isEmpty()) {
            return res;
        }
        for (TileHyperCube cube : cubes) {
            if (cube != this && cube != null && cube.canRecieveItems()) {
                if (cube.inventoriesDirty) {
                    cube.updateInventories();
                }
                res.addInventory(cube.localInventory);
            }
        }

        return res;
    }

    @Override
    public int getSizeInventory() {
        return getRemoteInventory().getSizeInventory();
    }

    @Override
    public boolean hasCustomInventoryName() {
        return false;
    }

    @Override
    public ItemStack getStackInSlot(int i) {
        return getRemoteInventory().getStackInSlot(i);
    }

    @Override
    public ItemStack decrStackSize(int i, int j) {
        return getRemoteInventory().decrStackSize(i, j);
    }

    @Override
    public void setInventorySlotContents(int i, ItemStack itemstack) {
        getRemoteInventory().setInventorySlotContents(i, itemstack);
    }

    @Override
    public int[] getAccessibleSlotsFromSide(int var1) {
        return getRemoteInventory().getAccessibleSlotsFromSide(var1);
    }

    @Override
    public boolean canInsertItem(int i, ItemStack itemstack, int j) {
        return getRemoteInventory().canInsertItem(i, itemstack, j);
    }

    @Override
    public boolean canExtractItem(int i, ItemStack itemstack, int j) {
        return getRemoteInventory().canExtractItem(i, itemstack, j);
    }

    @Override
    public boolean isItemValidForSlot(int i, ItemStack itemstack) {
        return getRemoteInventory().isItemValidForSlot(i, itemstack);
    }

    @Override
    public String getInventoryName() {
        return ModObject.blockHyperCube.unlocalisedName;
    }

    @Override
    public int getInventoryStackLimit() {
        return 64;
    }

    @Override
    public boolean isUseableByPlayer(EntityPlayer entityplayer) {
        return false;
    }

    @Override
    public void openInventory() {}

    @Override
    public void closeInventory() {}

    @Override
    public ItemStack getStackInSlotOnClosing(int i) {
        return null;
    }

    // ---- Serialisation ---------------------------------------------------------

    @Override
    public void readCustomNBT(NBTTagCompound nbtRoot) {

        int energy;
        if (nbtRoot.hasKey("storedEnergy")) {
            energy = (int) (nbtRoot.getFloat("storedEnergy") * 10);
        } else {
            energy = nbtRoot.getInteger("storedEnergyRF");
        }
        setEnergyStored(energy);

        String channelName = nbtRoot.getString("channelName");
        UUID channelUser = PlayerUtil.getPlayerUIDUnstable(nbtRoot.getString("channelUser"));
        if (channelName != null && !channelName.isEmpty()) {
            channel = new Channel(channelName, channelUser);
        } else {
            channel = null;
        }

        owner = PlayerUtil.getPlayerUIDUnstable(nbtRoot.getString("owner"));

        for (SubChannel subChannel : SubChannel.values()) {
            String key = "subChannel" + subChannel.ordinal();
            if (nbtRoot.hasKey(key)) {
                setModeForChannel(subChannel, IoMode.values()[nbtRoot.getShort(key)]);
            }
        }

        recieveBuffer.readFromNBT(nbtRoot);

        if (nbtRoot.hasKey("rsMode")) {
            redstoneControlMode = RedstoneControlMode.values()[nbtRoot.getShort("rsMode")];
        } else {
            redstoneControlMode = RedstoneControlMode.IGNORE;
        }
    }

    @Override
    public void writeCustomNBT(NBTTagCompound nbtRoot) {
        nbtRoot.setInteger("storedEnergyRF", storedEnergyRF);
        if (channel != null) {
            nbtRoot.setString("channelName", channel.name);
            if (channel.user != null) {
                nbtRoot.setString("channelUser", channel.user.toString());
            }
        }
        if (owner != null) {
            nbtRoot.setString("owner", owner.toString());
        }

        for (SubChannel subChannel : SubChannel.values()) {
            IoMode mode = getModeForChannel(subChannel);
            nbtRoot.setShort("subChannel" + subChannel.ordinal(), (short) mode.ordinal());
        }
        if (redstoneControlMode != null) {
            nbtRoot.setShort("rsMode", (short) redstoneControlMode.ordinal());
        }
        recieveBuffer.writeToNBT(nbtRoot);
    }

    static class Receptor {
        IPowerInterface receptor;
        ForgeDirection fromDir;

        private Receptor(IPowerInterface rec, ForgeDirection fromDir) {
            receptor = rec;
            this.fromDir = fromDir;
        }
    }

    static class NetworkFluidHandler {
        final TileHyperCube node;
        final IFluidHandler handler;
        final ForgeDirection dir;
        final ForgeDirection dirOp;

        private NetworkFluidHandler(TileHyperCube node, IFluidHandler handler, ForgeDirection dir) {
            this.node = node;
            this.handler = handler;
            this.dir = dir;
            dirOp = dir.getOpposite();
        }
    }

    @Override
    public boolean displayPower() {
        return true;
    }
}
