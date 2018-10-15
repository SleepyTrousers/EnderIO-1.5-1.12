package crazypants.enderio.base.config.config;

import crazypants.enderio.base.config.factory.IValue;
import crazypants.enderio.base.config.factory.IValueFactory;

public final class BlockConfig {

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

  public static final IValue<Float> darkSteelAnvilDamageChance = F.make("darkSteelAnvilDamageChance", 0.024f, //
      "Chance that the dark steel anvil will take damage after repairing something.").setRange(0, 1).sync();

  public static final IValue<Float> darkSteelLadderSpeedBoost = F.make("darkSteelLadderSpeedBoost", 0.06f, //
      "Speed boost, in blocks per tick, that the DS ladder gives over the vanilla ladder.").setRange(0, 0.6).sync();

  public static final IValueFactory F_SKULLS = F.section(".skulls");

  public static final IValue<Float> darkSteelSwordSkullChance = F_SKULLS.make("darkSteelSwordSkullChance", 0.05f, //
      "The base chance that a skull will be dropped when using a powered dark steel sword (0 = no chance, 1 = 100% chance; can go over 100%)").setMin(0).sync();
  public static final IValue<Float> darkSteelSwordSkullLootingModifier = F_SKULLS.make("darkSteelSwordSkullLootingModifier", 0.05f, //
      "The added chance per looting level that a skull will be dropped when using a powered dark steel sword (0 = no chance, 1 = 100% chance; can go over 100%)")
      .setMin(0).sync();
  public static final IValue<Float> darkSteelSwordWitherSkullChance = F_SKULLS.make("darkSteelSwordWitherSkullChance", 0.1f, //
      "The base chance that a wither skull will be dropped when using a powered dark steel sword (0 = no chance, 1 = 100% chance; can go over 100%)").setMin(0)
      .sync();
  public static final IValue<Float> darkSteelSwordWitherSkullLootingModifier = F_SKULLS.make("darkSteelSwordWitherSkullLootingModifier", 0.05f, //
      "The added chance per looting level that a wither skull will be dropped when using a powered dark steel sword (0 = no chance, 1 = 100% chance; can go over 100%)")
      .setMin(0).sync();

  public static final IValue<Float> vanillaSwordSkullChance = F_SKULLS.make("vanillalSwordSkullChance", 0.05f, //
      "The base chance that a skull will be dropped when using a non dark steel sword (0 = no chance, 1 = 100% chance; can go over 100%)").setMin(0).sync();
  public static final IValue<Float> vanillaSwordSkullLootingModifier = F_SKULLS.make("vanillaSwordSkullLootingModifier", 0.05f, //
      "The added chance per looting level that a skull will be dropped when using a non dark steel sword (0 = no chance, 1 = 100% chance; can go over 100%)")
      .setMin(0).sync();
  public static final IValue<Float> ticBeheadingSkullModifier = F_SKULLS.make("ticBeheadingSkullModifier", 0.75f, //
      "The added chance per level of beaheading that a skull will be dropped when using a TiC weapon (0 = no chance, 1 = 100% chance; can go over 100%)")
      .setMin(0).sync();
  public static final IValue<Float> fakePlayerSkullChance = F_SKULLS.make("fakePlayerSkullChance", 0.5f, //
      "The ratio of skull drops when a mob is killed by a 'FakePlayer', such as Killer Joe. When set to 0 no skulls will drop, at 1 the rate of skull drops is not modified")
      .setRange(0, 1).sync();

}
