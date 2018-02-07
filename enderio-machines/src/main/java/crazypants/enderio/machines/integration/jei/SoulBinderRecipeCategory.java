package crazypants.enderio.machines.integration.jei;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;

import com.enderio.core.common.util.NNList;

import crazypants.enderio.base.EnderIO;
import crazypants.enderio.base.integration.jei.energy.EnergyIngredient;
import crazypants.enderio.base.integration.jei.energy.EnergyIngredientRenderer;
import crazypants.enderio.base.recipe.MachineRecipeRegistry;
import crazypants.enderio.base.recipe.soul.ISoulBinderRecipe;
import crazypants.enderio.base.recipe.soul.SoulBinderTunedPressurePlateRecipe;
import crazypants.enderio.machines.EnderIOMachines;
import crazypants.enderio.machines.lang.Lang;
import crazypants.enderio.machines.machine.soul.ContainerSoulBinder;
import crazypants.enderio.machines.machine.soul.GuiSoulBinder;
import crazypants.enderio.util.CapturedMob;
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
import mezz.jei.api.recipe.IFocus;
import mezz.jei.api.recipe.IFocus.Mode;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import static crazypants.enderio.base.init.ModObject.itemBrokenSpawner;
import static crazypants.enderio.base.init.ModObject.itemSoulVial;
import static crazypants.enderio.machines.init.MachineObject.block_soul_binder;
import static crazypants.enderio.machines.machine.soul.ContainerSoulBinder.FIRST_INVENTORY_SLOT;
import static crazypants.enderio.machines.machine.soul.ContainerSoulBinder.FIRST_RECIPE_SLOT;
import static crazypants.enderio.machines.machine.soul.ContainerSoulBinder.NUM_INVENTORY_SLOT;
import static crazypants.enderio.machines.machine.soul.ContainerSoulBinder.NUM_RECIPE_SLOT;

public class SoulBinderRecipeCategory extends BlankRecipeCategory<SoulBinderRecipeCategory.SoulBinderRecipeWrapper> {

  public static final @Nonnull String UID = "SoulBinder";

  // ------------ Recipes

  public static class SoulBinderRecipeWrapper extends BlankRecipeWrapper {

    private ISoulBinderRecipe recipe;

    public SoulBinderRecipeWrapper(ISoulBinderRecipe recipe) {
      this.recipe = recipe;
    }

    @Override
    public void getIngredients(@Nonnull IIngredients ingredients) {

      final ItemStack outputStack = recipe.getOutputStack();
      final List<ResourceLocation> supportedSouls = recipe.getSupportedSouls();
      final ItemStack inputStack = recipe.getInputStack();
      final List<ItemStack> inputItemList = new NNList<>();

      if (Prep.isValid(inputStack)) {
        inputItemList.add(inputStack);
        if (itemBrokenSpawner.getItem() == inputStack.getItem()) {
          // a Broken Spawner on the input side always means "any kind of", so put any kind of broken spawner into the input list. Skip the same type as the
          // output, that would be a wasteful NOP recipe
          CapturedMob resultMob = CapturedMob.create(outputStack);
          for (CapturedMob soul : CapturedMob.getAllSouls()) {
            if (resultMob == null || !resultMob.isSameType(soul)) {
              inputItemList.add(soul.toStack(itemBrokenSpawner.getItemNN(), 0, 1));
            }
          }
        }
      }

      List<CapturedMob> souls = CapturedMob.getSouls(supportedSouls);
      final List<ItemStack> soulStacks = new ArrayList<ItemStack>();
      for (CapturedMob soul : souls) {
        soulStacks.add(soul.toStack(itemSoulVial.getItemNN(), 1, 1));
      }

      ingredients.setInputLists(ItemStack.class, new NNList<List<ItemStack>>(soulStacks, inputItemList));

      if (itemBrokenSpawner.getItem() == outputStack.getItem() || recipe instanceof SoulBinderTunedPressurePlateRecipe) {
        // these recipes take any kind of mob. make it so that any type is in the input list
        List<ItemStack> outputs = new ArrayList<ItemStack>();
        for (CapturedMob soul : souls) {
          outputs.add(soul.toStack(outputStack.getItem(), outputStack.getMetadata(), 1));
        }
        ingredients.setOutputLists(ItemStack.class, new NNList<List<ItemStack>>(Collections.singletonList(new ItemStack(itemSoulVial.getItemNN())), outputs));
      } else {
        ingredients.setOutputLists(ItemStack.class,
            new NNList<List<ItemStack>>(Collections.singletonList(new ItemStack(itemSoulVial.getItemNN())), Collections.singletonList(outputStack)));
      }

      ingredients.setInput(EnergyIngredient.class, new EnergyIngredient(recipe.getEnergyRequired()));
    }

    @Override
    public void drawInfo(@Nonnull Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY) {
      int cost = recipe.getExperienceLevelsRequired();
      String str = Lang.GUI_VANILLA_REPAIR_COST.get(cost);
      minecraft.fontRenderer.drawString(str, 6, 26, 0x80FF20, true);
      GlStateManager.color(1, 1, 1, 1);
    }
  }

