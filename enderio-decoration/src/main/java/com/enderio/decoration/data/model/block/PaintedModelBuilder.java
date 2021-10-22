package com.enderio.decoration.data.model.block;

import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.*;
import net.minecraftforge.client.model.generators.BlockModelBuilder;
import net.minecraftforge.common.data.ExistingFileHelper;

public class PaintedModelBuilder extends BlockModelBuilder {

    private final Block reference;

    public PaintedModelBuilder(ResourceLocation outputLocation, ExistingFileHelper existingFileHelper, Block reference) {
        super(outputLocation, existingFileHelper);
        this.reference = reference;
        transform();
    }

    @Override
    public JsonObject toJson() {
        JsonObject json = super.toJson();
        JsonObject root = new JsonObject();
        root.addProperty("loader", reference instanceof SlabBlock ? "enderio:painted_slab" : "enderio:painted_model");
        root.addProperty("reference", reference.getRegistryName().toString());
        if (json.has("display")) {
            root.add("display", json.get("display"));
        }
        return root;
    }

    private void transform() {
        if (reference instanceof StairBlock) {
            // @formatter:off
            transforms
                .transform(Perspective.GUI).rotation(30, 135, 0).scale(0.625f).end()
                .transform(Perspective.HEAD).rotation(0, -90, 0).end()
                .transform(Perspective.THIRDPERSON_LEFT).rotation(75, -135, 0).translation(0, 2.5f, 0).scale(0.375f).end();
            // @formatter:on
        }
        if (reference instanceof FenceGateBlock) {
            // @formatter:off
            transforms
                .transform(Perspective.GUI).rotation(30, 45, 0).translation(0, -1, 0).scale(0.8f).end()
                .transform(Perspective.HEAD).translation(0, -3, -6).end();
            // @formatter:on
        }
        if (reference instanceof FenceBlock) {
            // @formatter:off
            transforms
                .transform(Perspective.GUI).rotation(30, 135, 0).scale(0.625f).end()
                .transform(Perspective.FIXED).rotation(0, 90, 0).scale(0.5f).end();
            // @formatter:on
        }
    }
}
