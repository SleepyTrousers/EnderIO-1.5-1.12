package crazypants.enderio.material.glass;

import javax.annotation.Nonnull;

import com.enderio.core.common.util.NNList;
import com.enderio.core.common.util.NNList.NNIterator;

import crazypants.enderio.machine.painter.recipe.BasicPainterTemplate;
import crazypants.enderio.paint.PainterUtil2;
import crazypants.util.Prep;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;

public class GlassPaintingRecipe extends BasicPainterTemplate<BlockPaintedFusedQuartz> {

  public GlassPaintingRecipe(@Nonnull BlockPaintedFusedQuartz resultBlock, Block[] validTargetBlocks) {
    super(resultBlock, validTargetBlocks);
  }

  protected @Nonnull ItemStack mkItemStack(@Nonnull ItemStack target, @Nonnull Block targetBlock) {
    NNIterator<FusedQuartzType> iterator = NNList.of(FusedQuartzType.class).iterator();
    while (iterator.hasNext()) {
      FusedQuartzType type = iterator.next();
      if (type.getBlock() == targetBlock) {
        return new ItemStack(targetBlock, 1, FusedQuartzType.getMetaFromType(type));
      }
    }
    return new ItemStack(targetBlock, 1, 0);
  }

  @Override
  public boolean isValidTarget(@Nonnull ItemStack target) {
    return super.isValidTarget(target) && target.getItemDamage() == 0;
  }

  @Override
  public @Nonnull ItemStack isUnpaintingOp(@Nonnull ItemStack paintSource, @Nonnull ItemStack target) {
    if (Prep.isInvalid(paintSource) || Prep.isInvalid(target)) {
      return Prep.getEmpty();
    }

    Block paintBlock = PainterUtil2.getBlockFromItem(paintSource);
    Block targetBlock = Block.getBlockFromItem(target.getItem());
    if (paintBlock == null || targetBlock == Blocks.AIR) {
      return Prep.getEmpty();
    }

    if (paintBlock == FusedQuartzType.getTypeFromMeta(target.getItemDamage()).getBlock() && paintSource.getItemDamage() == 0) {
      return new ItemStack(paintBlock, 1, 0);
    }

    return Prep.getEmpty();
  }

}