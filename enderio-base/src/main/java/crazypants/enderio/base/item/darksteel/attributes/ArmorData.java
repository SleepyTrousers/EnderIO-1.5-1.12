package crazypants.enderio.base.item.darksteel.attributes;

import javax.annotation.Nonnull;

import com.enderio.core.common.util.NullHelper;

import crazypants.enderio.base.EnderIO;
import crazypants.enderio.base.material.alloy.Alloy;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemArmor.ArmorMaterial;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public enum ArmorData {
  DARK_STEEL(35, new int[] { 2, 5, 6, 2 }, new int[] { 3, 6, 8, 3 }, 15, SoundEvents.ITEM_ARMOR_EQUIP_IRON, 1f, 2f, Alloy.DARK_STEEL.getOreIngot(),
      "dark_steel_layer_1", "dark_steel_layer_2"),

  END_STEEL(50, new int[] { 4, 7, 10, 4 }, new int[] { 5, 8, 12, 5 }, 25, SoundEvents.ITEM_ARMOR_EQUIP_IRON, 3f, 4f, Alloy.END_STEEL.getOreIngot(),
      "end_steel_layer_1", "end_steel_layer_2") {

    @Override
    @SideOnly(Side.CLIENT)
    protected @Nonnull String getAnimationFrame() {
      // TODO config flag to disable this
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
    }

  },

  // Note maximum total armor value that has any effect at toughness 4*4 for damage=20 is 24. End steel comes up to 25.

  // Or the other way around: End steel has maximum protection (80%) up to 30 damage.

  // Note: The ISpecialArmor ratio removes 25% (end)/50% (empowered end) of damage before the armor value is applied.

  ;

  private final @Nonnull ArmorMaterial material, materialEmpowered;
  private final @Nonnull String repairIngotOredict;
  private final @Nonnull String texture1, texture2;

  private ArmorData(int maxDamageFactor, @Nonnull int[] damageReduction, @Nonnull int[] damageReductionEmpowered, int enchantability, SoundEvent soundEvent,
      float toughness, float toughnessEmpowered, @Nonnull String repairIngotOredict, @Nonnull String texture1, @Nonnull String texture2) {
    this.material = NullHelper.notnullF(
        EnumHelper.addEnum(ArmorMaterial.class, name(), new Class<?>[] { String.class, int.class, int[].class, int.class, SoundEvent.class, float.class },
            name(), maxDamageFactor, damageReduction, enchantability, soundEvent, toughness),
        "Failed to create armor material");
    this.materialEmpowered = NullHelper.notnullF(EnumHelper.addEnum(ArmorMaterial.class, name() + "_EMPOWERED",
        new Class<?>[] { String.class, int.class, int[].class, int.class, SoundEvent.class, float.class }, name() + "_EMPOWERED", maxDamageFactor,
        damageReductionEmpowered, enchantability, soundEvent, toughnessEmpowered), "Failed to create armor material");
    this.repairIngotOredict = repairIngotOredict;
    this.texture1 = EnderIO.DOMAIN + ":textures/models/armor/" + texture1;
    this.texture2 = EnderIO.DOMAIN + ":textures/models/armor/" + texture2;
  }

  public @Nonnull ArmorMaterial getMaterial() {
    return material;
  }

  public @Nonnull ArmorMaterial getMaterialEmpowered() {
    return materialEmpowered;
  }

  public @Nonnull String getRepairIngotOredict() {
    return repairIngotOredict;
  }

  public @Nonnull String getTexture1() {
    return texture1 + getAnimationFrame() + ".png";
  }

  public @Nonnull String getTexture2() {
    return texture2 + getAnimationFrame() + ".png";
  }

  protected @Nonnull String getAnimationFrame() {
    return "";
  }
}
