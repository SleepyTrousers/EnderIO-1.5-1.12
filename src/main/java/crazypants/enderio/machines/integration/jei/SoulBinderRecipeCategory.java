package crazypants.enderio.machines.integration.jei;

import static crazypants.enderio.base.init.ModObject.itemBrokenSpawner;
import static crazypants.enderio.base.init.ModObject.itemSoulVial;
import static crazypants.enderio.machines.init.MachineObject.block_soul_binder;
import static crazypants.enderio.machines.machine.soul.ContainerSoulBinder.FIRST_INVENTORY_SLOT;
import static crazypants.enderio.machines.machine.soul.ContainerSoulBinder.FIRST_RECIPE_SLOT;
import static crazypants.enderio.machines.machine.soul.ContainerSoulBinder.NUM_INVENTORY_SLOT;
import static crazypants.enderio.machines.machine.soul.ContainerSoulBinder.NUM_RECIPE_SLOT;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;

import crazypants.enderio.base.EnderIO;
import crazypants.enderio.base.init.ModObject;
import crazypants.enderio.base.machine.interfaces.ISoulBinder;
import crazypants.enderio.base.power.PowerDisplayUtil;
import crazypants.enderio.base.recipe.MachineRecipeRegistry;
import crazypants.enderio.base.recipe.soul.ISoulBinderRecipe;
import crazypants.enderio.base.recipe.soul.SoulBinderTunedPressurePlateRecipe;
import crazypants.enderio.machines.machine.soul.ContainerSoulBinder;
import crazypants.enderio.machines.machine.soul.GuiSoulBinder;
import crazypants.enderio.util.CapturedMob;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IDrawableAnimated;
import mezz.jei.api.gui.IDrawableStatic;
import mezz.jei.api.gui.IGuiItemStackGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.BlankRecipeCategory;
import mezz.jei.api.recipe.BlankRecipeWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class SoulBinderRecipeCategory extends BlankRecipeCategory<SoulBinderRecipeCategory.SoulBinderRecipeWrapper> {

  public static final @Nonnull String UID = "SoulBinder";

  // ------------ Recipes

  public static class SoulBinderRecipeWrapper extends BlankRecipeWrapper {
    
    private ISoulBinderRecipe recipe;
    
    public SoulBinderRecipeWrapper(ISoulBinderRecipe recipe) {
      this.recipe = recipe;      
    }

    public long getEnergyRequired() { 
      return recipe.getEnergyRequired();
    }

    @Override
    public void getIngredients(@Nonnull IIngredients ingredients) {
      ingredients.setInputs(ItemStack.class, Arrays.asList(new ItemStack(itemSoulVial.getItemNN()), recipe.getInputStack()));
      ingredients.setOutputs(ItemStack.class, Arrays.asList(recipe.getOutputStack(), new ItemStack(itemSoulVial.getItemNN())));
    }
    
    @Override
    public void drawInfo(@Nonnull Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY) {           

      int cost = recipe.getExperienceLevelsRequired();
      String str = I18n.format("container.repair.cost", new Object[] { cost });
      minecraft.fontRenderer.drawString(str, 6, 26, 0x80FF20);
      
      String energyString = PowerDisplayUtil.formatPower(recipe.getEnergyRequired()) + " " + PowerDisplayUtil.abrevation();
      minecraft.fontRenderer.drawString(energyString, 6, 36, 0x808080, false);    
      GlStateManager.color(1,1,1,1);      
    }
  }
 
  public static void register(IModRegistry registry, IGuiHelper guiHelper) {
    
    registry.addRecipeCategories(new SoulBinderRecipeCategory(guiHelper));
    registry.handleRecipes(ISoulBinderRecipe.class, SoulBinderRecipeWrapper::new, SoulBinderRecipeCategory.UID);
    registry.addRecipeClickArea(GuiSoulBinder.class, 155, 42, 16, 16, SoulBinderRecipeCategory.UID);
    registry.addRecipeCategoryCraftingItem(new ItemStack(block_soul_binder.getBlock()), SoulBinderRecipeCategory.UID);

    registry.addRecipes(
      MachineRecipeRegistry.instance.getRecipesForMachine(block_soul_binder.getUnlocalisedName()).values().stream()
        .filter(r -> r instanceof ISoulBinderRecipe)
        .collect(Collectors.toList()), 
      UID
    );

    registry.getRecipeTransferRegistry().addRecipeTransferHandler(ContainerSoulBinder.class, SoulBinderRecipeCategory.UID, FIRST_RECIPE_SLOT, NUM_RECIPE_SLOT,
        FIRST_INVENTORY_SLOT, NUM_INVENTORY_SLOT);
  }

  // ------------ Category

  //Offsets from full size gui, makes it much easier to get the location correct
  private int xOff = 34;
  private int yOff = 28;
  
  @Nonnull
  private final IDrawable background;

  @Nonnull
  protected final IDrawableAnimated arror;
  
  private SoulBinderRecipeWrapper currentRecipe;

  public SoulBinderRecipeCategory(IGuiHelper guiHelper) {
    ResourceLocation backgroundLocation = EnderIO.proxy.getGuiTexture("soulFuser");
    background = guiHelper.createDrawable(backgroundLocation, xOff, yOff, 120, 50);

    IDrawableStatic flameDrawable = guiHelper.createDrawable(backgroundLocation, 177, 14, 22, 16);
    arror = guiHelper.createAnimatedDrawable(flameDrawable, 200, IDrawableAnimated.StartDirection.LEFT, false);
  }

  @Override
  public @Nonnull String getUid() {
    return UID;
  }

  @Override
  public @Nonnull String getTitle() {
    String localizedName = block_soul_binder.getBlock().getLocalizedName();
    return localizedName != null ? localizedName : "ERROR";
  }

  @Override
  public @Nonnull IDrawable getBackground() {
    return background;
  }

  @Override
  public void drawExtras(@Nonnull Minecraft minecraft) {
    arror.draw(minecraft, 81 - xOff, 35 - yOff);
  }  
  
  @Override
  public void setRecipe(@Nonnull IRecipeLayout recipeLayout, @Nonnull SoulBinderRecipeCategory.SoulBinderRecipeWrapper recipeWrapper, @Nonnull IIngredients ingredients) {
    currentRecipe = recipeWrapper;

    IGuiItemStackGroup guiItemStacks = recipeLayout.getItemStacks();
    guiItemStacks.init(0, true, 37 - xOff, 33 - yOff);
    guiItemStacks.init(1, true, 58 - xOff, 33 - yOff);
    guiItemStacks.init(2, false, 111 - xOff, 33 - yOff);
    guiItemStacks.init(3, false, 133 - xOff, 33 - yOff);
    
    final ItemStack outputStack = currentRecipe.recipe.getOutputStack();
    final List<ResourceLocation> supportedSouls = currentRecipe.recipe.getSupportedSouls();
    final ItemStack inputStack = currentRecipe.recipe.getInputStack();

    List<CapturedMob> souls = CapturedMob.getSouls(supportedSouls);
    final List<ItemStack> soulStacks = new ArrayList<ItemStack>();
    for (CapturedMob soul : souls) {
      soulStacks.add(soul.toStack(itemSoulVial.getItemNN(), 1, 1));
    }
    guiItemStacks.set(0, soulStacks);

    if (inputStack != null) {
      guiItemStacks.set(1, inputStack);
    }

    if (itemBrokenSpawner.getItem() == outputStack.getItem() || currentRecipe.recipe instanceof SoulBinderTunedPressurePlateRecipe) {
      List<ItemStack> outputs = new ArrayList<ItemStack>();
      for (CapturedMob soul : souls) {
        outputs.add(soul.toStack(outputStack.getItem(), outputStack.getMetadata(), 1));
      }      
      guiItemStacks.set(2, outputs);
    } else {
      guiItemStacks.set(2, outputStack);
    }

    guiItemStacks.set(3, new ItemStack(itemSoulVial.getItemNN()));
  }
  
}
