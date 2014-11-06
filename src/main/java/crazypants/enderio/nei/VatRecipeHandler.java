package crazypants.enderio.nei;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidStack;

import org.lwjgl.opengl.GL11;

import codechicken.lib.gui.GuiDraw;
import codechicken.nei.PositionedStack;
import codechicken.nei.recipe.GuiRecipe;
import codechicken.nei.recipe.RecipeInfo;
import codechicken.nei.recipe.TemplateRecipeHandler;
import crazypants.enderio.machine.power.PowerDisplayUtil;
import crazypants.enderio.machine.recipe.IRecipe;
import crazypants.enderio.machine.recipe.RecipeInput;
import crazypants.enderio.machine.still.GuiVat;
import crazypants.enderio.machine.still.VatRecipeManager;
import crazypants.render.RenderUtil;

public class VatRecipeHandler extends TemplateRecipeHandler {

  private Rectangle inTankBounds = new Rectangle(25, 1, 15, 47);
  private Rectangle outTankBounds = new Rectangle(127, 1, 15, 47);
  private Rectangle inTankBoundsLower = new Rectangle(25, 70, 15, 47);
  private Rectangle outTankBoundsLower = new Rectangle(127, 70, 15, 47);

  public VatRecipeHandler() {
  }

  @Override
  public String getRecipeName() {
    return "Vat";
  }

  @Override
  public String getGuiTexture() {
    return "enderio:textures/gui/vat.png";
  }

  public PositionedStack getResult() {
    return null;
  }

  @Override
  public Class<? extends GuiContainer> getGuiClass() {
    return GuiVat.class;
  }

  @Override
  public String getOverlayIdentifier() {
    return "EnderIOVat";
  }

  @Override
  public void loadTransferRects() {
    transferRects.add(new TemplateRecipeHandler.RecipeTransferRect(new Rectangle(149, 32, 16, 16), "EnderIOVat", new Object[0]));
  }

  @Override
  public void loadCraftingRecipes(final ItemStack result) {

    if(result == null) {
      return;
    }
    if(FluidContainerRegistry.isFilledContainer(result)) {
      FluidStack fluid = FluidContainerRegistry.getFluidForFilledItem(result);
      if(fluid != null) {
        List<IRecipe> recipes = VatRecipeManager.getInstance().getRecipes();
        for (IRecipe recipe : recipes) {
          FluidStack output = recipe.getOutputs()[0].getFluidOutput();
          if(output.isFluidEqual(fluid)) {
            InnerVatRecipe res = new InnerVatRecipe(recipe.getEnergyRequired(), recipe.getInputs(), output);
            arecipes.add(res);
          }
        }
      }
    }
  }

  @Override
  public void loadCraftingRecipes(String outputId, Object... results) {
    if(outputId.equals("EnderIOVat") && getClass() == VatRecipeHandler.class) {
      List<IRecipe> recipes = VatRecipeManager.getInstance().getRecipes();
      for (IRecipe recipe : recipes) {
        FluidStack output = recipe.getOutputs()[0].getFluidOutput();
        InnerVatRecipe res = new InnerVatRecipe(recipe.getEnergyRequired(), recipe.getInputs(), output);
        arecipes.add(res);
      }

    } else {
      super.loadCraftingRecipes(outputId, results);
    }
  }

  @Override
  public void loadUsageRecipes(ItemStack ingredient) {
    List<IRecipe> recipes = VatRecipeManager.getInstance().getRecipes();
    for (IRecipe recipe : recipes) {
      boolean addRecipe = false;
      if(recipe.isValidInput(0, ingredient) || recipe.isValidInput(1, ingredient)) {
        addRecipe = true;
      } else if(FluidContainerRegistry.isFilledContainer(ingredient)) {
        FluidStack fluid = FluidContainerRegistry.getFluidForFilledItem(ingredient);
        if(fluid != null) {
          if(recipe.isValidInput(fluid)) {
            addRecipe = true;
          }
        }
      }
      if(addRecipe) {
        FluidStack output = recipe.getOutputs()[0].getFluidOutput();
        InnerVatRecipe res = new InnerVatRecipe(recipe.getEnergyRequired(), recipe.getInputs(), output);
        res.setIngredientPermutation(res.inputs, ingredient);
        arecipes.add(res);
      }
    }

  }

  @Override
  public void drawBackground(int recipeIndex) {
    GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
    GuiDraw.changeTexture(getGuiTexture());
    GuiDraw.drawTexturedModalRect(22, 0, 27, 11, 123, 52);
  }

