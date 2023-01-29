package crazypants.enderio.nei;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.resources.I18n;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentData;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;

import codechicken.lib.gui.GuiDraw;
import codechicken.nei.PositionedStack;
import codechicken.nei.recipe.TemplateRecipeHandler;

import com.enderio.core.client.render.EnderWidget;

import crazypants.enderio.gui.GuiContainerBaseEIO;
import crazypants.enderio.gui.IconEIO;
import crazypants.enderio.machine.enchanter.EnchanterRecipe;
import crazypants.enderio.machine.enchanter.EnchanterRecipeManager;
import crazypants.enderio.machine.enchanter.GuiEnchanter;
import crazypants.enderio.machine.enchanter.TileEnchanter;
import crazypants.enderio.machine.recipe.RecipeInput;

public class EnchanterRecipeHandler extends TemplateRecipeHandler {

    @Override
    public String getRecipeName() {
        return StatCollector.translateToLocal("enderio.nei.enchanter");
    }

    @Override
    public String getGuiTexture() {
        return GuiContainerBaseEIO.getGuiTexture("enchanter").toString();
    }

    @Override
    public Class<? extends GuiContainer> getGuiClass() {
        return GuiEnchanter.class;
    }

    @Override
    public String getOverlayIdentifier() {
        return "EIOEnchanter";
    }

    @Override
    public void loadTransferRects() {
        transferRects.add(
                new TemplateRecipeHandler.RecipeTransferRect(
                        new Rectangle(149, -3, 16, 16),
                        "EIOEnchanter",
                        new Object[0]));
    }

    @Override
    public void loadCraftingRecipes(ItemStack result) {
        if (result == null || result.getItem() != Items.enchanted_book) {
            return;
        }
        Map<Number, Number> enchants = EnchantmentHelper.getEnchantments(result);
        List<EnchanterRecipe> recipes = EnchanterRecipeManager.getInstance().getRecipes();

        for (Number id : enchants.keySet()) {
            if (id != null && id.intValue() >= 0 && id.intValue() < Enchantment.enchantmentsList.length) {
                Enchantment ench = Enchantment.enchantmentsList[id.intValue()];
                if (ench != null && ench.getName() != null) {

                    for (EnchanterRecipe recipe : recipes) {
                        if (recipe.isValid() && recipe.getEnchantment().getName().equals(ench.getName())) {
                            EnchanterRecipeNEI rec = new EnchanterRecipeNEI(recipe);
                            arecipes.add(rec);
                        }
                    }
                }
            }
        }
    }

    @Override
    public void loadCraftingRecipes(String outputId, Object... results) {
        if (outputId.equals("EIOEnchanter") && getClass() == EnchanterRecipeHandler.class) {
            List<EnchanterRecipe> recipes = EnchanterRecipeManager.getInstance().getRecipes();
            for (EnchanterRecipe recipe : recipes) {
                if (recipe.isValid()) {
                    EnchanterRecipeNEI rec = new EnchanterRecipeNEI(recipe);
                    arecipes.add(rec);
                }
            }
        } else {
            super.loadCraftingRecipes(outputId, results);
        }
    }

    @Override
    public void loadUsageRecipes(ItemStack ingredient) {
        List<EnchanterRecipe> recipes = EnchanterRecipeManager.getInstance().getRecipes();
        for (EnchanterRecipe recipe : recipes) {
            if (recipe.isValid()) {
                EnchanterRecipeNEI rec = new EnchanterRecipeNEI(recipe);
                if (rec.contains(rec.input, ingredient)) {
                    rec.setIngredientPermutation(rec.input, ingredient);
                    arecipes.add(rec);
                }
            }
        }
    }

    @Override
    public void drawExtras(int recipeIndex) {
        EnchanterRecipeNEI recipe = (EnchanterRecipeNEI) arecipes.get(recipeIndex);

        GuiDraw.drawStringC(recipe.getEnchantName(), 83, 10, 0x808080, false);

        int level = 1;
        List<PositionedStack> ingredients = recipe.getIngredients();
        if (ingredients != null && ingredients.size() == 2) {
            ItemStack item = ingredients.get(1).item;
            level = recipe.recipe.getLevelForStackSize(item.stackSize);
        }

        int cost = TileEnchanter.getEnchantmentCost(recipe.recipe, level);
        if (cost > 0) {
            String s = I18n.format("container.repair.cost", new Object[] { cost });
            GuiDraw.drawStringC(s, 83, 46, 0x80FF20);
        }

        int x = 149, y = -3;
        EnderWidget.map.render(EnderWidget.BUTTON, x, y, 16, 16, 0, true);
        IconEIO.map.render(IconEIO.RECIPE, x + 1, y + 1, 14, 14, 0, true);
    }

    public List<ItemStack> getInputs(RecipeInput input) {
        List<ItemStack> result = new ArrayList<ItemStack>();
        result.add(input.getInput());
        ItemStack[] equivs = input.getEquivelentInputs();
        if (equivs != null && equivs.length > 0) {
            result.addAll(Arrays.asList(equivs));
        }
        return result;
    }

    public class EnchanterRecipeNEI extends TemplateRecipeHandler.CachedRecipe {

        private final ArrayList<PositionedStack> input;
        private final PositionedStack output;
        private final EnchanterRecipe recipe;

        public String getEnchantName() {
            int maxLevel = recipe.getEnchantment().getMaxLevel();
            if (maxLevel > 1) {
                int cycle = cycleticks / 20;
                int level = cycle % maxLevel + 1;
                return recipe.getEnchantment().getTranslatedName(level);
            }
            return StatCollector.translateToLocal(recipe.getEnchantment().getName());
        }

        @Override
        public List<PositionedStack> getIngredients() {
            int cycle = cycleticks / 20;
            getCycledIngredients(cycle, input);
            int maxLevel = recipe.getEnchantment().getMaxLevel();
            if (maxLevel > 1) {
                int level = cycle % maxLevel + 1;
                input.get(1).item.stackSize = recipe.getItemsPerLevel() * level;
            }
            return input;
        }

        @Override
        public PositionedStack getResult() {
            int cycle = cycleticks / 20;
            output.setPermutationToRender(cycle % output.items.length);
            return output;
        }

        public EnchanterRecipeNEI(EnchanterRecipe recipe) {
            this.recipe = recipe;
            input = new ArrayList<PositionedStack>();
            input.add(new PositionedStack(new ItemStack(Items.writable_book), 22, 24));
            input.add(new PositionedStack(getInputs(recipe.getInput()), 71, 24));

            int maxLevel = recipe.getEnchantment().getMaxLevel();
            ItemStack[] outputItems = new ItemStack[maxLevel];
            for (int level = 0; level < maxLevel; level++) {
                EnchantmentData enchantment = new EnchantmentData(recipe.getEnchantment(), level + 1);
                outputItems[level] = new ItemStack(Items.enchanted_book);
                Items.enchanted_book.addEnchantment(outputItems[level], enchantment);
            }
            output = new PositionedStack(outputItems, 129, 24);
        }
    }
}
