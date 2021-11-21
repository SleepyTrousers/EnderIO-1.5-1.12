package com.enderio.base.common.recipe.grindingball;

import net.minecraft.world.Container;
import net.minecraft.world.item.crafting.Recipe;

public interface IGrindingballRecipe extends Recipe<Container>{

    public float getGrinding();
    
    public float getChance();
    
    public float getPower();
    
    public int getDurability();
    
}
