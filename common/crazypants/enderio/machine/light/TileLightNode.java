package crazypants.enderio.machine.light;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import crazypants.enderio.EnderIO;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraftforge.common.ForgeDirection;

public class TileLightNode extends TileEntity {

  int parentX;
  int parentY;
  int parentZ;

  public TileElectricLight getParent() {
    TileEntity te = worldObj.getBlockTileEntity(parentX, parentY, parentZ);
    if(te instanceof TileElectricLight) {
      return (TileElectricLight)te;
    }
    return null;
  }
  
//  public int getLightValue() {
//    TileElectricLight p = getParent();
//    if(p == null) {
//      return 0;
//    }
//    return EnderIO.blockElectricLight.getLightValue(worldObj, parentX, parentY, parentZ);
//  }
  
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
  }

  @Override
  public void writeToNBT(NBTTagCompound root) {
    super.writeToNBT(root);
    root.setInteger("parentX", parentX);
    root.setInteger("parentY", parentY);
    root.setInteger("parentZ", parentZ);    
  }

  @Override
  @SideOnly(Side.CLIENT)
  public AxisAlignedBB getRenderBoundingBox() {
    return null;
  }
  
  
  

  
  
}
