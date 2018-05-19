package crazypants.enderio.zoo.enchantment;

import crazypants.enderio.zoo.config.Config;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.MobEffects;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.potion.PotionEffect;

public class EnchantmentWitherWeapon extends Enchantment {

  private static final String NAME = "witherWeapon";

  public EnchantmentWitherWeapon() {
    super(Config.enchantmentWitherWeaponRarity, EnumEnchantmentType.WEAPON, new EntityEquipmentSlot[] {EntityEquipmentSlot.MAINHAND, EntityEquipmentSlot.OFFHAND});    
    setName(NAME);
    setRegistryName(NAME);

  }

  @Override
  public int getMinEnchantability(int p_77321_1_) {
    return Config.enchantmentWitherWeaponMinEnchantability;
  }

  @Override
  public int getMaxEnchantability(int p_77317_1_) {
    return Config.enchantmentWitherWeaponMaxEnchantability;
  }

  @Override
  public int getMaxLevel() {
    return 1;
  }

  @Override
  public void onEntityDamaged(EntityLivingBase user, Entity entityHit, int level) {
    //calc damage modifier    
    if (entityHit instanceof EntityLivingBase) {
      ((EntityLivingBase) entityHit).addPotionEffect(new PotionEffect(MobEffects.WITHER, Config.enchantmentWitherWeaponDuration));
    }
  }

}
