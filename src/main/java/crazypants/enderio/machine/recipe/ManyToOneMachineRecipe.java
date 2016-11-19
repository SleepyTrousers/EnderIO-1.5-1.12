package crazypants.enderio.machine.recipe;

import java.util.List;

import javax.annotation.Nonnull;

import crazypants.enderio.machine.MachineRecipeInput;
import net.minecraft.item.ItemStack;

public class ManyToOneMachineRecipe extends AbstractMachineRecipe {

  private final String uid;
  private final String machineName;
  private final ManyToOneRecipeManager recipeManager;
  
  public ManyToOneMachineRecipe(String uid, String machineName, ManyToOneRecipeManager recipeManager) {  
    this.uid = uid;
    this.machineName = machineName;
    this.recipeManager = recipeManager;
  }

  @Override
  public String getUid() {
    return uid;
  }

  @Override
  public IRecipe getRecipeForInputs(MachineRecipeInput[] inputs) {
    return recipeManager.getRecipeForInputs(inputs);
  }

  @Override
  public boolean isValidInput(MachineRecipeInput input) {
    if(input == null) {
      return false;
    }
    return recipeManager.isValidInput(input);
  }

  @Override
  public String getMachineName() {
    return machineName;
  }

  @Override
  public float getExperienceForOutput(ItemStack output) {
    return recipeManager.getExperianceForOutput(output);
  }

  public boolean isValidRecipeComponents(ItemStack[] resultInv) {
    return recipeManager.isValidRecipeComponents(resultInv);
  }

  @Nonnull
  public List<IManyToOneRecipe> getRecipesThatHaveTheseAsValidRecipeComponents(ItemStack[] resultInv) {
    return recipeManager.getRecipesThatHaveTheseAsValidRecipeComponents(resultInv);
  }

}
