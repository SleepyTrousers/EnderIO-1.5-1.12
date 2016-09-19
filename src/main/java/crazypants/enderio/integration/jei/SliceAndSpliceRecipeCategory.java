package crazypants.enderio.integration.jei;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import crazypants.enderio.EnderIO;
import crazypants.enderio.item.darksteel.DarkSteelItems;
import crazypants.enderio.machine.power.PowerDisplayUtil;
import crazypants.enderio.machine.recipe.IRecipe;
import crazypants.enderio.machine.slicensplice.ContainerSliceAndSplice;
import crazypants.enderio.machine.slicensplice.GuiSliceAndSplice;
import crazypants.enderio.machine.slicensplice.SliceAndSpliceRecipeManager;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IDrawableAnimated;
import mezz.jei.api.gui.IDrawableStatic;
import mezz.jei.api.gui.IGuiItemStackGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.recipe.BlankRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import static crazypants.enderio.machine.slicensplice.ContainerSliceAndSplice.FIRST_INVENTORY_SLOT;
import static crazypants.enderio.machine.slicensplice.ContainerSliceAndSplice.FIRST_RECIPE_SLOT;
import static crazypants.enderio.machine.slicensplice.ContainerSliceAndSplice.NUM_INVENTORY_SLOT;
import static crazypants.enderio.machine.slicensplice.ContainerSliceAndSplice.NUM_RECIPE_SLOT;

public class SliceAndSpliceRecipeCategory extends BlankRecipeCategory<SliceAndSpliceRecipeCategory.SliceAndSpliceRecipe> {

  public static final @Nonnull String UID = "SliceNSPlice";

  // ------------ Recipes

  public static class SliceAndSpliceRecipe extends RecipeWrapper {
    public SliceAndSpliceRecipe(IRecipe recipe) {
      super(recipe);
    }
  }
 
  public static void register(IModRegistry registry, IGuiHelper guiHelper) {
    
    registry.addRecipeCategories(new SliceAndSpliceRecipeCategory(guiHelper));
    registry.addRecipeHandlers(new RecipeHandler<SliceAndSpliceRecipe>(SliceAndSpliceRecipe.class, SliceAndSpliceRecipeCategory.UID));
    registry.addRecipeClickArea(GuiSliceAndSplice.class, 155, 42, 16, 16, SliceAndSpliceRecipeCategory.UID);
    registry.addRecipeCategoryCraftingItem(new ItemStack(EnderIO.blockSliceAndSplice), SliceAndSpliceRecipeCategory.UID);

    List<SliceAndSpliceRecipe> result = new ArrayList<SliceAndSpliceRecipe>();    
    for (IRecipe rec : SliceAndSpliceRecipeManager.getInstance().getRecipes()) {
      result.add(new SliceAndSpliceRecipe(rec));
    }    
    registry.addRecipes(result);

    registry.getRecipeTransferRegistry().addRecipeTransferHandler(ContainerSliceAndSplice.class, SliceAndSpliceRecipeCategory.UID, FIRST_RECIPE_SLOT,
        NUM_RECIPE_SLOT, FIRST_INVENTORY_SLOT, NUM_INVENTORY_SLOT);
  }

  // ------------ Category

  //Offsets from full size gui, makes it much easier to get the location correct
  private int xOff = 34;
  private int yOff = 10;
  
  @Nonnull
  private final IDrawable background;

  @Nonnull
  protected final IDrawableAnimated arror;
  
  private SliceAndSpliceRecipe currentRecipe;

  public SliceAndSpliceRecipeCategory(IGuiHelper guiHelper) {
    ResourceLocation backgroundLocation = EnderIO.proxy.getGuiTexture("sliceAndSplice");
    background = guiHelper.createDrawable(backgroundLocation, xOff, yOff, 125, 70);

    IDrawableStatic flameDrawable = guiHelper.createDrawable(backgroundLocation, 177, 14, 22, 16);
    arror = guiHelper.createAnimatedDrawable(flameDrawable, 200, IDrawableAnimated.StartDirection.LEFT, false);
  }

  @Override
  public @Nonnull String getUid() {
    return UID;
  }

  @Override
  public @Nonnull String getTitle() {
    String localizedName = EnderIO.blockSliceAndSplice.getLocalizedName();
    return localizedName != null ? localizedName : "ERROR";
  }

  @Override
  public @Nonnull IDrawable getBackground() {
    return background;
  }

  @Override
  public void drawAnimations(@Nonnull Minecraft minecraft) {
    arror.draw(minecraft, 104 - xOff, 49 - yOff);
  }
  
  @Override
  public void drawExtras(@Nonnull Minecraft minecraft) {
    if(currentRecipe == null) {
      return;
    }
    String energyString = PowerDisplayUtil.formatPower(currentRecipe.getEnergyRequired()) + " " + PowerDisplayUtil.abrevation();
    minecraft.fontRendererObj.drawString(energyString, 108 - xOff, 72 - yOff, 0x808080, false);    
    GlStateManager.color(1,1,1,1);
  }
  
  @Override
  public void setRecipe(@Nonnull IRecipeLayout recipeLayout, @Nonnull SliceAndSpliceRecipeCategory.SliceAndSpliceRecipe recipeWrapper) {
    currentRecipe = recipeWrapper;

    IGuiItemStackGroup guiItemStacks = recipeLayout.getItemStacks();

    guiItemStacks.init(0, false, 53 - xOff, 15 - yOff);
    guiItemStacks.init(1, false, 71 - xOff, 15 - yOff);
    guiItemStacks.init(2, true, 43 - xOff, 39 - yOff);
    guiItemStacks.init(3, true, 61 - xOff, 39 - yOff);
    guiItemStacks.init(4, true, 79 - xOff, 39 - yOff);
    guiItemStacks.init(5, true, 43 - xOff, 57 - yOff);
    guiItemStacks.init(6, true, 61 - xOff, 57 - yOff);
    guiItemStacks.init(7, true, 79 - xOff, 57 - yOff);        
    guiItemStacks.init(8, false, 133 - xOff, 48 - yOff);

    
    guiItemStacks.setFromRecipe(0, getAxes());
    guiItemStacks.setFromRecipe(1, getShears());
    
    
    List<?> inputs = recipeWrapper.getInputs();
    int slot = 2;
    for(Object input : inputs) {
      if (input != null) {
        guiItemStacks.setFromRecipe(slot, input);
      }
      ++slot;
    }    
    Object output = recipeWrapper.getOutputs().get(0);
    if (output != null) {
      guiItemStacks.setFromRecipe(8, output);
    }
  }
  
  private @Nonnull List<ItemStack> getAxes() {
    List<ItemStack> res = new ArrayList<ItemStack>();
    res.add(new ItemStack(Items.WOODEN_AXE));
    res.add(new ItemStack(Items.IRON_AXE));
    res.add(new ItemStack(Items.GOLDEN_AXE));
    res.add(new ItemStack(Items.DIAMOND_AXE));
    res.add(new ItemStack(DarkSteelItems.itemDarkSteelAxe));
    return res;
  }
  
  private @Nonnull List<ItemStack> getShears() {
    List<ItemStack> res = new ArrayList<ItemStack>();
    res.add(new ItemStack(Items.SHEARS));    
    res.add(new ItemStack(DarkSteelItems.itemDarkSteelShears));
    return res;
  }

}
