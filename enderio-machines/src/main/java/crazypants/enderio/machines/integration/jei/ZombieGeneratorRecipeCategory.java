package crazypants.enderio.machines.integration.jei;

import java.awt.Color;
import java.util.Collections;
import java.util.List;

import javax.annotation.Nonnull;

import com.enderio.core.client.render.ColorUtil;

import crazypants.enderio.base.EnderIO;
import crazypants.enderio.base.capacitor.DefaultCapacitorData;
import crazypants.enderio.base.fluid.Fluids;
import crazypants.enderio.base.integration.jei.energy.EnergyIngredient;
import crazypants.enderio.base.integration.jei.energy.EnergyIngredientRenderer;
import crazypants.enderio.base.lang.LangFluid;
import crazypants.enderio.machines.capacitor.CapacitorKey;
import crazypants.enderio.machines.config.config.ZombieGenConfig;
import crazypants.enderio.machines.init.MachineObject;
import crazypants.enderio.machines.lang.Lang;
import crazypants.enderio.machines.machine.generator.zombie.GuiZombieGenerator;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IGuiFluidStackGroup;
import mezz.jei.api.gui.IGuiIngredientGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.ITooltipCallback;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.BlankRecipeCategory;
import mezz.jei.api.recipe.BlankRecipeWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;

public class ZombieGeneratorRecipeCategory extends BlankRecipeCategory<ZombieGeneratorRecipeCategory.ZombieGeneratorRecipeWrapper> {

  public static final @Nonnull String UID = "ZombieGenerator";
  public static final int tankCapacity = 2000;

  // ------------ Recipes

  public static class ZombieGeneratorRecipeWrapper extends BlankRecipeWrapper {
    @Override
    public void getIngredients(@Nonnull IIngredients ingredients) {
      ingredients.setInput(FluidStack.class,
          new FluidStack(Fluids.NUTRIENT_DISTILLATION.getFluid(), Math.round(ZombieGenConfig.minimumTankLevel.get() * tankCapacity)));
      ingredients.setOutput(EnergyIngredient.class,
          new EnergyIngredient(Math.round(CapacitorKey.ZOMBIE_POWER_GEN.getFloat(DefaultCapacitorData.BASIC_CAPACITOR)), true));
    }

    @Override
    public void drawInfo(@Nonnull Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY) {
      FontRenderer fr = minecraft.fontRenderer;
      String txt = Lang.GUI_ZOMBGEN_OUTPUT.get("");
      fr.drawStringWithShadow(txt, 10, 3, ColorUtil.getRGB(Color.WHITE));

      txt = LangFluid.tMB(ZombieGenConfig.ticksPerBucketOfFuel.get() / 1000);
      int sw = fr.getStringWidth(txt);
      fr.drawStringWithShadow(txt, recipeWidth / 2 - sw / 2, 14 + 47 + fr.FONT_HEIGHT / 2, ColorUtil.getRGB(Color.WHITE));

      GlStateManager.color(1, 1, 1, 1);
    }
  }

  // -------------------------------------

  public static void register(IModRegistry registry, IGuiHelper guiHelper) {
    registry.addRecipeCategories(new ZombieGeneratorRecipeCategory(guiHelper));
    registry.addRecipeCategoryCraftingItem(new ItemStack(MachineObject.block_zombie_generator.getBlockNN(), 1, 0), ZombieGeneratorRecipeCategory.UID);
    registry.addRecipeClickArea(GuiZombieGenerator.class, 155, 42, 16, 16, ZombieGeneratorRecipeCategory.UID);
    registry.addRecipes(Collections.singletonList(new ZombieGeneratorRecipeWrapper()), UID);
  }

  // ------------ Category

  // Offsets from full size gui, makes it much easier to get the location correct
  static int xOff = 25 + 3;
  static int yOff = 7;
  static int xSize = 136 - 3;

  @Nonnull
  private final IDrawable background;

  public ZombieGeneratorRecipeCategory(IGuiHelper guiHelper) {
    ResourceLocation backgroundLocation = EnderIO.proxy.getGuiTexture("zombie_generator");
    background = guiHelper.createDrawable(backgroundLocation, xOff, yOff, xSize, 75);
  }

  @Override
  public @Nonnull String getUid() {
    return UID;
  }

  @SuppressWarnings("null")
  @Override
  public @Nonnull String getTitle() {
    return MachineObject.block_zombie_generator.getBlock().getLocalizedName();
  }

  @Override
  public @Nonnull IDrawable getBackground() {
    return background;
  }

  @Override
  public void setRecipe(@Nonnull IRecipeLayout recipeLayout, @Nonnull ZombieGeneratorRecipeWrapper recipeWrapper, @Nonnull IIngredients ingredients) {
    IGuiFluidStackGroup guiFluidStacks = recipeLayout.getFluidStacks();
    IGuiIngredientGroup<EnergyIngredient> group = recipeLayout.getIngredientsGroup(EnergyIngredient.class);

    guiFluidStacks.init(0, true, 80 - xOff, 21 - yOff, 15, 47, tankCapacity, true, null);
    guiFluidStacks.addTooltipCallback(new ITooltipCallback<FluidStack>() {

      @Override
      public void onTooltip(int slotIndex, boolean input, FluidStack ingredient, List<String> tooltip) {
        if (slotIndex != 0)
          return;
        tooltip.add(Lang.GUI_ZOMBGEN_MINREQ.get(LangFluid.MB(Math.round(ZombieGenConfig.minimumTankLevel.get() * tankCapacity))));
      }
    });

    group.init(1, false, EnergyIngredientRenderer.INSTANCE, 75, 3, 40, 10, 0, 0);

    guiFluidStacks.set(ingredients);
    group.set(ingredients);
  }
}
