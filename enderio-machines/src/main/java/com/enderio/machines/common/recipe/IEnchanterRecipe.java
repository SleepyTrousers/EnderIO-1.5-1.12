package com.enderio.machines.common.recipe;

import net.minecraft.world.Container;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.enchantment.Enchantment;

public interface IEnchanterRecipe extends Recipe<Container>{
    
    Enchantment getEnchantment();
    
    int getLevel();
}
