package crazypants.enderio.base.recipe.painter;

import javax.annotation.Nonnull;

import crazypants.enderio.base.conduit.facade.ItemConduitFacade;
import crazypants.enderio.base.paint.PaintUtil;
import crazypants.enderio.base.recipe.MachineRecipeInput;
import crazypants.enderio.util.Prep;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;

public class FacadePainterRecipe extends AbstractPainterTemplate<ItemConduitFacade> {

  public FacadePainterRecipe(ItemConduitFacade facade) {
    super();
    PaintUtil.registerPaintable(facade);
  }

  @Override
  public boolean isValidTarget(@Nonnull ItemStack target) {
    return Prep.isValid(target) && target.getItem() instanceof ItemConduitFacade;
  }

  @Override
  public @Nonnull ResultStack[] getCompletedResult(@Nonnull ItemStack paintSource, @Nonnull ItemStack target) {
    if (Prep.isInvalid(target) || Prep.isInvalid(paintSource)) {
      return new ResultStack[0];
    }
    Block paintBlock = PaintUtil.getBlockFromItem(paintSource);
    if (paintBlock == null) {
      return new ResultStack[0];
    }
    IBlockState paintState = PaintUtil.Block$getBlockFromItem_stack$getItem___$getStateFromMeta_stack$getMetadata___(paintSource, paintBlock);
    if (paintState == null) {
      return new ResultStack[0];
    }

    ItemStack result = target.copy();
    result.setCount(1);
    PaintUtil.setSourceBlock(result, paintState);

    return new ResultStack[] { new ResultStack(result) };
  }

  @Override
  public boolean isRecipe(@Nonnull ItemStack paintSource, @Nonnull ItemStack target) {
    return isValidTarget(target) && isValidPaint(paintSource);
  }

  @Override
  public boolean isPartialRecipe(@Nonnull ItemStack paintSource, @Nonnull ItemStack target) {
    if (Prep.isInvalid(paintSource)) {
      return isValidTarget(target);
    }
    if (Prep.isInvalid(target)) {
      return isValidPaint(paintSource);
    }
    return isValidTarget(target) && isValidPaint(paintSource);
  }

  protected boolean isValidPaint(@Nonnull ItemStack paintSource) {
    return Prep.isValid(paintSource) && PaintUtil.isValid(paintSource, null);
  }

  @Override
  public boolean isValidInput(@Nonnull MachineRecipeInput input) {
    if (input.slotNumber == 0) {
      return isValidTarget(input.item);
    }
    if (input.slotNumber == 1) {
      return isValidPaint(input.item);
    }
    return false;
  }

  @Override
  protected void registerTargetsWithTooltipProvider() {
  }

}
