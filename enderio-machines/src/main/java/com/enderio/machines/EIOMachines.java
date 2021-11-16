package com.enderio.machines;

import com.enderio.machines.common.block.MachineBlocks;
import com.enderio.machines.common.blockentity.MachineBlockEntities;
import com.enderio.machines.common.menu.MachineMenus;
import com.enderio.machines.common.recipe.MachineRecipes;
import com.tterrag.registrate.Registrate;
import com.tterrag.registrate.util.NonNullLazyValue;

import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(EIOMachines.MODID)
public class EIOMachines {
    public static final String MODID = "enderiomachines";
    public static final String DOMAIN = "enderio";

    private static final NonNullLazyValue<Registrate> REGISTRATE = new NonNullLazyValue<>(() -> Registrate.create(DOMAIN));

    public EIOMachines() {
        MachineBlocks.register();
        MachineBlockEntities.register();
        MachineMenus.register();
        
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        
        MachineRecipes.register(modEventBus);
    }

    public static Registrate registrate() {
        return REGISTRATE.get();
    }
}
