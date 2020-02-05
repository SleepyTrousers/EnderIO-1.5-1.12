package crazypants.enderio.material;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

import org.apache.commons.lang3.StringUtils;

import crazypants.enderio.EnderIO;

public enum Material {

  SILICON("silicon"),
  CONDUIT_BINDER("conduitBinder"),
  BINDER_COMPOSITE("binderComposite"),
  PHASED_IRON_NUGGET("phasedIronNugget"),
  VIBRANT_NUGGET("vibrantNugget"),
  PULSATING_CYSTAL("pulsatingCrystal", true),
  VIBRANT_CYSTAL("vibrantCrystal", true),
  DARK_GRINDING_BALL("darkGrindingBall"),
  ENDER_CRYSTAL("enderCrystal", true),
  ATTRACTOR_CRYSTAL("attractorCrystal", true),
  WEATHER_CRYSTAL("weatherCrystal", true),
  END_STEEL_NUGGET("endSteelNugget"),
  ELECTRICAL_GRINDING_BALL("electricalGrindingBall"),
  ENERGETIC_GRINDING_BALL("energeticGrindingBall"),
  VIBRANT_GRINDING_BALL("vibrantGrindingBall"),
  REDSTONE_GRINDING_BALL("redstoneGrindingBall"),
  CONDUCTIVE_GRINDING_BALL("conductiveGrindingBall"),
  PULSATING_GRINDING_BALL("pulsatingGrindingBall"),
  SOULARIUM_GRINDING_BALL("soulariumGrindingBall"),
  END_STEEL_GRINDING_BALL("endSteelGrindingBall"),
  DARK_STEEL_ROD("darkSteelRod");



  public final String unlocalisedName;
  public final String iconKey;
  public final String oreDict;
  public final boolean hasEffect;

  private Material(String unlocalisedName) {
    this(unlocalisedName, false);
  }

  private Material(String unlocalisedName, boolean hasEffect) {
    this.unlocalisedName = "enderio." + unlocalisedName;
    this.iconKey = "enderio:" + unlocalisedName;
    this.hasEffect = hasEffect;
    this.oreDict = "item" + StringUtils.capitalize(unlocalisedName);
  }

  public static void registerOres(Item item) {
    for (Material m : values()) {
      OreDictionary.registerOre(m.oreDict, new ItemStack(item, 1, m.ordinal()));
    }
  }

  public ItemStack getStack() {
    return getStack(1);
  }

  public ItemStack getStack(int size) {
    return new ItemStack(EnderIO.itemMaterial, size, ordinal());
  }
}
