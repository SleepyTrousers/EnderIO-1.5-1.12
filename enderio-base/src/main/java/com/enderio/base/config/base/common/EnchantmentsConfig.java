package com.enderio.base.config.base.common;

import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraftforge.common.ForgeConfigSpec;

public class EnchantmentsConfig {
    public final ForgeConfigSpec.ConfigValue<Enchantment.Rarity> AUTO_SMELT_RARITY;
    public final ForgeConfigSpec.ConfigValue<Integer> AUTO_SMELT_MAX_COST;
    public final ForgeConfigSpec.ConfigValue<Integer> AUTO_SMELT_MIN_COST;

    public final ForgeConfigSpec.ConfigValue<Enchantment.Rarity> REPELLENT_RARITY;
    public final ForgeConfigSpec.ConfigValue<Integer> REPELLENT_MAX_LEVEL;
    public final ForgeConfigSpec.ConfigValue<Integer> REPELLENT_MAX_COST_BASE;
    public final ForgeConfigSpec.ConfigValue<Integer> REPELLENT_MAX_COST_MULT;
    public final ForgeConfigSpec.ConfigValue<Integer> REPELLENT_MIN_COST_BASE;
    public final ForgeConfigSpec.ConfigValue<Integer> REPELLENT_MIN_COST_MULT;
    public final ForgeConfigSpec.ConfigValue<Float> REPELLENT_CHANCE_BASE;
    public final ForgeConfigSpec.ConfigValue<Float> REPELLENT_CHANCE_MULT;
    public final ForgeConfigSpec.ConfigValue<Double> REPELLENT_RANGE_BASE;
    public final ForgeConfigSpec.ConfigValue<Double> REPELLENT_RANGE_MULT;
    public final ForgeConfigSpec.ConfigValue<Float> REPELLENT_NON_PLAYER_CHANCE;

    public final ForgeConfigSpec.ConfigValue<Enchantment.Rarity> SHIMMER_RARITY;
    public final ForgeConfigSpec.ConfigValue<Integer> SHIMMER_MAX_COST;
    public final ForgeConfigSpec.ConfigValue<Integer> SHIMMER_MIN_COST;

    public final ForgeConfigSpec.ConfigValue<Enchantment.Rarity> SOUL_BOUND_RARITY;
    public final ForgeConfigSpec.ConfigValue<Integer> SOUL_BOUND_MAX_COST;
    public final ForgeConfigSpec.ConfigValue<Integer> SOUL_BOUND_MIN_COST;

    public final ForgeConfigSpec.ConfigValue<Enchantment.Rarity> WITHER_ARROW_RARITY;
    public final ForgeConfigSpec.ConfigValue<Integer> WITHER_ARROW_MAX_COST;
    public final ForgeConfigSpec.ConfigValue<Integer> WITHER_ARROW_MIN_COST;

    public final ForgeConfigSpec.ConfigValue<Enchantment.Rarity> WITHER_WEAPON_RARITY;
    public final ForgeConfigSpec.ConfigValue<Integer> WITHER_WEAPON_MAX_COST;
    public final ForgeConfigSpec.ConfigValue<Integer> WITHER_WEAPON_MIN_COST;

    public final ForgeConfigSpec.ConfigValue<Enchantment.Rarity> XP_BOOST_RARITY;
    public final ForgeConfigSpec.ConfigValue<Integer> XP_BOOST_MAX_COST_BASE;
    public final ForgeConfigSpec.ConfigValue<Integer> XP_BOOST_MAX_COST_MULT;
    public final ForgeConfigSpec.ConfigValue<Integer> XP_BOOST_MIN_COST_BASE;
    public final ForgeConfigSpec.ConfigValue<Integer> XP_BOOST_MIN_COST_MULT;

    public EnchantmentsConfig(ForgeConfigSpec.Builder builder) {
        builder.push("enchantments");

        builder.push("autoSmelt");
        AUTO_SMELT_RARITY = builder.define("rarity", Enchantment.Rarity.RARE);
        AUTO_SMELT_MAX_COST = builder.define("maxCost", 60);
        AUTO_SMELT_MIN_COST = builder.define("minCost", 15);
        builder.pop();

        builder.push("repellent");
        REPELLENT_RARITY = builder.define("rarity", Enchantment.Rarity.VERY_RARE);
        REPELLENT_MAX_LEVEL = builder.define("maxLevel", 4);
        REPELLENT_MAX_COST_BASE = builder.define("maxCostBase", 10);
        REPELLENT_MAX_COST_MULT = builder.define("maxCostPerLevel", 10);
        REPELLENT_MIN_COST_BASE = builder.define("minCostBase", 10);
        REPELLENT_MIN_COST_MULT = builder.define("minCostPerLevel", 5);
        REPELLENT_CHANCE_BASE = builder.define("chanceBase", 0.35f);
        REPELLENT_CHANCE_MULT = builder.define("chancePerLevel", 0.1f);
        REPELLENT_RANGE_BASE = builder.define("rangeBase", 8d);
        REPELLENT_RANGE_MULT = builder.define("rangePerLevel", 8d);
        REPELLENT_NON_PLAYER_CHANCE = builder.define("nonPlayerChance", 0.75f);
        builder.pop();

        builder.push("shimmer");
        SHIMMER_RARITY = builder.define("rarity", Enchantment.Rarity.VERY_RARE);
        SHIMMER_MAX_COST = builder.define("maxCost", 100);
        SHIMMER_MIN_COST = builder.define("minCost", 1);
        builder.pop();

        builder.push("soulBound");
        SOUL_BOUND_RARITY = builder.define("rarity", Enchantment.Rarity.VERY_RARE);
        SOUL_BOUND_MAX_COST = builder.define("maxCost", 60);
        SOUL_BOUND_MIN_COST = builder.define("minCost", 16);
        builder.pop();

        builder.push("witherArrow");
        WITHER_ARROW_RARITY = builder.define("rarity", Enchantment.Rarity.UNCOMMON);
        WITHER_ARROW_MAX_COST = builder.define("maxCost", 50);
        WITHER_ARROW_MIN_COST = builder.define("minCost", 20);
        builder.pop();

        builder.push("witherWeapon");
        WITHER_WEAPON_RARITY = builder.define("rarity", Enchantment.Rarity.UNCOMMON);
        WITHER_WEAPON_MAX_COST = builder.define("maxCost", 100);
        WITHER_WEAPON_MIN_COST = builder.define("minCost", 1);
        builder.pop();

        builder.push("xpBoost");
        // TODO: Defaults need work because the original code for XPboost was odd.
        XP_BOOST_RARITY = builder.define("rarity", Enchantment.Rarity.COMMON);
        XP_BOOST_MAX_COST_BASE = builder.define("maxCostBase", 30);
        XP_BOOST_MAX_COST_MULT = builder.define("maxCostPerLevel", 10);
        XP_BOOST_MIN_COST_BASE = builder.define("minCostBase", 1);
        XP_BOOST_MIN_COST_MULT = builder.define("minCostPerLevel", 10);
        builder.pop();

        builder.pop();
    }
}
