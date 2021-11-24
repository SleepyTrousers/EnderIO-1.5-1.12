package com.enderio.core.data;

import com.tterrag.registrate.providers.DataGenContext;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.client.model.generators.ModelBuilder;
import net.minecraftforge.client.model.generators.ModelFile;

public class RotatingItemModel {
    public static void create(DataGenContext<Item, ? extends Item> ctx, ItemModelProvider prov) {
        // json so the BEWLR is used + perspectives

        ResourceLocation registryName = ctx.get().getRegistryName();

        // @formatter:off
        prov
            .getBuilder(registryName.getPath())
            .parent((new ModelFile.UncheckedModelFile("builtin/entity")))
            .transforms()
            .transform(ModelBuilder.Perspective.GROUND).rotation(0, 0, 0).translation(0, 2, 0).scale(0.5F, 0.5F, 0.5F).end()
            .transform(ModelBuilder.Perspective.HEAD).rotation(0, 180, 0).translation(0, 13, 7).scale(1F, 1F, 1F).end()
            .transform(ModelBuilder.Perspective.THIRDPERSON_RIGHT).rotation(0, 0, 0).translation(0, 3, 1).scale(0.55F, 0.55F, 0.55F).end()
            .transform(ModelBuilder.Perspective.FIRSTPERSON_RIGHT).rotation(0, -90F, 25F).translation(1.13F, 3.2F, 1.13F).scale(0.68F, 0.68F, 0.68F).end()
            .transform(ModelBuilder.Perspective.FIXED).rotation(0, 180, 0).translation(0, 0, 0).scale(1F, 1F, 1F).end()
            .end();
        // @formatter:on

        // json with the actual model
        prov
            .getBuilder(registryName.getPath() + "_helper")
            .parent(new ModelFile.UncheckedModelFile("item/generated"))
            .texture("layer0", new ResourceLocation(registryName.getNamespace(), "item/" + registryName.getPath()));
    }
}
