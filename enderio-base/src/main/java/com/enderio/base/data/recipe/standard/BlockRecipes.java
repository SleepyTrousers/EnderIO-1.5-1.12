package com.enderio.base.data.recipe.standard;

import com.enderio.base.common.block.EIOBlocks;
import com.enderio.base.common.block.ResettingLeverBlock;
import com.enderio.base.common.item.EIOItems;
import com.tterrag.registrate.util.entry.BlockEntry;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.Consumer;

public class BlockRecipes extends RecipeProvider {
    public BlockRecipes(DataGenerator pGenerator) {
        super(pGenerator);
    }

    @Override
    protected void buildCraftingRecipes(@Nonnull Consumer<FinishedRecipe> recipeConsumer) {
        addPressurePlateRecipes(recipeConsumer);
        addLeverRecipes(recipeConsumer);
    }

    private void addPressurePlateRecipes(@Nonnull Consumer<FinishedRecipe> recipeConsumer) {
        //eio plates
        addPressurePlateRecipe(recipeConsumer, EIOBlocks.DARK_STEEL_PRESSURE_PLATE, EIOItems.DARK_STEEL_INGOT::get);
        addSilentPressurePlateRecipe(recipeConsumer, EIOBlocks.SILENT_DARK_STEEL_PRESSURE_PLATE, EIOBlocks.DARK_STEEL_PRESSURE_PLATE.get());
        addPressurePlateRecipe(recipeConsumer, EIOBlocks.SOULARIUM_PRESSURE_PLATE, EIOItems.SOULARIUM_INGOT::get);
        addSilentPressurePlateRecipe(recipeConsumer, EIOBlocks.SILENT_SOULARIUM_PRESSURE_PLATE, EIOBlocks.SOULARIUM_PRESSURE_PLATE.get());
        //wooden silent plates
        addSilentPressurePlateRecipe(recipeConsumer, EIOBlocks.SILENT_OAK_PRESSURE_PLATE, Blocks.OAK_PRESSURE_PLATE);
        addSilentPressurePlateRecipe(recipeConsumer, EIOBlocks.SILENT_ACACIA_PRESSURE_PLATE, Blocks.ACACIA_PRESSURE_PLATE);
        addSilentPressurePlateRecipe(recipeConsumer, EIOBlocks.SILENT_DARK_OAK_PRESSURE_PLATE, Blocks.DARK_OAK_PRESSURE_PLATE);
        addSilentPressurePlateRecipe(recipeConsumer, EIOBlocks.SILENT_SPRUCE_PRESSURE_PLATE, Blocks.SPRUCE_PRESSURE_PLATE);
        addSilentPressurePlateRecipe(recipeConsumer, EIOBlocks.SILENT_BIRCH_PRESSURE_PLATE, Blocks.BIRCH_PRESSURE_PLATE);
        addSilentPressurePlateRecipe(recipeConsumer, EIOBlocks.SILENT_JUNGLE_PRESSURE_PLATE, Blocks.JUNGLE_PRESSURE_PLATE);
        addSilentPressurePlateRecipe(recipeConsumer, EIOBlocks.SILENT_CRIMSON_PRESSURE_PLATE, Blocks.CRIMSON_PRESSURE_PLATE);
        addSilentPressurePlateRecipe(recipeConsumer, EIOBlocks.SILENT_WARPED_PRESSURE_PLATE, Blocks.WARPED_PRESSURE_PLATE);
        //stone silent plates
        addSilentPressurePlateRecipe(recipeConsumer, EIOBlocks.SILENT_STONE_PRESSURE_PLATE, Blocks.STONE_PRESSURE_PLATE);
        addSilentPressurePlateRecipe(recipeConsumer, EIOBlocks.SILENT_POLISHED_BLACKSTONE_PRESSURE_PLATE, Blocks.POLISHED_BLACKSTONE_PRESSURE_PLATE);
        //wighted silent plates
        addSilentPressurePlateRecipe(recipeConsumer, EIOBlocks.SILENT_HEAVY_WEIGHTED_PRESSURE_PLATE, Blocks.HEAVY_WEIGHTED_PRESSURE_PLATE);
        addSilentPressurePlateRecipe(recipeConsumer, EIOBlocks.SILENT_LIGHT_WEIGHTED_PRESSURE_PLATE, Blocks.LIGHT_WEIGHTED_PRESSURE_PLATE);
    }

