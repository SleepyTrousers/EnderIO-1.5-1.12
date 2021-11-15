package com.enderio.base.common.recipe.grindingball;

import net.minecraft.world.item.crafting.Recipe;
import net.minecraftforge.items.wrapper.RecipeWrapper;

public interface IGrindingballRecipe extends Recipe<RecipeWrapper>{

    public float getGrinding();
    
    public float getChance();
    
    public float getPower();
    
    public int getDurability();
    
}
