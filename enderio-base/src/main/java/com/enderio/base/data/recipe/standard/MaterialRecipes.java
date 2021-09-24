package com.enderio.base.data.recipe.standard;

import com.enderio.base.EIOItems;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.world.item.Items;
import net.minecraftforge.common.Tags;

import javax.annotation.Nonnull;
import java.util.function.Consumer;

public class MaterialRecipes extends RecipeProvider {
    public MaterialRecipes(DataGenerator pGenerator) {
        super(pGenerator);
    }

    @Override
    protected void buildCraftingRecipes(@Nonnull Consumer<FinishedRecipe> recipeConsumer) {
        ShapedRecipeBuilder.shaped(EIOItems.SIMPLE_MACHINE_CHASSIS::get)
            .pattern("BIB")
            .pattern("IGI")
            .pattern("BIB")
            .define('B', Items.IRON_BARS)
            .define('I', Tags.Items.INGOTS_IRON)
            .define('G', EIOItems.GRAINS_OF_INFINITY::get)
            .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(EIOItems.GRAINS_OF_INFINITY::get))
            .save(recipeConsumer);
    }
}
