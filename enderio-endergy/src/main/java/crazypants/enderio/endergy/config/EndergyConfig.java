package crazypants.enderio.endergy.config;

import com.enderio.core.common.util.NNList;

import crazypants.enderio.base.config.factory.IValue;
import crazypants.enderio.base.config.factory.IValueFactory;

public final class EndergyConfig {

  private static final int MAX = 2_000_000_000; // 0x77359400, keep some headroom to MAX_INT
  private static final int MAXIO = MAX / 2;

  public static final IValueFactory F = Config.F.section("conduit");
  public static final IValueFactory FE = F.section(".energy");

  public static final NNList<IValue<Integer>> maxIO = new NNList<>( //
      FE.make("tier1_maxIO", 40, "The maximum IO for the cobble endergy conduit.").setRange(1, MAXIO).sync(),
      FE.make("tier2_maxIO", 80, "The maximum IO for the iron endergy conduit.").setRange(1, MAXIO).sync(),
      FE.make("tier3_maxIO", 160, "The maximum IO for the gold endergy conduit.").setRange(1, MAXIO).sync(),
      FE.make("tier4_maxIO", 320, "The maximum IO for the silver endergy conduit.").setRange(1, MAXIO).sync(),
      // normal tier 1 here, 640
      FE.make("tier5_maxIO", 1280, "The maximum IO for the electrum endergy conduit.").setRange(1, MAXIO).sync(),
      FE.make("tier6_maxIO", 2560, "The maximum IO for the aluminium endergy conduit.").setRange(1, MAXIO).sync(),
      // normal tier 2 here, 5120
      FE.make("tier7_maxIO", 10240, "The maximum IO for the copper endergy conduit.").setRange(1, MAXIO).sync(),
      // normal tier 3 here, 20480
      FE.make("tierX_maxIO", 1, "The maximum IO for the <add more here, epic> endergy conduit.").setRange(1, MAXIO).sync()

  );

}
