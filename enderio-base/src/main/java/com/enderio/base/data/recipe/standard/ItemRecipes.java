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

public class ItemRecipes extends RecipeProvider {
    public ItemRecipes(DataGenerator pGenerator) {
        super(pGenerator);
    }

    @Override
    protected void buildCraftingRecipes(@Nonnull Consumer<FinishedRecipe> recipeConsumer) {
        addYetaWrenchRecipe(recipeConsumer);
    }


    private void addYetaWrenchRecipe(@Nonnull Consumer<FinishedRecipe> recipeConsumer) {
        ShapedRecipeBuilder
            .shaped(EIOItems.YETA_WRENCH.get())
            .define('I', EIOItems.ELECTRICAL_STEEL_INGOT.get())
            .define('G', EIOItems.GEAR_STONE.get())
            .pattern("I I")
            .pattern(" G ")
            .pattern(" I ")
            .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(EIOItems.ELECTRICAL_STEEL_INGOT.get()))
            .save(recipeConsumer);
    }

}
