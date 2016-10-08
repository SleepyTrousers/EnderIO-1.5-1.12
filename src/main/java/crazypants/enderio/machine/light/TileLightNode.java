package crazypants.enderio.machine.light;

import crazypants.enderio.TileEntityEio;
import info.loenwind.autosave.annotations.Storable;
import info.loenwind.autosave.annotations.Store;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;

import static crazypants.enderio.ModObject.blockElectricLight;

@Storable
public class TileLightNode extends TileEntityEio {

  @Store
  BlockPos parent;

  public TileElectricLight getParent() {
    if (worldObj == null || parent == null) {
      return null;
    }
    TileEntity te = worldObj.getTileEntity(parent);
    if(te instanceof TileElectricLight) {
      return (TileElectricLight) te;
    }
    return null;
  }

  public void checkParent() {
    if (hasWorldObj() && parent != null && worldObj.isBlockLoaded(parent)) {
      if (worldObj.getBlockState(parent).getBlock() != blockElectricLight.getBlock()) {
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
  public String toString() {
    return "TileLightNode [parent=" + parent + ",  pos=" + pos + ", tileEntityInvalid=" + tileEntityInvalid + "]";
  }

  public void setParentPos(BlockPos pos) {
    parent = pos.toImmutable();
  }

}
