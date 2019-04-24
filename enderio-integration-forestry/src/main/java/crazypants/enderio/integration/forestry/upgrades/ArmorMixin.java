package crazypants.enderio.integration.forestry.upgrades;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.common.mixin.SimpleMixin;

import crazypants.enderio.api.upgrades.IDarkSteelItem;
import crazypants.enderio.base.item.darksteel.ItemDarkSteelArmor;
import forestry.api.apiculture.IArmorApiarist;
import forestry.api.core.IArmorNaturalist;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

@SimpleMixin(dependencies = "forestry", value = ItemDarkSteelArmor.class)
public abstract class ArmorMixin extends Item implements IArmorApiarist, IArmorNaturalist, IDarkSteelItem {

  @Override
  public boolean canSeePollination(@Nonnull EntityPlayer player, @Nonnull ItemStack armor, boolean doSee) {
    return isForSlot(EntityEquipmentSlot.HEAD) && NaturalistEyeUpgrade.INSTANCE.hasUpgrade(player.getItemStackFromSlot(EntityEquipmentSlot.HEAD));
  }

  @Override
  public boolean protectEntity(@Nonnull EntityLivingBase entity, @Nonnull ItemStack armor, @Nullable String cause, boolean doProtect) {
    return ApiaristArmorUpgrade.HELMET.hasUpgrade(armor) || ApiaristArmorUpgrade.CHEST.hasUpgrade(armor) || ApiaristArmorUpgrade.BOOTS.hasUpgrade(armor)
        || ApiaristArmorUpgrade.LEGS.hasUpgrade(armor);
  }

}
