package crazypants.enderio.base.config.config;

import info.loenwind.autoconfig.factory.IValue;
import info.loenwind.autoconfig.factory.IValueFactory;

public final class DiagnosticsConfig {

  private static final String GENERIC_WARNING = "This will flood your logfile with gigabytes of data filling up your harddisk very fast. DO NOT enable unless asked by an Ender IO developer!";

  public static final IValueFactory F = BaseConfig.F.section("diagnostics");

  public static final IValue<Boolean> debugUpdatePackets = F.make("debugUpdatePackets", false, //
      "If true, TEs will flash when they recieve an update packet.");

  public static final IValue<Boolean> debugChunkRerenders = F.make("debugChunkRerenders", false, //
      "If true, TEs will flash when they trigger a chunk re-render.");

  public static final IValue<Boolean> debugUpgradeDebugMessagesToInfo = F.make("debugUpgradeDebugMessagesToInfo", false, GENERIC_WARNING);

  public static final IValue<Boolean> debugTraceNBTActivityExtremelyDetailed = F.make("debugTraceNBTActivityExtremelyDetailed", false, GENERIC_WARNING);

  public static final IValue<Boolean> debugTraceTELivecycleExtremelyDetailed = F.make("debugTraceTELivecycleExtremelyDetailed", false, GENERIC_WARNING);

  public static final IValue<Boolean> debugTraceCapLimitsExtremelyDetailed = F.make("debugTraceCapLimitsExtremelyDetailed", false, GENERIC_WARNING);

  public static final IValue<Boolean> debugProfilerTracer = F.make("debugProfilerTracer", false, GENERIC_WARNING);

  public static final IValue<Boolean> debugProfilerAntiNuclearActivist = F.make("debugProfilerAntiNuclearActivist", true, //
      "This will change profiler interactions. DO NOT change unless asked by an Ender IO developer!");

  public static final IValue<Boolean> debugProfilerResetOnServerTick = F.make("debugProfilerResetOnServerTick", true, //
      "This will change profiler interactions. DO NOT change unless asked by an Ender IO developer!");

  public static final IValue<Boolean> experimentalChunkLoadTeleport = F.make("experimentalChunkLoadTeleport", true, //
      "Experimental: When enabled, this will chunkload the involved chunks of a (potential) long-range teleport for 5 seconds. "
          + "This is intended to prevent the server from losing track of the player. Symptoms would be the player being invisible to other players, "
          + "not being able to interact with pressure plates, or being stuck in beds.");

  public static final IValue<Protection> protectEnergyOverflow = F.make("protectAgainstEnergyOverflow", Protection.SOFT, //
      "Should TEs protected their maximum energy input against multiple inserts?");

  public enum Protection {
    NONE,
    SOFT,
    HARD;
  }

  public static final IValue<Boolean> debugSuppressDebugMessages = F.make("debugSuppressDebugMessages", true, //
      "If enabled, debug level log messages will not be generated. Disabling this can put vital information into the debug log for dealing "
          + "with rare issues but will also slow down the game. This value is only checked once when the game starts.");

  public static final IValue<Boolean> debugComplainAboutForgeLogging = F.make("debugComplainAboutForgeLogging", true, //
      "If enabled, debug level configurations will be check at startup. Disable if you don't want to see the result in the logfile.");

}
