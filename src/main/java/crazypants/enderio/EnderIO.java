package crazypants.enderio;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.List;

import javax.annotation.Nonnull;

import com.enderio.core.common.Lang;
import com.enderio.core.common.util.EntityUtil;
import com.enderio.core.common.util.NullHelper;
import com.google.common.collect.ImmutableList;

import crazypants.enderio.api.IMC;
import crazypants.enderio.conduit.geom.ConduitGeometryUtil;
import crazypants.enderio.config.Config;
import crazypants.enderio.diagnostics.EnderIOCrashCallable;
import crazypants.enderio.enchantment.Enchantments;
import crazypants.enderio.farming.FarmersRegistry;
import crazypants.enderio.fluid.FluidFuelRegister;
import crazypants.enderio.fluid.Fluids;
import crazypants.enderio.handler.darksteel.DarkSteelController;
import crazypants.enderio.init.ModObjectRegistry;
import crazypants.enderio.integration.bigreactors.BRProxy;
import crazypants.enderio.integration.buildcraft.BuildcraftIntegration;
import crazypants.enderio.integration.chiselsandbits.CABIMC;
import crazypants.enderio.integration.te.TEUtil;
import crazypants.enderio.integration.tic.TicProxy;
import crazypants.enderio.item.darksteel.upgrade.energy.EnergyUpgradePowerAdapter;
import crazypants.enderio.item.spawner.BrokenSpawnerHandler;
import crazypants.enderio.item.spawner.PoweredSpawnerConfig;
import crazypants.enderio.loot.Loot;
import crazypants.enderio.loot.LootManager;
import crazypants.enderio.material.MaterialRecipes;
import crazypants.enderio.material.OreDictionaryPreferences;
import crazypants.enderio.material.material.MaterialCraftingHandler;
import crazypants.enderio.network.PacketHandler;
import crazypants.enderio.paint.PaintSourceValidator;
import crazypants.enderio.power.CapInjectHandler;
import crazypants.enderio.recipe.alloysmelter.AlloyRecipeManager;
import crazypants.enderio.recipe.sagmill.SagMillRecipeManager;
import crazypants.enderio.recipe.slicensplice.SliceAndSpliceRecipeManager;
import crazypants.enderio.recipe.soul.SoulBinderRecipeManager;
import crazypants.enderio.recipe.vat.VatRecipeManager;
import crazypants.enderio.transceiver.ServerChannelRegister;
import crazypants.util.CapturedMob;
import info.loenwind.scheduler.Celeb;
import info.loenwind.scheduler.Scheduler;
import net.minecraft.util.ResourceLocation;
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
import net.minecraftforge.fml.common.event.FMLMissingMappingsEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartedEvent;
import net.minecraftforge.fml.common.event.FMLServerStoppedEvent;

@Mod(modid = crazypants.enderio.EnderIO.MODID, name = crazypants.enderio.EnderIO.MOD_NAME, version = crazypants.enderio.EnderIO.VERSION, //
    dependencies = "after:endercore;after:hwyla;after:jei", guiFactory = "crazypants.enderio.config.ConfigFactoryEIO")
public class EnderIO {

  public static final @Nonnull String MODID = "enderiobase";
  public static final @Nonnull String DOMAIN = "enderio";
  public static final @Nonnull String MOD_NAME = "Ender IO Base";
  public static final @Nonnull String VERSION = "@VERSION@";

  @Instance(MODID)
  public static EnderIO instance;

  @SidedProxy(clientSide = "crazypants.enderio.ClientProxy", serverSide = "crazypants.enderio.CommonProxy")
  public static CommonProxy proxy;

  public static final @Nonnull PacketHandler packetPipeline = new PacketHandler();

  public static final @Nonnull Lang lang = new Lang("enderio");

  // Blocks
  public static Fluids fluids;

  // prePreInit
  static {
    FluidRegistry.enableUniversalBucket();
    CapInjectHandler.loadClass();
  }

  @EventHandler
  public void preInit(@Nonnull FMLPreInitializationEvent event) {

    EnderIOCrashCallable.init(event);

    Config.init(event);

    proxy.loadIcons();

    ConduitGeometryUtil.setupBounds((float) Config.conduitScale);

    FluidFuelRegister.init(event);
    fluids = new Fluids();
    fluids.registerFluids();

    TicProxy.init(event);

    ModObjectRegistry.init(event);

    EnergyUpgradePowerAdapter.init(event);

    DarkSteelController.init(event);

    MaterialRecipes.init(event);

    Loot.init(event);

    BrokenSpawnerHandler.init(event);

    PoweredSpawnerConfig.init(event);

    BRProxy.init(event);

    ServerChannelRegister.init(event);

    proxy.init(event);
  }

  @EventHandler
  public void load(@Nonnull FMLInitializationEvent event) {
    Config.init(event);

    ModObjectRegistry.init(event);

    CABIMC.init(event);

    PacketHandler.init(event);

    GuiID.init(event);

    MaterialRecipes.init(event);

    MaterialCraftingHandler.init(event);

    proxy.init(event);
  }

