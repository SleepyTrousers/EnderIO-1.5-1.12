package crazypants.enderio.machine.power;

import static net.minecraftforge.fluids.FluidContainerRegistry.BUCKET_VOLUME;

import java.util.ArrayList;
import java.util.List;

import buildcraft.api.power.PowerHandler;
import buildcraft.api.power.PowerHandler.PowerReceiver;
import buildcraft.api.power.PowerHandler.Type;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.packet.Packet;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.fluids.FluidStack;
import crazypants.enderio.ModObject;
import crazypants.enderio.PacketHandler;
import crazypants.enderio.machine.RedstoneControlMode;
import crazypants.enderio.machine.reservoir.ReservoirTank;
import crazypants.enderio.machine.reservoir.TileReservoir;
import crazypants.enderio.power.BasicCapacitor;
import crazypants.enderio.power.IInternalPowerReceptor;
import crazypants.enderio.power.PowerHandlerUtil;
import crazypants.util.BlockCoord;

public class TileCapacitorBank extends TileEntity implements IInternalPowerReceptor {

  private static final BasicCapacitor BASE_CAP = new BasicCapacitor(100, 250000);
  
  BlockCoord[] multiblock = null;

  private PowerHandler powerHandler;

  private float lastSyncPowerStored;

  private float storedEnergy;
  
  private int maxStoredEnergy;
  
  private int maxIO;

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

    if (worldObj.isRemote || !isContoller()) {      
      return;
    } // else is server, do all logic only on the server

    //do the required tick to keep BC API happy
    float stored = powerHandler.getEnergyStored();
    powerHandler.update();
    powerHandler.setEnergy(stored);       

//    redstoneCheckPassed = true;
//    if (redstoneControlMode == RedstoneControlMode.ON) {
//      int powerLevel = worldObj.getStrongestIndirectPower(xCoord, yCoord, zCoord);
//      if (powerLevel < 1) {
//        redstoneCheckPassed = false;
//      }
//    } else if (redstoneControlMode == RedstoneControlMode.OFF) {
//      int powerLevel = worldObj.getStrongestIndirectPower(xCoord, yCoord, zCoord);
//      if (powerLevel > 0) {
//        redstoneCheckPassed = false;
//      }
//    }
    
    
    
    storedEnergy = powerHandler.getEnergyStored();
    

    // Update if our power has changed by more than 1%
    boolean requiresClientSync = Math.abs(lastSyncPowerStored - powerHandler.getEnergyStored()) > powerHandler.getMaxEnergyStored() / 100;
    if(!requiresClientSync && lastSyncPowerStored == 0 &&powerHandler.getEnergyStored() > 0) {
      requiresClientSync = true;
    }

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
  

  
  // ------------  Multiblock overrides
  
  public int getEnergyStoredScaled(int scale) {
    return getController().doGetEnergyStoredScaled(scale);
  }

  public float getEnergyStored() {
    return getController().doGetEnergyStored();
  }
  
  public float getEnergyStoredRatio() {
    return getController().doGetEnergyStoredRatio();
  }
  
  @Override
  public PowerHandler getPowerHandler() {
    return getController().doGetPowerHandler();
  }
  
  @Override
  public PowerReceiver getPowerReceiver(ForgeDirection side) {
    return getPowerHandler().getPowerReceiver();
  }
  
  
  // ------------  Multiblock implementations
    
  public PowerHandler doGetPowerHandler() {
    return powerHandler;
  }
  
  public int doGetEnergyStoredScaled(int scale) {
    // NB: called on the client so can't use the power provider
    return (int) (scale * (storedEnergy / maxStoredEnergy));
  }

  public float doGetEnergyStored() {
    return storedEnergy;
  }
  
  public float doGetEnergyStoredRatio() {
    return storedEnergy / maxStoredEnergy;
  }
  
  // ------------  Common power functions 
  
  

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
  
  public boolean onBlockAdded() {
    boolean res = formMultiblock();
    return res;
  }

  public void onNeighborBlockChange(int blockId) {    
    if (blockId == ModObject.blockCapacitorBank.actualId) {

      if (!isCurrentMultiblockValid()) {
        // if its not, try and form a new one
        TileCapacitorBank controller = getController();
        if (controller != null) {
          controller.clearCurrentMultiblock();
          controller.formMultiblock();
        } else {
          clearCurrentMultiblock();
          formMultiblock();
        }
        
      }
    }    
  }
  
  public void onBreakBlock() {
    TileCapacitorBank controller = getController();
    if (controller != null) {
      controller.clearCurrentMultiblock();
      controller.formMultiblock();
    } else {
      clearCurrentMultiblock();
      formMultiblock();
    }
    
  }
  
  
  private boolean formMultiblock() {
    List<TileCapacitorBank> blocks = new ArrayList<TileCapacitorBank>();
    blocks.add(this);
    findNighbouringBanks(this, blocks);
    
    if(blocks.size() < 2) {
      return false;
    }    
    for(TileCapacitorBank cb : blocks) {
      cb.clearCurrentMultiblock();
    }
    
    BlockCoord[] mb = new BlockCoord[blocks.size()];
    for(int i=0;i<blocks.size();i++) {
      mb[i] = new BlockCoord(blocks.get(i));
    }    
    for(TileCapacitorBank cb : blocks) {
      cb.setMultiblock(mb);          
    }        
    return true;
  }
  
  
  
  private void findNighbouringBanks(TileCapacitorBank tileCapacitorBank, List<TileCapacitorBank> blocks) {
    BlockCoord bc = new BlockCoord(tileCapacitorBank);
    for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
      TileCapacitorBank cb = getCapBank(bc.getLocation(dir));
      if(cb != null && !blocks.contains(cb)) {
        blocks.add(cb);
        findNighbouringBanks(cb, blocks);
      }
    }    
  }

  private void setMultiblock(BlockCoord[] mb) {
    
    
    if (multiblock != null  && isMaster()) { 
      //split up current multiblock and reconfigure all the internal capacitors
      float powerPerBlock = storedEnergy / multiblock.length;
      for(BlockCoord bc : multiblock) {
        TileCapacitorBank cb = getCapBank(bc);
        if(cb != null) {
          cb.maxStoredEnergy = BASE_CAP.getMaxEnergyStored();
          cb.maxIO = BASE_CAP.getMaxEnergyExtracted();
          cb.storedEnergy = powerPerBlock;
          System.out.println("TileCapacitorBank.setMultiblock: Set stored energy to " + cb.storedEnergy);
          cb.updatePowerHandler();
        }
      }

    }
    multiblock = mb;
    if(isMaster()) {
      float totalStored = 0;
      int totalCap = 0;
      int totalIO = 0;
      for(BlockCoord bc : multiblock) {
        TileCapacitorBank cb = getCapBank(bc);
        if(cb != null) {
          totalStored += cb.getEnergyStored();
          totalCap += cb.getPowerHandler().getMaxEnergyStored();
          totalIO += cb.getPowerHandler().getMaxEnergyReceived();
        }
      }
      storedEnergy = totalStored;
      maxStoredEnergy = totalCap;
      maxIO = totalIO;
      updatePowerHandler();
    }
    
    // Forces an update
    worldObj.setBlockMetadataWithNotify(xCoord, yCoord, zCoord, isMultiblock() ? 1 : 0, 2);
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
      multiblock = new BlockCoord[coords.length/3];
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

  

}
