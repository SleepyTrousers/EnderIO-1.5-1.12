package crazypants.enderio.base.enchantment;

import javax.annotation.Nonnull;

import com.enderio.core.api.common.enchant.IAdvancedEnchant;

import crazypants.enderio.base.EnderIO;
import crazypants.enderio.base.config.Config;
import crazypants.enderio.base.lang.Lang;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@EventBusSubscriber(modid = EnderIO.MODID)
public class EnchantmentSoulBound extends Enchantment implements IAdvancedEnchant {

  private static final @Nonnull String NAME = "soulbound";

  @SubscribeEvent
  public static void registerDarkSteelUpgrades(@Nonnull RegistryEvent.Register<Enchantment> event) {
    event.getRegistry().register(new EnchantmentSoulBound());
  }

  private EnchantmentSoulBound() {
    super(Config.enchantmentSoulBoundRarity, EnumEnchantmentType.ALL, EntityEquipmentSlot.values());
    setName(NAME);
    setRegistryName(EnderIO.DOMAIN, NAME);
  }

  @Override
  public int getMaxEnchantability(int level) {
    return 60;
  }

  @Override
  public int getMinEnchantability(int level) {
    return 16;
  }

  @Override
  public int getMaxLevel() {
    return 1;
  }

  @Override
  public @Nonnull String[] getTooltipDetails(@Nonnull ItemStack stack) {
    return new String[] { Lang.ENCHANT_SOULBOUND.get() };
  }

}
