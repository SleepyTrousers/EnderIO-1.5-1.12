package crazypants.enderio;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.List;
import java.util.Locale;

import net.minecraft.block.Block;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.WeightedRandomChestContent;
import net.minecraftforge.common.ChestGenHooks;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Loader;
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
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.relauncher.Side;

import com.enderio.core.common.Lang;
import com.enderio.core.common.network.MessageTileNBT;
import com.enderio.core.common.util.EntityUtil;
import com.google.common.collect.ImmutableList;

import crazypants.enderio.api.IMC;
import crazypants.enderio.block.BlockDarkSteelAnvil;
import crazypants.enderio.block.BlockDarkSteelLadder;
import crazypants.enderio.block.BlockReinforcedObsidian;
import crazypants.enderio.conduit.BlockConduitBundle;
import crazypants.enderio.conduit.ConduitRecipes;
import crazypants.enderio.conduit.facade.BlockConduitFacade;
import crazypants.enderio.conduit.geom.ConduitGeometryUtil;
import crazypants.enderio.conduit.item.ItemExtractSpeedUpgrade;
import crazypants.enderio.conduit.item.ItemFunctionUpgrade;
import crazypants.enderio.conduit.item.ItemItemConduit;
import crazypants.enderio.conduit.item.filter.ItemBasicItemFilter;
import crazypants.enderio.conduit.item.filter.ItemExistingItemFilter;
import crazypants.enderio.conduit.item.filter.ItemModItemFilter;
import crazypants.enderio.conduit.item.filter.ItemPowerItemFilter;
import crazypants.enderio.conduit.liquid.ItemLiquidConduit;
import crazypants.enderio.conduit.power.ItemPowerConduit;
import crazypants.enderio.conduit.redstone.ConduitBundledRedstoneProvider;
import crazypants.enderio.conduit.redstone.InsulatedRedstoneConduit;
import crazypants.enderio.conduit.redstone.ItemRedstoneConduit;
import crazypants.enderio.config.Config;
import crazypants.enderio.enchantment.Enchantments;
import crazypants.enderio.enderface.BlockEnderIO;
import crazypants.enderio.enderface.EnderfaceRecipes;
import crazypants.enderio.enderface.ItemEnderface;
import crazypants.enderio.fluid.FluidFuelRegister;
import crazypants.enderio.fluid.Fluids;
import crazypants.enderio.item.ItemConduitProbe;
import crazypants.enderio.item.ItemEnderFood;
import crazypants.enderio.item.ItemRecipes;
import crazypants.enderio.item.ItemSoulVessel;
import crazypants.enderio.item.ItemYetaWrench;
import crazypants.enderio.item.darksteel.DarkSteelItems;
import crazypants.enderio.item.skull.BlockEndermanSkull;
import crazypants.enderio.machine.MachineRecipes;
import crazypants.enderio.machine.PacketRedstoneMode;
import crazypants.enderio.machine.alloy.AlloyRecipeManager;
import crazypants.enderio.machine.alloy.BlockAlloySmelter;
import crazypants.enderio.machine.buffer.BlockBuffer;
import crazypants.enderio.machine.capbank.BlockCapBank;
import crazypants.enderio.machine.crafter.BlockCrafter;
import crazypants.enderio.machine.enchanter.BlockEnchanter;
import crazypants.enderio.machine.enchanter.EnchanterRecipeManager;
import crazypants.enderio.machine.farm.BlockFarmStation;
import crazypants.enderio.machine.farm.FarmersRegistry;
import crazypants.enderio.machine.gauge.BlockGauge;
import crazypants.enderio.machine.generator.combustion.BlockCombustionGenerator;
import crazypants.enderio.machine.generator.stirling.BlockStirlingGenerator;
import crazypants.enderio.machine.generator.zombie.BlockZombieGenerator;
import crazypants.enderio.machine.invpanel.BlockInventoryPanel;
import crazypants.enderio.machine.killera.BlockKillerJoe;
import crazypants.enderio.machine.light.BlockElectricLight;
import crazypants.enderio.machine.light.BlockLightNode;
import crazypants.enderio.machine.monitor.BlockPowerMonitor;
import crazypants.enderio.machine.obelisk.attractor.BlockAttractor;
import crazypants.enderio.machine.obelisk.aversion.BlockAversionObelisk;
import crazypants.enderio.machine.obelisk.inhibitor.BlockInhibitorObelisk;
import crazypants.enderio.machine.obelisk.weather.BlockWeatherObelisk;
import crazypants.enderio.machine.obelisk.xp.BlockExperienceObelisk;
import crazypants.enderio.machine.obelisk.xp.ItemXpTransfer;
import crazypants.enderio.machine.painter.BlockPainter;
import crazypants.enderio.machine.painter.blocks.BlockPaintedCarpet;
import crazypants.enderio.machine.painter.blocks.BlockPaintedFence;
import crazypants.enderio.machine.painter.blocks.BlockPaintedFenceGate;
import crazypants.enderio.machine.painter.blocks.BlockPaintedGlowstone;
import crazypants.enderio.machine.painter.blocks.BlockPaintedPressurePlate;
import crazypants.enderio.machine.painter.blocks.BlockPaintedSlab;
import crazypants.enderio.machine.painter.blocks.BlockPaintedStairs;
import crazypants.enderio.machine.painter.blocks.BlockPaintedWall;
import crazypants.enderio.machine.ranged.RangeEntity;
import crazypants.enderio.machine.reservoir.BlockReservoir;
import crazypants.enderio.machine.sagmill.BlockSagMill;
import crazypants.enderio.machine.sagmill.SagMillRecipeManager;
import crazypants.enderio.machine.slicensplice.BlockSliceAndSplice;
import crazypants.enderio.machine.slicensplice.SliceAndSpliceRecipeManager;
import crazypants.enderio.machine.solar.BlockSolarPanel;
import crazypants.enderio.machine.soul.BlockSoulBinder;
import crazypants.enderio.machine.soul.SoulBinderRecipeManager;
import crazypants.enderio.machine.spawner.BlockPoweredSpawner;
import crazypants.enderio.machine.spawner.ItemBrokenSpawner;
import crazypants.enderio.machine.spawner.PoweredSpawnerConfig;
import crazypants.enderio.machine.tank.BlockTank;
import crazypants.enderio.machine.transceiver.BlockTransceiver;
import crazypants.enderio.machine.transceiver.ServerChannelRegister;
import crazypants.enderio.machine.vacuum.BlockVacuumChest;
import crazypants.enderio.machine.vat.BlockVat;
import crazypants.enderio.machine.vat.VatRecipeManager;
import crazypants.enderio.machine.wireless.BlockWirelessCharger;
import crazypants.enderio.material.Alloy;
import crazypants.enderio.material.BlockDarkIronBars;
import crazypants.enderio.material.BlockIngotStorage;
import crazypants.enderio.material.ItemAlloy;
import crazypants.enderio.material.ItemCapacitor;
import crazypants.enderio.material.ItemFrankenSkull;
import crazypants.enderio.material.ItemMachinePart;
import crazypants.enderio.material.ItemMaterial;
import crazypants.enderio.material.ItemPowderIngot;
import crazypants.enderio.material.MaterialRecipes;
import crazypants.enderio.material.OreDictionaryPreferences;
import crazypants.enderio.material.fusedQuartz.BlockFusedQuartz;
import crazypants.enderio.material.fusedQuartz.BlockPaintedFusedQuartz;
import crazypants.enderio.network.PacketHandler;
import crazypants.enderio.paint.PaintSourceValidator;
import crazypants.enderio.rail.BlockEnderRail;
import crazypants.enderio.render.dummy.BlockMachineBase;
import crazypants.enderio.render.dummy.BlockMachineIO;
import crazypants.enderio.teleport.ItemTravelStaff;
import crazypants.enderio.teleport.TeleportRecipes;
import crazypants.enderio.teleport.TravelController;
import crazypants.enderio.teleport.anchor.BlockTravelAnchor;
import crazypants.enderio.teleport.anchor.TileTravelAnchor;
import crazypants.enderio.teleport.telepad.BlockTelePad;
import crazypants.enderio.teleport.telepad.ItemCoordSelector;
import crazypants.enderio.thaumcraft.ThaumcraftCompat;
import crazypants.enderio.tool.EnderIOCrashCallable;
import crazypants.util.CapturedMob;

