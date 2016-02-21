package crazypants.enderio.machine.painter;

import com.enderio.core.common.util.IBlockAccessWrapper;

import net.minecraft.world.IBlockAccess;

//TODO: 1.8
public class PaintedBlockAccessWrapper extends IBlockAccessWrapper {

  public PaintedBlockAccessWrapper(IBlockAccess ba) {
    super(ba);
  }

//  @Override
//  public IBlockState getBlockState(BlockPos pos) {
//    IBlockState res = super.getBlockState(pos);
//    TileEntity te = getTileEntity(pos);    
//    if(te instanceof IPaintableTileEntity) {
//      IPaintableTileEntity tcb = (IPaintableTileEntity) te;
//      Block fac = tcb.getSourceBlock();
//      if(fac != null) {
//        res = fac;
//      }
//    }
//    return res;
//    
//  }


//  @Override
//  @SideOnly(Side.CLIENT)
//  public int getLightBrightnessForSkyBlocks(int var1, int var2, int var3, int var4) {
//    return wrapped.getLightBrightnessForSkyBlocks(var1, var2, var3, var4);
//  }
//
//  @Override
//  public int getBlockMetadata(int x, int y, int z) {
//    Block block = super.getBlock(x, y, z);
//    TileEntity te = getTileEntity(x, y, z);
//    if(te instanceof IPaintableTileEntity) {
//      IPaintableTileEntity tcb = (IPaintableTileEntity) te;
//      Block fac = tcb.getSourceBlock();
//      if(fac != null) {
//        return tcb.getSourceBlockMetadata();
//      }
//    }
//    return super.getBlockMetadata(x, y, z);
//  }

}