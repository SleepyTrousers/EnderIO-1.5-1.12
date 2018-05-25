package crazypants.enderio.base.potion;

import javax.annotation.Nonnull;

import net.minecraft.init.Items;
import net.minecraft.init.PotionTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionUtils;

public class PotionUtil {

  public static @Nonnull ItemStack getEmptyPotion(boolean isSplash) {
    // in 1.11.2 brewing must start with potiontypes water
    // this was created to mimic vanilla ItemPotion.getDefaultInstance()
    ItemStack res;
    if (isSplash) {
      res = PotionUtils.addPotionToItemStack(new ItemStack(Items.SPLASH_POTION), PotionTypes.WATER);
    } else {
      res = PotionUtils.addPotionToItemStack(new ItemStack(Items.POTIONITEM), PotionTypes.WATER);
    }
    return res;
  }

  public static @Nonnull ItemStack createHarmingPotion(boolean isAugmented, boolean isSplash) {
    ItemStack res = getEmptyPotion(isSplash);
    PotionUtils.addPotionToItemStack(res, PotionTypes.HARMING);
    return res;
  }

  public static @Nonnull ItemStack createWitherPotion(boolean isProlonged, boolean isSplash) {
    ItemStack res = getEmptyPotion(isSplash);
    if (isProlonged) {
      PotionUtils.addPotionToItemStack(res, PotionWithering.getWitheringlong());
    } else {
      PotionUtils.addPotionToItemStack(res, PotionWithering.getWithering());
    }
    return res;
  }

  public static @Nonnull ItemStack createHealthPotion(boolean isAugmented, boolean isSplash) {
    ItemStack res = getEmptyPotion(isSplash);
    if (isAugmented) {
      PotionUtils.addPotionToItemStack(res, PotionTypes.STRONG_HEALING);
    } else {
      PotionUtils.addPotionToItemStack(res, PotionTypes.HEALING);
    }
    return res;
  }

  public static @Nonnull ItemStack createRegenerationPotion(boolean isProlonged, boolean isAugmented, boolean isSplash) {
    ItemStack res = getEmptyPotion(isSplash);
    if (isAugmented) {
      PotionUtils.addPotionToItemStack(res, PotionTypes.STRONG_REGENERATION);
    } else if (isProlonged) {
      PotionUtils.addPotionToItemStack(res, PotionTypes.LONG_REGENERATION);
    } else {
      PotionUtils.addPotionToItemStack(res, PotionTypes.REGENERATION);
    }
    return res;
  }

  public static @Nonnull ItemStack createSwiftnessPotion(boolean isProlonged, boolean isSplash) {
    ItemStack res = getEmptyPotion(isSplash);
    if (isProlonged) {
      PotionUtils.addPotionToItemStack(res, PotionTypes.LONG_SWIFTNESS);
    } else {
      PotionUtils.addPotionToItemStack(res, PotionTypes.SWIFTNESS);
    }
    return res;
  }

  public static @Nonnull ItemStack createNightVisionPotion(boolean isProlonged, boolean isSplash) {
    ItemStack res = getEmptyPotion(isSplash);
    if (isProlonged) {
      PotionUtils.addPotionToItemStack(res, PotionTypes.LONG_NIGHT_VISION);
    } else {
      PotionUtils.addPotionToItemStack(res, PotionTypes.NIGHT_VISION);
    }
    return res;
  }

}
