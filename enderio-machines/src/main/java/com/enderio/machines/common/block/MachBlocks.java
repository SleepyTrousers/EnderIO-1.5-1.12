package com.enderio.machines.common.block;

import com.enderio.base.common.item.EIOCreativeTabs;
import com.enderio.machines.EIOMachines;
import com.enderio.machines.common.blockentity.MachinesBlockEntities;
import com.tterrag.registrate.Registrate;
import com.tterrag.registrate.util.NonNullLazyValue;
import com.tterrag.registrate.util.entry.BlockEntry;

public class MachBlocks {
    private static final Registrate REGISTRATE = EIOMachines.registrate();

    public static final BlockEntry<MachineBlock> SIMPLE_POWERED_FURNACE = REGISTRATE
        .block("simple-powered_furnace", props -> new MachineBlock(props, MachinesBlockEntities.SMELTER::create))
        .item()
        .group(new NonNullLazyValue<>(() -> EIOCreativeTabs.MACHINES))
        .build()
        .register();

    public static void register() {}
}
