package com.enderio.base;

import javax.annotation.Nonnull;

import com.enderio.base.client.renderers.GraveRenderer;
import com.enderio.base.common.block.EIOBlocks;
import com.enderio.base.common.blockentity.EIOBlockEntities;
import com.enderio.base.common.enchantments.EIOEnchantments;
import com.enderio.base.common.item.EIOItems;
import com.enderio.base.common.recipe.EIORecipes;
import com.enderio.base.data.recipe.standard.StandardRecipes;
import com.tterrag.registrate.Registrate;
import com.tterrag.registrate.util.NonNullLazyValue;

import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.event.EntityRenderersEvent.RegisterRenderers;
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
        EIOBlocks.register();
        EIOItems.register();
        EIOBlocks.register();
        EIOBlockEntities.register();
        EIOEnchantments.register();

        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        EIORecipes.register(modEventBus);


        // Run datagen after registrate is finished.
        modEventBus.addListener(EventPriority.LOWEST, this::gatherData);

    }

    public static ResourceLocation loc(String path) {
        return new ResourceLocation(DOMAIN, path);
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
