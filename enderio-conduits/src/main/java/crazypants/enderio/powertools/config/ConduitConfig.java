package crazypants.enderio.powertools.config;

import javax.annotation.Nonnull;

import crazypants.enderio.base.config.Config.Section;
import crazypants.enderio.base.config.SectionedValueFactory;
import crazypants.enderio.base.config.ValueFactory.IValue;

public final class ConduitConfig {

  private static final int MAX = 2_000_000_000; // 0x77359400, keep some headroom to MAX_INT
  private static final int MAXIO = MAX / 2;

  public static final SectionedValueFactory F = new SectionedValueFactory(Config.F, new Section("", "condit"));

  public static final SectionedValueFactory FE = new SectionedValueFactory(Config.F, new Section("", "condit.energy"));

  public static final @Nonnull IValue<Integer> tier1_maxIO = FE.make("tier1_maxIO", 640, //
      "The maximum IO for the tier 1 power conduit.").setRange(1, MAXIO).sync();
  public static final @Nonnull IValue<Integer> tier2_maxIO = FE.make("tier2_maxIO", 5120, //
      "The maximum IO for the tier 2 power conduit.").setRange(1, MAXIO).sync();
  public static final @Nonnull IValue<Integer> tier3_maxIO = FE.make("tier3_maxIO", 20480, //
      "The maximum IO for the tier 3 power conduit.").setRange(1, MAXIO).sync();

  public static final @Nonnull IValue<Boolean> canDifferentTiersConnect = FE.make("canDifferentTiersConnect", false, //
      "If set to false power conduits of different tiers cannot be connected. in this case a block such as a cap. bank is needed to bridge different tiered networks.");

}
