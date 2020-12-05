package crazypants.enderio.base;

import java.util.Map;

import javax.annotation.Nonnull;

import org.apache.commons.lang3.tuple.Triple;

import com.enderio.core.common.Lang;
import com.enderio.core.common.mixin.SimpleMixinLoader;
import com.enderio.core.common.util.NNList;
import com.enderio.core.common.util.NullHelper;
import com.google.common.collect.ImmutableList;

import crazypants.enderio.api.IMC;
import crazypants.enderio.api.addon.IEnderIOAddon;
import crazypants.enderio.base.capacitor.CapacitorKeyRegistry;
import crazypants.enderio.base.conduit.redstone.ConnectivityTool;
import crazypants.enderio.base.config.Config;
import crazypants.enderio.base.config.config.BaseConfig;
import crazypants.enderio.base.config.config.DiagnosticsConfig;
import crazypants.enderio.base.config.config.PersonalConfig;
import crazypants.enderio.base.config.config.TeleportConfig;
import crazypants.enderio.base.config.recipes.RecipeFactory;
import crazypants.enderio.base.config.recipes.RecipeLoader;
import crazypants.enderio.base.diagnostics.EnderIOCrashCallable;
import crazypants.enderio.base.diagnostics.ProfilerAntiReactor;
import crazypants.enderio.base.diagnostics.ProfilerDebugger;
import crazypants.enderio.base.events.EnderIOLifecycleEvent;
import crazypants.enderio.base.fluid.FluidFuelRegister;
import crazypants.enderio.base.handler.ServerTickHandler;
import crazypants.enderio.base.init.CommonProxy;
import crazypants.enderio.base.init.ModObject;
import crazypants.enderio.base.init.ModObjectRegistry;
import crazypants.enderio.base.integration.buildcraft.BuildcraftIntegration;
import crazypants.enderio.base.integration.railcraft.RailcraftUtil;
import crazypants.enderio.base.material.recipes.MaterialOredicts;
import crazypants.enderio.base.network.PacketHandler;
import crazypants.enderio.base.paint.PaintSourceValidator;
import crazypants.enderio.base.recipe.sagmill.SagMillRecipeManager;
import crazypants.enderio.base.recipe.slicensplice.SliceAndSpliceRecipeManager;
import crazypants.enderio.base.recipe.spawner.EntityDataRegistry;
import crazypants.enderio.base.recipe.vat.VatRecipeManager;
import crazypants.enderio.base.scheduler.Celeb;
import crazypants.enderio.base.scheduler.Scheduler;
import crazypants.enderio.base.transceiver.ServerChannelRegister;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.crash.ICrashReportDetail;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fml.common.FMLCommonHandler;
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
import net.minecraftforge.fml.common.network.NetworkCheckHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.server.permission.DefaultPermissionHandler;
import net.minecraftforge.server.permission.PermissionAPI;

