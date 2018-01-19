package crazypants.enderio.integration.ftbl;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.common.Lang;

import crazypants.enderio.api.addon.IEnderIOAddon;
import crazypants.enderio.base.integration.IntegrationRegistry;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@Mod(modid = EnderIOIntegrationFtbl.MODID, name = EnderIOIntegrationFtbl.MOD_NAME, version = EnderIOIntegrationFtbl.VERSION, dependencies = EnderIOIntegrationFtbl.DEPENDENCIES)
@EventBusSubscriber(Side.CLIENT)
public class EnderIOIntegrationFtbl implements IEnderIOAddon {

  public static final @Nonnull String MODID = "enderiointegrationftbl";
  public static final @Nonnull String DOMAIN = "enderio";
  public static final @Nonnull String MOD_NAME = "Ender IO Integration FTBL";
  public static final @Nonnull String VERSION = "@VERSION@";

  private static final @Nonnull String DEFAULT_DEPENDENCIES = "after:" + crazypants.enderio.base.EnderIO.MODID;
  public static final @Nonnull String DEPENDENCIES = DEFAULT_DEPENDENCIES;

  @SubscribeEvent
  @SideOnly(Side.CLIENT)
  public static void onModelRegister(ModelRegistryEvent event) {
  }

  @EventHandler
  public static void init(FMLPreInitializationEvent event) {
  }

  @EventHandler
  public static void init(FMLInitializationEvent event) {
  }

  @EventHandler
  public static void init(FMLPostInitializationEvent event) {
  }

  @EventHandler
  public void serverLoad(FMLServerStartingEvent event) {
    // register server commands
    IntegrationRegistry.register(new FtblIntegration());
  }

  public static final @Nonnull Lang lang = new Lang(DOMAIN);

  @Override
  @Nullable
  public Configuration getConfiguration() {
    return null;
  }

}
