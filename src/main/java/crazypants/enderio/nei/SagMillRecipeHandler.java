package crazypants.enderio.nei;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;

import org.lwjgl.opengl.GL11;

import codechicken.nei.PositionedStack;
import codechicken.nei.recipe.GuiRecipe;
import codechicken.nei.recipe.TemplateRecipeHandler;
import crazypants.enderio.machine.crusher.CrusherRecipeManager;
import crazypants.enderio.machine.crusher.GrindingBall;
import crazypants.enderio.machine.crusher.GuiCrusher;
import crazypants.enderio.machine.power.PowerDisplayUtil;
import crazypants.enderio.machine.recipe.Recipe;
import crazypants.enderio.machine.recipe.RecipeInput;
import crazypants.enderio.machine.recipe.RecipeOutput;
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
    System.out.println("SagMillRecipeHandler.loadTransferRects: ");
    //Set this up later to show all alloy recipes
    transferRects.add(new TemplateRecipeHandler.RecipeTransferRect(new Rectangle(149, 32, 16, 16), "EnderIOSagMill", new Object[0]));
  }

  @Override
  public void loadCraftingRecipes(final ItemStack result) {

    List<Recipe> recipes = CrusherRecipeManager.getInstance().getRecipes();
    List<MillRecipe> toAdd = new ArrayList<MillRecipe>();
    for (Recipe recipe : recipes) {
      if(recipe.hasOuput(result)) {
        MillRecipe res = new MillRecipe(result, recipe.getEnergyRequired(), recipe.getInputs()[0], recipe.getOutputs());
        toAdd.add(res);
      }
    }

    Collections.sort(toAdd, comparator);

    arecipes.addAll(toAdd);
  }

  @Override
  public void loadCraftingRecipes(String outputId, Object... results) {
    if(outputId.equals("EnderIOSagMill") && getClass() == SagMillRecipeHandler.class) {
      List<Recipe> recipes = CrusherRecipeManager.getInstance().getRecipes();
      for (Recipe recipe : recipes) {
        MillRecipe res = new MillRecipe(recipe.getEnergyRequired(), recipe.getInputs()[0], recipe.getOutputs());
        arecipes.add(res);
      }
    } else {
      super.loadCraftingRecipes(outputId, results);
    }
  }

  @Override
  public void loadUsageRecipes(ItemStack ingredient) {

    List<Recipe> recipes = CrusherRecipeManager.getInstance().getRecipes();
    for (Recipe recipe : recipes) {
      if(recipe.isValidInput(0, ingredient)) {
        MillRecipe res = new MillRecipe(recipe.getEnergyRequired(), recipe.getInputs()[0], recipe.getOutputs());
        arecipes.add(res);
      }
    }    
  }

  @Override
  public void drawExtras(int recipeIndex) {
    drawProgressBar(98, 33, 176, 0, 22, 13, 48, 3);
    drawProgressBar(50, 33, 176, 0, 22, 13, 48, 3);

    MillRecipe recipe = (MillRecipe) arecipes.get(recipeIndex);
    String energyString = PowerDisplayUtil.formatPower(recipe.getEnergy()) + " " + PowerDisplayUtil.abrevation();
    Minecraft.getMinecraft().fontRenderer.drawString(energyString, 90, 22, 0xFFFFFFFF);
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

  public void renderIcon(IIcon icon, double x, double y, double width, double height, double zLevel) {

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

  public List<ItemStack> getInputs(RecipeInput input) {
    List<ItemStack> result = new ArrayList<ItemStack>();
    result.add(input.getInput());
    ItemStack[] eq = input.getEquivelentInputs();
    if(eq != null) {
      for (ItemStack st : eq) {
        result.add(st);
      }
    }
    return result;
  }

  private static Point offset = new Point(5, 11);

  public class MillRecipe extends TemplateRecipeHandler.CachedRecipe {

    protected int numTargetOuput;
    protected int indexOfTargetOutput;
    private List<PositionedStack> input;
    private PositionedStack output;
    private ArrayList<PositionedStack> otherOutputs;
    private float[] outputChance;
    private int energy;
    
    public int getEnergy() {
      return energy;
    }

    public float getChanceForOutput(ItemStack stack) {
      if(stack == null) {
        return -1;
      }
      if(output.item.equals(stack)) {
        return outputChance[0];
      }
      for (int i = 0; i < otherOutputs.size(); i++) {
        PositionedStack oo = otherOutputs.get(i);
        if(oo != null && oo.item.equals(stack) && i + 1 < outputChance.length) {
          return outputChance[i + 1];
        }
      }
      return -1;
    }

    @Override
    public List<PositionedStack> getIngredients() {
      return getCycledIngredients(cycleticks / 20, input);
    }

    @Override
    public PositionedStack getResult() {
      return output;
    }

    @Override
    public List<PositionedStack> getOtherStacks() {
      return otherOutputs;
    }

    public MillRecipe(int energy, RecipeInput ingredient, RecipeOutput[] outputs) {
      this(null, energy, ingredient, outputs);
    }

    public MillRecipe(ItemStack targetedResult, int energy, RecipeInput ingredient, RecipeOutput[] outputs) {
      this.energy = energy;

      input = new ArrayList<PositionedStack>(2);
      input.add(new PositionedStack(getInputs(ingredient), 80 - offset.x, 12 - offset.y));

      input.add(new PositionedStack(getBalls(), 122 - offset.x, 23 - offset.y));

      output = new PositionedStack(outputs[0].getOutput(), 49 - offset.x, 59 - offset.y);
      otherOutputs = new ArrayList<PositionedStack>();
      if(outputs.length > 1) {
        otherOutputs.add(new PositionedStack(outputs[1].getOutput(), 70 - offset.x, 59 - offset.y));
      }
      if(outputs.length > 2) {
        otherOutputs.add(new PositionedStack(outputs[2].getOutput(), 91 - offset.x, 59 - offset.y));
      }
      if(outputs.length > 3) {
        otherOutputs.add(new PositionedStack(outputs[3].getOutput(), 112 - offset.x, 59 - offset.y));
      }

      outputChance = new float[outputs.length];
      indexOfTargetOutput = 0;
      numTargetOuput = 1;
      for (int i = 0; i < outputChance.length; i++) {
        outputChance[i] = outputs[i].getChance();
        if(targetedResult != null && outputs[i].getOutput().isItemEqual(targetedResult)) {
          indexOfTargetOutput = i;
          numTargetOuput = outputs[i].getOutput().stackSize;
        }
      }
    }

    private List<ItemStack> getBalls() {
      List<GrindingBall> daBalls = CrusherRecipeManager.getInstance().getBalls();
      List<ItemStack> res = new ArrayList<ItemStack>();
      for (GrindingBall ball : daBalls) {
        res.add(ball.getInput());
      }
      return res;
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
