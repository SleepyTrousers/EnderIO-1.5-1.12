package crazypants.enderio;

import static crazypants.enderio.EnderIO.MODID;
import static crazypants.enderio.EnderIO.MOD_NAME;
import static crazypants.enderio.EnderIO.VERSION;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Locale;

import com.enderio.core.common.Lang;
import com.enderio.core.common.util.EntityUtil;
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
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.relauncher.Side;
import crazypants.enderio.api.IMC;
import crazypants.enderio.block.BlockDarkSteelAnvil;
import crazypants.enderio.block.BlockDarkSteelLadder;
import crazypants.enderio.block.BlockDarkSteelPressurePlate;
import crazypants.enderio.block.BlockReinforcedObsidian;
import crazypants.enderio.conduit.BlockConduitBundle;
import crazypants.enderio.conduit.ConduitRecipes;
import crazypants.enderio.conduit.facade.BlockConduitFacade;
import crazypants.enderio.conduit.facade.ItemConduitFacade;
import crazypants.enderio.conduit.gas.ItemGasConduit;
import crazypants.enderio.conduit.geom.ConduitGeometryUtil;
import crazypants.enderio.conduit.item.ItemExtractSpeedUpgrade;
import crazypants.enderio.conduit.item.ItemFunctionUpgrade;
import crazypants.enderio.conduit.item.ItemItemConduit;
import crazypants.enderio.conduit.item.filter.ItemBasicItemFilter;
import crazypants.enderio.conduit.item.filter.ItemBigItemFilter;
import crazypants.enderio.conduit.item.filter.ItemExistingItemFilter;
import crazypants.enderio.conduit.item.filter.ItemModItemFilter;
import crazypants.enderio.conduit.item.filter.ItemPowerItemFilter;
import crazypants.enderio.conduit.liquid.ItemLiquidConduit;
import crazypants.enderio.conduit.me.ItemMEConduit;
import crazypants.enderio.conduit.oc.ItemOCConduit;
import crazypants.enderio.conduit.power.ItemPowerConduit;
import crazypants.enderio.conduit.power.endergy.ItemPowerConduitEndergy;
import crazypants.enderio.conduit.redstone.ConduitBundledRedstoneProvider;
import crazypants.enderio.conduit.redstone.InsulatedRedstoneConduit;
import crazypants.enderio.conduit.redstone.ItemRedstoneConduit;
import crazypants.enderio.config.Config;
import crazypants.enderio.enchantment.Enchantments;
import crazypants.enderio.enderface.BlockEnderIO;
import crazypants.enderio.enderface.EnderfaceRecipes;
import crazypants.enderio.enderface.ItemEnderface;
import crazypants.enderio.entity.SkeletonHandler;
import crazypants.enderio.fluid.BlockFluidEio;
import crazypants.enderio.fluid.FluidFuelRegister;
import crazypants.enderio.fluid.Fluids;
import crazypants.enderio.fluid.ItemBucketEio;
import crazypants.enderio.item.ItemConduitProbe;
import crazypants.enderio.item.ItemEnderFood;
import crazypants.enderio.item.ItemRecipes;
import crazypants.enderio.item.ItemSoulVessel;
import crazypants.enderio.item.ItemYetaWrench;
import crazypants.enderio.item.darksteel.DarkSteelItems;
import crazypants.enderio.item.darksteel.SoundEntity;
import crazypants.enderio.item.skull.BlockEndermanSkull;
import crazypants.enderio.machine.MachineRecipes;
import crazypants.enderio.machine.PacketRedstoneMode;
import crazypants.enderio.machine.alloy.AlloyRecipeManager;
import crazypants.enderio.machine.alloy.BlockAlloySmelter;
import crazypants.enderio.machine.buffer.BlockBuffer;
import crazypants.enderio.machine.capbank.BlockCapBank;
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
import crazypants.enderio.machine.generator.zombie.BlockZombieGenerator.BlockEnderGenerator;
import crazypants.enderio.machine.generator.zombie.BlockZombieGenerator.BlockFrankenZombieGenerator;
import crazypants.enderio.machine.generator.zombie.PacketNutrientTank;
import crazypants.enderio.machine.hypercube.BlockHyperCube;
import crazypants.enderio.machine.hypercube.HyperCubeRegister;
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
import crazypants.enderio.machine.painter.BlockPaintedCarpet;
import crazypants.enderio.machine.painter.BlockPaintedFence;
import crazypants.enderio.machine.painter.BlockPaintedFenceGate;
import crazypants.enderio.machine.painter.BlockPaintedGlowstone;
import crazypants.enderio.machine.painter.BlockPaintedSlab;
import crazypants.enderio.machine.painter.BlockPaintedStair;
import crazypants.enderio.machine.painter.BlockPaintedWall;
import crazypants.enderio.machine.painter.BlockPainter;
import crazypants.enderio.machine.painter.PaintSourceValidator;
import crazypants.enderio.machine.power.BlockCapacitorBank;
import crazypants.enderio.machine.ranged.RangeEntity;
import crazypants.enderio.machine.reservoir.BlockReservoir;
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
import crazypants.enderio.material.BlockDarkSteelBars;
import crazypants.enderio.material.BlockDarkSteelBars.BlockEndSteelBars;
import crazypants.enderio.material.BlockDarkSteelBars.BlockSoulariumBars;
import crazypants.enderio.material.BlockFusedQuartz;
import crazypants.enderio.material.BlockIngotStorage;
import crazypants.enderio.material.ItemAlloy;
import crazypants.enderio.material.ItemCapacitor;
import crazypants.enderio.material.ItemFrankenSkull;
import crazypants.enderio.material.ItemFusedQuartzFrame;
import crazypants.enderio.material.ItemGrindingBall;
import crazypants.enderio.material.ItemMachinePart;
import crazypants.enderio.material.ItemMaterial;
import crazypants.enderio.material.ItemPowderIngot;
import crazypants.enderio.material.MaterialRecipes;
import crazypants.enderio.material.OreDictionaryPreferences;
import crazypants.enderio.material.endergy.BlockIngotStorageEndergy;
import crazypants.enderio.material.endergy.ItemAlloyEndergy;
import crazypants.enderio.material.endergy.ItemGrindingBallEndergy;
import crazypants.enderio.network.PacketHandler;
import crazypants.enderio.rail.BlockEnderRail;
import crazypants.enderio.teleport.ItemTravelStaff;
import crazypants.enderio.teleport.TeleportRecipes;
import crazypants.enderio.teleport.TravelController;
import crazypants.enderio.teleport.anchor.BlockTravelAnchor;
import crazypants.enderio.teleport.telepad.BlockTelePad;
import crazypants.enderio.teleport.telepad.ItemCoordSelector;
import crazypants.enderio.thaumcraft.ThaumcraftCompat;
import crazypants.enderio.tool.EnderIOCrashCallable;
import crazypants.util.EE3Util;
import net.minecraft.block.material.Material;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.WeightedRandomChestContent;
import net.minecraftforge.common.ChestGenHooks;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;

