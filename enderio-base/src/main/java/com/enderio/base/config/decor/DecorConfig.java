package com.enderio.base.config.decor;

import com.enderio.base.config.decor.client.DecorClientConfig;
import com.enderio.base.config.decor.common.DecorCommonConfig;
import net.minecraftforge.common.ForgeConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

public class DecorConfig {
    public static final DecorCommonConfig COMMON;
    public static final ForgeConfigSpec COMMON_SPEC;

    public static final DecorClientConfig CLIENT;
    public static final ForgeConfigSpec CLIENT_SPEC;

    static {
        Pair<DecorCommonConfig, ForgeConfigSpec> commonSpecPair = new ForgeConfigSpec.Builder().configure(DecorCommonConfig::new);
        COMMON = commonSpecPair.getLeft();
        COMMON_SPEC = commonSpecPair.getRight();

        Pair<DecorClientConfig, ForgeConfigSpec> clientSpecPair = new ForgeConfigSpec.Builder().configure(DecorClientConfig::new);
        CLIENT = clientSpecPair.getLeft();
        CLIENT_SPEC = clientSpecPair.getRight();
    }
}
