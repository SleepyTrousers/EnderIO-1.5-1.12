package com.enderio.base;

import javax.annotation.Nonnull;

import com.enderio.base.data.recipe.standard.StandardRecipes;
import com.tterrag.registrate.Registrate;
import com.tterrag.registrate.util.NonNullLazyValue;

import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.forge.event.lifecycle.GatherDataEvent;

@Mod(EnderIO.MODID)
public class EnderIO {
    public static final @Nonnull String MODID = "enderio";
    public static final @Nonnull String DOMAIN = "enderio";
    

    private static final NonNullLazyValue<Registrate> REGISTRATE = new NonNullLazyValue<>(() -> Registrate.create(DOMAIN));

    public EnderIO() {
        EIOItems.register();
        EIOEnchantments.register();

        IEventBus modEventBus = FMLJavaModLoadingContext
            .get()
            .getModEventBus();

        // Run datagen after registrate is finished.
        modEventBus.addListener(EventPriority.LOWEST, this::gatherData);
        modEventBus.addListener(this::ModelLoaders);
        
    }

    //TODO Move to an other location
    public void ModelLoaders(ModelRegistryEvent event) {
        ModelLoader.addSpecialModel(new ResourceLocation(MODID,"item/wood_gear_helper"));
        ModelLoader.addSpecialModel(new ResourceLocation(MODID,"item/stone_gear_helper"));
        ModelLoader.addSpecialModel(new ResourceLocation(MODID,"item/iron_gear_helper"));
        ModelLoader.addSpecialModel(new ResourceLocation(MODID,"item/energized_gear_helper"));
        ModelLoader.addSpecialModel(new ResourceLocation(MODID,"item/vibrant_gear_helper"));
        ModelLoader.addSpecialModel(new ResourceLocation(MODID,"item/dark_bimetal_gear_helper"));
    }

    public void gatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        if (event.includeServer()) {
            StandardRecipes.generate(generator);
        }
    }

    public static Registrate registrate() {
        return REGISTRATE.get();
    }
}
