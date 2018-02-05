package crazypants.enderio.base.render.registry;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import crazypants.enderio.base.paint.IPaintable;
import crazypants.enderio.base.render.ITintedBlock;
import crazypants.enderio.base.render.ITintedItem;
import crazypants.enderio.util.Prep;
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
  public int colorMultiplier(@Nonnull ItemStack stack, int tintIndex) {
    if (Prep.isInvalid(stack)) {
      return -1;
    }
    Item item = stack.getItem();
    Block block = Block.getBlockFromItem(item);
    if (block instanceof IPaintable) {
      IBlockState paintSource = ((IPaintable) block).getPaintSource(block, stack);
      if (paintSource != null) {
        final ItemStack paintStack = new ItemStack(paintSource.getBlock(), 1, paintSource.getBlock().getMetaFromState(paintSource));
        if (paintStack.getItem() != item) {
          return Minecraft.getMinecraft().getItemColors().colorMultiplier(paintStack, tintIndex);
        }
      }
    }
    if (item instanceof IItemColor) {
      return ((IItemColor) item).colorMultiplier(stack, tintIndex);
    }
    if (item instanceof ITintedItem) {
      return ((ITintedItem) item).getItemTint(stack, tintIndex);
    }
    if (block instanceof IItemColor) {
      return ((IItemColor) block).colorMultiplier(stack, tintIndex);
    }
    if (block instanceof ITintedItem) {
      return ((ITintedItem) block).getItemTint(stack, tintIndex);
    }
    return -1;
  }

  @Override
  public int colorMultiplier(@Nonnull IBlockState state, @Nullable IBlockAccess world, @Nullable BlockPos pos, int tintIndex) {
    if (world == null || pos == null) {
      return -1;
    }
    Block block = state.getBlock();

    IBlockState paintSource = null;
    if (block instanceof IPaintable) {
      paintSource = ((IPaintable) block).getPaintSource(state, world, pos);
      if (paintSource != null && paintSource.getBlock() != block) {
        block = paintSource.getBlock();
        state = paintSource;
      } else {
        paintSource = null;
      }
    }
    if (block instanceof ITintedBlock) {
      return ((ITintedBlock) block).getBlockTint(state, world, pos, tintIndex);
    }
    if (block instanceof IBlockColor) {
      return ((IBlockColor) block).colorMultiplier(state, world, pos, tintIndex);
    }
    if (paintSource != null) {
      return Minecraft.getMinecraft().getBlockColors().colorMultiplier(paintSource, world, pos, tintIndex);
    }
    return -1;
  }

}
