package com.enderio.base.common.enchantments;

import java.util.function.Predicate;

import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.FishingRodItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

public class EIOEnchantmentCategories {

    public static final EnchantmentCategory XPBOOST = EnchantmentCategory.create("EIO_XPBOOST", new Predicate<Item>() {

        @Override
        public boolean test(Item t) {
            return new ItemStack(t).isDamageableItem() && !(t instanceof ArmorItem) && !(t instanceof FishingRodItem);
        }
    });

    public static final EnchantmentCategory ARROW = EnchantmentCategory.create("EIO_ARROW", new Predicate<Item>() {

        @Override
        public boolean test(Item t) {
            return t instanceof CrossbowItem || t instanceof BowItem;
        }
    });
}
