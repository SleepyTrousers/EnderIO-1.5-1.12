package crazypants.enderio.machine.painter;

import static crazypants.enderio.machine.MachineRecipeInput.getInputForSlot;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import crazypants.enderio.ModObject;
import crazypants.enderio.machine.IMachineRecipe;
import crazypants.enderio.machine.MachineRecipeInput;
import crazypants.util.Util;

public abstract class BasicPainterTemplate implements IMachineRecipe {

  // 5 seconds at the default energy use of 2 mj per tick.
  public static float DEFAULT_ENERGY_PER_TASK = 200;

  public static boolean isValidSourceDefault(ItemStack paintSource) {
    if(paintSource == null) {
      return false;
    }
    Block block = Util.getBlockFromItemId(paintSource.itemID);
    if(block == null) {
      return false;
    }
    return Block.isNormalCube(block.blockID) || block.blockID == Block.glass.blockID;
  }

  protected final int[] validIds;

  protected BasicPainterTemplate(int... validTargetBlocksIds) {
    this.validIds = validTargetBlocksIds;
  }

  @Override
  public float getEnergyRequired(MachineRecipeInput... inputs) {
    return DEFAULT_ENERGY_PER_TASK;
  }

  @Override
  public boolean isRecipe(MachineRecipeInput... inputs) {
    return isValidTarget(getTarget(inputs)) && isValidPaintSource(getPaintSource(inputs));
  }

  @Override
  public ItemStack[] getCompletedResult(float chance, MachineRecipeInput... inputs) {
    ItemStack target = getTarget(inputs);
    ItemStack paintSource = getPaintSource(inputs);
    if(target == null || paintSource == null) {
      return null;
    }
    ItemStack result = new ItemStack(getResultId(target), 1, target.getItemDamage());
    PainterUtil.setSourceBlock(result, paintSource.itemID, paintSource.getItemDamage());
    return new ItemStack[] { result };
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
      return isValidPaintSource(input.item);
    }
    return false;
  }

  @Override
  public String getMachineName() {
    return ModObject.blockPainter.unlocalisedName;
  }

  public boolean isValidPaintSource(ItemStack paintSource) {
    return isValidSourceDefault(paintSource);
  }

  public boolean isValidTarget(ItemStack target) {
    // first check for exact matches, then check for item blocks
    if(target == null) {
      return false;
    }

    for (int i = 0; i < validIds.length; i++) {
      if(validIds[i] == target.itemID) {
        return true;
      }
    }

    Block blk = Util.getBlockFromItemId(target.itemID);
    if(blk == null) {
      return false;
    }
    for (int i = 0; i < validIds.length; i++) {
      if(validIds[i] == blk.blockID) {
        return true;
      }
    }
    return false;
  }

  @Override
  public String getUid() {
    return getClass().getCanonicalName();
  }

  protected int getResultId(ItemStack target) {
    return target.itemID;
  }

  public int getQuantityConsumed(MachineRecipeInput input) {
    return input.slotNumber == 0 ? 1 : 0;
  }

  @Override
  public MachineRecipeInput[] getQuantitiesConsumed(MachineRecipeInput[] inputs) {
    MachineRecipeInput consume = null;
    for (MachineRecipeInput input : inputs) {
      if(input != null && input.slotNumber == 0 && input.item != null) {
        ItemStack consumed = input.item.copy();
        consumed.stackSize = 1;
        consume = new MachineRecipeInput(input.slotNumber, consumed);
      }
    }
    if(consume != null) {
      return new MachineRecipeInput[] { consume };
    }
    return null;
  }

  @Override
  public float getExperianceForOutput(ItemStack output) {
    return 0;
  }

}
