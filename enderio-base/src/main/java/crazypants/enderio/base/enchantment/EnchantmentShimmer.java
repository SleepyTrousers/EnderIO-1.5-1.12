package crazypants.enderio.base.enchantment;

import javax.annotation.Nonnull;

import crazypants.enderio.base.EnderIO;
import crazypants.enderio.base.config.config.EnchantmentConfig;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraftforge.event.RegistryEvent.Register;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@EventBusSubscriber(modid = EnderIO.MODID)
public class EnchantmentShimmer extends EnchantmentBase {

  private static final @Nonnull String NAME = "shimmer";

  @SubscribeEvent
  public static void register(Register<Enchantment> event) {
    event.getRegistry().register(new EnchantmentShimmer());
  }

  public EnchantmentShimmer() {
    super(NAME, EnchantmentConfig.shimmerRarity, EnumEnchantmentType.ALL, EntityEquipmentSlot.values(), EnchantmentConfig.shimmerEnabled);
  }

  @Override
  public int getMinEnchantability(int enchantmentLevel) {
    return EnchantmentConfig.shimmerMinEnchantability.get();
  }

  @Override
  public int getMaxEnchantability(int enchantmentLevel) {
    return EnchantmentConfig.shimmerMaxEnchantability.get();
  }

  @Override
  public int getMaxLevel() {
    return 1;
  }

}
