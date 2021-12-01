package com.enderio.base.common.enchantment;

import com.enderio.base.config.base.BaseConfig;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.item.enchantment.Enchantments;

import javax.annotation.Nonnull;

public class AutoSmeltEnchantment extends EIOBaseEnchantment {

    public AutoSmeltEnchantment() {
        super(BaseConfig.COMMON.ENCHANTMENTS.AUTO_SMELT_RARITY.get(), EnchantmentCategory.DIGGER, new EquipmentSlot[] { EquipmentSlot.MAINHAND }, () -> true);
    }

    @Override
    public int getMaxLevel() {
        return 1;
    }

    @Override
    public int getMaxCost(int pLevel) {
        return BaseConfig.COMMON.ENCHANTMENTS.AUTO_SMELT_MAX_COST.get();
    }

    @Override
    public int getMinCost(int pLevel) {
        return BaseConfig.COMMON.ENCHANTMENTS.AUTO_SMELT_MIN_COST.get();
    }

    @Override
    protected boolean checkCompatibility(@Nonnull Enchantment pOther) {
        return super.checkCompatibility(pOther) && pOther != Enchantments.SILK_TOUCH;
    }
}