  @SuppressWarnings("null")
  public static void register(IModRegistry registry, IGuiHelper guiHelper) {
    registry.addRecipeCategories(new SoulBinderRecipeCategory(guiHelper));
    registry.handleRecipes(ISoulBinderRecipe.class, SoulBinderRecipeWrapper::new, SoulBinderRecipeCategory.UID);
    registry.addRecipeClickArea(GuiSoulBinder.class, 155, 42, 16, 16, SoulBinderRecipeCategory.UID);
    registry.addRecipeCategoryCraftingItem(new ItemStack(block_soul_binder.getBlockNN()), SoulBinderRecipeCategory.UID);

    registry.addRecipes(MachineRecipeRegistry.instance.getRecipesForMachine(MachineRecipeRegistry.SOULBINDER).values().stream()
        .filter(r -> r instanceof ISoulBinderRecipe).collect(Collectors.toList()), UID);

    registry.getRecipeTransferRegistry().addRecipeTransferHandler(ContainerSoulBinder.class, SoulBinderRecipeCategory.UID, FIRST_RECIPE_SLOT, NUM_RECIPE_SLOT,
        FIRST_INVENTORY_SLOT, NUM_INVENTORY_SLOT);
  }

  // ------------ Category

  // Offsets from full size gui, makes it much easier to get the location correct
  private int xOff = 34;
  private int yOff = 28;

  private final @Nonnull IDrawable background;
  private final @Nonnull IDrawableAnimated arrow;

  public SoulBinderRecipeCategory(IGuiHelper guiHelper) {
    ResourceLocation backgroundLocation = EnderIO.proxy.getGuiTexture("soul_fuser");
    background = guiHelper.createDrawable(backgroundLocation, xOff, yOff, 120, 50);

    IDrawableStatic flameDrawable = guiHelper.createDrawable(backgroundLocation, 177, 14, 22, 16);
    arrow = guiHelper.createAnimatedDrawable(flameDrawable, 200, IDrawableAnimated.StartDirection.LEFT, false);
  }

  @Override
  public @Nonnull String getUid() {
    return UID;
  }

  @Override
  public @Nonnull String getTitle() {
    return block_soul_binder.getBlockNN().getLocalizedName();
  }

  @Override
  public @Nonnull IDrawable getBackground() {
    return background;
  }

  @Override
  public void drawExtras(@Nonnull Minecraft minecraft) {
    arrow.draw(minecraft, 81 - xOff, 35 - yOff);
  }

  @Override
  public void setRecipe(@Nonnull IRecipeLayout recipeLayout, @Nonnull SoulBinderRecipeCategory.SoulBinderRecipeWrapper recipeWrapper,
      @Nonnull IIngredients ingredients) {

    IGuiItemStackGroup guiItemStacks = recipeLayout.getItemStacks();
    IGuiIngredientGroup<EnergyIngredient> group = recipeLayout.getIngredientsGroup(EnergyIngredient.class);

    guiItemStacks.init(0, true, 37 - xOff, 33 - yOff);
    guiItemStacks.init(1, true, 58 - xOff, 33 - yOff);
    guiItemStacks.init(2, false, 111 - xOff, 33 - yOff);
    guiItemStacks.init(3, false, 133 - xOff, 33 - yOff);
    group.init(xOff, true, EnergyIngredientRenderer.INSTANCE, 5, 35, 60, 10, 0, 0);

    guiItemStacks.set(ingredients);
    IFocus<?> focus = recipeLayout.getFocus();
    if (focus != null && focus.getMode() == Mode.INPUT && focus.getValue() instanceof ItemStack
        && ((ItemStack) focus.getValue()).getItem() == itemSoulVial.getItemNN() && CapturedMob.containsSoul(((ItemStack) focus.getValue()))
        && ingredients.getOutputs(ItemStack.class).get(1).size() > 1) {
      // we are focused on a filled soul vial on the input side and the output has a list
      final CapturedMob vialMob = CapturedMob.create(((ItemStack) focus.getValue()));
      if (vialMob != null) {
        List<ItemStack> newOutputs = new ArrayList<>();
        for (ItemStack output : ingredients.getOutputs(ItemStack.class).get(1)) {
          if (output != null && vialMob.isSameType(CapturedMob.create(output))) {
            newOutputs.add(output);
          }
        }
        guiItemStacks.set(3, newOutputs);
      }
    }

    if (focus != null && focus.getMode() == Mode.OUTPUT && focus.getValue() instanceof ItemStack && CapturedMob.containsSoul(((ItemStack) focus.getValue()))
        && ingredients.getInputs(ItemStack.class).get(0).size() > 1 && ingredients.getOutputs(ItemStack.class).get(1).size() > 1) {
      // we are focused on a output item and the both sides have a list
      final CapturedMob resultMob = CapturedMob.create(((ItemStack) focus.getValue()));
      if (resultMob != null) {
        List<ItemStack> newInputs = new ArrayList<>();
        for (ItemStack input : ingredients.getInputs(ItemStack.class).get(0)) {
          if (input != null && resultMob.isSameType(CapturedMob.create(input))) {
            newInputs.add(input);
          }
        }
        guiItemStacks.set(0, newInputs);
        guiItemStacks.set(3, ((ItemStack) focus.getValue()));
      }
    }

    group.set(ingredients);
  }

  @Override
  public @Nonnull String getModName() {
    return EnderIOMachines.MODID;
  }

}
