package crazypants.enderio.base.enchantment;

import javax.annotation.Nonnull;

import com.enderio.core.api.common.enchant.IAdvancedEnchant;
import com.enderio.core.common.util.NullHelper;

import crazypants.enderio.base.EnderIO;
import crazypants.enderio.base.config.config.EnchantmentConfig;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@EventBusSubscriber(modid = EnderIO.MODID)
public final class EnchantmentSoulBound extends EnchantmentBase {

  private static final @Nonnull String NAME = "soulbound";

  @SubscribeEvent
  public static void register(@Nonnull RegistryEvent.Register<Enchantment> event) {
    event.getRegistry().register(new EnchantmentSoulBound());
  }

  private EnchantmentSoulBound() {
    super(NAME, EnchantmentConfig.soulboundRarity, NullHelper.first(IAdvancedEnchant.ALL, EnumEnchantmentType.ALL), EntityEquipmentSlot.values(),
        EnchantmentConfig.soulboundEnabled);
  }

  @Override
  public int getMinEnchantability(int level) {
    return EnchantmentConfig.soulboundMinEnchantability.get();
  }

  @Override
  public int getMaxEnchantability(int level) {
    return EnchantmentConfig.soulboundMaxEnchantability.get();
  }

  @Override
  public int getMaxLevel() {
    return 1;
  }

}
