package crazypants.enderio.recipe.spawner;

import java.util.Collections;
import java.util.List;

import javax.annotation.Nonnull;

import crazypants.enderio.recipe.IMachineRecipe;
import crazypants.enderio.recipe.MachineRecipeInput;
import crazypants.enderio.recipe.MachineRecipeRegistry;
import crazypants.enderio.recipe.RecipeBonusType;
import net.minecraft.item.ItemStack;

public class DummyRecipe implements IMachineRecipe {

  @Override
  public @Nonnull String getUid() {
    return "PoweredTaskRecipe";
  }

  @Override
  public int getEnergyRequired(@Nonnull MachineRecipeInput... inputs) {
    // NB: This value is not actually used, see createTask in the tile
    return 8000;
  }

  @Override
  public boolean isRecipe(@Nonnull MachineRecipeInput... inputs) {
    return true;
  }

  @Override
  public @Nonnull ResultStack[] getCompletedResult(float randomChance, @Nonnull MachineRecipeInput... inputs) {
    return new ResultStack[0];
  }

  @Override
  public @Nonnull RecipeBonusType getBonusType(@Nonnull MachineRecipeInput... inputs) {
    return RecipeBonusType.NONE;
  }

  @Override
  public float getExperienceForOutput(@Nonnull ItemStack output) {
    return 0;
  }

  @Override
  public boolean isValidInput(@Nonnull MachineRecipeInput input) {
    return false;
  }

  @Override
  public @Nonnull String getMachineName() {
    return MachineRecipeRegistry.SPAWNER;
  }

  @Override
  public @Nonnull List<MachineRecipeInput> getQuantitiesConsumed(@Nonnull MachineRecipeInput... inputs) {
    return Collections.emptyList();
  }

}
