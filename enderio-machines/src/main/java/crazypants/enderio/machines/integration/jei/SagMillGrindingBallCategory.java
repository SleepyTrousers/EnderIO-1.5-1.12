package crazypants.enderio.machines.integration.jei;

import java.util.Collections;
import java.util.List;

import javax.annotation.Nonnull;

import crazypants.enderio.base.EnderIO;
import crazypants.enderio.base.lang.Lang;
import crazypants.enderio.base.lang.LangPower;
import crazypants.enderio.base.recipe.sagmill.GrindingBall;
import crazypants.enderio.base.recipe.sagmill.SagMillRecipeManager;
import crazypants.enderio.machines.EnderIOMachines;
import crazypants.enderio.machines.config.config.PersonalConfig;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IGuiItemStackGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.ITickTimer;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.BlankRecipeCategory;
import mezz.jei.api.recipe.BlankRecipeWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import static crazypants.enderio.machines.init.MachineObject.block_sag_mill;

public class SagMillGrindingBallCategory extends BlankRecipeCategory<SagMillGrindingBallCategory.GrindingBallWrapper> {

  public static final @Nonnull String UID = "GrindingBall";

  // ------------ Recipes

  public static class GrindingBallWrapper extends BlankRecipeWrapper {
    private final GrindingBall ball;

    public ITickTimer timer;

    public GrindingBallWrapper(GrindingBall ball) {
      this.ball = ball;
    }

    @Override
    public void getIngredients(@Nonnull IIngredients ingredients) {
      ingredients.setInput(ItemStack.class, ball.getInput());
      ingredients.setOutput(ItemStack.class, ball.getInput());
    }

    @Override
    public void drawInfo(@Nonnull Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY) {
      FontRenderer fr = minecraft.fontRenderer;

      fr.drawString(Lang.GRINDING_BALL_2.get("", LangPower.toPercent(ball.getGrindingMultiplier())), 48, 4, 0x404040);
      fr.drawString(Lang.GRINDING_BALL_3.get("", LangPower.toPercent(ball.getChanceMultiplier())), 48, 16, 0x404040);
      fr.drawString(Lang.GRINDING_BALL_4.get("", LangPower.toPercent(ball.getPowerMultiplier())), 48, 28, 0x404040);

      int dura = ball.getDurability();
      int usedDura = (timer.getValue() * 400) % dura;
      int barH = (16 * usedDura) / dura;

      int barColor = 0xFFCA2F21;

      Gui.drawRect(37, 12 + barH, 41, 28, barColor);
    }

    @Override
    public @Nonnull List<String> getTooltipStrings(int mouseX, int mouseY) {
      if (mouseX > 48 && mouseX < 146) {
        if (mouseY < 14) {
          return crazypants.enderio.machines.lang.Lang.JEI_GRINDING_BALL_MAIN.getLines();
        } else if (mouseY < 26) {
          return crazypants.enderio.machines.lang.Lang.JEI_GRINDING_BALL_BONUS.getLines();
        } else {
          return crazypants.enderio.machines.lang.Lang.JEI_GRINDING_BALL_POWER.getLines();
        }

      } else if (mouseX >= 36 && mouseX <= 41 && mouseY >= 12 && mouseY <= 29) {
        return Collections.singletonList(LangPower.RF(ball.getDurability()));
      }
      return super.getTooltipStrings(mouseX, mouseY);
    }
  }

  public static void register() {
    // Check JEI recipes are enabled
    if (!PersonalConfig.enableGrindingBallJEIRecipes.get()) {
      return;
    }

    MachinesPlugin.iModRegistry.addRecipeCategories(new SagMillGrindingBallCategory(MachinesPlugin.iGuiHelper));
    MachinesPlugin.iModRegistry.addRecipeCategoryCraftingItem(new ItemStack(block_sag_mill.getBlockNN()), SagMillGrindingBallCategory.UID);

    MachinesPlugin.iModRegistry.handleRecipes(GrindingBall.class, SagMillGrindingBallCategory.GrindingBallWrapper::new, SagMillGrindingBallCategory.UID);
    MachinesPlugin.iModRegistry.addRecipes(SagMillRecipeManager.getInstance().getBalls(), UID);
  }

  // ------------ Category

  @Nonnull
  private final IDrawable background;
  private final ITickTimer timer;

  public SagMillGrindingBallCategory(IGuiHelper guiHelper) {
    ResourceLocation backgroundLocation = new ResourceLocation(EnderIO.DOMAIN, "textures/gui/jei.grinding_ball.png");
    background = guiHelper.createDrawable(backgroundLocation, 0, 0, 146, 40, 146, 40);
    timer = guiHelper.createTickTimer(20000, 20000, false);
  }

  @Override
  public @Nonnull String getUid() {
    return UID;
  }

  @SuppressWarnings("null")
  @Override
  public @Nonnull String getTitle() {
    return Lang.GRINDING_BALL_1.get("");
  }

  @Override
  public @Nonnull IDrawable getBackground() {
    return background;
  }

  @Override
  public void setRecipe(@Nonnull IRecipeLayout recipeLayout, @Nonnull GrindingBallWrapper recipeWrapper, @Nonnull IIngredients ingredients) {
    IGuiItemStackGroup guiItemStacks = recipeLayout.getItemStacks();

    guiItemStacks.init(0, true, 16, 11);
    guiItemStacks.set(ingredients);

    recipeWrapper.timer = timer;
  }

  @Override
  public @Nonnull String getModName() {
    return EnderIOMachines.MODID;
  }

}
