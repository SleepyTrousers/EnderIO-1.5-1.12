package crazypants.enderio.machines;

import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.commons.lang3.tuple.Triple;

import com.enderio.core.common.Lang;
import com.enderio.core.common.transform.EnderCorePlugin;
import com.enderio.core.common.util.NNList;

import crazypants.enderio.api.addon.IEnderIOAddon;
import crazypants.enderio.base.config.ConfigHandlerEIO;
import crazypants.enderio.base.config.recipes.RecipeFactory;
import crazypants.enderio.machines.config.Config;
import crazypants.enderio.machines.init.EIOMDataFixer;
import crazypants.enderio.machines.machine.obelisk.render.ObeliskRenderManager;
import crazypants.enderio.machines.network.PacketHandler;
import info.loenwind.autoconfig.ConfigHandler;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.common.config.Configuration;
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

@Mod(modid = EnderIOMachines.MODID, name = EnderIOMachines.MOD_NAME, version = EnderIOMachines.VERSION, dependencies = EnderIOMachines.DEPENDENCIES)
@EventBusSubscriber(Side.CLIENT)
public class EnderIOMachines implements IEnderIOAddon {

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

  public static final @Nonnull String MODID = "enderiomachines";
  public static final @Nonnull String DOMAIN = "enderio";
  public static final @Nonnull String MOD_NAME = "Ender IO Machines";
  public static final @Nonnull String VERSION = "@VERSION@";

  private static final @Nonnull String DEFAULT_DEPENDENCIES = "after:" + crazypants.enderio.base.EnderIO.MODID;
  public static final @Nonnull String DEPENDENCIES = DEFAULT_DEPENDENCIES;
  @SuppressWarnings("unused")
  private static ConfigHandler configHandler;

  public EnderIOMachines() {
    EnderCorePlugin.instance().loadMixinSources(this);
  }

  @SubscribeEvent
  @SideOnly(Side.CLIENT)
  public static void onModelRegister(ModelRegistryEvent event) {
    ObeliskRenderManager.INSTANCE.registerRenderers();
  }

  @EventHandler
  public static void init(@Nonnull FMLPreInitializationEvent event) {
    configHandler = new ConfigHandlerEIO(event, Config.F);
    EIOMDataFixer.register();
  }

  @EventHandler
  public static void init(FMLInitializationEvent event) {
    PacketHandler.init(event);
  }

  @EventHandler
  public static void init(FMLPostInitializationEvent event) {
  }

  public static final @Nonnull Lang lang = new Lang(DOMAIN);

  @Override
  @Nullable
  public Configuration getConfiguration() {
    return Config.F.getConfig();
  }

  @Override
  @Nonnull
  public NNList<Triple<Integer, RecipeFactory, String>> getRecipeFiles() {
    return new NNList<>(Triple.of(2, null, "machines"), Triple.of(2, null, "sagmill"), Triple.of(3, null, "sagmill_modded"), Triple.of(3, null, "sagmill_ores"),
        Triple.of(3, null, "sagmill_metals"), Triple.of(3, null, "sagmill_vanilla"), Triple.of(3, null, "sagmill_vanilla2modded"),
        Triple.of(3, null, "sagmill_silentgems"), Triple.of(3, null, "vat"), Triple.of(3, null, "enchanter"), Triple.of(3, null, "spawner"),
        Triple.of(9, null, "capacitor_machines"), Triple.of(3, null, "integration_railcraft_recipes"), Triple.of(3, null, "soulbinder"),
        Triple.of(3, null, "tank"));
  }

  @Override
  @Nonnull
  public NNList<String> getExampleFiles() {
    return new NNList<>("machines_easy_recipes", "machines_easy_recipes", "infinity", "cheap_machines", "cheaty_spawner", "sagmill_dupe_recipe_patches");
  }

}
