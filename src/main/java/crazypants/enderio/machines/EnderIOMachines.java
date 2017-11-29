package crazypants.enderio.machines;

import javax.annotation.Nonnull;

import crazypants.enderio.machines.config.Config;
import crazypants.enderio.machines.machine.obelisk.render.ObeliskRenderManager;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import static crazypants.enderio.machines.EnderIOMachines.MODID;
import static crazypants.enderio.machines.EnderIOMachines.MOD_NAME;
import static crazypants.enderio.machines.EnderIOMachines.VERSION;

@Mod(modid = MODID, name = MOD_NAME, version = VERSION, guiFactory = "crazypants.enderio.machines.config.gui.ConfigFactory")
@EventBusSubscriber(Side.CLIENT)
public class EnderIOMachines {

  public static final @Nonnull String MODID = "enderio-machines";
  public static final @Nonnull String DOMAIN = "enderio";
  public static final @Nonnull String MOD_NAME = "Ender IO Machines";
  public static final @Nonnull String VERSION = "@VERSION@";
  
  @SubscribeEvent
  @SideOnly(Side.CLIENT)
  public static void onModelRegister(ModelRegistryEvent event) {
    ObeliskRenderManager.INSTANCE.registerRenderers();
  }

  @EventHandler
  public static void init(FMLPreInitializationEvent event) {
    Config.init(event);
  }

  @EventHandler
  public static void init(FMLInitializationEvent event) {
    Config.init(event);
  }

  @EventHandler
  public static void init(FMLPostInitializationEvent event) {
    Config.init(event);
  }

}
