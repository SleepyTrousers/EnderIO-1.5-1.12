package com.enderio.machines.common.recipe;

import com.enderio.base.EnderIO;

import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fmllegacy.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class MachineRecipes {

    public static void register(IEventBus bus) {
        Serializer.register(bus);
        Types.register();
    }

    public static class Serializer {
        private Serializer() {}

        public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZER_REGISTRY = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, EnderIO.DOMAIN);

        public static final RegistryObject<EnchanterRecipe.Serializer> ENCHANTING = RECIPE_SERIALIZER_REGISTRY.register("enchanting",
                EnchanterRecipe.Serializer::new);

        public static void register(IEventBus bus) {
            RECIPE_SERIALIZER_REGISTRY.register(bus);
        }
    }

    public static class Types {
        private Types() {}

        public static RecipeType<IEnchanterRecipe> ENCHANTING = RecipeType.register(EnderIO.DOMAIN + ":enchanting");

        public static void register() {}
    }
}
