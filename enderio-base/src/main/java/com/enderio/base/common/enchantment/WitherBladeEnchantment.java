package com.enderio.base.common.enchantment;

import com.enderio.base.config.base.BaseConfig;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.item.enchantment.EnchantmentHelper;

public class WitherBladeEnchantment extends EIOBaseEnchantment {

    public WitherBladeEnchantment() {
        super(BaseConfig.COMMON.ENCHANTMENTS.WITHERING_BLADE_RARITY.get(), EnchantmentCategory.WEAPON, new EquipmentSlot[] { EquipmentSlot.MAINHAND, EquipmentSlot.OFFHAND }, () -> true);
    }

    @Override
    public int getMaxLevel() {
        return 1;
    }

    @Override
    public int getMaxCost(int pLevel) {
        return BaseConfig.COMMON.ENCHANTMENTS.WITHERING_BLADE_MAX_COST.get();
    }

    public int getMinCost(int pLevel) {
        return BaseConfig.COMMON.ENCHANTMENTS.WITHERING_BLADE_MIN_COST.get();
    }

    @Override
    public void doPostAttack(LivingEntity pAttacker, Entity pTarget, int pLevel) {
        if (pTarget instanceof LivingEntity target && EnchantmentHelper
            .getEnchantments(pAttacker.getMainHandItem())
            .containsKey(this)) {
            target.addEffect(new MobEffectInstance(MobEffects.WITHER, 200));
        }
    }
}
