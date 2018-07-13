package crazypants.enderio.machines.integration.jei;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;

import javax.annotation.Nonnull;

import com.enderio.core.client.render.ColorUtil;
import com.enderio.core.common.util.NNList;
import com.enderio.core.common.util.NNList.Callback;
import com.enderio.core.common.util.NNMap;

import crazypants.enderio.base.EnderIO;
import crazypants.enderio.base.Log;
import crazypants.enderio.base.capacitor.DefaultCapacitorData;
import crazypants.enderio.base.capacitor.ICapacitorKey;
import crazypants.enderio.base.gui.IconEIO;
import crazypants.enderio.base.integration.jei.energy.EnergyIngredient;
import crazypants.enderio.base.integration.jei.energy.EnergyIngredientRenderer;
import crazypants.enderio.machines.EnderIOMachines;
import crazypants.enderio.machines.capacitor.CapacitorKey;
import crazypants.enderio.machines.init.MachineObject;
import crazypants.enderio.machines.lang.Lang;
import crazypants.enderio.machines.machine.generator.stirling.ContainerStirlingGenerator;
import crazypants.enderio.machines.machine.generator.stirling.FuelCache;
import crazypants.enderio.machines.machine.generator.stirling.GuiStirlingGenerator;
import crazypants.enderio.machines.machine.generator.stirling.TileStirlingGenerator;
import crazypants.enderio.util.Prep;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IDrawableAnimated;
import mezz.jei.api.gui.IDrawableStatic;
import mezz.jei.api.gui.IGuiIngredientGroup;
import mezz.jei.api.gui.IGuiItemStackGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.BlankRecipeCategory;
import mezz.jei.api.recipe.BlankRecipeWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class StirlingRecipeCategory extends BlankRecipeCategory<StirlingRecipeCategory.StirlingRecipeWrapper> {

  public static final @Nonnull String UID = "StirlingGenerator";

  // ------------ Recipes

  public static class StirlingRecipeWrapper extends BlankRecipeWrapper {

    private final @Nonnull NNList<ItemStack> solidFuel;
    private IDrawable stirlingFront;

    private StirlingRecipeWrapper(@Nonnull NNList<ItemStack> solidFuel, @Nonnull IGuiHelper guiHelper) {
      this.solidFuel = solidFuel;
      if (!simpleFuel(solidFuel.get(0))) {
        ResourceLocation stirlingFrontLocation = new ResourceLocation(EnderIO.DOMAIN, "textures/blocks/block_stirling_gen_simple_front_off.png");
        stirlingFront = guiHelper.createDrawable(stirlingFrontLocation, 0, 0, 16, 16, 16, 16);
      }
    }

    /**
     * Checks if the fuel works with Simple Stirling Generator
     */
    private boolean simpleFuel(@Nonnull ItemStack fuel) {
      return Prep.isInvalid(fuel.getItem().getContainerItem(fuel));
    }

    @Override
    public void getIngredients(@Nonnull IIngredients ingredients) {
      List<List<ItemStack>> list = new NNList<>();
      list.add(solidFuel);
      ingredients.setInputLists(ItemStack.class, list);
      
      ICapacitorKey minKey = simpleFuel(solidFuel.get(0)) ? CapacitorKey.SIMPLE_STIRLING_POWER_GEN : CapacitorKey.STIRLING_POWER_GEN;

      double minEnergyProducedPerTick = minKey.getFloat(DefaultCapacitorData.BASIC_CAPACITOR);
      double maxEnergyProducedPerTick = CapacitorKey.STIRLING_POWER_GEN.getFloat(DefaultCapacitorData.ENDER_CAPACITOR);

      double minEnergyProduced = minEnergyProducedPerTick * TileStirlingGenerator.getBurnTime(solidFuel.get(0), minKey, DefaultCapacitorData.BASIC_CAPACITOR);
      double maxEnergyProduced = maxEnergyProducedPerTick * TileStirlingGenerator.getBurnTime(solidFuel.get(0), CapacitorKey.STIRLING_POWER_GEN, DefaultCapacitorData.ENDER_CAPACITOR);

      ingredients.setOutputs(EnergyIngredient.class,
          new NNList<>(new EnergyIngredient((int) Math.round(minEnergyProducedPerTick), true),
              new EnergyIngredient((int) Math.round(maxEnergyProducedPerTick), true), new EnergyIngredient((int) Math.round(minEnergyProduced), false),
              new EnergyIngredient((int) Math.round(maxEnergyProduced), false)));
    }

    @Override
    public void drawInfo(@Nonnull Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY) {
      FontRenderer fr = minecraft.fontRenderer;

      String txt = Lang.GUI_STIRGEN_OUTPUT.get("");
      int sw = fr.getStringWidth(txt);
      fr.drawStringWithShadow(txt, 89 - sw / 2 - xOff, 10 - yOff, ColorUtil.getRGB(Color.WHITE));
      txt = "-";
      sw = fr.getStringWidth(txt);
      fr.drawStringWithShadow(txt, 89 - sw / 2 - xOff, 22 - yOff, ColorUtil.getRGB(Color.WHITE));
      fr.drawStringWithShadow(txt, 89 - sw / 2 - xOff, 68 - yOff, ColorUtil.getRGB(Color.WHITE));

      GlStateManager.color(1, 1, 1, 1);

      if (stirlingFront != null) {
        stirlingFront.draw(minecraft, 129 - xOff, 40 - yOff);
        IconEIO.map.render(IconEIO.GENERIC_VERBOTEN, 135 - xOff, 34 - yOff, true);
      }
    }

    @Override
    public @Nonnull List<String> getTooltipStrings(int mouseX, int mouseY) {
      if (stirlingFront != null && mouseX >= (121 - xOff) && mouseX <= (121 - xOff + 32) && mouseY >= 32 - yOff && mouseY <= 32 - yOff + 32) {
        return Lang.JEI_STIRGEN_NOTSIMPLE.getLines();
      }
      if (mouseY < (32 - yOff) || mouseY >= (69 - yOff)) {
        return Lang.JEI_STIRGEN_RANGE.getLines();
      }
      return super.getTooltipStrings(mouseX, mouseY);
    }

  } // -------------------------------------

  public static void register(@Nonnull IModRegistry registry, @Nonnull IGuiHelper guiHelper) {

    registry.addRecipeCategories(new StirlingRecipeCategory(guiHelper));
    registry.addRecipeCategoryCraftingItem(new ItemStack(MachineObject.block_stirling_generator.getBlockNN(), 1, 0), StirlingRecipeCategory.UID);
    registry.addRecipeCategoryCraftingItem(new ItemStack(MachineObject.block_simple_stirling_generator.getBlockNN(), 1, 0), StirlingRecipeCategory.UID);
    registry.addRecipeClickArea(GuiStirlingGenerator.class, 155, 42, 16, 16, StirlingRecipeCategory.UID);
    registry.getRecipeTransferRegistry().addRecipeTransferHandler(ContainerStirlingGenerator.Normal.class, StirlingRecipeCategory.UID, 0, 1, 2, 4 * 9);
    registry.getRecipeTransferRegistry().addRecipeTransferHandler(ContainerStirlingGenerator.Simple.class, StirlingRecipeCategory.UID, 0, 1, 1, 4 * 9);

    long start = System.nanoTime();

    // Put valid fuel to "buckets" based on their burn time (energy production)
    FuelCache.initialize(registry.getIngredientRegistry().getAllIngredients(ItemStack.class));
    NNMap<Integer, NNList<ItemStack>> recipeInputs = new NNMap.Brutal<>();

    FuelCache.getFuels().apply(new Callback<ItemStack>() {
      @Override
      public void apply(@Nonnull ItemStack stack) {
        int burntime = TileStirlingGenerator.getBurnTimeGeneric(stack);
        if (burntime <= 0)
          return;
        if (recipeInputs.containsKey(burntime)) {
          recipeInputs.get(burntime).add(stack);
        } else {
          NNList<ItemStack> list = new NNList<>();
          list.add(stack);
          recipeInputs.put(burntime, list);
        }
      }
    });

    List<StirlingRecipeWrapper> recipeList = new ArrayList<StirlingRecipeWrapper>();
    // Order recipes from best to worst
    TreeSet<Integer> recipeOrder = new TreeSet<Integer>(recipeInputs.keySet());
    Iterator<Integer> it = recipeOrder.descendingIterator();
    while (it.hasNext())
      recipeList.add(new StirlingRecipeWrapper(recipeInputs.get(it.next()), guiHelper));

    registry.addRecipes(recipeList, UID);

    long end = System.nanoTime();
    Log.info(String.format("StirlingRecipeCategory: Added %d stirling generator recipes for %d solid fuels to JEI in %.3f seconds.", recipeList.size(),
        FuelCache.getFuels().size(), (end - start) / 1000000000d));
  }

  // ------------ Category

  // Offsets from full size gui, makes it much easier to get the location
  // correct
  static int xOff = 25 + 3;
  static int yOff = 7;
  static int xSize = 136 - 3;

  private final @Nonnull IDrawable background;
  private final @Nonnull IDrawableAnimated flame;

  public StirlingRecipeCategory(@Nonnull IGuiHelper guiHelper) {
    ResourceLocation backgroundLocation = EnderIO.proxy.getGuiTexture("stirling_generator");
    background = guiHelper.createDrawable(backgroundLocation, xOff, yOff, xSize, 70);

    IDrawableStatic flameDrawable = guiHelper.createDrawable(backgroundLocation, 176, 0, 13, 13);
    flame = guiHelper.createAnimatedDrawable(flameDrawable, 200, IDrawableAnimated.StartDirection.BOTTOM, false);
  }

  @Override
  public @Nonnull String getUid() {
    return UID;
  }

  @SuppressWarnings("null")
  @Override
  public @Nonnull String getTitle() {
    return MachineObject.block_stirling_generator.getBlock().getLocalizedName();
  }

  @Override
  public @Nonnull IDrawable getBackground() {
    return background;
  }

  @Override
  public void drawExtras(@Nonnull Minecraft minecraft) {
    flame.draw(minecraft, 81 - xOff, 53 - yOff);
  }

  @Override
  public void setRecipe(@Nonnull IRecipeLayout recipeLayout, @Nonnull StirlingRecipeWrapper recipeWrapper, @Nonnull IIngredients ingredients) {
    IGuiItemStackGroup guiItemStacks = recipeLayout.getItemStacks();
    IGuiIngredientGroup<EnergyIngredient> group = recipeLayout.getIngredientsGroup(EnergyIngredient.class);

    guiItemStacks.init(0, true, 79 - xOff, 33 - yOff);
    group.init(1, false, EnergyIngredientRenderer.INSTANCE, 37 - xOff, 21 - yOff, 40, 10, 0, 0);
    group.init(2, false, EnergyIngredientRenderer.INSTANCE, 54 + 47 - xOff, 21 - yOff, 40, 10, 0, 0);
    group.init(3, false, EnergyIngredientRenderer.INSTANCE, 30 - xOff, 67 - yOff, 52, 10, 0, 0);
    group.init(4, false, EnergyIngredientRenderer.INSTANCE, 54 + 44 - xOff, 67 - yOff, 60, 10, 0, 0);

    guiItemStacks.set(ingredients);
    group.set(ingredients);
  }

  @Override
  public @Nonnull String getModName() {
    return EnderIOMachines.MODID;
  }

}