@Mod(modid = MODID, name = MOD_NAME, version = VERSION, dependencies = "after:endercore;after:MineFactoryReloaded;after:Forestry;after:Waila@[1.5.8,);after:Thaumcraft;after:appliedenergistics2@[rv2-beta-8,);after:chisel", guiFactory = "crazypants.enderio.config.ConfigFactoryEIO")
public class EnderIO {

  public static final String MODID = "EnderIO";
  public static final String DOMAIN = MODID.toLowerCase(Locale.US);
  public static final String MOD_NAME = "Ender IO";
  public static final String VERSION = "GRADLETOKEN_VERSION";

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
  public static ItemGrindingBall itemGrindingBall;
  public static ItemAlloyEndergy itemAlloyEndergy;
  public static ItemGrindingBallEndergy itemGrindingBallEndergy;
  public static BlockFusedQuartz blockFusedQuartz;
  public static ItemFusedQuartzFrame itemFusedQuartzFrame;
  public static ItemMachinePart itemMachinePart;
  public static ItemPowderIngot itemPowderIngot;
  public static ItemMaterial itemMaterial;
  public static BlockIngotStorage blockIngotStorage;
  public static BlockIngotStorageEndergy blockIngotStorageEndergy;
  public static BlockDarkSteelBars blockDarkIronBars;
  public static BlockSoulariumBars blockSoulariumBars;
  public static BlockEndSteelBars blockEndSteelBars;
  public static ItemEnderFood itemEnderFood;