import static crazypants.enderio.EnderIO.MODID;
import static crazypants.enderio.EnderIO.MOD_NAME;
import static crazypants.enderio.EnderIO.VERSION;

// ATTN: The dependencies string MUST be kept in sync with the "toReplace" in gradle.properties!
@Mod(modid = MODID, name = MOD_NAME, version = VERSION, dependencies = "after:endercore;after:MineFactoryReloaded;after:Waila;after:Thaumcraft;after:appliedenergistics2", guiFactory = "crazypants.enderio.config.ConfigFactoryEIO")
public class EnderIO {

  public static final String MODID = "EnderIO";
  public static final String DOMAIN = MODID.toLowerCase(Locale.US);
  public static final String MOD_NAME = "Ender IO";
  public static final String VERSION = "@VERSION@";

  @Instance(MODID)
  public static EnderIO instance;

  @SidedProxy(clientSide = "crazypants.enderio.ClientProxy", serverSide = "crazypants.enderio.CommonProxy")
  public static CommonProxy proxy;

  public static final PacketHandler packetPipeline = new PacketHandler();

  public static GuiHandler guiHandler = new GuiHandler();

  public static final Lang lang = new Lang("enderio");

  // Materials
  public static ItemCapacitor itemBasicCapacitor;
  public static ItemAlloy itemAlloy;
  public static BlockFusedQuartz blockFusedQuartz;
  public static ItemMachinePart itemMachinePart;
  public static ItemPowderIngot itemPowderIngot;
  public static ItemMaterial itemMaterial;
  public static BlockIngotStorage blockIngotStorage;
  public static BlockDarkIronBars blockDarkIronBars;
  public static ItemEnderFood itemEnderFood;

//  // Enderface
  public static BlockEnderIO blockEnderIo;
  public static ItemEnderface itemEnderface;

//  //Teleporting
  public static BlockTravelAnchor<TileTravelAnchor> blockTravelPlatform;
  public static BlockTelePad blockTelePad;
  public static ItemCoordSelector itemCoordSelector;
  public static ItemTravelStaff itemTravelStaff;
//
//  // Painter
  public static BlockPainter blockPainter;
  public static BlockPaintedFence blockPaintedFence;
  public static BlockPaintedFenceGate blockPaintedFenceGate;
  public static BlockPaintedWall blockPaintedWall;
  public static BlockPaintedStairs blockPaintedStair;
  public static BlockPaintedSlab blockPaintedSlab;
  public static BlockPaintedGlowstone blockPaintedGlowstone;
  public static BlockPaintedCarpet blockPaintedCarpet;
  public static BlockPaintedPressurePlate blockPaintedPressurePlate;
//
//  // Conduits
  public static BlockConduitBundle blockConduitBundle;
  public static BlockConduitFacade blockConduitFacade;
  public static ItemRedstoneConduit itemRedstoneConduit;
  public static ItemPowerConduit itemPowerConduit;
  public static ItemLiquidConduit itemLiquidConduit;
  public static ItemItemConduit itemItemConduit;