@Mod(modid = EnderIO.MODID, name = EnderIO.MOD_NAME, version = EnderIO.VERSION, dependencies = EnderIO.DEPENDENCIES, guiFactory = "crazypants.enderio.base.config.GUIFactory")
public class EnderIO implements IEnderIOAddon {

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
  }

  @SuppressWarnings("unused")
  private static Config configHandler;

  public EnderIO() {
    SimpleMixinLoader.loadMixinSources(this);
    startupChecks();
  }

  @EventHandler
  public void preInit(@Nonnull FMLPreInitializationEvent event) {
    Log.debug("PHASE PRE-INIT START");

    MinecraftForge.EVENT_BUS.post(new EnderIOLifecycleEvent.Config.Pre());
    configHandler = new Config(event, BaseConfig.F, DOMAIN);
    MinecraftForge.EVENT_BUS.post(new EnderIOLifecycleEvent.Config.Post());

    MinecraftForge.EVENT_BUS.post(new EnderIOLifecycleEvent.PreInit());

    Log.debug("PHASE PRE-INIT END");
  }

  @EventHandler
  public void load(@Nonnull FMLInitializationEvent event) {
    Log.debug("PHASE INIT START");

    initCrashData(); // after blocks have been created

    MinecraftForge.EVENT_BUS.post(new EnderIOLifecycleEvent.Init.Pre(event));

    PacketHandler.init(event);

    MinecraftForge.EVENT_BUS.post(new EnderIOLifecycleEvent.Init.Normal(event));

    MinecraftForge.EVENT_BUS.post(new EnderIOLifecycleEvent.Init.Post(event));

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

    MaterialOredicts.checkOreRegistrations();

    RecipeLoader.addRecipes();

    CapacitorKeyRegistry.validate();

    RailcraftUtil.registerFuels();

    // END mess

    Log.debug("PHASE IMC END");
  }

  @EventHandler
  public void postInit(@Nonnull FMLPostInitializationEvent event) {
    Log.debug("PHASE POST-INIT START");

    MinecraftForge.EVENT_BUS.post(new EnderIOLifecycleEvent.PostInit.Pre());

    ModObjectRegistry.init(event);

    SagMillRecipeManager.getInstance().create();
    SliceAndSpliceRecipeManager.getInstance().create();
    VatRecipeManager.getInstance().create();
    PaintSourceValidator.instance.loadConfig();

    BuildcraftIntegration.init(event);
    // TEUtil.init(event);

    Celeb.init(event);
    Scheduler.instance.start();

    MinecraftForge.EVENT_BUS.post(new EnderIOLifecycleEvent.PostInit.Post());

    Log.debug("PHASE POST-INIT END");
    // crazypants.enderio.base.init.ModObjectRegistry.dumpItems();
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
      Log.info("Processing IMC message ", key, " from ", msg.getSender());
      try {
        if (IMC.ENABLE_PAINTING.equals(key)) {
          PersonalConfig.TooltipPaintEnum.setPainterAvailable();
        } else if (msg.isStringMessage()) {
          String value = msg.getStringValue();
          if (value == null) {
            return;
          }
          if (IMC.XML_RECIPE.equals(key)) {
            RecipeLoader.addIMCRecipe(msg.getSender(), false, value);
          } else if (IMC.XML_RECIPE_FILE.equals(key)) {
            RecipeLoader.addIMCRecipe(msg.getSender(), true, value);
          } else if (IMC.TELEPORT_BLACKLIST_ADD.equals(key)) {
            TeleportConfig.blockBlacklist.get().add(value);
          } else if (IMC.REDSTONE_CONNECTABLE_ADD.equals(key)) {
            ConnectivityTool.registerRedstoneAware(value);
          }
        } else if (msg.isResourceLocationMessage()) {
          ResourceLocation value = msg.getResourceLocationValue();
          if (IMC.SOUL_VIAL_BLACKLIST.equals(key)) {
            EntityDataRegistry.getInstance().addToBlacklistSoulVial(null, elem -> value.equals(elem));
          } else if (IMC.SOUL_VIAL_UNSPAWNABLELIST.equals(key)) {
            EntityDataRegistry.getInstance().setNeedsCloning(null, elem -> value.equals(elem));
          }
        } else if (msg.isNBTMessage()) {
          final NBTTagCompound nbtValue = msg.getNBTValue();
          if (nbtValue == null) {
            return;
          }
          if (IMC.FLUID_FUEL_ADD.equals(key)) {
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
        Log.error("Error occurred handling IMC message ", key, " from ", msg.getSender());
      }
    }
  }

  public static @Nonnull EnderIO getInstance() {
    return NullHelper.notnullF(instance, "instance is missing");
  }

  @Override
  public @Nonnull Configuration getConfiguration() {
    return BaseConfig.F.getConfig();
  }

  public static @Nonnull Config getConfigHandler() {
    return NullHelper.notnull(configHandler, "Cannot access config before preInit phase");
  }

  @EventHandler
  public void onServerAboutToStart(FMLServerAboutToStartEvent event) {
    if (DiagnosticsConfig.debugProfilerTracer.get()) {
      ProfilerDebugger.init(event);
    } else if (DiagnosticsConfig.debugProfilerAntiNuclearActivist.get()) {
      ProfilerAntiReactor.init(event);
    }
    if (PermissionAPI.getPermissionHandler() == DefaultPermissionHandler.INSTANCE) {
      Log.info("Permission Handler is: (default)");
    } else {
      Log.info("Permission Handler is: " + PermissionAPI.getPermissionHandler());
    }
  }

  @Override
  @Nonnull
  public NNList<Triple<Integer, RecipeFactory, String>> getRecipeFiles() {
    return new NNList<>(Triple.of(0, null, "aliases"), Triple.of(1, null, "materials"), Triple.of(1, null, "items"), Triple.of(1, null, "base"),
        Triple.of(1, null, "balls"), Triple.of(9, null, "misc"), Triple.of(9, null, "capacitor"), Triple.of(1, null, "hiding_base"),
        Triple.of(1, null, "darksteel_upgrades"), Triple.of(1, null, "fuels"), Triple.of(1, null, "glass"), Triple.of(1, null, "generated"));
  }

  @Override
  @Nonnull
  public NNList<String> getExampleFiles() {
    return new NNList<>("peaceful", "easy_recipes", "hard_recipes", "broken_spawner", "cheap_materials", "legacy_recipes", "strict_iron", "optional_tweaks",
        "unhide_base");
  }

  static void initCrashData() {
    // this is an ugly hack to make sure all anon subclasses of CrashReportCategory that are needed for a crash report are actually loaded
    CrashReport crashreport = CrashReport.makeCrashReport(new RuntimeException(), "Exception while updating neighbours");
    CrashReportCategory crashreportcategory = crashreport.makeCategory("Block being updated");
    crashreportcategory.addDetail("Source block type", new ICrashReportDetail<String>() {
      @Override
      public String call() throws Exception {
        return "foo";
      }
    });
    CrashReportCategory.addBlockInfo(crashreportcategory, new BlockPos(0, 0, 0), ModObject.block_machine_base.getBlockNN().getDefaultState());
    // the one failing usually is in net.minecraft.world.World.neighborChanged(BlockPos, Block, BlockPos). That one's $2.
    try {
      net.minecraft.world.World.class.getClassLoader().loadClass("net/minecraft/world/World$2");
    } catch (ClassNotFoundException e) {
      // This is unexpected but not our problem.
    }
  }

  private static void startupChecks() {
    FMLCommonHandler.instance().registerCrashCallable(new EnderIOCrashCallable());
    if (com.mojang.authlib.minecraft.MinecraftProfileTexture.Type.ELYTRA.getClass().toString().equals("force a classload real hard")) {
      // this will crash some pirated clients.
      // better now than some weird and seemingly random crashes later in the game.
    }
  }

}