  // Enderface
  public static BlockEnderIO blockEnderIo;
  public static ItemEnderface itemEnderface;

  //Teleporting
  public static BlockTravelAnchor blockTravelPlatform;
  public static BlockTelePad blockTelePad;
  public static ItemCoordSelector itemCoordSelector;
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
  public static ItemPowerConduitEndergy itemPowerConduitEndergy;
  public static ItemLiquidConduit itemLiquidConduit;
  public static ItemItemConduit itemItemConduit;
  public static ItemGasConduit itemGasConduit;
  public static ItemMEConduit itemMEConduit;
  public static ItemOCConduit itemOCConduit;
  public static ItemBasicItemFilter itemBasicFilterUpgrade;
  public static ItemBigItemFilter itemBigFilterUpgrade;
  public static ItemExistingItemFilter itemExistingItemFilter;
  public static ItemModItemFilter itemModItemFilter;
  public static ItemPowerItemFilter itemPowerItemFilter;
  public static ItemExtractSpeedUpgrade itemExtractSpeedUpgrade;
  public static ItemFunctionUpgrade itemFunctionUpgrade;

  // Machines
  public static BlockStirlingGenerator blockStirlingGenerator;
  public static BlockCombustionGenerator blockCombustionGenerator;
  public static BlockZombieGenerator blockZombieGenerator;
  public static BlockFrankenZombieGenerator blockFrankenZombieGenerator;
  public static BlockEnderGenerator blockEnderGenerator;
  public static BlockSolarPanel blockSolarPanel;
  public static BlockReservoir blockReservoir;
  public static BlockAlloySmelter blockAlloySmelter;
  public static BlockCapacitorBank blockCapacitorBank;
  public static BlockCapBank blockCapBank;
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
  public static BlockAversionObelisk blockSpawnGuard;
  public static BlockExperienceObelisk blockExperianceOblisk;
  public static BlockWeatherObelisk blockWeatherObelisk;
  public static BlockInhibitorObelisk blockInhibitorObelisk;
  public static BlockTransceiver blockTransceiver;
  public static BlockBuffer blockBuffer;
  public static BlockInventoryPanel blockInventoryPanel;

  public static BlockKillerJoe blockKillerJoe;

  public static BlockEnchanter blockEnchanter;

  public static BlockElectricLight blockElectricLight;
  public static BlockLightNode blockLightNode;

  //Blocks
  public static BlockDarkSteelPressurePlate blockDarkSteelPressurePlate;
  public static BlockDarkSteelAnvil blockDarkSteelAnvil;
  public static BlockDarkSteelLadder blockDarkSteelLadder;
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

  public static Fluid fluidLiquidSunshine;
  public static BlockFluidEio blockLiquidSunshine;
  public static ItemBucketEio itemBucketLiquidSunshine;

  public static Fluid fluidCloudSeed;
  public static BlockFluidEio blockCloudSeed;
  public static ItemBucketEio itemBucketCloudSeed;

  public static Fluid fluidCloudSeedConcentrated;
  public static BlockFluidEio blockCloudSeedConcentrated;
  public static ItemBucketEio itemBucketCloudSeedConcentrated;

  public static Fluid fluidEnderDistillation;
  public static BlockFluidEio blockEnderDistillation;
  public static ItemBucketEio itemBucketEnderDistillation;

  public static Fluid fluidVapourOfLevity;
  public static BlockFluidEio blockVapourOfLevity;
  public static ItemBucketEio itemBucketVapourOfLevity;

  //Open block compatable liquid XP
  public static Fluid fluidXpJuice;
  public static ItemBucketEio itemBucketXpJuice;

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

    ConduitGeometryUtil.setupBounds((float) Config.conduitScale);

