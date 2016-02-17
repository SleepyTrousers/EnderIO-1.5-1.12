package crazypants.enderio.enchantment;

import crazypants.enderio.config.Config;

public class Enchantments {

  private static Enchantments instance;

  public static Enchantments getInstance() {
    if(instance == null) {
      instance = new Enchantments();
      instance.registerEnchantments();
    }
    return instance;
  }

  private EnchantmentSoulBound soulBound;

  private void registerEnchantments() {
    if(Config.enchantmentSoulBoundEnabled) {     
      soulBound = EnchantmentSoulBound.create(Config.enchantmentSoulBoundId);
    }
  }
  
}
