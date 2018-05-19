package crazypants.enderio.zoo.enchantment;

import crazypants.enderio.zoo.config.Config;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.MobEffects;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.potion.PotionEffect;

public class EnchantmentWitherArrow extends Enchantment {

  private static final String NAME = "witherArrow";

  public EnchantmentWitherArrow() {
    super(Config.enchantmentWitherArrowRarity, EnumEnchantmentType.BOW, new EntityEquipmentSlot[] {EntityEquipmentSlot.MAINHAND});
    setName(NAME);
    setRegistryName(NAME);
  }

  @Override
  public int getMinEnchantability(int enchantmentLevel) {
    return Config.enchantmentWitherArrowMinEnchantability;
  }

  @Override
  public int getMaxEnchantability(int enchantmentLevel) {
    return Config.enchantmentWitherArrowMaxEnchantability;
  }

  @Override
  public int getMaxLevel() {
    return 1;
  }

  @Override
  public void onEntityDamaged(EntityLivingBase user, Entity entityHit, int level) {
    //calc damage modifier    
    if (entityHit instanceof EntityLivingBase) {
      ((EntityLivingBase) entityHit).addPotionEffect(new PotionEffect(MobEffects.WITHER, Config.enchantmentWitherArrowDuration));
    }
  }
 
}
