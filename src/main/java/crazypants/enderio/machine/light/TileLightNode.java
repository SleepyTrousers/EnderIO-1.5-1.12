package crazypants.enderio.machine.light;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import crazypants.enderio.EnderIO;
import crazypants.enderio.TileEntityEio;

public class TileLightNode extends TileEntityEio {

  int parentX;
  int parentY;
  int parentZ;

  public TileElectricLight getParent() {
    TileEntity te = worldObj.getTileEntity(parentX, parentY, parentZ);
    if(te instanceof TileElectricLight) {
      return (TileElectricLight) te;
    }
    return null;
  }

  @Override
  protected boolean shouldUpdate() {
    return false;
  }

  public void checkParent() {
    if(worldObj.blockExists(parentX, parentY, parentZ)) {
      if(worldObj.getBlock(parentX, parentY, parentZ) != EnderIO.blockElectricLight) {
        worldObj.setBlockToAir(xCoord, yCoord, zCoord);
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
    return "TileLightNode [parentX=" + parentX + ", parentY=" + parentY + ", parentZ=" + parentZ + ",  xCoord=" + xCoord
        + ", yCoord=" + yCoord + ", zCoord=" + zCoord + ", tileEntityInvalid=" + tileEntityInvalid + "]";
  }

}