  public static ItemBasicItemFilter itemBasicFilterUpgrade;
  public static ItemExistingItemFilter itemExistingItemFilter;
  public static ItemModItemFilter itemModItemFilter;
  public static ItemPowerItemFilter itemPowerItemFilter;
  public static ItemExtractSpeedUpgrade itemExtractSpeedUpgrade;
  public static ItemFunctionUpgrade itemFunctionUpgrade;

  // Machines
  public static BlockStirlingGenerator blockStirlingGenerator;
  public static BlockCombustionGenerator blockCombustionGenerator;
  public static BlockZombieGenerator blockZombieGenerator;
  public static BlockSolarPanel blockSolarPanel;
  public static BlockReservoir blockReservoir;
  public static BlockAlloySmelter blockAlloySmelter;

  public static BlockCapBank blockCapBank;
  public static BlockWirelessCharger blockWirelessCharger;
  public static BlockSagMill blockCrusher; 
  public static Block blockPowerMonitor;
  public static Block blockPowerMonitorAdvanced;
  public static BlockVat blockVat;
  public static BlockFarmStation blockFarmStation;
  public static BlockTank blockTank;
  public static BlockCrafter blockCrafter;
  public static BlockPoweredSpawner blockPoweredSpawner;
  public static ItemBrokenSpawner itemBrokenSpawner;
  public static BlockSliceAndSplice blockSliceAndSplice;
  public static BlockSoulBinder blockSoulFuser;
  public static BlockAttractor blockAttractor;
  public static BlockAversionObelisk blockSpawnGuard;
  public static BlockExperienceObelisk blockExperianceOblisk;
  public static BlockWeatherObelisk blockWeatherObelisk;
  public static BlockInhibitorObelisk blockInhibitorObelisk;
  public static BlockTransceiver blockTransceiver;
  public static BlockBuffer blockBuffer;
  public static BlockInventoryPanel blockInventoryPanel;
  public static BlockKillerJoe blockKillerJoe;
  public static BlockEnchanter blockEnchanter;
  public static BlockGauge blockGauge;

