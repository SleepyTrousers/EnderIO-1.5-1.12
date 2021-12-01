package com.enderio.base.config.base.common;

import net.minecraftforge.common.ForgeConfigSpec;

public class ItemsConfig {
    public final ForgeConfigSpec.ConfigValue<Float> ENDERIOS_CHANCE;
    public final ForgeConfigSpec.ConfigValue<Float> ENDERIOS_RANGE;

    public final ForgeConfigSpec.ConfigValue<Integer> ELECTROMAGNET_ENERGY_USE;
    public final ForgeConfigSpec.ConfigValue<Integer> ELECTROMAGNET_MAX_ENERGY;
    public final ForgeConfigSpec.ConfigValue<Integer> ELECTROMAGNET_RANGE;
    public final ForgeConfigSpec.ConfigValue<Integer> ELECTROMAGNET_MAX_ITEMS;

    public final ForgeConfigSpec.ConfigValue<Integer> LEVITATION_STAFF_ENERGY_USE;
    public final ForgeConfigSpec.ConfigValue<Integer> LEVITATION_STAFF_MAX_ENERGY;

    public ItemsConfig(ForgeConfigSpec.Builder builder) {
        builder.push("items");

        builder.push("food");
        ENDERIOS_CHANCE = builder.comment("The chance of enderios teleporting the player").define("enderioChance", 0.3f);
        ENDERIOS_RANGE = builder.comment("The range of an enderio teleport").define("enderioRange", 16.0f);
        builder.pop();

        builder.push("electromagnet");
        ELECTROMAGNET_ENERGY_USE = builder.define("energyUse", 1);
        ELECTROMAGNET_MAX_ENERGY = builder.define("maxEnergy", 100000);
        ELECTROMAGNET_RANGE = builder.define("range", 5);
        ELECTROMAGNET_MAX_ITEMS = builder.define("maxItems", 20);
        builder.pop();

        builder.push("levitationstaff");
        LEVITATION_STAFF_ENERGY_USE = builder.define("energyUse", 1);
        LEVITATION_STAFF_MAX_ENERGY = builder.define("maxEnergy", 1000);
        builder.pop();

        builder.pop();
    }
}
