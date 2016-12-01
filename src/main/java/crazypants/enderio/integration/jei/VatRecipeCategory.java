package crazypants.enderio.integration.jei;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;

import com.enderio.core.client.render.RenderUtil;

import crazypants.enderio.EnderIO;
import crazypants.enderio.machine.recipe.IRecipe;
import crazypants.enderio.machine.recipe.RecipeInput;
import crazypants.enderio.machine.vat.ContainerVat;
import crazypants.enderio.machine.vat.GuiVat;
import crazypants.enderio.machine.vat.VatRecipeManager;
import crazypants.enderio.power.PowerDisplayUtil;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IGuiIngredient;
import mezz.jei.api.gui.IGuiItemStackGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.BlankRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;

import static crazypants.enderio.ModObject.blockVat;
import static crazypants.enderio.machine.vat.ContainerVat.FIRST_INVENTORY_SLOT;
import static crazypants.enderio.machine.vat.ContainerVat.FIRST_RECIPE_SLOT;
import static crazypants.enderio.machine.vat.ContainerVat.NUM_INVENTORY_SLOT;
import static crazypants.enderio.machine.vat.ContainerVat.NUM_RECIPE_SLOT;

public class VatRecipeCategory extends BlankRecipeCategory<VatRecipeCategory.VatRecipeWrapper> {

  public static final @Nonnull String UID = "Vat";

  // Offsets from full size gui, makes it much easier to get the location
  // correct
  private static final int xOff = 24;
  private static final int yOff = 9;

  // ------------ Recipes

  public static class VatRecipeWrapper extends RecipeWrapper {

    private Rectangle inTankBounds = new Rectangle(30 - xOff, 12 - yOff, 15, 47);
    private Rectangle outTankBounds = new Rectangle(132 - xOff, 12 - yOff, 15, 47);

    Map<Integer, ? extends IGuiIngredient<ItemStack>> currentIngredients;
    private final FluidStack inputFl;
    private final FluidStack outputFl;

    public VatRecipeWrapper(IRecipe rec) {
      super(rec);
      FluidStack fl = null;
      for (RecipeInput ri : rec.getInputs()) {
        if (ri.isFluid()) {
          fl = ri.getFluidInput();
          break;
        }
      }
      inputFl = fl;
      outputFl = rec.getOutputs()[0].getFluidOutput();

    }

    @Override
    public void drawInfo(@Nonnull Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY) {
      if (currentIngredients == null || inputFl == null || outputFl == null) {
        return;
      }

      String str = getTextForSlot(0);
      if (str != null) {
        minecraft.fontRendererObj.drawString(str, 54 - xOff, 31 - yOff, 0x808080);
      }
      str = getTextForSlot(1);
      if (str != null) {
        minecraft.fontRendererObj.drawString(str, 104 - xOff, 31 - yOff, 0x808080);
      }

      RenderUtil.renderGuiTank(inputFl, inputFl.amount, inputFl.amount, inTankBounds.x, inTankBounds.y, 0, inTankBounds.width, inTankBounds.height);
      RenderUtil.renderGuiTank(outputFl, outputFl.amount, outputFl.amount, outTankBounds.x, outTankBounds.y, 0, outTankBounds.width, outTankBounds.height);

      IRecipe rec = getRecipe();
      String energyString = PowerDisplayUtil.formatPower(rec.getEnergyRequired()) + " " + PowerDisplayUtil.abrevation();
      minecraft.fontRendererObj.drawString(energyString, 76, 58, 0x808080, false);

    }

    private String getTextForSlot(int forSlot) {
      IGuiIngredient<ItemStack> ging = currentIngredients.get(forSlot);
      ItemStack stack = ging.getDisplayedIngredient();

      if (stack == null) {
        return null;
      }
      float mult = VatRecipeManager.getInstance().getMultiplierForInput(inputFl.getFluid(), stack, outputFl.getFluid());
      String str = "x" + mult;
      return str;
    }

    public void setInfoData(Map<Integer, ? extends IGuiIngredient<ItemStack>> ings) {
      currentIngredients = ings;
    }

    @SuppressWarnings("null")
    @Override
    public @Nonnull List<FluidStack> getFluidInputs() {
      return Collections.singletonList(inputFl);
    }

