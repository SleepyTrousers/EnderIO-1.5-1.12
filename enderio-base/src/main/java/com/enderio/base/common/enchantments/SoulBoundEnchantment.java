package com.enderio.base.common.enchantments;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

public class SoulBoundEnchantment extends EIOBaseEnchantment {

    //TODO config rarity?
    public SoulBoundEnchantment() {
        super(Rarity.VERY_RARE, EnchantmentCategory.VANISHABLE, EquipmentSlot.values(), () -> true);
    }

    @Override
    public int getMaxLevel() {
        return 1;
    }

    //TODO config?
    @Override
    public int getMaxCost(int pLevel) {
        return 60;
    }

    //TODO config
    @Override
    public int getMinCost(int pLevel) {
        return 16;
    }
}