  public static BlockElectricLight blockElectricLight;
  public static BlockLightNode blockLightNode;

  //Blocks
  public static BlockDarkSteelAnvil blockDarkSteelAnvil;
  public static BlockDarkSteelLadder blockDarkSteelLadder;
  public static BlockEndermanSkull blockEndermanSkull;
  public static BlockReinforcedObsidian blockReinforcedObsidian;
  public static BlockEnderRail blockEnderRail;

  public static Fluids fluids;  

  // Items
  public static ItemYetaWrench itemYetaWench;
  public static ItemConduitProbe itemConduitProbe;
  public static ItemXpTransfer itemXpTransfer;

  public static ItemSoulVessel itemSoulVessel;
  public static ItemFrankenSkull itemFrankenSkull;

  public static BlockVacuumChest blockVacuumChest;

  @EventHandler
  public void preInit(FMLPreInitializationEvent event) {
  
    EnderIOCrashCallable.create();

    Config.load(event);
    
    proxy.loadIcons();

    // Dummy blocks that contain the models for all the other blocks
    BlockMachineBase.create();
    BlockMachineIO.create();
    // TODO: find some block that can take those models as extra

    ConduitGeometryUtil.setupBounds((float) Config.conduitScale);

    blockStirlingGenerator = BlockStirlingGenerator.create();
        
    blockCombustionGenerator = BlockCombustionGenerator.create();
    blockZombieGenerator = BlockZombieGenerator.create();
    blockSolarPanel = BlockSolarPanel.create();

    blockCrusher = BlockSagMill.create();
    blockAlloySmelter = BlockAlloySmelter.create();
    blockCapBank = BlockCapBank.create();
    
    blockPainter = BlockPainter.create();
    blockPaintedFence = BlockPaintedFence.create();
    blockPaintedFenceGate = BlockPaintedFenceGate.create();
    blockPaintedWall = BlockPaintedWall.create();
    blockPaintedStair = BlockPaintedStairs.create();
    blockPaintedSlab = BlockPaintedSlab.create();
    blockPaintedGlowstone = BlockPaintedGlowstone.create();
    blockPaintedCarpet = BlockPaintedCarpet.create();
    blockPaintedPressurePlate = BlockPaintedPressurePlate.create();

    blockCrafter = BlockCrafter.create();    
    blockVat = BlockVat.create();
    blockPowerMonitor = BlockPowerMonitor.createPowerMonitor();
    blockPowerMonitorAdvanced = BlockPowerMonitor.createAdvancedPowerMonitor();
    blockFarmStation = BlockFarmStation.create();

    blockWirelessCharger = BlockWirelessCharger.create();
    
    blockTank = BlockTank.create();
    
    blockReservoir = BlockReservoir.create();
    blockVacuumChest = BlockVacuumChest.create();

    blockTransceiver = BlockTransceiver.create();

    blockBuffer = BlockBuffer.create();
    blockInventoryPanel = BlockInventoryPanel.create();

    blockEnderIo = BlockEnderIO.create();
    
    blockTravelPlatform = BlockTravelAnchor.create();
    itemCoordSelector = ItemCoordSelector.create();
    
    blockTelePad = BlockTelePad.createTelepad();    

    blockSliceAndSplice = BlockSliceAndSplice.create();
    blockSoulFuser = BlockSoulBinder.create();
    blockPoweredSpawner = BlockPoweredSpawner.create();
    blockKillerJoe = BlockKillerJoe.create();
    blockAttractor = BlockAttractor.create();
    blockSpawnGuard = BlockAversionObelisk.create();
    blockExperianceOblisk = BlockExperienceObelisk.create();
    blockWeatherObelisk = BlockWeatherObelisk.create();
    blockInhibitorObelisk = BlockInhibitorObelisk.create();
    blockEnchanter = BlockEnchanter.create();


    blockDarkSteelAnvil = BlockDarkSteelAnvil.create();
    blockDarkSteelLadder = BlockDarkSteelLadder.create();
        
    blockElectricLight = BlockElectricLight.create();
    blockLightNode = BlockLightNode.create();

    blockReinforcedObsidian = BlockReinforcedObsidian.create();

    blockFusedQuartz = BlockFusedQuartz.create();
    BlockPaintedFusedQuartz.create();

//    blockEnderRail = BlockEnderRail.create();

    blockConduitBundle = BlockConduitBundle.create();
    
    blockConduitFacade = BlockConduitFacade.create();

    itemBrokenSpawner = ItemBrokenSpawner.create();

    blockEndermanSkull = BlockEndermanSkull.create();
    itemFrankenSkull = ItemFrankenSkull.create();

    itemRedstoneConduit = ItemRedstoneConduit.create();
    itemPowerConduit = ItemPowerConduit.create();
    itemLiquidConduit = ItemLiquidConduit.create();
    itemItemConduit = ItemItemConduit.create();

    itemBasicFilterUpgrade = ItemBasicItemFilter.create();
    itemExistingItemFilter = ItemExistingItemFilter.create();
    itemModItemFilter = ItemModItemFilter.create();
    itemPowerItemFilter = ItemPowerItemFilter.create();
    itemExtractSpeedUpgrade = ItemExtractSpeedUpgrade.create();
    itemFunctionUpgrade = ItemFunctionUpgrade.create();

    itemBasicCapacitor = ItemCapacitor.create();
    itemMachinePart = ItemMachinePart.create();
    itemMaterial = ItemMaterial.create();
    itemAlloy = ItemAlloy.create();
    itemPowderIngot = ItemPowderIngot.create();

    fluids = new Fluids();
    fluids.registerFluids();

    itemYetaWench = ItemYetaWrench.create();
    itemEnderface = ItemEnderface.create();
    itemTravelStaff = ItemTravelStaff.create();
    itemConduitProbe = ItemConduitProbe.create();

    itemXpTransfer = ItemXpTransfer.create();
    itemSoulVessel = ItemSoulVessel.create();

    blockIngotStorage = BlockIngotStorage.create();

    blockDarkIronBars = BlockDarkIronBars.create();
    
    itemEnderFood = ItemEnderFood.create();

    blockGauge = BlockGauge.create();

    DarkSteelItems.createDarkSteelArmorItems();

    EntityRegistry.registerModEntity(RangeEntity.class, "rangeEntity", Config.rangeEntityID, this, 0, 0, false);

    FMLInterModComms.sendMessage("Waila", "register", "crazypants.enderio.waila.WailaCompat.load");

    MaterialRecipes.registerOresInDictionary();
    
    proxy.preInit();
  }

