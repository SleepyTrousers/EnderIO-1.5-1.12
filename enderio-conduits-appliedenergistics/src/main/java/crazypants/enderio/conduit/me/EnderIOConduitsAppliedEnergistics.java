package crazypants.enderio.conduit.me;

import java.util.Map;

import javax.annotation.Nonnull;

import org.apache.commons.lang3.tuple.Triple;

import com.enderio.core.common.transform.EnderCorePlugin;
import com.enderio.core.common.util.NNList;

import appeng.api.networking.IGridHost;
import crazypants.enderio.api.addon.IEnderIOAddon;
import crazypants.enderio.base.Log;
import crazypants.enderio.base.config.recipes.RecipeFactory;
import crazypants.enderio.base.init.RegisterModObject;
import crazypants.enderio.conduit.me.init.ConduitAppliedEnergisticsObject;
import crazypants.enderio.conduits.conduit.TileConduitBundle;
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

@Mod(modid = EnderIOConduitsAppliedEnergistics.MODID, name = EnderIOConduitsAppliedEnergistics.MOD_NAME, version = EnderIOConduitsAppliedEnergistics.VERSION, dependencies = EnderIOConduitsAppliedEnergistics.DEPENDENCIES)
@EventBusSubscriber
public class EnderIOConduitsAppliedEnergistics implements IEnderIOAddon {

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

  public static final @Nonnull String MODID = "enderioconduitsappliedenergistics";
  public static final @Nonnull String DOMAIN = "enderio";
  public static final @Nonnull String MOD_NAME = "Ender IO Conduits Applied Energistics";
  public static final @Nonnull String VERSION = "@VERSION@";

  private static final @Nonnull String DEFAULT_DEPENDENCIES = "after:" + crazypants.enderio.base.EnderIO.MODID;
  public static final @Nonnull String DEPENDENCIES = DEFAULT_DEPENDENCIES;

  public EnderIOConduitsAppliedEnergistics() {
    EnderCorePlugin.instance().loadMixinSources(this);
  }

  @EventHandler
  public static void init(@Nonnull FMLPreInitializationEvent event) {
    if (MEUtil.isMEEnabled()) {
      Log.warn("Applied Energistics conduits loaded. Let your networks connect!");
    } else {
      Log.warn("Applied Energistics conduits NOT loaded. Applied Energistics is not installed");
    }
  }

  @EventHandler
  public static void init(FMLInitializationEvent event) {
    if (MEUtil.isMEEnabled()) {
      // Sanity checking
      System.out.println("Mixin successful? " + IGridHost.class.isAssignableFrom(TileConduitBundle.class));
    }
  }

  @EventHandler
  public static void init(FMLPostInitializationEvent event) {
    if (MEUtil.isMEEnabled()) {
    }
  }

  @SubscribeEvent
  public static void registerConduits(@Nonnull RegisterModObject event) {
    if (MEUtil.isMEEnabled()) {
      ConduitAppliedEnergisticsObject.registerBlocksEarly(event);
    }
  }

  @Override
  @Nonnull
  public NNList<Triple<Integer, RecipeFactory, String>> getRecipeFiles() {
    if (MEUtil.isMEEnabled()) {
      if (MeUtil2.isFluixEnabled()) {
        return new NNList<>(Triple.of(2, null, "conduits-applied-energistics"));
      } else {
        Log.error("[" + MOD_NAME
            + "]: AE2 Fluix and Pure Fluix or Quartz Fibres are disabled. There will be no way to craft ME conduits unless YOU add a custom recipe.");
      }
    }
    return NNList.emptyList();
  }

}
