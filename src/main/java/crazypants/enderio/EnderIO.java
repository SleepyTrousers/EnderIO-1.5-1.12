package crazypants.enderio;

import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.MaterialLiquid;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.WeightedRandomChestContent;
import net.minecraftforge.common.ChestGenHooks;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import buildcraft.api.fuels.IronEngineCoolant;
import buildcraft.api.fuels.IronEngineFuel;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLInterModComms;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartedEvent;
import cpw.mods.fml.common.event.FMLServerStoppedEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import crazypants.enderio.conduit.BlockConduitBundle;
import crazypants.enderio.conduit.ConduitRecipes;
import crazypants.enderio.conduit.facade.BlockConduitFacade;
import crazypants.enderio.conduit.facade.ItemConduitFacade;
import crazypants.enderio.conduit.geom.ConduitGeometryUtil;
import crazypants.enderio.conduit.item.ItemItemConduit;
import crazypants.enderio.conduit.liquid.ItemLiquidConduit;
import crazypants.enderio.conduit.power.ItemPowerConduit;
import crazypants.enderio.conduit.redstone.ItemRedstoneConduit;
import crazypants.enderio.enderface.BlockEnderIO;
import crazypants.enderio.enderface.EnderfaceRecipes;
import crazypants.enderio.enderface.ItemEnderface;
import crazypants.enderio.fluid.BlockFluidEio;
import crazypants.enderio.fluid.Fluids;
import crazypants.enderio.fluid.ItemBucketEio;
import crazypants.enderio.item.ItemMagnet;
import crazypants.enderio.item.ItemRecipes;
import crazypants.enderio.item.ItemYetaWrench;
import crazypants.enderio.item.darksteel.ItemDarkSteelArmor;
import crazypants.enderio.item.darksteel.ItemDarkSteelPickaxe;
import crazypants.enderio.item.darksteel.ItemDarkSteelSword;
import crazypants.enderio.machine.MachineRecipes;
import crazypants.enderio.machine.PacketRedstoneMode;
import crazypants.enderio.machine.alloy.AlloyRecipeManager;
import crazypants.enderio.machine.alloy.BlockAlloySmelter;
import crazypants.enderio.machine.crusher.BlockCrusher;
import crazypants.enderio.machine.crusher.CrusherRecipeManager;
import crazypants.enderio.machine.farm.BlockFarmStation;
import crazypants.enderio.machine.farm.FarmersRegistry;
import crazypants.enderio.machine.generator.BlockCombustionGenerator;
import crazypants.enderio.machine.generator.BlockStirlingGenerator;
import crazypants.enderio.machine.hypercube.BlockHyperCube;
import crazypants.enderio.machine.hypercube.HyperCubeRegister;
import crazypants.enderio.machine.light.BlockElectricLight;
import crazypants.enderio.machine.light.BlockLightNode;
import crazypants.enderio.machine.monitor.BlockPowerMonitor;
import crazypants.enderio.machine.monitor.ItemConduitProbe;
import crazypants.enderio.machine.painter.BlockPaintedFence;
import crazypants.enderio.machine.painter.BlockPaintedFenceGate;
import crazypants.enderio.machine.painter.BlockPaintedSlab;
import crazypants.enderio.machine.painter.BlockPaintedStair;
import crazypants.enderio.machine.painter.BlockPaintedWall;
import crazypants.enderio.machine.painter.BlockPainter;
import crazypants.enderio.machine.power.BlockCapacitorBank;
import crazypants.enderio.machine.reservoir.BlockReservoir;
import crazypants.enderio.machine.solar.BlockSolarPanel;
import crazypants.enderio.machine.still.BlockVat;
import crazypants.enderio.machine.still.VatRecipeManager;
import crazypants.enderio.machine.tank.BlockTank;
import crazypants.enderio.material.Alloy;
import crazypants.enderio.material.BlockFusedQuartz;
import crazypants.enderio.material.ItemAlloy;
import crazypants.enderio.material.ItemCapacitor;
import crazypants.enderio.material.ItemFusedQuartzFrame;
import crazypants.enderio.material.ItemMachinePart;
import crazypants.enderio.material.ItemMaterial;
import crazypants.enderio.material.ItemPowderIngot;
import crazypants.enderio.material.MaterialRecipes;
import crazypants.enderio.network.PacketPipeline;
import crazypants.enderio.network.PacketTileEntityNbt;
import crazypants.enderio.teleport.BlockTravelAnchor;
import crazypants.enderio.teleport.ItemTravelStaff;
import crazypants.enderio.teleport.TeleportRecipes;