    blockStirlingGenerator = BlockStirlingGenerator.create();
    blockCombustionGenerator = BlockCombustionGenerator.create();
    blockZombieGenerator = BlockZombieGenerator.create();
    blockFrankenZombieGenerator = BlockFrankenZombieGenerator.create();
    blockEnderGenerator = BlockEnderGenerator.create();
    blockSolarPanel = BlockSolarPanel.create();

    blockCrusher = BlockCrusher.create();
    blockAlloySmelter = BlockAlloySmelter.create();
    blockCapacitorBank = BlockCapacitorBank.create();
    blockCapBank = BlockCapBank.create();

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

    blockBuffer = BlockBuffer.create();
    blockInventoryPanel = BlockInventoryPanel.create();

    blockEnderIo = BlockEnderIO.create();
    blockTravelPlatform = BlockTravelAnchor.create();
    blockTelePad = BlockTelePad.create();
    itemCoordSelector = ItemCoordSelector.create();

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

    blockDarkSteelPressurePlate = BlockDarkSteelPressurePlate.create();
    blockDarkSteelAnvil = BlockDarkSteelAnvil.create();
    blockDarkSteelLadder = BlockDarkSteelLadder.create();
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
    itemPowerConduitEndergy = ItemPowerConduitEndergy.create();
    itemLiquidConduit = ItemLiquidConduit.create();
    itemItemConduit = ItemItemConduit.create();
    itemGasConduit = ItemGasConduit.create();
    itemMEConduit = ItemMEConduit.create();
    itemOCConduit = ItemOCConduit.create();

    itemBasicFilterUpgrade = ItemBasicItemFilter.create();
    itemBigFilterUpgrade = ItemBigItemFilter.create();
    itemExistingItemFilter = ItemExistingItemFilter.create();
    itemModItemFilter = ItemModItemFilter.create();
    itemPowerItemFilter = ItemPowerItemFilter.create();
    itemExtractSpeedUpgrade = ItemExtractSpeedUpgrade.create();
    itemFunctionUpgrade = ItemFunctionUpgrade.create();

    itemBasicCapacitor = ItemCapacitor.create();
    itemMachinePart = ItemMachinePart.create();
    itemMaterial = ItemMaterial.create();
    itemAlloy = ItemAlloy.create();
    itemGrindingBall = ItemGrindingBall.create();
    itemAlloyEndergy = ItemAlloyEndergy.create();
    itemGrindingBallEndergy = ItemGrindingBallEndergy.create();
    itemPowderIngot = ItemPowderIngot.create();

    registerFluids();

    itemYetaWench = ItemYetaWrench.create();
    itemEnderface = ItemEnderface.create();
    itemTravelStaff = ItemTravelStaff.create();
    itemConduitProbe = ItemConduitProbe.create();

    itemXpTransfer = ItemXpTransfer.create();

    itemSoulVessel = ItemSoulVessel.create();

    blockIngotStorage = BlockIngotStorage.create();
    blockIngotStorageEndergy = BlockIngotStorageEndergy.create();

    blockDarkIronBars = BlockDarkSteelBars.create();
    blockSoulariumBars = BlockSoulariumBars.create();
    blockEndSteelBars = BlockEndSteelBars.create();


    itemEnderFood = ItemEnderFood.create();

    DarkSteelItems.createDarkSteelArmorItems();

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
    Fluid f = new Fluid(Fluids.NUTRIENT_DISTILLATION).setDensity(1500).setViscosity(3000);
    FluidRegistry.registerFluid(f);
    fluidNutrientDistillation = FluidRegistry.getFluid(f.getName());
    blockNutrientDistillation = BlockFluidEio.NutrientDistillation.create(fluidNutrientDistillation, Material.water);

    PacketHandler.INSTANCE.registerMessage(PacketNutrientTank.class, PacketNutrientTank.class, PacketHandler.nextID(), Side.CLIENT);

    f = new Fluid(Fluids.HOOTCH).setDensity(900).setViscosity(1000);
    FluidRegistry.registerFluid(f);
    fluidHootch = FluidRegistry.getFluid(f.getName());
    blockHootch = BlockFluidEio.Hootch.create(fluidHootch, Material.water);
    FluidFuelRegister.instance.addFuel(f, Config.hootchPowerPerCycleRF, Config.hootchPowerTotalBurnTime);
    FMLInterModComms.sendMessage("Railcraft", "boiler-fuel-liquid", Fluids.HOOTCH + "@"
        + (Config.hootchPowerPerCycleRF / 10 * Config.hootchPowerTotalBurnTime));

