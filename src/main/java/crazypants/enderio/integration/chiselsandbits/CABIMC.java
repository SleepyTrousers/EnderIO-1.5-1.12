package crazypants.enderio.integration.chiselsandbits;

import crazypants.enderio.Log;
import crazypants.enderio.ModObject;
import net.minecraft.block.Block;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLInterModComms;

public class CABIMC {

  public static void init(FMLInitializationEvent event) {
    /* they don't work */
    // whitelist(ModObject.blockFusedQuartz);
    // for (FusedQuartzType glasstype : FusedQuartzType.values()) {
    // whitelist(glasstype.getBlock());
    // }
    /* they do work with only minor visual anomalies (direction is different, reverse quads are missing */
    whitelist(ModObject.blockDecoration1);
    whitelist(ModObject.blockDecoration2);
  }

  private static void whitelist(ModObject modObject) {
    if (modObject.getBlock() != null) {
      FMLInterModComms.sendMessage("chiselsandbits", "ignoreblocklogic", modObject.getBlock().getRegistryName());
      Log.info("Sending whitelist message to Chisel and Bits for block ", modObject);
    }
  }

  @SuppressWarnings("unused")
  private static void whitelist(Block block) {
    if (block != null) {
      FMLInterModComms.sendMessage("chiselsandbits", "ignoreblocklogic", block.getRegistryName());
      Log.info("Sending whitelist message to Chisel and Bits for block ", block);
    }
  }

}
