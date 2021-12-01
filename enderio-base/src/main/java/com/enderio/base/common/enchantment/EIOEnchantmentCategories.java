package com.enderio.base.common.enchantment;

import net.minecraft.world.item.*;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

public class EIOEnchantmentCategories {
    public static final EnchantmentCategory XPBOOST = EnchantmentCategory.create("EIO_XPBOOST",
        t -> new ItemStack(t).isDamageableItem() && !(t instanceof ArmorItem) && !(t instanceof FishingRodItem));

    public static final EnchantmentCategory ARROW = EnchantmentCategory.create("EIO_ARROW", t -> t instanceof BowItem);
    public static final EnchantmentCategory BOLT = EnchantmentCategory.create("EIO_BOLT", t -> t instanceof CrossbowItem);
}
