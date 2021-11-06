package com.enderio.base.common.item.tool;

import com.enderio.base.common.capability.EIOCapabilities;
import com.enderio.base.common.capability.toggled.Toggled;
import com.enderio.base.common.item.util.IEnergyBar;
import com.enderio.core.common.capability.IMultiCapabilityItem;
import com.enderio.core.common.capability.MultiCapabilityProvider;
import com.enderio.core.common.util.EnergyUtil;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.EnergyStorage;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public abstract class PoweredToggledItem extends Item implements IEnergyBar, IMultiCapabilityItem {

    public PoweredToggledItem(Properties pProperties) {
        super(pProperties.stacksTo(1));
    }

    protected abstract void onTickWhenActive(Player player, @Nonnull ItemStack pStack, @Nonnull Level pLevel, @Nonnull Entity pEntity, int pSlotId,
        boolean pIsSelected);

    protected abstract int getEnergyUse();

    protected abstract int getMaxEnergy();

    protected void enable(ItemStack stack) {
        Toggled.setEnabled(stack, true);
    }

    protected void disable(ItemStack stack) {
        Toggled.setEnabled(stack, false);
    }

    protected boolean hasEnergy(ItemStack pStack) {
        return EnergyUtil.extractEnergy(pStack, getEnergyUse(), true) > 0;
    }

    protected void useEnergy(ItemStack pStack) {
        EnergyUtil.extractEnergy(pStack, getEnergyUse(), false);
    }

    @Override
    public boolean isFoil(ItemStack pStack) {
        return Toggled.isEnabled(pStack);
    }

    @Override
    public void fillItemCategory(@Nonnull CreativeModeTab pCategory, @Nonnull NonNullList<ItemStack> pItems) {
        if (allowdedIn(pCategory)) {
            ItemStack is = new ItemStack(this);
            pItems.add(is.copy());

            EnergyUtil.setFull(is);
            pItems.add(is);
        }
    }

    @Nonnull
    @Override
    public InteractionResultHolder<ItemStack> use(@Nonnull Level pLevel, Player pPlayer, @Nonnull InteractionHand pUsedHand) {
        if (pPlayer.isCrouching()) {
            ItemStack stack = pPlayer.getItemInHand(pUsedHand);
            if (Toggled.isEnabled(stack)) {
                disable(stack);
            } else if (hasEnergy(stack)) {
                enable(stack);
            }
        }
        return super.use(pLevel, pPlayer, pUsedHand);
    }

    @Override
    public void inventoryTick(@Nonnull ItemStack pStack, @Nonnull Level pLevel, @Nonnull Entity pEntity, int pSlotId, boolean pIsSelected) {
        if (pEntity instanceof Player player) {
            if (Toggled.isEnabled(pStack)) {
                if (hasEnergy(pStack)) {
                    useEnergy(pStack);
                    onTickWhenActive(player, pStack, pLevel, pEntity, pSlotId, pIsSelected);
                } else {
                    disable(pStack);
                }
            }
        }
    }

    @Nullable
    @Override
    public MultiCapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt, MultiCapabilityProvider provider) {
        provider.addSerialized(EIOCapabilities.TOGGLED, LazyOptional.of(Toggled::new));
        provider.addSerialized("Energy", CapabilityEnergy.ENERGY, LazyOptional.of(() -> new EnergyStorage(getMaxEnergy())));
        return provider;
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        if (slotChanged) {
            return super.shouldCauseReequipAnimation(oldStack, newStack, true);
        }
        return oldStack.getItem() != newStack.getItem() || Toggled.isEnabled(oldStack) != Toggled.isEnabled(newStack);
    }

}
