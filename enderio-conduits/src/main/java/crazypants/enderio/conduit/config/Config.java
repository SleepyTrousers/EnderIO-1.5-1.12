package crazypants.enderio.conduit.config;

import crazypants.enderio.base.config.Config.Section;
import crazypants.enderio.base.config.ValueFactory;
import crazypants.enderio.conduit.EnderIOConduits;

public final class Config {

  public static final Section sectionCapacitor = new Section("", "conduit");

  public static final ValueFactory F = new ValueFactory(EnderIOConduits.MODID);

  //

  public static void load() {
    // force sub-configs to be classloaded with the main config
    ConduitConfig.F.getClass();
  }
}
