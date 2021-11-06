package com.enderio.base.data.recipe.standard;

import java.util.function.Consumer;

import javax.annotation.Nonnull;

import com.enderio.base.EnderIO;
import com.enderio.base.common.block.EIOBlocks;
import com.enderio.base.common.item.EIOItems;
import com.enderio.base.common.tag.EIOTags;

import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.Tag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.Tags;

public class MaterialRecipes extends RecipeProvider {
    public MaterialRecipes(DataGenerator pGenerator) {
        super(pGenerator);
    }

    @Override
    protected void buildCraftingRecipes(@Nonnull Consumer<FinishedRecipe> recipeConsumer) {
        ShapedRecipeBuilder
            .shaped(EIOBlocks.SIMPLE_MACHINE_CHASSIS.get().asItem())
            .pattern("BIB")
            .pattern("IGI")
            .pattern("BIB")
            .define('B', Items.IRON_BARS)
            .define('I', Tags.Items.INGOTS_IRON)
            .define('G', EIOItems.GRAINS_OF_INFINITY::get)
            .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(EIOItems.GRAINS_OF_INFINITY::get))
            .save(recipeConsumer);
        
        ShapedRecipeBuilder.shaped(Items.CAKE)
            .pattern("MMM")
            .pattern("SCS")
            .define('M', Items.MILK_BUCKET)
            .define('S', Items.SUGAR)
            .define('C', EIOItems.CAKE_BASE.get())
            .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(EIOItems.CAKE_BASE.get()))
            .save(recipeConsumer);
        
        ShapelessRecipeBuilder.shapeless(EIOItems.PHOTOVOLTAIC_COMPOSITE.get())
            .requires(EIOTags.Items.DUSTS_LAPIS)
            .requires(EIOTags.Items.DUSTS_COAL)
            .requires(EIOTags.Items.SILICON)
            .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(EIOItems.SILICON.get()))
            .save(recipeConsumer);
        
        ShapedRecipeBuilder.shaped(EIOItems.EMPTY_SOUL_VIAL.get())
            .pattern(" S ")
            .pattern("Q Q")
            .pattern(" Q ")
            .define('S', EIOItems.SOULARIUM_INGOT.get())
            .define('Q', EIOTags.Items.FUSED_QUARTZ)
            .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(EIOItems.SOULARIUM_INGOT.get()))
            .save(recipeConsumer);
        
        ShapedRecipeBuilder.shaped(EIOItems.UNFIRED_DEATH_URN.get())
            .pattern("CPC")
            .pattern("C C")
            .pattern("CCC")
            .define('C', Items.CLAY_BALL)
            .define('P', EIOItems.PULSATING_POWDER.get())
            .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(EIOItems.PULSATING_POWDER.get()))
            .save(recipeConsumer);
        
        blockToIngots(recipeConsumer, EIOItems.ELECTRICAL_STEEL_INGOT.get(),EIOBlocks.ELECTRICAL_STEEL_BLOCK.get());
        ingotToNuggets(recipeConsumer, EIOItems.ELECTRICAL_STEEL_NUGGET.get(), EIOItems.ELECTRICAL_STEEL_INGOT.get());
        blockToIngots(recipeConsumer, EIOItems.ENERGETIC_ALLOY_INGOT.get(),EIOBlocks.ENERGETIC_ALLOY_BLOCK.get());
        ingotToNuggets(recipeConsumer, EIOItems.ENERGETIC_ALLOY_NUGGET.get(), EIOItems.ENERGETIC_ALLOY_INGOT.get());
        blockToIngots(recipeConsumer, EIOItems.VIBRANT_ALLOY_INGOT.get(),EIOBlocks.VIBRANT_ALLOY_BLOCK.get());
        ingotToNuggets(recipeConsumer, EIOItems.VIBRANT_ALLOY_NUGGET.get(), EIOItems.VIBRANT_ALLOY_INGOT.get());
        blockToIngots(recipeConsumer, EIOItems.REDSTONE_ALLOY_INGOT.get(),EIOBlocks.REDSTONE_ALLOY_BLOCK.get());
        ingotToNuggets(recipeConsumer, EIOItems.REDSTONE_ALLOY_NUGGET.get(), EIOItems.REDSTONE_ALLOY_INGOT.get()); 
        blockToIngots(recipeConsumer, EIOItems.CONDUCTIVE_IRON_INGOT.get(),EIOBlocks.CONDUCTIVE_IRON_BLOCK.get());
        ingotToNuggets(recipeConsumer, EIOItems.CONDUCTIVE_IRON_NUGGET.get(), EIOItems.CONDUCTIVE_IRON_INGOT.get());
        blockToIngots(recipeConsumer, EIOItems.PULSATING_IRON_INGOT.get(),EIOBlocks.PULSATING_IRON_BLOCK.get());
        ingotToNuggets(recipeConsumer, EIOItems.PULSATING_IRON_NUGGET.get(), EIOItems.PULSATING_IRON_INGOT.get());
        blockToIngots(recipeConsumer, EIOItems.DARK_STEEL_INGOT.get(),EIOBlocks.DARK_STEEL_BLOCK.get());
        ingotToNuggets(recipeConsumer, EIOItems.DARK_STEEL_NUGGET.get(), EIOItems.DARK_STEEL_INGOT.get());
        blockToIngots(recipeConsumer, EIOItems.SOULARIUM_INGOT.get(),EIOBlocks.SOULARIUM_BLOCK.get());
        ingotToNuggets(recipeConsumer, EIOItems.SOULARIUM_NUGGET.get(), EIOItems.SOULARIUM_INGOT.get());
        blockToIngots(recipeConsumer, EIOItems.END_STEEL_INGOT.get(),EIOBlocks.END_STEEL_BLOCK.get());
        ingotToNuggets(recipeConsumer, EIOItems.END_STEEL_NUGGET.get(), EIOItems.END_STEEL_INGOT.get());
        blockToIngots(recipeConsumer, EIOItems.CONSTRUCTION_ALLOY_INGOT.get(),EIOBlocks.CONSTRUCTION_ALLOY_BLOCK.get());
        ingotToNuggets(recipeConsumer, EIOItems.CONSTRUCTION_ALLOY_NUGGET.get(), EIOItems.CONSTRUCTION_ALLOY_INGOT.get());
        
        ShapedRecipeBuilder.shaped(EIOItems.CONDUIT_BINDER_COMPOSITE.get())
            .pattern("GCG")
            .pattern("SGS")
            .pattern("GCG")
            .define('G', Tags.Items.GRAVEL)
            .define('S', Tags.Items.SAND)
            .define('C', Items.CLAY_BALL)
            .unlockedBy("has_ingredient_gravel", InventoryChangeTrigger.TriggerInstance.hasItems(Items.GRAVEL))
            .unlockedBy("has_ingredient_sand", InventoryChangeTrigger.TriggerInstance.hasItems(Items.SAND))
            .unlockedBy("has_ingredient_clay", InventoryChangeTrigger.TriggerInstance.hasItems(Items.CLAY_BALL))
            .save(recipeConsumer);
        
        ShapedRecipeBuilder.shaped(EIOItems.PULSATING_CRYSTAL.get())
            .pattern("PPP")
            .pattern("PDP")
            .pattern("PPP")
            .define('P', EIOItems.PULSATING_IRON_NUGGET.get())
            .define('D', Tags.Items.GEMS_DIAMOND)
            .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(EIOItems.PULSATING_IRON_NUGGET.get()))
            .save(recipeConsumer);
        
        ShapedRecipeBuilder.shaped(EIOItems.VIBRANT_CRYSTAL.get())
            .pattern("PPP")
            .pattern("PDP")
            .pattern("PPP")
            .define('P', EIOItems.VIBRANT_ALLOY_NUGGET.get())
            .define('D', Tags.Items.GEMS_EMERALD)
            .unlockedBy("has_ingredien", InventoryChangeTrigger.TriggerInstance.hasItems(EIOItems.VIBRANT_ALLOY_NUGGET.get()))
            .save(recipeConsumer);
        
        ShapedRecipeBuilder.shaped(EIOItems.DYE_INDUSTRIAL_BLEND.get())
            .pattern("LQG")
            .pattern("QBQ")
            .pattern("GQL")
            .define('L', EIOTags.Items.DUSTS_LAPIS)
            .define('Q', EIOTags.Items.DUSTS_QUARTZ)
            .define('B', EIOItems.DYE_BLACK.get())
            .define('G', EIOItems.DYE_GREEN.get())
            .unlockedBy("has_ingredient_black", InventoryChangeTrigger.TriggerInstance.hasItems(EIOItems.DYE_BLACK.get()))
            .unlockedBy("has_ingredient_green", InventoryChangeTrigger.TriggerInstance.hasItems(EIOItems.DYE_GREEN.get()))
            .unlockedBy("has_ingredient_lapis", InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().of(EIOTags.Items.DUSTS_LAPIS).build()))
            .save(recipeConsumer);
        
        ShapedRecipeBuilder.shaped(EIOItems.DYE_ENHANCED_BLEND.get())
            .pattern("PQP")
            .pattern("QBQ")
            .pattern("PQP")
            .define('Q', EIOTags.Items.DUSTS_QUARTZ)
            .define('B', EIOItems.DYE_BLACK.get())
            .define('P', EIOItems.PULSATING_POWDER.get())
            .unlockedBy("has_ingredient_black", InventoryChangeTrigger.TriggerInstance.hasItems(EIOItems.DYE_BLACK.get()))
            .unlockedBy("has_ingredient_powder", InventoryChangeTrigger.TriggerInstance.hasItems(EIOItems.PULSATING_POWDER.get()))
            .save(recipeConsumer);
        
        ShapedRecipeBuilder.shaped(EIOItems.DYE_SOUL_ATTUNED_BLEND.get())
            .pattern("SQS")
            .pattern("QBQ")
            .pattern("SQS")
            .define('Q', EIOTags.Items.DUSTS_QUARTZ)
            .define('B', EIOItems.DYE_BLACK.get())
            .define('S', EIOItems.SOUL_POWDER.get())
            .unlockedBy("has_ingredient_black", InventoryChangeTrigger.TriggerInstance.hasItems(EIOItems.DYE_BLACK.get()))
            .unlockedBy("has_ingredient_powder", InventoryChangeTrigger.TriggerInstance.hasItems(EIOItems.SOUL_POWDER.get()))
            .save(recipeConsumer);
        
        ShapedRecipeBuilder.shaped(EIOItems.GEAR_WOOD.get())
            .pattern(" S ")
            .pattern("S S")
            .pattern(" S ")
            .define('S', Tags.Items.RODS_WOODEN)
            .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().of(Tags.Items.RODS_WOODEN).build()))
            .save(recipeConsumer);
        
        ShapedRecipeBuilder.shaped(EIOItems.GEAR_WOOD.get())
            .pattern("S S")
            .pattern("   ")
            .pattern("S S")
            .define('S', Tags.Items.RODS_WOODEN)
            .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().of(Tags.Items.RODS_WOODEN).build()))
            .save(recipeConsumer, new ResourceLocation(EnderIO.MODID, EIOItems.GEAR_WOOD.get().getRegistryName().getPath() +"_corner"));
        
        ShapedRecipeBuilder.shaped(EIOItems.GEAR_STONE.get())
            .pattern("NIN")
            .pattern("I I")
            .pattern("NIN")
            .define('N', Tags.Items.RODS_WOODEN)
            .define('I', Tags.Items.COBBLESTONE)
            .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().of(Tags.Items.COBBLESTONE).build()))
            .save(recipeConsumer);
        
        ShapedRecipeBuilder.shaped(EIOItems.GEAR_STONE.get())
            .pattern(" I ")
            .pattern("IGI")
            .pattern(" I ")
            .define('I', Tags.Items.COBBLESTONE)
            .define('G', EIOItems.GEAR_WOOD.get())
            .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(EIOItems.GEAR_WOOD.get()))
            .save(recipeConsumer, new ResourceLocation(EnderIO.MODID, EIOItems.GEAR_STONE.get().getRegistryName().getPath() + "_upgrade"));
        
        upgradeGear(recipeConsumer, EIOItems.GEAR_IRON.get(), EIOItems.GRAINS_OF_INFINITY.get(), Tags.Items.INGOTS_IRON, Tags.Items.NUGGETS_IRON);
        upgradeGear(recipeConsumer, EIOItems.GEAR_ENERGIZED.get(), EIOItems.GEAR_IRON.get(), EIOItems.ENERGETIC_ALLOY_INGOT.get(), EIOItems.ENERGETIC_ALLOY_NUGGET.get());
        upgradeGear(recipeConsumer, EIOItems.GEAR_VIBRANT.get(), EIOItems.GEAR_ENERGIZED.get(), EIOItems.VIBRANT_ALLOY_INGOT.get(), EIOItems.VIBRANT_ALLOY_NUGGET.get());
        upgradeGear(recipeConsumer, EIOItems.GEAR_DARK_STEEL.get(), EIOItems.GEAR_ENERGIZED.get(), EIOItems.DARK_STEEL_INGOT.get(), EIOItems.DARK_STEEL_NUGGET.get());

        ShapedRecipeBuilder.shaped(EIOItems.WEATHER_CRYSTAL.get())
            .pattern(" P ")
            .pattern("VEV")
            .pattern(" P ")
            .define('P', EIOItems.PULSATING_CRYSTAL.get())
            .define('V', EIOItems.VIBRANT_CRYSTAL.get())
            .define('E', EIOItems.ENDER_CRYSTAL.get())
            .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(EIOItems.PULSATING_CRYSTAL.get()))
            .save(recipeConsumer);
        
        ShapelessRecipeBuilder.shapeless(EIOItems.ENDERIOS.get())
            .requires(Items.BOWL)
            .requires(Items.MILK_BUCKET)
            .requires(Items.WHEAT)
            .requires(EIOItems.ENDER_FRAGMENT.get())
            .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(Items.WHEAT))
            .save(recipeConsumer);
        
        ShapedRecipeBuilder.shaped(Items.STICK, 16)
            .pattern("W")
            .pattern("W")
            .define('W', ItemTags.LOGS)
            .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().of(ItemTags.LOGS).build()))
            .save(recipeConsumer);
        
    }

    private void blockToIngots(Consumer<FinishedRecipe> recipeConsumer, Item ingot, Block block) {
        ShapelessRecipeBuilder.shapeless(ingot, 9)
            .requires(block.asItem())
            .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(block.asItem()))
            .save(recipeConsumer);
    }
    

    private void ingotToNuggets(Consumer<FinishedRecipe> recipeConsumer, Item nugget, Item ingot) {
        ShapelessRecipeBuilder.shapeless(nugget, 9)
            .requires(ingot)
            .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(ingot))
            .save(recipeConsumer);
    }
    
    private void upgradeGear(Consumer<FinishedRecipe> recipeConsumer, Item resultGear, ItemLike inputGear, ItemLike cross, ItemLike corner) {
        ShapedRecipeBuilder.shaped(resultGear)
            .pattern("NIN")
            .pattern("IGI")
            .pattern("NIN")
            .define('N', corner)
            .define('I', cross)
            .define('G', inputGear)
            .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(inputGear))
            .save(recipeConsumer);
    }
    
    private void upgradeGear(Consumer<FinishedRecipe> recipeConsumer, Item resultGear, ItemLike inputGear, Tag<Item> cross, Tag<Item> corner) {
        ShapedRecipeBuilder.shaped(resultGear)
            .pattern("NIN")
            .pattern("IGI")
            .pattern("NIN")
            .define('N', corner)
            .define('I', cross)
            .define('G', inputGear)
            .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(inputGear))
            .save(recipeConsumer);
    }
    
}
