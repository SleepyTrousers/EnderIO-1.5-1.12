package com.enderio.machines.common.recipe;

import java.util.Optional;

import com.enderio.base.common.recipe.DataGenSerializer;
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
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.Level;

public class EnchanterRecipe implements IEnchanterRecipe{
    private ResourceLocation id;
    private Enchantment enchantment;
    private int levelmodifier;
    private Ingredient ingredient;
    private int amountPerLevel;

    public EnchanterRecipe(ResourceLocation id, Ingredient ingredient, Enchantment enchantment, int amountPerLevel, int levelModifier) {
        this.id = id;
        this.ingredient = ingredient;
        this.enchantment = enchantment;
        this.amountPerLevel = amountPerLevel;
        this.levelmodifier = levelModifier;
    }

    @Override
    public Enchantment getEnchantment() {
        return this.enchantment;
    }
    
    @Override
    public int getLevelModifier() {
        return levelmodifier;
    }
    
    public int getLevelCost(Container container) {
        int level = getEnchantmentLevel(container.getItem(1).getCount());
        return getEnchantCost(level);
    }
    
    public int getAmountPerLevel() {
        return amountPerLevel;
    }
    
    public int getEnchantmentLevel(int amount) {
        return Math.min(amount / amountPerLevel, enchantment.getMaxLevel());
    }
    
    public int getLapisForLevel(int level) {
        int res = enchantment.getMaxLevel() == 1 ? 5 : level;
        return (int) Math.max(1, Math.round(res * 1)); //TODO config
    }
    
    public int getAmount(Container container) {
        if (matches(container, null)) {
            return getEnchantmentLevel(container.getItem(1).getCount()) * this.amountPerLevel;
        }
        return 0;
    }
    
    public int getEnchantCost(int level) {
        level = Math.min(level, enchantment.getMaxLevel());
        int cost = getRawXPCostForLevel(level);
        if (level < enchantment.getMaxLevel()) {
            // min cost of half the next levels XP cause books combined in anvil
            int nextCost = getEnchantCost(level + 1);
            cost = Math.max(nextCost / 2, cost);
        }
        return Math.max(1, cost);
    }
    
    private int getRawXPCostForLevel(int level) {
        double min = Math.max(1, enchantment.getMinCost(level));
        min *= levelmodifier;
        int cost = (int) Math.round(min * 1); //TODO global scaling
        cost += 1; //TODO base cost
        return cost;
      }

    @Override
    public boolean matches(Container pContainer, Level pLevel) {
        if (!pContainer.getItem(0).is(Items.WRITABLE_BOOK)) {
            return false;
        }
        if (!ingredient.test(pContainer.getItem(1)) || pContainer.getItem(1).getCount() < amountPerLevel) {
           return false; 
        }
        if (!pContainer.getItem(2).is(Items.LAPIS_LAZULI) || pContainer.getItem(2).getCount() < getLapisForLevel(getEnchantmentLevel(pContainer.getItem(1).getCount()))) {
            return false;
        }
        return true;
    }

    @Override
    public ItemStack assemble(Container pContainer) {
        ItemStack result = new ItemStack(Items.ENCHANTED_BOOK);
        result.enchant(enchantment, getEnchantmentLevel(pContainer.getItem(1).getCount()));
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
    public DataGenSerializer<EnchanterRecipe, Container> getSerializer() {
        return MachineRecipes.Serializer.ENCHANTING.get();
    }

    @Override
    public RecipeType<?> getType() {
        return MachineRecipes.Types.ENCHANTING;
    }
    
    @Override
    public NonNullList<Ingredient> getIngredients() {
        NonNullList<Ingredient> ingredients = NonNullList.create();
        ingredients.add(ingredient);
        return ingredients;
    }
    
    public static class Serializer extends DataGenSerializer<EnchanterRecipe, Container> {

        @Override
        public EnchanterRecipe fromJson(ResourceLocation pRecipeId, JsonObject pSerializedRecipe) {
            Ingredient ingredient = Ingredient.fromJson(pSerializedRecipe.get("ingredient").getAsJsonObject());
            Optional<Enchantment> enchantment = Registry.ENCHANTMENT.getOptional(new ResourceLocation(pSerializedRecipe.get("enchantment").getAsString()));
            if (enchantment.isEmpty()) {
                throw new ResourceLocationException("The enchantment in " + pRecipeId.toString() + " does not exist");
            }
            int amount = pSerializedRecipe.get("amount").getAsInt();
            int level = pSerializedRecipe.get("level").getAsInt();
            return new EnchanterRecipe(pRecipeId, ingredient, enchantment.get(), amount, level);
        }

        @Override
        public EnchanterRecipe fromNetwork(ResourceLocation pRecipeId, FriendlyByteBuf pBuffer) {
            Ingredient ingredient = Ingredient.fromNetwork(pBuffer);
            Enchantment enchantment = Registry.ENCHANTMENT.get(pBuffer.readResourceLocation());
            int amount = pBuffer.readInt();
            int level = pBuffer.readInt();
            return new EnchanterRecipe(pRecipeId, ingredient, enchantment, amount, level);
        }

        @Override
        public void toNetwork(FriendlyByteBuf pBuffer, EnchanterRecipe pRecipe) {
           pRecipe.ingredient.toNetwork(pBuffer);
           pBuffer.writeResourceLocation(pRecipe.enchantment.getRegistryName());
           pBuffer.writeInt(pRecipe.amountPerLevel);
           pBuffer.writeInt(pRecipe.levelmodifier);
        }

        @Override
        public void toJson(EnchanterRecipe recipe, JsonObject json) {
            json.add("ingredient", recipe.ingredient.toJson());
            json.addProperty("enchantment", recipe.enchantment.getRegistryName().toString());
            json.addProperty("amount", recipe.amountPerLevel);
            json.addProperty("level", recipe.levelmodifier);
        }    
    }
}