  @EventHandler
  public void load(FMLInitializationEvent event) {

    Config.init();

    instance = this;

    PacketHandler.INSTANCE.registerMessage(MessageTileNBT.class, MessageTileNBT.class, PacketHandler.nextID(), Side.SERVER);
    PacketHandler.INSTANCE.registerMessage(PacketRedstoneMode.class, PacketRedstoneMode.class, PacketHandler.nextID(), Side.SERVER);

    NetworkRegistry.INSTANCE.registerGuiHandler(this, guiHandler);
    MinecraftForge.EVENT_BUS.register(this);

    //Register Custom Dungeon Loot here
    if(Config.lootDarkSteel) {
      ChestGenHooks.getInfo(ChestGenHooks.DUNGEON_CHEST).addItem(
          new WeightedRandomChestContent(new ItemStack(EnderIO.itemAlloy, 1, Alloy.DARK_STEEL.ordinal()), 1, 3, 15));
    }

    if(Config.lootItemConduitProbe) {
      ChestGenHooks.getInfo(ChestGenHooks.DUNGEON_CHEST).addItem(new WeightedRandomChestContent(new ItemStack(EnderIO.itemConduitProbe, 1, 0), 1, 1, 10));
    }

    if(Config.lootQuartz) {
      ChestGenHooks.getInfo(ChestGenHooks.DUNGEON_CHEST).addItem(new WeightedRandomChestContent(new ItemStack(Items.quartz), 3, 16, 20));
    }

    if(Config.lootNetherWart) {
      ChestGenHooks.getInfo(ChestGenHooks.DUNGEON_CHEST).addItem(new WeightedRandomChestContent(new ItemStack(Items.nether_wart), 1, 4, 10));
    }

    if(Config.lootEnderPearl) {
      ChestGenHooks.getInfo(ChestGenHooks.DUNGEON_CHEST).addItem(new WeightedRandomChestContent(new ItemStack(Items.ender_pearl), 1, 2, 30));
    }

    if(Config.lootElectricSteel) {
      ChestGenHooks.getInfo(ChestGenHooks.VILLAGE_BLACKSMITH).addItem(
          new WeightedRandomChestContent(new ItemStack(EnderIO.itemAlloy, 1, Alloy.ELECTRICAL_STEEL.ordinal()), 2, 6, 20));
    }

    if(Config.lootRedstoneAlloy) {
      ChestGenHooks.getInfo(ChestGenHooks.VILLAGE_BLACKSMITH).addItem(
          new WeightedRandomChestContent(new ItemStack(EnderIO.itemAlloy, 1, Alloy.REDSTONE_ALLOY.ordinal()), 3, 6, 35));
    }

    if(Config.lootDarkSteel) {
      ChestGenHooks.getInfo(ChestGenHooks.VILLAGE_BLACKSMITH).addItem(
          new WeightedRandomChestContent(new ItemStack(EnderIO.itemAlloy, 1, Alloy.DARK_STEEL.ordinal()), 3, 6, 35));
    }

    if(Config.lootPhasedIron) {
      ChestGenHooks.getInfo(ChestGenHooks.VILLAGE_BLACKSMITH).addItem(
          new WeightedRandomChestContent(new ItemStack(EnderIO.itemAlloy, 1, Alloy.PULSATING_IRON.ordinal()), 1, 2, 10));
    }

    if(Config.lootPhasedGold) {
      ChestGenHooks.getInfo(ChestGenHooks.VILLAGE_BLACKSMITH).addItem(
          new WeightedRandomChestContent(new ItemStack(EnderIO.itemAlloy, 1, Alloy.VIBRANT_ALLOY.ordinal()), 1, 2, 5));
    }

    if(Config.lootTravelStaff) {
      ItemStack staff = new ItemStack(EnderIO.itemTravelStaff, 1, 0);
      ChestGenHooks.getInfo(ChestGenHooks.PYRAMID_DESERT_CHEST).addItem(new WeightedRandomChestContent(staff, 1, 1, 3));
      ChestGenHooks.getInfo(ChestGenHooks.PYRAMID_JUNGLE_CHEST).addItem(new WeightedRandomChestContent(staff, 1, 1, 3));
    }

    DarkSteelItems.addLoot();

    if(Loader.isModLoaded("ComputerCraft")) {
      ConduitBundledRedstoneProvider.register();
    }

    MaterialRecipes.registerDependantOresInDictionary();

    if (Config.recipeLevel >= 0) {
      EnderfaceRecipes.addRecipes();
      MaterialRecipes.addRecipes();
      ConduitRecipes.addRecipes();
      MachineRecipes.addRecipes();
      ItemRecipes.addRecipes();
      TeleportRecipes.addRecipes();
    }
    
    proxy.init();
  }

