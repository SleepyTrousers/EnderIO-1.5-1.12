package com.enderio.base.data.recipe.standard;

import com.enderio.base.common.block.EIOBlocks;
import com.enderio.base.common.item.EIOItems;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.Tags;

import javax.annotation.Nonnull;
import java.util.function.Consumer;

public class BlockRecipes extends RecipeProvider {
    public BlockRecipes(DataGenerator pGenerator) {
        super(pGenerator);
    }

    @Override
    protected void buildCraftingRecipes(@Nonnull Consumer<FinishedRecipe> recipeConsumer) {
        ShapedRecipeBuilder
            .shaped(EIOBlocks.DARK_STEEL_PRESSURE_PLATE
                .get()
                .asItem())
            .define('#', EIOItems.DARK_STEEL_INGOT::get)
            .pattern("##")
            .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(EIOItems.DARK_STEEL_INGOT::get))
            .save(recipeConsumer);
    }
}
