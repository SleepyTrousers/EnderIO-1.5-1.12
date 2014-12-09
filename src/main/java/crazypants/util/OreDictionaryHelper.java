package crazypants.util;

import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

public final class OreDictionaryHelper {

  public static final String INGOT_COPPER = "ingotCopper";
  public static final String INGOT_TIN = "ingotTin";
  public static final String DUST_ENDERPEARL = "dustEnderPearl";

  public static boolean isRegistered(String name) {
    if(!getOres(name).isEmpty()) {
      return true;
    }
    return false;
  }

  public static List<ItemStack> getOres(String name) {
    return OreDictionary.getOres(name);
  }

  public static boolean hasCopper() {
    return isRegistered(INGOT_COPPER);
  }

  public static boolean hasTin() {
    return isRegistered(INGOT_TIN);
  }

  public static boolean hasEnderPearlDust() {
    return isRegistered(DUST_ENDERPEARL);
  }

  public static ItemStack getPreffered(String oreDictName) {
    List<ItemStack> ores = getOres(oreDictName);
    if(ores.isEmpty()) {
      return null;
    }
    return ores.get(0);
  }

  private OreDictionaryHelper() {
  }

}
