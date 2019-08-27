package crazypants.enderio.base.integration.thaumcraft;

import com.enderio.core.common.mixin.SimpleMixin;

import crazypants.enderio.base.handler.darksteel.DarkSteelController;
import crazypants.enderio.base.item.darksteel.ItemDarkSteelArmor;
import crazypants.enderio.util.Prep;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import thaumcraft.api.items.IGoggles;
import thaumcraft.api.items.IRevealer;
import thaumcraft.api.items.IVisDiscountGear;

@SimpleMixin(value = ItemDarkSteelArmor.class, dependencies = "thaumcraft")
public abstract class ThaumcraftArmorMixin extends Item implements IVisDiscountGear, IGoggles, IRevealer {

  @Override
  public boolean showNodes(ItemStack stack, EntityLivingBase player) {
    return stack != null && Prep.isValid(stack) && (player instanceof EntityPlayer)
        && DarkSteelController.isActive((EntityPlayer) player, GogglesOfRevealingUpgrade.INSTANCE) && GogglesOfRevealingUpgrade.INSTANCE.hasUpgrade(stack);
  }

  @Override
  public boolean showIngamePopups(ItemStack stack, EntityLivingBase player) {
    return stack != null && Prep.isValid(stack) && (player instanceof EntityPlayer)
        && DarkSteelController.isActive((EntityPlayer) player, GogglesOfRevealingUpgrade.INSTANCE) && GogglesOfRevealingUpgrade.INSTANCE.hasUpgrade(stack);
  }

  @Override
  public int getVisDiscount(ItemStack stack, EntityPlayer player) {
    if (stack == null || Prep.isInvalid(stack)) {
      return -100; // Garbage in, garbage out
    }
    return ThaumaturgeRobesUpgrade.BOOTS.hasUpgrade(stack) ? 2
        : ThaumaturgeRobesUpgrade.LEGS.hasUpgrade(stack) ? 3
            : ThaumaturgeRobesUpgrade.CHEST.hasUpgrade(stack) ? 3 : GogglesOfRevealingUpgrade.INSTANCE.hasUpgrade(stack) ? 5 : 0;
  }

}
