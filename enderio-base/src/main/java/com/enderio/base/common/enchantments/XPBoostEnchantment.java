package com.enderio.base.common.enchantments;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;

public class XPBoostEnchantment extends EIOBaseEnchantment {

    //TODO config
    public XPBoostEnchantment() {
        super(Rarity.COMMON, EIOEnchantmentCategories.XPBOOST,
                new EquipmentSlot[] { EquipmentSlot.MAINHAND, EquipmentSlot.OFFHAND }, () -> true);
    }

    @Override
    public int getMaxLevel() {
        return 3;
    }

    //TODO config?
    @Override
    public int getMaxCost(int pLevel) {
        return super.getMaxCost(pLevel) + 30;
    }

    //TODO config
    @Override
    public int getMinCost(int pLevel) {
        return super.getMinCost(pLevel);
    }

    @Override
    protected boolean checkCompatibility(Enchantment pOther) {
        return super.checkCompatibility(pOther) && pOther != Enchantments.SILK_TOUCH;
    }
}
