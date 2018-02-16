package crazypants.enderio.machines.config.config;

import crazypants.enderio.base.config.Config.Section;
import crazypants.enderio.base.config.SectionedValueFactory;
import crazypants.enderio.base.config.ValueFactory.IValue;
import crazypants.enderio.machines.config.Config;

public final class InhibitorConfig {

  public static final SectionedValueFactory F = new SectionedValueFactory(Config.F, new Section("", "inhibitor"));

  public static final IValue<Boolean> stopAllSlimes = F.make("stopAllSlimes", false, //
      "When true, slimes wont be allowed to spawn at all. Only added to aid testing in super flat worlds.").sync();
  public static final IValue<Boolean> stopAllSquid = F.make("stopAllSquid", false, //
      "When true, squid wont be allowed to spawn at all. Only added to aid testing in super flat worlds.").sync();

}
