package crazypants.enderio.material;

import javax.annotation.Nonnull;

import com.enderio.core.common.util.NNList;
import com.enderio.core.common.util.NNList.ShortCallback;
import com.enderio.core.common.util.NNMap;
import com.enderio.core.common.util.NullHelper;

import crazypants.util.Prep;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.oredict.OreDictionary;

public final class OreDictionaryPreferences {

  public static final OreDictionaryPreferences instance = new OreDictionaryPreferences();

  public static void init(@Nonnull FMLPostInitializationEvent event) {
    OreDictionaryPreferenceParser.loadConfig();
  }

  private NNMap<String, ItemStack> preferences = new NNMap.Default<String, ItemStack>(Prep.getEmpty());
  private NNMap<StackKey, ItemStack> stackCache = new NNMap.Default<StackKey, ItemStack>(Prep.getEmpty());

  public void setPreference(@Nonnull String oreDictName, @Nonnull ItemStack stack) {
    preferences.put(oreDictName, stack.copy());
  }

  public @Nonnull ItemStack getPreferred(@Nonnull final String oreDictName, boolean createIfNull) {
    if (!preferences.containsKey(oreDictName) && createIfNull) {
      final WildcardFilter filter = new WildcardFilter(oreDictName);
      if (!NNList.wrap(OreDictionary.getOres(oreDictName)).apply(filter)) {
        preferences.put(oreDictName, filter.getResult());
      }
    }
    return preferences.get(oreDictName);
  }

  public @Nonnull ItemStack getPreferred(@Nonnull ItemStack stack) {
    if (Prep.isInvalid(stack)) {
      return stack;
    }
    ItemStack result = Prep.getEmpty();
    StackKey key = new StackKey(stack);
    if (stackCache.containsKey(key)) {
      result = stackCache.get(key);
    } else {
      int[] ids = NullHelper.notnullF(OreDictionary.getOreIDs(stack), "OreDictionary.getOreIDs() returned 'null'");
      for (int i = 0; i < ids.length && Prep.isInvalid(result); i++) {
        String oreDict = NullHelper.notnullF(OreDictionary.getOreName(ids[i]), "invalid ore dictionary ID 'null'");
        result = getPreferred(oreDict, false);
        if (result.getMetadata() == OreDictionary.WILDCARD_VALUE) {
          result = Prep.getEmpty();
        }
      }
      stackCache.put(key, result);
    }
    return Prep.isInvalid(result) ? stack : result;
  }

  private class WildcardFilter implements ShortCallback<ItemStack> {
    private final String oreDictName;
    private ItemStack result = null;

    @Nonnull
    ItemStack getResult() {
      return NullHelper.first(result, Prep.getEmpty());
    }

    private WildcardFilter(String oreDictName) {
      this.oreDictName = oreDictName;
    }

    @Override
    public boolean apply(@Nonnull ItemStack ore) {
      if (ore.getMetadata() != OreDictionary.WILDCARD_VALUE) {
        preferences.put(oreDictName, ore);
        return true;
      } else if (result == null) {
        result = ore;
      }
      return false;
    }
  }

  private static class StackKey {

    private final @Nonnull Item item;
    private final int damage;

    StackKey(@Nonnull ItemStack stack) {
      item = stack.getItem();
      damage = stack.getItemDamage();
    }

    @Override
    public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + damage;
      result = prime * result + item.hashCode();
      return result;
    }

    @Override
    public boolean equals(Object obj) {
      if(this == obj) {
        return true;
      }
      if(obj == null) {
        return false;
      }
      if(getClass() != obj.getClass()) {
        return false;
      }
      StackKey other = (StackKey) obj;
      if(damage != other.damage) {
        return false;
      }
      if (!item.equals(other.item)) {
        return false;
      }
      return true;
    }

  }

}
