package crazypants.enderio.base.config.config;

import crazypants.enderio.base.config.factory.IValue;
import crazypants.enderio.base.config.factory.IValueFactory;

public final class ZooConfig {

  public static final IValueFactory F = BaseConfig.F.section("blocks");
  public static final IValueFactory F1 = F.section(".charges");
  public static final IValueFactory F11 = F1.section(".confusion");
  public static final IValueFactory F12 = F1.section(".concussion");
  public static final IValueFactory F13 = F1.section(".ender");

  public static final IValue<Float> confusingChargeRange = F11.make("range", 6f, //
      "The range of the confusion charge's effect.").setRange(1, 99).sync();

  public static final IValue<Integer> confusingChargeEffectDuration = F11.make("duration", 300, //
      "Numer of ticks the confusion effect active. Scales with distance from the expolosion.").setRange(1, 3000).sync();

  public static final IValue<Float> enderChargeRange = F13.make("range", 6f, //
      "The range of the ender charge's effect.").setRange(1, 99).sync();

}