    f = new Fluid(Fluids.ROCKET_FUEL).setDensity(900).setViscosity(1000);
    FluidRegistry.registerFluid(f);
    fluidRocketFuel = FluidRegistry.getFluid(f.getName());
    blockRocketFuel = BlockFluidEio.RocketFuel.create(fluidRocketFuel, Material.water);
    FluidFuelRegister.instance.addFuel(f, Config.rocketFuelPowerPerCycleRF, Config.rocketFuelPowerTotalBurnTime);
    FMLInterModComms.sendMessage("Railcraft", "boiler-fuel-liquid", Fluids.ROCKET_FUEL + "@"
        + (Config.rocketFuelPowerPerCycleRF / 10 * Config.rocketFuelPowerTotalBurnTime));

    f = new Fluid(Fluids.FIRE_WATER).setDensity(900).setViscosity(1000).setTemperature(FluidRegistry.LAVA.getTemperature() * 2);
    FluidRegistry.registerFluid(f);
    fluidFireWater = FluidRegistry.getFluid(f.getName());
    blockFireWater = BlockFluidEio.FireWater.create(fluidFireWater, Material.lava);
    FluidFuelRegister.instance.addFuel(f, Config.fireWaterPowerPerCycleRF, Config.fireWaterPowerTotalBurnTime);
    FMLInterModComms.sendMessage("Railcraft", "boiler-fuel-liquid", Fluids.FIRE_WATER + "@"
        + (Config.fireWaterPowerPerCycleRF / 10 * Config.fireWaterPowerTotalBurnTime));

    f = new Fluid(Fluids.LIQUID_SUNSHINE).setDensity(200).setViscosity(400);
    FluidRegistry.registerFluid(f);
    fluidLiquidSunshine = FluidRegistry.getFluid(f.getName());
    blockLiquidSunshine = BlockFluidEio.create(fluidLiquidSunshine, Material.water);

    f = new Fluid(Fluids.CLOUD_SEED).setDensity(500).setViscosity(800);
    FluidRegistry.registerFluid(f);
    fluidCloudSeed = FluidRegistry.getFluid(f.getName());
    blockCloudSeed = BlockFluidEio.create(fluidCloudSeed, Material.water);

    f = new Fluid(Fluids.CLOUD_SEED_CONCENTRATED).setDensity(1000).setViscosity(1200);
    FluidRegistry.registerFluid(f);
    fluidCloudSeedConcentrated = FluidRegistry.getFluid(f.getName());
    blockCloudSeedConcentrated = BlockFluidEio.CloudSeedConcentrated.create(fluidCloudSeedConcentrated, Material.water);

    f = new Fluid(Fluids.ENDER_DISTILLATION).setDensity(200).setViscosity(1000).setTemperature(175);
    FluidRegistry.registerFluid(f);
    fluidEnderDistillation = FluidRegistry.getFluid(f.getName());
    blockEnderDistillation = BlockFluidEio.create(fluidEnderDistillation, Material.water);

    f = new Fluid(Fluids.VAPOR_OF_LEVITY).setDensity(-10).setViscosity(100).setTemperature(5).setGaseous(true);
    FluidRegistry.registerFluid(f);
    fluidVapourOfLevity = FluidRegistry.getFluid(f.getName());
    blockVapourOfLevity = BlockFluidEio.VapourOfLevity.create(fluidVapourOfLevity, Material.water);
    FluidFuelRegister.instance.addCoolant(f, 0.0314f);

    if (!Loader.isModLoaded("OpenBlocks")) {
      Log.info("XP Juice registered by Ender IO.");
      fluidXpJuice = new Fluid(Config.xpJuiceName).setLuminosity(10).setDensity(800).setViscosity(1500).setUnlocalizedName("eio.xpjuice");
      FluidRegistry.registerFluid(fluidXpJuice);
      itemBucketXpJuice = ItemBucketEio.create(fluidXpJuice);
    } else {
      Log.info("XP Juice regististration left to Open Blocks.");
    }

