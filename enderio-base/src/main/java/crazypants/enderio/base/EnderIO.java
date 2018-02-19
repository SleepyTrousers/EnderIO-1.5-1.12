package crazypants.enderio.base;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.commons.lang3.tuple.Triple;

import com.enderio.core.common.Lang;
import com.enderio.core.common.util.NNList;
import com.enderio.core.common.util.NullHelper;
import com.google.common.collect.ImmutableList;

import crazypants.enderio.api.IMC;
import crazypants.enderio.api.addon.IEnderIOAddon;
import crazypants.enderio.base.conduit.geom.ConduitGeometryUtil;
import crazypants.enderio.base.conduit.redstone.ConnectivityTool;
import crazypants.enderio.base.config.Config;
import crazypants.enderio.base.config.config.DiagnosticsConfig;
import crazypants.enderio.base.config.recipes.RecipeFactory;
import crazypants.enderio.base.config.recipes.RecipeLoader;
import crazypants.enderio.base.diagnostics.ProfilerAntiReactor;
import crazypants.enderio.base.diagnostics.ProfilerDebugger;
import crazypants.enderio.base.fluid.FluidFuelRegister;
import crazypants.enderio.base.fluid.Fluids;
import crazypants.enderio.base.gui.handler.GuiHelper;
import crazypants.enderio.base.handler.ServerTickHandler;
import crazypants.enderio.base.handler.darksteel.DarkSteelController;
import crazypants.enderio.base.init.CommonProxy;
import crazypants.enderio.base.init.ModObjectRegistry;
import crazypants.enderio.base.integration.bigreactors.BRProxy;
import crazypants.enderio.base.integration.buildcraft.BuildcraftIntegration;
import crazypants.enderio.base.integration.chiselsandbits.CABIMC;
import crazypants.enderio.base.integration.te.TEUtil;
import crazypants.enderio.base.loot.Loot;
import crazypants.enderio.base.loot.LootManager;
import crazypants.enderio.base.material.OreDictionaryPreferences;
import crazypants.enderio.base.material.recipes.MaterialOredicts;
import crazypants.enderio.base.network.PacketHandler;
import crazypants.enderio.base.paint.PaintSourceValidator;
import crazypants.enderio.base.power.CapInjectHandler;
import crazypants.enderio.base.recipe.alloysmelter.AlloyRecipeManager;
import crazypants.enderio.base.recipe.sagmill.SagMillRecipeManager;
import crazypants.enderio.base.recipe.slicensplice.SliceAndSpliceRecipeManager;
import crazypants.enderio.base.recipe.soul.SoulBinderRecipeManager;
import crazypants.enderio.base.recipe.vat.VatRecipeManager;
import crazypants.enderio.base.transceiver.ServerChannelRegister;
import crazypants.enderio.util.CapturedMob;
import info.loenwind.scheduler.Celeb;
import info.loenwind.scheduler.Scheduler;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLInterModComms;
import net.minecraftforge.fml.common.event.FMLInterModComms.IMCEvent;
import net.minecraftforge.fml.common.event.FMLInterModComms.IMCMessage;
import net.minecraftforge.fml.common.event.FMLLoadCompleteEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerAboutToStartEvent;
import net.minecraftforge.fml.common.event.FMLServerStoppedEvent;

@Mod(modid = EnderIO.MODID, name = EnderIO.MOD_NAME, version = EnderIO.VERSION, dependencies = EnderIO.DEPENDENCIES, guiFactory = "crazypants.enderio.base.config.ConfigFactoryEIO")
public class EnderIO implements IEnderIOAddon {

  public static final @Nonnull String MODID = "enderio";
  public static final @Nonnull String DOMAIN = "enderio";
  public static final @Nonnull String MOD_NAME = "Ender IO";
  public static final @Nonnull String VERSION = "@VERSION@";

  private static final @Nonnull String DEFAULT_DEPENDENCIES = "after:endercore;after:hwyla;after:jei";
  public static final @Nonnull String DEPENDENCIES = DEFAULT_DEPENDENCIES;

  @Instance(MODID)
  public static EnderIO instance;

  @SidedProxy(clientSide = "crazypants.enderio.base.init.ClientProxy", serverSide = "crazypants.enderio.base.init.CommonProxy")
  public static CommonProxy proxy;

  public static final @Nonnull Lang lang = new Lang(MODID);

  // prePreInit
  static {
    FluidRegistry.enableUniversalBucket();
    CapInjectHandler.loadClass();
  }

  @EventHandler
  public void preInit(@Nonnull FMLPreInitializationEvent event) {
    Log.debug("PHASE PRE-INIT START");

    Config.init(event);

    proxy.loadIcons();

    ConduitGeometryUtil.setupBounds((float) Config.conduitScale);

    DarkSteelController.init(event);

    Loot.init(event);

    ServerChannelRegister.init(event);

    proxy.init(event);

    Log.debug("PHASE PRE-INIT END");
  }

  @EventHandler
  public void load(@Nonnull FMLInitializationEvent event) {
    Log.debug("PHASE INIT START");

    Fluids.registerFuels();

    ModObjectRegistry.init(event);

    BRProxy.init(event);

    CABIMC.init(event);

    PacketHandler.init(event);

    GuiHelper.init(event);

    proxy.init(event);

    Log.debug("PHASE INIT END");
  }

