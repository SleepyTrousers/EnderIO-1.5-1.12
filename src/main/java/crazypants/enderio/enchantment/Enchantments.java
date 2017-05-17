package crazypants.enderio.enchantment;

import javax.annotation.Nonnull;

import com.enderio.core.common.util.NullHelper;

import crazypants.enderio.config.Config;
import net.minecraft.enchantment.Enchantment;
import net.minecraftforge.common.MinecraftForge;

public class Enchantments {

  private static Enchantment soulbound;

  public static void register() {
    if (Config.enchantmentSoulBoundEnabled) {
      soulbound = EnchantmentSoulBound.create();
      MinecraftForge.EVENT_BUS.register(HandlerSoulBound.class);
    }
  }

  public static @Nonnull Enchantment getSoulbound() {
    return NullHelper.notnull(soulbound, "enchantment soulbound went unbound");
  }

}
