package com.enderio.machines.common.blockentity;

import com.enderio.machines.EIOMachines;
import com.enderio.machines.common.block.MachineBlocks;
import com.tterrag.registrate.Registrate;
import com.tterrag.registrate.builders.TileEntityBuilder;
import com.tterrag.registrate.util.entry.TileEntityEntry;

import lombok.experimental.UtilityClass;

@UtilityClass
public class MachineBlockEntities {
    private static final Registrate REGISTRATE = EIOMachines.registrate();

    public static final TileEntityEntry<SimpleSmelterBlockEntity> SMELTER = REGISTRATE
        .tileEntity("smelter",
            (TileEntityBuilder.BlockEntityFactory<SimpleSmelterBlockEntity>) (pos, state, type) -> new SimpleSmelterBlockEntity(type, pos, state))
        .validBlocks(MachineBlocks.SIMPLE_POWERED_FURNACE)
        .register();

    public static final TileEntityEntry<FluidTankBlockEntity> FLUID_TANK = REGISTRATE
        .tileEntity("fluid_tank",
            (TileEntityBuilder.BlockEntityFactory<FluidTankBlockEntity>) (pos, state, type) -> new FluidTankBlockEntity(type, pos, state))
        .validBlocks(MachineBlocks.FLUID_TANK)
        .register();
    
    public static final TileEntityEntry<EnchanterBlockEntity> ENCHANTER = REGISTRATE
            .tileEntity("enchanter",
                (TileEntityBuilder.BlockEntityFactory<EnchanterBlockEntity>) (pos, state, type) -> new EnchanterBlockEntity(type, pos, state))
            .validBlocks(MachineBlocks.ENCHANTER)
            .register();

    public static void register() {}
}
