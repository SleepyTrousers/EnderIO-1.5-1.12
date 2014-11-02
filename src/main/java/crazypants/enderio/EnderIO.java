package crazypants.enderio;

import static crazypants.enderio.EnderIO.MODID;
import static crazypants.enderio.EnderIO.MOD_NAME;
import static crazypants.enderio.EnderIO.VERSION;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.List;

import net.minecraft.block.material.Material;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.WeightedRandomChestContent;
import net.minecraftforge.common.ChestGenHooks;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;

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
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartedEvent;
import cpw.mods.fml.common.event.FMLServerStoppedEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.relauncher.Side;
import crazypants.enderio.block.BlockDarkSteelAnvil;
import crazypants.enderio.block.BlockDarkSteelPressurePlate;
import crazypants.enderio.block.BlockReinforcedObsidian;
import crazypants.enderio.conduit.BlockConduitBundle;
import crazypants.enderio.conduit.ConduitRecipes;
import crazypants.enderio.conduit.facade.BlockConduitFacade;
import crazypants.enderio.conduit.facade.ItemConduitFacade;
import crazypants.enderio.conduit.gas.ItemGasConduit;
import crazypants.enderio.conduit.geom.ConduitGeometryUtil;
import crazypants.enderio.conduit.item.ItemExtractSpeedUpgrade;
import crazypants.enderio.conduit.item.ItemItemConduit;
import crazypants.enderio.conduit.item.filter.ItemBasicItemFilter;
import crazypants.enderio.conduit.item.filter.ItemExistingItemFilter;
import crazypants.enderio.conduit.item.filter.ItemModItemFilter;
import crazypants.enderio.conduit.liquid.ItemLiquidConduit;
import crazypants.enderio.conduit.power.ItemPowerConduit;
import crazypants.enderio.conduit.redstone.ItemRedstoneConduit;
import crazypants.enderio.config.Config;
import crazypants.enderio.enderface.BlockEnderIO;
import crazypants.enderio.enderface.EnderfaceRecipes;
import crazypants.enderio.enderface.ItemEnderface;
import crazypants.enderio.fluid.BlockFluidEio;
import crazypants.enderio.fluid.Fluids;
import crazypants.enderio.fluid.ItemBucketEio;
import crazypants.enderio.fluid.FluidFuelRegister;
import crazypants.enderio.item.ItemConduitProbe;
import crazypants.enderio.item.ItemMagnet;
import crazypants.enderio.item.ItemRecipes;
import crazypants.enderio.item.ItemSoulVessel;
import crazypants.enderio.item.ItemYetaWrench;
import crazypants.enderio.item.darksteel.ItemDarkSteelArmor;
import crazypants.enderio.item.darksteel.ItemDarkSteelAxe;
import crazypants.enderio.item.darksteel.ItemDarkSteelPickaxe;
import crazypants.enderio.item.darksteel.ItemDarkSteelSword;
import crazypants.enderio.item.darksteel.ItemGliderWing;
import crazypants.enderio.item.darksteel.SoundEntity;
import crazypants.enderio.item.skull.BlockEndermanSkull;
import crazypants.enderio.machine.MachineRecipes;
import crazypants.enderio.machine.PacketRedstoneMode;
import crazypants.enderio.machine.alloy.AlloyRecipeManager;
import crazypants.enderio.machine.alloy.BlockAlloySmelter;
import crazypants.enderio.machine.attractor.BlockAttractor;
import crazypants.enderio.machine.crafter.BlockCrafter;
import crazypants.enderio.machine.crusher.BlockCrusher;
import crazypants.enderio.machine.crusher.CrusherRecipeManager;
import crazypants.enderio.machine.enchanter.BlockEnchanter;
import crazypants.enderio.machine.enchanter.EnchanterRecipeManager;
import crazypants.enderio.machine.farm.BlockFarmStation;
import crazypants.enderio.machine.farm.FarmersRegistry;
import crazypants.enderio.machine.generator.combustion.BlockCombustionGenerator;
import crazypants.enderio.machine.generator.stirling.BlockStirlingGenerator;
import crazypants.enderio.machine.generator.zombie.BlockZombieGenerator;
import crazypants.enderio.machine.hypercube.BlockHyperCube;
import crazypants.enderio.machine.hypercube.HyperCubeRegister;
import crazypants.enderio.machine.killera.BlockKillerJoe;
import crazypants.enderio.machine.light.BlockElectricLight;
import crazypants.enderio.machine.light.BlockLightNode;
import crazypants.enderio.machine.monitor.BlockPowerMonitor;
import crazypants.enderio.machine.painter.BlockPaintedCarpet;
import crazypants.enderio.machine.painter.BlockPaintedFence;
import crazypants.enderio.machine.painter.BlockPaintedFenceGate;
import crazypants.enderio.machine.painter.BlockPaintedGlowstone;
import crazypants.enderio.machine.painter.BlockPaintedSlab;
import crazypants.enderio.machine.painter.BlockPaintedStair;
import crazypants.enderio.machine.painter.BlockPaintedWall;
import crazypants.enderio.machine.painter.BlockPainter;
import crazypants.enderio.machine.power.BlockCapacitorBank;
import crazypants.enderio.machine.reservoir.BlockReservoir;
import crazypants.enderio.machine.slicensplice.BlockSliceAndSplice;
import crazypants.enderio.machine.slicensplice.SliceAndSpliceRecipeManager;
import crazypants.enderio.machine.solar.BlockSolarPanel;
import crazypants.enderio.machine.soul.BlockSoulBinder;
import crazypants.enderio.machine.soul.SoulBinderRecipeManager;
import crazypants.enderio.machine.spawner.BlockPoweredSpawner;
import crazypants.enderio.machine.spawner.ItemBrokenSpawner;
import crazypants.enderio.machine.spawnguard.BlockSpawnGuard;
import crazypants.enderio.machine.spawnguard.RangeEntity;
import crazypants.enderio.machine.still.BlockVat;
import crazypants.enderio.machine.still.VatRecipeManager;
import crazypants.enderio.machine.tank.BlockTank;
import crazypants.enderio.machine.transceiver.BlockTransceiver;
import crazypants.enderio.machine.transceiver.ServerChannelRegister;
import crazypants.enderio.machine.vacuum.BlockVacuumChest;
import crazypants.enderio.machine.wireless.BlockWirelessCharger;
import crazypants.enderio.machine.xp.BlockExperienceObelisk;
import crazypants.enderio.machine.xp.ItemXpTransfer;
import crazypants.enderio.material.Alloy;
import crazypants.enderio.material.BlockDarkIronBars;
import crazypants.enderio.material.BlockFusedQuartz;
import crazypants.enderio.material.BlockIngotStorage;
import crazypants.enderio.material.ItemAlloy;
import crazypants.enderio.material.ItemCapacitor;
import crazypants.enderio.material.ItemFrankenSkull;
import crazypants.enderio.material.ItemFusedQuartzFrame;
import crazypants.enderio.material.ItemMachinePart;
import crazypants.enderio.material.ItemMaterial;
import crazypants.enderio.material.ItemPowderIngot;
import crazypants.enderio.material.MaterialRecipes;
import crazypants.enderio.network.MessageTileNBT;
import crazypants.enderio.network.PacketHandler;
import crazypants.enderio.rail.BlockEnderRail;
import crazypants.enderio.teleport.BlockTravelAnchor;
import crazypants.enderio.teleport.ItemTravelStaff;
import crazypants.enderio.teleport.TeleportRecipes;
import crazypants.util.EntityUtil;

