package crazypants.enderio.base.enchantment;

import javax.annotation.Nonnull;

import com.enderio.core.api.common.enchant.IAdvancedEnchant;

import crazypants.enderio.base.EnderIO;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraftforge.event.RegistryEvent.Register;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@EventBusSubscriber(modid = EnderIO.MODID)
public class EnchantmentShimmer extends Enchantment implements IAdvancedEnchant {

  private static final @Nonnull String NAME = "shimmer";

  @SubscribeEvent
  public static void register(Register<Enchantment> event) {
    event.getRegistry().register(new EnchantmentShimmer());
  }

  public EnchantmentShimmer() {
    super(Rarity.VERY_RARE, EnumEnchantmentType.ALL, EntityEquipmentSlot.values());
    setName(EnderIO.DOMAIN + "." + NAME);
    setRegistryName(EnderIO.DOMAIN, NAME);
  }

  @Override
  public int getMinEnchantability(int enchantmentLevel) {
    return 1;
  }

  @Override
  public int getMaxEnchantability(int enchantmentLevel) {
    return 100;
  }

  @Override
  public int getMaxLevel() {
    return 1;
  }

}
