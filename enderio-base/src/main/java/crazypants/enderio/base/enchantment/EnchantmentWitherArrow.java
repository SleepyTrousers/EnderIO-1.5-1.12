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
public class EnchantmentWitherArrow extends Enchantment {

  private static final @Nonnull String NAME = "witherarrow";

  @SubscribeEvent
  public static void register(Register<Enchantment> event) {
    event.getRegistry().register(new EnchantmentWitherArrow());
  }

  public EnchantmentWitherArrow() {
    super(Rarity.UNCOMMON, EnumEnchantmentType.BOW, new EntityEquipmentSlot[] { EntityEquipmentSlot.MAINHAND });
    setName(EnderIO.DOMAIN + "." + NAME);
    setRegistryName(EnderIO.DOMAIN, NAME);
  }

  @Override
  public int getMinEnchantability(int enchantmentLevel) {
    return 20;
  }

  @Override
  public int getMaxEnchantability(int enchantmentLevel) {
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
