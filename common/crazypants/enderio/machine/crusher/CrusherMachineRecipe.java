package crazypants.enderio.machine.crusher;

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
import crazypants.enderio.crafting.impl.RecipeOutput;
import crazypants.enderio.machine.IMachineRecipe;
import crazypants.enderio.machine.MachineRecipeInput;

public class CrusherMachineRecipe implements IMachineRecipe {

  @Override
  public String getUid() {
    return "CrusherRecipe";
  }

  @Override
  public float getEnergyRequired(MachineRecipeInput... inputs) {
    if(inputs == null || inputs.length <= 0) {
      return 0;
    }
    CrusherRecipe recipe = CrusherRecipeManager.instance.getRecipeForInput(inputs[0].item);
    return recipe == null ? 0 : recipe.getEnergyRequired();
  }

  @Override
  public boolean isRecipe(MachineRecipeInput... inputs) {
    if(inputs == null || inputs.length <= 0) {
      return false;
    }
    CrusherRecipe recipe = CrusherRecipeManager.instance.getRecipeForInput(inputs[0].item);
    return recipe != null;
  }

  @Override
  public ItemStack[] getCompletedResult(float chance, MachineRecipeInput... inputs) {
    if(inputs == null || inputs.length <= 0) {
      return new ItemStack[0];
    }
    CrusherRecipe recipe = CrusherRecipeManager.instance.getRecipeForInput(inputs[0].item);
    if(recipe == null) {
      return new ItemStack[0];
    }
    CrusherOutput[] outputs = recipe.getOutput();
    if(outputs == null) {
      return new ItemStack[0];
    }
    List<ItemStack> result = new ArrayList<ItemStack>();
    for (CrusherOutput output : outputs) {
      if(output.getChance() >= chance) {
        result.add(output.getOutput());
      }
    }
    return result.toArray(new ItemStack[result.size()]);

  }

  @Override
  public boolean isValidInput(MachineRecipeInput input) {
    if(input == null) {
      return false;
    }
    return CrusherRecipeManager.instance.getRecipeForInput(input.item) != null;
  }

  @Override
  public String getMachineName() {
    return ModObject.blockCrusher.unlocalisedName;
  }

  @Override
  public MachineRecipeInput[] getQuantitiesConsumed(MachineRecipeInput[] inputs) {
    MachineRecipeInput[] res = new MachineRecipeInput[inputs.length];
    int i = 0;
    for (MachineRecipeInput input : inputs) {
      ItemStack used = input.item.copy();
      used.stackSize = 1;
      MachineRecipeInput ri = new MachineRecipeInput(input.slotNumber, used);
      res[i] = ri;
      i++;
    }
    return res;
  }

  @Override
  public float getExperianceForOutput(ItemStack output) {
    return 0;
  }

  @Override
  public List<IEnderIoRecipe> getAllRecipes() {
    List<IEnderIoRecipe> result = new ArrayList<IEnderIoRecipe>();
    List<CrusherRecipe> recipes = CrusherRecipeManager.getInstance().getRecipes();
    for (CrusherRecipe cr : recipes) {
      IRecipeInput input = new RecipeInput(cr.getInput());
      List<IRecipeComponent> components = new ArrayList<IRecipeComponent>();
      components.add(input);
      for (CrusherOutput co : cr.getOutput()) {
        IRecipeOutput output = new RecipeOutput(co.getOutput(), co.getChance());
        components.add(output);
      }
      result.add(new EnderIoRecipe(IEnderIoRecipe.SAG_MILL_ID, cr.getEnergyRequired(), components));
    }
    return result;
  }
}
