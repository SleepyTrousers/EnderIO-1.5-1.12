package crazypants.enderio.recipe.spawner;

import java.util.Collections;
import java.util.List;

import net.minecraft.item.ItemStack;
import crazypants.enderio.init.ModObject;
import crazypants.enderio.recipe.IMachineRecipe;
import crazypants.enderio.recipe.MachineRecipeInput;
import crazypants.enderio.recipe.RecipeBonusType;

public class DummyRecipe implements IMachineRecipe {

  @Override
  public String getUid() {
    return "PoweredTaskRecipe";
  }

  @Override
  public int getEnergyRequired(MachineRecipeInput... inputs) {
    //NB: This value is not actually used, see createTask in the tile
    return 8000;
  }

  @Override
  public boolean isRecipe(MachineRecipeInput... inputs) {
    return true;
  }

  @Override
  public ResultStack[] getCompletedResult(float randomChance, MachineRecipeInput... inputs) {
    return new ResultStack[0];
  }

  @Override
  public RecipeBonusType getBonusType(MachineRecipeInput... inputs) {
    return RecipeBonusType.NONE;
  }

  @Override
  public float getExperienceForOutput(ItemStack output) {
    return 0;
  }

  @Override
  public boolean isValidInput(MachineRecipeInput input) {
    return false;
  }

  @Override
  public String getMachineName() {
    return ModObject.blockPoweredSpawner.getUnlocalisedName();
  }

  @Override
  public List<MachineRecipeInput> getQuantitiesConsumed(MachineRecipeInput[] inputs) {
    return Collections.emptyList();
  }

}
