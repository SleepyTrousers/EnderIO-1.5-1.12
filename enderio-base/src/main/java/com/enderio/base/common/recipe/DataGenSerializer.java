package com.enderio.base.common.recipe;

import com.google.gson.JsonObject;
import net.minecraft.world.Container;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.registries.ForgeRegistryEntry;

public abstract class DataGenSerializer<T extends Recipe<C>, C extends Container> extends ForgeRegistryEntry<RecipeSerializer<?>>
    implements RecipeSerializer<T> {
    public abstract void toJson(T recipe, JsonObject json);
}
