package crazypants.enderio.integration.forestry.upgrades;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.common.transform.SimpleMixin;

import crazypants.enderio.api.upgrades.IDarkSteelItem;
import crazypants.enderio.base.item.darksteel.ItemDarkSteelArmor;
import forestry.api.apiculture.IArmorApiarist;
import forestry.api.core.IArmorNaturalist;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;

import static crazypants.enderio.integration.forestry.upgrades.ForestryUpgrades.*;

@SimpleMixin(dependencies = "forestry", value = ItemDarkSteelArmor.class)
public abstract class ArmorMixin implements IArmorApiarist, IArmorNaturalist, IDarkSteelItem {

  @Override
  public boolean canSeePollination(@Nonnull EntityPlayer player, @Nonnull ItemStack armor, boolean doSee) {
    System.out.println(FORESTRY_EYES != null);
    return isForSlot(EntityEquipmentSlot.HEAD) && FORESTRY_EYES != null && FORESTRY_EYES.hasUpgrade(player.getItemStackFromSlot(EntityEquipmentSlot.HEAD));
  }

  @Override
  public boolean protectEntity(@Nonnull EntityLivingBase entity, @Nonnull ItemStack armor, @Nullable String cause, boolean doProtect) {
    return (FORESTRY_HEAD != null && FORESTRY_HEAD.hasUpgrade(armor)) || (FORESTRY_CHEST != null && FORESTRY_CHEST.hasUpgrade(armor))
        || (FORESTRY_FEET != null && FORESTRY_FEET.hasUpgrade(armor)) || (FORESTRY_LEGS != null && FORESTRY_LEGS.hasUpgrade(armor));
  }

}
