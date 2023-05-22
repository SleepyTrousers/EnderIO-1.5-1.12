package crazypants.enderio.nei;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;

import org.lwjgl.opengl.GL11;

import com.enderio.core.client.render.EnderWidget;

import codechicken.lib.gui.GuiDraw;
import codechicken.nei.PositionedStack;
import codechicken.nei.recipe.TemplateRecipeHandler;
import crazypants.enderio.gui.GuiContainerBaseEIO;
import crazypants.enderio.gui.IconEIO;
import crazypants.enderio.machine.power.PowerDisplayUtil;
import crazypants.enderio.machine.recipe.IRecipe;
import crazypants.enderio.machine.recipe.RecipeInput;
import crazypants.enderio.machine.slicensplice.ContainerSliceAndSplice;
import crazypants.enderio.machine.slicensplice.GuiSliceAndSplice;
import crazypants.enderio.machine.slicensplice.SliceAndSpliceRecipeManager;

public class SliceAndSpliceRecipeHandler extends TemplateRecipeHandler {

    @Override
    public String getRecipeName() {
        return StatCollector.translateToLocal("enderio.nei.slicensplice");
    }

    @Override
    public String getGuiTexture() {
        return GuiContainerBaseEIO.getGuiTexture("sliceAndSplice").toString();
    }

    @Override
    public Class<? extends GuiContainer> getGuiClass() {
        return GuiSliceAndSplice.class;
    }

    @Override
    public String getOverlayIdentifier() {
        return "EnderIOSliceAndSplice";
    }

    @Override
    public void loadTransferRects() {
        transferRects.add(
                new TemplateRecipeHandler.RecipeTransferRect(
                        new Rectangle(149, 32, 16, 16),
                        "EnderIOSliceAndSplice",
                        new Object[0]));
    }

    @Override
    public void loadCraftingRecipes(ItemStack result) {

        if (result == null) {
            return;
        }

        List<IRecipe> recipes = new ArrayList<IRecipe>(SliceAndSpliceRecipeManager.getInstance().getRecipes());
        for (IRecipe recipe : recipes) {
            ItemStack output = recipe.getOutputs()[0].getOutput();
            if (result.getItem() == output.getItem() && result.getItemDamage() == output.getItemDamage()) {
                SliceAndSpliceRecipe res = new SliceAndSpliceRecipe(recipe);
                arecipes.add(res);
            }
        }
    }

    @Override
    public void loadCraftingRecipes(String outputId, Object... results) {
        if (outputId.equals("EnderIOSliceAndSplice") && getClass() == SliceAndSpliceRecipeHandler.class) {
            List<IRecipe> recipes = new ArrayList<IRecipe>(SliceAndSpliceRecipeManager.getInstance().getRecipes());
            for (IRecipe recipe : recipes) {
                SliceAndSpliceRecipe res = new SliceAndSpliceRecipe(recipe);
                arecipes.add(res);
            }
        } else {
            super.loadCraftingRecipes(outputId, results);
        }
    }

    @Override
    public void loadUsageRecipes(ItemStack ingredient) {
        List<IRecipe> recipes = new ArrayList<IRecipe>(SliceAndSpliceRecipeManager.getInstance().getRecipes());
        for (IRecipe recipe : recipes) {
            if (recipe.isValidInput(0, ingredient)) {
                SliceAndSpliceRecipe res = new SliceAndSpliceRecipe(recipe);
                res.setIngredientPermutation(res.input, ingredient);
                arecipes.add(res);
            }
        }
    }

    @Override
    public void drawBackground(int recipeIndex) {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GuiDraw.changeTexture(getGuiTexture());
        GuiDraw.drawTexturedModalRect(16, 0, 29, 11, 142, 65);
    }

    @Override
    public void drawExtras(int recipeIndex) {
        drawProgressBar(91, 38, 177, 14, 23, 17, 120, 0);

        SliceAndSpliceRecipe recipe = (SliceAndSpliceRecipe) arecipes.get(recipeIndex);
        String energyString = PowerDisplayUtil.formatPower(recipe.getEnergy()) + " " + PowerDisplayUtil.abrevation();
        GuiDraw.drawString(energyString, 100, 57, 0x808080, false);

        int x = 149, y = 32;
        EnderWidget.map.render(EnderWidget.BUTTON, x, y, 16, 16, 0, true);
        IconEIO.map.render(IconEIO.RECIPE, x + 1, y + 1, 14, 14, 0, true);
    }

    public List<ItemStack> getInputs(RecipeInput input) {
        List<ItemStack> result = new ArrayList<ItemStack>();
        result.add(input.getInput());
        ItemStack[] equivs = input.getEquivelentInputs();
        if (equivs != null) {
            for (ItemStack st : equivs) {
                result.add(st);
            }
        }
        return result;
    }

    public class SliceAndSpliceRecipe extends TemplateRecipeHandler.CachedRecipe {

        private ArrayList<PositionedStack> input;
        private PositionedStack output;
        private int energy;

        public int getEnergy() {
            return energy;
        }

        @Override
        public List<PositionedStack> getIngredients() {
            return getCycledIngredients(cycleticks / 20, input);
        }

        @Override
        public PositionedStack getResult() {
            return output;
        }

        public SliceAndSpliceRecipe(IRecipe recipe) {
            input = new ArrayList<PositionedStack>();
            int yOff = 11;
            int xOff = 13;

            Point pos;
            for (RecipeInput recipeInput : recipe.getInputs()) {
                if (recipeInput != null) {
                    pos = ContainerSliceAndSplice.INPUT_SLOTS[recipeInput.getSlotNumber()];
                    input.add(new PositionedStack(getInputs(recipeInput), pos.x - xOff, pos.y - yOff));
                }
            }
            pos = ContainerSliceAndSplice.INPUT_SLOTS[6];
            input.add(new PositionedStack(new ItemStack(Items.iron_axe), pos.x - xOff, pos.y - yOff));
            pos = ContainerSliceAndSplice.INPUT_SLOTS[7];
            input.add(new PositionedStack(new ItemStack(Items.shears), pos.x - xOff, pos.y - yOff));

            if (recipe.getOutputs()[0] != null) {
                output = new PositionedStack(
                        recipe.getOutputs()[0].getOutput(),
                        ContainerSliceAndSplice.OUTPUT_SLOT.x - xOff,
                        ContainerSliceAndSplice.OUTPUT_SLOT.y - yOff);
            }
            energy = recipe.getEnergyRequired();
        }
    }
}
