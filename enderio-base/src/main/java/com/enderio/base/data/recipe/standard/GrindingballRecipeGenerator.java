package com.enderio.base.data.recipe.standard;

import java.util.function.Consumer;

import com.enderio.base.common.recipe.grindingball.GrindingballRecipe;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;

public class GrindingballRecipeGenerator extends RecipeProvider {

    public GrindingballRecipeGenerator(DataGenerator pGenerator) {
        super(pGenerator);
    }
    
    @Override
    protected void buildCraftingRecipes(Consumer<FinishedRecipe> pFinishedRecipeConsumer) {
        build(Items.FLINT, 1.2F, 1.25F, 0.85F, 24000, pFinishedRecipeConsumer);
    }

    protected void build(Item item, float grinding, float chance, float power, int durability, Consumer<FinishedRecipe> recipeConsumer) {
        build(new GrindingballRecipe(null, Ingredient.of(item), grinding, chance, power, durability), item.getRegistryName().getPath(), recipeConsumer);
    }

    protected void build(GrindingballRecipe recipe, String name, Consumer<FinishedRecipe> recipeConsumer) {
        recipeConsumer.accept(new GrindingballRecipeResult(recipe, name));
    }

}
