package crazypants.enderio.base.recipe.painter;

import javax.annotation.Nonnull;

import crazypants.enderio.base.block.painted.BlockPaintedPressurePlate;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;

public class PressurePlatePainterTemplate extends BasicPainterTemplate<BlockPaintedPressurePlate> {

  private final int meta;

  public PressurePlatePainterTemplate(@Nonnull BlockPaintedPressurePlate resultBlock, int meta, @Nonnull Block... validTargetBlocks) {
    super(resultBlock, validTargetBlocks);
    this.meta = meta;
  }

  @Override
  protected @Nonnull ItemStack mkItemStack(@Nonnull ItemStack target, @Nonnull Block targetBlock) {
    if (targetBlock == resultBlock) {
      return new ItemStack(targetBlock, 1, meta);
    } else {
      return super.mkItemStack(target, targetBlock);
    }
  }

  @Override
  public boolean isValidTarget(@Nonnull ItemStack target) {
    return Block.getBlockFromItem(target.getItem()) != resultBlock && super.isValidTarget(target);
  }

}