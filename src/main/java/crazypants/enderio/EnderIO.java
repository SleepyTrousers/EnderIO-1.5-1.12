package crazypants.enderio;

import static crazypants.enderio.EnderIO.MODID;
import static crazypants.enderio.EnderIO.MOD_NAME;
import static crazypants.enderio.EnderIO.VERSION;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.lang.reflect.Field;
import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;

import com.google.common.collect.ImmutableList;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLInterModComms;
import cpw.mods.fml.common.event.FMLInterModComms.IMCEvent;
import cpw.mods.fml.common.event.FMLInterModComms.IMCMessage;
import cpw.mods.fml.common.event.FMLLoadCompleteEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartedEvent;
import cpw.mods.fml.common.event.FMLServerStoppedEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.relauncher.Side;
import crazypants.enderio.api.IMC;
import crazypants.enderio.conduit.ConduitRecipes;
import crazypants.enderio.conduit.geom.ConduitGeometryUtil;
import crazypants.enderio.conduit.redstone.ConduitBundledRedstoneProvider;
import crazypants.enderio.conduit.redstone.InsulatedRedstoneConduit;
import crazypants.enderio.config.Config;
import crazypants.enderio.enchantment.Enchantments;
import crazypants.enderio.enderface.EnderfaceRecipes;
import crazypants.enderio.entity.SkeletonHandler;
import crazypants.enderio.fluid.FluidFuelRegister;
import crazypants.enderio.init.DarkSteelItems;
import crazypants.enderio.init.EIOBlocks;
import crazypants.enderio.init.EIOEntities;
import crazypants.enderio.init.EIOFluids;
import crazypants.enderio.init.EIOItems;
import crazypants.enderio.item.ItemRecipes;
import crazypants.enderio.machine.MachineRecipes;
import crazypants.enderio.machine.PacketRedstoneMode;
import crazypants.enderio.machine.alloy.AlloyRecipeManager;
import crazypants.enderio.machine.crusher.CrusherRecipeManager;
import crazypants.enderio.machine.enchanter.EnchanterRecipeManager;
import crazypants.enderio.machine.farm.FarmersRegistry;
import crazypants.enderio.machine.hypercube.HyperCubeRegister;
import crazypants.enderio.machine.painter.PaintSourceValidator;
import crazypants.enderio.machine.slicensplice.SliceAndSpliceRecipeManager;
import crazypants.enderio.machine.soul.SoulBinderRecipeManager;
import crazypants.enderio.machine.spawner.PoweredSpawnerConfig;
import crazypants.enderio.machine.transceiver.ServerChannelRegister;
import crazypants.enderio.machine.vat.VatRecipeManager;
import crazypants.enderio.material.MaterialRecipes;
import crazypants.enderio.material.OreDictionaryPreferences;
import crazypants.enderio.network.MessageTileNBT;
import crazypants.enderio.network.PacketHandler;
import crazypants.enderio.teleport.TeleportRecipes;
import crazypants.enderio.teleport.TravelController;
import crazypants.enderio.thaumcraft.ThaumcraftCompat;
import crazypants.util.EntityUtil;

@Mod(modid = MODID, name = MOD_NAME, version = VERSION, dependencies = "required-after:Forge@10.13.0.1150,);after:MineFactoryReloaded;after:Waila@[1.5.8,);after:Thaumcraft", guiFactory = "crazypants.enderio.config.ConfigFactoryEIO")
public class EnderIO {

  public static final String MODID = "EnderIO";
  public static final String MOD_NAME = "Ender IO";
  public static final String VERSION = "@VERSION@";

  @Instance(MODID)
  public static EnderIO instance;

  @SidedProxy(clientSide = "crazypants.enderio.ClientProxy", serverSide = "crazypants.enderio.CommonProxy")
  public static CommonProxy proxy;

  public static final PacketHandler packetPipeline = new PacketHandler();

