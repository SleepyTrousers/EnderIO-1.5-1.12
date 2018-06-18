package crazypants.enderio.base.enchantment;

import javax.annotation.Nonnull;

import com.enderio.core.api.common.enchant.IAdvancedEnchant;

import crazypants.enderio.base.EnderIO;
import crazypants.enderio.base.teleport.RandomTeleportUtil;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraftforge.event.RegistryEvent.Register;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@EventBusSubscriber(modid = EnderIO.MODID)
public class EnchantmentRepellent extends Enchantment implements IAdvancedEnchant {

  private static final @Nonnull String NAME = "repellent";

  @SubscribeEvent
  public static void register(Register<Enchantment> event) {
    event.getRegistry().register(new EnchantmentRepellent());
  }

  public EnchantmentRepellent() {
    super(Rarity.VERY_RARE, EnumEnchantmentType.ARMOR,
        new EntityEquipmentSlot[] { EntityEquipmentSlot.CHEST, EntityEquipmentSlot.LEGS, EntityEquipmentSlot.FEET });
    setName(EnderIO.DOMAIN + "." + NAME);
    setRegistryName(EnderIO.DOMAIN, NAME);
  }

  @Override
  public int getMinEnchantability(int enchantmentLevel) {
    return 10 + 5 * enchantmentLevel;
  }

  @Override
  public int getMaxEnchantability(int enchantmentLevel) {
    return 10 + 10 * enchantmentLevel;
  }

  @Override
  public int getMaxLevel() {
    return 4;
  }

  @Override
  public void onUserHurt(@Nonnull EntityLivingBase user, @Nonnull Entity attacker, int level) {
    if (attacker instanceof EntityLivingBase && !EnchantmentHelper.getEnchantedItem(this, user).isEmpty() && user.getRNG().nextFloat() < (.5f + .1f * level)) {
      RandomTeleportUtil.teleportEntity(attacker.world, attacker, false, attacker instanceof EntityPlayer || user.getRNG().nextFloat() < .75f, 16 * level);
    }
  }

}
