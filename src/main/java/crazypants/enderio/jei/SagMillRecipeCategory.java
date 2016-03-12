package crazypants.enderio.jei;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import crazypants.enderio.EnderIO;
import crazypants.enderio.gui.TooltipHandlerGrinding;
import crazypants.enderio.machine.crusher.CrusherRecipeManager;
import crazypants.enderio.machine.crusher.GrindingBall;
import crazypants.enderio.machine.crusher.GuiCrusher;
import crazypants.enderio.machine.power.PowerDisplayUtil;
import crazypants.enderio.machine.recipe.IRecipe;
import crazypants.enderio.machine.recipe.RecipeOutput;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IDrawableAnimated;
import mezz.jei.api.gui.IDrawableStatic;
import mezz.jei.api.gui.IGuiItemStackGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.ITooltipCallback;
import mezz.jei.api.recipe.BlankRecipeCategory;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;

public class SagMillRecipeCategory extends BlankRecipeCategory implements ITooltipCallback<ItemStack> {

  public static final @Nonnull String UID = "SagMill";

  // ------------ Recipes

  public static class SagRecipe extends RecipeWrapper {
    public SagRecipe(IRecipe recipe) {
      super(recipe);
    }
  }

  public static void register(IModRegistry registry, IGuiHelper guiHelper) {

    registry.addRecipeCategories(new SagMillRecipeCategory(guiHelper));
    registry.addRecipeHandlers(new RecipeHandler<SagRecipe>(SagRecipe.class, SagMillRecipeCategory.UID));
    registry.addRecipeClickArea(GuiCrusher.class, 155, 42, 16, 16, SagMillRecipeCategory.UID);

    List<SagRecipe> result = new ArrayList<SagRecipe>();
    for (IRecipe rec : CrusherRecipeManager.getInstance().getRecipes()) {
      result.add(new SagRecipe(rec));
    }
    registry.addRecipes(result);
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
    ResourceLocation backgroundLocation = new ResourceLocation(EnderIO.MODID, "textures/gui/23/crusher.png");
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
    String localizedName = EnderIO.blockCrusher.getLocalizedName();
    return localizedName != null ? localizedName : "ERROR";
  }

  @Override
  public @Nonnull IDrawable getBackground() {
    return background;
  }

  @Override
  public void drawAnimations(@Nonnull Minecraft minecraft) {
    arrow.draw(minecraft, 80 - xOff, 32 - yOff);
  }

  @Override
  public void drawExtras(@Nonnull Minecraft minecraft) {
    if (currentRecipe == null) {
      return;
    }
    String energyString = PowerDisplayUtil.formatPower(currentRecipe.getEnergyRequired()) + " " + PowerDisplayUtil.abrevation();
    minecraft.fontRendererObj.drawString(energyString, 135 - xOff, 63 - yOff, 0x808080, false);
    GlStateManager.color(1, 1, 1, 1);
  }

  @Override
  public void setRecipe(@Nonnull IRecipeLayout recipeLayout, @Nonnull IRecipeWrapper recipeWrapper) {

    if (recipeWrapper instanceof SagRecipe) {
      currentRecipe = (SagRecipe) recipeWrapper;
    } else {
      currentRecipe = null;
      return;
    }

    IGuiItemStackGroup guiItemStacks = recipeLayout.getItemStacks();    
    guiItemStacks.addTooltipCallback(this);

    guiItemStacks.init(0, true, 79 - xOff, 11 - yOff);
    guiItemStacks.init(1, true, 48 - xOff, 58 - yOff);
    guiItemStacks.init(2, true, 69 - xOff, 58 - yOff);
    guiItemStacks.init(3, false, 90 - xOff, 58 - yOff);
    guiItemStacks.init(4, false, 111 - xOff, 58 - yOff);    
    guiItemStacks.init(5, true, 121 - xOff, 22 - yOff);

    Object ingredients = currentRecipe.getInputs().get(0);
    if (ingredients != null) {
      guiItemStacks.setFromRecipe(0, ingredients);
    }
    int i = 1;
    for (Object output : currentRecipe.getOutputs()) {
      if (output != null) {
        guiItemStacks.setFromRecipe(i, output);
        i++;
      }
    }    
    guiItemStacks.set(5, getBalls());       
  }
  
  @Override
  public void onTooltip(int slotIndex, boolean input, ItemStack ingredient, List<String> tooltip) {  
    if(slotIndex == 0) {
      return;
    }
    if(slotIndex == 5) {
      if(ballsTT.shouldHandleItem(ingredient)) {
        ballsTT.addDetailedEntries(ingredient, Minecraft.getMinecraft().thePlayer, tooltip, true);
      }
      return;
    }
    if(slotIndex - 1 >= currentRecipe.getRecipe().getOutputs().length) {
      return;
    }
    RecipeOutput output = currentRecipe.getRecipe().getOutputs()[slotIndex - 1];
    float chance = output.getChance();
    if(chance > 0 && chance < 1) {
      int chanceInt = (int) (chance * 100);
      tooltip.add(EnumChatFormatting.GRAY + MessageFormat.format(StatCollector.translateToLocal("enderio.nei.sagmill.outputchance"), chanceInt));
    }    
  }
  
  private @Nonnull List<ItemStack> getBalls() {
    List<GrindingBall> daBalls = CrusherRecipeManager.getInstance().getBalls();
    List<ItemStack> res = new ArrayList<ItemStack>();
    res.add(null);
    for (GrindingBall ball : daBalls) {
      res.add(ball.getInput());
    }    
    return res;
  }

}
