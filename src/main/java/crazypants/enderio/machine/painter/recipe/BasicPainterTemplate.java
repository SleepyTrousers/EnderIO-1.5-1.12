package crazypants.enderio.machine.painter.recipe;

import java.util.Collections;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import crazypants.enderio.ModObject;
import crazypants.enderio.config.Config;
import crazypants.enderio.machine.IMachineRecipe;
import crazypants.enderio.machine.MachineRecipeInput;
import crazypants.enderio.machine.recipe.RecipeBonusType;
import crazypants.enderio.paint.IPaintable;
import crazypants.enderio.paint.PainterUtil2;
import static crazypants.enderio.machine.MachineRecipeInput.getInputForSlot;

public class BasicPainterTemplate<T extends Block & IPaintable> implements IMachineRecipe {

  public static int DEFAULT_ENERGY_PER_TASK = Config.painterEnergyPerTaskRF;

  protected final T resultBlock;
  protected final Block[] validTargets;

  protected BasicPainterTemplate(T resultBlock, Block... validTargetBlocks) {
    this.resultBlock = resultBlock;
    validTargets = validTargetBlocks;
  }

  @Override
  public int getEnergyRequired(MachineRecipeInput... inputs) {
    return DEFAULT_ENERGY_PER_TASK;
  }

  @Override
  public RecipeBonusType getBonusType(MachineRecipeInput... inputs) {
    return RecipeBonusType.NONE;
  }

  @Override
  public boolean isRecipe(MachineRecipeInput... inputs) {
    return getPaintSource(inputs) != null && isValidTarget(getTarget(inputs))
        && PainterUtil2.isValid(getPaintSource(inputs), getTargetBlock(getTarget(inputs)));
  }

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
  public ResultStack[] getCompletedResult(float chance, MachineRecipeInput... inputs) {
    ItemStack target = getTarget(inputs);
    Block targetBlock = getTargetBlock(target);
    ItemStack paintSource = getPaintSource(inputs);
    if (target == null || paintSource == null || targetBlock == null) {
      return new ResultStack[0];
    }
    Block paintBlock = Block.getBlockFromItem(paintSource.getItem());
    if (paintBlock == null) {
      return new ResultStack[0];
    }
    IBlockState paintState = paintBlock.getStateFromMeta(paintSource.getMetadata());
    if (paintState == null) {
      return new ResultStack[0];
    }

    ItemStack result = isUnpaintingOp(paintSource, target);
    if (result == null) {
      result = new ItemStack(targetBlock, 1, target.getItemDamage());
      ((IPaintable) targetBlock).setPaintSource(targetBlock, result, paintState);
    }
    return new ResultStack[] { new ResultStack(result) };
  }

  public ItemStack getTarget(MachineRecipeInput... inputs) {
    return getInputForSlot(0, inputs);
  }

  public ItemStack getPaintSource(MachineRecipeInput... inputs) {
    return getInputForSlot(1, inputs);
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

  @Override
  public String getMachineName() {
    return ModObject.blockPainter.unlocalisedName;
  }

  protected T getTargetBlock(ItemStack target) {
    return resultBlock;
  }

  public ItemStack isUnpaintingOp(ItemStack paintSource, ItemStack target) {
    if (paintSource == null || target == null) {
      return null;
    }

    Block paintBlock = Block.getBlockFromItem(paintSource.getItem());
    Block targetBlock = Block.getBlockFromItem(target.getItem());
    if (paintBlock == null || targetBlock == null) {
      return null;
    }

    // The paint source is the paintable block we produce with this recipe. We know it must be unpainted, as paint sources that are painted are rejected. So we
    // user wants the input item but without its paint. The input item must be able to exist in the world unpainted because it does so as paint source. So we
    // copy the input item without its paint information.
    if (paintBlock == resultBlock) {
      return new ItemStack(targetBlock, 1, target.getItemDamage());
    }

    // The paint source and the target are the same item, but maybe with different meta. This means that we can simplify the painting by doing an item
    // conversion (e.g. blue carpet to red carpet).
    if (paintBlock == targetBlock) {
      return new ItemStack(targetBlock, 1, paintSource.getItemDamage());
    }

    // The target is paintable, so let's check if the paint source is what was used to create it. If yes, then we unpaint it into it's original form.
    if (targetBlock == resultBlock) {
      for (Block validTarget : validTargets) {
        if (paintBlock == validTarget) {
          return new ItemStack(paintBlock, 1, paintSource.getItemDamage());
        }
      }
    }

    return null;
  }

  public boolean isValidTarget(ItemStack target) {
    // first check for exact matches, then check for item blocks
    if(target == null) {
      return false;
    }
    
    Block blk = Block.getBlockFromItem(target.getItem());
    if(blk == null) {
      return false;
    }
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

  @Override
  public String getUid() {
    return getClass().getCanonicalName() + "@" + Integer.toHexString(hashCode());
  }

  protected Item getResultId(ItemStack target) {
    return target.getItem();
  }

  public int getQuantityConsumed(MachineRecipeInput input) {
    return input.slotNumber == 0 ? 1 : 0;
  }

  @Override
  public List<MachineRecipeInput> getQuantitiesConsumed(MachineRecipeInput[] inputs) {
    MachineRecipeInput consume = null;
    for (MachineRecipeInput input : inputs) {
      if(input != null && input.slotNumber == 0 && input.item != null) {
        ItemStack consumed = input.item.copy();
        consumed.stackSize = 1;
        consume = new MachineRecipeInput(input.slotNumber, consumed);
      }
    }
    if(consume != null) {
      return Collections.singletonList(consume);
    }
    return null;
  }

  @Override
  public float getExperienceForOutput(ItemStack output) {
    return 0;
  }

}