  @EventHandler
  public void postInit(FMLPostInitializationEvent event) {

    Config.postInit();

    //Regsiter the enchants
    Enchantments.getInstance();
    
    //This must be loaded before parsing the recipes so we get the preferred outputs
    OreDictionaryPreferences.loadConfig();

    SagMillRecipeManager.getInstance().loadRecipesFromConfig();
    AlloyRecipeManager.getInstance().loadRecipesFromConfig();
    SliceAndSpliceRecipeManager.getInstance().loadRecipesFromConfig();
    VatRecipeManager.getInstance().loadRecipesFromConfig();
    EnchanterRecipeManager.getInstance().loadRecipesFromConfig();
    FarmersRegistry.addFarmers();
    SoulBinderRecipeManager.getInstance().addDefaultRecipes();
    PaintSourceValidator.instance.loadConfig();

    if(Fluids.fluidXpJuice == null) { //should have been registered by open blocks
      fluids.forgeRegisterXPJuice();      
    }

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
    
    //Register villagers
    FMLInterModComms.sendMessage("EnderStructures", "addResourcePath", "/assets/enderio/villagers");
    FMLInterModComms.sendMessage("EnderStructures", "registerVillageGenerator", "enderioMobDropVillager");
  }

  @EventHandler
  public void loadComplete(FMLLoadCompleteEvent event) {
    processImc(FMLInterModComms.fetchRuntimeMessages(this)); //Some mods send IMCs during PostInit, so we catch them here.
  }

