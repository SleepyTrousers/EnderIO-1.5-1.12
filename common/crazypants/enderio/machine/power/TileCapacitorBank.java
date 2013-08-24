package crazypants.enderio.machine.power;

import static net.minecraftforge.fluids.FluidContainerRegistry.BUCKET_VOLUME;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.packet.Packet;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.fluids.FluidStack;
import crazypants.enderio.ModObject;
import crazypants.enderio.PacketHandler;
import crazypants.enderio.machine.reservoir.ReservoirTank;
import crazypants.enderio.machine.reservoir.TileReservoir;
import crazypants.util.BlockCoord;

public class TileCapacitorBank extends TileEntity {

  BlockCoord[] multiblock = null;

  public TileCapacitorBank() {
  }

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
    multiblock = mb;
    if (isMaster()) {

//      regenTank = new ReservoirTank(BUCKET_VOLUME * 2);
//      tank.setCapacity(BUCKET_VOLUME * 2);
//      for (BlockCoord bc : multiblock) {
//        TileReservoir res = getReservoir(bc);
//        if (res != null) {
//          FluidStack drained = res.doDrain(ForgeDirection.UNKNOWN, regenTank.getAvailableSpace(), true);
//          if (drained != null) {
//            regenTank.addAmount(drained.amount);
//          }
//          // incase regen tank is full, add to normal tank
//          drained = res.doDrain(ForgeDirection.UNKNOWN, tank.getAvailableSpace(), true);
//          if (drained != null) {
//            tank.addAmount(drained.amount);
//          }
//        }
//      }
//
//      if (doIsAutoEject()) {
//        updateTankNeighbours();
//      }

    } 
    // Forces an update
    //worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
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
