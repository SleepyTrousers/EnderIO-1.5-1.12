package com.enderio.base.data.recipe.standard;

import java.util.function.Consumer;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.resources.ResourceLocation;

public class StandardRecipes {

    public static void generate(DataGenerator dataGenerator) {
        dataGenerator.addProvider(new MaterialRecipes(dataGenerator));
        dataGenerator.addProvider(new BlockRecipes(dataGenerator));
        dataGenerator.addProvider(new CapacitorDataRecipeGenerator(dataGenerator));
    }

    public static void saveRecipe(RecipeBuilder recipe, String variant, Consumer<FinishedRecipe> recipeConsumer) {
        ResourceLocation defaultLoc = recipe.getResult().getRegistryName();
        if(variant == null) {
            recipe.save(recipeConsumer);
        } else {
            recipe.save(recipeConsumer, new ResourceLocation(defaultLoc.getNamespace(), defaultLoc.getPath() + variant));
        }
    }
}
