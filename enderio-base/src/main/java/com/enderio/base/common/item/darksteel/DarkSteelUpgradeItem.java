package com.enderio.base.common.item.darksteel;

import com.enderio.base.common.capability.darksteel.IDarkSteelUpgrade;
import com.enderio.base.common.item.darksteel.upgrades.DarkSteelUpgradeRegistry;
import com.enderio.base.common.lang.EIOLang;
import net.minecraft.Util;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
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
        if (!DarkSteelUpgradeRegistry.instance().hasUpgrade(stack)) {
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

    @Override
    public void appendHoverText(@Nonnull ItemStack pStack, @Nullable Level pLevel, @Nonnull List<Component> pTooltipComponents,
        @Nonnull TooltipFlag pIsAdvanced) {
        super.appendHoverText(pStack, pLevel, pTooltipComponents, pIsAdvanced);

        //TODO: Only show when shift is held down
        if(!DarkSteelUpgradeRegistry.instance().hasUpgrade(pStack)) {
            pTooltipComponents.add(new TranslatableComponent(EIOLang.DS_UPGRADE_XP_COST.getKey(), levelsRequired));
            pTooltipComponents.add(EIOLang.DS_UPGRADE_ACTIVATE);
        }
    }

}
