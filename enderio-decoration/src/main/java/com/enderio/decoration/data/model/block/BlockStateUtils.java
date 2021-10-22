package com.enderio.decoration.data.model.block;

import com.tterrag.registrate.providers.DataGenContext;
import com.tterrag.registrate.providers.RegistrateBlockstateProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.model.generators.ModelProvider;

// TODO: Name.
public class BlockStateUtils {
    /**
     * {@see ModelProvider.MODEL}
     */
    public static void paintedBlock(DataGenContext<Block, ? extends Block> ctx, RegistrateBlockstateProvider prov, Block toCopy) {
        Block paintedBlock = ctx.get();
        PaintedModelBuilder paintedModel = new PaintedModelBuilder(
            new ResourceLocation(paintedBlock.getRegistryName().getNamespace(), ModelProvider.BLOCK_FOLDER + "/" + paintedBlock.getRegistryName().getPath()),
            prov.models().existingFileHelper, toCopy);
        prov.models().getBuilder(paintedBlock.getRegistryName().getPath());
        prov.models().generatedModels.put(paintedModel.getLocation(), paintedModel);
        prov.simpleBlock(paintedBlock, paintedModel);
    }
}
