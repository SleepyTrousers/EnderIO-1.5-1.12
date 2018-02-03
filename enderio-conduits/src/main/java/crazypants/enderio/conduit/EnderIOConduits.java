package crazypants.enderio.conduit;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import crazypants.enderio.api.addon.IEnderIOAddon;
import crazypants.enderio.conduit.config.ConfigHandler;
import crazypants.enderio.conduit.config.RecipeLoaderConduits;
import crazypants.enderio.conduit.init.CommonProxy;
import crazypants.enderio.conduit.packet.PacketHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = EnderIOConduits.MODID, name = EnderIOConduits.MOD_NAME, version = EnderIOConduits.VERSION, dependencies = EnderIOConduits.DEPENDENCIES)
public class EnderIOConduits implements IEnderIOAddon {

  public static final @Nonnull String MODID = "enderioconduits";
  public static final @Nonnull String DOMAIN = "enderio";
  public static final @Nonnull String MOD_NAME = "Ender IO Conduits";
  public static final @Nonnull String VERSION = "@VERSION@";

  private static final @Nonnull String DEFAULT_DEPENDENCIES = "after:" + crazypants.enderio.base.EnderIO.MODID;
  public static final @Nonnull String DEPENDENCIES = DEFAULT_DEPENDENCIES;

  @SidedProxy(clientSide = "crazypants.enderio.conduit.init.ClientProxy", serverSide = "crazypants.enderio.conduit.init.CommonProxy")
  public static CommonProxy proxy;

  @Override
  @Nullable
  public Configuration getConfiguration() {
    return ConfigHandler.config;
  }

  @EventHandler
  public void preInit(@Nonnull FMLPreInitializationEvent event) {
    ConfigHandler.init(event);
    proxy.init(event);
  }

  @EventHandler
  public void init(@Nonnull FMLInitializationEvent event) {
    ConfigHandler.init(event);
    proxy.init(event);
    PacketHandler.init(event);
    MinecraftForge.EVENT_BUS.register(ConduitNetworkTickHandler.instance);
  }

  @EventHandler
  public void postInit(@Nonnull FMLPostInitializationEvent event) {
    ConfigHandler.init(event);
    RecipeLoaderConduits.addRecipes();
    proxy.init(event);
  }

}
