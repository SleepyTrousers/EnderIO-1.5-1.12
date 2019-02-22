package crazypants.enderio.base.config.config;

import info.loenwind.autoconfig.factory.IValue;
import info.loenwind.autoconfig.factory.IValueFactory;
import net.minecraft.enchantment.Enchantment.Rarity;

public final class EnchantmentConfig {

  public static final IValueFactory F = BaseConfig.F.section("enchantments");

  public static final IValueFactory F_REP = F.section(".repellent");

  public static final IValue<Boolean> repellentEnabled = F_REP.make("enabled", true, //
      "Should Repellent be acquirable? If disabled, it will still work but it will not be possible to apply it to items.").sync();
  public static final IValue<Integer> repellentMinEnchantabilityBase = F_REP.make("minEnchantabilityBase", 10, //
      "Minimum enchantability for Repellent, base value.").setRange(1, 100).sync();
  public static final IValue<Integer> repellentMinEnchantabilityPerLevel = F_REP.make("minEnchantabilityPerLevel", 5, //
      "Minimum enchantability for Repellent, per level.").setRange(1, 100).sync();
  public static final IValue<Integer> repellentMaxEnchantabilityBase = F_REP.make("maxEnchantabilityBase", 10, //
      "Maximum enchantability for Repellent, base value.").setRange(1, 100).sync();
  public static final IValue<Integer> repellentMaxEnchantabilityPerLevel = F_REP.make("maxEnchantabilityPerLevel", 10, //
      "Maximum enchantability for Repellent, per level.").setRange(1, 100).sync();
  public static final IValue<Rarity> repellentRarity = F_REP.make("rarity", Rarity.VERY_RARE, //
      "Rarity for Repellent.").sync();

  public static final IValue<Integer> repellentMaxLevel = F_REP.make("maxLevel", 4, //
      "Maximum enchantment level. (Restart your game after changing this.)").setRange(1, 10).sync();
  public static final IValue<Float> repellentChanceBase = F_REP.make("chanceBase", .35f, //
      "Base chance to teleport.").setRange(0, 1).sync();
  public static final IValue<Float> repellentChancePerLevel = F_REP.make("chancePerLevel", .1f, //
      "Chance to teleport per level.").setRange(0.01, 1).sync();
  public static final IValue<Float> repellentSafeMobsChance = F_REP.make("safeMobsChanc", .75f, //
      "Probability that non-players will be teleported to a safe target location. (Player always will be teleported safely.)").setRange(0, 1).sync();
  public static final IValue<Float> repellentRangeBase = F_REP.make("rangeBase", 8f, //
      "Base teleport range.").setRange(0, 64).sync();
  public static final IValue<Float> repellentRangePerLevel = F_REP.make("rangePerLevel", 8f, //
      "Teleport range per level.").setRange(0, 64).sync();

  public static final IValueFactory F_SIM = F.section(".shimmer");

  public static final IValue<Boolean> shimmerEnabled = F_SIM.make("enabled", true, //
      "Should Shimmer be acquirable? If disabled, it will still work but it will not be possible to apply it to items.").sync();
  public static final IValue<Integer> shimmerMinEnchantability = F_SIM.make("minEnchantability", 1, //
      "Minimum enchantability for Shimmer.").setRange(1, 100).sync();
  public static final IValue<Integer> shimmerMaxEnchantability = F_SIM.make("maxEnchantability", 100, //
      "Maximum enchantability for Shimmer.").setRange(1, 100).sync();
  public static final IValue<Rarity> shimmerRarity = F_SIM.make("rarity", Rarity.VERY_RARE, //
      "Rarity for Shimmer.").sync();

  public static final IValueFactory F_WIA = F.section(".witherarrow");

  public static final IValue<Boolean> witherArrowEnabled = F_WIA.make("enabled", true, //
      "Should WitherArrow (Withering) be acquirable? If disabled, it will still work but it will not be possible to apply it to items.").sync();
  public static final IValue<Integer> witherArrowMinEnchantability = F_WIA.make("wninEnchantability", 20, //
      "Minimum enchantability for WitherArrow (Withering).").setRange(1, 100).sync();
  public static final IValue<Integer> witherArrowMaxEnchantability = F_WIA.make("naxEnchantability", 50, //
      "Maximum enchantability for WitherArrow (Withering).").setRange(1, 100).sync();
  public static final IValue<Rarity> witherArrowRarity = F_WIA.make("rarity", Rarity.UNCOMMON, //
      "Rarity for WitherArrow (Withering).").sync();

  public static final IValueFactory F_WIW = F.section(".witherweapon");

  public static final IValue<Boolean> witherWeaponEnabled = F_WIW.make("enabled", true, //
      "Should WitherWeapon (Decay) be acquirable? If disabled, it will still work but it will not be possible to apply it to items.").sync();
  public static final IValue<Integer> witherWeaponMinEnchantability = F_WIW.make("minEnchantability", 20, //
      "Minimum enchantability for WitherWeapon (Decay).").setRange(1, 100).sync();
  public static final IValue<Integer> witherWeaponMaxEnchantability = F_WIW.make("maxEnchantability", 50, //
      "Maximum enchantability for WitherWeapon (Decay).").setRange(1, 100).sync();
  public static final IValue<Rarity> witherWeaponRarity = F_WIW.make("rarity", Rarity.UNCOMMON, //
      "Rarity for WitherWeapon (Decay).").sync();

  public static final IValueFactory F_SOB = F.section(".soulbound");

  public static final IValue<Boolean> soulboundEnabled = F_SOB.make("enabled", true, //
      "Should Soulbound be acquirable? If disabled, it will still work but it will not be possible to apply it to items.").sync();
  public static final IValue<Integer> soulboundMinEnchantability = F_SOB.make("minEnchantability", 16, //
      "Minimum enchantability for Soulbound.").setRange(1, 100).sync();
  public static final IValue<Integer> soulboundMaxEnchantability = F_SOB.make("maxEnchantability", 60, //
      "Maximum enchantability for Soulbound.").setRange(1, 100).sync();
  public static final IValue<Rarity> soulboundRarity = F_SOB.make("rarity", Rarity.VERY_RARE, //
      "Rarity for Soulbound.").sync();

}
