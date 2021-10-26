package com.enderio.base.common.recipe;

import com.google.gson.JsonObject;
import com.mojang.serialization.JsonOps;
import net.minecraft.nbt.*;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import com.enderio.base.common.capability.capacitors.ICapacitorData;
import com.enderio.base.common.capability.capacitors.CapacitorData;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class CapacitorDataRecipe implements Recipe<Container> {

    private final ResourceLocation id;
    private final Ingredient capacitor;
    private final CapacitorData capacitorData;

    public CapacitorDataRecipe(ResourceLocation id, Ingredient capacitor, CapacitorData capacitorData) {
        this.id = id;
        this.capacitor = capacitor;
        this.capacitorData = capacitorData;
    }

    public ICapacitorData getCapacitorData() {
        return capacitorData;
    }

    public boolean matches(ItemStack stack) {
        return capacitor.test(stack);
    }

    // Prevent unknown RecipeCategory log spamming
    @Override
    public boolean isIncomplete() {
        return true;
    }

    @Override
    public boolean matches(@Nonnull Container pContainer, @Nonnull Level pLevel) {
        return false;
    }

    @Nonnull
    @Override
    public ItemStack assemble(@Nonnull Container pContainer) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canCraftInDimensions(int pWidth, int pHeight) {
        return false;
    }

    @Nonnull
    @Override
    public ItemStack getResultItem() {
        return ItemStack.EMPTY;
    }

    @Nonnull
    @Override
    public ResourceLocation getId() {
        return id;
    }

    @Nonnull
    @Override
    public DataGenSerializer<CapacitorDataRecipe, Container> getSerializer() {
        return EIORecipes.Serializer.CAPACITOR_DATA.get();
    }

    @Nonnull
    @Override
    public RecipeType<?> getType() {
        return EIORecipes.Types.CAPACITOR_DATA;
    }

    public static class Serializer extends DataGenSerializer<CapacitorDataRecipe, Container> {

        @Nonnull
        @Override
        public CapacitorDataRecipe fromJson(@Nonnull ResourceLocation recipeId, JsonObject json) {
            Ingredient capacitor = Ingredient.fromJson(json.get("capacitor"));
            CapacitorData capacitorData = new CapacitorData();
            capacitorData.deserializeNBT(JsonOps.INSTANCE.convertTo(NbtOps.INSTANCE, json.get("data")));
            return new CapacitorDataRecipe(recipeId, capacitor, capacitorData);
        }

        @Nullable
        @Override
        public CapacitorDataRecipe fromNetwork(@Nonnull ResourceLocation recipeId, FriendlyByteBuf buffer) {
            CapacitorData capacitorData = new CapacitorData();
            capacitorData.deserializeNBT(buffer.readNbt());
            return new CapacitorDataRecipe(recipeId, Ingredient.fromNetwork(buffer), capacitorData);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer, CapacitorDataRecipe recipe) {
            buffer.writeNbt(recipe.capacitorData.serializeNBT());
            recipe.capacitor.toNetwork(buffer);
        }

        @Override
        public void toJson(CapacitorDataRecipe recipe, JsonObject json) {
            json.add("capacitor", recipe.capacitor.toJson());
            json.add("data", NbtOps.INSTANCE.convertTo(JsonOps.INSTANCE, recipe.capacitorData.serializeNBT()));
        }
    }
}
