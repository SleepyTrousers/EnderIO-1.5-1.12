package com.enderio.base.common.enchantments;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.enchantment.EnchantmentHelper;

public class WitherArrowEnchantment extends EIOBaseEnchantment {

    //TODO config rarity?
    public WitherArrowEnchantment() {
        super(Rarity.UNCOMMON, EIOEnchantmentCategories.ARROW, new EquipmentSlot[] { EquipmentSlot.MAINHAND },
                () -> true);
    }

    @Override
    public int getMaxLevel() {
        return 1;
    }

    //TODO config?
    @Override
    public int getMaxCost(int pLevel) {
        return 50;
    }

    @Override
    public int getMinCost(int pLevel) {
        return 20;
    }

    @Override
    public void doPostAttack(LivingEntity pAttacker, Entity pTarget, int pLevel) {
        if (pTarget instanceof LivingEntity target
                && EnchantmentHelper.getEnchantments(pAttacker.getMainHandItem()).containsKey(this)) {
            target.addEffect(new MobEffectInstance(MobEffects.WITHER, 200));
        }
    }
}
