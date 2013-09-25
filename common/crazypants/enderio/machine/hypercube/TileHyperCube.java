package crazypants.enderio.machine.hypercube;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.packet.Packet;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;
import buildcraft.api.power.IPowerReceptor;
import buildcraft.api.power.PowerHandler;
import buildcraft.api.power.PowerHandler.PowerReceiver;
import buildcraft.api.power.PowerHandler.Type;
import crazypants.enderio.Config;
import crazypants.enderio.PacketHandler;
import crazypants.enderio.machine.RedstoneControlMode;
import crazypants.enderio.power.BasicCapacitor;
import crazypants.enderio.power.IInternalPowerReceptor;
import crazypants.enderio.power.PowerHandlerUtil;
import crazypants.util.BlockCoord;
import crazypants.vecmath.VecmathUtil;

public class TileHyperCube extends TileEntity implements IInternalPowerReceptor, IFluidHandler {

  private static final float ENERGY_LOSS = (float) Config.transceiverEnergyLoss;

  private static final float ENERGY_UPKEEP = (float) Config.transceiverUpkeepCost;

  private static final float MILLIBUCKET_TRANSMISSION_COST = (float) Config.transceiverBucketTransmissionCost / 1000F;

  private RedstoneControlMode inputControlMode = RedstoneControlMode.IGNORE;

  private RedstoneControlMode outputControlMode = RedstoneControlMode.IGNORE;

  private boolean powerOutputEnabled = true;

  private boolean powerInputEnabled = true;

  private final BasicCapacitor internalCapacitor = new BasicCapacitor(Config.transceiverMaxIO, 25000);

  PowerHandler powerHandler;

  private float lastSyncPowerStored = 0;

  private final List<Receptor> receptors = new ArrayList<Receptor>();
  private ListIterator<Receptor> receptorIterator = receptors.listIterator();
  private boolean receptorsDirty = true;

  private final List<NetworkFluidHandler> fluidHandlers = new ArrayList<NetworkFluidHandler>();
  private boolean fluidHandlersDirty = true;

  private PowerHandler disabledPowerHandler;

  private Channel channel = null;
  private Channel registeredChannel = null;
  private String owner;

  private boolean init = true;

  private float milliBucketsTransfered = 0;

  public TileHyperCube() {
    powerHandler = PowerHandlerUtil.createHandler(internalCapacitor, this, Type.STORAGE);
  }

  public RedstoneControlMode getInputControlMode() {
    return inputControlMode;
  }

  public void setInputControlMode(RedstoneControlMode powerInputControlMode) {
    this.inputControlMode = powerInputControlMode;
  }

  public RedstoneControlMode getOutputControlMode() {
    return outputControlMode;
  }

  public void setOutputControlMode(RedstoneControlMode powerOutputControlMode) {
    this.outputControlMode = powerOutputControlMode;
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
    if(channel == null || HyperCubeRegister.instance == null) {
      return false;
    }
    List<TileHyperCube> cons = HyperCubeRegister.instance.getCubesForChannel(channel);
    return cons != null && cons.size() > 1;
  }

  private void balanceCubeNetworkEnergy() {

    List<TileHyperCube> cubes = HyperCubeRegister.instance.getCubesForChannel(channel);
    if(cubes == null || cubes.isEmpty()) {
      return;
    }
    float totalEnergy = 0;
    for (TileHyperCube cube : cubes) {
      totalEnergy += cube.powerHandler.getEnergyStored();
    }

    float energyPerNode = totalEnergy / cubes.size();
    float totalToTranfer = 0;
    for (TileHyperCube cube : cubes) {
      if(cube.powerHandler.getEnergyStored() < energyPerNode) {
        totalToTranfer += (energyPerNode - cube.powerHandler.getEnergyStored());
      }
    }

    float totalLoss = totalToTranfer * ENERGY_LOSS;
    totalEnergy -= totalLoss;
    energyPerNode = totalEnergy / cubes.size();

    for (TileHyperCube cube : cubes) {
      cube.powerHandler.setEnergy(energyPerNode);
    }

  }

  @Override
  public void onChunkUnload() {
    if(HyperCubeRegister.instance != null) {
      HyperCubeRegister.instance.deregister(this);
    }
  }

