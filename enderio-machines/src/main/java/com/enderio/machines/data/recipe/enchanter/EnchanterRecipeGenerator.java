package com.enderio.machines.data.recipe.enchanter;

import java.util.function.Consumer;

import com.enderio.base.common.enchantment.EIOEnchantments;
import com.enderio.base.common.item.EIOItems;
import com.enderio.machines.common.recipe.EnchanterRecipe;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.Tag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;

public class EnchanterRecipeGenerator extends RecipeProvider {
    public EnchanterRecipeGenerator(DataGenerator dataGenerator) {
        super(dataGenerator);
    }
    
    @Override
    protected void buildCraftingRecipes(Consumer<FinishedRecipe> pFinishedRecipeConsumer) {
        //vanilla
        build(Enchantments.ALL_DAMAGE_PROTECTION, EIOItems.DARK_STEEL_INGOT.get(), 16, 1, pFinishedRecipeConsumer);
        build(Enchantments.FIRE_PROTECTION, Items.BLAZE_POWDER, 16, 1, pFinishedRecipeConsumer);
        build(Enchantments.FALL_PROTECTION, Items.FEATHER, 1, 1, pFinishedRecipeConsumer);
        build(Enchantments.BLAST_PROTECTION, Items.GUNPOWDER, 16, 1, pFinishedRecipeConsumer);
        build(Enchantments.PROJECTILE_PROTECTION, Items.LEATHER, 16, 1, pFinishedRecipeConsumer);//change arrow->leather?
        build(Enchantments.RESPIRATION, Items.GLASS_BOTTLE, 1, 1, pFinishedRecipeConsumer);
        build(Enchantments.AQUA_AFFINITY, Items.LILY_PAD, 1, 1, pFinishedRecipeConsumer);
        build(Enchantments.DEPTH_STRIDER, Items.PRISMARINE_SHARD, 1, 1, pFinishedRecipeConsumer);
        build(Enchantments.FROST_WALKER, Items.ICE, 16, 1, pFinishedRecipeConsumer);
        build(Enchantments.THORNS, Items.ROSE_BUSH, 4, 1, pFinishedRecipeConsumer);
        build(Enchantments.SHARPNESS, Items.QUARTZ, 12, 1, pFinishedRecipeConsumer);
        build(Enchantments.SMITE, Items.ROTTEN_FLESH, 12, 1, pFinishedRecipeConsumer);
        build(Enchantments.BANE_OF_ARTHROPODS, Items.SPIDER_EYE, 12, 1, pFinishedRecipeConsumer);
        build(Enchantments.KNOCKBACK, Items.PISTON, 1, 1, pFinishedRecipeConsumer);
        build(Enchantments.FIRE_ASPECT, Items.BLAZE_ROD, 8, 1, pFinishedRecipeConsumer);
        build(Enchantments.MOB_LOOTING, Items.SKELETON_SKULL, 1, 1, pFinishedRecipeConsumer);
        build(Enchantments.BLOCK_EFFICIENCY, Items.REDSTONE, 12, 1, pFinishedRecipeConsumer);
        build(Enchantments.SILK_TOUCH, Items.SLIME_BALL, 1, 1, pFinishedRecipeConsumer);
        build(Enchantments.UNBREAKING, Items.OBSIDIAN, 1, 1, pFinishedRecipeConsumer);
        //build(Enchantments.MENDING, EIOItems.xp, 12, 1, pFinishedRecipeConsumer); //TODO "enderio:item_xp_transfer"
        build(Enchantments.BLOCK_FORTUNE, Items.EMERALD, 1, 1, pFinishedRecipeConsumer);
        build(Enchantments.POWER_ARROWS, Items.FLINT, 12, 1, pFinishedRecipeConsumer);
        build(Enchantments.PUNCH_ARROWS, Items.STRING, 1, 1, pFinishedRecipeConsumer);
        build(Enchantments.FLAMING_ARROWS, Items.NETHERRACK, 1, 1, pFinishedRecipeConsumer);
        build(Enchantments.INFINITY_ARROWS, EIOItems.GRAINS_OF_INFINITY.get(), 1, 1, pFinishedRecipeConsumer);
        build(Enchantments.FISHING_LUCK, Items.LAPIS_LAZULI, 1, 1, pFinishedRecipeConsumer);
        build(Enchantments.FISHING_SPEED, ItemTags.FISHES, 1, 1, pFinishedRecipeConsumer);
        build(Enchantments.SWEEPING_EDGE, Items.IRON_INGOT, 8, 1, pFinishedRecipeConsumer);
        //new
        build(Enchantments.CHANNELING, Items.LIGHTNING_ROD, 1, 1, pFinishedRecipeConsumer);
        build(Enchantments.IMPALING, Items.IRON_INGOT, 8, 1, pFinishedRecipeConsumer);//TODO
        build(Enchantments.LOYALTY, Items.LEAD, 1, 1, pFinishedRecipeConsumer);
        build(Enchantments.MULTISHOT, Items.ARROW, 16, 1, pFinishedRecipeConsumer);//TODO
        build(Enchantments.PIERCING, Items.IRON_INGOT, 8, 1, pFinishedRecipeConsumer);//TODO
        build(Enchantments.QUICK_CHARGE, Items.SUGAR, 16, 1, pFinishedRecipeConsumer);
        build(Enchantments.RIPTIDE, Items.IRON_INGOT, 8, 1, pFinishedRecipeConsumer);//TODO
        build(Enchantments.SOUL_SPEED, Items.SOUL_SOIL, 16, 1, pFinishedRecipeConsumer);

        
        //enderio
        build(EIOEnchantments.SOULBOUND.get(), EIOItems.ENDER_CRYSTAL.get(), 1, 1, pFinishedRecipeConsumer);
        //build(EIOEnchantments.WITHER_ARROW.get(), witherpotion, 1, 1, pFinishedRecipeConsumer); //TODO Potion:"enderio:withering"
        build(EIOEnchantments.WITHER_WEAPON.get(), EIOItems.WITHERING_POWDER.get(), 4, 1, pFinishedRecipeConsumer);
        build(EIOEnchantments.REPELLENT.get(), Items.ENDER_PEARL, 4, 2, pFinishedRecipeConsumer);
    }
    
    protected void build(Enchantment enchantment, Item ingredient, int amountPerLevel, int levelModifier, Consumer<FinishedRecipe> recipeConsumer) {
        build(new EnchanterRecipe(null, Ingredient.of(ingredient), enchantment, amountPerLevel, levelModifier), enchantment.getRegistryName().getPath(), recipeConsumer);
    }
    
    protected void build(Enchantment enchantment, Tag<Item> ingredient, int amountPerLevel, int levelModifier, Consumer<FinishedRecipe> recipeConsumer) {
        build(new EnchanterRecipe(null, Ingredient.of(ingredient), enchantment, amountPerLevel, levelModifier), enchantment.getRegistryName().getPath(), recipeConsumer);
    }
    
    protected void build(EnchanterRecipe recipe, String name, Consumer<FinishedRecipe> recipeConsumer) {
        recipeConsumer.accept(new EnchanterRecipeResult(recipe, name));
    }
}
