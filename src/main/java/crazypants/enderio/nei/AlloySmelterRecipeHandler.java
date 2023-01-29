package crazypants.enderio.nei;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;
import net.minecraftforge.oredict.OreDictionary;

import org.lwjgl.opengl.GL11;

import codechicken.lib.gui.GuiDraw;
import codechicken.nei.ItemList;
import codechicken.nei.PositionedStack;
import codechicken.nei.recipe.TemplateRecipeHandler;

import com.enderio.core.client.render.EnderWidget;
import com.enderio.core.common.util.ItemUtil;

import crazypants.enderio.gui.IconEIO;
import crazypants.enderio.machine.alloy.AlloyRecipeManager;
import crazypants.enderio.machine.alloy.GuiAlloySmelter;
import crazypants.enderio.machine.power.PowerDisplayUtil;
import crazypants.enderio.machine.recipe.IRecipe;
import crazypants.enderio.machine.recipe.RecipeInput;

public class AlloySmelterRecipeHandler extends TemplateRecipeHandler {

    @Override
    public String getRecipeName() {
        return StatCollector.translateToLocal("enderio.nei.alloysmelter");
    }

    @Override
    public String getGuiTexture() {
        return "enderio:textures/gui/nei/alloySmelter.png";
    }

    @Override
    public Class<? extends GuiContainer> getGuiClass() {
        return GuiAlloySmelter.class;
    }

    @Override
    public String getOverlayIdentifier() {
        return "EnderIOAlloySmelter";
    }

    @Override
    public void loadTransferRects() {
        transferRects.add(
                new TemplateRecipeHandler.RecipeTransferRect(
                        new Rectangle(149, 32, 16, 16),
                        "EnderIOAlloySmelter",
                        new Object[0]));
    }

    @Override
    public void loadCraftingRecipes(ItemStack result) {

        if (result == null) {
            return;
        }

        List<IRecipe> recipes = new ArrayList<IRecipe>(AlloyRecipeManager.getInstance().getRecipes());
        recipes.addAll(AlloyRecipeManager.getInstance().getVanillaRecipe().getAllRecipes());
        for (IRecipe recipe : recipes) {
            ItemStack output = recipe.getOutputs()[0].getOutput();
            if (result.getItem() == output.getItem() && result.getItemDamage() == output.getItemDamage()) {
                AlloySmelterRecipe res = new AlloySmelterRecipe(recipe.getEnergyRequired(), recipe.getInputs(), output);
                arecipes.add(res);
            }
        }
    }

    @Override
    public void loadCraftingRecipes(String outputId, Object... results) {
        if (outputId.equals("EnderIOAlloySmelter") && getClass() == AlloySmelterRecipeHandler.class) {
            List<IRecipe> recipes = new ArrayList<IRecipe>(AlloyRecipeManager.getInstance().getRecipes());
            recipes.addAll(AlloyRecipeManager.getInstance().getVanillaRecipe().getAllRecipes());
            for (IRecipe recipe : recipes) {
                ItemStack output = recipe.getOutputs()[0].getOutput();
                AlloySmelterRecipe res = new AlloySmelterRecipe(recipe.getEnergyRequired(), recipe.getInputs(), output);
                arecipes.add(res);
            }
        } else {
            super.loadCraftingRecipes(outputId, results);
        }
    }

    @Override
    public void loadUsageRecipes(ItemStack ingredient) {
        List<IRecipe> recipes = new ArrayList<IRecipe>(AlloyRecipeManager.getInstance().getRecipes());
        recipes.addAll(AlloyRecipeManager.getInstance().getVanillaRecipe().getAllRecipes());
        for (IRecipe recipe : recipes) {
            if (recipe.isValidInput(0, ingredient)) {
                ItemStack output = recipe.getOutputs()[0].getOutput();
                AlloySmelterRecipe res = new AlloySmelterRecipe(recipe.getEnergyRequired(), recipe.getInputs(), output);
                res.setIngredientPermutation(res.input, ingredient);
                arecipes.add(res);
            }
        }
    }

    @Override
    public void drawBackground(int recipeIndex) {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GuiDraw.changeTexture(getGuiTexture());
        GuiDraw.drawTexturedModalRect(0, 0, 0, 0, 166, 65);
    }

    @Override
    public void drawExtras(int recipeIndex) {
        drawProgressBar(51, 31, 166, 0, 22, 13, 48, 3);
        drawProgressBar(99, 31, 166, 0, 22, 13, 48, 3);
        AlloySmelterRecipe recipe = (AlloySmelterRecipe) arecipes.get(recipeIndex);
        String energyString = PowerDisplayUtil.formatPower(recipe.getEnergy()) + " " + PowerDisplayUtil.abrevation();
        GuiDraw.drawString(energyString, 100, 52, 0x808080, false);

        int x = 149, y = 32;
        EnderWidget.map.render(EnderWidget.BUTTON, x, y, 16, 16, 0, true);
        IconEIO.map.render(IconEIO.RECIPE, x + 1, y + 1, 14, 14, 0, true);
    }

    public List<ItemStack> getInputs(RecipeInput input) {
        List<ItemStack> result = new ArrayList<ItemStack>();
        result.add(input.getInput());
        ItemStack[] equivs = input.getEquivelentInputs();
        if (equivs != null) {
            for (ItemStack item : equivs) {
                if (item.getItemDamage() == OreDictionary.WILDCARD_VALUE) {
                    List<ItemStack> permutations = ItemList.itemMap.get(item.getItem());
                    if (permutations.isEmpty()) {
                        ItemStack base = new ItemStack(item.getItem(), item.stackSize);
                        base.stackTagCompound = item.stackTagCompound;
                        result.add(base);
                    } else {
                        for (ItemStack stack : permutations) {
                            ItemStack perm = stack.copy();
                            perm.stackSize = item.stackSize;
                            result.add(perm);
                        }
                    }
                } else {
                    result.add(item.copy());
                }
            }
            List<ItemStack> result2 = new ArrayList<ItemStack>();
            for (ItemStack candidate : result) {
                boolean skip = false;
                for (ItemStack existing : result2) {
                    skip = skip || ItemUtil.areStacksEqual(candidate, existing);
                }
                if (!skip) {
                    System.out.println("Adding " + candidate + " for " + input);
                    result2.add(candidate);
                }
            }
            return result2;
        } else {
            return result;
        }
    }

    public class AlloySmelterRecipe extends TemplateRecipeHandler.CachedRecipe {

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

        public AlloySmelterRecipe(int energy, RecipeInput[] ingredients, ItemStack result) {
            int recipeSize = ingredients.length;
            this.input = new ArrayList<PositionedStack>();
            if (recipeSize > 0) {
                this.input.add(new PositionedStack(getInputs(ingredients[0]), 50, 13));
            }
            if (recipeSize > 1) {
                this.input.add(new PositionedStack(getInputs(ingredients[1]), 75, 3));
            }
            if (recipeSize > 2) {
                this.input.add(new PositionedStack(getInputs(ingredients[2]), 99, 13));
            }
            if (result != null) {
                this.output = new PositionedStack(result, 75, 42);
            }
            this.energy = energy;
        }
    }
}
