package crazypants.enderio.machine.hypercube;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.packet.Packet;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import buildcraft.api.power.IPowerReceptor;
import buildcraft.api.power.PowerHandler;
import buildcraft.api.power.PowerHandler.PowerReceiver;
import buildcraft.api.power.PowerHandler.Type;
import crazypants.enderio.PacketHandler;
import crazypants.enderio.machine.RedstoneControlMode;
import crazypants.enderio.power.BasicCapacitor;
import crazypants.enderio.power.IInternalPowerReceptor;
import crazypants.enderio.power.PowerHandlerUtil;
import crazypants.util.BlockCoord;

public class TileHyperCube extends TileEntity implements IInternalPowerReceptor {

  private static final float ENERGY_LOSS = 0.1f;

  private RedstoneControlMode inputControlMode = RedstoneControlMode.IGNORE;

  private RedstoneControlMode outputControlMode = RedstoneControlMode.IGNORE;

  private boolean powerOutputEnabled = true;

  private boolean powerInputEnabled = true;

  private final BasicCapacitor internalCapacitor = new BasicCapacitor(256, 25000);

  PowerHandler powerHandler;

  private float lastSyncPowerStored = 0;

  private final List<Receptor> receptors = new ArrayList<Receptor>();
  private ListIterator<Receptor> receptorIterator = receptors.listIterator();
  private boolean receptorsDirty = true;

  private PowerHandler disabledPowerHandler;
  
  private Channel channel = null;
  private Channel registeredChannel = null;
  private String owner;
  
  private boolean init = true;

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
    return (int) (scale * (powerHandler.getEnergyStored() / powerHandler.getMaxEnergyStored()));
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

  private void balanceCubeNetworkEnergy() {
    
    List<TileHyperCube> cubes = HyperCubeRegister.instance.getCubesForChannel(channel);
    if(cubes == null || cubes.isEmpty()) {
      return;
    }
    float totalEnergy = 0;
    for(TileHyperCube cube : cubes) {
      totalEnergy += cube.getPowerHandler().getEnergyStored();
    }
    
    float energyPerNode = totalEnergy / cubes.size();
    float totalToTranfer = 0;        
    for(TileHyperCube cube : cubes) {
      if(cube.getPowerHandler().getEnergyStored() < energyPerNode) {
        totalToTranfer += (energyPerNode - cube.getPowerHandler().getEnergyStored());
      }
    }
    
    float totalLoss = totalToTranfer * ENERGY_LOSS;   
    totalEnergy -= totalLoss;
    energyPerNode = totalEnergy / cubes.size();
    
    for(TileHyperCube cube : cubes) {
      cube.getPowerHandler().setEnergy(energyPerNode);      
    }
    
  }
  
  @Override
  public void onChunkUnload() {
    HyperCubeRegister.instance.deregister(this);
  }

  public void onNeighborBlockChange() {
    receptorsDirty = true;
  }

  @Override
  public void updateEntity() {
    if (worldObj == null) { // sanity check
      return;
    }
    if (worldObj.isRemote) {      
      return;
    } // else is server, do all logic only on the server

    // do the required tick to keep BC API happy
    float stored = powerHandler.getEnergyStored();
    powerHandler.update();
    // do a dummy recieve of power to force the updating of what is an isn't a
    // power source as we rely on this
    // to make sure we dont both send and recieve to the same source
    powerHandler.getPowerReceiver().receiveEnergy(Type.STORAGE, 1, null);

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

    if (powerOutputEnabled) {
      transmitEnergy();
    }

    float storedEnergy = powerHandler.getEnergyStored();

    // Update if our power has changed by more than 0.5%
    requiresClientSync |= lastSyncPowerStored != storedEnergy && worldObj.getTotalWorldTime() % 21 == 0;

    if (requiresClientSync) {
      lastSyncPowerStored = storedEnergy;
      // this will cause 'getPacketDescription()' to be called and its result
      // will be sent to the PacketHandler on the other end of
      // client/server connection
      worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
      // And this will make sure our current tile entity state is saved
      worldObj.updateTileEntityChunkAndDoNothing(xCoord, yCoord, zCoord, this);
    }

  }

  private boolean transmitEnergy() {
    
    if (powerHandler.getEnergyStored() <= 0) {
      return false;
    }
    float canTransmit = Math.min(powerHandler.getEnergyStored(), internalCapacitor.getMaxEnergyExtracted());
    float transmitted = 0;

    checkReceptors();

    if (!receptors.isEmpty() && !receptorIterator.hasNext()) {
      receptorIterator = receptors.listIterator();
    }

    int appliedCount = 0;
    int numReceptors = receptors.size();
    while (receptorIterator.hasNext() && canTransmit > 0 && appliedCount < numReceptors) {

      Receptor receptor = receptorIterator.next();
      PowerReceiver pp = receptor.receptor.getPowerReceiver(receptor.fromDir);
      if (pp != null && pp.getMinEnergyReceived() <= canTransmit && pp.getType() != Type.ENGINE && !powerHandler.isPowerSource(receptor.fromDir)) {
        float used;
        float boundCanTransmit = Math.min(canTransmit, pp.getMaxEnergyReceived());        
        if (receptor.receptor instanceof IInternalPowerReceptor) {
          used = PowerHandlerUtil.transmitInternal((IInternalPowerReceptor) receptor.receptor, pp, boundCanTransmit, Type.STORAGE, receptor.fromDir);
        } else {
          used = pp.receiveEnergy(Type.STORAGE, boundCanTransmit, receptor.fromDir);
        }
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
    if (powerInputEnabled) {
      return powerHandler;
    }
    return getDisabledPowerHandler();
  }

  private PowerHandler getDisabledPowerHandler() {
    if (disabledPowerHandler == null) {
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
    if (!receptorsDirty) {
      return;
    }
    receptors.clear();
    BlockCoord myLoc = new BlockCoord(this);
    for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
      BlockCoord checkLoc = myLoc.getLocation(dir);
      TileEntity te = worldObj.getBlockTileEntity(checkLoc.x, checkLoc.y, checkLoc.z);
      if (te instanceof IPowerReceptor) {
        IPowerReceptor rec = (IPowerReceptor) te;
        PowerReceiver reciever = rec.getPowerReceiver(dir.getOpposite());
        receptors.add(new Receptor((IPowerReceptor) te, dir.getOpposite()));
      }
    }

    receptorIterator = receptors.listIterator();
    receptorsDirty = false;
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
      super();
      this.receptor = rec;
      this.fromDir = fromDir;
    }
  }

}
