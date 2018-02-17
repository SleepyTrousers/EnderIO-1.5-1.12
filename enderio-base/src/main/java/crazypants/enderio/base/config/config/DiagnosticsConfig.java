package crazypants.enderio.base.config.config;

import crazypants.enderio.base.config.Config.Section;
import crazypants.enderio.base.config.SectionedValueFactory;
import crazypants.enderio.base.config.ValueFactory.IValue;

public final class DiagnosticsConfig {

  public static final SectionedValueFactory F = new SectionedValueFactory(BaseConfig.F, new Section("", "diagnostics"));

  public static final IValue<Boolean> debugUpdatePackets = F.make("debugUpdatePackets", false, //
      "If true, TEs will flash when they recieve an update packet.");

  public static final IValue<Boolean> debugTraceNBTActivityExtremelyDetailed = F.make("debugTraceNBTActivityExtremelyDetailed", false, //
      "This will flood your logfile with gigabytes of data filling up your harddisk very fast. DO NOT enable unless asked by an Ender IO developer!");

  public static final IValue<Boolean> debugTraceTELivecycleExtremelyDetailed = F.make("debugTraceTELivecycleExtremelyDetailed", false, //
      "This will flood your logfile with gigabytes of data filling up your harddisk very fast. DO NOT enable unless asked by an Ender IO developer!");

  public static final IValue<Boolean> debugTraceCapLimitsExtremelyDetailed = F.make("debugTraceCapLimitsExtremelyDetailed", false, //
      "This will flood your logfile with gigabytes of data filling up your harddisk very fast. DO NOT enable unless asked by an Ender IO developer!");

}
