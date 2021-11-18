package com.enderio.base;

import javax.annotation.Nonnull;

import com.enderio.base.common.block.EIOBlocks;
import com.enderio.base.common.blockentity.EIOBlockEntities;
import com.enderio.base.common.enchantment.EIOEnchantments;
import com.enderio.base.common.item.EIOItems;
import com.enderio.base.common.lang.EIOLang;
import com.enderio.base.common.menu.EIOMenus;
import com.enderio.base.common.network.EIOPackets;
import com.enderio.base.common.tag.EIOTags;
import com.enderio.base.common.recipe.EIORecipes;
import com.enderio.base.config.base.BaseConfig;
import com.enderio.base.config.decor.DecorConfig;
import com.enderio.base.config.machines.MachinesConfig;
import com.enderio.base.data.recipe.standard.EIOItemTagsProvider;
import com.enderio.base.data.recipe.standard.StandardRecipes;
import com.tterrag.registrate.Registrate;
import com.tterrag.registrate.util.NonNullLazyValue;

import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.data.ForgeBlockTagsProvider;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLConfig;
import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.forge.event.lifecycle.GatherDataEvent;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Mod(EnderIO.MODID)
public class EnderIO {
    public static final @Nonnull String MODID = "enderio";
    public static final @Nonnull String DOMAIN = "enderio";

    private static final NonNullLazyValue<Registrate> REGISTRATE = new NonNullLazyValue<>(() -> Registrate.create(DOMAIN));

    public EnderIO() {
        // Create configs subdirectory
        try {
            Files.createDirectories(FMLPaths.CONFIGDIR.get().resolve("enderio"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Register config files
        var ctx = ModLoadingContext.get();
        ctx.registerConfig(ModConfig.Type.COMMON, BaseConfig.COMMON_SPEC, "enderio/base-common.toml");
        ctx.registerConfig(ModConfig.Type.CLIENT, BaseConfig.CLIENT_SPEC, "enderio/base-client.toml");
        ctx.registerConfig(ModConfig.Type.COMMON, DecorConfig.COMMON_SPEC, "enderio/decor-common.toml");
        ctx.registerConfig(ModConfig.Type.CLIENT, DecorConfig.CLIENT_SPEC, "enderio/decor-client.toml");
        ctx.registerConfig(ModConfig.Type.COMMON, MachinesConfig.COMMON_SPEC, "enderio/machines-common.toml");
        ctx.registerConfig(ModConfig.Type.CLIENT, MachinesConfig.CLIENT_SPEC, "enderio/machines-client.toml");

        // Registries
        EIOItems.register();
        EIOBlocks.register();
        EIOBlockEntities.register();
        EIOEnchantments.register();
        EIOTags.init();
        EIOMenus.register();
        EIOPackets.register();
        EIOLang.register();

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
            ForgeBlockTagsProvider b = new ForgeBlockTagsProvider(generator, event.getExistingFileHelper());
            generator.addProvider( new EIOItemTagsProvider(generator, b, event.getExistingFileHelper()));
        }
    }

    public static Registrate registrate() {
        return REGISTRATE.get();
    }
}