  @EventHandler
  public void postInit(@Nonnull FMLPostInitializationEvent event) {

    Config.init(event);

    LootManager.init(event);

    // Register the enchants
    Enchantments.init(event);

    // This must be loaded before parsing the recipes so we get the preferred
    // outputs
    OreDictionaryPreferences.init(event);

    SagMillRecipeManager.getInstance().loadRecipesFromConfig();
    AlloyRecipeManager.getInstance().loadRecipesFromConfig();
    SliceAndSpliceRecipeManager.getInstance().loadRecipesFromConfig();
    VatRecipeManager.getInstance().loadRecipesFromConfig();
    EnchanterRecipeManager.getInstance().loadRecipesFromConfig();
    FarmersRegistry.init(event);
    SoulBinderRecipeManager.getInstance().addDefaultRecipes();
    PaintSourceValidator.instance.loadConfig();

    if (Config.dumpMobNames) {
      dumpMobNamesToFile();
    }

    // ThaumcraftCompat.load();
    BuildcraftIntegration.init(event);
    TEUtil.init(event);
    TicProxy.init(event);

    proxy.init(event);

    Celeb.init(event);
    Scheduler.instance.start();
  }

  @EventHandler
  public void loadComplete(@Nonnull FMLLoadCompleteEvent event) {
    // Some mods send IMCs during PostInit, so we catch them here.
    processImc(FMLInterModComms.fetchRuntimeMessages(this));
  }

  @EventHandler
  public void serverStarted(@Nonnull FMLServerStartedEvent event) {
    ServerChannelRegister.load();
  }

  @EventHandler
  public void serverStopped(@Nonnull FMLServerStoppedEvent event) {
    ServerChannelRegister.store();
    ConduitNetworkTickHandler.instance.flush();
  }

  @EventHandler
  public void onImc(@Nonnull IMCEvent evt) {
    processImc(evt.getMessages());
  }

  private void processImc(ImmutableList<IMCMessage> messages) {
    for (IMCMessage msg : messages) {
      String key = msg.key;
      try {
        if (msg.isStringMessage()) {
          String value = msg.getStringValue();
          if (IMC.VAT_RECIPE.equals(key)) {
            VatRecipeManager.getInstance().addCustomRecipes(value);
          } else if (IMC.SAG_RECIPE.equals(key)) {
            SagMillRecipeManager.getInstance().addCustomRecipes(value);
          } else if (IMC.ALLOY_RECIPE.equals(key)) {
            AlloyRecipeManager.getInstance().addCustomRecipes(value);
          } else if (IMC.TELEPORT_BLACKLIST_ADD.equals(key)) {
            Config.TRAVEL_BLACKLIST.add(value);
          } else if (IMC.ENCHANTER_RECIPE.equals(key)) {
            EnchanterRecipeManager.getInstance().addCustomRecipes(value);
          } else if (IMC.SLINE_N_SPLICE_RECIPE.equals(key)) {
            SliceAndSpliceRecipeManager.getInstance().addCustomRecipes(key);
          }
        } else if (msg.isResourceLocationMessage()) {
          ResourceLocation value = msg.getResourceLocationValue();
          if (IMC.SOUL_VIAL_BLACKLIST.equals(key)) {
            CapturedMob.addToBlackList(value);
          } else if (IMC.SOUL_VIAL_UNSPAWNABLELIST.equals(key)) {
            CapturedMob.addToUnspawnableList(value);
          } else if (IMC.POWERED_SPAWNER_BLACKLIST_ADD.equals(key)) {
            PoweredSpawnerConfig.getInstance().addToBlacklist(value);
          }
        } else if (msg.isNBTMessage()) {
          if (IMC.SOUL_BINDER_RECIPE.equals(key)) {
            SoulBinderRecipeManager.getInstance().addRecipeFromNBT(msg.getNBTValue());
          } else if (IMC.POWERED_SPAWNER_COST_MULTIPLIER.equals(key)) {
            PoweredSpawnerConfig.getInstance().addEntityCostFromNBT(msg.getNBTValue());
          } else if (IMC.FLUID_FUEL_ADD.equals(key)) {
            FluidFuelRegister.instance.addFuel(msg.getNBTValue());
          } else if (IMC.FLUID_COOLANT_ADD.equals(key)) {
            FluidFuelRegister.instance.addCoolant(msg.getNBTValue());
          } else if (IMC.REDSTONE_CONNECTABLE_ADD.equals(key)) {
            InsulatedRedstoneConduit.addConnectableBlock(msg.getNBTValue());
          }
        } else if (msg.isItemStackMessage()) {
          if (IMC.PAINTER_WHITELIST_ADD.equals(key)) {
            PaintSourceValidator.instance.addToWhitelist(msg.getItemStackValue());
          } else if (IMC.PAINTER_BLACKLIST_ADD.equals(key)) {
            PaintSourceValidator.instance.addToBlacklist(msg.getItemStackValue());
          }
        }
      } catch (Exception e) {
        Log.error("Error occurred handling IMC message " + key + " from " + msg.getSender());
      }
    }
  }

  private void dumpMobNamesToFile() {
    File dumpFile = new File(Config.configDirectory, "mobTypes.txt");
    List<ResourceLocation> names = EntityUtil.getAllRegisteredMobNames();

    try {
      BufferedWriter br = new BufferedWriter(new FileWriter(dumpFile, false));
      for (ResourceLocation name : names) {
        br.append(name.toString());
        br.newLine();
      }
      br.flush();
      br.close();
    } catch (Exception e) {
      Log.error("Could not write mob types file: " + e);
    }
  }

  @EventHandler
  public static void handleMappings(FMLMissingMappingsEvent event) {
    MigrationMapper.handleMappings(event);
  }

  public static @Nonnull EnderIO getInstance() {
    return NullHelper.notnullF(instance, "instance is missing");
  }

}
