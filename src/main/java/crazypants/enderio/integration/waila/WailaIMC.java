package crazypants.enderio.integration.waila;

import net.minecraftforge.fml.common.event.FMLInterModComms;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class WailaIMC {

  public static void init(FMLPreInitializationEvent event) {
    FMLInterModComms.sendMessage("Waila", "register", "crazypants.enderio.integration.waila.WailaCompat.load");
  }

}
