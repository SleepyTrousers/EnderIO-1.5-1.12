package com.enderio.base.config.base.common;

import net.minecraftforge.common.ForgeConfigSpec;

public class BlocksConfig {
    public ForgeConfigSpec.ConfigValue<Float> BROKEN_SPAWNER_DROP_CHANCE;

    public ForgeConfigSpec.ConfigValue<Float> DARK_STEEL_LADDER_BOOST;

    public BlocksConfig(ForgeConfigSpec.Builder builder) {
        builder.push("blocks");

        builder.push("brokenSpawner");
        BROKEN_SPAWNER_DROP_CHANCE = builder.comment("The chance of a spawner dropping a broken spawner.").define("dropChance", 1.0f);
        // TODO: Tool blacklist
        builder.pop();

        DARK_STEEL_LADDER_BOOST = builder.comment("The speed boost granted by the Dark Steel ladder.").define("darkSteelLadderBoost", 0.15f);

        builder.pop();
    }
}