    @Override
    public List<String> getTooltipStrings(int mouseX, int mouseY) {
      List<String> res = new ArrayList<String>(2);
      if (inTankBounds.contains(mouseX, mouseY)) {
        res.add(inputFl.getLocalizedName());
      } else if (outTankBounds.contains(mouseX, mouseY)) {
        res.add(outputFl.getLocalizedName());
      }
      return res;
    }

  }

  public static void register(IModRegistry registry, IGuiHelper guiHelper) {

    registry.addRecipeCategories(new VatRecipeCategory(guiHelper));
    registry.addRecipeHandlers(new BaseRecipeHandler<VatRecipeWrapper>(VatRecipeWrapper.class, VatRecipeCategory.UID) {

      @Override
      public boolean isRecipeValid(@Nonnull VatRecipeWrapper recipe) {
        return recipe.isValid();
      }

    });
    registry.addRecipeClickArea(GuiVat.class, 155, 42, 16, 16, VatRecipeCategory.UID);
    registry.addRecipeCategoryCraftingItem(new ItemStack(blockVat.getBlock()), VatRecipeCategory.UID);

    List<VatRecipeWrapper> result = new ArrayList<VatRecipeWrapper>();
    for (IRecipe rec : VatRecipeManager.getInstance().getRecipes()) {
      result.add(new VatRecipeWrapper(rec));
    }
    registry.addRecipes(result);

    registry.getRecipeTransferRegistry().addRecipeTransferHandler(ContainerVat.class, VatRecipeCategory.UID, FIRST_RECIPE_SLOT, NUM_RECIPE_SLOT,
        FIRST_INVENTORY_SLOT, NUM_INVENTORY_SLOT);
  }

  // ------------ Category

  @Nonnull
  private final IDrawable background;

  private VatRecipeWrapper currentRecipe;

  public VatRecipeCategory(IGuiHelper guiHelper) {
    ResourceLocation backgroundLocation = EnderIO.proxy.getGuiTexture("vat");
    background = guiHelper.createDrawable(backgroundLocation, xOff, yOff, 130, 70);
  }

  @Override
  public @Nonnull String getUid() {
    return UID;
  }

  @SuppressWarnings("null")
  @Override
  public @Nonnull String getTitle() {
    return blockVat.getBlock().getLocalizedName();
  }

  @Override
  public @Nonnull IDrawable getBackground() {
    return background;
  }

  @Override
  public void setRecipe(@Nonnull IRecipeLayout recipeLayout, @Nonnull VatRecipeCategory.VatRecipeWrapper recipeWrapper, @Nonnull IIngredients ingredients) {

    currentRecipe = recipeWrapper;

    IGuiItemStackGroup guiItemStacks = recipeLayout.getItemStacks();

    Map<Integer, ? extends IGuiIngredient<ItemStack>> ings = guiItemStacks.getGuiIngredients();
    currentRecipe.setInfoData(ings);

    guiItemStacks.init(0, true, 55 - xOff, 11 - yOff);
    guiItemStacks.init(1, true, 104 - xOff, 11 - yOff);

    ArrayList<ItemStack> inputsOne = new ArrayList<ItemStack>();
    ArrayList<ItemStack> inputsTwo = new ArrayList<ItemStack>();
    for (RecipeInput input : currentRecipe.getRecipe().getInputs()) {
      if (input.getInput() != null) {
        List<ItemStack> equivs = getInputStacks(input);
        if (input.getSlotNumber() == 0) {
          inputsOne.addAll(equivs);
        } else if (input.getSlotNumber() == 1) {
          inputsTwo.addAll(equivs);
        }
      }
    }
    guiItemStacks.set(0, inputsOne);
    guiItemStacks.set(1, inputsTwo);
  }

  private List<ItemStack> getInputStacks(RecipeInput input) {
    List<ItemStack> result = new ArrayList<ItemStack>();
    result.add(input.getInput());
    ItemStack[] eq = input.getEquivelentInputs();
    if (eq != null) {
      for (ItemStack st : eq) {
        result.add(st);
      }
    }
    return result;
  }

}
