package crazypants.enderio.machine.painter.recipe;

import java.util.Collections;
import java.util.List;

import crazypants.enderio.ModObject;
import crazypants.enderio.config.Config;
import crazypants.enderio.machine.IMachineRecipe;
import crazypants.enderio.machine.MachineRecipeInput;
import crazypants.enderio.machine.recipe.RecipeBonusType;
import net.minecraft.item.ItemStack;

import static crazypants.enderio.machine.MachineRecipeInput.getInputForSlot;

public abstract class AbstractPainterTemplate<T> implements IMachineRecipe {

  public AbstractPainterTemplate() {
    super();
  }

  public abstract boolean isValidTarget(ItemStack target);

  public abstract ResultStack[] getCompletedResult(ItemStack paintSource, ItemStack target);

  public abstract boolean isRecipe(ItemStack paintSource, ItemStack target);

  public abstract boolean isPartialRecipe(ItemStack paintSource, ItemStack target);

  @Override
  public int getEnergyRequired(MachineRecipeInput... inputs) {
    return Config.painterEnergyPerTaskRF;
  }

  @Override
  public RecipeBonusType getBonusType(MachineRecipeInput... inputs) {
    return RecipeBonusType.NONE;
  }

  public ItemStack getTarget(MachineRecipeInput... inputs) {
    return getInputForSlot(0, inputs);
  }

  public ItemStack getPaintSource(MachineRecipeInput... inputs) {
    return getInputForSlot(1, inputs);
  }

  @Override
  public final boolean isRecipe(MachineRecipeInput... inputs) {
    return isRecipe(getPaintSource(inputs), getTarget(inputs));
  }

  @Override
  public final ResultStack[] getCompletedResult(float chance, MachineRecipeInput... inputs) {
    return getCompletedResult(getPaintSource(inputs), getTarget(inputs));
  }

  @Override
  public String getMachineName() {
    return ModObject.blockPainter.getUnlocalisedName();
  }

  @Override
  public String getUid() {
    return getClass().getCanonicalName() + "@" + Integer.toHexString(hashCode());
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

  @Override
  public abstract boolean isValidInput(MachineRecipeInput input);

}