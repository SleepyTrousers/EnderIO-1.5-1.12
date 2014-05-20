package crazypants.enderio.machine.hypercube;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.ListIterator;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.packet.Packet;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;
import buildcraft.api.power.PowerHandler;
import buildcraft.api.power.PowerHandler.PowerReceiver;
import buildcraft.api.power.PowerHandler.Type;
import crazypants.enderio.Config;
import crazypants.enderio.ModObject;
import crazypants.enderio.PacketHandler;
import crazypants.enderio.machine.RedstoneControlMode;
import crazypants.enderio.power.BasicCapacitor;
import crazypants.enderio.power.IInternalPowerReceptor;
import crazypants.enderio.power.IPowerInterface;
import crazypants.enderio.power.PowerHandlerUtil;
import crazypants.util.BlockCoord;
import crazypants.util.ItemUtil;
import crazypants.util.Lang;
import crazypants.vecmath.VecmathUtil;

public class TileHyperCube extends TileEntity implements IInternalPowerReceptor, IFluidHandler, ISidedInventory {

  private static final float ENERGY_LOSS = (float) Config.transceiverEnergyLoss;

  private static final float ENERGY_UPKEEP = (float) Config.transceiverUpkeepCost;

  private static final float MILLIBUCKET_TRANSMISSION_COST = (float) Config.transceiverBucketTransmissionCost / 1000F;

  public static enum IoMode {

    SEND("gui.send"),
    RECIEVE("gui.recieve"),
    BOTH("gui.sendRecieve"),
    NEITHER("gui.disabled");

    public static IoMode next(IoMode mode) {
      int index = mode.ordinal() + 1;
      if(index >= values().length) {
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
      return Lang.localize(unlocalisedName);
    }
  }

  public static enum SubChannel {
    POWER,
    FLUID,
    ITEM
  }

  private final BasicCapacitor internalCapacitor = new BasicCapacitor(Config.transceiverMaxIO, 25000);

  PowerHandler powerHandler;

  private float lastSyncPowerStored = 0;

  private final List<Receptor> receptors = new ArrayList<Receptor>();
  private ListIterator<Receptor> receptorIterator = receptors.listIterator();
  private boolean receptorsDirty = true;

  private final List<NetworkFluidHandler> fluidHandlers = new ArrayList<NetworkFluidHandler>();
  private boolean fluidHandlersDirty = true;

  private CompositeInventory localInventory = new CompositeInventory();
  private boolean inventoriesDirty = true;

  private PowerHandler disabledPowerHandler;

  private Channel channel = null;
  private Channel registeredChannel = null;
  private String owner;

  private boolean init = true;

  private float milliBucketsTransfered = 0;

  private EnumMap<SubChannel, IoMode> ioModes = new EnumMap<TileHyperCube.SubChannel, TileHyperCube.IoMode>(SubChannel.class);

  private ItemRecieveBuffer recieveBuffer;

  protected RedstoneControlMode redstoneControlMode = RedstoneControlMode.IGNORE;
  protected boolean redstoneCheckPassed;
  private boolean redstoneStateDirty = true;

  public TileHyperCube() {
    powerHandler = PowerHandlerUtil.createHandler(internalCapacitor, this, Type.STORAGE);
    redstoneControlMode = RedstoneControlMode.IGNORE;
    recieveBuffer = new ItemRecieveBuffer(this);
  }

  public RedstoneControlMode getRedstoneControlMode() {
    return redstoneControlMode;
  }

  public void setRedstoneControlMode(RedstoneControlMode redstoneControlMode) {
    this.redstoneControlMode = redstoneControlMode;
    redstoneStateDirty = true;
  }

