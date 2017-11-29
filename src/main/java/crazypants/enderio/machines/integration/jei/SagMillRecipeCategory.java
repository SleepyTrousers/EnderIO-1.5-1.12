package crazypants.enderio.machines.integration.jei;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import com.enderio.core.common.util.NNList;

import crazypants.enderio.EnderIO;
import crazypants.enderio.gui.TooltipHandlerGrinding;
import crazypants.enderio.integration.jei.RecipeWrapper;
import crazypants.enderio.machines.machine.sagmill.ContainerSagMill;
import crazypants.enderio.machines.machine.sagmill.GuiSagMill;
import crazypants.enderio.power.PowerDisplayUtil;
import crazypants.enderio.recipe.IRecipe;
import crazypants.enderio.recipe.RecipeOutput;
import crazypants.enderio.recipe.sagmill.GrindingBall;
import crazypants.enderio.recipe.sagmill.SagMillRecipeManager;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IDrawableAnimated;
import mezz.jei.api.gui.IDrawableStatic;
import mezz.jei.api.gui.IGuiItemStackGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.ITooltipCallback;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.BlankRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.translation.I18n;

import static crazypants.enderio.machines.init.MachineObject.block_sag_mill;
import static crazypants.enderio.machines.machine.sagmill.ContainerSagMill.FIRST_INVENTORY_SLOT;
import static crazypants.enderio.machines.machine.sagmill.ContainerSagMill.FIRST_RECIPE_SLOT;
import static crazypants.enderio.machines.machine.sagmill.ContainerSagMill.NUM_INVENTORY_SLOT;
import static crazypants.enderio.machines.machine.sagmill.ContainerSagMill.NUM_RECIPE_SLOT;

public class SagMillRecipeCategory extends BlankRecipeCategory<SagMillRecipeCategory.SagRecipe> implements ITooltipCallback<ItemStack> {

  public static final @Nonnull String UID = "SagMill";

  // ------------ Recipes

  public static class SagRecipe extends RecipeWrapper {
    public SagRecipe(IRecipe recipe) {
      super(recipe);
    }
  }

  public static void register(IModRegistry registry, IGuiHelper guiHelper) {

    registry.addRecipeCategories(new SagMillRecipeCategory(guiHelper));
    registry.handleRecipes(IRecipe.class, SagRecipe::new, SagMillRecipeCategory.UID);
    registry.addRecipeClickArea(GuiSagMill.class, 155, 42, 16, 16, SagMillRecipeCategory.UID);
    registry.addRecipeCategoryCraftingItem(new ItemStack(block_sag_mill.getBlock()), SagMillRecipeCategory.UID);

    registry.addRecipes(SagMillRecipeManager.getInstance().getRecipes(), UID);

    registry.getRecipeTransferRegistry().addRecipeTransferHandler(ContainerSagMill.class, SagMillRecipeCategory.UID, FIRST_RECIPE_SLOT, NUM_RECIPE_SLOT,
        FIRST_INVENTORY_SLOT, NUM_INVENTORY_SLOT);
  }

  // ------------ Category

  // Offsets from full size gui, makes it much easier to get the location
  // correct
  private int xOff = 45;
  private int yOff = 3;

  @Nonnull
  private final IDrawable background;

  @Nonnull
  protected final IDrawableAnimated arrow;

  private SagRecipe currentRecipe;

  private final TooltipHandlerGrinding ballsTT = new TooltipHandlerGrinding();

  public SagMillRecipeCategory(IGuiHelper guiHelper) {
    ResourceLocation backgroundLocation = EnderIO.proxy.getGuiTexture("crusher");
    background = guiHelper.createDrawable(backgroundLocation, xOff, yOff, 109, 78);

    IDrawableStatic flameDrawable = guiHelper.createDrawable(backgroundLocation, 201, 1, 16, 22);
    arrow = guiHelper.createAnimatedDrawable(flameDrawable, 200, IDrawableAnimated.StartDirection.TOP, false);
  }

  @Override
  public @Nonnull String getUid() {
    return UID;
  }

  @Override
  public @Nonnull String getTitle() {
    return block_sag_mill.getBlock().getLocalizedName();
  }

  @Override
  public @Nonnull IDrawable getBackground() {
    return background;
  }

  @Override
  public void drawExtras(@Nonnull Minecraft minecraft) {
    if (currentRecipe == null) {
      return;
    }
    String energyString = PowerDisplayUtil.formatPower(currentRecipe.getEnergyRequired()) + " " + PowerDisplayUtil.abrevation();
    minecraft.fontRenderer.drawString(energyString, 135 - xOff, 60 - yOff, 0x808080, false);
    GlStateManager.color(1, 1, 1, 1);
    
    arrow.draw(minecraft, 80 - xOff, 32 - yOff);
  }

  @Override
  public void setRecipe(@Nonnull IRecipeLayout recipeLayout, @Nonnull SagMillRecipeCategory.SagRecipe recipeWrapper, @Nonnull IIngredients ingredients) {

    currentRecipe = recipeWrapper;

    IGuiItemStackGroup guiItemStacks = recipeLayout.getItemStacks();
    guiItemStacks.addTooltipCallback(this);

    guiItemStacks.init(0, true, 79 - xOff, 11 - yOff);
    guiItemStacks.init(1, false, 48 - xOff, 58 - yOff);
    guiItemStacks.init(2, false, 69 - xOff, 58 - yOff);
    guiItemStacks.init(3, false, 90 - xOff, 58 - yOff);
    guiItemStacks.init(4, false, 111 - xOff, 58 - yOff);
    guiItemStacks.init(5, false, 121 - xOff, 22 - yOff);

    List<ItemStack> inputs = ingredients.getInputs(ItemStack.class).get(0);
    if (inputs != null) {
      guiItemStacks.set(0, inputs);
    }
    List<List<ItemStack>> outputs = ingredients.getOutputs(ItemStack.class);
    for (int i = 0; i <= 4 && i < outputs.size(); i++) {
      for (ItemStack output : outputs.get(i)) {
        if (!output.isEmpty()) {
          guiItemStacks.set(i, output);
        }
      }
    }
    guiItemStacks.set(5, getBalls());
  }

  @Override
  public void onTooltip(int slotIndex, boolean input, @Nonnull ItemStack ingredient, @Nonnull List<String> tooltip) {
    if (slotIndex == 0) {
      return;
    }
    if (slotIndex == 5) {
      if (ballsTT.shouldHandleItem(ingredient)) {
        ballsTT.addDetailedEntries(ingredient, Minecraft.getMinecraft().player, tooltip, true);
      }
      return;
    }
    if (slotIndex - 1 >= currentRecipe.getRecipe().getOutputs().length) {
      return;
    }
    RecipeOutput output = currentRecipe.getRecipe().getOutputs()[slotIndex - 1];
    float chance = output.getChance();
    if (chance > 0 && chance < 1) {
      int chanceInt = (int) (chance * 100);
      Object[] objects = { chanceInt };
      tooltip.add(TextFormatting.GRAY + MessageFormat.format(I18n.translateToLocal("enderio.nei.sagmill.outputchance"), objects));
    }
  }

  private @Nonnull List<ItemStack> getBalls() {
    NNList<GrindingBall> daBalls = SagMillRecipeManager.getInstance().getBalls();
    List<ItemStack> res = new ArrayList<ItemStack>();
    res.add(null);
    for (GrindingBall ball : daBalls) {
      res.add(ball.getInput());
    }
    return res;
  }

}
