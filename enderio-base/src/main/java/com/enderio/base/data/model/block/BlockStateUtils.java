package com.enderio.base.data.model.block;

import com.tterrag.registrate.providers.DataGenContext;
import com.tterrag.registrate.providers.RegistrateBlockstateProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.IronBarsBlock;
import net.minecraftforge.client.model.generators.ModelProvider;

public class BlockStateUtils {
    public static void paneBlock(DataGenContext<Block, ? extends IronBarsBlock> ctx, RegistrateBlockstateProvider cons) {
        cons.paneBlock(ctx.get(), cons.models().panePost(ctx.getName().concat("_post"), cons.blockTexture(ctx.get()), cons.blockTexture(ctx.get())),
            cons.models().paneSide(ctx.getName().concat("_side"), cons.blockTexture(ctx.get()), cons.blockTexture(ctx.get())),
            cons.models().paneSideAlt(ctx.getName().concat("_side_alt"), cons.blockTexture(ctx.get()), cons.blockTexture(ctx.get())),
            cons.models().paneNoSide(ctx.getName().concat("_no_side"), cons.blockTexture(ctx.get())),
            cons.models().paneNoSideAlt(ctx.getName().concat("_no_side_alt"), cons.blockTexture(ctx.get())));
    }
}
