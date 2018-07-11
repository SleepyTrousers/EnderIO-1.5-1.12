package crazypants.enderio.base.item.darksteel.attributes;

import javax.annotation.Nonnull;

import com.enderio.core.common.util.NullHelper;

import crazypants.enderio.api.upgrades.IEquipmentData;
import crazypants.enderio.base.EnderIO;
import crazypants.enderio.base.config.Config;
import crazypants.enderio.base.config.config.PersonalConfig;
import crazypants.enderio.base.material.alloy.Alloy;
import crazypants.enderio.base.material.material.Material;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item.ToolMaterial;
import net.minecraft.item.ItemArmor.ArmorMaterial;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public enum EquipmentData implements IEquipmentData {

  IRON(ArmorMaterial.IRON, ArmorMaterial.IRON, ToolMaterial.IRON, "ingotIron", "stickWood", "", "", 0),

  DARK_STEEL(35, new int[] { 2, 5, 6, 2 }, new int[] { 3, 6, 8, 3 }, 15, SoundEvents.ITEM_ARMOR_EQUIP_IRON, 1f, 2f, "darkSteel",
      Config.darkSteelPickMinesTiCArdite ? 5 : 3, 2000, 8, 3.0001f, 25, Alloy.DARK_STEEL.getOreIngot(), Material.NUTRITIOUS_STICK.getOreDict(),
      "dark_steel_layer_1", "dark_steel_layer_2", 1),

  END_STEEL(50, new int[] { 4, 7, 10, 4 }, new int[] { 5, 8, 12, 5 }, 25, SoundEvents.ITEM_ARMOR_EQUIP_IRON, 3f, 4f, "endSteel",
      Config.endSteelPickMinesTiCArdite ? 5 : 3, 2000, 12, 5f, 30, Alloy.END_STEEL.getOreIngot(), Material.INFINITY_ROD.getOreDict(), "end_steel_layer_1",
      "end_steel_layer_2", 2) {

    @Override
    @SideOnly(Side.CLIENT)
    protected @Nonnull String getAnimationFrame() {
      if (PersonalConfig.animatedEnderArmorEnabled.get()) {
        int tick = (int) ((EnderIO.proxy.getTickCount() / 2) % 26);
        switch (tick) {
        case 16:
        case 25:
          return "_1";
        case 17:
        case 24:
          return "_2";
        case 18:
        case 23:
          return "_3";
        case 19:
        case 22:
          return "_4";
        case 20:
        case 21:
          return "_5";
        default:
          return "";
        }
      } else {
        return "";
      }
    }

  },

  // Note maximum total armor value that has any effect at toughness 4*4 for damage=20 is 24. End steel comes up to 25.

  // Or the other way around: End steel has maximum protection (80%) up to 30 damage.

  // Note: The ISpecialArmor ratio removes 25% (end)/50% (empowered end) of damage before the armor value is applied.

  ;

  private final @Nonnull ArmorMaterial armorMaterial, armorMaterialEmpowered;
  private final @Nonnull ToolMaterial toolMaterial;
  private final @Nonnull String repairIngotOredict, bowRepairIngotOredict;
  private final @Nonnull String texture1, texture2;
  private final @Nonnull Integer tier;

  private EquipmentData(int maxDamageFactor, @Nonnull int[] damageReduction, @Nonnull int[] damageReductionEmpowered, int armorEnchantability,
      SoundEvent soundEvent, float toughness, float toughnessEmpowered, String toolMaterialName, int harvestLevel, int maxUses, float efficiency, float damage,
      int toolEnchanability, @Nonnull String repairIngotOredict, @Nonnull String bowRepairIngotOredict, @Nonnull String texture1, @Nonnull String texture2,
      @Nonnull Integer tier) {

    this.armorMaterial = NullHelper.notnullF(
        EnumHelper.addArmorMaterial(name(), name(), maxDamageFactor, damageReduction, armorEnchantability, soundEvent, toughness),
        "Failed to create armor material");
    this.armorMaterialEmpowered = NullHelper.notnullF(EnumHelper.addArmorMaterial(name() + "_EMPOWERED", name() + "_EMPOWERED", maxDamageFactor,
        damageReductionEmpowered, armorEnchantability, soundEvent, toughnessEmpowered), "Failed to create armor material");
    this.toolMaterial = NullHelper.notnullF(EnumHelper.addToolMaterial(toolMaterialName, harvestLevel, maxUses, efficiency, damage, toolEnchanability),
        "failed to add tool material dark steel");
    this.repairIngotOredict = repairIngotOredict;
    this.bowRepairIngotOredict = bowRepairIngotOredict;
    this.texture1 = EnderIO.DOMAIN + ":textures/models/armor/" + texture1;
    this.texture2 = EnderIO.DOMAIN + ":textures/models/armor/" + texture2;
    this.tier = tier;
  }

  private EquipmentData(@Nonnull ArmorMaterial armorMaterial, @Nonnull ArmorMaterial armorMaterialEmpowered, @Nonnull ToolMaterial toolMaterial,
      @Nonnull String repairIngotOredict, @Nonnull String bowRepairIngotOredict, @Nonnull String texture1, @Nonnull String texture2, @Nonnull Integer tier) {
    this.armorMaterial = armorMaterial;
    this.armorMaterialEmpowered = armorMaterialEmpowered;
    this.toolMaterial = toolMaterial;
    this.repairIngotOredict = repairIngotOredict;
    this.bowRepairIngotOredict = bowRepairIngotOredict;
    this.texture1 = texture1;
    this.texture2 = texture2;
    this.tier = tier;
  }

  @Override
  public @Nonnull ArmorMaterial getArmorMaterial() {
    return armorMaterial;
  }

  @Override
  public @Nonnull ArmorMaterial getArmorMaterialEmpowered() {
    return armorMaterialEmpowered;
  }

  @Override
  public @Nonnull ToolMaterial getToolMaterial() {
    return toolMaterial;
  }

  @Override
  public @Nonnull String getRepairIngotOredict() {
    return repairIngotOredict;
  }

  @Override
  public @Nonnull String getTexture1() {
    return texture1 + getAnimationFrame() + ".png";
  }

  @Override
  public @Nonnull String getTexture2() {
    return texture2 + getAnimationFrame() + ".png";
  }

  protected @Nonnull String getAnimationFrame() {
    return "";
  }

  @Override
  public @Nonnull Integer getTier() {
    return tier;
  }

  @Override
  @Nonnull
  public String getBowRepairIngotOredict() {
    return bowRepairIngotOredict;
  }

}
