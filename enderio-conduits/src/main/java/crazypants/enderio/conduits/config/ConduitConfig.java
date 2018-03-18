package crazypants.enderio.conduits.config;

import crazypants.enderio.base.config.factory.IValue;
import crazypants.enderio.base.config.factory.IValueFactory;

public final class ConduitConfig {

  private static final int MAX = 2_000_000_000; // 0x77359400, keep some headroom to MAX_INT
  private static final int MAXIO = MAX / 2;

  public static final IValueFactory F = Config.F.section("condit");
  public static final IValueFactory FE = F.section(".energy");
  public static final IValueFactory FF = F.section(".fluid");
  public static final IValueFactory FI = F.section(".item");
  public static final IValueFactory FR = F.section(".redstone");

  public static final IValue<Boolean> dynamicLighting = F.make("dynamicLighting", false, //
      "If enabled, conduits will change their light levels based on their contents.").sync();
  public static final IValue<Boolean> updateLightingWhenHidingFacades = F.make("updateLightingWhenHidingFacades", false, //
      "When true: Correct lighting is recalculated (client side) for conduit bundles when transitioning from being hidden "
          + "behind a facade. This produces better quality rendering but can result in frame stutters when switching to/from a "
          + "wrench. (client-only setting)");
  public static final IValue<Boolean> transparentFacadesLetThroughBeaconBeam = F.make("transparentFacadesLetThroughBeaconBeam", true, //
      "If enabled, transparent facades will not block the Beacon's beam. As side effect they will also let through a tiny amount of light.").sync();

  public static final IValue<Integer> tier1_maxIO = FE.make("tier1_maxIO", 640, //
      "The maximum IO for the tier 1 power conduit.").setRange(1, MAXIO).sync();
  public static final IValue<Integer> tier2_maxIO = FE.make("tier2_maxIO", 5120, //
      "The maximum IO for the tier 2 power conduit.").setRange(1, MAXIO).sync();
  public static final IValue<Integer> tier3_maxIO = FE.make("tier3_maxIO", 20480, //
      "The maximum IO for the tier 3 power conduit.").setRange(1, MAXIO).sync();

  public static final IValue<Boolean> canDifferentTiersConnect = FE.make("canDifferentTiersConnect", false, //
      "If set to false power conduits of different tiers cannot be connected. in this case a block such as a cap. bank is needed to bridge "
          + "different tiered networks.")
      .sync();
  public static final IValue<Boolean> detailedTracking = FE.make("detailedTracking", false, //
      "Enable per tick sampling on individual power inputs and outputs. This allows slightly more detailed messages from the Conduit Probe but "
          + "has a negative impact on server performance.")
      .sync();

  public static final IValue<Boolean> showState = FR.make("showState", false, //
      "If set to false redstone conduits will look the same whether they are recieving a signal or not. This can help with performance.").sync();

  public static final IValue<Integer> fluid_tier1_extractRate = FF.make("tier1_extractRate", 50, //
      "Millibuckets per tick extracted by a fluid conduit's auto extracting.").setRange(1, MAXIO).sync();
  public static final IValue<Integer> fluid_tier1_maxIO = FF.make("tier1_maxIO", 200, //
      "Millibuckets per tick that can pass through a single connection to a fluid conduit.").setRange(1, MAXIO).sync();
  public static final IValue<Integer> fluid_tier2_extractRate = FF.make("tier2_extractRate", 100, //
      "Millibuckets per tick extracted by a pressurized fluid conduit's auto extracting.").setRange(1, MAXIO).sync();
  public static final IValue<Integer> fluid_tier2_maxIO = FF.make("tier2_maxIO", 400, //
      "Millibuckets per tick that can pass through a single connection to a pressurized fluid conduit.").setRange(1, MAXIO).sync();
  public static final IValue<Integer> fluid_tier3_extractRate = FF.make("tier3_extractRate", 200, //
      "Millibuckets per tick extracted by a ender fluid conduit's auto extracting.").setRange(1, MAXIO).sync();
  public static final IValue<Integer> fluid_tier3_maxIO = FF.make("tier3_maxIO", 800, //
      "Millibuckets per tick that can pass through a single connection to a ender fluid conduit.").setRange(1, MAXIO).sync();

  public static final IValue<Boolean> usePhyscialDistance = FI.make("usePhyscialDistance", false, //
      "If true, 'line of sight' distance rather than conduit path distance is used to calculate priorities.").sync();

}
