package crazypants.enderio;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.List;
import java.util.Locale;

import javax.annotation.Nonnull;

import com.enderio.core.common.Lang;
import com.enderio.core.common.util.EntityUtil;
import com.google.common.collect.ImmutableList;

import crazypants.enderio.api.IMC;
import crazypants.enderio.conduit.geom.ConduitGeometryUtil;
import crazypants.enderio.conduit.redstone.InsulatedRedstoneConduit;
import crazypants.enderio.config.Config;
import crazypants.enderio.enchantment.Enchantments;
import crazypants.enderio.fluid.FluidFuelRegister;
import crazypants.enderio.fluid.Fluids;
import crazypants.enderio.integration.buildcraft.BuildcraftIntegration;
import crazypants.enderio.item.darksteel.DarkSteelController;
import crazypants.enderio.item.darksteel.DarkSteelItems;
import crazypants.enderio.machine.alloy.AlloyRecipeManager;
import crazypants.enderio.machine.enchanter.EnchanterRecipeManager;
import crazypants.enderio.machine.farm.FarmersRegistry;
import crazypants.enderio.machine.sagmill.SagMillRecipeManager;
import crazypants.enderio.machine.slicensplice.SliceAndSpliceRecipeManager;
import crazypants.enderio.machine.soul.SoulBinderRecipeManager;
import crazypants.enderio.machine.spawner.PoweredSpawnerConfig;
import crazypants.enderio.machine.transceiver.ServerChannelRegister;
import crazypants.enderio.machine.vat.VatRecipeManager;
import crazypants.enderio.material.MaterialRecipes;
import crazypants.enderio.material.OreDictionaryPreferences;
import crazypants.enderio.network.PacketHandler;
import crazypants.enderio.paint.PaintSourceValidator;
import crazypants.enderio.power.CapInjectHandler;
import crazypants.enderio.power.PowerHandlerUtil;
import crazypants.enderio.render.dummy.BlockMachineBase;
import crazypants.enderio.render.dummy.BlockMachineIO;
import crazypants.enderio.tool.EnderIOCrashCallable;
import crazypants.util.CapturedMob;
import crazypants.util.Things;
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
import net.minecraftforge.fml.common.event.FMLServerStartedEvent;
import net.minecraftforge.fml.common.event.FMLServerStoppedEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;

import static crazypants.enderio.EnderIO.MODID;
import static crazypants.enderio.EnderIO.MOD_NAME;
import static crazypants.enderio.EnderIO.VERSION;
import static crazypants.enderio.ModObject.itemSoulVessel;
import static crazypants.util.Things.TRAVEL_BLACKLIST;

@Mod(modid = MODID, name = MOD_NAME, version = VERSION, dependencies = "after:endercore;after:Waila;after:JEI@[3.11.1,)", guiFactory = "crazypants.enderio.config.ConfigFactoryEIO")
public class EnderIO {

  public static final @Nonnull String MODID = "EnderIO";
  public static final @Nonnull String DOMAIN = MODID.toLowerCase(Locale.US);
  public static final String MOD_NAME = "Ender IO";
  public static final String VERSION = "@VERSION@";
  
  //Enable this to use forge buckets
//  static {
//    FluidRegistry.enableUniversalBucket();
//  }

  @Instance(MODID)
  public static EnderIO instance;

  @SidedProxy(clientSide = "crazypants.enderio.ClientProxy", serverSide = "crazypants.enderio.CommonProxy")
  public static CommonProxy proxy;

  public static final PacketHandler packetPipeline = new PacketHandler();

  public static GuiHandler guiHandler = new GuiHandler();

  public static final Lang lang = new Lang("enderio");

  // Blocks
  public static Fluids fluids;

  // prePreInit
  static {
    CapInjectHandler.loadClass();
  }

  @EventHandler
  public void preInit(FMLPreInitializationEvent event) {

    EnderIOCrashCallable.create();

    PowerHandlerUtil.create();

    Config.preInit(event);

    proxy.loadIcons();

    // Dummy blocks that contain the models for all the other blocks
    BlockMachineBase.create();
    BlockMachineIO.create();
    // TODO: find some block that can take those models as extra

    ConduitGeometryUtil.setupBounds((float) Config.conduitScale);

    fluids = new Fluids();
    fluids.registerFluids();

    ModObject.preInit(event);

    DarkSteelItems.createDarkSteelArmorItems();
    DarkSteelController.instance.register();

    FMLInterModComms.sendMessage("Waila", "register", "crazypants.enderio.integration.waila.WailaCompat.load");

    MaterialRecipes.registerOresInDictionary();

    proxy.preInit(event);
  }

