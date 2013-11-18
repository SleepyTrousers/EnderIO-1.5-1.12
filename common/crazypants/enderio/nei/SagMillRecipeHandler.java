package crazypants.enderio.nei;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Icon;

import org.lwjgl.opengl.GL11;

import codechicken.core.gui.GuiDraw;
import codechicken.nei.PositionedStack;
import codechicken.nei.recipe.GuiRecipe;
import codechicken.nei.recipe.TemplateRecipeHandler;
import crazypants.enderio.crafting.IEnderIoRecipe;
import crazypants.enderio.crafting.IRecipeInput;
import crazypants.enderio.crafting.IRecipeOutput;
import crazypants.enderio.crafting.RecipeReigistry;
import crazypants.enderio.machine.crusher.GuiCrusher;
import crazypants.render.RenderUtil;

public class SagMillRecipeHandler extends TemplateRecipeHandler {

  private RecipeComparator comparator = new RecipeComparator();

  @Override
  public String getRecipeName() {
    return "SAG Mill";
  }

  @Override
  public String getGuiTexture() {
    return "enderio:textures/gui/crusher.png";
  }

  public PositionedStack getResult() {
    return null;
  }

  @Override
  public Class<? extends GuiContainer> getGuiClass() {
    return GuiCrusher.class;
  }

  @Override
  public String getOverlayIdentifier() {
    return "EnderIOSagMill";
  }

  @Override
  public void loadTransferRects() {
    //Set this up later to show all alloy recipes
    transferRects.add(new TemplateRecipeHandler.RecipeTransferRect(new Rectangle(81-5, 32-11, 15, 23), "EnderIOSagMill", new Object[0]));
  }

  @Override
  public void loadCraftingRecipes(final ItemStack result) {

    List<IEnderIoRecipe> recipes = RecipeReigistry.instance.getRecipesForOutput(IEnderIoRecipe.SAG_MILL_ID, result);

    List<MillRecipe> toAdd = new ArrayList<MillRecipe>();

    for (IEnderIoRecipe recipe : recipes) {
      List<IRecipeOutput> ro = recipe.getOutputs();
      MillRecipe res = new MillRecipe(result, recipe.getRequiredEnergy(), recipe.getInputs().get(0), ro);
      toAdd.add(res);
    }

    Collections.sort(toAdd, comparator);

    arecipes.addAll(toAdd);
  }

  @Override
  public void loadCraftingRecipes(String outputId, Object... results)
  {
    if(outputId.equals("EnderIOSagMill") && getClass() == SagMillRecipeHandler.class)
    {
      List<IEnderIoRecipe> recipes = RecipeReigistry.instance.getRecipesForCrafter(IEnderIoRecipe.SAG_MILL_ID);

      for (IEnderIoRecipe recipe : recipes) {
        for (IRecipeOutput output : recipe.getOutputs()) {
          MillRecipe res = new MillRecipe(recipe.getRequiredEnergy(), recipe.getInputs().get(0), recipe.getOutputs());
          arecipes.add(res);
        }
      }
    } else {
      super.loadCraftingRecipes(outputId, results);
    }
  }

  @Override
  public void loadUsageRecipes(ItemStack ingredient) {

    List<IEnderIoRecipe> recipes = RecipeReigistry.instance.getRecipesForCrafter(IEnderIoRecipe.SAG_MILL_ID);

    for (IEnderIoRecipe recipe : recipes) {
      if (recipe.isInput(ingredient)) {
        MillRecipe res = new MillRecipe(recipe.getRequiredEnergy(), recipe.getInputs().get(0), recipe.getOutputs());
        arecipes.add(res);
      }
    }
  }

  @Override
  public void drawExtras(int recipeIndex) {
    drawProgressBar(98, 33, 176, 0, 22, 13, 48, 3);
    drawProgressBar(50, 33, 176, 0, 22, 13, 48, 3);

    MillRecipe recipe = (MillRecipe) arecipes.get(recipeIndex);
    String energyString = (int) recipe.getEnergy() + " MJ";
    GuiDraw.drawString(energyString, 90, 22, 0xFFFFFFFF);
  }

  @Override
  public List<String> handleItemTooltip(GuiRecipe gui, ItemStack stack, List<String> currenttip, int recipeIndex) {
    MillRecipe recipe = (MillRecipe) arecipes.get(recipeIndex);
    float chance = recipe.getChanceForOutput(stack);
    if(chance > 0 && chance < 1) {
      int chanceInt = (int) (chance * 100);
      if(chanceInt == 0) {
        currenttip.add("Less than 1%");
      } else {
        currenttip.add(chanceInt + "% Chance");
      }
    }
    return currenttip;
  }

