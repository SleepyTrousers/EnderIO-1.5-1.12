package com.enderio.machines.data.recipe.enchanter;

import java.util.function.Consumer;

import com.enderio.base.common.item.EIOItems;
import com.enderio.machines.common.recipe.EnchanterRecipe;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;

public class EnchanterRecipeGenerator extends RecipeProvider {
    public EnchanterRecipeGenerator(DataGenerator dataGenerator) {
        super(dataGenerator);
    }
    
    @Override
    protected void buildCraftingRecipes(Consumer<FinishedRecipe> pFinishedRecipeConsumer) {
        build(Enchantments.ALL_DAMAGE_PROTECTION, EIOItems.DARK_STEEL_INGOT.get(), 16, 1, pFinishedRecipeConsumer);
    }
    
    protected void build(Enchantment enchantment, Item ingredient, int amountPerLevel, int levelModifier, Consumer<FinishedRecipe> recipeConsumer) {
        build(new EnchanterRecipe(null, Ingredient.of(ingredient), enchantment, amountPerLevel, levelModifier), enchantment.getRegistryName().getPath(), recipeConsumer);
    }
    
    protected void build(EnchanterRecipe recipe, String name, Consumer<FinishedRecipe> recipeConsumer) {
        recipeConsumer.accept(new EnchanterRecipeResult(recipe, name));
    }
}