  @Override
  public void drawExtras(int recipeIndex) {
    InnerVatRecipe rec = (InnerVatRecipe) arecipes.get(recipeIndex);
    if(rec.inFluid != null && rec.inFluid.getFluid() != null) {
      RenderUtil.renderGuiTank(rec.inFluid, rec.inFluid.amount, rec.inFluid.amount, inTankBounds.x, inTankBounds.y, 0, inTankBounds.width, inTankBounds.height);
    }

    if(rec.result != null && rec.result.getFluid() != null) {
      RenderUtil
          .renderGuiTank(rec.result, rec.result.amount, rec.result.amount, outTankBounds.x, outTankBounds.y, 0, outTankBounds.width, outTankBounds.height);
    }

    String energyString = PowerDisplayUtil.formatPower(rec.energy) + " " + PowerDisplayUtil.abrevation();
    GuiDraw.drawStringC(energyString, 86, 54, 0x808080, false);

    Fluid outputFluid = rec.result.getFluid();
    List<PositionedStack> stacks = rec.getIngredients();
    for (PositionedStack ps : stacks) {
      float mult = VatRecipeManager.getInstance().getMultiplierForInput(ps.item, outputFluid);
      String str = "x" + mult;
      GuiDraw.drawStringC(str, ps.relx + 8, ps.rely + 19, 0x808080, false);
    }
  }

  @Override
  public List<String> handleItemTooltip(GuiRecipe gui, ItemStack stack, List<String> currenttip, int recipeIndex) {
    return currenttip;
  }

  @Override
  public List<String> handleTooltip(GuiRecipe gui, List<String> arg1, int recipeIndex) {

    Point pos = GuiDraw.getMousePosition();

    int[] offset = RecipeInfo.getGuiOffset(gui);
    Point relMouse = new Point(pos.x - ((gui.width - 176) / 2) - offset[0], pos.y - ((gui.height - 166) / 2) - offset[1]);

    if(mouseInBounds(relMouse)) {
      if(recipeIndex % 2 == 0 && inTankBounds.union(outTankBounds).contains(relMouse)) {
        return getVatFluid(recipeIndex, arg1, inTankBounds.contains(relMouse));
      }
      else if(recipeIndex % 2 == 1 && inTankBoundsLower.union(outTankBoundsLower).contains(relMouse)) {
        return getVatFluid(recipeIndex, arg1, inTankBoundsLower.contains(relMouse));
      }
    }
    return super.handleTooltip(gui, arg1, recipeIndex);
  }

  private boolean mouseInBounds(Point mouse) {
    return inTankBounds.contains(mouse) || outTankBounds.contains(mouse) || inTankBoundsLower.contains(mouse) || outTankBoundsLower.contains(mouse);
  }

  private List<String> getVatFluid(int index, List<String> list, boolean in) {
    InnerVatRecipe rec = (InnerVatRecipe) arecipes.get(index);
    if(in) {
      if(rec.inFluid != null && rec.inFluid.getFluid() != null) {
        list.add(rec.inFluid.getFluid().getLocalizedName(rec.inFluid));
      }
    } else {
      if(rec.result != null && rec.result.getFluid() != null) {
        list.add(rec.result.getFluid().getLocalizedName(rec.result));
      }
    }
    return list;
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

  public class InnerVatRecipe extends TemplateRecipeHandler.CachedRecipe {

    private ArrayList<PositionedStack> inputs;
    private int energy;
    private FluidStack result;
    private FluidStack inFluid;

    public int getEnergy() {
      return energy;
    }

    @Override
    public List<PositionedStack> getIngredients() {
      return getCycledIngredients(cycleticks / 30, inputs);
    }

    @Override
    public PositionedStack getResult() {
      return null;
    }

    public InnerVatRecipe(int energy, RecipeInput[] ingredients, FluidStack result) {
      ArrayList<ItemStack> inputsOne = new ArrayList<ItemStack>();
      ArrayList<ItemStack> inputsTwo = new ArrayList<ItemStack>();
      for (RecipeInput input : ingredients) {
        if(input.getInput() != null) {
          List<ItemStack> equivs = getInputs(input);
          if(input.getSlotNumber() == 0) {
            inputsOne.addAll(equivs);
          } else if(input.getSlotNumber() == 1) {
            inputsTwo.addAll(equivs);
          }
        } else if(input.getFluidInput() != null) {
          inFluid = input.getFluidInput();
        }
      }

      inputs = new ArrayList<PositionedStack>();
      
      if (inputsOne != null) {
        inputs.add(new PositionedStack(inputsOne, 51, 1));
      }
      if (inputsTwo != null) {
        inputs.add(new PositionedStack(inputsTwo, 100, 1));
      }

      this.energy = energy;
      this.result = result;
    }
  }
}
