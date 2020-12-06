package gg.galaxygaming.gasconduits.common.config;

import crazypants.enderio.base.config.factory.ValueFactoryEIO;
import gg.galaxygaming.gasconduits.GasConduitsConstants;

public final class Config {

    public static final ValueFactoryEIO F = new ValueFactoryEIO(GasConduitsConstants.MOD_ID);

    static {
        // force sub-configs to be classloaded with the main config
        GasConduitConfig.F.getClass();
    }
}