package crazypants.enderio.base.config.config;

import crazypants.enderio.base.config.factory.IValue;
import crazypants.enderio.base.config.factory.IValueFactory;

public final class DiagnosticsConfig {

  public static final IValueFactory F = BaseConfig.F.section("diagnostics");

  public static final IValue<Boolean> debugUpdatePackets = F.make("debugUpdatePackets", false, //
      "If true, TEs will flash when they recieve an update packet.");

  public static final IValue<Boolean> debugChunkRerenders = F.make("debugChunkRerenders", false, //
      "If true, TEs will flash when they trigger a chunk re-render.");

  public static final IValue<Boolean> debugTraceNBTActivityExtremelyDetailed = F.make("debugTraceNBTActivityExtremelyDetailed", false, //
      "This will flood your logfile with gigabytes of data filling up your harddisk very fast. DO NOT enable unless asked by an Ender IO developer!");

  public static final IValue<Boolean> debugTraceTELivecycleExtremelyDetailed = F.make("debugTraceTELivecycleExtremelyDetailed", false, //
      "This will flood your logfile with gigabytes of data filling up your harddisk very fast. DO NOT enable unless asked by an Ender IO developer!");

  public static final IValue<Boolean> debugTraceCapLimitsExtremelyDetailed = F.make("debugTraceCapLimitsExtremelyDetailed", false, //
      "This will flood your logfile with gigabytes of data filling up your harddisk very fast. DO NOT enable unless asked by an Ender IO developer!");

  public static final IValue<Boolean> debugProfilerTracer = F.make("debugProfilerTracer", false, //
      "This will flood your logfile with gigabytes of data filling up your harddisk very fast. DO NOT enable unless asked by an Ender IO developer!");

  public static final IValue<Boolean> debugProfilerAntiNuclearActivist = F.make("debugProfilerAntiNuclearActivist", true, //
      "This will flood your logfile with gigabytes of data filling up your harddisk very fast. DO NOT change unless asked by an Ender IO developer!");

  public static final IValue<Boolean> experimentalChunkLoadTeleport = F.make("experimentalChunkLoadTeleport", true, //
      "Experimental: When enabled, this will chunkload the involved chunks of a (potential) long-range teleport for 5 seconds. "
          + "This is intended to prevent the server from losing track of the player. Symptoms would be the player being invisible to other players, "
          + "not being able to interact with pressure plates, or being stuck in beds.");

}
