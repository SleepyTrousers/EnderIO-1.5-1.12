package crazypants.enderio.render;

import crazypants.enderio.paint.IPaintable;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.color.IBlockColor;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

public class PaintTintHandler implements IBlockColor, IItemColor {

  @Override
  public int getColorFromItemstack(ItemStack stack, int tintIndex) {
    if (stack == null || stack.getItem() == null) {
      return -1;
    }
    Item item = stack.getItem();
    Block block = Block.getBlockFromItem(item);
    if (block instanceof IPaintable) {
      IBlockState paintSource = ((IPaintable) block).getPaintSource(block, stack);
      if (paintSource != null) {
        final ItemStack paintStack = new ItemStack(paintSource.getBlock(), 1, paintSource.getBlock().getMetaFromState(paintSource));
        if (paintStack.getItem() != item) {
          return Minecraft.getMinecraft().getItemColors().getColorFromItemstack(paintStack, tintIndex);
        }
      }
    }
    if (item instanceof IItemColor) {
      return ((IItemColor) item).getColorFromItemstack(stack, tintIndex);
    }
    if (block instanceof IItemColor) {
      return ((IItemColor) block).getColorFromItemstack(stack, tintIndex);
    }
    return -1;
  }

  @Override
  public int colorMultiplier(IBlockState state, IBlockAccess p_186720_2_, BlockPos pos, int tintIndex) {
    if (state == null || state.getBlock() == null) {
      return -1;
    }
    Block block = state.getBlock();
    if (block instanceof IPaintable) {
      IBlockState paintSource = ((IPaintable) block).getPaintSource(state, p_186720_2_, pos);
      if (paintSource != null && paintSource != block) {
        return Minecraft.getMinecraft().getBlockColors().colorMultiplier(paintSource, p_186720_2_, pos, tintIndex);
      }
    }
    if (block instanceof IBlockColor) {
      return ((IBlockColor) block).colorMultiplier(state, p_186720_2_, pos, tintIndex);
    }
    return -1;
  }

}
