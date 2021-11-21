package com.enderio.base.common.item.tool;

import com.enderio.base.config.base.BaseConfig;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import javax.annotation.Nonnull;

public class LevitationStaffItem extends PoweredToggledItem {
    public LevitationStaffItem(Properties pProperties) {
        super(pProperties);
    }

    @Override
    protected int getEnergyUse() {
        return BaseConfig.COMMON.ITEMS.LEVITATION_STAFF_ENERGY_USE.get();
    }

    @Override
    protected int getMaxEnergy() {
        return BaseConfig.COMMON.ITEMS.LEVITATION_STAFF_MAX_ENERGY.get();
    }

    @Override
    protected boolean hasEnergy(ItemStack pStack) {
        //TODO: Check fluid tank.
        return super.hasEnergy(pStack);
    }

    @Override
    protected void useEnergy(ItemStack pStack) {
        super.useEnergy(pStack);
        //TODO: Use fluid
    }

    @Override
    protected void onTickWhenActive(Player player, @Nonnull ItemStack pStack, @Nonnull Level pLevel, @Nonnull Entity pEntity, int pSlotId, boolean pIsSelected) {
        player.addEffect(new MobEffectInstance(MobEffects.LEVITATION, 1)); // TODO: An upgrade to make it faster?
    }

}
