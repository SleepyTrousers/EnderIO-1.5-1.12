package com.enderio.machines.common.menu;

import com.enderio.machines.EIOMachines;
import com.enderio.machines.client.EnchanterScreen;
import com.enderio.machines.client.FluidTankScreen;
import com.tterrag.registrate.Registrate;
import com.tterrag.registrate.util.entry.ContainerEntry;

import lombok.experimental.UtilityClass;

@UtilityClass
public class MachineMenus {

    private static final Registrate REGISTRATE = EIOMachines.registrate();

    public static final ContainerEntry<FluidTankMenu> FLUID_TANK = REGISTRATE.container("fluid_tank",
        FluidTankMenu::factory, () -> FluidTankScreen::new).register();
    
    public static final ContainerEntry<EnchanterMenu> ENCHANTER = REGISTRATE.container("enchanter",
            EnchanterMenu::factory, () -> EnchanterScreen::new).register();

    public static void register() {}
}
