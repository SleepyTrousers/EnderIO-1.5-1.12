package crazypants.enderio.machine.painter.recipe;

import javax.annotation.Nonnull;

import crazypants.enderio.machine.MachineRecipeInput;
import crazypants.enderio.paint.IPaintable;
import crazypants.enderio.paint.PainterUtil2;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class BasicPainterTemplate<T extends Block & IPaintable> extends AbstractPainterTemplate<T> {

  protected final T resultBlock;
  protected final Block[] validTargets;
  protected final boolean allowEasyConversion;

  public BasicPainterTemplate(boolean allowEasyConversion, T resultBlock, Block... validTargetBlocks) {
    this.resultBlock = resultBlock;
    this.validTargets = validTargetBlocks;
    this.allowEasyConversion = allowEasyConversion;
  }

  public BasicPainterTemplate(T resultBlock, Block... validTargetBlocks) {
    this(true, resultBlock, validTargetBlocks);
  }

  @Override
  public boolean isRecipe(ItemStack paintSource, ItemStack target) {
    return paintSource != null && isValidTarget(target) && PainterUtil2.isValid(paintSource, getTargetBlock(target));
  }

  @Override
  public boolean isPartialRecipe(ItemStack paintSource, ItemStack target) {
    if (paintSource == null) {
      return isValidTarget(target);
    }
    if (target == null) {
      return PainterUtil2.isValid(paintSource, getTargetBlock(null));
    }
    return isValidTarget(target) && PainterUtil2.isValid(paintSource, getTargetBlock(target));
  }

  @Override
  public ResultStack[] getCompletedResult(ItemStack paintSource, ItemStack target) {
    Block targetBlock = getTargetBlock(target);
    if (target == null || paintSource == null || targetBlock == null) {
      return new ResultStack[0];
    }
    Block paintBlock = PainterUtil2.getBlockFromItem(paintSource);
    if (paintBlock == null) {
      return new ResultStack[0];
    }
    IBlockState paintState = Block$getBlockFromItem_stack$getItem___$getStateFromMeta_stack$getMetadata___(paintSource, paintBlock);
    if (paintState == null) {
      return new ResultStack[0];
    }

    ItemStack result = isUnpaintingOp(paintSource, target);
    if (result == null) {
      result = mkItemStack(target, targetBlock);
      if (targetBlock == Block.getBlockFromItem(target.getItem()) && target.hasTagCompound()) {
        result.setTagCompound((NBTTagCompound) target.getTagCompound().copy());
      }
      ((IPaintable) targetBlock).setPaintSource(targetBlock, result, paintState);
    } else if (result.getItem() == target.getItem() && target.hasTagCompound()) {
      result.setTagCompound((NBTTagCompound) target.getTagCompound().copy());

      Block realresult = PainterUtil2.getBlockFromItem(result);
      if (realresult instanceof IPaintable) {
        ((IPaintable) realresult).setPaintSource(realresult, result, null);
      } else {
        PainterUtil2.setSourceBlock(result, null);
      }
    }
    return new ResultStack[] { new ResultStack(result) };
  }

  // This line is in this excessively named method to show up nicely in a stack trace
  private IBlockState Block$getBlockFromItem_stack$getItem___$getStateFromMeta_stack$getMetadata___(ItemStack paintSource, Block paintBlock) {
    return paintBlock.getStateFromMeta(paintSource.getItem().getMetadata(paintSource.getMetadata()));
  }

  protected @Nonnull ItemStack mkItemStack(@Nonnull ItemStack target, @Nonnull Block targetBlock) {
    Item itemFromBlock = Item.getItemFromBlock(targetBlock);
    if (itemFromBlock == null || itemFromBlock.isDamageable() || itemFromBlock.getHasSubtypes()) {
      return new ItemStack(targetBlock, 1, target.getItemDamage());
    } else {
      return new ItemStack(targetBlock, 1, 0);
    }
  }

  @Override
  public boolean isValidInput(MachineRecipeInput input) {
    if(input == null) {
      return false;
    }
    if(input.slotNumber == 0) {
      return isValidTarget(input.item);
    }
    if(input.slotNumber == 1) {
      return PainterUtil2.isValid(input.item, resultBlock);
    }
    return false;
  }

  protected T getTargetBlock(ItemStack target) {
    return resultBlock;
  }

  public ItemStack isUnpaintingOp(ItemStack paintSource, ItemStack target) {
    if (paintSource == null || target == null) {
      return null;
    }

    Block paintBlock = PainterUtil2.getBlockFromItem(paintSource);
    Block targetBlock = Block.getBlockFromItem(target.getItem());
    if (paintBlock == null || targetBlock == null) {
      return null;
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

    return null;
  }

  @Override
  public boolean isValidTarget(ItemStack target) {
    // first check for exact matches, then check for item blocks
    if(target == null) {
      return false;
    }
    
    Block blk = Block.getBlockFromItem(target.getItem());

    if (blk == resultBlock) {
      return true;
    }

    for (int i = 0; i < validTargets.length; i++) {
      if(validTargets[i] == blk) {
        return true;
      }
    }
    
    return false;
  }

}
