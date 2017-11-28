package crazypants.enderio.machine;

import static crazypants.enderio.machine.EnderIOMachines.MODID;
import static crazypants.enderio.machine.EnderIOMachines.MOD_NAME;
import static crazypants.enderio.machine.EnderIOMachines.VERSION;

import javax.annotation.Nonnull;

import crazypants.enderio.machine.obelisk.render.ObeliskRenderManager;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@Mod(modid = MODID, name = MOD_NAME, version = VERSION)
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
}
