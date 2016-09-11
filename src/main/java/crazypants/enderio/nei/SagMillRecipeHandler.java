package crazypants.enderio.nei;

import java.awt.Rectangle;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;

import org.lwjgl.opengl.GL11;

import com.enderio.core.client.render.EnderWidget;

import codechicken.lib.gui.GuiDraw;
import codechicken.nei.PositionedStack;
import codechicken.nei.recipe.GuiRecipe;
import codechicken.nei.recipe.TemplateRecipeHandler;
import crazypants.enderio.gui.IconEIO;
import crazypants.enderio.machine.crusher.CrusherRecipeManager;
import crazypants.enderio.machine.crusher.GrindingBall;
import crazypants.enderio.machine.crusher.GuiCrusher;
import crazypants.enderio.machine.power.PowerDisplayUtil;
import crazypants.enderio.machine.recipe.Recipe;
import crazypants.enderio.machine.recipe.RecipeInput;
import crazypants.enderio.machine.recipe.RecipeOutput;

public class SagMillRecipeHandler extends TemplateRecipeHandler {

  private RecipeComparator comparator = new RecipeComparator();

  @Override
  public String getRecipeName() {
    return StatCollector.translateToLocal("enderio.nei.sagmill");
  }

  @Override
  public String getGuiTexture() {
    return "enderio:textures/gui/nei/crusher.png";
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
      MillRecipe res = new MillRecipe(recipe.getEnergyRequired(), recipe.getInputs()[0], recipe.getOutputs());
      if(res.contains(res.input, ingredient)) {
        res.setIngredientPermutation(res.input, ingredient);
        arecipes.add(res);
      }
    }
  }

  @Override
  public void drawBackground(int recipeIndex) {
    GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
    GuiDraw.changeTexture(getGuiTexture());
    GuiDraw.drawTexturedModalRect(0, 0, 0, 0, 166, 65);
  }

  @Override
  public void drawExtras(int recipeIndex) {
    drawProgressBar(73, 20, 166, 0, 17, 24, 40, 1);
    drawProgressBar(136, 12, 166, 24, 4, 16, 160, 11);

    MillRecipe recipe = (MillRecipe) arecipes.get(recipeIndex);
    String energyString = PowerDisplayUtil.formatPower(recipe.getEnergy()) + " " + PowerDisplayUtil.abrevation();
    GuiDraw.drawString(energyString, 96, 33, 0x808080, false);

    int x = 149, y = 32;
    EnderWidget.map.render(EnderWidget.BUTTON, x, y, 16, 16, 0, true);
    IconEIO.map.render(IconEIO.RECIPE, x + 1, y + 1, 14, 14, 0, true);
  }

  @Override
  public List<String> handleItemTooltip(GuiRecipe gui, ItemStack stack, List<String> currenttip, int recipeIndex) {
    MillRecipe recipe = (MillRecipe) arecipes.get(recipeIndex);
    float chance = recipe.getChanceForOutput(stack);
    if(chance > 0 && chance < 1) {
      int chanceInt = (int) (chance * 100);
      currenttip.add(EnumChatFormatting.GRAY + MessageFormat.format(StatCollector.translateToLocal("enderio.nei.sagmill.outputchance"), chanceInt));
    }
    return currenttip;
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
      input.add(new PositionedStack(getInputs(ingredient), 74, 2));

      input.add(new PositionedStack(getBalls(), 116, 12));

      output = new PositionedStack(outputs[0].getOutput(), 43, 46);
      otherOutputs = new ArrayList<PositionedStack>();
      if(outputs.length > 1) {
        otherOutputs.add(new PositionedStack(outputs[1].getOutput(), 64, 46));
      }
      if(outputs.length > 2) {
        otherOutputs.add(new PositionedStack(outputs[2].getOutput(), 85, 46));
      }
      if(outputs.length > 3) {
        otherOutputs.add(new PositionedStack(outputs[3].getOutput(), 106, 46));
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
