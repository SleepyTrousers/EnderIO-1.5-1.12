package crazypants.enderio.machine;

import net.minecraftforge.fml.common.Mod;

@Mod(modid = "enderio-machines", name="Ender IO Machines", version = "1.0")
public class EnderIOMachines {

  static {
    MachineObject.values();
  }
}
