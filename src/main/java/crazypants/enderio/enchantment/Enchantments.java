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

  private void registerEnchantments() {
    if(Config.enchantmentSoulBoundEnabled) {     
      EnchantmentSoulBound.create(Config.enchantmentSoulBoundId);
    }
  }
  
}