  public void renderIcon(Icon icon, double x, double y, double width, double height, double zLevel) {

    Tessellator tessellator = Tessellator.instance;

    RenderUtil.bindItemTexture();
    GL11.glColor3f(1, 1, 1);
    tessellator.startDrawingQuads();

    float minU = icon.getMinU();
    float minV = icon.getMinV();
    float maxU = icon.getMaxU();
    float maxV = icon.getMaxV();
    tessellator.addVertexWithUV(x, y + height, zLevel, minU, maxV);
    tessellator.addVertexWithUV(x + width, y + height, zLevel, maxU, maxV);
    tessellator.addVertexWithUV(x + width, y + 0, zLevel, maxU, minV);
    tessellator.addVertexWithUV(x, y + 0, zLevel, minU, minV);

    tessellator.draw();
  }

  @Override
  public void drawProgressBar(int x, int y, int tx, int ty, int w, int h, float completion, int direction) {
    super.drawProgressBar(87 - 13, 37 - 16, 200, 0, 17, 24, completion, 1);
  }

  public List<ItemStack> getInputs(IRecipeInput input) {
    List<ItemStack> result = new ArrayList<ItemStack>();
    result.add(input.getItem());
    result.addAll(input.getEquivelentInputs());
    return result;
  }

  private static Point offset = new Point(5, 11);

  public class MillRecipe extends TemplateRecipeHandler.CachedRecipe {

    protected int numTargetOuput;
    protected int indexOfTargetOutput;
    private PositionedStack input;
    private PositionedStack output;
    private ArrayList<PositionedStack> otherOutputs;
    private float[] outputChance;
    private double energy;

    // Possible energy cost in the future?
    public double getEnergy() {
      return energy;
    }

    public float getChanceForOutput(ItemStack stack) {
      if(output.item.equals(stack)) {
        return outputChance[0];
      }
      for (int i = 0; i < otherOutputs.size(); i++) {
        if(otherOutputs.get(i).item.equals(stack)) {
          return outputChance[i + 1];
        }
      }
      return -1;
    }

    @Override
    public PositionedStack getIngredient() {
      randomRenderPermutation(input, cycleticks / 20);
      return input;
    }

    @Override
    public PositionedStack getResult() {
      return output;
    }

    @Override
    public List<PositionedStack> getOtherStacks() {
      return otherOutputs;
    }

    public MillRecipe(float energy, IRecipeInput ingredient, List<IRecipeOutput> outputs) {
      this(null, energy, ingredient, outputs);
    }

    public MillRecipe(ItemStack targetedResult, float energy, IRecipeInput ingredient, List<IRecipeOutput> outputs) {
      this.energy = energy;
      input = new PositionedStack(getInputs(ingredient), 80 - offset.x, 12 - offset.y);
      output = new PositionedStack(outputs.get(0).getItem(), 49 - offset.x, 59 - offset.y);
      otherOutputs = new ArrayList<PositionedStack>();
      if(outputs.size() > 1) {
        otherOutputs.add(new PositionedStack(outputs.get(1).getItem(), 70 - offset.x, 59 - offset.y));
      }
      if(outputs.size() > 2) {
        otherOutputs.add(new PositionedStack(outputs.get(2).getItem(), 91 - offset.x, 59 - offset.y));
      }
      if(outputs.size() > 3) {
        otherOutputs.add(new PositionedStack(outputs.get(3).getItem(), 112 - offset.x, 59 - offset.y));
      }

      outputChance = new float[outputs.size()];
      indexOfTargetOutput = 0;
      numTargetOuput = 1;
      for (int i = 0; i < outputChance.length; i++) {
        outputChance[i] = outputs.get(i).getChance();
        if(targetedResult != null && outputs.get(i).isEquivalent(targetedResult)) {
          indexOfTargetOutput = i;
          numTargetOuput = outputs.get(i).getQuantity();
        }
      }
    }
  }

  private class RecipeComparator implements Comparator<MillRecipe> {

    @Override
    public int compare(MillRecipe o1, MillRecipe o2) {
      float c1 = o1.outputChance[o1.indexOfTargetOutput];
      float c2 = o2.outputChance[o2.indexOfTargetOutput];
      if(c1 != c2) {
        return -Float.compare(c1, c2);
      }
      int num1 = o1.numTargetOuput;
      int num2 = o2.numTargetOuput;
      return -compare(num1, num2);
    }

    public int compare(int x, int y) {
      return (x < y) ? -1 : ((x == y) ? 0 : 1);
    }
  }

}
