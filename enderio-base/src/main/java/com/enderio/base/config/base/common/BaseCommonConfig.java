package com.enderio.base.config.base.common;

import net.minecraftforge.common.ForgeConfigSpec;

public class BaseCommonConfig {
    public final BlocksConfig BLOCKS;
    public final EnchantmentsConfig ENCHANTMENTS;
    public final ItemsConfig ITEMS;

    public BaseCommonConfig(ForgeConfigSpec.Builder builder) {
        BLOCKS = new BlocksConfig(builder);
        ENCHANTMENTS = new EnchantmentsConfig(builder);
        ITEMS = new ItemsConfig(builder);
    }
}
