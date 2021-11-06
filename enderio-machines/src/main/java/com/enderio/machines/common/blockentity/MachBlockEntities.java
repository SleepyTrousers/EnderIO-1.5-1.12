package com.enderio.machines.common.blockentity;

import com.enderio.machines.EIOMachines;
import com.enderio.machines.common.block.MachBlocks;
import com.tterrag.registrate.Registrate;
import com.tterrag.registrate.builders.TileEntityBuilder;
import com.tterrag.registrate.util.entry.TileEntityEntry;

public class MachBlockEntities {
    private static final Registrate REGISTRATE = EIOMachines.registrate();

    public static final TileEntityEntry<SimpleSmelterBlockEntity> SMELTER = REGISTRATE
        .tileEntity("smelter",
            (TileEntityBuilder.BlockEntityFactory<SimpleSmelterBlockEntity>) (pos, state, type) -> new SimpleSmelterBlockEntity(type, pos, state))
        .validBlocks(MachBlocks.SIMPLE_POWERED_FURNACE)
        .register();

    public static void register() {}
}
