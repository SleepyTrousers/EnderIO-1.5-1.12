package crazypants.enderio.conduits.oc;

import javax.annotation.Nonnull;

import org.apache.commons.lang3.tuple.Triple;

import com.enderio.core.common.util.NNList;

import crazypants.enderio.api.addon.IEnderIOAddon;
import crazypants.enderio.base.Log;
import crazypants.enderio.base.config.recipes.RecipeFactory;
import crazypants.enderio.conduits.conduit.TileConduitBundle;
import crazypants.enderio.conduits.oc.init.ConduitOpenComputersObject;
import li.cil.oc.api.network.Environment;
import net.minecraft.block.Block;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod(modid = EnderIOConduitsOpenComputers.MODID, name = EnderIOConduitsOpenComputers.MOD_NAME, version = EnderIOConduitsOpenComputers.VERSION, dependencies = EnderIOConduitsOpenComputers.DEPENDENCIES)
@EventBusSubscriber
public class EnderIOConduitsOpenComputers implements IEnderIOAddon {

  public static final @Nonnull String MODID = "enderioconduitsopencomputers";
  public static final @Nonnull String DOMAIN = "enderio";
  public static final @Nonnull String MOD_NAME = "Ender IO Conduits OpenComputers";
  public static final @Nonnull String VERSION = "@VERSION@";

  private static final @Nonnull String DEFAULT_DEPENDENCIES = "after:" + crazypants.enderio.base.EnderIO.MODID;
  public static final @Nonnull String DEPENDENCIES = DEFAULT_DEPENDENCIES;

  @EventHandler
  public static void init(@Nonnull FMLPreInitializationEvent event) {
    if (OCUtil.isOCEnabled()) {
      Log.warn("OpenComputers conduits loaded. Let your networks connect!");
    } else {
      Log.warn("OpenComputers conduits NOT loaded. OpenComputers is not installed");
    }
  }

  @EventHandler
  public static void init(FMLInitializationEvent event) {
    if (OCUtil.isOCEnabled()) {
      // Sanity checking
      System.out.println("Mixin successful? " + Environment.class.isAssignableFrom(TileConduitBundle.class));
    }
  }

  @EventHandler
  public static void init(FMLPostInitializationEvent event) {
    if (OCUtil.isOCEnabled()) {
    }
  }

  @SubscribeEvent(priority = EventPriority.HIGHEST)
  public static void registerConduits(@Nonnull RegistryEvent.Register<Block> event) {
    if (OCUtil.isOCEnabled()) {
      ConduitOpenComputersObject.registerBlocksEarly(event);
    }
  }

  @Override
  @Nonnull
  public NNList<Triple<Integer, RecipeFactory, String>> getRecipeFiles() {
    if (OCUtil.isOCEnabled()) {
      return new NNList<>(Triple.of(2, null, "conduits-opencomputers"));
    }
    return NNList.emptyList();
  }

}