  @EventHandler
  public void onImc(@Nonnull IMCEvent event) {
    Log.debug("PHASE IMC START");
    processImc(event.getMessages());

    /*
     * This is a mess.
     * 
     * Items are registered in the registry event between preInit and Init. OreDicts are registered in Init (because Lex says so). MaterialRecipes.init() must
     * run after all oreDict registrations. RecipeLoader.addRecipes() should run in the registry event.
     * 
     * At the moment we can delay RecipeLoader.addRecipes(), but still PostInit is too late as there's code that depends on it being done for all submods.
     */

    MaterialOredicts.init(event); // handles oredict registration for dependent entries

    // This must be loaded before parsing the recipes so we get the preferred
    // outputs
    OreDictionaryPreferences.init(event);

    RecipeLoader.addRecipes();

    // END mess

    Log.debug("PHASE IMC END");
  }

  @EventHandler
  public void postInit(@Nonnull FMLPostInitializationEvent event) {
    Log.debug("PHASE POST-INIT START");

    Config.init(event);

    ModObjectRegistry.init(event);

    LootManager.init(event);

    SagMillRecipeManager.getInstance().create();
    AlloyRecipeManager.getInstance().create();
    SliceAndSpliceRecipeManager.getInstance().create();
    VatRecipeManager.getInstance().loadRecipesFromConfig();
    SoulBinderRecipeManager.getInstance().addDefaultRecipes();
    PaintSourceValidator.instance.loadConfig();

    BuildcraftIntegration.init(event);
    TEUtil.init(event);

    proxy.init(event);

    Celeb.init(event);
    Scheduler.instance.start();

    Log.debug("PHASE POST-INIT END");
  }

  @EventHandler
  public void loadComplete(@Nonnull FMLLoadCompleteEvent event) {
    Log.debug("PHASE LOAD COMPLETE START");

    // Some mods send IMCs during PostInit, so we catch them here.
    processImc(FMLInterModComms.fetchRuntimeMessages(this));

    Log.debug("PHASE LOAD COMPLETE END");
  }

  @EventHandler
  public void serverStopped(@Nonnull FMLServerStoppedEvent event) {
    ServerTickHandler.flush();
    ServerChannelRegister.instance.reset();
  }

  @EventHandler
  public static void onServerStart(FMLServerAboutToStartEvent event) {
    ServerChannelRegister.instance.reset();
  }

  void processImc(ImmutableList<IMCMessage> messages) {
    for (IMCMessage msg : messages) {
      String key = msg.key;
      Log.info("Processing IMC message " + key + " from " + msg.getSender());
      try {
        if (msg.isStringMessage()) {
          String value = msg.getStringValue();
          if (value == null) {
            return;
          }
          if (IMC.XML_RECIPE.equals(key)) {
            RecipeLoader.addIMCRecipe(value);
          } else if (IMC.VAT_RECIPE.equals(key)) {
            VatRecipeManager.getInstance().addCustomRecipes(value);
          } else if (IMC.TELEPORT_BLACKLIST_ADD.equals(key)) {
            Config.TRAVEL_BLACKLIST.add(value);
          } else if (IMC.REDSTONE_CONNECTABLE_ADD.equals(key)) {
            ConnectivityTool.registerRedstoneAware(value);
          }
        } else if (msg.isResourceLocationMessage()) {
          ResourceLocation value = msg.getResourceLocationValue();
          if (IMC.SOUL_VIAL_BLACKLIST.equals(key)) {
            CapturedMob.addToBlackList(value);
          } else if (IMC.SOUL_VIAL_UNSPAWNABLELIST.equals(key)) {
            CapturedMob.addToUnspawnableList(value);
          }
        } else if (msg.isNBTMessage()) {
          final NBTTagCompound nbtValue = msg.getNBTValue();
          if (nbtValue == null) {
            return;
          }
          if (IMC.SOUL_BINDER_RECIPE.equals(key)) {
            SoulBinderRecipeManager.getInstance().addRecipeFromNBT(nbtValue);
          } else if (IMC.FLUID_FUEL_ADD.equals(key)) {
            FluidFuelRegister.instance.addFuel(nbtValue);
          } else if (IMC.FLUID_COOLANT_ADD.equals(key)) {
            FluidFuelRegister.instance.addCoolant(nbtValue);
          }
        } else if (msg.isItemStackMessage()) {
          if (IMC.PAINTER_WHITELIST_ADD.equals(key)) {
            PaintSourceValidator.instance.addToWhitelist(msg.getItemStackValue());
          } else if (IMC.PAINTER_BLACKLIST_ADD.equals(key)) {
            PaintSourceValidator.instance.addToBlacklist(msg.getItemStackValue());
          }
        }
      } catch (Exception e) {
        e.printStackTrace();
        Log.error("Error occurred handling IMC message " + key + " from " + msg.getSender());
      }
    }
  }

  public static @Nonnull EnderIO getInstance() {
    return NullHelper.notnullF(instance, "instance is missing");
  }

  @Override
  @Nullable
  public Configuration getConfiguration() {
    return Config.config;
  }

  @EventHandler
  public void onServerAboutToStart(FMLServerAboutToStartEvent event) {
    if (DiagnosticsConfig.debugProfilerTracer.get()) {
      ProfilerDebugger.init(event);
    } else if (DiagnosticsConfig.debugProfilerAntiNuclearActivist.get()) {
      ProfilerAntiReactor.init(event);
    }
  }

  @Override
  @Nonnull
  public NNList<Triple<Integer, RecipeFactory, String>> getRecipeFiles() {
    return new NNList<>(Triple.of(0, null, "aliases"), Triple.of(1, null, "materials"), Triple.of(1, null, "items"), Triple.of(1, null, "base"));
  }

  @Override
  @Nonnull
  public NNList<String> getExampleFiles() {
    return new NNList<>("peaceful", "easy_recipes", "hard_recipes");
  }
}