@Mod(modid = "EnderIO", name = "Ender IO", version = "1.1.0_alpha", dependencies = "required-after:Forge@[10.12.1.1060,);required-after:FML@[5.0.5,)")
public class EnderIO {

  @Instance("EnderIO")
  public static EnderIO instance;

  @SidedProxy(clientSide = "crazypants.enderio.ClientProxy", serverSide = "crazypants.enderio.CommonProxy")
  public static CommonProxy proxy;

  public static final PacketPipeline packetPipeline = new PacketPipeline();

  public static GuiHandler guiHandler = new GuiHandler();

  // Materials
  public static ItemCapacitor itemBasicCapacitor;
  public static ItemAlloy itemAlloy;
  public static BlockFusedQuartz blockFusedQuartz;
  public static ItemFusedQuartzFrame itemFusedQuartzFrame;
  public static ItemMachinePart itemMachinePart;
  public static ItemPowderIngot itemPowderIngot;
  public static ItemMaterial itemMaterial;

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

  // Conduits
  public static BlockConduitBundle blockConduitBundle;
  public static BlockConduitFacade blockConduitFacade;
  public static ItemConduitFacade itemConduitFacade;
  public static ItemRedstoneConduit itemRedstoneConduit;
  public static ItemPowerConduit itemPowerConduit;
  public static ItemLiquidConduit itemLiquidConduit;
  public static ItemItemConduit itemItemConduit;

  // Machines
  public static BlockStirlingGenerator blockStirlingGenerator;
  public static BlockCombustionGenerator blockCombustionGenerator;
  public static BlockSolarPanel blockSolarPanel;
  public static BlockReservoir blockReservoir;
  public static BlockAlloySmelter blockAlloySmelter;
  public static BlockCapacitorBank blockCapacitorBank;
  public static BlockCrusher blockCrusher;
  public static BlockHyperCube blockHyperCube;
  public static BlockPowerMonitor blockPowerMonitor;
  public static BlockVat blockVat;
  public static BlockFarmStation blockFarmStation;
  public static BlockTank blockTank;

  public static BlockElectricLight blockElectricLight;
  public static BlockLightNode blockLightNode;

  //Fluids
  public static Fluid fluidNutrientDistillation;
  public static BlockFluidEio blockNutrientDistillation;
  public static ItemBucketEio itemBucketNutrientDistillation;

  public static Fluid fluidHootch;
  public static BlockFluidEio blocHootch;
  public static ItemBucketEio itemBucketHootch;

  public static Fluid fluidRocketFuel;
  public static BlockFluidEio blockRocketFuel;
  public static ItemBucketEio itemBucketRocketFuel;

  public static Fluid fluidFireWater;
  public static BlockFluidEio blockFireWater;
  public static ItemBucketEio itemBucketFireWater;

  // Items
  public static ItemYetaWrench itemYetaWench;
  public static ItemConduitProbe itemConduitProbe;
  public static ItemMagnet itemMagnet;

  public static ItemDarkSteelArmor itemDarkSteelHelmet;
  public static ItemDarkSteelArmor itemDarkSteelChestplate;
  public static ItemDarkSteelArmor itemDarkSteelLeggings;
  public static ItemDarkSteelArmor itemDarkSteelBoots;
  public static ItemDarkSteelSword itemDarkSteelSword;
  public static ItemDarkSteelPickaxe itemDarkSteelPickaxe;

  

  //  public static ITrigger triggerNoEnergy;
  //  public static ITrigger triggerHasEnergy;
  //  public static ITrigger triggerFullEnergy;
  //  public static ITrigger triggerIsCharging;
  //  public static ITrigger triggerFinishedCharging;

