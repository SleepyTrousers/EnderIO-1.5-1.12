package com.enderio.base.common.item.food;

import com.enderio.core.common.util.TeleportUtils;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.BowlFoodItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import javax.annotation.Nonnull;

public class EnderiosItem extends BowlFoodItem {
    private static final FoodProperties properties = ((new FoodProperties.Builder())
        .nutrition(10)
        .saturationMod(0.8f)
        .build());

    public EnderiosItem(Properties pProperties) {
        super(pProperties.food(properties));
    }

    @Override
    public ItemStack finishUsingItem(@Nonnull ItemStack pStack, @Nonnull Level pLevel, @Nonnull LivingEntity pEntityLiving) {
        ItemStack itemStack = super.finishUsingItem(pStack, pLevel, pEntityLiving);
        TeleportUtils.randomTeleport(pEntityLiving, 16.0f); // TODO: Config
        return itemStack;
    }

    // TODO: Teleportation behaviours.
}
