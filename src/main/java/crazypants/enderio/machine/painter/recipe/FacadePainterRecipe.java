package crazypants.enderio.machine.painter.recipe;

import crazypants.enderio.conduit.facade.ItemConduitFacade;
import crazypants.enderio.paint.PaintTooltipUtil;
import crazypants.enderio.paint.PainterUtil2;
import crazypants.util.Prep;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

public class FacadePainterRecipe extends AbstractPainterTemplate<ItemConduitFacade> {

  public FacadePainterRecipe(ItemConduitFacade facade) {
    super();
    PaintTooltipUtil.registerPaintable(facade);
  }

  @Override
  public boolean isValidTarget(ItemStack target) {
    return Prep.isValid(target) && target.getItem() instanceof ItemConduitFacade;
  }

  @Override
  public ResultStack[] getCompletedResult(ItemStack paintSource, ItemStack target) {
    if (Prep.isInvalid(target) || Prep.isInvalid(paintSource)) {
      return new ResultStack[0];
    }
    Block paintBlock = PainterUtil2.getBlockFromItem(paintSource);
    if (paintBlock == null) {
      return new ResultStack[0];
    }
    IBlockState paintState = PainterUtil2.Block$getBlockFromItem_stack$getItem___$getStateFromMeta_stack$getMetadata___(paintSource, paintBlock);
    if (paintState == null) {
      return new ResultStack[0];
    }

    ItemStack result = target.copy();
    result.stackSize = 1;
    PainterUtil2.setSourceBlock(result, paintState);

    return new ResultStack[] { new ResultStack(result) };
  }

  @Override
  public boolean isRecipe(ItemStack paintSource, ItemStack target) {
    return isValidTarget(target) && isValidPaint(paintSource);
  }

  @Override
  public boolean isPartialRecipe(ItemStack paintSource, ItemStack target) {
    return isValidTarget(target) || isValidPaint(paintSource);
  }

  protected boolean isValidPaint(ItemStack paintSource) {
    return Prep.isValid(paintSource) && paintSource.getItem() instanceof ItemBlock;
  }

  @Override
  public boolean isValidInput(MachineRecipeInput input) {
    if (input == null) {
      return false;
    }
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
