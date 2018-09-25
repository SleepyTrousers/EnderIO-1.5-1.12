package crazypants.enderio.machines.integration.jei;

import java.awt.Rectangle;

import javax.annotation.Nonnull;

import crazypants.enderio.base.EnderIO;
import crazypants.enderio.base.integration.jei.RecipeWrapper;
import crazypants.enderio.base.integration.jei.energy.EnergyIngredient;
import crazypants.enderio.base.integration.jei.energy.EnergyIngredientRenderer;
import crazypants.enderio.base.recipe.IRecipe;
import crazypants.enderio.base.recipe.vat.VatRecipe;
import crazypants.enderio.base.recipe.vat.VatRecipe.RecipeMatch;
import crazypants.enderio.base.recipe.vat.VatRecipeManager;
import crazypants.enderio.machines.EnderIOMachines;
import crazypants.enderio.machines.config.config.PersonalConfig;
import crazypants.enderio.machines.machine.vat.ContainerVat;
import crazypants.enderio.machines.machine.vat.GuiVat;
import crazypants.enderio.util.Prep;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IGuiFluidStackGroup;
import mezz.jei.api.gui.IGuiIngredientGroup;
import mezz.jei.api.gui.IGuiItemStackGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.BlankRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;

import static crazypants.enderio.machines.init.MachineObject.block_vat;
import static crazypants.enderio.machines.machine.vat.ContainerVat.FIRST_INVENTORY_SLOT;
import static crazypants.enderio.machines.machine.vat.ContainerVat.FIRST_RECIPE_SLOT;
import static crazypants.enderio.machines.machine.vat.ContainerVat.NUM_INVENTORY_SLOT;
import static crazypants.enderio.machines.machine.vat.ContainerVat.NUM_RECIPE_SLOT;

public class VatRecipeCategory extends BlankRecipeCategory<VatRecipeCategory.VatRecipeWrapper> {

  public static final @Nonnull String UID = "Vat";

  // ------------ Recipes

  public static class VatRecipeWrapper extends RecipeWrapper {

    private IRecipeLayout currentLayout;

    @SuppressWarnings("null")
    public VatRecipeWrapper(IRecipe rec) {
      super(rec);
    }

    @Override
    public void getIngredients(@Nonnull IIngredients ingredients) {
      super.getIngredients(ingredients);
      ingredients.setInput(EnergyIngredient.class, new EnergyIngredient(recipe.getEnergyRequired()));
    }

    public void setCurrentLayout(IRecipeLayout currentLayout) {
      this.currentLayout = currentLayout;
    }

    @Override
    public void drawInfo(@Nonnull Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY) {
      // This is early everytime the recipe is drawn, so re-calc based on the active items here
      if (currentLayout != null) {
        final IGuiItemStackGroup guiItemStacks = currentLayout.getItemStacks();
        final IGuiFluidStackGroup fluidStacks = currentLayout.getFluidStacks();
        final ItemStack in0stack = guiItemStacks.getGuiIngredients().get(0).getDisplayedIngredient();
        final ItemStack in1Stack = guiItemStacks.getGuiIngredients().get(1).getDisplayedIngredient();
        final FluidStack fluidIn = fluidStacks.getGuiIngredients().get(2).getDisplayedIngredient();
        if (in0stack != null && fluidIn != null) {
          final RecipeMatch match = ((VatRecipe) recipe).matchRecipe(fluidIn, in0stack, in1Stack != null ? in1Stack : Prep.getEmpty());
          if (match != null) {
            fluidStacks.set(2, match.in);
            fluidStacks.set(3, match.out);
            minecraft.fontRenderer.drawString("x" + match.r0.getMulitplier(), 54 - xOff, 31 - yOff, 0x808080);
            minecraft.fontRenderer.drawString("x" + match.r1.getMulitplier(), 104 - xOff, 31 - yOff, 0x808080);
          }
        }
      }
    }

  }

  public static void register(IModRegistry registry, IGuiHelper guiHelper) {
    // Check JEI recipes are enabled
    if (!PersonalConfig.enableVatJEIRecipes.get()) {
      return;
    }

    registry.addRecipeCategories(new VatRecipeCategory(guiHelper));
    registry.handleRecipes(IRecipe.class, VatRecipeWrapper::new, VatRecipeCategory.UID);
    registry.addRecipeClickArea(GuiVat.class, 155, 42, 16, 16, VatRecipeCategory.UID);
    registry.addRecipeCategoryCraftingItem(new ItemStack(block_vat.getBlockNN()), VatRecipeCategory.UID);

    registry.addRecipes(VatRecipeManager.getInstance().getRecipes(), UID);

    registry.getRecipeTransferRegistry().addRecipeTransferHandler(ContainerVat.class, VatRecipeCategory.UID, FIRST_RECIPE_SLOT, NUM_RECIPE_SLOT,
        FIRST_INVENTORY_SLOT, NUM_INVENTORY_SLOT);
  }

  // ------------ Category

  // Offsets from full size gui, makes it much easier to get the location
  // correct
  private static final int xOff = 24;
  private static final int yOff = 9;
  private Rectangle inTankBounds = new Rectangle(30 - xOff, 12 - yOff, 15, 47);
  private Rectangle outTankBounds = new Rectangle(132 - xOff, 12 - yOff, 15, 47);

  private final @Nonnull IDrawable background;

  public VatRecipeCategory(IGuiHelper guiHelper) {
    ResourceLocation backgroundLocation = EnderIO.proxy.getGuiTexture("vat");
    background = guiHelper.createDrawable(backgroundLocation, xOff, yOff, 130, 70);
  }

  @Override
  public @Nonnull String getUid() {
    return UID;
  }

  @Override
  public @Nonnull String getTitle() {
    return block_vat.getBlockNN().getLocalizedName();
  }

  @Override
  public @Nonnull IDrawable getBackground() {
    return background;
  }

  @Override
  public void setRecipe(@Nonnull IRecipeLayout recipeLayout, @Nonnull VatRecipeCategory.VatRecipeWrapper recipeWrapper, @Nonnull IIngredients ingredients) {
    recipeWrapper.setCurrentLayout(recipeLayout);

    final IGuiItemStackGroup guiItemStacks = recipeLayout.getItemStacks();
    final IGuiFluidStackGroup fluidStacks = recipeLayout.getFluidStacks();
    IGuiIngredientGroup<EnergyIngredient> group = recipeLayout.getIngredientsGroup(EnergyIngredient.class);

    guiItemStacks.init(0, true, 55 - xOff, 11 - yOff);
    guiItemStacks.init(1, true, 104 - xOff, 11 - yOff);
    fluidStacks.init(2, true, inTankBounds.x, inTankBounds.y, inTankBounds.width, inTankBounds.height, 8000, false, null);
    fluidStacks.init(3, false, outTankBounds.x, outTankBounds.y, outTankBounds.width, outTankBounds.height, 8000, false, null);
    group.init(4, true, EnergyIngredientRenderer.INSTANCE, 76, 58, 50, 10, 0, 0);

    guiItemStacks.set(ingredients);
    fluidStacks.set(ingredients);
    group.set(ingredients);
  }

  @Override
  public @Nonnull String getModName() {
    return EnderIOMachines.MODID;
  }

}
