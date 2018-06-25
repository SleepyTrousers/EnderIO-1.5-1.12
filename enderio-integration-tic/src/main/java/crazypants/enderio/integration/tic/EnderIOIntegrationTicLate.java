package crazypants.enderio.integration.tic;

import java.util.Map;

import javax.annotation.Nonnull;

import crazypants.enderio.api.addon.IEnderIOAddon;
import crazypants.enderio.base.Log;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkCheckHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@Mod(modid = EnderIOIntegrationTicLate.MODID, name = EnderIOIntegrationTicLate.MOD_NAME, version = EnderIOIntegrationTicLate.VERSION, dependencies = EnderIOIntegrationTicLate.DEFAULT_DEPENDENCIES)
@EventBusSubscriber
public class EnderIOIntegrationTicLate implements IEnderIOAddon {

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

  public static final @Nonnull String MODID = "enderiointegrationticlate";
  public static final @Nonnull String DOMAIN = "enderio";
  public static final @Nonnull String MOD_NAME = "Ender IO Integration with Tinkers' Construct";
  public static final @Nonnull String VERSION = "@VERSION@";

  public static final @Nonnull String DEFAULT_DEPENDENCIES = "after:tconstruct;after:enderiointegrationtic;after:enderio";

  @EventHandler
  public static void init(FMLPostInitializationEvent event) {
    if (EnderIOIntegrationTic.isLoaded()) {
      Log.debug("PHASE POST-INIT EIO TIC L");
      TicControl.postInitAfterTic(event);
    }
  }

}
