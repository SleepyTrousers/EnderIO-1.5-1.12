package crazypants.enderio.base.enchantment;

import javax.annotation.Nonnull;

import com.enderio.core.common.util.NullHelper;

import crazypants.enderio.base.config.Config;
import net.minecraft.enchantment.Enchantment;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;

public class Enchantments {

  private static Enchantment soulbound;

  public static void init(@Nonnull FMLInitializationEvent event) {
    if (Config.enchantmentSoulBoundEnabled) {
      soulbound = EnchantmentSoulBound.create();
      MinecraftForge.EVENT_BUS.register(HandlerSoulBound.class);
    }
  }

  public static @Nonnull Enchantment getSoulbound() {
    return NullHelper.notnull(soulbound, "enchantment soulbound went unbound");
  }

}
