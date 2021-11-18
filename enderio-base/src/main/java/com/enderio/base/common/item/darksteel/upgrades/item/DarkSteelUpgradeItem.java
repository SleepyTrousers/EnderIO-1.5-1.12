package com.enderio.base.common.item.darksteel.upgrades.item;

import com.enderio.base.common.capability.darksteel.IDarkSteelUpgrade;
import com.enderio.base.common.item.darksteel.upgrades.DarkSteelUpgradeRegistry;
import com.enderio.base.common.lang.EIOLang;
import net.minecraft.Util;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.Random;
import java.util.function.Supplier;

public class DarkSteelUpgradeItem extends Item {

    private final int levelsRequired;

    private final Supplier<? extends IDarkSteelUpgrade> upgrade;

    public DarkSteelUpgradeItem(Properties pProperties, int levelsRequired, Supplier<? extends IDarkSteelUpgrade> upgrade) {
        super(pProperties.stacksTo(1));
        this.levelsRequired = levelsRequired;
        this.upgrade = upgrade;
    }

    @Override
    public boolean isFoil(ItemStack pStack) {
        return DarkSteelUpgradeRegistry.instance().hasUpgrade(pStack);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pUsedHand) {
        ItemStack stack = pPlayer.getItemInHand(pUsedHand);
        if (pPlayer.isCrouching() && !DarkSteelUpgradeRegistry.instance().hasUpgrade(stack)) {
            if(pPlayer.experienceLevel >= levelsRequired || pPlayer.isCreative()) {
                if(!pPlayer.isCreative()) {
                    pPlayer.giveExperienceLevels(-levelsRequired);
                }
                DarkSteelUpgradeRegistry.instance().writeUpgradeToItemStack(stack, upgrade.get());
                pLevel.playSound(pPlayer, pPlayer.getOnPos(), SoundEvents.ENCHANTMENT_TABLE_USE, SoundSource.BLOCKS, 1.0F, new Random().nextFloat() * 0.1F + 0.9F);
            } else if(pLevel.isClientSide){
                pPlayer.sendMessage(EIOLang.DS_UPGRADE_ITEM_NO_XP, Util.NIL_UUID);
            }

            return InteractionResultHolder.consume(stack);
        }
        return super.use(pLevel, pPlayer, pUsedHand);
    }
}
