package crazypants.enderio.base.integration.chiselsandbits;

import javax.annotation.Nonnull;

import crazypants.enderio.base.EnderIO;
import crazypants.enderio.base.Log;
import crazypants.enderio.base.events.EnderIOLifecycleEvent;
import crazypants.enderio.base.init.ModObject;
import net.minecraft.block.Block;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.event.FMLInterModComms;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@EventBusSubscriber(modid = EnderIO.MODID)
public class CABIMC {

  @SubscribeEvent
  public static void init(@Nonnull EnderIOLifecycleEvent.Init.Normal event) {
    /* they don't work */
    // whitelist(ModObject.blockFusedQuartz);
    // for (FusedQuartzType glasstype : FusedQuartzType.values()) {
    // whitelist(glasstype.getBlock());
    // }
    /* they do work with only minor visual anomalies (direction is different, reverse quads are missing */
    whitelist(ModObject.blockDecoration1);
    whitelist(ModObject.blockDecoration2);
    whitelist(ModObject.blockDecoration3);
  }

  private static void whitelist(@Nonnull ModObject modObject) {
    if (modObject.getBlock() != null) {
      FMLInterModComms.sendMessage("chiselsandbits", "ignoreblocklogic", modObject.getBlockNN().getRegistryName());
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
