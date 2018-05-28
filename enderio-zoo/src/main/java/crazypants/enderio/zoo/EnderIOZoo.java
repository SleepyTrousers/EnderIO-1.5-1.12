package crazypants.enderio.zoo;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.commons.lang3.tuple.Triple;

import com.enderio.core.common.Lang;
import com.enderio.core.common.util.NNList;

import crazypants.enderio.api.addon.IEnderIOAddon;
import crazypants.enderio.base.config.recipes.RecipeFactory;
import crazypants.enderio.zoo.config.ConfigHandler;
import crazypants.enderio.zoo.init.CommonProxy;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = EnderIOZoo.MODID, name = EnderIOZoo.MOD_NAME, version = EnderIOZoo.VERSION, dependencies = EnderIOZoo.DEPENDENCIES)
@EventBusSubscriber
public class EnderIOZoo implements IEnderIOAddon {

  public static final @Nonnull String MODID = "enderzoo";
  public static final @Nonnull String DOMAIN = "enderio";
  public static final @Nonnull String MOD_NAME = "Ender Zoo";
  public static final @Nonnull String VERSION = "@VERSION@";

  private static final @Nonnull String DEFAULT_DEPENDENCIES = "after:" + crazypants.enderio.base.EnderIO.MODID;
  public static final @Nonnull String DEPENDENCIES = DEFAULT_DEPENDENCIES;

  @SidedProxy(clientSide = "crazypants.enderio.zoo.init.ClientProxy", serverSide = "crazypants.enderio.zoo.init.CommonProxy")
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

  public static final @Nonnull Lang lang = new Lang(DOMAIN);

  @Override
  @Nullable
  public Configuration getConfiguration() {
    return ConfigHandler.config;
  }

  @Override
  @Nonnull
  public NNList<Triple<Integer, RecipeFactory, String>> getRecipeFiles() {
    return new NNList<>(Triple.of(2, null, "enderzoo"));
  }

}
