package com.enderio.machines.data.recipe.enchanter;

import javax.annotation.Nullable;

import com.enderio.machines.EIOMachines;
import com.enderio.machines.common.recipe.EnchanterRecipe;
import com.google.gson.JsonObject;

import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;

public class EnchanterRecipeResult implements FinishedRecipe{
    private EnchanterRecipe recipe;
    private ResourceLocation id;

    public EnchanterRecipeResult(EnchanterRecipe recipe, String name) {
        this(recipe, new ResourceLocation(EIOMachines.DOMAIN, "enchanting/+"+name));
    }
    
    public EnchanterRecipeResult(EnchanterRecipe recipe, ResourceLocation id) {
        this.recipe = recipe;
        this.id = id;
    }

    @Override
    public void serializeRecipeData(JsonObject pJson) {
        recipe.getSerializer().toJson(recipe, pJson);
    }

    @Override
    public ResourceLocation getId() {
        return id;
    }

    @Override
    public RecipeSerializer<?> getType() {
        return recipe.getSerializer();
    }

    @Nullable
    @Override
    public JsonObject serializeAdvancement() {
        return null;
    }

    @Nullable
    @Override
    public ResourceLocation getAdvancementId() {
        return null;
    }

}
