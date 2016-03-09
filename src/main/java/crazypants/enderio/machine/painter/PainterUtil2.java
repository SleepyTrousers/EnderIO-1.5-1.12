package crazypants.enderio.machine.painter;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import crazypants.enderio.render.paint.IPaintableBlock;

public class PainterUtil2 {

  public static boolean isValid(ItemStack paintSource, ItemStack target) {
    if (paintSource == null && target == null) {
      return false;
    }
    Block block = null;
    if (target != null) {
      Item item = paintSource.getItem();
      if (item instanceof ItemBlock) {
        block = ((ItemBlock) item).getBlock();
      } else {
        return false;
      }
    }
    return isValid(paintSource, block);
  }

  public static boolean isValid(ItemStack paintSource, Block target) {
    if (paintSource == null && target == null) {
      return false;
    }
    boolean nonSolidPaint = false;
    if (paintSource != null) {
      Item item = paintSource.getItem();
      if (item instanceof ItemBlock) {
        Block block = ((ItemBlock) item).getBlock();
        if (block instanceof IPaintableBlock) {
          IBlockState paintSource2 = ((IPaintableBlock) block).getPaintSource(block, paintSource);
          if (paintSource2 != null) {
            return false;
          }
        }
        nonSolidPaint = !block.isOpaqueCube();
      } else {
        return false;
      }
    }

    if (target != null) {
      if (target instanceof IPaintableBlock.ITexturePaintableBlock || target instanceof IPaintableBlock.IAnyBlockPaintableBlock) {
        return true;
      } else if (target instanceof IPaintableBlock.ISolidBlockPaintableBlock) {
        return !nonSolidPaint;
      } else {
        return false;
      }
    }

    return true;
  }

}
