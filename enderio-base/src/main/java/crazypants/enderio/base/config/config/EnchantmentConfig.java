package crazypants.enderio.base.config.config;

import crazypants.enderio.base.config.factory.IValue;
import crazypants.enderio.base.config.factory.IValueFactory;
import net.minecraft.enchantment.Enchantment.Rarity;

public final class EnchantmentConfig {

  public static final IValueFactory F = BaseConfig.F.section("enchantments");

  public static final IValue<Boolean> repellentEnabled = F.make("repellentEnabled", true, //
      "Should Repellent be acquirable? If disabled, it will still work but it will not be possible to apply it to items.").sync();

  public static final IValue<Integer> repellentMinEnchantabilityBase = F.make("repellentMinEnchantabilityBase", 10, //
      "Minimum enchantability for Repellent, base value.").setRange(1, 100).sync();
  public static final IValue<Integer> repellentMinEnchantabilityPerLevel = F.make("repellentMinEnchantabilityPerLevel", 5, //
      "Minimum enchantability for Repellent, per level.").setRange(1, 100).sync();
  public static final IValue<Integer> repellentMaxEnchantabilityBase = F.make("repellentMaxEnchantabilityBase", 10, //
      "Maximum enchantability for Repellent, base value.").setRange(1, 100).sync();
  public static final IValue<Integer> repellentMaxEnchantabilityPerLevel = F.make("repellentMaxEnchantabilityPerLevel", 10, //
      "Maximum enchantability for Repellent, per level.").setRange(1, 100).sync();

  public static final IValue<Rarity> repellentRarity = F.make("repellentRarity", Rarity.VERY_RARE, //
      "Rarity for Repellent.").sync();

  public static final IValue<Boolean> shimmerEnabled = F.make("shimmerEnabled", true, //
      "Should Shimmer be acquirable? If disabled, it will still work but it will not be possible to apply it to items.").sync();
  public static final IValue<Integer> shimmerMinEnchantability = F.make("shimmerMinEnchantability", 1, //
      "Minimum enchantability for Shimmer.").setRange(1, 100).sync();
  public static final IValue<Integer> shimmerMaxEnchantability = F.make("shimmerMaxEnchantability", 100, //
      "Maximum enchantability for Shimmer.").setRange(1, 100).sync();
  public static final IValue<Rarity> shimmerRarity = F.make("shimmerRarity", Rarity.VERY_RARE, //
      "Rarity for Shimmer.").sync();

  public static final IValue<Boolean> witherArrowEnabled = F.make("witherArrowEnabled", true, //
      "Should WitherArrow (Withering) be acquirable? If disabled, it will still work but it will not be possible to apply it to items.").sync();
  public static final IValue<Integer> witherArrowMinEnchantability = F.make("witherArrowMinEnchantability", 20, //
      "Minimum enchantability for WitherArrow (Withering).").setRange(1, 100).sync();
  public static final IValue<Integer> witherArrowMaxEnchantability = F.make("witherArrowMaxEnchantability", 50, //
      "Maximum enchantability for WitherArrow (Withering).").setRange(1, 100).sync();
  public static final IValue<Rarity> witherArrowRarity = F.make("witherArrowRarity", Rarity.UNCOMMON, //
      "Rarity for WitherArrow (Withering).").sync();

  public static final IValue<Boolean> witherWeaponEnabled = F.make("witherWeaponEnabled", true, //
      "Should WitherWeapon (Decay) be acquirable? If disabled, it will still work but it will not be possible to apply it to items.").sync();
  public static final IValue<Integer> witherWeaponMinEnchantability = F.make("witherWeaponMinEnchantability", 20, //
      "Minimum enchantability for WitherWeapon (Decay).").setRange(1, 100).sync();
  public static final IValue<Integer> witherWeaponMaxEnchantability = F.make("witherWeaponMaxEnchantability", 50, //
      "Maximum enchantability for WitherWeapon (Decay).").setRange(1, 100).sync();
  public static final IValue<Rarity> witherWeaponRarity = F.make("witherWeaponRarity", Rarity.UNCOMMON, //
      "Rarity for WitherWeapon (Decay).").sync();

  public static final IValue<Boolean> soulboundEnabled = F.make("soulboundEnabled", true, //
      "Should Soulbound be acquirable? If disabled, it will still work but it will not be possible to apply it to items.").sync();
  public static final IValue<Integer> soulboundMinEnchantability = F.make("soulboundMinEnchantability", 16, //
      "Minimum enchantability for Soulbound.").setRange(1, 100).sync();
  public static final IValue<Integer> soulboundMaxEnchantability = F.make("soulboundMaxEnchantability", 60, //
      "Maximum enchantability for Soulbound.").setRange(1, 100).sync();
  public static final IValue<Rarity> soulboundRarity = F.make("soulboundRarity", Rarity.VERY_RARE, //
      "Rarity for Soulbound.").sync();

}