  public static GuiHandler guiHandler = new GuiHandler();

  @EventHandler
  public void preInit(FMLPreInitializationEvent event) {

    Config.load(event);

    ConduitGeometryUtil.setupBounds((float) Config.conduitScale);

    EIOBlocks.registerBlocks();

    EIOItems.registerItems();

    EIOFluids.registerFluids();

    DarkSteelItems.registerItems();

    EIOEntities.registerEntities();

    FMLInterModComms.sendMessage("Waila", "register", "crazypants.enderio.waila.WailaCompat.load");

    MaterialRecipes.registerOresInDictionary();
  }

  @EventHandler
  public void load(FMLInitializationEvent event) {

    Config.init();

    PacketHandler.INSTANCE.registerMessage(MessageTileNBT.class, MessageTileNBT.class, PacketHandler.nextID(), Side.SERVER);
    PacketHandler.INSTANCE.registerMessage(PacketRedstoneMode.class, PacketRedstoneMode.class, PacketHandler.nextID(), Side.SERVER);

    NetworkRegistry.INSTANCE.registerGuiHandler(instance, guiHandler);
    MinecraftForge.EVENT_BUS.register(instance);

    EIOItems.registerDungeonLoot();

    DarkSteelItems.registerDungeonLoot();

    if(Loader.isModLoaded("ComputerCraft")) {
      ConduitBundledRedstoneProvider.register();
    }

    if(Config.replaceWitherSkeletons)
    {
      SkeletonHandler.registerSkeleton(this);
    }

    EnderfaceRecipes.addRecipes();
    MaterialRecipes.addRecipes();
    ConduitRecipes.addRecipes();
    MachineRecipes.addRecipes();
    ItemRecipes.addRecipes();
    TeleportRecipes.addRecipes();

    proxy.load();
  }

