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
import crazypants.enderio.conduit.ConnectionMode;
import crazypants.enderio.conduit.IConduitBundle;
import crazypants.enderio.conduit.power.IPowerConduit;
import crazypants.enderio.machine.RedstoneControlMode;
import crazypants.enderio.power.BasicCapacitor;
import crazypants.enderio.power.IInternalPowerReceptor;
import crazypants.enderio.power.PowerHandlerUtil;
import crazypants.util.BlockCoord;

public class TileHyperCube extends TileEntity implements IInternalPowerReceptor {

  private RedstoneControlMode powerInputControlMode = RedstoneControlMode.IGNORE;

  private RedstoneControlMode powerOutputControlMode = RedstoneControlMode.IGNORE;

  private boolean powerOutputEnabled = true;

  private boolean powerInputEnabled = true;

  private final BasicCapacitor internalCapacitor = new BasicCapacitor(256, 25000);

  private PowerHandler powerHandler;

  private float lastSyncPowerStored = 0;

  private final List<Receptor> receptors = new ArrayList<Receptor>();
  private ListIterator<Receptor> receptorIterator = receptors.listIterator();
  private boolean receptorsDirty = true;

  private PowerHandler disabledPowerHandler;
  
  private String channel = "Default";
  
  private boolean init = true;

  public TileHyperCube() {
    powerHandler = PowerHandlerUtil.createHandler(internalCapacitor, this, Type.STORAGE);
  }

  public void onBreakBlock() {
    HyperCubeRegister.instance.deregister(this, channel);
  }

  public void onBlockAdded() {
    HyperCubeRegister.instance.register(this, channel);
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
    for(TileHyperCube cube : cubes) {
     cube.getPowerHandler().setEnergy(energyPerNode);
    }
  }
  
  @Override
  public void onChunkUnload() {
    HyperCubeRegister.instance.deregister(this, channel);
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

    if(init) {
      onBlockAdded();
      init = false;
    }
    
    // do the required tick to keep BC API happy
    float stored = powerHandler.getEnergyStored();
    powerHandler.update();
    // do a dummy recieve of power to force the updating of what is an isn't a
    // power source as we rely on this
    // to make sure we dont both send and recieve to the same source
    powerHandler.getPowerReceiver().receiveEnergy(Type.STORAGE, 1, null);

    powerHandler.setEnergy(stored);

    balanceCubeNetworkEnergy();

    boolean requiresClientSync = false;
    powerInputEnabled = RedstoneControlMode.isConditionMet(powerInputControlMode, this);
    powerOutputEnabled = RedstoneControlMode.isConditionMet(powerOutputControlMode, this);

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

    //TODO: Energy loss on energy transfer
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
        if (receptor.receptor instanceof IInternalPowerReceptor) {
          used = PowerHandlerUtil.transmitInternal((IInternalPowerReceptor) receptor.receptor, pp, canTransmit, Type.STORAGE, receptor.fromDir);
        } else {
          used = pp.receiveEnergy(Type.STORAGE, canTransmit, receptor.fromDir);
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
    powerInputControlMode = RedstoneControlMode.values()[nbtRoot.getShort("powerInputControlMode")];
    powerOutputControlMode = RedstoneControlMode.values()[nbtRoot.getShort("powerOutputControlMode")];
  }

  @Override
  public void writeToNBT(NBTTagCompound nbtRoot) {
    super.writeToNBT(nbtRoot);
    nbtRoot.setFloat("storedEnergy", powerHandler.getEnergyStored());
    nbtRoot.setShort("powerInputControlMode", (short) powerInputControlMode.ordinal());
    nbtRoot.setShort("powerOutputControlMode", (short) powerOutputControlMode.ordinal());
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
