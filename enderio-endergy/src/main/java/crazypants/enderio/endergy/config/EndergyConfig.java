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
      FE.make("tier1", 20, "The maximum IO for the cobble endergy conduit.").setRange(1, MAX).sync(),
      FE.make("tier2", 40, "The maximum IO for the iron endergy conduit.").setRange(1, MAX).sync(),
      FE.make("tier3", 80, "The maximum IO for the aluminium endergy conduit.").setRange(1, MAX).sync(),
      FE.make("tier4", 160, "The maximum IO for the gold endergy conduit.").setRange(1, MAX).sync(),
      FE.make("tier5", 320, "The maximum IO for the copper endergy conduit.").setRange(1, MAX).sync(),
      // normal tier1, 640, conductive iron
      FE.make("tier6", 1280, "The maximum IO for the silver endergy conduit.").setRange(1, MAX).sync(),
      FE.make("tier7", 2560, "The maximum IO for the electrum endergy conduit.").setRange(1, MAX).sync(),
      // normal tier2, 5120, energetic alloy
      FE.make("tier8", 10240, "The maximum IO for the energetic silver endergy conduit.").setRange(1, MAX).sync(),
      // normal tier3, 20480, vibrant alloy
      FE.make("tier9", 40960, "The maximum IO for the crystalline endergy conduit.").setRange(1, MAX).sync(),
      FE.make("tier10", 81920, "The maximum IO for the pink slime endergy conduit.").setRange(1, MAX).sync(),
      // missing tier, 163840
      FE.make("tier12", 327680, "The maximum IO for the melodic endergy conduit.").setRange(1, MAX).sync(),
      FE.make("tier13", MAX, "The maximum IO for the stellar endergy conduit.").setRange(1, MAX).sync()

  );

}