  @EventHandler
  public void postInit(FMLPostInitializationEvent event) {

    Config.postInit();

    // Register the enchants
    Enchantments.getInstance();

    MaterialRecipes.registerDependantOresInDictionary();

    //This must be loaded before parsing the recipes so we get the preferred outputs
    OreDictionaryPreferences.loadConfig();

    MaterialRecipes.addOreDictionaryRecipes();
    MachineRecipes.addOreDictionaryRecipes();
    ItemRecipes.addOreDictionaryRecipes();

    CrusherRecipeManager.getInstance().loadRecipesFromConfig();
    AlloyRecipeManager.getInstance().loadRecipesFromConfig();
    SliceAndSpliceRecipeManager.getInstance().loadRecipesFromConfig();
    VatRecipeManager.getInstance().loadRecipesFromConfig();
    EnchanterRecipeManager.getInstance().loadRecipesFromConfig();
    FarmersRegistry.addFarmers();
    SoulBinderRecipeManager.getInstance().addDefaultRecipes();
    PaintSourceValidator.instance.loadConfig();

    EIOFluids.postInitFluids();

    if(Config.dumpMobNames) {
      File dumpFile = new File(Config.configDirectory, "mobTypes.txt");
      List<String> names = EntityUtil.getAllRegisteredMobNames(false);

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

    addModIntegration();
  }

  @EventHandler
  public void loadComplete(FMLLoadCompleteEvent event) {
    processImc(FMLInterModComms.fetchRuntimeMessages(this)); //Some mods send IMCs during PostInit, so we catch them here.
  }

  @SuppressWarnings("unchecked")
  private void addModIntegration() {

    if(Loader.isModLoaded("TConstruct")) {
      try {
        Class<?> ttClass = Class.forName("tconstruct.tools.TinkerTools");
        Field modFluxF = ttClass.getField("modFlux");
        Object modFluxInst = modFluxF.get(null);

        Class<?> modFluxClass = Class.forName("tconstruct.modifiers.tools.ModFlux");
        Field batteriesField = modFluxClass.getField("batteries");
        List<ItemStack> batteries = (List<ItemStack>) batteriesField.get(modFluxInst);
        batteries.add(new ItemStack(EIOBlocks.blockCapBank));
        Log.info("Registered Capacitor Banks as Tinkers Construct Flux Upgrades");
      } catch (Exception e) {
        //Doesn't matter if it didnt work
        Log.info("Failed to registered Capacitor Banks as Tinkers Construct Flux Upgrades");
      }
    }
    
    ThaumcraftCompat.load();
  }

  @EventHandler
  public void onImc(IMCEvent evt) {
    processImc(evt.getMessages());
  }

  private void processImc(ImmutableList<IMCMessage> messages) {
    for (IMCMessage msg : messages) {
      String key = msg.key;
      try {
        if(msg.isStringMessage()) {
          String value = msg.getStringValue();
          if(IMC.VAT_RECIPE.equals(key)) {
            VatRecipeManager.getInstance().addCustomRecipes(value);
          } else if(IMC.SAG_RECIPE.equals(key)) {
            CrusherRecipeManager.getInstance().addCustomRecipes(value);
          } else if(IMC.ALLOY_RECIPE.equals(key)) {
            AlloyRecipeManager.getInstance().addCustomRecipes(value);
          } else if(IMC.POWERED_SPAWNER_BLACKLIST_ADD.equals(key)) {
            PoweredSpawnerConfig.getInstance().addToBlacklist(value);
          } else if(IMC.TELEPORT_BLACKLIST_ADD.equals(key)) {
            TravelController.instance.addBlockToBlinkBlackList(value);
          } else if (IMC.SOUL_VIAL_BLACKLIST.equals(key) && EIOItems.itemSoulVessel != null) {
            EIOItems.itemSoulVessel.addEntityToBlackList(value);
          } else if(IMC.ENCHANTER_RECIPE.equals(key)) {
            EnchanterRecipeManager.getInstance().addCustomRecipes(value);
          } else if(IMC.SLINE_N_SPLICE_RECIPE.equals(key)) {
            SliceAndSpliceRecipeManager.getInstance().addCustomRecipes(key);
          }
        } else if(msg.isNBTMessage()) {
          if(IMC.SOUL_BINDER_RECIPE.equals(key)) {
            SoulBinderRecipeManager.getInstance().addRecipeFromNBT(msg.getNBTValue());
          } else if(IMC.POWERED_SPAWNER_COST_MULTIPLIER.equals(key)) {
            PoweredSpawnerConfig.getInstance().addEntityCostFromNBT(msg.getNBTValue());
          } else if(IMC.FLUID_FUEL_ADD.equals(key)) {
            FluidFuelRegister.instance.addFuel(msg.getNBTValue());
          } else if(IMC.FLUID_COOLANT_ADD.equals(key)) {
            FluidFuelRegister.instance.addCoolant(msg.getNBTValue());
          } else if(IMC.REDSTONE_CONNECTABLE_ADD.equals(key)) {
            InsulatedRedstoneConduit.addConnectableBlock(msg.getNBTValue());
          }
        } else if(msg.isItemStackMessage()) {
          if(IMC.PAINTER_WHITELIST_ADD.equals(key)) {
            PaintSourceValidator.instance.addToWhitelist(msg.getItemStackValue());
          } else if(IMC.PAINTER_BLACKLIST_ADD.equals(key)) {
            PaintSourceValidator.instance.addToBlacklist(msg.getItemStackValue());
          }
        }
      } catch (Exception e) {
        Log.error("Error occured handling IMC message " + key + " from " + msg.getSender());
      }
    }
  }

  @EventHandler
  public void serverStarted(FMLServerStartedEvent event) {
    HyperCubeRegister.load();
    ServerChannelRegister.load();
  }

  @EventHandler
  public void serverStopped(FMLServerStoppedEvent event) {
    HyperCubeRegister.unload();
    ServerChannelRegister.store();
  }
}
