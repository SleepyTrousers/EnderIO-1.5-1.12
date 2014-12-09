package crazypants.enderio.material;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import crazypants.util.OreDictionaryHelper;

import net.minecraft.item.ItemStack;

public final class OreDictionaryPreferences {

  public static final OreDictionaryPreferences instance = new OreDictionaryPreferences();

  public static void loadConfig() {
    OreDictionaryPreferenceParser.loadConfig();
  }

  private Map<String, ItemStack> preferences = new HashMap<String, ItemStack>();

  public void setPreference(String oreDictName, ItemStack stack) {
    if(oreDictName == null || stack == null) {
      return;
    }
    preferences.put(oreDictName, stack.copy());
  }

  public ItemStack getPreferred(String oreDictName) {
    ItemStack result = null;
    if(preferences.containsKey(oreDictName)) {
      result = preferences.get(oreDictName);
    } else {
      List<ItemStack> ores = OreDictionaryHelper.getOres(oreDictName);
      if(!ores.isEmpty() && ores.get(0) != null) {
        result = ores.get(0).copy();
      }
      preferences.put(oreDictName, result);
    }
    return result;
  }

}
