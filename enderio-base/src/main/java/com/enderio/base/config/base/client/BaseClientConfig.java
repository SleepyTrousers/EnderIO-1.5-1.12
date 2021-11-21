package com.enderio.base.config.base.client;

import net.minecraftforge.common.ForgeConfigSpec;

public class BaseClientConfig {
    public final ForgeConfigSpec.ConfigValue<Boolean> MACHINE_PARTICLES;

    public BaseClientConfig(ForgeConfigSpec.Builder builder) {
        builder.push("visual");
        MACHINE_PARTICLES = builder.comment("Enable machine particles").define("machineParticles", true);
        builder.pop();
    }
}
