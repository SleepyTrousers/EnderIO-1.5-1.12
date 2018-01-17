package crazypants.enderio.base.recipe.painter;

import static crazypants.enderio.base.recipe.MachineRecipeInput.getInputForSlot;

import java.util.Collections;
import java.util.List;

import javax.annotation.Nonnull;

import com.enderio.core.common.util.NNList;

import crazypants.enderio.base.config.Config;
import crazypants.enderio.base.recipe.IMachineRecipe;
import crazypants.enderio.base.recipe.MachineRecipeInput;
import crazypants.enderio.base.recipe.MachineRecipeRegistry;
import crazypants.enderio.base.recipe.RecipeBonusType;
import crazypants.enderio.util.Prep;
import net.minecraft.item.ItemStack;

public abstract class AbstractPainterTemplate<T> implements IMachineRecipe {

  public AbstractPainterTemplate() {
    registerTargetsWithTooltipProvider();
  }

  /**
   * An reminder to register things with the PaintTooltipUtil if needed...
   */
  protected abstract void registerTargetsWithTooltipProvider();

  public abstract boolean isValidTarget(@Nonnull ItemStack target);

  public abstract @Nonnull ResultStack[] getCompletedResult(@Nonnull ItemStack paintSource, @Nonnull ItemStack target);

  public abstract boolean isRecipe(@Nonnull ItemStack paintSource, @Nonnull ItemStack target);

  public abstract boolean isPartialRecipe(@Nonnull ItemStack paintSource, @Nonnull ItemStack target);

  @Override
  public int getEnergyRequired(@Nonnull NNList<MachineRecipeInput> inputs) {
    return Config.painterEnergyPerTaskRF;
  }

  @Override
  public @Nonnull RecipeBonusType getBonusType(@Nonnull NNList<MachineRecipeInput> inputs) {
    return RecipeBonusType.NONE;
  }

  public @Nonnull ItemStack getTarget(@Nonnull NNList<MachineRecipeInput> inputs) {
    return getInputForSlot(0, inputs);
  }

  public @Nonnull ItemStack getPaintSource(@Nonnull NNList<MachineRecipeInput> inputs) {
    return getInputForSlot(1, inputs);
  }

  @Override
  public final boolean isRecipe(@Nonnull NNList<MachineRecipeInput> inputs) {
    return isRecipe(getPaintSource(inputs), getTarget(inputs));
  }

  @Override
  public final @Nonnull ResultStack[] getCompletedResult(float chance, @Nonnull NNList<MachineRecipeInput> inputs) {
    return getCompletedResult(getPaintSource(inputs), getTarget(inputs));
  }

  @Override
  public @Nonnull String getMachineName() {
    return MachineRecipeRegistry.PAINTER;
  }

  @Override
  public @Nonnull String getUid() {
    return getClass().getCanonicalName() + "@" + Integer.toHexString(hashCode());
  }

  public int getQuantityConsumed(@Nonnull MachineRecipeInput input) {
    return input.slotNumber == 0 ? 1 : 0;
  }

  @Override
  public @Nonnull List<MachineRecipeInput> getQuantitiesConsumed(@Nonnull NNList<MachineRecipeInput> inputs) {
    MachineRecipeInput consume = null;
    for (MachineRecipeInput input : inputs) {
      if (input != null && input.slotNumber == 0 && Prep.isValid(input.item)) {
        ItemStack consumed = input.item.copy();
        consumed.setCount(1);
        consume = new MachineRecipeInput(input.slotNumber, consumed);
      }
    }
    if(consume != null) {
      return Collections.singletonList(consume);
    }
    return Collections.emptyList();
  }

  @Override
  public float getExperienceForOutput(@Nonnull ItemStack output) {
    return 0;
  }

  @Override
  public abstract boolean isValidInput(@Nonnull MachineRecipeInput input);

}