package crazypants.enderio.machine.alloy;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.item.ItemStack;
import crazypants.enderio.ModObject;
import crazypants.enderio.crafting.IEnderIoRecipe;
import crazypants.enderio.crafting.IRecipeComponent;
import crazypants.enderio.crafting.IRecipeInput;
import crazypants.enderio.crafting.IRecipeOutput;
import crazypants.enderio.crafting.impl.EnderIoRecipe;
import crazypants.enderio.crafting.impl.RecipeInput;
import crazypants.enderio.machine.MachineRecipeInput;
import crazypants.enderio.machine.recipe.AbstractMachineRecipe;
import crazypants.enderio.machine.recipe.IRecipe;
import crazypants.enderio.machine.recipe.RecipeOutput;

public class AlloyMachineRecipe extends AbstractMachineRecipe {

  @Override
  public String getUid() {
    return "AlloySmelterRecipe";
  }

  @Override
  public IRecipe getRecipeForInputs(MachineRecipeInput[] inputs) {
    List<ItemStack> stacks = new ArrayList<ItemStack>();
    for (MachineRecipeInput mi : inputs) {
      if(mi != null && mi.item != null) {
        stacks.add(mi.item);
      }
    }
    return AlloyRecipeManager.instance.getRecipeForInputs(stacks.toArray(new ItemStack[stacks.size()]));
  }

  @Override
  public boolean isValidInput(MachineRecipeInput input) {
    if(input == null) {
      return false;
    }
    return AlloyRecipeManager.instance.isValidInput(input);
  }

  @Override
  public String getMachineName() {
    return ModObject.blockAlloySmelter.unlocalisedName;
  }

  @Override
  public float getExperianceForOutput(ItemStack output) {
    return AlloyRecipeManager.instance.getExperianceForOutput(output);
  }

  @Override
  public List<IEnderIoRecipe> getAllRecipes() {
    List<IEnderIoRecipe> result = new ArrayList<IEnderIoRecipe>();
    List<IAlloyRecipe> recipes = AlloyRecipeManager.getInstance().getRecipes();
    for (IRecipe cr : recipes) {
      List<IRecipeComponent> components = new ArrayList<IRecipeComponent>();
      for (crazypants.enderio.machine.recipe.RecipeInput ri : cr.getInputs()) {
        if(ri.getInput() != null) {
          IRecipeInput input = new RecipeInput(ri.getInput(), -1, ri.getEquivelentInputs());
          components.add(input);
        }
      }

      for (RecipeOutput co : cr.getOutputs()) {
        IRecipeOutput output = new crazypants.enderio.crafting.impl.RecipeOutput(co.getOutput(), co.getChance());
        components.add(output);
      }
      result.add(new EnderIoRecipe(IEnderIoRecipe.ALLOY_SMELTER_ID, cr.getEnergyRequired(), components));
    }
    return result;
  }

  public boolean isValidRecipeComponents(ItemStack[] resultInv) {
    return AlloyRecipeManager.instance.isValidRecipeComponents(resultInv);
  }
}