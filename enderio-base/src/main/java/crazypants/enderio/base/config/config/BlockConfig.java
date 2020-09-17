package crazypants.enderio.base.config.config;

import crazypants.enderio.base.config.factory.IValueFactoryEIO;
import info.loenwind.autoconfig.factory.IValue;

public final class BlockConfig {

  public static final IValueFactoryEIO F = BaseConfig.F.section("blocks");
  public static final IValueFactoryEIO F1 = F.section(".charges");
  public static final IValueFactoryEIO F11 = F1.section(".confusion");
  public static final IValueFactoryEIO F12 = F1.section(".concussion");
  public static final IValueFactoryEIO F13 = F1.section(".ender");

  public static final IValue<Float> confusingChargeRange = F11.make("range", 6f, //
      "The range of the confusion charge's effect.").setRange(1, 99).sync();

  public static final IValue<Integer> confusingChargeEffectDuration = F11.make("duration", 300, //
      "Numer of ticks the confusion effect active. Scales with distance from the expolosion.").setRange(1, 3000).sync();

  public static final IValue<Float> enderChargeRange = F13.make("range", 6f, //
      "The range of the ender charge's effect.").setRange(1, 99).sync();

  public static final IValue<Float> darkSteelLadderSpeedBoost = F.make("darkSteelLadderSpeedBoost", 0.06f, //
      "Speed boost, in blocks per tick, that the DS ladder gives over the vanilla ladder.").setRange(0, 0.6).sync();

  public static final IValueFactoryEIO GLASS = F.section(".glass");

  public static final IValue<Boolean> clearGlassConnectToFusedQuartz = GLASS.make("clearGlassConnectToFusedQuartz", false, //
      "If true, quite clear glass will connect textures with fused quartz.");
  public static final IValue<Boolean> glassConnectToTheirVariants = GLASS.make("glassConnectToTheirVariants", true, //
      "If true, quite clear glass and fused quartz will connect textures with their respective enlightened and darkened variants.");
  public static final IValue<Boolean> glassConnectToTheirColorVariants = GLASS.make("glassConnectToTheirColorVariants", true, //
      "If true, quite clear glass and fused quartz of different colors will connect textures.");

  public static final IValueFactoryEIO GLOW = F.section(".glowstone");

  public static final IValue<Boolean> paintedGlowstoneRequireSilkTouch = GLASS.make("paintedGlowstoneRequireSilkTouch", false, //
      "If true, painted glowstone will drop dust unless broken with silk touch.").sync();

  public static final IValueFactoryEIO DSA = F.section(".dark_steel_anvil");

  public static final IValue<Float> darkSteelAnvilDamageChance = DSA.make("damageChance", 0.024f, //
      "Chance that the Dark Steel Anvil will take damage after repairing something. (Vanilla anvil is 0.12)").setRange(0, 1).sync();

  public static final IValue<Integer> dsaMaxCost = DSA.make("maxCost", 80, //
      "Maximum enchantment cost the Dark Steel Anvil can handle. Also applies to the Anvil Upgrade.").setRange(40, 400).sync();

}
