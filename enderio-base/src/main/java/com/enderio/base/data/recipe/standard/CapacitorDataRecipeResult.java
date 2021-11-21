package com.enderio.base.data.recipe.standard;

import com.enderio.base.EnderIO;
import com.enderio.base.common.recipe.capacitor.CapacitorDataRecipe;
import com.google.gson.JsonObject;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class CapacitorDataRecipeResult implements FinishedRecipe {

    private final CapacitorDataRecipe recipe;
    private final ResourceLocation id;

    public CapacitorDataRecipeResult(CapacitorDataRecipe recipe, String name) {
        this(recipe, new ResourceLocation(EnderIO.DOMAIN, "capacitor_data/" + name));
    }

    public CapacitorDataRecipeResult(CapacitorDataRecipe recipe, ResourceLocation id) {
        this.recipe = recipe;
        this.id = id;
    }

    @Override
    public void serializeRecipeData(@Nonnull JsonObject json) {
        recipe.getSerializer().toJson(recipe, json);
    }

    @Nonnull
    @Override
    public ResourceLocation getId() {
        return id;
    }

    @Nonnull
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
