package com.enderio.machines.common.recipe;

import java.util.Optional;

import com.enderio.base.common.recipe.DataGenSerializer;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import net.minecraft.ResourceLocationException;
import net.minecraft.core.NonNullList;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.Level;

public class EnchanterRecipe implements IEnchanterRecipe{
    private ResourceLocation id;
    private Enchantment enchantment;
    private int level;
    private NonNullList<Ingredient> ingredients;

    public EnchanterRecipe(ResourceLocation id, NonNullList<Ingredient> ingredients, Enchantment enchantment, int level) {
        this.id = id;
        this.ingredients = ingredients;
        this.enchantment = enchantment;
        this.level = level;
    }

    @Override
    public Enchantment getEnchantment() {
        return this.enchantment;
    }
    
    @Override
    public int getLevel() {
        return level;
    }

    @Override
    public boolean matches(Container pContainer, Level pLevel) {
        if(pContainer.getItem(0).is(Items.WRITABLE_BOOK) && ingredients.get(0).test(pContainer.getItem(1)) && ingredients.get(1).test(pContainer.getItem(2))) {
            return true;
        }
        return false;
    }

    @Override
    public ItemStack assemble(Container pContainer) {
        ItemStack result = new ItemStack(Items.ENCHANTED_BOOK);
        result.enchant(enchantment, 1);
        return result;
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
        return this.id;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return MachineRecipes.Serializer.ENCHANTING.get();
    }

    @Override
    public RecipeType<?> getType() {
        return MachineRecipes.Types.ENCHANTING;
    }
    
    @Override
    public NonNullList<Ingredient> getIngredients() {
        return ingredients;
    }
    
    public static class Serializer extends DataGenSerializer<EnchanterRecipe, Container> {

        @Override
        public EnchanterRecipe fromJson(ResourceLocation pRecipeId, JsonObject pSerializedRecipe) {
            NonNullList<Ingredient> nonnulllist = itemsFromJson(pSerializedRecipe.get("ingredients").getAsJsonArray());
            Optional<Enchantment> enchantment = Registry.ENCHANTMENT.getOptional(new ResourceLocation(pSerializedRecipe.get("enchantment").getAsString()));
            if (enchantment.isEmpty()) {
                throw new ResourceLocationException("The enchantment in " + pRecipeId.toString() + " does not exist");
            }
            int level = pSerializedRecipe.get("levels").getAsInt();
            return new EnchanterRecipe(pRecipeId, nonnulllist, enchantment.get(), level);
        }

        @Override
        public EnchanterRecipe fromNetwork(ResourceLocation pRecipeId, FriendlyByteBuf pBuffer) {
            int i = pBuffer.readVarInt();
            NonNullList<Ingredient> nonnulllist = NonNullList.withSize(i, Ingredient.EMPTY);
            for(int j = 0; j < nonnulllist.size(); ++j) {
                nonnulllist.set(j, Ingredient.of(pBuffer.readItem()));
            }
            Enchantment enchantment = Registry.ENCHANTMENT.get(pBuffer.readResourceLocation());
            int level = pBuffer.readInt();
            return new EnchanterRecipe(pRecipeId, nonnulllist, enchantment, level);
        }

        @Override
        public void toNetwork(FriendlyByteBuf pBuffer, EnchanterRecipe pRecipe) {
            pBuffer.writeVarInt(pRecipe.ingredients.size());
            for(Ingredient ingredient : pRecipe.ingredients) {
                pBuffer.writeItemStack(ingredient.getItems()[0], false);
            }
           pBuffer.writeResourceLocation(pRecipe.enchantment.getRegistryName());
           pBuffer.writeInt(pRecipe.level);
        }

        @Override
        public void toJson(EnchanterRecipe recipe, JsonObject json) {
            JsonArray jsonarray = new JsonArray();
            for(Ingredient ingredient : recipe.ingredients) {
               jsonarray.add(ingredient.toJson());
            }
            json.add("ingredients", jsonarray);
            json.addProperty("enchantment", recipe.enchantment.getRegistryName().toString());
            json.addProperty("levels", recipe.level);
        }
        
        private static NonNullList<Ingredient> itemsFromJson(JsonArray pIngredientArray) {
            NonNullList<Ingredient> nonnulllist = NonNullList.withSize(2, Ingredient.of(ItemStack.EMPTY));
            for(int i = 0; i < pIngredientArray.size(); ++i) {
                Ingredient ingredient = Ingredient.of(ShapedRecipe.itemStackFromJson(pIngredientArray.get(i).getAsJsonObject()));
                if (!ingredient.isEmpty()) {
                    nonnulllist.set(i,ingredient);
                }
            }
            
            return nonnulllist;
        }
        
    }

}
