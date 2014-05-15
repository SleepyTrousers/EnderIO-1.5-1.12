package crazypants.enderio.nei;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.item.ItemStack;

import org.lwjgl.opengl.GL11;

import codechicken.nei.PositionedStack;
import codechicken.nei.recipe.TemplateRecipeHandler;
import crazypants.enderio.crafting.IEnderIoRecipe;
import crazypants.enderio.crafting.IRecipeInput;
import crazypants.enderio.crafting.IRecipeOutput;
import crazypants.enderio.crafting.RecipeReigistry;
import crazypants.enderio.machine.alloy.GuiAlloySmelter;
import crazypants.enderio.machine.power.PowerDisplayUtil;

public class AlloySmelterRecipeHandler extends TemplateRecipeHandler {

  @Override
  public String getRecipeName() {
    return "Alloy Smelter";
  }

  @Override
  public String getGuiTexture() {
    return "enderio:textures/gui/alloySmelter.png";
  }


  @Override
  public Class<? extends GuiContainer> getGuiClass() {
    return GuiAlloySmelter.class;
  }

  @Override
  public String getOverlayIdentifier() {
    return "EnderIOAlloySmelter";
  }

  @Override
  public void loadTransferRects() {
    transferRects.add(new TemplateRecipeHandler.RecipeTransferRect(new Rectangle(149, 32, 16, 16), "EnderIOAlloySmelter", new Object[0]));
  }

  @Override
  public void loadCraftingRecipes(ItemStack result) {

    if(result == null) {
      return;
    }

    List<IEnderIoRecipe> recipes = RecipeReigistry.instance.getRecipesForOutput(IEnderIoRecipe.ALLOY_SMELTER_ID, result);

    for (IEnderIoRecipe recipe : recipes) {
      for (IRecipeOutput output : recipe.getOutputs()) {
        AlloySmelterRecipe res = new AlloySmelterRecipe(recipe.getRequiredEnergy(), recipe.getInputs(), output.getItem());
        arecipes.add(res);
      }
    }
  }

  @Override
  public void loadCraftingRecipes(String outputId, Object... results)
  {
    if(outputId.equals("EnderIOAlloySmelter") && getClass() == AlloySmelterRecipeHandler.class)
    {
      List<IEnderIoRecipe> recipes = RecipeReigistry.instance.getRecipesForCrafter(IEnderIoRecipe.ALLOY_SMELTER_ID);
      for (IEnderIoRecipe recipe : recipes) {
        AlloySmelterRecipe res = new AlloySmelterRecipe(recipe.getRequiredEnergy(), recipe.getInputs(), recipe.getOutputs().get(0).getItem());
        arecipes.add(res);
      }
    } else {
      super.loadCraftingRecipes(outputId, results);
    }
  }

  @Override
  public void loadUsageRecipes(ItemStack ingredient) {

    List<IEnderIoRecipe> recipes = RecipeReigistry.instance.getRecipesForCrafter(IEnderIoRecipe.ALLOY_SMELTER_ID);

    for (IEnderIoRecipe recipe : recipes) {
      if(recipe.isInput(ingredient)) {
        for (IRecipeOutput output : recipe.getOutputs()) {
          AlloySmelterRecipe res = new AlloySmelterRecipe(recipe.getRequiredEnergy(), recipe.getInputs(), output.getItem());
          res.setIngredientPermutation(res.input, ingredient);
          arecipes.add(res);
        }
      }
    }
  }

  @Override
  public void drawExtras(int recipeIndex) {
    drawProgressBar(98, 25, 176, 0, 22, 13, 48, 3);
    drawProgressBar(50, 25, 176, 0, 22, 13, 48, 3);
    AlloySmelterRecipe recipe = (AlloySmelterRecipe) arecipes.get(recipeIndex);
    String energyString = PowerDisplayUtil.formatPower(recipe.getEnergy()) + " " + PowerDisplayUtil.abrevation();
    Minecraft.getMinecraft().fontRenderer.drawString(energyString, 100, 50, 0xFFFFFFFF);
  }

  public List<ItemStack> getInputs(IRecipeInput input) {
    List<ItemStack> result = new ArrayList<ItemStack>();
    result.add(input.getItem());
    result.addAll(input.getEquivelentInputs());
    return result;
  }

  public class AlloySmelterRecipe extends TemplateRecipeHandler.CachedRecipe {

    private ArrayList<PositionedStack> input;
    private PositionedStack output;
    private double energy;

    // Possible energy cost in the future?
    public double getEnergy() {
      return energy;
    }

    @Override
    public List<PositionedStack> getIngredients() {
      return getCycledIngredients(cycleticks / 20, input);
    }

    @Override
    public PositionedStack getResult() {
      return output;
    }

    public AlloySmelterRecipe(float energy, List<IRecipeInput> ingredients, ItemStack result) {
      int recipeSize = ingredients.size();
      this.input = new ArrayList<PositionedStack>();
      int yOff = 8;
      if(recipeSize > 0) {
        this.input.add(new PositionedStack(getInputs(ingredients.get(0)), 49, 14 - yOff));
      }
      if(recipeSize > 1) {
        this.input.add(new PositionedStack(getInputs(ingredients.get(1)), 73, 4 - yOff));
      }
      if(recipeSize > 2) {
        this.input.add(new PositionedStack(getInputs(ingredients.get(2)), 98, 14 - yOff));
      }
      if(result != null) {
        this.output = new PositionedStack(result, 74, 54 - yOff);
      }
      this.energy = energy; //If we wanted to do an energy cost
    }
  }
}
