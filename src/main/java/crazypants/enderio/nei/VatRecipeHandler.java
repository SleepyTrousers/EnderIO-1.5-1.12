package crazypants.enderio.nei;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidStack;

import org.lwjgl.opengl.GL11;

import codechicken.lib.gui.GuiDraw;
import codechicken.nei.PositionedStack;
import codechicken.nei.recipe.GuiRecipe;
import codechicken.nei.recipe.RecipeInfo;
import codechicken.nei.recipe.TemplateRecipeHandler;
import crazypants.enderio.crafting.IEnderIoRecipe;
import crazypants.enderio.crafting.IRecipeInput;
import crazypants.enderio.crafting.RecipeReigistry;
import crazypants.enderio.machine.power.PowerDisplayUtil;
import crazypants.enderio.machine.still.GuiVat;
import crazypants.render.ColorUtil;
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
        List<IEnderIoRecipe> recipes = RecipeReigistry.instance.getRecipesForOutput(IEnderIoRecipe.VAT_ID, fluid);
        for (IEnderIoRecipe recipe : recipes) {
          InnerVatRecipe res = new InnerVatRecipe(recipe.getRequiredEnergy(), recipe.getInputs(), recipe.getOutputs().get(0).getFluid());
          arecipes.add(res);
        }

      }
    }

    List<IEnderIoRecipe> recipes = RecipeReigistry.instance.getRecipesForOutput(IEnderIoRecipe.VAT_ID, result);
    for (IEnderIoRecipe recipe : recipes) {
      InnerVatRecipe res = new InnerVatRecipe(recipe.getRequiredEnergy(), recipe.getInputs(), recipe.getOutputs().get(0).getFluid());
      arecipes.add(res);
    }
  }

  @Override
  public void loadCraftingRecipes(String outputId, Object... results) {
    if(outputId.equals("EnderIOVat") && getClass() == VatRecipeHandler.class) {
      List<IEnderIoRecipe> recipes = RecipeReigistry.instance.getRecipesForCrafter(IEnderIoRecipe.VAT_ID);
      for (IEnderIoRecipe recipe : recipes) {
        InnerVatRecipe res = new InnerVatRecipe(recipe.getRequiredEnergy(), recipe.getInputs(), recipe.getOutputs().get(0).getFluid());
        arecipes.add(res);

      }
    } else {
      super.loadCraftingRecipes(outputId, results);
    }
  }

  @Override
  public void loadUsageRecipes(ItemStack ingredient) {
    List<IEnderIoRecipe> recipes = RecipeReigistry.instance.getRecipesForCrafter(IEnderIoRecipe.VAT_ID);

    for (IEnderIoRecipe recipe : recipes) {
      if(recipe.isInput(ingredient)) {
        InnerVatRecipe res = new InnerVatRecipe(recipe.getRequiredEnergy(), recipe.getInputs(), recipe.getOutputs().get(0).getFluid());
        res.setIngredientPermutation(res.inputs, ingredient);
        arecipes.add(res);
      }
    }
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

    //    drawProgressBar(98, 33, 176, 0, 22, 13, 48, 3);
    //    drawProgressBar(50, 33, 176, 0, 22, 13, 48, 3);

    String energyString = PowerDisplayUtil.formatPower(rec.energy) + " " + PowerDisplayUtil.abrevation();
    int width = Minecraft.getMinecraft().fontRenderer.getStringWidth(energyString);

    GL11.glEnable(GL11.GL_BLEND);
    GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
    int col = ColorUtil.getRGBA(0.75f, 1.0f, 1.0f, 1.0f);
    Minecraft.getMinecraft().fontRenderer.drawString(energyString, 86 - width / 2, 55, col, true);
    GL11.glDisable(GL11.GL_BLEND);
    GL11.glColor4f(1, 1, 1, 1);

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
        list.add(rec.inFluid.getFluid().getLocalizedName());
      }
    } else {
      if(rec.result != null && rec.result.getFluid() != null) {
        list.add(rec.result.getFluid().getLocalizedName());
      }
    }
    return list;
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

  public List<ItemStack> getInputs(IRecipeInput input) {
    List<ItemStack> result = new ArrayList<ItemStack>();
    result.add(input.getItem());
    result.addAll(input.getEquivelentInputs());
    return result;
  }

  public class InnerVatRecipe extends TemplateRecipeHandler.CachedRecipe {

    private ArrayList<PositionedStack> inputs;
    private double energy;
    private FluidStack result;
    private FluidStack inFluid;

    public double getEnergy() {
      return energy;
    }

    @Override
    public List<PositionedStack> getIngredients() {
      return getCycledIngredients(cycleticks / 20, inputs);
    }

    @Override
    public PositionedStack getResult() {
      return null;
    }

    public InnerVatRecipe(float energy, List<IRecipeInput> ingredients, FluidStack result) {
      ArrayList<ItemStack> inputsOne = new ArrayList<ItemStack>();
      ArrayList<ItemStack> inputsTwo = new ArrayList<ItemStack>();
      for (IRecipeInput input : ingredients) {
        if(input.getItem() != null) {
          List<ItemStack> equivs = getInputs(input);
          if(input.getSlot() == 0) {
            inputsOne.addAll(equivs);
          } else if(input.getSlot() == 1) {
            inputsTwo.addAll(equivs);
          }
        } else if(input.getFluid() != null) {
          inFluid = input.getFluid();
        }
      }

      inputs = new ArrayList<PositionedStack>();
      inputs.add(new PositionedStack(inputsOne, 51, 1));
      inputs.add(new PositionedStack(inputsTwo, 100, 1));

      this.energy = energy;
      this.result = result;
    }
  }

}
