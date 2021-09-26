package com.enderio.base.common.enchantments;

import java.util.function.Supplier;

import javax.annotation.Nonnull;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

public class EIOBaseEnchantment extends Enchantment {
    private EnchantmentCategory category;
    protected final @Nonnull Supplier<Boolean> enableFlag;

    public EIOBaseEnchantment(Rarity pRarity, EnchantmentCategory pCategory, EquipmentSlot[] pApplicableSlots,
            @Nonnull Supplier<Boolean> flag) {
        super(pRarity, pCategory, pApplicableSlots);
        this.category = pCategory;
        this.enableFlag = flag;
    }

    public EnchantmentCategory getCategory() {
        return this.category;
    }

    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack) {
        return super.canApplyAtEnchantingTable(stack) && enableFlag.get();
    }

    @Override
    public boolean canEnchant(ItemStack pStack) {
        return super.canEnchant(pStack) && enableFlag.get();
    }

    @Override
    public boolean isAllowedOnBooks() {
        return super.isAllowedOnBooks() && enableFlag.get();
    }
}
