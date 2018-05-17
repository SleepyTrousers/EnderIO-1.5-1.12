package crazypants.enderio.machines.integration.jei;

import crazypants.enderio.base.EnderIO;
import crazypants.enderio.base.lang.Lang;
import crazypants.enderio.base.recipe.sagmill.GrindingBall;
import crazypants.enderio.base.recipe.sagmill.SagMillRecipeManager;
import crazypants.enderio.machines.EnderIOMachines;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IGuiItemStackGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.BlankRecipeCategory;
import mezz.jei.api.recipe.BlankRecipeWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;

import static crazypants.enderio.machines.init.MachineObject.block_sag_mill;

public class SagMillGrindingBallCategory extends BlankRecipeCategory<SagMillGrindingBallCategory.GrindingBallWrapper> {

    public static final @Nonnull String UID = "GrindingBall";

    // ------------ Recipes

    public static class GrindingBallWrapper extends BlankRecipeWrapper {
        private final GrindingBall ball;

        public GrindingBallWrapper(GrindingBall ball) {
            this.ball = ball;
        }

        @Override
        public void getIngredients(IIngredients ingredients) {
            ingredients.setInput(ItemStack.class, ball.getInput());
            ingredients.setOutput(ItemStack.class, ball.getInput());
        }

        private String toPercent(float fl) {
            fl = fl * 100;
            int per = Math.round(fl);
            return " " + per + "%";
        }

        @Override
        public void drawInfo(Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY) {
            FontRenderer fr = minecraft.fontRenderer;

            fr.drawString(Lang.GRINDING_BALL_2.get("", toPercent(ball.getGrindingMultiplier())), 48, 4, 0x404040);
            fr.drawString(Lang.GRINDING_BALL_3.get("", toPercent(ball.getChanceMultiplier())), 48, 16, 0x404040);
            fr.drawString(Lang.GRINDING_BALL_4.get("", toPercent(ball.getPowerMultiplier())), 48, 28, 0x404040);
        }

        @Override
        public @Nonnull
        java.util.List<String> getTooltipStrings(int mouseX, int mouseY) {
            if (mouseX > 48 && mouseX < 146) {
                if (mouseY < 14) {
                    return crazypants.enderio.machines.lang.Lang.JEI_GRINDING_BALL_MAIN.getLines();
                } else if (mouseY < 26) {
                    return crazypants.enderio.machines.lang.Lang.JEI_GRINDING_BALL_BONUS.getLines();
                } else {
                    return crazypants.enderio.machines.lang.Lang.JEI_GRINDING_BALL_POWER.getLines();
                }

            }
            return super.getTooltipStrings(mouseX, mouseY);
        }
    }

    public static void register(IModRegistry registry, IGuiHelper guiHelper) {
        registry.addRecipeCategories(new SagMillGrindingBallCategory(guiHelper));
        registry.addRecipeCategoryCraftingItem(new ItemStack(block_sag_mill.getBlockNN()), SagMillGrindingBallCategory.UID);

        registry.handleRecipes(GrindingBall.class, SagMillGrindingBallCategory.GrindingBallWrapper::new, SagMillGrindingBallCategory.UID);
        registry.addRecipes(SagMillRecipeManager.getInstance().getBalls(), UID);
    }

    // ------------ Category

    @Nonnull
    private final IDrawable background;

    public SagMillGrindingBallCategory(IGuiHelper guiHelper) {
        ResourceLocation backgroundLocation = new ResourceLocation(EnderIO.DOMAIN, "textures/gui/jei.grinding_ball.png");
        background = guiHelper.createDrawable(backgroundLocation, 0, 0, 146, 40, 146, 40);
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
    public void setRecipe(IRecipeLayout recipeLayout, GrindingBallWrapper recipeWrapper, IIngredients ingredients) {
        IGuiItemStackGroup guiItemStacks = recipeLayout.getItemStacks();

        guiItemStacks.init(0, true, 16, 11);
        guiItemStacks.set(ingredients);
    }

    @Override
    public @Nonnull String getModName() {
        return EnderIOMachines.MODID;
    }

}
