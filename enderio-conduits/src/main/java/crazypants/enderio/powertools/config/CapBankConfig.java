package crazypants.enderio.powertools.config;

import crazypants.enderio.base.config.Config.Section;
import crazypants.enderio.base.config.SectionedValueFactory;
import crazypants.enderio.base.config.ValueFactory.IValue;

public final class CapBankConfig {

  private static final int MAX = 2_000_000_000; // 0x77359400, keep some headroom to MAX_INT
  private static final int MAXIO = MAX / 2;

  public static final SectionedValueFactory F = new SectionedValueFactory(Config.F, new Section("", "capbank"));

  public static final IValue<Integer> tierC_maxIO = F.make("tierC_maxIO", 50000, //
      "The maximum IO per tick for a creative capacitor bank.").setRange(1, MAXIO).sync();

  public static final IValue<Integer> tierC_maxStorage = F.make("tierC_maxStorage", 100000000, //
      "The maximum storage for a creative capacitor bank.").setRange(1, MAX).sync();

  public static final IValue<Integer> tier1_maxIO = F.make("tier1_maxIO", 1000, //
      "The maximum IO per tick for a single tier one capacitor bank.").setRange(1, MAXIO).sync();

  public static final IValue<Integer> tier1_maxStorage = F.make("tier1_maxStorage", 1000000, //
      "The maximum storage for a single tier one capacitor bank.").setRange(1, MAX).sync();

  public static final IValue<Integer> tier2_maxIO = F.make("tier2_maxIO", 5000, //
      "The maximum IO per tick for a single tier two capacitor bank.").setRange(1, MAXIO).sync();

  public static final IValue<Integer> tier2_maxStorage = F.make("tier2_maxStorage", 5000000, //
      "The maximum storage for a single tier two capacitor bank.").setRange(1, MAX).sync();

  public static final IValue<Integer> tier3_maxIO = F.make("tier3_maxIO", 25000, //
      "The maximum IO per tick for a single tier three capacitor bank.").setRange(1, MAXIO).sync();

  public static final IValue<Integer> tier3_maxStorage = F.make("tier3_maxStorage", 25000000, //
      "The maximum storage for a single tier three capacitor bank.").setRange(1, MAX).sync();

}