  @EventHandler
  public void preInit(FMLPreInitializationEvent event) {

    Config.load(event);

    ConduitGeometryUtil.setupBounds((float) Config.conduitScale);

    blockEnderIo = BlockEnderIO.create();

    blockTravelPlatform = BlockTravelAnchor.create();

    blockSolarPanel = BlockSolarPanel.create();
    blockStirlingGenerator = BlockStirlingGenerator.create();
    blockCombustionGenerator = BlockCombustionGenerator.create();

    blockCrusher = BlockCrusher.create();
    blockAlloySmelter = BlockAlloySmelter.create();
    blockVat = BlockVat.create();
    blockPowerMonitor = BlockPowerMonitor.create();
    blockFarmStation = BlockFarmStation.create();
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

    blockHyperCube = BlockHyperCube.create();
    blockElectricLight = BlockElectricLight.create();
    blockLightNode = BlockLightNode.create();
    blockTank = BlockTank.create();
    blockReservoir = BlockReservoir.create();

    blockFusedQuartz = BlockFusedQuartz.create();
    itemFusedQuartzFrame = ItemFusedQuartzFrame.create();

    blockConduitBundle = BlockConduitBundle.create();
    blockConduitFacade = BlockConduitFacade.create();
    itemConduitFacade = ItemConduitFacade.create();

    itemRedstoneConduit = ItemRedstoneConduit.create();
    itemPowerConduit = ItemPowerConduit.create();
    itemLiquidConduit = ItemLiquidConduit.create();
    itemItemConduit = ItemItemConduit.create();

    Fluid f = new Fluid(Fluids.NUTRIENT_DISTILLATION_NAME).setDensity(1500).setViscosity(3000);
    FluidRegistry.registerFluid(f);
    fluidNutrientDistillation = FluidRegistry.getFluid(f.getName());
    blockNutrientDistillation = BlockFluidEio.create(fluidNutrientDistillation, new MaterialLiquid(MapColor.brownColor));


    f = new Fluid(Fluids.HOOTCH_NAME).setDensity(900).setViscosity(1000);
    FluidRegistry.registerFluid(f);
    fluidHootch = FluidRegistry.getFluid(f.getName());
    blocHootch = BlockFluidEio.create(fluidHootch, new MaterialLiquid(MapColor.grayColor));
    IronEngineFuel.addFuel(Fluids.HOOTCH_NAME, Config.hootchPowerPerCycle, Config.hootchPowerTotalBurnTime);
    FMLInterModComms.sendMessage("Railcraft", "boiler-fuel-liquid", Fluids.HOOTCH_NAME + "@" + (Config.hootchPowerPerCycle * Config.hootchPowerTotalBurnTime));


    f = new Fluid(Fluids.ROCKET_FUEL_NAME).setDensity(900).setViscosity(1000);
    FluidRegistry.registerFluid(f);
    fluidRocketFuel = FluidRegistry.getFluid(f.getName());
    blockRocketFuel = BlockFluidEio.create(fluidRocketFuel, new MaterialLiquid(MapColor.grayColor));
    IronEngineFuel.addFuel(Fluids.ROCKET_FUEL_NAME, Config.rocketFuelPowerPerCycle, Config.rocketFuelPowerTotalBurnTime);
    FMLInterModComms.sendMessage("Railcraft", "boiler-fuel-liquid", Fluids.ROCKET_FUEL_NAME + "@" + (Config.rocketFuelPowerPerCycle * Config.rocketFuelPowerTotalBurnTime));


    f = new Fluid(Fluids.FIRE_WATER_NAME).setDensity(900).setViscosity(1000);
    FluidRegistry.registerFluid(f);
    fluidFireWater = FluidRegistry.getFluid(f.getName());
    blockFireWater = BlockFluidEio.create(fluidFireWater, new MaterialLiquid(MapColor.grayColor));
    IronEngineFuel.addFuel(Fluids.FIRE_WATER_NAME, Config.fireWaterPowerPerCycle, Config.fireWaterPowerTotalBurnTime);
    FMLInterModComms.sendMessage("Railcraft", "boiler-fuel-liquid", Fluids.FIRE_WATER_NAME + "@" + (Config.fireWaterPowerPerCycle * Config.fireWaterPowerTotalBurnTime));

    if(!IronEngineCoolant.isCoolant(FluidRegistry.getFluid("water"))) {
      IronEngineCoolant.addCoolant(FluidRegistry.getFluid("water"), 0.0023F);
    }


    itemBasicCapacitor = ItemCapacitor.create();
    itemMachinePart = ItemMachinePart.create();
    itemMaterial = ItemMaterial.create();
    itemAlloy = ItemAlloy.create();
    itemPowderIngot = ItemPowderIngot.create();

    itemBucketNutrientDistillation = ItemBucketEio.create(fluidNutrientDistillation);
    itemBucketHootch = ItemBucketEio.create(fluidHootch);
    itemBucketRocketFuel = ItemBucketEio.create(fluidRocketFuel);
    itemBucketFireWater = ItemBucketEio.create(fluidFireWater);

    itemYetaWench = ItemYetaWrench.create();
    itemEnderface = ItemEnderface.create();
    itemTravelStaff = ItemTravelStaff.create();
    itemConduitProbe = ItemConduitProbe.create();
    
    itemMagnet = ItemMagnet.create();

    itemDarkSteelHelmet = ItemDarkSteelArmor.create(0);
    itemDarkSteelChestplate = ItemDarkSteelArmor.create(1);
    itemDarkSteelLeggings = ItemDarkSteelArmor.create(2);
    itemDarkSteelBoots = ItemDarkSteelArmor.create(3);

    itemDarkSteelSword = ItemDarkSteelSword.create();
    itemDarkSteelPickaxe = ItemDarkSteelPickaxe.create();
    
    

    MaterialRecipes.registerOresInDictionary();
  }

