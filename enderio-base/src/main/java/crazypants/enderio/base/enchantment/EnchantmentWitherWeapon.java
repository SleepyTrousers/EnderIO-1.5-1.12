package crazypants.enderio.base.enchantment;

import javax.annotation.Nonnull;

import crazypants.enderio.base.EnderIO;
import net.minecraft.enchantment.Enchantment;
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
public class EnchantmentWitherWeapon extends Enchantment {

  private static final @Nonnull String NAME = "witherweapon";

  @SubscribeEvent
  public static void register(Register<Enchantment> event) {
    event.getRegistry().register(new EnchantmentWitherWeapon());
  }

  public EnchantmentWitherWeapon() {
    super(Rarity.UNCOMMON, EnumEnchantmentType.WEAPON, new EntityEquipmentSlot[] { EntityEquipmentSlot.MAINHAND, EntityEquipmentSlot.OFFHAND });
    setName(EnderIO.DOMAIN + "." + NAME);
    setRegistryName(EnderIO.DOMAIN, NAME);
  }

  @Override
  public int getMinEnchantability(int p_77321_1_) {
    return 20;
  }

  @Override
  public int getMaxEnchantability(int p_77317_1_) {
    return 50;
  }

  @Override
  public int getMaxLevel() {
    return 1;
  }

  @Override
  public void onEntityDamaged(@Nonnull EntityLivingBase user, @Nonnull Entity entityHit, int level) {
    if (entityHit instanceof EntityLivingBase) {
      ((EntityLivingBase) entityHit).addPotionEffect(new PotionEffect(MobEffects.WITHER, 200));
    }
  }

}
