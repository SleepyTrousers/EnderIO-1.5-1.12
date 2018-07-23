package crazypants.enderio.endergy;

import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import crazypants.enderio.api.addon.IEnderIOAddon;
import crazypants.enderio.endergy.config.ConfigHandler;
import crazypants.enderio.endergy.init.CommonProxy;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkCheckHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@Mod(modid = EnderIOEndergy.MODID, name = EnderIOEndergy.MOD_NAME, version = EnderIOEndergy.VERSION, dependencies = EnderIOEndergy.DEPENDENCIES)
@EventBusSubscriber
public class EnderIOEndergy implements IEnderIOAddon {

  @NetworkCheckHandler
  @SideOnly(Side.CLIENT)
  public boolean checkModLists(Map<String, String> modList, Side side) {
    /*
     * On the client when showing the server list: Require the mod to be there and of the same version.
     *
     * On the client when connecting to a server: Require the mod to be there. Version check is done on the server.
     *
     * On the server when a client connects: Standard Forge version checks with a nice error message apply.
     *
     * On the integrated server when a client connects: Require the mod to be there and of the same version. Ugly error message.
     */
    return modList.keySet().contains(MODID) && VERSION.equals(modList.get(MODID));
  }

  public static final @Nonnull String MODID = "enderioendergy";
  public static final @Nonnull String DOMAIN = "enderio";
  public static final @Nonnull String MOD_NAME = "Ender IO Endergy";
  public static final @Nonnull String VERSION = "@VERSION@";

  private static final @Nonnull String DEFAULT_DEPENDENCIES = "after:" + crazypants.enderio.base.EnderIO.MODID;
  public static final @Nonnull String DEPENDENCIES = DEFAULT_DEPENDENCIES;

  @SidedProxy(clientSide = "crazypants.enderio.endergy.init.ClientProxy", serverSide = "crazypants.enderio.endergy.init.CommonProxy")
  public static CommonProxy proxy;

  @EventHandler
  public static void init(@Nonnull FMLPreInitializationEvent event) {
    ConfigHandler.init(event);
    proxy.preInit();
  }

  @EventHandler
  public static void init(FMLInitializationEvent event) {
  }

  @EventHandler
  public static void init(FMLPostInitializationEvent event) {
  }

  @Override
  @Nullable
  public Configuration getConfiguration() {
    return ConfigHandler.config;
  }

}
