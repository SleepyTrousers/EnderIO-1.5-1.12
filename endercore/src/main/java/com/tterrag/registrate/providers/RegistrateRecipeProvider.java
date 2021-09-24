package com.tterrag.registrate.providers;

import java.nio.file.Path;
import java.util.function.Consumer;
import java.util.function.Supplier;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nullable;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonObject;
import com.tterrag.registrate.AbstractRegistrate;
import com.tterrag.registrate.util.DataIngredient;
import com.tterrag.registrate.util.nullness.NonNullSupplier;

import net.minecraft.advancements.critereon.EnterBlockTrigger;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.HashCache;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.data.recipes.SimpleCookingRecipeBuilder;
import net.minecraft.data.recipes.SingleItemRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.Tag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SimpleCookingSerializer;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.Tags;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.registries.IForgeRegistryEntry;

public class RegistrateRecipeProvider extends RecipeProvider implements RegistrateProvider, Consumer<FinishedRecipe> {
    
    private final AbstractRegistrate<?> owner;

    public RegistrateRecipeProvider(AbstractRegistrate<?> owner, DataGenerator generatorIn) {
        super(generatorIn);
        this.owner = owner;
    }

    @Override
    public LogicalSide getSide() {
        return LogicalSide.SERVER;
    }
    
    @Nullable
    private Consumer<FinishedRecipe> callback;
    
    @Override
    public void accept(@Nullable FinishedRecipe t) {
        if (callback == null) {
            throw new IllegalStateException("Cannot accept recipes outside of a call to registerRecipes");
        }
        callback.accept(t);
    }

    @Override
    protected void buildCraftingRecipes(Consumer<FinishedRecipe> consumer) {
        this.callback = consumer;
        owner.genData(ProviderType.RECIPE, this);
        this.callback = null;
    }
    
    public ResourceLocation safeId(ResourceLocation id) {
        return new ResourceLocation(owner.getModid(), safeName(id));
    }

    public ResourceLocation safeId(DataIngredient source) {
        return safeId(source.getId());
    }

    public ResourceLocation safeId(IForgeRegistryEntry<?> registryEntry) {
        return safeId(registryEntry.getRegistryName());
    }

    public String safeName(ResourceLocation id) {
        return id.getPath().replace('/', '_');
    }

    public String safeName(DataIngredient source) {
        return safeName(source.getId());
    }

    public String safeName(IForgeRegistryEntry<?> registryEntry) {
        return safeName(registryEntry.getRegistryName());
    }

    public static final int DEFAULT_SMELT_TIME = 200;
    public static final int DEFAULT_BLAST_TIME = DEFAULT_SMELT_TIME / 2;
    public static final int DEFAULT_SMOKE_TIME = DEFAULT_BLAST_TIME;
    public static final int DEFAULT_CAMPFIRE_TIME = DEFAULT_SMELT_TIME * 3;
    
    private static final String SMELTING_NAME = "smelting";
    @SuppressWarnings("null")
    private static final ImmutableMap<SimpleCookingSerializer<?>, String> COOKING_TYPE_NAMES = ImmutableMap.<SimpleCookingSerializer<?>, String>builder()
            .put(RecipeSerializer.SMELTING_RECIPE, SMELTING_NAME)
            .put(RecipeSerializer.BLASTING_RECIPE, "blasting")
            .put(RecipeSerializer.SMOKING_RECIPE, "smoking")
            .put(RecipeSerializer.CAMPFIRE_COOKING_RECIPE, "campfire")
            .build();
    
    public <T extends ItemLike & IForgeRegistryEntry<?>> void cooking(DataIngredient source, Supplier<? extends T> result, float experience, int cookingTime, SimpleCookingSerializer<?> serializer) {
        cooking(source, result, experience, cookingTime, COOKING_TYPE_NAMES.get(serializer), serializer);
    }
    
    public <T extends ItemLike & IForgeRegistryEntry<?>> void cooking(DataIngredient source, Supplier<? extends T> result, float experience, int cookingTime, String typeName, SimpleCookingSerializer<?> serializer) {
        SimpleCookingRecipeBuilder.cooking(source, result.get(), experience, cookingTime, serializer)
            .unlockedBy("has_" + safeName(source), source.getCritereon(this))
            .save(this, safeId(result.get()) + "_from_" + safeName(source) + "_" + typeName);
    }
    
    public <T extends ItemLike & IForgeRegistryEntry<?>> void smelting(DataIngredient source, Supplier<? extends T> result, float experience) {
        smelting(source, result, experience, DEFAULT_SMELT_TIME);
    }

    public <T extends ItemLike & IForgeRegistryEntry<?>> void smelting(DataIngredient source, Supplier<? extends T> result, float experience, int cookingTime) {
        cooking(source, result, experience, cookingTime, RecipeSerializer.SMELTING_RECIPE);
    }
    