    itemBucketNutrientDistillation = ItemBucketEio.create(fluidNutrientDistillation);
    itemBucketHootch = ItemBucketEio.create(fluidHootch);
    itemBucketRocketFuel = ItemBucketEio.create(fluidRocketFuel);
    itemBucketFireWater = ItemBucketEio.create(fluidFireWater);
    itemBucketLiquidSunshine = ItemBucketEio.create(fluidLiquidSunshine);
    itemBucketCloudSeed = ItemBucketEio.create(fluidCloudSeed);
    itemBucketCloudSeedConcentrated = ItemBucketEio.create(fluidCloudSeedConcentrated);
    itemBucketEnderDistillation = ItemBucketEio.create(fluidEnderDistillation);
    itemBucketVapourOfLevity = ItemBucketEio.create(fluidVapourOfLevity);

  }

  @EventHandler
  public void load(FMLInitializationEvent event) {

    Config.init();

    instance = this;

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

    DarkSteelItems.addLoot();

    if(Loader.isModLoaded("ComputerCraft")) {
      ConduitBundledRedstoneProvider.register();
    }

    if(Config.replaceWitherSkeletons)
    {
      SkeletonHandler.registerSkeleton(this);
    }

    MaterialRecipes.registerDependantOresInDictionary();

    EnderfaceRecipes.addRecipes();
    MaterialRecipes.addRecipes();
    ConduitRecipes.addRecipes();
    MachineRecipes.addRecipes();
    ItemRecipes.addRecipes();
    TeleportRecipes.addRecipes();
    EE3Util.registerMiscRecipes();

    proxy.load();
  }

  @EventHandler
  public void postInit(FMLPostInitializationEvent event) {

    Config.postInit();

    //Regsiter the enchants
    Enchantments.getInstance();

    //This must be loaded before parsing the recipes so we get the preferred outputs
    OreDictionaryPreferences.loadConfig();

    CrusherRecipeManager.getInstance().loadRecipesFromConfig();
    AlloyRecipeManager.getInstance().loadRecipesFromConfig();
    SliceAndSpliceRecipeManager.getInstance().loadRecipesFromConfig();
    VatRecipeManager.getInstance().loadRecipesFromConfig();
    EnchanterRecipeManager.getInstance().loadRecipesFromConfig();
    FarmersRegistry.addFarmers();
    SoulBinderRecipeManager.getInstance().addDefaultRecipes();
    PaintSourceValidator.instance.loadConfig();

    if(fluidXpJuice == null) { //should have been registered by open blocks
      fluidXpJuice = FluidRegistry.getFluid(getXPJuiceName());
      if(fluidXpJuice == null) {
        Log.error("Liquid XP Juice registration left to open blocks but could not be found.");
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

    addModIntegration();

    //Register villagers
    FMLInterModComms.sendMessage("EnderStructures", "addResourcePath", "/assets/enderio/villagers");
    FMLInterModComms.sendMessage("EnderStructures", "registerVillageGenerator", "enderioMobDropVillager");
  }

  @EventHandler
  public void loadComplete(FMLLoadCompleteEvent event) {
    processImc(FMLInterModComms.fetchRuntimeMessages(this)); //Some mods send IMCs during PostInit, so we catch them here.
  }

  private static String getXPJuiceName() {
    String openBlocksXPJuiceName = null;

    try {
      Field getField = Class.forName("openblocks.Config").getField("xpFluidId");
      openBlocksXPJuiceName = (String) getField.get(null);
    } catch (Exception e) {
    }

    if(openBlocksXPJuiceName != null && !Config.xpJuiceName.equals(openBlocksXPJuiceName)) {
      Log.info("Overwriting XP Juice name with '" + openBlocksXPJuiceName + "' taken from OpenBlocks' config");
      return openBlocksXPJuiceName;
    }

    return Config.xpJuiceName;
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
        batteries.add(new ItemStack(blockCapBank));
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
          } else if(IMC.SOUL_VIAL_BLACKLIST.equals(key) && itemSoulVessel != null) {
            itemSoulVessel.addEntityToBlackList(value);
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
