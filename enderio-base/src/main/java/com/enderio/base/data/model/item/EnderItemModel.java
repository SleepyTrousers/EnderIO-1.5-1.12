package com.enderio.base.data.model.item;

import com.enderio.base.EnderIO;
import com.enderio.base.common.item.GearItem;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.client.model.generators.ModelBuilder.Perspective;
import net.minecraftforge.client.model.generators.ModelFile;

public class EnderItemModel {

    // Datagen for gears
    public static void gearModel(ItemModelProvider provider, GearItem gear) {
        // json so the BEWLR is used + perspectives

        // @formatter:off
        provider
            .getBuilder(gear.getRegistryName().getPath().toString())
            .parent((new ModelFile.UncheckedModelFile("builtin/entity")))
            .transforms()
            .transform(Perspective.GROUND).rotation(0, 0, 0).translation(0, 2, 0).scale(0.5F, 0.5F, 0.5F).end()
            .transform(Perspective.HEAD).rotation(0, 180, 0).translation(0, 13, 7).scale(1F, 1F, 1F).end()
            .transform(Perspective.THIRDPERSON_RIGHT).rotation(0, 0, 0).translation(0, 3, 1).scale(0.55F, 0.55F, 0.55F).end()
            .transform(Perspective.FIRSTPERSON_RIGHT).rotation(0, -90F, 25F).translation(1.13F, 3.2F, 1.13F).scale(0.68F, 0.68F, 0.68F).end()
            .transform(Perspective.FIXED).rotation(0, 180, 0).translation(0, 0, 0).scale(1F, 1F, 1F).end()
            .end();
        // @formatter:on

        // json with the actual model
        provider
            .getBuilder(gear.getRegistryName().getPath().toString() + "_helper")
            .parent(new ModelFile.UncheckedModelFile("item/generated"))
            .texture("layer0", new ResourceLocation(EnderIO.MODID, "item/" + gear.getRegistryName().getPath().toString()));
    }

}
