package com.enderio.decoration.common.blockentity;

import com.enderio.decoration.EIODecor;
import com.enderio.decoration.common.block.DecorBlocks;
import com.tterrag.registrate.Registrate;
import com.tterrag.registrate.builders.TileEntityBuilder;
import com.tterrag.registrate.util.entry.TileEntityEntry;
import com.tterrag.registrate.util.nullness.NonNullSupplier;

public class DecorBlockEntities {

    private static final Registrate REGISTRATE = EIODecor.registrate();

    public static final TileEntityEntry<SinglePaintedBlockEntity> SINGLE_PAINTED = REGISTRATE
        .tileEntity("single_painted",
            (TileEntityBuilder.BlockEntityFactory<SinglePaintedBlockEntity>) (pos, state, type) -> new SinglePaintedBlockEntity(type, pos, state))
        .validBlocks(DecorBlocks.getPaintedSupplier().toArray(new NonNullSupplier[0]))
        .register();

    public static final TileEntityEntry<DoublePaintedBlockEntity> DOUBLE_PAINTED = REGISTRATE
        .tileEntity("double_painted",
            (TileEntityBuilder.BlockEntityFactory<DoublePaintedBlockEntity>) (pos, state, type) -> new DoublePaintedBlockEntity(type, pos, state))
        .validBlocks(DecorBlocks.PAINTED_SLAB)
        .register();

    public static void register() {}

}
