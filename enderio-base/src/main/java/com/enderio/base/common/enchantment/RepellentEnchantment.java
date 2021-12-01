package com.enderio.base.common.enchantment;

import com.enderio.base.config.base.BaseConfig;
import com.enderio.core.common.util.TeleportUtils;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

public class RepellentEnchantment extends EIOBaseEnchantment {
    public RepellentEnchantment() {
        super(BaseConfig.COMMON.ENCHANTMENTS.REPELLENT_RARITY.get(), EnchantmentCategory.ARMOR,
            new EquipmentSlot[] { EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET }, () -> true);
    }

    @Override
    public int getMaxLevel() {
        return BaseConfig.COMMON.ENCHANTMENTS.REPELLENT_MAX_LEVEL.get();
    }

    @Override
    public int getMaxCost(int pLevel) {
        return BaseConfig.COMMON.ENCHANTMENTS.REPELLENT_MAX_COST_BASE.get() + BaseConfig.COMMON.ENCHANTMENTS.REPELLENT_MAX_COST_MULT.get() * pLevel;
    }

    @Override
    public int getMinCost(int pLevel) {
        return BaseConfig.COMMON.ENCHANTMENTS.REPELLENT_MIN_COST_BASE.get() + BaseConfig.COMMON.ENCHANTMENTS.REPELLENT_MIN_COST_MULT.get() * pLevel;
    }

    private float getChance(int level) {
        return BaseConfig.COMMON.ENCHANTMENTS.REPELLENT_CHANCE_BASE.get() + BaseConfig.COMMON.ENCHANTMENTS.REPELLENT_CHANCE_MULT.get() * level;
    }

    private double getRange(int level) {
        return BaseConfig.COMMON.ENCHANTMENTS.REPELLENT_RANGE_BASE.get() + BaseConfig.COMMON.ENCHANTMENTS.REPELLENT_RANGE_MULT.get() * level;
    }

    @Override
    public void doPostHurt(LivingEntity pUser, Entity pAttacker, int pLevel) {
        if (pUser instanceof Player && pAttacker instanceof LivingEntity attacker) {
            if (pUser.getRandom().nextFloat() < getChance(pLevel)) {
                if (pAttacker instanceof Player) {
                    TeleportUtils.randomTeleport(attacker, getRange(pLevel));
                } else if (pUser.getRandom().nextFloat() < BaseConfig.COMMON.ENCHANTMENTS.REPELLENT_NON_PLAYER_CHANCE.get()) {
                    TeleportUtils.randomTeleport(attacker, getRange(pLevel));
                }
            }
        }
    }
}