@Mod(modid = MODID, name = MOD_NAME, version = VERSION, dependencies = "required-after:Forge@10.13.0.1150,);after:MineFactoryReloaded", guiFactory = "crazypants.enderio.config.ConfigFactoryEIO")
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

  // Materials
  public static ItemCapacitor itemBasicCapacitor;
  public static ItemAlloy itemAlloy;
  public static BlockFusedQuartz blockFusedQuartz;
  public static ItemFusedQuartzFrame itemFusedQuartzFrame;
  public static ItemMachinePart itemMachinePart;
  public static ItemPowderIngot itemPowderIngot;
  public static ItemMaterial itemMaterial;
  public static BlockIngotStorage blockIngotStorage;
  public static BlockDarkIronBars blockDarkIronBars;

  // Enderface
  public static BlockEnderIO blockEnderIo;
  public static ItemEnderface itemEnderface;

  //Teleporting
  public static BlockTravelAnchor blockTravelPlatform;
  public static ItemTravelStaff itemTravelStaff;

  // Painter
  public static BlockPainter blockPainter;
  public static BlockPaintedFence blockPaintedFence;
  public static BlockPaintedFenceGate blockPaintedFenceGate;
  public static BlockPaintedWall blockPaintedWall;
  public static BlockPaintedStair blockPaintedStair;
  public static BlockPaintedSlab blockPaintedSlab;
  public static BlockPaintedSlab blockPaintedDoubleSlab;
  public static BlockPaintedGlowstone blockPaintedGlowstone;
  public static BlockPaintedCarpet blockPaintedCarpet;

  // Conduits
  public static BlockConduitBundle blockConduitBundle;
  public static BlockConduitFacade blockConduitFacade;
  public static ItemConduitFacade itemConduitFacade;
  public static ItemRedstoneConduit itemRedstoneConduit;
  public static ItemPowerConduit itemPowerConduit;
  public static ItemLiquidConduit itemLiquidConduit;
  public static ItemItemConduit itemItemConduit;
  public static ItemGasConduit itemGasConduit;
  public static ItemBasicItemFilter itemBasicFilterUpgrade;
  public static ItemExistingItemFilter itemExistingItemFilter;
  public static ItemModItemFilter itemModItemFilter;
  public static ItemExtractSpeedUpgrade itemExtractSpeedUpgrade;

  // Machines
  public static BlockStirlingGenerator blockStirlingGenerator;
  public static BlockCombustionGenerator blockCombustionGenerator;
  public static BlockZombieGenerator blockZombieGenerator;
  public static BlockSolarPanel blockSolarPanel;
  public static BlockReservoir blockReservoir;
  public static BlockAlloySmelter blockAlloySmelter;
  public static BlockCapacitorBank blockCapacitorBank;
  public static BlockWirelessCharger blockWirelessCharger;
  public static BlockCrusher blockCrusher;
  public static BlockHyperCube blockHyperCube;
  public static BlockPowerMonitor blockPowerMonitor;
  public static BlockVat blockVat;
  public static BlockFarmStation blockFarmStation;
  public static BlockTank blockTank;
  public static BlockCrafter blockCrafter;
  public static BlockPoweredSpawner blockPoweredSpawner;
  public static ItemBrokenSpawner itemBrokenSpawner;
  public static BlockSliceAndSplice blockSliceAndSplice;
  public static BlockSoulBinder blockSoulFuser;
  public static BlockAttractor blockAttractor;
  public static BlockSpawnGuard blockSpawnGuard;
  public static BlockExperienceObelisk blockExperianceOblisk;
  public static BlockTransceiver blockTransceiver;

  public static BlockKillerJoe blockKillerJoe;

  public static BlockEnchanter blockEnchanter;

  public static BlockElectricLight blockElectricLight;
  public static BlockLightNode blockLightNode;

  //Blocks
  public static BlockDarkSteelPressurePlate blockDarkSteelPressurePlate;
  public static BlockDarkSteelAnvil blockDarkSteelAnvil;
  public static BlockEndermanSkull blockEndermanSkull;
  public static BlockReinforcedObsidian blockReinforcedObsidian;
  
  public static BlockEnderRail blockEnderRail;

  //Fluids
  public static Fluid fluidNutrientDistillation;
  public static BlockFluidEio blockNutrientDistillation;
  public static ItemBucketEio itemBucketNutrientDistillation;

  public static Fluid fluidHootch;
  public static BlockFluidEio blockHootch;
  public static ItemBucketEio itemBucketHootch;

  public static Fluid fluidRocketFuel;
  public static BlockFluidEio blockRocketFuel;
  public static ItemBucketEio itemBucketRocketFuel;

  public static Fluid fluidFireWater;
  public static BlockFluidEio blockFireWater;
  public static ItemBucketEio itemBucketFireWater;

  //Open block compatable liquid XP
  public static Fluid fluidXpJuice;
  public static ItemBucketEio itemBucketXpJuice;

  // Items
  public static ItemYetaWrench itemYetaWench;
  public static ItemConduitProbe itemConduitProbe;
  public static ItemMagnet itemMagnet;
  public static ItemXpTransfer itemXpTransfer;

  public static ItemSoulVessel itemSoulVessel;
  public static ItemFrankenSkull itemFrankenSkull;

  public static ItemDarkSteelArmor itemDarkSteelHelmet;
  public static ItemDarkSteelArmor itemDarkSteelChestplate;
  public static ItemDarkSteelArmor itemDarkSteelLeggings;
  public static ItemDarkSteelArmor itemDarkSteelBoots;
  public static ItemDarkSteelSword itemDarkSteelSword;
  public static ItemDarkSteelPickaxe itemDarkSteelPickaxe;
  public static ItemDarkSteelAxe itemDarkSteelAxe;
  public static BlockVacuumChest blockVacuumChest;
  public static ItemGliderWing itemGliderWing;

  @EventHandler
  public void preInit(FMLPreInitializationEvent event) {

    Config.load(event);

    ConduitGeometryUtil.setupBounds((float) Config.conduitScale);

    blockStirlingGenerator = BlockStirlingGenerator.create();
    blockCombustionGenerator = BlockCombustionGenerator.create();
    blockZombieGenerator = BlockZombieGenerator.create();
    blockSolarPanel = BlockSolarPanel.create();

    blockCrusher = BlockCrusher.create();
    blockAlloySmelter = BlockAlloySmelter.create();
    blockCapacitorBank = BlockCapacitorBank.create();

    blockPainter = BlockPainter.create();
    blockPaintedFence = BlockPaintedFence.create();
    blockPaintedFenceGate = BlockPaintedFenceGate.create();
    blockPaintedWall = BlockPaintedWall.create();
    blockPaintedStair = BlockPaintedStair.create();
    blockPaintedSlab = new BlockPaintedSlab(false);
    blockPaintedDoubleSlab = new BlockPaintedSlab(true);
    blockPaintedSlab.init();
    blockPaintedDoubleSlab.init();
    blockCrafter = BlockCrafter.create();
    blockPaintedGlowstone = BlockPaintedGlowstone.create();
    blockPaintedCarpet = BlockPaintedCarpet.create();

    blockVat = BlockVat.create();
    blockPowerMonitor = BlockPowerMonitor.create();
    blockFarmStation = BlockFarmStation.create();

    blockWirelessCharger = BlockWirelessCharger.create();
    blockHyperCube = BlockHyperCube.create();
    blockTank = BlockTank.create();
    blockReservoir = BlockReservoir.create();
    blockVacuumChest = BlockVacuumChest.create();

    blockTransceiver = BlockTransceiver.create();

    blockEnderIo = BlockEnderIO.create();
    blockTravelPlatform = BlockTravelAnchor.create();

    blockSliceAndSplice = BlockSliceAndSplice.create();
    blockSoulFuser = BlockSoulBinder.create();
    blockPoweredSpawner = BlockPoweredSpawner.create();
    blockKillerJoe = BlockKillerJoe.create();
    blockAttractor = BlockAttractor.create();
    blockSpawnGuard = BlockSpawnGuard.create();
    blockExperianceOblisk = BlockExperienceObelisk.create();
    blockEnchanter = BlockEnchanter.create();

    blockDarkSteelPressurePlate = BlockDarkSteelPressurePlate.create();
    blockDarkSteelAnvil = BlockDarkSteelAnvil.create();
    blockElectricLight = BlockElectricLight.create();
    blockLightNode = BlockLightNode.create();

    blockReinforcedObsidian = BlockReinforcedObsidian.create();

    blockFusedQuartz = BlockFusedQuartz.create();
    itemFusedQuartzFrame = ItemFusedQuartzFrame.create();

    blockEnderRail = BlockEnderRail.create();
    
    blockConduitBundle = BlockConduitBundle.create();
    blockConduitFacade = BlockConduitFacade.create();
    itemConduitFacade = ItemConduitFacade.create();

    itemBrokenSpawner = ItemBrokenSpawner.create();

    blockEndermanSkull = BlockEndermanSkull.create();
    itemFrankenSkull = ItemFrankenSkull.create();

    itemRedstoneConduit = ItemRedstoneConduit.create();
    itemPowerConduit = ItemPowerConduit.create();
    itemLiquidConduit = ItemLiquidConduit.create();
    itemItemConduit = ItemItemConduit.create();
    itemGasConduit = ItemGasConduit.create();

    itemBasicFilterUpgrade = ItemBasicItemFilter.create();
    itemExistingItemFilter = ItemExistingItemFilter.create();
    itemModItemFilter = ItemModItemFilter.create();
    itemExtractSpeedUpgrade = ItemExtractSpeedUpgrade.create();

    itemBasicCapacitor = ItemCapacitor.create();
    itemMachinePart = ItemMachinePart.create();
    itemMaterial = ItemMaterial.create();
    itemAlloy = ItemAlloy.create();
    itemPowderIngot = ItemPowderIngot.create();

    registerFluids();

    itemYetaWench = ItemYetaWrench.create();
    itemEnderface = ItemEnderface.create();
    itemTravelStaff = ItemTravelStaff.create();
    itemConduitProbe = ItemConduitProbe.create();

    itemMagnet = ItemMagnet.create();
    itemXpTransfer = ItemXpTransfer.create();

    itemSoulVessel = ItemSoulVessel.create();

    blockIngotStorage = BlockIngotStorage.create();

    blockDarkIronBars = BlockDarkIronBars.create();

    itemGliderWing = ItemGliderWing.create();

    itemDarkSteelHelmet = ItemDarkSteelArmor.create(0);
    itemDarkSteelChestplate = ItemDarkSteelArmor.create(1);
    itemDarkSteelLeggings = ItemDarkSteelArmor.create(2);
    itemDarkSteelBoots = ItemDarkSteelArmor.create(3);

    itemDarkSteelSword = ItemDarkSteelSword.create();
    itemDarkSteelPickaxe = ItemDarkSteelPickaxe.create();
    itemDarkSteelAxe = ItemDarkSteelAxe.create();

    int entityID = EntityRegistry.findGlobalUniqueEntityId();
    EntityRegistry.registerGlobalEntityID(SoundEntity.class, "soundEntity", entityID);
    EntityRegistry.registerModEntity(SoundEntity.class, "soundEntity", entityID, this, 0, 0, false);

    entityID = EntityRegistry.findGlobalUniqueEntityId();
    EntityRegistry.registerGlobalEntityID(RangeEntity.class, "rangeEntity", entityID);
    EntityRegistry.registerModEntity(RangeEntity.class, "rangeEntity", entityID, this, 0, 0, false);

    FMLInterModComms.sendMessage("Waila", "register", "crazypants.enderio.waila.WailaCompat.load");

    MaterialRecipes.registerOresInDictionary();
  }

  private void registerFluids() {
    Fluid f = new Fluid(Fluids.NUTRIENT_DISTILLATION_NAME).setDensity(1500).setViscosity(3000);
    FluidRegistry.registerFluid(f);
    fluidNutrientDistillation = FluidRegistry.getFluid(f.getName());
    blockNutrientDistillation = BlockFluidEio.create(fluidNutrientDistillation, Material.water);

    f = new Fluid(Fluids.HOOTCH_NAME).setDensity(900).setViscosity(1000);
    FluidRegistry.registerFluid(f);
    fluidHootch = FluidRegistry.getFluid(f.getName());
    blockHootch = BlockFluidEio.create(fluidHootch, Material.water);    
    FluidFuelRegister.instance.addFuel(f, Config.hootchPowerPerCycleRF, Config.hootchPowerTotalBurnTime);
    FMLInterModComms.sendMessage("Railcraft", "boiler-fuel-liquid", Fluids.HOOTCH_NAME + "@" + (Config.hootchPowerPerCycleRF/10 * Config.hootchPowerTotalBurnTime));

    f = new Fluid(Fluids.ROCKET_FUEL_NAME).setDensity(900).setViscosity(1000);
    FluidRegistry.registerFluid(f);
    fluidRocketFuel = FluidRegistry.getFluid(f.getName());
    blockRocketFuel = BlockFluidEio.create(fluidRocketFuel, Material.water);
    FluidFuelRegister.instance.addFuel(f, Config.rocketFuelPowerPerCycleRF, Config.rocketFuelPowerTotalBurnTime);
    FMLInterModComms.sendMessage("Railcraft", "boiler-fuel-liquid", Fluids.ROCKET_FUEL_NAME + "@"
        + (Config.rocketFuelPowerPerCycleRF/10 * Config.rocketFuelPowerTotalBurnTime));

    f = new Fluid(Fluids.FIRE_WATER_NAME).setDensity(900).setViscosity(1000);
    FluidRegistry.registerFluid(f);
    fluidFireWater = FluidRegistry.getFluid(f.getName());
    blockFireWater = BlockFluidEio.create(fluidFireWater, Material.lava);
    FluidFuelRegister.instance.addFuel(f, Config.fireWaterPowerPerCycleRF, Config.fireWaterPowerTotalBurnTime);
    FMLInterModComms.sendMessage("Railcraft", "boiler-fuel-liquid", Fluids.FIRE_WATER_NAME + "@"
        + (Config.fireWaterPowerPerCycleRF/10 * Config.fireWaterPowerTotalBurnTime));

    fluidXpJuice = FluidRegistry.getFluid("xpjuice");
    if(!Loader.isModLoaded("OpenBlocks")) {
      Log.info("XP Juice registered by Ender IO.");
      fluidXpJuice = new Fluid("xpjuice").setLuminosity(10).setDensity(800).setViscosity(1500).setUnlocalizedName("eio.xpjuice");
      FluidRegistry.registerFluid(fluidXpJuice);
      itemBucketXpJuice = ItemBucketEio.create(fluidXpJuice);
    } else {
      Log.info("XP Juice regististration left to Open Blocks.");
    }

    itemBucketNutrientDistillation = ItemBucketEio.create(fluidNutrientDistillation);
    itemBucketHootch = ItemBucketEio.create(fluidHootch);
    itemBucketRocketFuel = ItemBucketEio.create(fluidRocketFuel);
    itemBucketFireWater = ItemBucketEio.create(fluidFireWater);
  }

  @EventHandler
  public void load(FMLInitializationEvent event) {

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
          new WeightedRandomChestContent(new ItemStack(EnderIO.itemAlloy, 1, Alloy.PHASED_IRON.ordinal()), 1, 2, 10));
    }

    if(Config.lootPhasedGold) {
      ChestGenHooks.getInfo(ChestGenHooks.VILLAGE_BLACKSMITH).addItem(
          new WeightedRandomChestContent(new ItemStack(EnderIO.itemAlloy, 1, Alloy.PHASED_GOLD.ordinal()), 1, 2, 5));
    }

    if(Config.lootTravelStaff) {
      ItemStack staff = new ItemStack(EnderIO.itemTravelStaff, 1, 0);
      ChestGenHooks.getInfo(ChestGenHooks.PYRAMID_DESERT_CHEST).addItem(new WeightedRandomChestContent(staff, 1, 1, 3));
      ChestGenHooks.getInfo(ChestGenHooks.PYRAMID_JUNGLE_CHEST).addItem(new WeightedRandomChestContent(staff, 1, 1, 3));
    }

    if(Config.lootTheEnder) {
      ItemStack sword = new ItemStack(EnderIO.itemDarkSteelSword, 1, 0);
      ChestGenHooks.getInfo(ChestGenHooks.DUNGEON_CHEST).addItem(new WeightedRandomChestContent(sword, 1, 1, 5));
      ChestGenHooks.getInfo(ChestGenHooks.VILLAGE_BLACKSMITH).addItem(new WeightedRandomChestContent(sword, 1, 1, 5));
      ChestGenHooks.getInfo(ChestGenHooks.PYRAMID_DESERT_CHEST).addItem(new WeightedRandomChestContent(sword, 1, 1, 4));
      ChestGenHooks.getInfo(ChestGenHooks.PYRAMID_JUNGLE_CHEST).addItem(new WeightedRandomChestContent(sword, 1, 1, 4));
    }

    if(Config.lootDarkSteelBoots) {
      ItemStack boots = new ItemStack(EnderIO.itemDarkSteelBoots, 1, 0);
      ChestGenHooks.getInfo(ChestGenHooks.DUNGEON_CHEST).addItem(new WeightedRandomChestContent(boots, 1, 1, 5));
      ChestGenHooks.getInfo(ChestGenHooks.VILLAGE_BLACKSMITH).addItem(new WeightedRandomChestContent(boots, 1, 1, 5));
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

    if(fluidXpJuice == null) { //should have been registered by open blocks 
      fluidXpJuice = FluidRegistry.getFluid("xpjuice");
      if(fluidXpJuice == null) {
        Log.error("Liquid XP registration left to open blocks but could not be found.");
      }
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
  }

  @EventHandler
  public void onImc(IMCEvent evt) {
    ImmutableList<IMCMessage> messages = evt.getMessages();
    for (IMCMessage msg : messages) {
      if(msg.isStringMessage()) {
        String key = msg.key;
        String value = msg.getStringValue();
        if(VatRecipeManager.IMC_KEY.equals(key)) {
          VatRecipeManager.getInstance().addCustumRecipes(value);
        } else if(CrusherRecipeManager.IMC_KEY.equals(key)) {
          CrusherRecipeManager.getInstance().addCustomRecipes(value);
        } else if(AlloyRecipeManager.IMC_KEY.equals(key)) {
          AlloyRecipeManager.getInstance().addCustumRecipes(value);
        }
      } else if(msg.isNBTMessage()) {
        if(SoulBinderRecipeManager.IMC_KEY.equals(msg.key)) {
          SoulBinderRecipeManager.getInstance().addRecipeFromNBT(msg.getNBTValue());
        }
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
