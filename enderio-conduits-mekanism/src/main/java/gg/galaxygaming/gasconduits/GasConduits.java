package gg.galaxygaming.gasconduits;

import com.enderio.core.common.mixin.SimpleMixinLoader;
import com.enderio.core.common.util.NNList;
import crazypants.enderio.api.addon.IEnderIOAddon;
import crazypants.enderio.base.Log;
import crazypants.enderio.base.config.ConfigHandlerEIO;
import crazypants.enderio.base.config.recipes.RecipeFactory;
import crazypants.enderio.base.init.RegisterModObject;
import gg.galaxygaming.gasconduits.common.conduit.GasConduitObject;
import gg.galaxygaming.gasconduits.common.config.Config;
import gg.galaxygaming.gasconduits.common.network.PacketHandler;
import info.loenwind.autoconfig.ConfigHandler;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.NetworkCheckHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.commons.lang3.tuple.Triple;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Map;

@Mod(modid = GasConduitsConstants.MOD_ID, name = GasConduitsConstants.MOD_NAME, version = GasConduitsConstants.VERSION,
        dependencies = GasConduits.DEPENDENCIES, acceptedMinecraftVersions = GasConduitsConstants.MC_VERSION)
@Mod.EventBusSubscriber(modid = GasConduitsConstants.MOD_ID)
public class GasConduits implements IEnderIOAddon {

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
        return modList.keySet().contains(GasConduitsConstants.MOD_ID) && GasConduitsConstants.VERSION.equals(modList.get(GasConduitsConstants.MOD_ID));
    }

    private static final @Nonnull String DEFAULT_DEPENDENCIES = "after:" + crazypants.enderio.base.EnderIO.MODID;
    public static final @Nonnull String DEPENDENCIES = DEFAULT_DEPENDENCIES;

    public static Logger logger;

    private static ConfigHandler configHandler;

    public GasConduits() {
        SimpleMixinLoader.loadMixinSources(this);
    }

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        if (isLoaded()) {
            logger = event.getModLog();
            configHandler = new ConfigHandlerEIO(event, Config.F);
            Log.warn("Mekanism Gas conduits loaded. Let your networks connect!");
        } else {
            Log.warn("Mekanism Gas conduits NOT loaded. Mekanism is not installed");
        }
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        PacketHandler.init(event);
    }

    @SubscribeEvent
    public static void registerBlocksEarly(@Nonnull RegisterModObject event) {
        if (isLoaded()) {
            GasConduitObject.registerBlocksEarly(event);
        }
    }

    @Override
    @Nullable
    public Configuration getConfiguration() {
        return Config.F.getConfig();
    }

    @Override
    @Nonnull
    public NNList<Triple<Integer, RecipeFactory, String>> getRecipeFiles() {
        if (isLoaded()) {
            return new NNList<>(Triple.of(2, null, "conduits-gas"));
        }
        return NNList.emptyList();
    }

    public static boolean isLoaded() {
        return Loader.isModLoaded("mekanism");
    }
}
