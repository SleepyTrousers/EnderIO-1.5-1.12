package crazypants.enderio.enchantment;

import crazypants.enderio.Log;
import crazypants.enderio.config.Config;
import net.minecraft.enchantment.Enchantment;

public class Enchantments {

    private static Enchantments instance;

    public static Enchantments getInstance() {
        if (instance == null) {
            instance = new Enchantments();
            instance.registerEnchantments();
        }
        return instance;
    }

    private EnchantmentSoulBound soulBound;

    private void registerEnchantments() {
        if (Config.enchantmentSoulBoundEnabled) {
            int id = Config.enchantmentSoulBoundId;
            if (id < 0) {
                id = getEmptyEnchantId();
            }
            if (id < 0) {
                Log.error("Could not find an empty enchantment ID to add enchanments");
                return;
            }
            soulBound = EnchantmentSoulBound.create(id);
        }
    }

    private int getEmptyEnchantId() {
        for (int i = 0; i < Enchantment.enchantmentsList.length; i++) {
            if (Enchantment.enchantmentsList[i] == null) {
                return i;
            }
        }
        return -1;
    }
}
