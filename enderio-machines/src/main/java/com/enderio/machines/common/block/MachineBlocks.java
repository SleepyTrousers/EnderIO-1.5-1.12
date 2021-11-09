package com.enderio.machines.common.block;

import com.enderio.base.common.item.EIOCreativeTabs;
import com.enderio.machines.EIOMachines;
import com.enderio.machines.common.blockentity.MachinesBlockEntities;
import com.tterrag.registrate.Registrate;
import com.tterrag.registrate.util.NonNullLazyValue;
import com.tterrag.registrate.util.entry.BlockEntry;
import com.tterrag.registrate.util.entry.TileEntityEntry;
import net.minecraft.world.level.block.entity.BlockEntity;

public class MachineBlocks {
    private static final Registrate REGISTRATE = EIOMachines.registrate();

    public static final BlockEntry<MachineBlock> SIMPLE_POWERED_FURNACE = REGISTRATE
        .block("simple_powered_furnace", props -> new MachineBlock(props, MachinesBlockEntities.SMELTER))
        .item()
        .group(new NonNullLazyValue<>(() -> EIOCreativeTabs.MACHINES))
        .build()
        .register();
    public static final BlockEntry<MachineBlock> FLUID_TANK = REGISTRATE
        .block("fluid_tank", props -> new MachineBlock(props, MachinesBlockEntities.FLUID_TANK))
        .item()
        .group(new NonNullLazyValue<>(() -> EIOCreativeTabs.MACHINES))
        .build()
        .register();
    public static void register() {}
}
