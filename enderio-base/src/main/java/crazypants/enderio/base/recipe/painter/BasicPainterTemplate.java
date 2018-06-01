package crazypants.enderio.base.recipe.painter;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.common.util.NullHelper;

import crazypants.enderio.base.paint.IPaintable;
import crazypants.enderio.base.paint.PaintUtil;
import crazypants.enderio.base.recipe.MachineRecipeInput;
import crazypants.enderio.util.Prep;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class BasicPainterTemplate<T extends Block & IPaintable> extends AbstractPainterTemplate<T> {

  protected final @Nullable T resultBlock;
  protected final @Nonnull Block[] validTargets;
  protected final boolean allowEasyConversion;

  public BasicPainterTemplate(boolean allowEasyConversion, @Nullable T resultBlock, @Nonnull Block... validTargetBlocks) {
    this.resultBlock = resultBlock;
    this.validTargets = validTargetBlocks;
    this.allowEasyConversion = allowEasyConversion;
    PaintUtil.registerPaintable(validTargetBlocks);
  }

  public BasicPainterTemplate(@Nullable T resultBlock, @Nonnull Block... validTargetBlocks) {
    this(true, resultBlock, validTargetBlocks);
  }

  @Override
  public boolean isRecipe(@Nonnull ItemStack paintSource, @Nonnull ItemStack target) {
    return isValidTarget(target) && Prep.isValid(paintSource) && PaintUtil.isValid(paintSource, getTargetBlock(target));
  }

  @Override
  public boolean isPartialRecipe(@Nonnull ItemStack paintSource, @Nonnull ItemStack target) {
    if (Prep.isInvalid(paintSource)) {
      return isValidTarget(target);
    }
    if (Prep.isInvalid(target)) {
      return PaintUtil.isValid(paintSource, getTargetBlock(Prep.getEmpty()));
    }
    return isValidTarget(target) && PaintUtil.isValid(paintSource, getTargetBlock(target));
  }

  @Override
  public @Nonnull ResultStack[] getCompletedResult(@Nonnull ItemStack paintSource, @Nonnull ItemStack inputStack) {
    Block outputBlock = getTargetBlock(inputStack);
    if (Prep.isInvalid(inputStack) || Prep.isInvalid(paintSource) || outputBlock == null) {
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

    ItemStack outputStack = isUnpaintingOp(paintSource, inputStack);
    if (Prep.isInvalid(outputStack)) {
      outputStack = mkItemStack(inputStack, outputBlock);
      if (outputBlock == Block.getBlockFromItem(inputStack.getItem()) && inputStack.hasTagCompound()) {
        outputStack.setTagCompound(NullHelper.notnullM(inputStack.getTagCompound(), "ItemStack.getTagCompound() after .hasTagCompound()").copy());
      }
      ((IPaintable) outputBlock).setPaintSource(outputBlock, outputStack, paintState);
    } else if (outputStack.getItem() == inputStack.getItem() && inputStack.hasTagCompound()) {
      outputStack.setTagCompound(NullHelper.notnullM(inputStack.getTagCompound(), "ItemStack.getTagCompound() after .hasTagCompound()").copy());

      Block realresult = PaintUtil.getBlockFromItem(outputStack);
      if (realresult instanceof IPaintable) {
        ((IPaintable) realresult).setPaintSource(realresult, outputStack, null);
      } else {
        PaintUtil.setSourceBlock(outputStack, null);
      }
    }
    return new ResultStack[] { new ResultStack(outputStack) };
  }

  protected @Nonnull ItemStack mkItemStack(@Nonnull ItemStack target, @Nonnull Block targetBlock) {
    Item itemFromBlock = Item.getItemFromBlock(targetBlock);
    if (itemFromBlock.isDamageable() || itemFromBlock.getHasSubtypes()) {
      return new ItemStack(targetBlock, 1, target.getItemDamage());
    } else {
      return new ItemStack(targetBlock, 1, 0);
    }
  }

  @Override
  public boolean isValidInput(@Nonnull MachineRecipeInput input) {
    if (input.slotNumber == 0) {
      return isValidTarget(input.item);
    }
    if (input.slotNumber == 1) {
      return PaintUtil.isValid(input.item, resultBlock);
    }
    return false;
  }

  protected @Nullable T getTargetBlock(@Nonnull ItemStack target) {
    return resultBlock;
  }

  public @Nonnull ItemStack isUnpaintingOp(@Nonnull ItemStack paintSource, @Nonnull ItemStack target) {
    if (Prep.isInvalid(paintSource) || Prep.isInvalid(target)) {
      return Prep.getEmpty();
    }

    Block paintBlock = PaintUtil.getBlockFromItem(paintSource);
    Block targetBlock = Block.getBlockFromItem(target.getItem());
    if (paintBlock == null || targetBlock == Blocks.AIR) {
      return Prep.getEmpty();
    }

    // The paint source is the paintable block we produce with this recipe. We know it must be unpainted, as paint sources that are painted are rejected. So we
    // user wants the input item but without its paint. The input item must be able to exist in the world unpainted because it does so as paint source. So we
    // copy the input item without its paint information.
    if (paintBlock == resultBlock) {
      return mkItemStack(target, targetBlock);
    }

    // The paint source and the target are the same item, but maybe with different meta. This means that we can simplify the painting by doing an item
    // conversion (e.g. blue carpet to red carpet).
    if (paintBlock == targetBlock && allowEasyConversion) {
      return mkItemStack(paintSource, targetBlock);
    }

    // The target is paintable, so let's check if the paint source is what was used to create it. If yes, then we unpaint it into it's original form.
    if (targetBlock == resultBlock && allowEasyConversion) {
      for (Block validTarget : validTargets) {
        if (paintBlock == validTarget) {
          return mkItemStack(paintSource, paintBlock);
        }
      }
    }

    return Prep.getEmpty();
  }

  @Override
  public boolean isValidTarget(@Nonnull ItemStack target) {
    // first check for exact matches, then check for item blocks
    if (Prep.isInvalid(target)) {
      return false;
    }

    Block blk = Block.getBlockFromItem(target.getItem());

    if (blk == Blocks.AIR) {
      return false;
    }

    if (blk == resultBlock) {
      return true;
    }

    for (int i = 0; i < validTargets.length; i++) {
      if (validTargets[i] == blk) {
        return true;
      }
    }

    return false;
  }

  @Override
  protected void registerTargetsWithTooltipProvider() {
  }

}
