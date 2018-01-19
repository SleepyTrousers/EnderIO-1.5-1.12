package crazypants.enderio.integration.ftbl;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.common.Lang;

import crazypants.enderio.api.addon.IEnderIOAddon;
import crazypants.enderio.base.Log;
import crazypants.enderio.base.integration.IIntegration;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod(modid = EnderIOIntegrationFtbl.MODID, name = EnderIOIntegrationFtbl.MOD_NAME, version = EnderIOIntegrationFtbl.VERSION, dependencies = EnderIOIntegrationFtbl.DEPENDENCIES)
@EventBusSubscriber
public class EnderIOIntegrationFtbl implements IEnderIOAddon {

  public static final @Nonnull String MODID = "enderiointegrationftbl";
  public static final @Nonnull String DOMAIN = "enderio";
  public static final @Nonnull String MOD_NAME = "Ender IO Integration FTBL";
  public static final @Nonnull String VERSION = "@VERSION@";

  private static final @Nonnull String DEFAULT_DEPENDENCIES = "after:" + crazypants.enderio.base.EnderIO.MODID;
  public static final @Nonnull String DEPENDENCIES = DEFAULT_DEPENDENCIES;

  @EventHandler
  public static void init(FMLPreInitializationEvent event) {
  }

  @EventHandler
  public static void init(FMLInitializationEvent event) {
  }

  @EventHandler
  public static void init(FMLPostInitializationEvent event) {
  }

  @SubscribeEvent
  public static void register(@Nonnull RegistryEvent.Register<IIntegration> event) {
    event.getRegistry().register(new FtblIntegration().setRegistryName(MODID, "ftbl"));
    Log.info("FTBL integration fully loaded");
  }

  public static final @Nonnull Lang lang = new Lang(DOMAIN);

  @Override
  @Nullable
  public Configuration getConfiguration() {
    return null;
  }

}
