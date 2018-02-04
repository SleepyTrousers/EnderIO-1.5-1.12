package crazypants.enderio.base.recipe.painter;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.common.util.stackable.IProducer;

import crazypants.enderio.base.block.painted.BlockPaintedDoor;
import crazypants.enderio.base.init.ModObjectRegistry;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;

public class DoorPainterTemplate extends BasicPainterTemplate<BlockPaintedDoor> {

  private final IProducer validTargetItem;

  public DoorPainterTemplate(@Nullable BlockPaintedDoor resultBlock, @Nonnull IProducer validTargetItem, @Nonnull Block validTargetBlock) {
    super(resultBlock, validTargetBlock);
    this.validTargetItem = validTargetItem;
  }

  @Override
  protected @Nonnull ItemStack mkItemStack(@Nonnull ItemStack target, @Nonnull Block targetBlock) {
    return new ItemStack(ModObjectRegistry.getModObjectNN(targetBlock).getItemNN());
  }

  @Override
  public boolean isValidTarget(@Nonnull ItemStack target) {
    return target.getItem() == validTargetItem.getItemNN();
  }

}
