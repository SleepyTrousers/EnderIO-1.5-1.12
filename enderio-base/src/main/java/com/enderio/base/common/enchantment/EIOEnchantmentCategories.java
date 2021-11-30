package com.enderio.base.common.enchantment;

import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.FishingRodItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

public class EIOEnchantmentCategories {
    public static final EnchantmentCategory XPBOOST = EnchantmentCategory.create("EIO_XPBOOST",
        t -> new ItemStack(t).isDamageableItem() && !(t instanceof ArmorItem) && !(t instanceof FishingRodItem));

    public static final EnchantmentCategory ARROW = EnchantmentCategory.create("EIO_ARROW", t -> t instanceof BowItem);
    public static final EnchantmentCategory BOLT = EnchantmentCategory.create("EIO_BOLT", t -> t instanceof CrossbowItem);
}