    private void addPressurePlateRecipe(@Nonnull Consumer<FinishedRecipe> recipeConsumer, BlockEntry<? extends Block> result, ItemLike ingredient) {
        ShapedRecipeBuilder
            .shaped(result
                .get()
                .asItem())
            .define('#', ingredient)
            .pattern("##")
            .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(ingredient))
            .save(recipeConsumer);
    }

    private void addSilentPressurePlateRecipe(@Nonnull Consumer<FinishedRecipe> recipeConsumer, BlockEntry<? extends Block> result, ItemLike ingredient) {
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

    private void addLeverRecipes(@Nonnull Consumer<FinishedRecipe> recipeConsumer) {
        addLeverRecipe(recipeConsumer, EIOBlocks.RESETTING_LEVER_FIVE, EIOBlocks.RESETTING_LEVER_FIVE_INV, null, null, 1);
        addLeverRecipe(recipeConsumer, EIOBlocks.RESETTING_LEVER_TEN, EIOBlocks.RESETTING_LEVER_TEN_INV, EIOBlocks.RESETTING_LEVER_FIVE,
            EIOBlocks.RESETTING_LEVER_FIVE_INV, 2);
        addLeverRecipe(recipeConsumer, EIOBlocks.RESETTING_LEVER_THIRTY, EIOBlocks.RESETTING_LEVER_THIRTY_INV, EIOBlocks.RESETTING_LEVER_TEN,
            EIOBlocks.RESETTING_LEVER_TEN_INV, 3);
        addLeverRecipe(recipeConsumer, EIOBlocks.RESETTING_LEVER_SIXTY, EIOBlocks.RESETTING_LEVER_SIXTY_INV, EIOBlocks.RESETTING_LEVER_THIRTY,
            EIOBlocks.RESETTING_LEVER_THIRTY_INV, 4);
        addLeverRecipe(recipeConsumer, EIOBlocks.RESETTING_LEVER_THREE_HUNDRED, EIOBlocks.RESETTING_LEVER_THREE_HUNDRED_INV, EIOBlocks.RESETTING_LEVER_SIXTY,
            EIOBlocks.RESETTING_LEVER_SIXTY_INV, 5);
    }

    private void addLeverRecipe(Consumer<FinishedRecipe> recipeConsumer, BlockEntry<? extends ResettingLeverBlock> base,
        BlockEntry<? extends ResettingLeverBlock> inverted, @Nullable BlockEntry<? extends ResettingLeverBlock> previous,
        @Nullable BlockEntry<? extends ResettingLeverBlock> previousInverted, int numRedstone) {

        //-- base recipes

        //lever and redstone
        ShapelessRecipeBuilder recipe = ShapelessRecipeBuilder
            .shapeless(base.get())
            .requires(Blocks.LEVER)
            .requires(Blocks.REDSTONE_WIRE, numRedstone)
            .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(Blocks.LEVER));
        StandardRecipes.saveRecipe(recipe, null, recipeConsumer);

        //inverted and torch
        recipe = ShapelessRecipeBuilder
            .shapeless(base.get())
            .requires(inverted.get())
            .requires(Blocks.REDSTONE_TORCH)
            .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(Blocks.LEVER));
        StandardRecipes.saveRecipe(recipe, "_from_inv", recipeConsumer);

        //previous and redstone
        if (previous != null) {
            recipe = ShapelessRecipeBuilder
                .shapeless(base.get())
                .requires(previous.get())
                .requires(Blocks.REDSTONE_WIRE)
                .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(Blocks.LEVER));
            StandardRecipes.saveRecipe(recipe, "_from_prev", recipeConsumer);
        }

        //-- inverted recipes

        //lever, redstone and torch
        ShapelessRecipeBuilder
            .shapeless(inverted.get())
            .requires(Blocks.LEVER)
            .requires(Blocks.REDSTONE_WIRE, numRedstone)
            .requires(Blocks.REDSTONE_TORCH)
            .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(Blocks.LEVER))
            .save(recipeConsumer);

        //base and torch
        recipe = ShapelessRecipeBuilder
            .shapeless(inverted.get())
            .requires(base.get())
            .requires(Blocks.REDSTONE_TORCH)
            .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(Blocks.LEVER));
        StandardRecipes.saveRecipe(recipe, "_from_base", recipeConsumer);

        //previous and redstone
        if (previousInverted != null) {
            recipe = ShapelessRecipeBuilder
                .shapeless(inverted.get())
                .requires(previousInverted.get())
                .requires(Blocks.REDSTONE_WIRE)
                .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(Blocks.LEVER));
            StandardRecipes.saveRecipe(recipe, "_from_prev", recipeConsumer);
        }

    }

}
