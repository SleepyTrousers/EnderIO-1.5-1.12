package crazypants.enderio.machine.painter.recipe;

import crazypants.enderio.machine.MachineObject;
import crazypants.enderio.config.Config;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.List;

import static crazypants.enderio.machine.MachineRecipeInput.getInputForSlot;

public abstract class AbstractPainterTemplate<T> implements IMachineRecipe {

  public AbstractPainterTemplate() {
    super();
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
  public int getEnergyRequired(MachineRecipeInput... inputs) {
    return Config.painterEnergyPerTaskRF;
  }

  @Override
  public @Nonnull RecipeBonusType getBonusType(MachineRecipeInput... inputs) {
    return RecipeBonusType.NONE;
  }

  public @Nonnull ItemStack getTarget(MachineRecipeInput... inputs) {
    return getInputForSlot(0, inputs);
  }

  public @Nonnull ItemStack getPaintSource(MachineRecipeInput... inputs) {
    return getInputForSlot(1, inputs);
  }

  @Override
  public final boolean isRecipe(MachineRecipeInput... inputs) {
    return isRecipe(getPaintSource(inputs), getTarget(inputs));
  }

  @Override
  public final @Nonnull ResultStack[] getCompletedResult(float chance, MachineRecipeInput... inputs) {
    return getCompletedResult(getPaintSource(inputs), getTarget(inputs));
  }

  @Override
  public @Nonnull String getMachineName() {
    return MachineObject.blockPainter.getUnlocalisedName();
  }

  @Override
  public @Nonnull String getUid() {
    return getClass().getCanonicalName() + "@" + Integer.toHexString(hashCode());
  }

  public int getQuantityConsumed(MachineRecipeInput input) {
    return input.slotNumber == 0 ? 1 : 0;
  }

  @Override
  public @Nonnull List<MachineRecipeInput> getQuantitiesConsumed(MachineRecipeInput... inputs) {
    MachineRecipeInput consume = null;
    for (MachineRecipeInput input : inputs) {
      if(input != null && input.slotNumber == 0 && input.item != null) {
        ItemStack consumed = input.item.copy();
        consumed.setCount(1);
        consume = new MachineRecipeInput(input.slotNumber, consumed);
      }
    }
    if(consume != null) {
      return Collections.singletonList(consume);
    }
    return null;
  }

  @Override
  public float getExperienceForOutput(@Nonnull ItemStack output) {
    return 0;
  }

  @Override
  public abstract boolean isValidInput(@Nonnull MachineRecipeInput input);

}