  @EventHandler
  public void load(FMLInitializationEvent event) {

    instance = this;

    packetPipeline.initalise();
    packetPipeline.registerPacket(PacketTileEntityNbt.class);
    packetPipeline.registerPacket(PacketRedstoneMode.class);

    NetworkRegistry.INSTANCE.registerGuiHandler(this, guiHandler);
    MinecraftForge.EVENT_BUS.register(this);

    //Register Custom Dungeon Loot here
    ChestGenHooks.getInfo(ChestGenHooks.DUNGEON_CHEST).addItem(
        new WeightedRandomChestContent(new ItemStack(EnderIO.itemAlloy, 1, Alloy.DARK_STEEL.ordinal()), 1, 3, 30));
    ChestGenHooks.getInfo(ChestGenHooks.DUNGEON_CHEST)
    .addItem(new WeightedRandomChestContent(new ItemStack(EnderIO.itemYetaWench, 1, 0), 1, 1, 15));
    ChestGenHooks.getInfo(ChestGenHooks.DUNGEON_CHEST).addItem(new WeightedRandomChestContent(new ItemStack(EnderIO.itemConduitProbe, 1, 0), 1, 1, 10));

    ChestGenHooks.getInfo(ChestGenHooks.DUNGEON_CHEST).addItem(new WeightedRandomChestContent(new ItemStack(Items.quartz), 3, 16, 40));
    ChestGenHooks.getInfo(ChestGenHooks.DUNGEON_CHEST).addItem(new WeightedRandomChestContent(new ItemStack(Items.nether_wart), 1, 4, 30));
    ChestGenHooks.getInfo(ChestGenHooks.DUNGEON_CHEST).addItem(new WeightedRandomChestContent(new ItemStack(Items.ender_pearl), 1, 2, 30));

    ChestGenHooks.getInfo(ChestGenHooks.VILLAGE_BLACKSMITH).addItem(
        new WeightedRandomChestContent(new ItemStack(EnderIO.itemAlloy, 1, Alloy.ELECTRICAL_STEEL.ordinal()), 2, 6, 40));
    ChestGenHooks.getInfo(ChestGenHooks.VILLAGE_BLACKSMITH).addItem(
        new WeightedRandomChestContent(new ItemStack(EnderIO.itemAlloy, 1, Alloy.REDSTONE_ALLOY.ordinal()), 3, 6, 35));
    ChestGenHooks.getInfo(ChestGenHooks.VILLAGE_BLACKSMITH).addItem(
        new WeightedRandomChestContent(new ItemStack(EnderIO.itemAlloy, 1, Alloy.DARK_STEEL.ordinal()), 3, 6, 35));
    ChestGenHooks.getInfo(ChestGenHooks.VILLAGE_BLACKSMITH).addItem(
        new WeightedRandomChestContent(new ItemStack(EnderIO.itemAlloy, 1, Alloy.PHASED_IRON.ordinal()), 1, 2, 10));
    ChestGenHooks.getInfo(ChestGenHooks.VILLAGE_BLACKSMITH).addItem(
        new WeightedRandomChestContent(new ItemStack(EnderIO.itemAlloy, 1, Alloy.PHASED_GOLD.ordinal()), 1, 2, 5));

    ItemStack staff = new ItemStack(EnderIO.itemTravelStaff, 1, 0);
    ChestGenHooks.getInfo(ChestGenHooks.VILLAGE_BLACKSMITH).addItem(new WeightedRandomChestContent(staff, 1, 1, 5));
    ChestGenHooks.getInfo(ChestGenHooks.PYRAMID_DESERT_CHEST).addItem(new WeightedRandomChestContent(staff, 1, 1, 5));
    ChestGenHooks.getInfo(ChestGenHooks.PYRAMID_JUNGLE_CHEST).addItem(new WeightedRandomChestContent(staff, 1, 1, 5));

    ItemStack sword = new ItemStack(EnderIO.itemDarkSteelSword, 1, 0);
    ChestGenHooks.getInfo(ChestGenHooks.DUNGEON_CHEST).addItem(new WeightedRandomChestContent(sword, 1, 1, 5));
    ChestGenHooks.getInfo(ChestGenHooks.VILLAGE_BLACKSMITH).addItem(new WeightedRandomChestContent(sword, 1, 1, 10));
    ChestGenHooks.getInfo(ChestGenHooks.PYRAMID_DESERT_CHEST).addItem(new WeightedRandomChestContent(sword, 1, 1, 5));
    ChestGenHooks.getInfo(ChestGenHooks.PYRAMID_JUNGLE_CHEST).addItem(new WeightedRandomChestContent(sword, 1, 1, 5));

    ItemStack boots = new ItemStack(EnderIO.itemDarkSteelBoots, 1, 0);
    ChestGenHooks.getInfo(ChestGenHooks.DUNGEON_CHEST).addItem(new WeightedRandomChestContent(boots, 1, 1, 5));
    ChestGenHooks.getInfo(ChestGenHooks.VILLAGE_BLACKSMITH).addItem(new WeightedRandomChestContent(boots, 1, 1, 5));

    EnderfaceRecipes.addRecipes();
    MaterialRecipes.addRecipes();
    ConduitRecipes.addRecipes();
    MachineRecipes.addRecipes();
    ItemRecipes.addRecipes();
    TeleportRecipes.addRecipes();

    //    triggerNoEnergy = new TriggerEnderIO("enderIO.trigger.noEnergy", 0);
    //    triggerHasEnergy = new TriggerEnderIO("enderIO.trigger.hasEnergy", 1);
    //    triggerFullEnergy = new TriggerEnderIO("enderIO.trigger.fullEnergy", 2);
    //    triggerIsCharging = new TriggerEnderIO("enderIO.trigger.isCharging", 3);
    //    triggerFinishedCharging = new TriggerEnderIO("enderIO.trigger.finishedCharging", 4);
    //    ActionManager.registerTriggerProvider(new TriggerProviderEIO());

    proxy.load();

  }

  @EventHandler
  public void postInit(FMLPostInitializationEvent event) {

    packetPipeline.postInitialise();
    MaterialRecipes.addOreDictionaryRecipes();
    MachineRecipes.addOreDictionaryRecipes();
    ItemRecipes.addOreDictionaryRecipes();

    CrusherRecipeManager.getInstance().loadRecipesFromConfig();
    AlloyRecipeManager.getInstance().loadRecipesFromConfig();
    VatRecipeManager.getInstance().loadRecipesFromConfig();
    FarmersRegistry.addFarmers();
  }

  @EventHandler
  public void serverStarted(FMLServerStartedEvent event) {
    HyperCubeRegister.load();
  }

  @EventHandler
  public void serverStopped(FMLServerStoppedEvent event) {
    HyperCubeRegister.unload();
  }
}
