package com.enderio.base.common.enchantments;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

public class ShimmerEnchantment extends EIOBaseEnchantment {

    //TODO config
    public ShimmerEnchantment() {
        super(Rarity.VERY_RARE, EnchantmentCategory.VANISHABLE, EquipmentSlot.values(), () -> true);
    }

    @Override
    public int getMaxLevel() {
        return 1;
    }

    //TODO config?
    @Override
    public int getMaxCost(int pLevel) {
        return 100;
    }

    //TODO config?
    @Override
    public int getMinCost(int pLevel) {
        return 1;
    }

    @Override
    public boolean isCurse() {
        return true;
    }

    @Override
    public boolean isTreasureOnly() {
        return true;
    }

    @Override
    public boolean isAllowedOnBooks() {
        return false;
    }
}
