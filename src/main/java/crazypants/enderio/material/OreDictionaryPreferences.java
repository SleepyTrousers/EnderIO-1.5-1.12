package crazypants.enderio.material;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

import com.enderio.core.common.util.OreDictionaryHelper;

public final class OreDictionaryPreferences {

    public static final OreDictionaryPreferences instance = new OreDictionaryPreferences();

    public static void loadConfig() {
        OreDictionaryPreferenceParser.loadConfig();
    }

    private Map<String, ItemStack> preferences = new HashMap<String, ItemStack>();
    private Map<StackKey, ItemStack> stackCache = new HashMap<StackKey, ItemStack>();

    public void setPreference(String oreDictName, ItemStack stack) {
        if (oreDictName == null || stack == null) {
            return;
        }
        preferences.put(oreDictName, stack.copy());
    }

    public ItemStack getPreferred(String oreDictName) {
        ItemStack result = null;
        if (preferences.containsKey(oreDictName)) {
            result = preferences.get(oreDictName);
        } else {
            List<ItemStack> ores = OreDictionaryHelper.getOres(oreDictName);
            if (!ores.isEmpty() && ores.get(0) != null) {
                result = ores.get(0).copy();
            }
            preferences.put(oreDictName, result);
        }
        return result;
    }

    public ItemStack getPreferred(ItemStack stack) {
        if (stack == null || stack.getItem() == null) {
            return stack;
        }
        StackKey key = new StackKey(stack);
        if (stackCache.containsKey(key)) {
            return stackCache.get(key);
        }
        ItemStack result = null;
        int[] ids = OreDictionary.getOreIDs(stack);
        if (ids != null) {
            for (int i = 0; i < ids.length && result == null; i++) {
                String oreDict = OreDictionary.getOreName(ids[i]);
                if (preferences.containsKey(oreDict)) {
                    result = preferences.get(oreDict);
                }
            }
        }

        if (result == null) {
            result = stack.copy();
        }
        stackCache.put(key, result);
        return stack;
    }

    private static class StackKey {

        Item item;
        int damage;

        StackKey(ItemStack stack) {
            item = stack.getItem();
            damage = stack.getItemDamage();
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + damage;
            result = prime * result + ((item == null) ? 0 : item.hashCode());
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            StackKey other = (StackKey) obj;
            if (damage != other.damage) {
                return false;
            }
            if (item == null) {
                if (other.item != null) {
                    return false;
                }
            } else if (!item.equals(other.item)) {
                return false;
            }
            return true;
        }
    }
}
