package crazypants.enderio.machine.painter;

import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;

import com.enderio.core.common.util.IBlockAccessWrapper;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class PaintedBlockAccessWrapper extends IBlockAccessWrapper {

    public PaintedBlockAccessWrapper(IBlockAccess ba) {
        super(ba);
    }

    @Override
    public Block getBlock(int x, int y, int z) {
        Block res = super.getBlock(x, y, z);
        TileEntity te = getTileEntity(x, y, z);
        if (te instanceof IPaintableTileEntity) {
            IPaintableTileEntity tcb = (IPaintableTileEntity) te;
            Block fac = tcb.getSourceBlock();
            if (fac != null) {
                res = fac;
            }
        }
        return res;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public int getLightBrightnessForSkyBlocks(int var1, int var2, int var3, int var4) {
        return wrapped.getLightBrightnessForSkyBlocks(var1, var2, var3, var4);
    }

    @Override
    public int getBlockMetadata(int x, int y, int z) {
        Block block = super.getBlock(x, y, z);
        TileEntity te = getTileEntity(x, y, z);
        if (te instanceof IPaintableTileEntity) {
            IPaintableTileEntity tcb = (IPaintableTileEntity) te;
            Block fac = tcb.getSourceBlock();
            if (fac != null) {
                return tcb.getSourceBlockMetadata();
            }
        }
        return super.getBlockMetadata(x, y, z);
    }
}
