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
        //eio plates
        addPressurePlateRecipe(recipeConsumer, EIOBlocks.DARK_STEEL_PRESSURE_PLATE, EIOItems.DARK_STEEL_INGOT::get);
        addSilentPressurePlateRecipe(recipeConsumer, EIOBlocks.SILENT_DARK_STEEL_PRESSURE_PLATE, EIOBlocks.DARK_STEEL_PRESSURE_PLATE.get());
        addPressurePlateRecipe(recipeConsumer, EIOBlocks.SOULARIUM_PRESSURE_PLATE, EIOItems.SOULARIUM_INGOT::get);
        addSilentPressurePlateRecipe(recipeConsumer, EIOBlocks.SILENT_SOULARIUM_PRESSURE_PLATE, EIOBlocks.SOULARIUM_PRESSURE_PLATE.get());
        //wooden silent plates
        addSilentPressurePlateRecipe(recipeConsumer, EIOBlocks.SILENT_OAK_PRESSURE_PLATE, Blocks.OAK_PRESSURE_PLATE);
        addSilentPressurePlateRecipe(recipeConsumer,  EIOBlocks.SILENT_ACACIA_PRESSURE_PLATE, Blocks.ACACIA_PRESSURE_PLATE);
        addSilentPressurePlateRecipe(recipeConsumer,  EIOBlocks.SILENT_DARK_OAK_PRESSURE_PLATE , Blocks.DARK_OAK_PRESSURE_PLATE);
        addSilentPressurePlateRecipe(recipeConsumer,  EIOBlocks.SILENT_SPRUCE_PRESSURE_PLATE, Blocks.SPRUCE_PRESSURE_PLATE);
        addSilentPressurePlateRecipe(recipeConsumer,  EIOBlocks.SILENT_BIRCH_PRESSURE_PLATE, Blocks.BIRCH_PRESSURE_PLATE);
        addSilentPressurePlateRecipe(recipeConsumer,  EIOBlocks.SILENT_JUNGLE_PRESSURE_PLATE, Blocks.JUNGLE_PRESSURE_PLATE);
        addSilentPressurePlateRecipe(recipeConsumer,  EIOBlocks.SILENT_CRIMSON_PRESSURE_PLATE , Blocks.CRIMSON_PRESSURE_PLATE);
        addSilentPressurePlateRecipe(recipeConsumer,  EIOBlocks.SILENT_WARPED_PRESSURE_PLATE, Blocks.WARPED_PRESSURE_PLATE);
        //stone silent plates
        addSilentPressurePlateRecipe(recipeConsumer, EIOBlocks.SILENT_STONE_PRESSURE_PLATE, Blocks.STONE_PRESSURE_PLATE);
        addSilentPressurePlateRecipe(recipeConsumer, EIOBlocks.SILENT_POLISHED_BLACKSTONE_PRESSURE_PLATE,Blocks.POLISHED_BLACKSTONE_PRESSURE_PLATE);
        //wighted silent plates
        addSilentPressurePlateRecipe(recipeConsumer, EIOBlocks.SILENT_HEAVY_WEIGHTED_PRESSURE_PLATE, Blocks.HEAVY_WEIGHTED_PRESSURE_PLATE);
        addSilentPressurePlateRecipe(recipeConsumer, EIOBlocks.SILENT_LIGHT_WEIGHTED_PRESSURE_PLATE, Blocks.LIGHT_WEIGHTED_PRESSURE_PLATE);
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
