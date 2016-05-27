package crazypants.enderio.enchantment;

import crazypants.enderio.config.Config;

public class Enchantments {

  private static Enchantments instance;

  public static void register() {
    if(instance == null) {
      instance = new Enchantments();
      instance.registerEnchantments();
    }    
  }

  private void registerEnchantments() {
    if(Config.enchantmentSoulBoundEnabled) {     
      EnchantmentSoulBound.create();
    }
  }
  
}
