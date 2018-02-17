package crazypants.enderio.machines.config.config;

import crazypants.enderio.base.config.Config.Section;
import crazypants.enderio.base.config.SectionedValueFactory;
import crazypants.enderio.base.config.ValueFactory.IValue;
import crazypants.enderio.machines.config.Config;

public final class ExperienceConfig {

  private static final int MAX = 2_000_000_000; // 0x77359400, keep some headroom to MAX_INT
  private static final int MAXIO = MAX / 2;

  public static final SectionedValueFactory F = new SectionedValueFactory(Config.F, new Section("", "experience"));

  public static final IValue<Integer> maxIO = F.make("maxIO", 200, //
      "Millibuckets per tick that can get in or out.").setRange(1, MAXIO).sync();

}
