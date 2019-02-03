package crazypants.enderio.integration.forestry.upgrades;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.common.transform.SimpleMixin;

import crazypants.enderio.api.upgrades.IDarkSteelItem;
import crazypants.enderio.api.upgrades.IDarkSteelUpgrade;
import crazypants.enderio.base.item.darksteel.ItemDarkSteelArmor;
import forestry.api.apiculture.IArmorApiarist;
import forestry.api.core.IArmorNaturalist;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.GameRegistry.ObjectHolder;

@SimpleMixin(dependencies = "forestry", value = ItemDarkSteelArmor.class)
public abstract class ArmorMixin implements IArmorApiarist, IArmorNaturalist, IDarkSteelItem {

  @ObjectHolder("enderiointegrationforestry:apiarist_armor_feet")
  public static final IDarkSteelUpgrade FORESTRY_FEET = null;
  @ObjectHolder("enderiointegrationforestry:apiarist_armor_legs")
  public static final IDarkSteelUpgrade FORESTRY_LEGS = null;
  @ObjectHolder("enderiointegrationforestry:apiarist_armor_chest")
  public static final IDarkSteelUpgrade FORESTRY_CHEST = null;
  @ObjectHolder("enderiointegrationforestry:apiarist_armor_head")
  public static final IDarkSteelUpgrade FORESTRY_HEAD = null;
  @ObjectHolder("enderiointegrationforestry:naturalist_eye")
  public static final IDarkSteelUpgrade FORESTRY_EYES = null;

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
