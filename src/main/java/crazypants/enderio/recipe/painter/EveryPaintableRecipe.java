package crazypants.enderio.recipe.painter;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import crazypants.enderio.paint.IPaintable;

public class EveryPaintableRecipe<T extends Block & IPaintable> extends BasicPainterTemplate<T> {

  public EveryPaintableRecipe() {
    super(null);
  }

  @Override
  public String getUid() {
    return "\uFFFE" + super.getUid();
  }

  @SuppressWarnings("unchecked")
  @Override
  protected T getTargetBlock(ItemStack target) {
    if (target != null) {
      Block blk = Block.getBlockFromItem(target.getItem());
      if (blk instanceof IPaintable) {
        return (T) blk;
      }
    }
    return null;
  }

  @Override
  public boolean isValidTarget(ItemStack target) {
    if (target == null) {
      return false;
    }

    Block blk = Block.getBlockFromItem(target.getItem());
    return blk instanceof IPaintable;
  }

}
