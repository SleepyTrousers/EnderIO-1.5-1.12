package crazypants.enderio.machine.power;

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
import crazypants.enderio.ModObject;
import crazypants.enderio.PacketHandler;
import crazypants.enderio.power.BasicCapacitor;
import crazypants.enderio.power.IInternalPowerReceptor;
import crazypants.enderio.power.PowerHandlerUtil;
import crazypants.util.BlockCoord;

public class TileCapacitorBank extends TileEntity implements IInternalPowerReceptor {

  static final BasicCapacitor BASE_CAP = new BasicCapacitor(100, 250000);

  BlockCoord[] multiblock = null;

  private PowerHandler powerHandler;

  private float lastSyncPowerStored;

  private float storedEnergy;

  private int maxStoredEnergy;

  private int maxIO;
  
  private boolean multiblockDirty = false;

  private final List<Receptor> receptors = new ArrayList<Receptor>();
  private ListIterator<Receptor> receptorIterator = receptors.listIterator();
  private boolean receptorsDirty = true;

  public TileCapacitorBank() {
    storedEnergy = 0;
    maxStoredEnergy = BASE_CAP.getMaxEnergyStored();
    maxIO = BASE_CAP.getMaxEnergyExtracted();
    updatePowerHandler();
  }

  @Override
  public void updateEntity() {

    if (worldObj == null) { // sanity check
      return;
    }

    if (worldObj.isRemote) {
      return;
    } // else is server, do all logic only on the server
    
    if(multiblockDirty) {
      formMultiblock();
      multiblockDirty = false;
    }
    
    if(!isContoller()) {
      return;
    }

    // do the required tick to keep BC API happy
    float stored = powerHandler.getEnergyStored();
    powerHandler.update();
    powerHandler.setEnergy(stored);

    // redstoneCheckPassed = true;
    // if (redstoneControlMode == RedstoneControlMode.ON) {
    // int powerLevel = worldObj.getStrongestIndirectPower(xCoord, yCoord,
    // zCoord);
    // if (powerLevel < 1) {
    // redstoneCheckPassed = false;
    // }
    // } else if (redstoneControlMode == RedstoneControlMode.OFF) {
    // int powerLevel = worldObj.getStrongestIndirectPower(xCoord, yCoord,
    // zCoord);
    // if (powerLevel > 0) {
    // redstoneCheckPassed = false;
    // }
    // }

    transmitEnergy();

    storedEnergy = powerHandler.getEnergyStored();

    // Update if our power has changed by more than 0.5%
    boolean requiresClientSync = lastSyncPowerStored != storedEnergy && worldObj.getWorldTime() % 21 == 0;    

    if (requiresClientSync) {
      lastSyncPowerStored = powerHandler.getEnergyStored();
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
    float canTransmit = Math.min(storedEnergy, maxIO);
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
      if (pp != null && pp.getMinEnergyReceived() <= canTransmit && pp.getType() != Type.ENGINE) {
        float used;
        if (receptor.receptor instanceof IInternalPowerReceptor) {
          // System.out.println("TileEntityStirlingGenerator.transmitEnergy: Sending "
          // + canTransmit + " to internal.");
          used = PowerHandlerUtil.transmitInternal((IInternalPowerReceptor) receptor.receptor, pp, canTransmit, Type.STORAGE, receptor.fromDir);
        } else {
          // System.out.println("TileEntityStirlingGenerator.transmitEnergy: Sending "
          // + canTransmit + " to EXTERNAL. Receptor is: " + receptor.receptor);
          used = pp.receiveEnergy(Type.STORAGE, canTransmit, receptor.fromDir);
        }
        transmitted += used;
        // if (used > 0) {
        // System.out.println("TileEntityStirlingGenerator.transmitEnergy: Trasnmitted energy "
        // + used + " to " + receptor.receptor);
        // }
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

  private void checkReceptors() {
    if (!receptorsDirty) {
      return;
    }
    receptors.clear();

    BlockCoord[] coords;
    if (isMultiblock()) {
      coords = multiblock;
    } else {
      coords = new BlockCoord[] { new BlockCoord(this) };
    }

    for (BlockCoord bc : coords) {
      // BlockCoord bc = new BlockCoord(xCoord, yCoord, zCoord);
      for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
        BlockCoord checkLoc = bc.getLocation(dir);
        TileEntity te = worldObj.getBlockTileEntity(checkLoc.x, checkLoc.y, checkLoc.z);
        if (te instanceof IPowerReceptor) {
          IPowerReceptor rec = (IPowerReceptor) te;
          if (!(te instanceof TileCapacitorBank)) {
            PowerReceiver reciever = rec.getPowerReceiver(dir.getOpposite());
            receptors.add(new Receptor((IPowerReceptor) te, dir.getOpposite()));
          }
        }
      }
    }
    receptorIterator = receptors.listIterator();
    receptorsDirty = false;
  }

  // ------------ Multiblock overrides

  public int getEnergyStoredScaled(int scale) {
    return getController().doGetEnergyStoredScaled(scale);
  }

  public float getEnergyStored() {
    return getController().doGetEnergyStored();
  }

  public float getEnergyStoredRatio() {
    return getController().doGetEnergyStoredRatio();
  }

  public int getMaxEnergyStored() {
    return getController().doGetMaxEnergyStored();
  }

  public int getMaxIO() {
    return getController().doGetMaxIO();
  }

  @Override
  public PowerHandler getPowerHandler() {
    return getController().doGetPowerHandler();
  }

  @Override
  public PowerReceiver getPowerReceiver(ForgeDirection side) {
    return getPowerHandler().getPowerReceiver();
  }
  
  public void addEnergy(float add) {
    getController().doAddEnergy(add);
    
  }

  // ------------ Multiblock implementations

  int doGetMaxIO() {
    return maxIO;
  }

  int doGetMaxEnergyStored() {
    return maxStoredEnergy;
  }

  PowerHandler doGetPowerHandler() {
    return powerHandler;
  }

  int doGetEnergyStoredScaled(int scale) {
    // NB: called on the client so can't use the power provider
    return (int) (scale * (storedEnergy / maxStoredEnergy));
  }

  float doGetEnergyStored() {
    return storedEnergy;
  }

  float doGetEnergyStoredRatio() {
    return storedEnergy / maxStoredEnergy;
  }
  
  void doAddEnergy(float add) {
    storedEnergy = Math.min(maxStoredEnergy, storedEnergy + add);
    powerHandler.setEnergy(storedEnergy);        
  }

  // ------------ Common power functions

  @Override
  public void doWork(PowerHandler workProvider) {
  }

  @Override
  public World getWorld() {
    return worldObj;
  }

  @Override
  public void applyPerdition() {
  }

  private void updatePowerHandler() {
    powerHandler = PowerHandlerUtil.createHandler(new BasicCapacitor(maxIO, maxStoredEnergy), this, Type.STORAGE);
    powerHandler.setEnergy(storedEnergy);
  }

  // ------------ Multiblock management

  public void onBlockAdded() {
    //formMultiblock();
    multiblockDirty = true;
  }

  public void onNeighborBlockChange(int blockId) {    
    if (blockId != ModObject.blockCapacitorBank.actualId) { 
      getController().receptorsDirty = true;
    }
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
  }

  private void formMultiblock() {
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
    for (TileCapacitorBank cb : blocks) {
      cb.setMultiblock(mb);
    }        
  }

  private void findNighbouringBanks(TileCapacitorBank tileCapacitorBank, List<TileCapacitorBank> blocks) {
    BlockCoord bc = new BlockCoord(tileCapacitorBank);
    for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
      TileCapacitorBank cb = getCapBank(bc.getLocation(dir));
      if (cb != null && !blocks.contains(cb)) {
        blocks.add(cb);
        findNighbouringBanks(cb, blocks);
      }
    }
  }

  private void setMultiblock(BlockCoord[] mb) {

    if (multiblock != null && isMaster()) {
      // split up current multiblock and reconfigure all the internal capacitors
      float powerPerBlock = storedEnergy / multiblock.length;
      for (BlockCoord bc : multiblock) {
        TileCapacitorBank cb = getCapBank(bc);
        if (cb != null) {
          cb.maxStoredEnergy = BASE_CAP.getMaxEnergyStored();
          cb.maxIO = BASE_CAP.getMaxEnergyExtracted();
          cb.storedEnergy = powerPerBlock;
          cb.updatePowerHandler();
          cb.multiblockDirty = true;
        }
      }

    }
    multiblock = mb;
    if (isMaster()) {
      float totalStored = 0;
      int totalCap = 0;
      int totalIO = 0;
      for (BlockCoord bc : multiblock) {
        TileCapacitorBank cb = getCapBank(bc);
        if (cb != null) {
          totalStored += cb.getEnergyStored();
          totalCap += cb.getPowerHandler().getMaxEnergyStored();
          totalIO += cb.getPowerHandler().getMaxEnergyReceived();
        }
        cb.multiblockDirty = false;
      }
      storedEnergy = totalStored;
      maxStoredEnergy = totalCap;
      maxIO = totalIO;
      updatePowerHandler();
    }
    receptorsDirty = true;

    // Forces an update
    worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
    worldObj.setBlockMetadataWithNotify(xCoord, yCoord, zCoord, isMultiblock() ? 1 : 0, 2);
  }

  TileCapacitorBank getController() {
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
    TileEntity te = worldObj.getBlockTileEntity(x, y, z);
    if (te instanceof TileCapacitorBank) {
      return (TileCapacitorBank) te;
    }
    return null;
  }

  @Override
  public void readFromNBT(NBTTagCompound nbtRoot) {
    super.readFromNBT(nbtRoot);

    storedEnergy = nbtRoot.getFloat("storedEnergy");
    maxStoredEnergy = nbtRoot.getInteger("maxStoredEnergy");
    maxIO = nbtRoot.getInteger("maxIO");

    updatePowerHandler();

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
  }

  @Override
  public void writeToNBT(NBTTagCompound nbtRoot) {
    super.writeToNBT(nbtRoot);

    nbtRoot.setFloat("storedEnergy", storedEnergy);
    nbtRoot.setInteger("maxStoredEnergy", maxStoredEnergy);
    nbtRoot.setInteger("maxIO", maxIO);

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
