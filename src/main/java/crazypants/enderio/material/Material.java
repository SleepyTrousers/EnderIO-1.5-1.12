package crazypants.enderio.material;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import crazypants.enderio.EnderIO;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.oredict.OreDictionary;

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
  WEATHER_CRYSTAL("weatherCrystal", true);

  public static List<ResourceLocation> resources() {
    List<ResourceLocation> res = new ArrayList<ResourceLocation>(values().length);
    for(Material c : values()) {
      res.add(new ResourceLocation(c.iconKey));
    }
    return res;
  }
  
  public final String baseName;
  public final String unlocalisedName;
  public final String iconKey;
  public final String oreDict;
  public final boolean hasEffect;

  private Material(String unlocalisedName) {
    this(unlocalisedName, false);
  }

  private Material(String baseName, boolean hasEffect) {
    this.baseName = baseName;
    this.unlocalisedName = "enderio." + baseName;
    this.iconKey = "enderio:" + baseName;
    this.hasEffect = hasEffect;
    this.oreDict = "item" + StringUtils.capitalize(baseName);
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
