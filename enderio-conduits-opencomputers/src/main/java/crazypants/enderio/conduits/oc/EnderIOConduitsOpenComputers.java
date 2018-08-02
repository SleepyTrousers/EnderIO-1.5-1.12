package crazypants.enderio.conduits.oc;

import java.util.Map;

import javax.annotation.Nonnull;

import org.apache.commons.lang3.tuple.Triple;

import com.enderio.core.common.util.NNList;

import crazypants.enderio.api.addon.IEnderIOAddon;
import crazypants.enderio.base.Log;
import crazypants.enderio.base.config.recipes.RecipeFactory;
import crazypants.enderio.base.init.RegisterModObject;
import crazypants.enderio.conduits.conduit.TileConduitBundle;
import crazypants.enderio.conduits.oc.init.ConduitOpenComputersObject;
import crazypants.enderio.conduits.oc.network.PacketHandler;
import li.cil.oc.api.network.Environment;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.NetworkCheckHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@Mod(modid = EnderIOConduitsOpenComputers.MODID, name = EnderIOConduitsOpenComputers.MOD_NAME, version = EnderIOConduitsOpenComputers.VERSION, dependencies = EnderIOConduitsOpenComputers.DEPENDENCIES)
@EventBusSubscriber
public class EnderIOConduitsOpenComputers implements IEnderIOAddon {

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
      PacketHandler.init(event);
    }
  }

  @EventHandler
  public static void init(FMLPostInitializationEvent event) {
    if (OCUtil.isOCEnabled()) {
    }
  }

  @SubscribeEvent
  public static void registerConduits(@Nonnull RegisterModObject event) {
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
