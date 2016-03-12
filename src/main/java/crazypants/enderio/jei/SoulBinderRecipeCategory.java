package crazypants.enderio.jei;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;

import crazypants.enderio.EnderIO;
import crazypants.enderio.ModObject;
import crazypants.enderio.machine.IMachineRecipe;
import crazypants.enderio.machine.MachineRecipeRegistry;
import crazypants.enderio.machine.power.PowerDisplayUtil;
import crazypants.enderio.machine.soul.GuiSoulBinder;
import crazypants.enderio.machine.soul.ISoulBinderRecipe;
import crazypants.enderio.machine.spawner.ItemBrokenSpawner;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IDrawableAnimated;
import mezz.jei.api.gui.IDrawableStatic;
import mezz.jei.api.gui.IGuiItemStackGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.recipe.BlankRecipeCategory;
import mezz.jei.api.recipe.BlankRecipeWrapper;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;

public class SoulBinderRecipeCategory extends BlankRecipeCategory {

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
    public List<?> getInputs() {      
      return Arrays.asList(new ItemStack(EnderIO.itemSoulVessel), recipe.getInputStack() );
    }

    @Override
    public List<?> getOutputs() {
      return Arrays.asList(recipe.getOutputStack(), new ItemStack(EnderIO.itemSoulVessel));
    }   
    
    @Override
    public void drawInfo(@Nonnull Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY) {           

      int cost = recipe.getExperienceLevelsRequired();
      String str = I18n.format("container.repair.cost", new Object[] { cost });
      minecraft.fontRendererObj.drawString(str, 6, 26, 0x80FF20);
      
      String energyString = PowerDisplayUtil.formatPower(recipe.getEnergyRequired()) + " " + PowerDisplayUtil.abrevation();
      minecraft.fontRendererObj.drawString(energyString, 6, 36, 0x808080, false);    
      GlStateManager.color(1,1,1,1);      
    }
  }
 
  public static void register(IModRegistry registry, IGuiHelper guiHelper) {
    
    registry.addRecipeCategories(new SoulBinderRecipeCategory(guiHelper));
    registry.addRecipeHandlers(new BaseRecipeHandler<SoulBinderRecipeWrapper>(SoulBinderRecipeWrapper.class, SoulBinderRecipeCategory.UID));
    registry.addRecipeClickArea(GuiSoulBinder.class, 155, 42, 16, 16, SoulBinderRecipeCategory.UID);
    
    List<SoulBinderRecipeWrapper> result = new ArrayList<SoulBinderRecipeWrapper>(); 
    Map<String, IMachineRecipe> recipes = MachineRecipeRegistry.instance.getRecipesForMachine(ModObject.blockSoulBinder.unlocalisedName);
    if(recipes.isEmpty()) {
      return;
    }
    for (IMachineRecipe recipe : recipes.values()) {
      if(recipe instanceof ISoulBinderRecipe) {
        result.add(new SoulBinderRecipeWrapper((ISoulBinderRecipe)recipe));
      }
    }
    registry.addRecipes(result);
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
    ResourceLocation backgroundLocation = new ResourceLocation(EnderIO.MODID, "textures/gui/23/soulFuser.png");
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
    String localizedName = EnderIO.blockSoulFuser.getLocalizedName();
    return localizedName != null ? localizedName : "ERROR";
  }

  @Override
  public @Nonnull IDrawable getBackground() {
    return background;
  }

  @Override
  public void drawAnimations(@Nonnull Minecraft minecraft) {
    arror.draw(minecraft, 81 - xOff, 35 - yOff);
  }  
  
  @Override
  public void setRecipe(@Nonnull IRecipeLayout recipeLayout, @Nonnull IRecipeWrapper recipeWrapper) {
    if(recipeWrapper instanceof SoulBinderRecipeWrapper) {
      currentRecipe = (SoulBinderRecipeWrapper)recipeWrapper;
    } else {
      currentRecipe = null;
    }
    
    IGuiItemStackGroup guiItemStacks = recipeLayout.getItemStacks();
    guiItemStacks.init(0, true, 37 - xOff, 33 - yOff);
    guiItemStacks.init(1, true, 58 - xOff, 33 - yOff);
    guiItemStacks.init(2, false, 111 - xOff, 33 - yOff);
    guiItemStacks.init(3, false, 133 - xOff, 33 - yOff);
    
    guiItemStacks.set(0, getSoulVialInputs(currentRecipe.recipe.getSupportedSouls()));
    guiItemStacks.setFromRecipe(1, currentRecipe.recipe.getInputStack());
    if(EnderIO.itemBrokenSpawner == currentRecipe.recipe.getOutputStack().getItem()) {
      List<ItemStack> outputs = new ArrayList<ItemStack>();
      for(String soul : currentRecipe.recipe.getSupportedSouls()) {
        outputs.add(ItemBrokenSpawner.createStackForMobType(soul));  
      }      
      guiItemStacks.setFromRecipe(2, outputs);
    } else {
      guiItemStacks.setFromRecipe(2, currentRecipe.recipe.getOutputStack());
    }
    guiItemStacks.setFromRecipe(3, new ItemStack(EnderIO.itemSoulVessel));
  }
  
  private List<ItemStack> getSoulVialInputs(List<String> mobs) {
    List<ItemStack> result = new ArrayList<ItemStack>(mobs.size());
    for (String mobName : mobs) {
      ItemStack sv = new ItemStack(EnderIO.itemSoulVessel);
      sv.setTagCompound(new NBTTagCompound());
      sv.getTagCompound().setString("id", mobName);
      result.add(sv);
    }
    return result;
  }
  
}
