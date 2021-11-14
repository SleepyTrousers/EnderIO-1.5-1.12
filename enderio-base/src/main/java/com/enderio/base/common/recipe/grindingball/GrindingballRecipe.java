package com.enderio.base.common.recipe.grindingball;

import com.enderio.base.common.recipe.EIORecipes;
import com.enderio.base.common.recipe.capacitor.DataGenSerializer;
import com.google.gson.JsonObject;

import net.minecraft.core.NonNullList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraftforge.items.wrapper.RecipeWrapper;

public class GrindingballRecipe implements IGrindingballRecipe{
    private ResourceLocation id;
    private Ingredient grindingball;
    private float grinding;
    private float chance;
    private float power;
    private int durability;

    public GrindingballRecipe(ResourceLocation id, Ingredient grindingball, float grinding, float chance, float power, int durability) {
        this.id = id;
        this.grindingball = grindingball;
        this.grinding = grinding;
        this.chance = chance;
        this.power = power;
        this.durability = durability;
    }
    
    @Override
    public boolean matches(RecipeWrapper pContainer, Level pLevel) {
        return this.grindingball.test(pContainer.getItem(0));
    }

    @Override
    public ItemStack assemble(RecipeWrapper pContainer) {
        return this.getResultItem();
    }

    @Override
    public boolean canCraftInDimensions(int pWidth, int pHeight) {
        return true;
    }

    @Override
    public ItemStack getResultItem() {
        return ItemStack.EMPTY;
    }

    @Override
    public ResourceLocation getId() {
        return id;
    }

    @Override
    public DataGenSerializer<GrindingballRecipe, RecipeWrapper> getSerializer() {
        return EIORecipes.Serializer.GRINDINGBALL.get();
    }

    @Override
    public RecipeType<?> getType() {
        return EIORecipes.Types.GRINDINGBALL;
    }
    
    @Override
    public NonNullList<Ingredient> getIngredients() {
        NonNullList<Ingredient> list = NonNullList.create();
        list.add(grindingball);
        return list;
    }
    
    @Override
    public float getGrinding() {
        return grinding;
    }
    
    @Override
    public float getChance() {
        return chance;
    }
    
    @Override
    public float getPower() {
        return power;
    }
    
    @Override
    public int getDurability() {
        return durability;
    }

    public static class Serializer extends DataGenSerializer<GrindingballRecipe, RecipeWrapper> {

        @Override
        public GrindingballRecipe fromJson(ResourceLocation pRecipeId, JsonObject pSerializedRecipe) {
            Ingredient grindingball = Ingredient.fromJson(pSerializedRecipe.getAsJsonObject("grindingball"));
            float grinding = pSerializedRecipe.get("grinding").getAsFloat();
            float chance = pSerializedRecipe.get("chance").getAsFloat();
            float power = pSerializedRecipe.get("power").getAsFloat();
            int durability = pSerializedRecipe.get("durability").getAsInt();
            return new GrindingballRecipe(pRecipeId, grindingball, grinding, chance, power, durability);
        }

        @Override
        public GrindingballRecipe fromNetwork(ResourceLocation pRecipeId, FriendlyByteBuf pBuffer) {
            Ingredient grindingball = Ingredient.fromNetwork(pBuffer);
            float grinding = pBuffer.readFloat();
            float chance = pBuffer.readFloat();
            float power = pBuffer.readFloat();
            int durability = pBuffer.readInt();
            return new GrindingballRecipe(pRecipeId, grindingball, grinding, chance, power, durability);
        }

        @Override
        public void toNetwork(FriendlyByteBuf pBuffer, GrindingballRecipe pRecipe) {
            pRecipe.grindingball.toNetwork(pBuffer);
            pBuffer.writeFloat(pRecipe.grinding);
            pBuffer.writeFloat(pRecipe.chance);
            pBuffer.writeFloat(pRecipe.power);
            pBuffer.writeInt(pRecipe.durability);
        }

        @Override
        public void toJson(GrindingballRecipe recipe, JsonObject json) {
            json.add("grindingball", recipe.grindingball.toJson());
            json.addProperty("grinding", recipe.grinding);
            json.addProperty("chance", recipe.chance);
            json.addProperty("power", recipe.power);
            json.addProperty("durability", recipe.durability);
        }
        
    }
}
