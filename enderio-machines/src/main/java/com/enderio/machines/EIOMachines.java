package com.enderio.machines;

import com.enderio.machines.common.block.MachBlocks;
import com.enderio.machines.common.blockentity.MachinesBlockEntities;
import com.tterrag.registrate.Registrate;
import com.tterrag.registrate.util.NonNullLazyValue;
import net.minecraftforge.fml.common.Mod;

@Mod(EIOMachines.MODID)
public class EIOMachines {
    public static final String MODID = "enderiomachines";
    public static final String DOMAIN = "enderio";

    private static final NonNullLazyValue<Registrate> REGISTRATE = new NonNullLazyValue<>(() -> Registrate.create(DOMAIN));

    public EIOMachines() {
        MachBlocks.register();
        MachinesBlockEntities.register();
    }

    public static Registrate registrate() {
        return REGISTRATE.get();
    }
}