  @EventHandler
  public void load(FMLInitializationEvent event) {
    Things.init(event);

    Config.init(event);

    instance = this;

    PacketHandler.init(event);

    NetworkRegistry.INSTANCE.registerGuiHandler(this, guiHandler);

    MaterialRecipes.registerDependantOresInDictionary();

    proxy.init();
  }

  @EventHandler
  public void postInit(FMLPostInitializationEvent event) {
    
    Config.postInit();

    LootManager.register();
    
    // Regsiter the enchants
    Enchantments.register();

    // This must be loaded before parsing the recipes so we get the preferred
    // outputs
    OreDictionaryPreferences.loadConfig();

    SagMillRecipeManager.getInstance().loadRecipesFromConfig();
    AlloyRecipeManager.getInstance().loadRecipesFromConfig();
    SliceAndSpliceRecipeManager.getInstance().loadRecipesFromConfig();
    VatRecipeManager.getInstance().loadRecipesFromConfig();
    EnchanterRecipeManager.getInstance().loadRecipesFromConfig();
    FarmersRegistry.addFarmers();
    SoulBinderRecipeManager.getInstance().addDefaultRecipes();
    PaintSourceValidator.instance.loadConfig();

    //should have been registered by open  blocks
    if (Fluids.fluidXpJuice == null) {
      fluids.forgeRegisterXPJuice();
    }
    if (Config.dumpMobNames) {
      dumpMobNamesToFile();
    }
    addModIntegration();
  }

  @EventHandler
  public void loadComplete(FMLLoadCompleteEvent event) {
    // Some mods send IMCs during PostInit, so we catch them here.
    processImc(FMLInterModComms.fetchRuntimeMessages(this));
  }
  
  @EventHandler
  public void serverStarted(FMLServerStartedEvent event) {
    ServerChannelRegister.load();
  }

  @EventHandler
  public void serverStopped(FMLServerStoppedEvent event) {
    ServerChannelRegister.store();
  }

  @EventHandler
  public void onImc(IMCEvent evt) {
    processImc(evt.getMessages());
  }
  
  private void addModIntegration() {
    // ThaumcraftCompat.load();
    BuildcraftIntegration.init();
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
          } else if (IMC.POWERED_SPAWNER_BLACKLIST_ADD.equals(key)) {
            PoweredSpawnerConfig.getInstance().addToBlacklist(value);
          } else if (IMC.TELEPORT_BLACKLIST_ADD.equals(key)) {
            TRAVEL_BLACKLIST.add(value);
          } else if (IMC.SOUL_VIAL_BLACKLIST.equals(key) && itemSoulVessel.getItem() != null) {
            CapturedMob.addToBlackList(value);
          } else if (IMC.ENCHANTER_RECIPE.equals(key)) {
            EnchanterRecipeManager.getInstance().addCustomRecipes(value);
          } else if (IMC.SLINE_N_SPLICE_RECIPE.equals(key)) {
            SliceAndSpliceRecipeManager.getInstance().addCustomRecipes(key);
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
        Log.error("Error occured handling IMC message " + key + " from " + msg.getSender());
      }
    }
  }
  
  private void dumpMobNamesToFile() {
    File dumpFile = new File(Config.configDirectory, "mobTypes.txt");
    List<String> names = EntityUtil.getAllRegisteredMobNames();

    try {
      BufferedWriter br = new BufferedWriter(new FileWriter(dumpFile, false));
      for (String name : names) {
        br.append(name);
        br.newLine();
      }
      br.flush();
      br.close();
    } catch (Exception e) {
      Log.error("Could not write mob types file: " + e);
    }
  }

  static {
    // uncomment to always use the universal bucket. leave it out to only use it if another mod requests it.
    // net.minecraftforge.fluids.FluidRegistry.enableUniversalBucket();
  }

}
