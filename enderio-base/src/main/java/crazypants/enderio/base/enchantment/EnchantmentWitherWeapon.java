package crazypants.enderio.base.enchantment;

import javax.annotation.Nonnull;

import crazypants.enderio.base.EnderIO;
import crazypants.enderio.base.config.config.EnchantmentConfig;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.MobEffects;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.potion.PotionEffect;
import net.minecraftforge.event.RegistryEvent.Register;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@EventBusSubscriber(modid = EnderIO.MODID)
public final class EnchantmentWitherWeapon extends EnchantmentBase {

  private static final @Nonnull String NAME = "witherweapon";

  @SubscribeEvent
  public static void register(Register<Enchantment> event) {
    event.getRegistry().register(new EnchantmentWitherWeapon());
  }

  private EnchantmentWitherWeapon() {
    super(NAME, EnchantmentConfig.witherWeaponRarity, EnumEnchantmentType.WEAPON,
        new EntityEquipmentSlot[] { EntityEquipmentSlot.MAINHAND, EntityEquipmentSlot.OFFHAND }, EnchantmentConfig.witherWeaponEnabled);
  }

  @Override
  public int getMinEnchantability(int p_77321_1_) {
    return EnchantmentConfig.witherWeaponMinEnchantability.get();
  }

  @Override
  public int getMaxEnchantability(int p_77317_1_) {
    return EnchantmentConfig.witherWeaponMaxEnchantability.get();
  }

  @Override
  public int getMaxLevel() {
    return 1;
  }

  /**
   * Original: "Called whenever a mob is damaged with an item that has this enchantment on it."
   * <p>
   * Correct: "Called whenever a mob is damaged and an item that has this enchantment on it is in the main hand, the off hand or any armor slot." (MC-131637)
   */
  @Override
  public void onEntityDamaged(@Nonnull EntityLivingBase user, @Nonnull Entity entityHit, int level) {
    if (entityHit instanceof EntityLivingBase && EnchantmentHelper.getEnchantmentLevel(this, user.getHeldItemMainhand()) > 0) {
      ((EntityLivingBase) entityHit).addPotionEffect(new PotionEffect(MobEffects.WITHER, 200));
    }
  }

}
