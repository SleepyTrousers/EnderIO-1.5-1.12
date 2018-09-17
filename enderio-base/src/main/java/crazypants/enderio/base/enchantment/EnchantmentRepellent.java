package crazypants.enderio.base.enchantment;

import javax.annotation.Nonnull;

import crazypants.enderio.base.EnderIO;
import crazypants.enderio.base.config.config.EnchantmentConfig;
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
public class EnchantmentRepellent extends EnchantmentBase {

  private static final @Nonnull String NAME = "repellent";

  @SubscribeEvent
  public static void register(Register<Enchantment> event) {
    event.getRegistry().register(new EnchantmentRepellent());
  }

  public EnchantmentRepellent() {
    super(NAME, EnchantmentConfig.repellentRarity, EnumEnchantmentType.ARMOR,
        new EntityEquipmentSlot[] { EntityEquipmentSlot.CHEST, EntityEquipmentSlot.LEGS, EntityEquipmentSlot.FEET }, EnchantmentConfig.repellentEnabled);
  }

  @Override
  public int getMinEnchantability(int enchantmentLevel) {
    return EnchantmentConfig.repellentMinEnchantabilityBase.get() + EnchantmentConfig.repellentMinEnchantabilityPerLevel.get() * enchantmentLevel;
  }

  @Override
  public int getMaxEnchantability(int enchantmentLevel) {
    return EnchantmentConfig.repellentMaxEnchantabilityBase.get() + EnchantmentConfig.repellentMaxEnchantabilityPerLevel.get() * enchantmentLevel;
  }

  @Override
  public int getMaxLevel() {
    return 4;
  }

  @Override
  public void onUserHurt(@Nonnull EntityLivingBase user, @Nonnull Entity attacker, int level) {
    if (user instanceof EntityPlayer && attacker instanceof EntityLivingBase && !EnchantmentHelper.getEnchantedItem(this, user).isEmpty()) {
      if (level > getMaxLevel()) {
        for (Entity e : user.world.getEntitiesWithinAABBExcludingEntity(user, user.getEntityBoundingBox().expand(level * 8, level * 4, level * 8))) {
          RandomTeleportUtil.teleportEntity(e.world, e, false, false, 16 * level);
        }
      } else if (user.getRNG().nextFloat() < (.5f + .1f * level)) {
        RandomTeleportUtil.teleportEntity(attacker.world, attacker, false, attacker instanceof EntityPlayer || user.getRNG().nextFloat() < .75f, 16 * level);
      }
    }
  }

}
