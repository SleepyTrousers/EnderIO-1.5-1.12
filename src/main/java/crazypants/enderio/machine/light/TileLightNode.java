package crazypants.enderio.machine.light;

import crazypants.enderio.EnderIO;
import crazypants.enderio.TileEntityEio;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;

public class TileLightNode extends TileEntityEio {

  int parentX;
  int parentY;
  int parentZ;

  public TileElectricLight getParent() {
    if(worldObj == null) {
      return null;
    }
    TileEntity te = worldObj.getTileEntity(new BlockPos(parentX, parentY, parentZ));
    if(te instanceof TileElectricLight) {
      return (TileElectricLight) te;
    }
    return null;
  }

  public void checkParent() {
    BlockPos bp = new BlockPos(parentX, parentY, parentZ);
    if(worldObj.isBlockLoaded(bp)) {
      if(worldObj.getBlockState(bp).getBlock() != EnderIO.blockElectricLight) {
        worldObj.setBlockToAir(pos);
      }
    }
  }

  public void onNeighbourChanged() {
    TileElectricLight p = getParent();
    if(p != null) {
      p.nodeNeighbourChanged(this);
    }
  }

  public void onBlockRemoved() {
    TileElectricLight p = getParent();
    if(p != null) {
      p.nodeRemoved(this);
    }
  }

  @Override
  public void readCustomNBT(NBTTagCompound root) {
    parentX = root.getInteger("parentX");
    parentY = root.getInteger("parentY");
    parentZ = root.getInteger("parentZ");
  }

  @Override
  public void writeCustomNBT(NBTTagCompound root) {
    root.setInteger("parentX", parentX);
    root.setInteger("parentY", parentY);
    root.setInteger("parentZ", parentZ);
  }

  @Override
  public String toString() {
    return "TileLightNode [parentX=" + parentX + ", parentY=" + parentY + ", parentZ=" + parentZ + ",  pos=" + pos + ", tileEntityInvalid=" + tileEntityInvalid + "]";
  }

  public void setParentPos(BlockPos pos) {
    parentX = pos.getX();
    parentY = pos.getY();
    parentZ = pos.getZ();
    
  }

}
