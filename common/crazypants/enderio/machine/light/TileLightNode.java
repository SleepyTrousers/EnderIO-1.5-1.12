package crazypants.enderio.machine.light;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import crazypants.enderio.ModObject;

public class TileLightNode extends TileEntity {

  int parentX;
  int parentY;
  int parentZ;

  boolean isDiagnal = false;

  public TileElectricLight getParent() {
    TileEntity te = worldObj.getBlockTileEntity(parentX, parentY, parentZ);
    if(te instanceof TileElectricLight) {
      return (TileElectricLight) te;
    }
    return null;
  }

  @Override
  public void updateEntity() {
    if(worldObj.isRemote) {
      return;
    }
    if(worldObj.getWorldTime() % 42 == 0) {
      if(worldObj.getBlockId(parentX, parentY, parentZ) != ModObject.blockElectricLight.actualId) {
        System.out.println("TileLightNode.updateEntity: ");
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
  public void readFromNBT(NBTTagCompound root) {
    super.readFromNBT(root);
    parentX = root.getInteger("parentX");
    parentY = root.getInteger("parentY");
    parentZ = root.getInteger("parentZ");
    isDiagnal = root.getBoolean("isDiagnal");
  }

  @Override
  public void writeToNBT(NBTTagCompound root) {
    super.writeToNBT(root);
    root.setInteger("parentX", parentX);
    root.setInteger("parentY", parentY);
    root.setInteger("parentZ", parentZ);
    root.setBoolean("isDiagnal", isDiagnal);
  }

  @Override
  public String toString() {
    return "TileLightNode [parentX=" + parentX + ", parentY=" + parentY + ", parentZ=" + parentZ + ", isDiagnal=" + isDiagnal + ", xCoord=" + xCoord
        + ", yCoord=" + yCoord + ", zCoord=" + zCoord + ", tileEntityInvalid=" + tileEntityInvalid + "]";
  }

}
