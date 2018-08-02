package crazypants.enderio.api.upgrades;

import javax.annotation.Nonnull;

import net.minecraft.item.Item.ToolMaterial;
import net.minecraft.item.ItemArmor.ArmorMaterial;

public interface IEquipmentData {

  @Nonnull
  ArmorMaterial getArmorMaterial();

  @Nonnull
  ArmorMaterial getArmorMaterialEmpowered();

  @Nonnull
  ToolMaterial getToolMaterial();

  @Nonnull
  String getRepairIngotOredict();

  @Nonnull
  String getBowRepairIngotOredict();

  @Nonnull
  String getTexture1();

  @Nonnull
  String getTexture2();

  @Nonnull
  Integer getTier();

}