  public void onNeighborBlockChange() {
    receptorsDirty = true;
    fluidHandlersDirty = true;
  }

  @Override
  public void updateEntity() {
    if(worldObj == null) { // sanity check
      return;
    }
    if(worldObj.isRemote) {
      return;
    } // else is server, do all logic only on the server

    updateFluidHandlers();

    // do the required tick to keep BC API happy
    float stored = powerHandler.getEnergyStored();
    powerHandler.update();
    // do a dummy recieve of power to force the updating of what is an isn't a
    // power source as we rely on this
    // to make sure we dont both send and recieve to the same source
    powerHandler.getPowerReceiver().receiveEnergy(Type.STORAGE, 1, null);

    //Pay upkeep cost
    stored -= ENERGY_UPKEEP;
    //Pay fluid transmission cost
    stored -= (MILLIBUCKET_TRANSMISSION_COST * milliBucketsTransfered);

    milliBucketsTransfered = 0;

    Math.max(stored, 0);

    powerHandler.setEnergy(stored);

    if(registeredChannel == null ? channel != null : !registeredChannel.equals(channel)) {
      if(registeredChannel != null) {
        HyperCubeRegister.instance.deregister(this, registeredChannel);
      }
      HyperCubeRegister.instance.register(this);
      registeredChannel = channel;
    }

    balanceCubeNetworkEnergy();

    boolean requiresClientSync = false;
    powerInputEnabled = RedstoneControlMode.isConditionMet(inputControlMode, this);
    powerOutputEnabled = RedstoneControlMode.isConditionMet(outputControlMode, this);

    if(powerOutputEnabled) {
      transmitEnergy();
    }

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

  private boolean transmitEnergy() {

    if(powerHandler.getEnergyStored() <= 0) {
      return false;
    }
    float canTransmit = Math.min(powerHandler.getEnergyStored(), internalCapacitor.getMaxEnergyExtracted());
    float transmitted = 0;

    checkReceptors();

    if(!receptors.isEmpty() && !receptorIterator.hasNext()) {
      receptorIterator = receptors.listIterator();
    }

    int appliedCount = 0;
    int numReceptors = receptors.size();
    while (receptorIterator.hasNext() && canTransmit > 0 && appliedCount < numReceptors) {

      Receptor receptor = receptorIterator.next();
      PowerReceiver pp = receptor.receptor.getPowerReceiver(receptor.fromDir.getOpposite());
      if(pp != null && pp.getMinEnergyReceived() <= canTransmit && pp.getType() != Type.ENGINE && !powerHandler.isPowerSource(receptor.fromDir)) {
        float used;
        float boundCanTransmit = Math.min(canTransmit, pp.getMaxEnergyReceived());
        if(boundCanTransmit > 0) {
          if(receptor.receptor instanceof IInternalPowerReceptor) {
            used = PowerHandlerUtil.transmitInternal((IInternalPowerReceptor) receptor.receptor, pp, boundCanTransmit, Type.STORAGE,
                receptor.fromDir.getOpposite());
          } else {
            used = pp.receiveEnergy(Type.STORAGE, boundCanTransmit, receptor.fromDir.getOpposite());
          }
          transmitted += used;
          canTransmit -= used;
        }
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
    if(powerInputEnabled) {
      return powerHandler;
    }
    return getDisabledPowerHandler();
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

  @Override
  public void doWork(PowerHandler workProvider) {
  }

  @Override
  public void applyPerdition() {
  }

  private void checkReceptors() {
    if(!receptorsDirty) {
      return;
    }
    receptors.clear();
    BlockCoord myLoc = new BlockCoord(this);
    for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
      BlockCoord checkLoc = myLoc.getLocation(dir);
      TileEntity te = worldObj.getBlockTileEntity(checkLoc.x, checkLoc.y, checkLoc.z);
      if(te instanceof IPowerReceptor) {
        IPowerReceptor rec = (IPowerReceptor) te;
        receptors.add(new Receptor((IPowerReceptor) te, dir));
      }
    }

    receptorIterator = receptors.listIterator();
    receptorsDirty = false;
  }

  @Override
  public int fill(ForgeDirection from, FluidStack resource, boolean doFill) {
    if(!powerInputEnabled) {
      return 0;
    }
    FluidStack in = resource.copy();
    int result = 0;
    for (NetworkFluidHandler h : getNetworkHandlers()) {
      if(h.node.powerOutputEnabled && h.handler.canFill(h.dirOp, in.getFluid())) {
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

  @Override
  public FluidStack drain(ForgeDirection from, FluidStack resource, boolean doDrain) {
    if(!powerOutputEnabled || resource == null) {
      return null;
    }

    FluidStack in = resource.copy();
    FluidStack result = null;
    for (NetworkFluidHandler h : getNetworkHandlers()) {
      if(h.node.powerInputEnabled && h.handler.canDrain(h.dirOp, in.getFluid())) {
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
    if(!powerOutputEnabled) {
      return null;
    }
    int maxDrain = maxDrainIn;
    FluidStack result = null;
    for (NetworkFluidHandler h : getNetworkHandlers()) {
      if(h.node.powerInputEnabled) {
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
    if(!powerInputEnabled) {
      return false;
    }
    for (NetworkFluidHandler h : getNetworkHandlers()) {
      if(h.node.powerOutputEnabled) {
        if(h.handler.canFill(h.dirOp, fluid)) {
          return true;
        }
      }
    }
    return false;
  }

  @Override
  public boolean canDrain(ForgeDirection from, Fluid fluid) {
    if(!powerOutputEnabled) {
      return false;
    }
    for (NetworkFluidHandler h : getNetworkHandlers()) {
      if(h.node.powerInputEnabled) {
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

    List<TileHyperCube> cubes = HyperCubeRegister.instance.getCubesForChannel(channel);
    if(cubes == null || cubes.isEmpty()) {
      return Collections.emptyList();
    }
    List<NetworkFluidHandler> result = new ArrayList<NetworkFluidHandler>();
    for (TileHyperCube cube : cubes) {
      if(cube != this) {
        result.addAll(cube.fluidHandlers);
      }
    }
    return result;

  }

  private void updateFluidHandlers() {
    if(!fluidHandlersDirty) {
      return;
    }
    fluidHandlers.clear();
    BlockCoord myLoc = new BlockCoord(this);
    for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
      BlockCoord checkLoc = myLoc.getLocation(dir);
      TileEntity te = worldObj.getBlockTileEntity(checkLoc.x, checkLoc.y, checkLoc.z);
      if(te instanceof IFluidHandler) {
        IFluidHandler fh = (IFluidHandler) te;
        fluidHandlers.add(new NetworkFluidHandler(this, fh, dir));
      }
    }
    fluidHandlersDirty = false;
  }

  @Override
  public void readFromNBT(NBTTagCompound nbtRoot) {
    super.readFromNBT(nbtRoot);
    powerHandler.setEnergy(nbtRoot.getFloat("storedEnergy"));
    inputControlMode = RedstoneControlMode.values()[nbtRoot.getShort("inputControlMode")];
    outputControlMode = RedstoneControlMode.values()[nbtRoot.getShort("outputControlMode")];

    String channelName = nbtRoot.getString("channelName");
    String channelUser = nbtRoot.getString("channelUser");
    if(channelName != null && !channelName.isEmpty()) {
      channel = new Channel(channelName, channelUser.isEmpty() ? null : channelUser);
    } else {
      channel = null;
    }

    owner = nbtRoot.getString("owner");
  }

  @Override
  public void writeToNBT(NBTTagCompound nbtRoot) {
    super.writeToNBT(nbtRoot);
    nbtRoot.setFloat("storedEnergy", powerHandler.getEnergyStored());
    nbtRoot.setShort("inputControlMode", (short) inputControlMode.ordinal());
    nbtRoot.setShort("outputControlMode", (short) outputControlMode.ordinal());
    if(channel == null) {
      nbtRoot.setString("channelName", "");
      nbtRoot.setString("channelUser", "");
    } else {
      nbtRoot.setString("channelName", channel.name);
      nbtRoot.setString("channelUser", channel.user == null ? "" : channel.user);
    }
    nbtRoot.setString("owner", owner);
  }

  @Override
  public Packet getDescriptionPacket() {
    return PacketHandler.getPacket(this);
  }

  static class Receptor {
    IPowerReceptor receptor;
    ForgeDirection fromDir;

    private Receptor(IPowerReceptor rec, ForgeDirection fromDir) {
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