  private void addModIntegration() {

//    if(Loader.isModLoaded("TConstruct")) {
//      try {
//        Class<?> ttClass = Class.forName("tconstruct.tools.TinkerTools");
//        Field modFluxF = ttClass.getField("modFlux");
//        Object modFluxInst = modFluxF.get(null);
//
//        Class<?> modFluxClass = Class.forName("tconstruct.modifiers.tools.ModFlux");
//        Field batteriesField = modFluxClass.getField("batteries");
//        List<ItemStack> batteries = (List<ItemStack>) batteriesField.get(modFluxInst);
//        batteries.add(new ItemStack(blockCapBank));
//        Log.info("Registered Capacitor Banks as Tinkers Construct Flux Upgrades");
//      } catch (Exception e) {
//        //Doesn't matter if it didnt work
//        Log.info("Failed to registered Capacitor Banks as Tinkers Construct Flux Upgrades");
//      }
//    }
    
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
            SagMillRecipeManager.getInstance().addCustomRecipes(value);
          } else if(IMC.ALLOY_RECIPE.equals(key)) {
            AlloyRecipeManager.getInstance().addCustomRecipes(value);
          } else if(IMC.POWERED_SPAWNER_BLACKLIST_ADD.equals(key)) {
            PoweredSpawnerConfig.getInstance().addToBlacklist(value);
          } else if(IMC.TELEPORT_BLACKLIST_ADD.equals(key)) {
            TravelController.instance.addBlockToBlinkBlackList(value);
          } else if(IMC.SOUL_VIAL_BLACKLIST.equals(key) && itemSoulVessel != null) {
            CapturedMob.addToBlackList(value);
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
    ServerChannelRegister.load();
  }

  @EventHandler
  public void serverStopped(FMLServerStoppedEvent event) {
    ServerChannelRegister.store();
  }
}
