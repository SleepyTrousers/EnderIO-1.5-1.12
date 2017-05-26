package crazypants.enderio.recipe.painter;

import javax.annotation.Nonnull;

import crazypants.enderio.paint.IPaintable;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;

public class EveryPaintableRecipe<T extends Block & IPaintable> extends BasicPainterTemplate<T> {

  public EveryPaintableRecipe() {
    super(null);
  }

  @Override
  public @Nonnull String getUid() {
    return "\uFFFE" + super.getUid();
  }

  @SuppressWarnings("unchecked")
  @Override
  protected T getTargetBlock(@Nonnull ItemStack target) {
    Block blk = Block.getBlockFromItem(target.getItem());
    if (blk instanceof IPaintable) {
      return (T) blk;
    }
    return null;
  }

  @Override
  public boolean isValidTarget(@Nonnull ItemStack target) {
    return Block.getBlockFromItem(target.getItem()) instanceof IPaintable;
  }

}