    public <T extends ItemLike & IForgeRegistryEntry<?>> void blasting(DataIngredient source, Supplier<? extends T> result, float experience) {
        blasting(source, result, experience, DEFAULT_BLAST_TIME);
    }

    public <T extends ItemLike & IForgeRegistryEntry<?>> void blasting(DataIngredient source, Supplier<? extends T> result, float experience, int cookingTime) {
        cooking(source, result, experience, cookingTime, RecipeSerializer.BLASTING_RECIPE);
    }

    public <T extends ItemLike & IForgeRegistryEntry<?>> void smoking(DataIngredient source, Supplier<? extends T> result, float experience) {
        smoking(source, result, experience, DEFAULT_SMOKE_TIME);
    }

    public <T extends ItemLike & IForgeRegistryEntry<?>> void smoking(DataIngredient source, Supplier<? extends T> result, float experience, int cookingTime) {
        cooking(source, result, experience, cookingTime, RecipeSerializer.SMOKING_RECIPE);
    }
    
    public <T extends ItemLike & IForgeRegistryEntry<?>> void campfire(DataIngredient source, Supplier<? extends T> result, float experience) {
        campfire(source, result, experience, DEFAULT_CAMPFIRE_TIME);
    }

    public <T extends ItemLike & IForgeRegistryEntry<?>> void campfire(DataIngredient source, Supplier<? extends T> result, float experience, int cookingTime) {
        cooking(source, result, experience, cookingTime, RecipeSerializer.CAMPFIRE_COOKING_RECIPE);
    }
    
    public <T extends ItemLike & IForgeRegistryEntry<?>> void stonecutting(DataIngredient source, Supplier<? extends T> result) {
        stonecutting(source, result, 1);
    }

    public <T extends ItemLike & IForgeRegistryEntry<?>> void stonecutting(DataIngredient source, Supplier<? extends T> result, int resultAmount) {
        SingleItemRecipeBuilder.stonecutting(source, result.get(), resultAmount)
            .unlockedBy("has_" + safeName(source), source.getCritereon(this))
            .save(this, safeId(result.get()) + "_from_" + safeName(source) + "_stonecutting");
    }
    
    public <T extends ItemLike & IForgeRegistryEntry<?>> void smeltingAndBlasting(DataIngredient source, Supplier<? extends T> result, float xp) {
        smelting(source, result, xp);
        blasting(source, result, xp);
    }

    public <T extends ItemLike & IForgeRegistryEntry<?>> void food(DataIngredient source, Supplier<? extends T> result, float xp) {
        smelting(source, result, xp);
        smoking(source, result, xp);
        campfire(source, result, xp);
    }
    
    public <T extends ItemLike & IForgeRegistryEntry<?>> void square(DataIngredient source, Supplier<? extends T> output, boolean small) {
        ShapedRecipeBuilder builder = ShapedRecipeBuilder.shaped(output.get())
                .define('X', source);
        if (small) {
            builder.pattern("XX").pattern("XX");
        } else {
            builder.pattern("XXX").pattern("XXX").pattern("XXX");
        }
        builder.unlockedBy("has_" + safeName(source), source.getCritereon(this))
            .save(this, safeId(output.get()));
    }

    /**
     * @param <T>
     * @param source
     * @param output
     * @deprecated Broken, use {@link #storage(NonNullSupplier, NonNullSupplier)} or {@link #storage(DataIngredient, NonNullSupplier, DataIngredient, NonNullSupplier)}.
     */
    @Deprecated
    public <T extends ItemLike & IForgeRegistryEntry<?>> void storage(DataIngredient source, NonNullSupplier<? extends T> output) {
        square(source, output, false);
        // This is backwards, but leaving in for binary compat
        singleItemUnfinished(source, output, 1, 9)
            .save(this, safeId(source) + "_from_" + safeName(output.get()));
    }

    public <T extends ItemLike & IForgeRegistryEntry<?>> void storage(NonNullSupplier<? extends T> source, NonNullSupplier<? extends T> output) {
        storage(DataIngredient.items(source), source, DataIngredient.items(output), output);
    }
    
    public <T extends ItemLike & IForgeRegistryEntry<?>> void storage(DataIngredient sourceIngredient, NonNullSupplier<? extends T> source, DataIngredient outputIngredient, NonNullSupplier<? extends T> output) {
        square(sourceIngredient, output, false);
        singleItemUnfinished(outputIngredient, source, 1, 9)
            .save(this, safeId(sourceIngredient) + "_from_" + safeName(output.get()));
    }

    @CheckReturnValue
    public <T extends ItemLike & IForgeRegistryEntry<?>> ShapelessRecipeBuilder singleItemUnfinished(DataIngredient source, Supplier<? extends T> result, int required, int amount) {
        return ShapelessRecipeBuilder.shapeless(result.get(), amount)
            .requires(source, required)
            .unlockedBy("has_" + safeName(source), source.getCritereon(this));
    }
    
