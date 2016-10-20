package crazypants.enderio.material;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import org.apache.commons.lang3.StringUtils;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.oredict.OreDictionary;

import static crazypants.enderio.ModObject.itemMaterial;

public enum Material {

  SILICON("silicon"),
  CONDUIT_BINDER("conduitBinder"),
  BINDER_COMPOSITE("binderComposite"),
  PULSATING_IRON_NUGGET("pulsatingIronNugget"),
  VIBRANT_NUGGET("vibrantNugget"),
  PULSATING_CYSTAL("pulsatingCrystal", true),
  VIBRANT_CYSTAL("vibrantCrystal", true),
  DARK_GRINDING_BALL("darkGrindingBall"),
  ENDER_CRYSTAL("enderCrystal", true),
  ATTRACTOR_CRYSTAL("attractorCrystal", true),
  WEATHER_CRYSTAL("weatherCrystal", true),
  VIBRANT_POWDER("vibrantPowder", true),
  PULSATING_POWDER("pulsatingPowder", true),
  ENDER_CYSTAL_POWDER("enderCrystalPowder", true),
  NUTRITIOUS_STICK("nutritiousStick", false),
  PRECIENT_CRYSTAL("precientCrystal", true),
  PRECIENT_POWDER("precientPowder", true),
  CHASSIPARTS("chassiParts", false);

  public static List<ResourceLocation> resources() {
    List<ResourceLocation> res = new ArrayList<ResourceLocation>(values().length);
    for(Material c : values()) {
      res.add(new ResourceLocation(c.iconKey));
    }
    return res;
  }
  
  public final @Nonnull String baseName;
  public final @Nonnull String unlocalisedName;
  public final @Nonnull String iconKey;
  public final @Nonnull String oreDict;
  public final boolean hasEffect;

  private Material(@Nonnull String unlocalisedName) {
    this(unlocalisedName, false);
  }

  private Material(@Nonnull String baseName, boolean hasEffect) {
    this.baseName = baseName;
    this.unlocalisedName = "enderio." + baseName;
    this.iconKey = "enderio:" + baseName;
    this.hasEffect = hasEffect;
    this.oreDict = "item" + StringUtils.capitalize(baseName);
  }

  public static void registerOres(@Nonnull Item item) {
    for (Material m : values()) {
      OreDictionary.registerOre(m.oreDict, new ItemStack(item, 1, m.ordinal()));
    }
  }

  public @Nonnull ItemStack getStack() {
    return getStack(1);
  }
  
  public @Nonnull ItemStack getStack(int size) {
    return new ItemStack(itemMaterial.getItem(), size, ordinal());
  }
}
