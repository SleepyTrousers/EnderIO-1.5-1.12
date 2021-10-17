package com.enderio.base.data.recipe.standard;

import com.enderio.base.common.block.EIOBlocks;
import com.enderio.base.common.item.EIOItems;
import com.tterrag.registrate.util.entry.BlockEntry;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

import javax.annotation.Nonnull;
import java.util.function.Consumer;

public class BlockRecipes extends RecipeProvider {
    public BlockRecipes(DataGenerator pGenerator) {
        super(pGenerator);
    }

    @Override
    protected void buildCraftingRecipes(@Nonnull Consumer<FinishedRecipe> recipeConsumer) {
        addPressurePlateRecipe(recipeConsumer, EIOBlocks.DARK_STEEL_PRESSURE_PLATE, EIOItems.DARK_STEEL_INGOT::get);
        addSilentPressurePlateRecipe(recipeConsumer, EIOBlocks.SILENT_DARK_STEEL_PRESSURE_PLATE, EIOBlocks.DARK_STEEL_PRESSURE_PLATE.get());
        addPressurePlateRecipe(recipeConsumer, EIOBlocks.SOULARIUM_PRESSURE_PLATE, EIOItems.SOULARIUM_INGOT::get);
        addSilentPressurePlateRecipe(recipeConsumer, EIOBlocks.SILENT_SOULARIUM_PRESSURE_PLATE, EIOBlocks.SOULARIUM_PRESSURE_PLATE.get());

        addSilentPressurePlateRecipe(recipeConsumer, EIOBlocks.SILENT_OAK_PRESSURE_PLATE, Blocks.OAK_PRESSURE_PLATE);
        addSilentPressurePlateRecipe(recipeConsumer, EIOBlocks.SILENT_STONE_PRESSURE_PLATE, Blocks.STONE_PRESSURE_PLATE);
    }

    private void addPressurePlateRecipe(Consumer<FinishedRecipe> recipeConsumer, BlockEntry<? extends Block> result, ItemLike ingredient) {
        ShapedRecipeBuilder
            .shaped(result
                .get()
                .asItem())
            .define('#', ingredient)
            .pattern("##")
            .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(ingredient))
            .save(recipeConsumer);
    }

    private void addSilentPressurePlateRecipe(Consumer<FinishedRecipe> recipeConsumer, BlockEntry<? extends Block> result, ItemLike ingredient) {
        ShapedRecipeBuilder
            .shaped(result
                .get()
                .asItem())
            .define('W', ItemTags.WOOL)
            .define('P', ingredient)
            .pattern("W")
            .pattern("P")
            .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(ingredient))
            .save(recipeConsumer);
    }


}
