package crazypants.enderio.base.item.darksteel.attributes;

import javax.annotation.Nonnull;

import com.enderio.core.common.util.NullHelper;

import crazypants.enderio.api.upgrades.IEquipmentData;
import crazypants.enderio.base.EnderIO;
import crazypants.enderio.base.config.Config;
import crazypants.enderio.base.config.config.PersonalConfig;
import crazypants.enderio.base.material.alloy.Alloy;
import crazypants.enderio.base.material.alloy.endergy.AlloyEndergy;
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

  DARK_STEEL(new Builder(1).setMaxDamageFactor(35).setDamageReduction(2, 5, 6, 2).setDamageReductionEmpowered(3, 6, 8, 3).setArmorEnchantability(15)
      .setSoundEvent(SoundEvents.ITEM_ARMOR_EQUIP_IRON).setToughness(1f).setToughnessEmpowered(2f).setToolMaterialName("darkSteel")
      .setHarvestLevel(Config.darkSteelPickMinesTiCArdite ? 5 : 3).setMaxUses(2000).setEfficiency(8).setDamage(3.0001f).setToolEnchanability(25)
      .setRepairIngotOredict(Alloy.DARK_STEEL.getOreIngot()).setBowRepairIngotOredict(Material.NUTRITIOUS_STICK.getOreDict()).setTexture1("dark_steel_layer_1")
      .setTexture2("dark_steel_layer_2")),

  END_STEEL(new Builder(2).setMaxDamageFactor(50).setDamageReduction(4, 7, 10, 4).setDamageReductionEmpowered(5, 8, 12, 5).setArmorEnchantability(25)
      .setSoundEvent(SoundEvents.ITEM_ARMOR_EQUIP_IRON).setToughness(3f).setToughnessEmpowered(4f).setToolMaterialName("endSteel")
      .setHarvestLevel(Config.darkSteelPickMinesTiCArdite ? 5 : 3).setMaxUses(2000).setEfficiency(12).setDamage(5f).setToolEnchanability(30)
      .setRepairIngotOredict(Alloy.END_STEEL.getOreIngot()).setBowRepairIngotOredict(Material.INFINITY_ROD.getOreDict()).setTexture1("end_steel_layer_1")
      .setTexture2("end_steel_layer_2")) {

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
  // 10, 15, 22, 10 - 3, 6, 8, 3 =
  // 57 - 20 = 37 * 5% = 185%
  STELLAR_ALLOY(new Builder(4).setMaxDamageFactor(75).setDamageReduction(8, 14, 18, 7).setDamageReductionEmpowered(10, 15, 22, 10).setArmorEnchantability(25)
      .setSoundEvent(SoundEvents.ITEM_ARMOR_EQUIP_DIAMOND).setToughness(7f).setToughnessEmpowered(9f).setToolMaterialName("stellarAlloy")
      .setHarvestLevel(Config.darkSteelPickMinesTiCArdite ? 5 : 3).setMaxUses(5000).setEfficiency(16).setDamage(11f).setToolEnchanability(25)
      .setRepairIngotOredict(AlloyEndergy.STELLAR_ALLOY.getOreIngot()).setBowRepairIngotOredict(AlloyEndergy.STELLAR_ALLOY.getOreIngot())
      .setTexture1("stellar_alloy_layer_1").setTexture2("stellar_alloy_layer_2")),

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

  private EquipmentData(@Nonnull Builder b) {
    this( //
        NullHelper.notnull(b.maxDamageFactor, "maxDamageFactor", " must be set"), //
        NullHelper.notnull(b.damageReduction, "damageReduction", " must be set"), //
        NullHelper.notnull(b.damageReductionEmpowered, "damageReductionEmpowered", " must be set"), //
        NullHelper.notnull(b.armorEnchantability, "armorEnchantability", " must be set"), //
        NullHelper.notnull(b.soundEvent, "soundEvent", " must be set"), //
        NullHelper.notnull(b.toughness, "toughness", " must be set"), //
        NullHelper.notnull(b.toughnessEmpowered, "toughnessEmpowered", " must be set"), //
        NullHelper.notnull(b.toolMaterialName, "toolMaterialName", " must be set"), //
        NullHelper.notnull(b.harvestLevel, "harvestLevel", " must be set"), //
        NullHelper.notnull(b.maxUses, "maxUses", " must be set"), //
        NullHelper.notnull(b.efficiency, "efficiency", " must be set"), //
        NullHelper.notnull(b.damage, "damage", " must be set"), //
        NullHelper.notnull(b.toolEnchanability, "toolEnchanability", " must be set"), //
        NullHelper.notnull(b.repairIngotOredict, "repairIngotOredict", " must be set"), //
        NullHelper.notnull(b.bowRepairIngotOredict, "bowRepairIngotOredict", " must be set"), //
        NullHelper.notnull(b.texture1, "texture1", " must be set"), //
        NullHelper.notnull(b.texture2, "texture2", " must be set"), //
        NullHelper.notnull(b.tier, "tier", " must be set") //
    );
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

  private final static class Builder {

    Integer maxDamageFactor;
    int[] damageReduction;
    int[] damageReductionEmpowered;
    Integer armorEnchantability;
    SoundEvent soundEvent;
    Float toughness;
    Float toughnessEmpowered;
    String toolMaterialName;
    Integer harvestLevel;
    Integer maxUses;
    Float efficiency;
    Float damage;
    Integer toolEnchanability;
    String repairIngotOredict;
    String bowRepairIngotOredict;
    String texture1;
    String texture2;
    Integer tier;

    @Nonnull
    Builder setMaxDamageFactor(int maxDamageFactor) {
      this.maxDamageFactor = maxDamageFactor;
      return this;
    }

    @Nonnull
    Builder setDamageReduction(int... damageReduction) {
      if (damageReduction.length != 4) {
        throw new RuntimeException("damageReduction must have 4 elements");
      }
      this.damageReduction = damageReduction;
      return this;
    }

    @Nonnull
    Builder setDamageReductionEmpowered(int... damageReductionEmpowered) {
      if (damageReductionEmpowered.length != 4) {
        throw new RuntimeException("damageReductionEmpowered must have 4 elements");
      }
      this.damageReductionEmpowered = damageReductionEmpowered;
      return this;
    }

    @Nonnull
    Builder setArmorEnchantability(int armorEnchantability) {
      this.armorEnchantability = armorEnchantability;
      return this;
    }

    @Nonnull
    Builder setSoundEvent(@Nonnull SoundEvent soundEvent) {
      this.soundEvent = soundEvent;
      return this;
    }

    @Nonnull
    Builder setToughness(float toughness) {
      this.toughness = toughness;
      return this;
    }

    @Nonnull
    Builder setToughnessEmpowered(float toughnessEmpowered) {
      this.toughnessEmpowered = toughnessEmpowered;
      return this;
    }

    @Nonnull
    Builder setToolMaterialName(@Nonnull String toolMaterialName) {
      this.toolMaterialName = toolMaterialName;
      return this;
    }

    @Nonnull
    Builder setHarvestLevel(int harvestLevel) {
      this.harvestLevel = harvestLevel;
      return this;
    }

    @Nonnull
    Builder setMaxUses(int maxUses) {
      this.maxUses = maxUses;
      return this;
    }

    @Nonnull
    Builder setEfficiency(float efficiency) {
      this.efficiency = efficiency;
      return this;
    }

    @Nonnull
    Builder setDamage(float damage) {
      this.damage = damage;
      return this;
    }

    @Nonnull
    Builder setToolEnchanability(int toolEnchanability) {
      this.toolEnchanability = toolEnchanability;
      return this;
    }

    @Nonnull
    Builder setRepairIngotOredict(@Nonnull String repairIngotOredict) {
      this.repairIngotOredict = repairIngotOredict;
      return this;
    }

    @Nonnull
    Builder setBowRepairIngotOredict(@Nonnull String bowRepairIngotOredict) {
      this.bowRepairIngotOredict = bowRepairIngotOredict;
      return this;
    }

    @Nonnull
    Builder setTexture1(@Nonnull String texture1) {
      this.texture1 = texture1;
      return this;
    }

    @Nonnull
    Builder setTexture2(@Nonnull String texture2) {
      this.texture2 = texture2;
      return this;
    }

    Builder(int tier) {
      this.tier = tier;
    }

  }

}