    public <T extends ItemLike & IForgeRegistryEntry<?>> void singleItem(DataIngredient source, Supplier<? extends T> result, int required, int amount) {
        singleItemUnfinished(source, result, required, amount).save(this, safeId(result.get()));
    }
    
    public <T extends ItemLike & IForgeRegistryEntry<?>> void planks(DataIngredient source, Supplier<? extends T> result) {
        singleItemUnfinished(source, result, 1, 4)
            .group("planks")
            .save(this, safeId(result.get()));
    }
    
    public <T extends ItemLike & IForgeRegistryEntry<?>> void stairs(DataIngredient source, Supplier<? extends T> result, @Nullable String group, boolean stone) {
        ShapedRecipeBuilder.shaped(result.get(), 4)
            .pattern("X  ").pattern("XX ").pattern("XXX")
            .define('X', source)
            .group(group)
            .unlockedBy("has_" + safeName(source), source.getCritereon(this))
            .save(this, safeId(result.get()));
        if (stone) {
            stonecutting(source, result);
        }
    }
    
    public <T extends ItemLike & IForgeRegistryEntry<?>> void slab(DataIngredient source, Supplier<? extends T> result, @Nullable String group, boolean stone) {
        ShapedRecipeBuilder.shaped(result.get(), 6)
            .pattern("XXX")
            .define('X', source)
            .group(group)
            .unlockedBy("has_" + safeName(source), source.getCritereon(this))
            .save(this, safeId(result.get()));
        if (stone) {
            stonecutting(source, result, 2);
        }
    }
    
    public <T extends ItemLike & IForgeRegistryEntry<?>> void fence(DataIngredient source, Supplier<? extends T> result, @Nullable String group) {
        ShapedRecipeBuilder.shaped(result.get(), 3)
            .pattern("W#W").pattern("W#W")
            .define('W', source)
            .define('#', Tags.Items.RODS_WOODEN)
            .group(group)
            .unlockedBy("has_" + safeName(source), source.getCritereon(this))
            .save(this, safeId(result.get()));
    }
    
    public <T extends ItemLike & IForgeRegistryEntry<?>> void fenceGate(DataIngredient source, Supplier<? extends T> result, @Nullable String group) {
        ShapedRecipeBuilder.shaped(result.get())
            .pattern("#W#").pattern("#W#")
            .define('W', source)
            .define('#', Tags.Items.RODS_WOODEN)
            .group(group)
            .unlockedBy("has_" + safeName(source), source.getCritereon(this))
            .save(this, safeId(result.get()));
    }
    
    public <T extends ItemLike & IForgeRegistryEntry<?>> void wall(DataIngredient source, Supplier<? extends T> result) {
        ShapedRecipeBuilder.shaped(result.get(), 6)
            .pattern("XXX").pattern("XXX")
            .define('X', source)
            .unlockedBy("has_" + safeName(source), source.getCritereon(this))
            .save(this, safeId(result.get()));
        stonecutting(source, result);
    }
    
    public <T extends ItemLike & IForgeRegistryEntry<?>> void door(DataIngredient source, Supplier<? extends T> result, @Nullable String group) {
        ShapedRecipeBuilder.shaped(result.get(), 3)
            .pattern("XX").pattern("XX").pattern("XX")
            .define('X', source)
            .group(group)
            .unlockedBy("has_" + safeName(source), source.getCritereon(this))
            .save(this, safeId(result.get()));
    }

    public <T extends ItemLike & IForgeRegistryEntry<?>> void trapDoor(DataIngredient source, Supplier<? extends T> result, @Nullable String group) {
        ShapedRecipeBuilder.shaped(result.get(), 2)
            .pattern("XXX").pattern("XXX")
            .define('X', source)
            .group(group)
            .unlockedBy("has_" + safeName(source), source.getCritereon(this))
            .save(this, safeId(result.get()));
    }

    // @formatter:off
    // GENERATED START

    @Override
    public void saveAdvancement(HashCache p_126014_, JsonObject p_126015_, Path p_126016_) { super.saveAdvancement(p_126014_, p_126015_, p_126016_); }

    public static EnterBlockTrigger.TriggerInstance insideOf(Block p_125980_) { return RecipeProvider.insideOf(p_125980_); }

    public static InventoryChangeTrigger.TriggerInstance has(ItemLike p_125978_) { return RecipeProvider.has(p_125978_); }

    public static InventoryChangeTrigger.TriggerInstance has(Tag<Item> p_125976_) { return RecipeProvider.has(p_125976_); }

    public static InventoryChangeTrigger.TriggerInstance inventoryTrigger(ItemPredicate... p_126012_) { return RecipeProvider.inventoryTrigger(p_126012_); }

    // GENERATED END
}
