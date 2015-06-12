package com.jaquadro.minecraft.storagedrawers.api;

import com.jaquadro.minecraft.storagedrawers.api.pack.IPackBlockFactory;
import com.jaquadro.minecraft.storagedrawers.api.registry.IRecipeHandlerRegistry;
import com.jaquadro.minecraft.storagedrawers.api.registry.IRenderRegistry;
import com.jaquadro.minecraft.storagedrawers.api.registry.IWailaRegistry;

public interface IStorageDrawersApi
{
    /**
     * Recipe handlers are used to make custom recipes compatible with compacting drawers.
     */
    IRecipeHandlerRegistry recipeHandlerRegistry ();

    IRenderRegistry renderRegistry ();

    IWailaRegistry wailaRegistry ();

    IPackBlockFactory packFactory ();
}
