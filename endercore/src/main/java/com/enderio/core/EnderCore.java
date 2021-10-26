package com.enderio.core;

import net.minecraftforge.fml.common.Mod;

import javax.annotation.Nonnull;
import java.util.logging.LogManager;
import java.util.logging.Logger;

@Mod(EnderCore.MODID)
public class EnderCore {
    public static final @Nonnull String MODID = "endercore";

    public static final Logger LOGGER = LogManager.getLogManager().getLogger(MODID);

    public EnderCore() {

    }
}