  public IoMode getModeForChannel(SubChannel channel) {
    IoMode mode = ioModes.get(channel);
    if(mode == null) {
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
    return VecmathUtil.clamp(Math.round(scale * (powerHandler.getEnergyStored() / powerHandler.getMaxEnergyStored())), 0, scale);
  }

  public void onBreakBlock() {
    HyperCubeRegister.instance.deregister(this);
  }

  public void onBlockAdded() {
    HyperCubeRegister.instance.register(this);
  }

  public void setOwner(String owner) {
    this.owner = owner;
  }

  private boolean isConnected() {
    if(channel == null || HyperCubeRegister.instance == null || !redstoneCheckPassed) {
      return false;
    }
    List<TileHyperCube> cons = HyperCubeRegister.instance.getCubesForChannel(channel);
    return cons != null && cons.size() > 1 && powerHandler.getEnergyStored() > 0;
  }

  private void sendEnergyToOtherNodes() {

    List<TileHyperCube> cubes = HyperCubeRegister.instance.getCubesForChannel(channel);
    if(cubes == null || cubes.isEmpty()) {
      return;
    }

    if(canSendPower()) {

      boolean iWasConnected = isConnected();

      for (TileHyperCube cube : cubes) {
        float stored = powerHandler.getEnergyStored();
        if(stored > 0 && cube != null && cube != this && cube.canRecievePower()) {
          boolean wasConnected = cube.isConnected();

          float curPower = cube.powerHandler.getEnergyStored();
          float requires = cube.powerHandler.getMaxEnergyStored() - curPower;
          float transfer = Math.min(requires, stored);
          transfer = Math.min(transfer, Config.transceiverMaxIO);
          cube.powerHandler.setEnergy(curPower + ((1 - ENERGY_LOSS) * transfer));
          powerHandler.setEnergy(powerHandler.getEnergyStored() - transfer);
          if(wasConnected != cube.isConnected()) {
            cube.fluidHandlersDirty = true;
          }
        }
      }

      if(iWasConnected != isConnected()) {
        fluidHandlersDirty = true;
      }

    }

  }

  @Override
  public void onChunkUnload() {
    if(HyperCubeRegister.instance != null) {
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
  public void updateEntity() {
    if(worldObj == null) { // sanity check
      return;
    }
    if(worldObj.isRemote) {
      return;
    } // else is server, do all logic only on the server

    // do the required tick to keep BC API happy
    float stored = powerHandler.getEnergyStored();
    powerHandler.update();
    // do a dummy recieve of power to force the updating of what is an isn't a
    // power source as we rely on this
    // to make sure we dont both send and recieve to the same source
    powerHandler.getPowerReceiver().receiveEnergy(Type.STORAGE, 1, null);

    boolean wasConnected = isConnected();

    // Pay upkeep cost
    stored -= ENERGY_UPKEEP;
    // Pay fluid transmission cost
    stored -= (MILLIBUCKET_TRANSMISSION_COST * milliBucketsTransfered);

    // update power status
    stored = Math.max(stored, 0);
    powerHandler.setEnergy(stored);

    milliBucketsTransfered = 0;

    boolean prevRedCheck = redstoneCheckPassed;
    if(redstoneStateDirty) {
      redstoneCheckPassed = RedstoneControlMode.isConditionMet(redstoneControlMode, this);
      redstoneStateDirty = false;
    }

    if(!redstoneCheckPassed) {
      if(registeredChannel != null) {
        HyperCubeRegister.instance.deregister(this, registeredChannel);
        registeredChannel = null;
      }

    }

    transmitEnergy();
    sendEnergyToOtherNodes();

    updateInventories();
    pushRecieveBuffer();

    // check we are still connected (i.e. we haven't run out of power or started
    // receiving power)
    boolean stillConnected = isConnected();
    if(wasConnected != stillConnected) {
      fluidHandlersDirty = true;
    }
    updateFluidHandlers();

    if(redstoneCheckPassed && (registeredChannel == null ? channel != null : !registeredChannel.equals(channel))) {
      if(registeredChannel != null) {
        HyperCubeRegister.instance.deregister(this, registeredChannel);
      }
      HyperCubeRegister.instance.register(this);
      registeredChannel = channel;
    }

    boolean requiresClientSync = wasConnected != stillConnected;
    requiresClientSync |= prevRedCheck != redstoneCheckPassed;

    float storedEnergy = powerHandler.getEnergyStored();
    // Update if our power has changed by more than 0.5%
    requiresClientSync |= lastSyncPowerStored != storedEnergy && worldObj.getTotalWorldTime() % 21 == 0;

    if(requiresClientSync) {
      lastSyncPowerStored = storedEnergy;
      // this will cause 'getPacketDescription()' to be called and its result
      // will be sent to the PacketHandler on the other end of
      // client/server connection
      worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
      // And this will make sure our current tile entity state is saved
      onInventoryChanged();
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

  //-------------------------- Power -----------------------------------------------

  private boolean transmitEnergy() {

    if(!getModeForChannel(SubChannel.POWER).isRecieveEnabled() || !redstoneCheckPassed || powerHandler.getEnergyStored() <= 0) {
      return false;
    }

    float canTransmit = Math.min(powerHandler.getEnergyStored(), internalCapacitor.getMaxEnergyExtracted());
    float transmitted = 0;

    updatePowersReceptors();

    if(!receptors.isEmpty() && !receptorIterator.hasNext()) {
      receptorIterator = receptors.listIterator();
    }

    int appliedCount = 0;
    int numReceptors = receptors.size();
    while (receptorIterator.hasNext() && canTransmit > 0 && appliedCount < numReceptors) {
      Receptor receptor = receptorIterator.next();
      IPowerInterface pp = receptor.receptor;
      if(pp != null && pp.getMinEnergyReceived(receptor.fromDir.getOpposite()) <= canTransmit) {
        float used = pp.recieveEnergy(receptor.fromDir.getOpposite(), canTransmit);
        transmitted += used;
        canTransmit -= used;
      }
      if(canTransmit <= 0) {
        break;
      }

      if(!receptors.isEmpty() && !receptorIterator.hasNext()) {
        receptorIterator = receptors.listIterator();
      }
      appliedCount++;
    }
    powerHandler.setEnergy(powerHandler.getEnergyStored() - transmitted);

    return transmitted > 0;

  }

  @Override
  public PowerReceiver getPowerReceiver(ForgeDirection side) {
    return getPowerHandler().getPowerReceiver();
  }

  @Override
  public World getWorld() {
    return worldObj;
  }

  @Override
  public PowerHandler getPowerHandler() {
    if(getModeForChannel(SubChannel.POWER) == IoMode.RECIEVE) {
      return getDisabledPowerHandler();
    }
    return powerHandler;
  }

  public PowerHandler getInternalPowerHandler() {
    return powerHandler;
  }

  private PowerHandler getDisabledPowerHandler() {
    if(disabledPowerHandler == null) {
      disabledPowerHandler = PowerHandlerUtil.createHandler(new BasicCapacitor(0, 0), this, Type.STORAGE);
    }
    return disabledPowerHandler;
  }

  // RF Power

  @Override
  public int receiveEnergy(ForgeDirection from, int maxReceive, boolean simulate) {
    if(getModeForChannel(SubChannel.POWER) != IoMode.RECIEVE) {
      return PowerHandlerUtil.recieveRedstoneFlux(from, powerHandler, maxReceive, simulate);
    }
    return 0;
  }

  @Override
  public int extractEnergy(ForgeDirection from, int maxExtract, boolean simulate) {
    return 0;
  }

  @Override
  public boolean canInterface(ForgeDirection from) {
    return true;
  }

  @Override
  public int getEnergyStored(ForgeDirection from) {
    return (int) (powerHandler.getEnergyStored() * 10);
  }

  @Override
  public int getMaxEnergyStored(ForgeDirection from) {
    return (int) (powerHandler.getMaxEnergyStored() * 10);
  }

  @Override
  public void doWork(PowerHandler workProvider) {
  }

  @Override
  public void applyPerdition() {
  }

  @Override
  public int fill(ForgeDirection from, FluidStack resource, boolean doFill) {
    if(!canSendFluid()) {
      return 0;
    }
    FluidStack in = resource.copy();
    int result = 0;
    for (NetworkFluidHandler h : getNetworkHandlers()) {
      if(h.node.canRecieveFluid() && h.handler.canFill(h.dirOp, in.getFluid())) {
        int filled = h.handler.fill(h.dirOp, in, doFill);
        in.amount -= filled;
        result += filled;
      }
    }
    if(doFill) {
      milliBucketsTransfered += result;
    }
    return result;
  }

  private void updatePowersReceptors() {
    if(!receptorsDirty) {
      return;
    }
    receptors.clear();
    BlockCoord myLoc = new BlockCoord(this);
    for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
      BlockCoord checkLoc = myLoc.getLocation(dir);
      TileEntity te = worldObj.getBlockTileEntity(checkLoc.x, checkLoc.y, checkLoc.z);
      IPowerInterface pi = PowerHandlerUtil.create(te);
      if(pi != null) {
        receptors.add(new Receptor(pi, dir));
      }
    }
    receptorIterator = receptors.listIterator();
    receptorsDirty = false;
  }

  //----------------------- Fluids -----------------------------------------------

  @Override
  public FluidStack drain(ForgeDirection from, FluidStack resource, boolean doDrain) {
    if(!canRecieveFluid() || resource == null) {
      return null;
    }

    FluidStack in = resource.copy();
    FluidStack result = null;
    for (NetworkFluidHandler h : getNetworkHandlers()) {
      if(h.node.canSendFluid() && h.handler.canDrain(h.dirOp, in.getFluid())) {
        FluidStack res = h.handler.drain(h.dirOp, in, false);
        if(res != null) {
          if(result == null) {
            result = res.copy();
            if(doDrain) {
              h.handler.drain(h.dirOp, in, true);
            }

          } else if(result.isFluidEqual(res)) {
            result.amount += res.amount;
            if(doDrain) {
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
    if(!canRecieveFluid()) {
      return null;
    }
    int maxDrain = maxDrainIn;
    FluidStack result = null;
    for (NetworkFluidHandler h : getNetworkHandlers()) {
      if(h.node.canSendFluid()) {
        FluidStack res = h.handler.drain(h.dirOp, maxDrain, false);
        if(res != null) {
          if(result == null) {
            result = res.copy();
            if(doDrain) {
              h.handler.drain(h.dirOp, maxDrain, true);
            }
            maxDrain -= res.amount;
          } else if(result.isFluidEqual(res)) {
            result.amount += res.amount;
            if(doDrain) {
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
    if(!canSendFluid()) {
      return false;
    }
    for (NetworkFluidHandler h : getNetworkHandlers()) {
      if(h.node.canRecieveFluid()) {
        if(h.handler.canFill(h.dirOp, fluid)) {
          return true;
        }
      }
    }
    return false;
  }

  @Override
  public boolean canDrain(ForgeDirection from, Fluid fluid) {
    if(!canRecieveFluid()) {
      return false;
    }
    for (NetworkFluidHandler h : getNetworkHandlers()) {
      if(h.node.canSendFluid()) {
        if(h.handler.canDrain(h.dirOp, fluid)) {
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
      if(ti != null) {
        for (FluidTankInfo t : ti) {
          if(t != null) {
            res.add(t);
          }
        }
      }
    }
    return res.toArray(new FluidTankInfo[res.size()]);
  }

  private List<NetworkFluidHandler> getNetworkHandlers() {
    if(HyperCubeRegister.instance == null || !redstoneCheckPassed) {
      return Collections.emptyList();
    }
    List<TileHyperCube> cubes = HyperCubeRegister.instance.getCubesForChannel(channel);
    if(cubes == null || cubes.isEmpty()) {
      return Collections.emptyList();
    }
    List<NetworkFluidHandler> result = new ArrayList<NetworkFluidHandler>();
    for (TileHyperCube cube : cubes) {
      if(cube != this && cube != null) {
        List<NetworkFluidHandler> handlers = cube.fluidHandlers;
        if(handlers != null && !handlers.isEmpty()) {
          result.addAll(handlers);
        }
      }
    }
    return result;

  }

  private void updateFluidHandlers() {
    if(!fluidHandlersDirty) {
      return;
    }
    fluidHandlers.clear();
    if(isConnected()) {
      BlockCoord myLoc = new BlockCoord(this);
      for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
        BlockCoord checkLoc = myLoc.getLocation(dir);
        TileEntity te = worldObj.getBlockTileEntity(checkLoc.x, checkLoc.y, checkLoc.z);
        if(te instanceof IFluidHandler && !(te instanceof TileHyperCube)) {
          IFluidHandler fh = (IFluidHandler) te;
          fluidHandlers.add(new NetworkFluidHandler(this, fh, dir));
        }
      }
      fluidHandlersDirty = false;
    }
  }

  //------- Item / Inventory ---------------------------------------------------------------------

  public ItemRecieveBuffer getRecieveBuffer() {
    return recieveBuffer;
  }

  private void updateInventories() {

    recieveBuffer.setRecieveEnabled(canSendItems());

    if(!inventoriesDirty) {
      return;
    }

    localInventory = new CompositeInventory();

    BlockCoord myLoc = new BlockCoord(this);
    for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
      BlockCoord checkLoc = myLoc.getLocation(dir);
      TileEntity te = worldObj.getBlockTileEntity(checkLoc.x, checkLoc.y, checkLoc.z);
      if(te instanceof IInventory && !(te instanceof TileHyperCube)) {
        localInventory.addInventory((IInventory) te, dir);
      }
    }
    inventoriesDirty = false;

  }

  void pushRecieveBuffer() {

    if(recieveBuffer.isEmpty()) {
      return;
    }
    List<TileHyperCube> cubes = HyperCubeRegister.instance.getCubesForChannel(channel);
    if(cubes == null || cubes.isEmpty()) {
      return;
    }

    for (int i = 0; i < recieveBuffer.getSizeInventory(); i++) {
      ItemStack toPush = recieveBuffer.getStackInSlot(i);
      if(toPush != null) {
        for (TileHyperCube cube : cubes) {
          if(toPush != null && cube != this && cube != null && cube.canRecieveItems()) {
            toPush = cube.recieveItems(toPush);
            recieveBuffer.getItems()[i] = toPush;
          }
        }
      }
    }

  }

  private ItemStack recieveItems(ItemStack toPush) {
    if(toPush == null) {
      return null;
    }
    ItemStack result = toPush.copy();
    //TODO: need to cache this
    BlockCoord myLoc = new BlockCoord(this);
    for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
      BlockCoord checkLoc = myLoc.getLocation(dir);
      TileEntity te = worldObj.getBlockTileEntity(checkLoc.x, checkLoc.y, checkLoc.z);
      result.stackSize -= ItemUtil.doInsertItem(te, result, dir.getOpposite());
      if(result.stackSize <= 0) {
        return null;
      }
    }
    return result;

  }

  private ISidedInventory getRemoteInventory() {

    CompositeInventory res = new CompositeInventory();
    res.addInventory(recieveBuffer, ForgeDirection.UNKNOWN);

    if(!canSendItems()) {
      return res;
    }

    if(HyperCubeRegister.instance == null) {
      return res;
    }
    List<TileHyperCube> cubes = HyperCubeRegister.instance.getCubesForChannel(channel);
    if(cubes == null || cubes.isEmpty()) {
      return res;
    }
    for (TileHyperCube cube : cubes) {
      if(cube != this && cube != null && cube.canRecieveItems()) {
        if(cube.inventoriesDirty) {
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
  public String getInvName() {
    return ModObject.blockHyperCube.name;
  }

  @Override
  public boolean isInvNameLocalized() {
    return false;
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
  public void openChest() {
  }

  @Override
  public void closeChest() {
  }

  @Override
  public ItemStack getStackInSlotOnClosing(int i) {
    return null;
  }

  //---- Serialisation ---------------------------------------------------------

  @Override
  public void readFromNBT(NBTTagCompound nbtRoot) {
    super.readFromNBT(nbtRoot);
    powerHandler.setEnergy(nbtRoot.getFloat("storedEnergy"));
    String channelName = nbtRoot.getString("channelName");
    String channelUser = nbtRoot.getString("channelUser");
    if(channelName != null && !channelName.isEmpty()) {
      channel = new Channel(channelName, channelUser == null || channelUser.isEmpty() ? null : channelUser);
    } else {
      channel = null;
    }

    owner = nbtRoot.getString("owner");

    for (SubChannel subChannel : SubChannel.values()) {
      String key = "subChannel" + subChannel.ordinal();
      if(nbtRoot.hasKey(key)) {
        setModeForChannel(subChannel, IoMode.values()[nbtRoot.getShort(key)]);
      }
    }

    recieveBuffer.readFromNBT(nbtRoot);

    if(nbtRoot.hasKey("rsMode")) {
      redstoneControlMode = RedstoneControlMode.values()[nbtRoot.getShort("rsMode")];
    } else {
      redstoneControlMode = RedstoneControlMode.IGNORE;
    }
  }

  @Override
  public void writeToNBT(NBTTagCompound nbtRoot) {
    super.writeToNBT(nbtRoot);
    nbtRoot.setFloat("storedEnergy", powerHandler.getEnergyStored());
    if(channel != null) {
      nbtRoot.setString("channelName", channel.name);
      if(channel.user != null) {
        nbtRoot.setString("channelUser", channel.user);
      }
    }
    if(owner != null && !(owner.isEmpty())) {
      nbtRoot.setString("owner", owner);
    }

    for (SubChannel subChannel : SubChannel.values()) {
      IoMode mode = getModeForChannel(subChannel);
      nbtRoot.setShort("subChannel" + subChannel.ordinal(), (short) mode.ordinal());
    }
    if(redstoneControlMode != null) {
      nbtRoot.setShort("rsMode", (short) redstoneControlMode.ordinal());
    }
    recieveBuffer.writeToNBT(nbtRoot);
  }

  @Override
  public Packet getDescriptionPacket() {
    return PacketHandler.getPacket(this);
  }

  static class Receptor {
    IPowerInterface receptor;
    ForgeDirection fromDir;

    private Receptor(IPowerInterface rec, ForgeDirection fromDir) {
      this.receptor = rec;
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

}
