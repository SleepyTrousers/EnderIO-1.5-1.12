package com.enderio.base.common.blockentity;

import com.enderio.base.EnderIO;
import com.enderio.base.common.block.EIOBlocks;
import com.tterrag.registrate.Registrate;
import com.tterrag.registrate.util.entry.TileEntityEntry;
import net.minecraft.world.level.block.entity.BlockEntity;

public class EIOBlockEntities {
    private static final Registrate REGISTRATE = EnderIO.registrate();

    public static final TileEntityEntry<BlockEntity> GRAVE = REGISTRATE
        .tileEntity("grave", (s, p, t) -> new GraveBlockEntity(t, s, p))
        .validBlock(EIOBlocks.GRAVE)
        .register();

    public static void register() {}
}
