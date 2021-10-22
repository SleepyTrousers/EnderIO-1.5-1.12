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
import net.minecraftforge.common.Tags;

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
        addMetalBlockRecipes(recipeConsumer);
        addChassisRecipes(recipeConsumer);
        addConstructionBlockRecipes(recipeConsumer);
    }

    private void addConstructionBlockRecipes(Consumer<FinishedRecipe> recipeConsumer) {

        ShapedRecipeBuilder
            .shaped(EIOBlocks.DARK_STEEL_LADDER
                .get(), 12)
            .define('I', EIOItems.DARK_STEEL_INGOT.get())
            .pattern(" I ")
            .pattern(" I ")
            .pattern(" I ")
            .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(EIOItems.DARK_STEEL_INGOT.get()))
            .save(recipeConsumer);

        ShapedRecipeBuilder
            .shaped(EIOBlocks.DARK_STEEL_BARS
                .get(), 16)
            .define('I', EIOItems.DARK_STEEL_INGOT.get())
            .pattern("III")
            .pattern("III")
            .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(EIOItems.DARK_STEEL_INGOT.get()))
            .save(recipeConsumer);

        ShapedRecipeBuilder
            .shaped(EIOBlocks.DARK_STEEL_TRAPDOOR
                .get(), 1)
            .define('I', EIOItems.DARK_STEEL_INGOT.get())
            .pattern("II")
            .pattern("II")
            .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(EIOItems.DARK_STEEL_INGOT.get()))
            .save(recipeConsumer);

        ShapedRecipeBuilder
            .shaped(EIOBlocks.DARK_STEEL_DOOR
                .get(), 3)
            .define('I', EIOItems.DARK_STEEL_INGOT.get())
            .pattern("II")
            .pattern("II")
            .pattern("II")
            .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(EIOItems.DARK_STEEL_INGOT.get()))
            .save(recipeConsumer);


        ShapedRecipeBuilder
            .shaped(EIOBlocks.END_STEEL_BARS
                .get(), 12)
            .define('I', EIOItems.END_STEEL_INGOT.get())
            .pattern("III")
            .pattern("III")
            .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(EIOItems.END_STEEL_INGOT.get()))
            .save(recipeConsumer);

        ShapedRecipeBuilder
            .shaped(EIOBlocks.REINFORCED_OBSIDIAN
                .get())
            .define('B', EIOBlocks.DARK_STEEL_BARS.get())
            .define('G', EIOItems.GRAINS_OF_INFINITY.get())
            .define('O', Blocks.OBSIDIAN)
            .pattern("GBG")
            .pattern("BOB")
            .pattern("GBG")
            .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(EIOItems.GRAINS_OF_INFINITY.get()))
            .save(recipeConsumer);
    }

    private void addChassisRecipes(Consumer<FinishedRecipe> recipeConsumer) {

        ShapedRecipeBuilder
            .shaped(EIOBlocks.SIMPLE_MACHINE_CHASSIS
                .get())
            .define('B', Blocks.IRON_BARS)
            .define('G', EIOItems.GRAINS_OF_INFINITY.get())
            .define('I', Tags.Items.INGOTS_IRON)
            .pattern("BIB")
            .pattern("IGI")
            .pattern("BIB")
            .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(EIOItems.GRAINS_OF_INFINITY.get()))
            .save(recipeConsumer);

        ShapedRecipeBuilder
            .shaped(EIOBlocks.END_STEEL_MACHINE_CHASSIS
                .get())
            .define('B', EIOBlocks.END_STEEL_BARS.get())
            .define('G', EIOItems.GRAINS_OF_INFINITY.get())
            .define('I', EIOItems.END_STEEL_INGOT.get())
            .pattern("BIB")
            .pattern("IGI")
            .pattern("BIB")
            .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(EIOItems.END_STEEL_INGOT.get()))
            .save(recipeConsumer);

    }

    private void addMetalBlockRecipes(Consumer<FinishedRecipe> recipeConsumer) {
        addMetalBlockRecipe(recipeConsumer, EIOBlocks.ELECTRICAL_STEEL_BLOCK, EIOItems.ELECTRICAL_STEEL_INGOT.get());
        addMetalBlockRecipe(recipeConsumer, EIOBlocks.ENERGETIC_ALLOY_BLOCK, EIOItems.ENERGETIC_ALLOY_INGOT.get());
        addMetalBlockRecipe(recipeConsumer, EIOBlocks.VIBRANT_ALLOY_BLOCK, EIOItems.VIBRANT_ALLOY_INGOT.get());
        addMetalBlockRecipe(recipeConsumer, EIOBlocks.REDSTONE_ALLOY_BLOCK, EIOItems.REDSTONE_ALLOY_INGOT.get());
        addMetalBlockRecipe(recipeConsumer, EIOBlocks.CONDUCTIVE_IRON_BLOCK, EIOItems.CONDUCTIVE_IRON_INGOT.get());
        addMetalBlockRecipe(recipeConsumer, EIOBlocks.PULSATING_IRON_BLOCK, EIOItems.PULSATING_IRON_INGOT.get());
        addMetalBlockRecipe(recipeConsumer, EIOBlocks.DARK_STEEL_BLOCK, EIOItems.DARK_STEEL_INGOT.get());
        addMetalBlockRecipe(recipeConsumer, EIOBlocks.SOULARIUM_BLOCK, EIOItems.SOULARIUM_INGOT.get());
        addMetalBlockRecipe(recipeConsumer, EIOBlocks.END_STEEL_BLOCK, EIOItems.END_STEEL_INGOT.get());
        addMetalBlockRecipe(recipeConsumer, EIOBlocks.CONSTRUCTION_ALLOY_BLOCK, EIOItems.CONSTRUCTION_ALLOY_INGOT.get());
    }

    private void addMetalBlockRecipe(@Nonnull Consumer<FinishedRecipe> recipeConsumer, BlockEntry<? extends Block> result, ItemLike ingredient) {
        ShapedRecipeBuilder
            .shaped(result
                .get())
            .define('#', ingredient)
            .pattern("###")
            .pattern("###")
            .pattern("###")
            .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(ingredient))
            .save(recipeConsumer);
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
