package crazypants.enderio;

import net.minecraftforge.common.MinecraftForge;
import buildcraft.api.gates.ActionManager;
import buildcraft.api.gates.ITrigger;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartedEvent;
import cpw.mods.fml.common.event.FMLServerStoppedEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.network.NetworkRegistry;
import crazypants.enderio.conduit.BlockConduitBundle;
import crazypants.enderio.conduit.ConduitRecipes;
import crazypants.enderio.conduit.facade.BlockConduitFacade;
import crazypants.enderio.conduit.facade.ItemConduitFacade;
import crazypants.enderio.conduit.geom.ConduitGeometryUtil;
import crazypants.enderio.conduit.liquid.ItemLiquidConduit;
import crazypants.enderio.conduit.power.ItemPowerConduit;
import crazypants.enderio.conduit.redstone.ItemRedstoneConduit;
import crazypants.enderio.enderface.BlockEnderIO;
import crazypants.enderio.enderface.EnderfaceRecipes;
import crazypants.enderio.enderface.ItemEnderface;
import crazypants.enderio.machine.MachineRecipeRegistry;
import crazypants.enderio.machine.MachineRecipes;
import crazypants.enderio.machine.RedstoneModePacketProcessor;
import crazypants.enderio.machine.alloy.BlockAlloySmelter;
import crazypants.enderio.machine.crusher.BlockCrusher;
import crazypants.enderio.machine.crusher.CrusherMachineRecipe;
import crazypants.enderio.machine.crusher.CrusherRecipeManager;
import crazypants.enderio.machine.generator.BlockStirlingGenerator;
import crazypants.enderio.machine.hypercube.BlockHyperCube;
import crazypants.enderio.machine.hypercube.HyperCubeRegister;
import crazypants.enderio.machine.light.BlockElectricLight;
import crazypants.enderio.machine.light.BlockLightNode;
import crazypants.enderio.machine.monitor.BlockPowerMonitor;
import crazypants.enderio.machine.monitor.ItemMJReader;
import crazypants.enderio.machine.painter.BlockCustomFence;
import crazypants.enderio.machine.painter.BlockCustomFenceGate;
import crazypants.enderio.machine.painter.BlockCustomSlab;
import crazypants.enderio.machine.painter.BlockCustomStair;
import crazypants.enderio.machine.painter.BlockCustomWall;
import crazypants.enderio.machine.painter.BlockPainter;
import crazypants.enderio.machine.power.BlockCapacitorBank;
import crazypants.enderio.machine.reservoir.BlockReservoir;
import crazypants.enderio.machine.solar.BlockSolarPanel;
import crazypants.enderio.material.BlockFusedQuartz;
import crazypants.enderio.material.ItemAlloy;
import crazypants.enderio.material.ItemCapacitor;
import crazypants.enderio.material.ItemFusedQuartzFrame;
import crazypants.enderio.material.ItemMachinePart;
import crazypants.enderio.material.ItemMaterial;
import crazypants.enderio.material.ItemPowderIngot;
import crazypants.enderio.material.ItemYetaWrench;
import crazypants.enderio.material.MaterialRecipes;
import crazypants.enderio.trigger.TriggerEnderIO;
import crazypants.enderio.trigger.TriggerProviderEIO;

@Mod(name = "EnderIO", modid = "EnderIO", version = "0.4.0", dependencies = "required-after:Forge@[9.11.0.883,)")
@NetworkMod(clientSideRequired = true, serverSideRequired = true, channels = { "EnderIO" }, packetHandler = PacketHandler.class)
public class EnderIO {

  @Instance("EnderIO")
  public static EnderIO instance;

  @SidedProxy(clientSide = "crazypants.enderio.ClientProxy", serverSide = "crazypants.enderio.CommonProxy")
  public static CommonProxy proxy;

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

  // Painter
  public static BlockPainter blockPainter;
  public static BlockCustomFence blockCustomFence;
  public static BlockCustomFenceGate blockCustomFenceGate;
  public static BlockCustomWall blockCustomWall;
  public static BlockCustomStair blockCustomStair;
  public static BlockCustomSlab blockCustomSlab;
  public static BlockCustomSlab blockCustomDoubleSlab;

  // Conduits
  public static BlockConduitBundle blockConduitBundle;
  public static BlockConduitFacade blockConduitFacade;
  public static ItemConduitFacade itemConduitFacade;
  public static ItemRedstoneConduit itemRedstoneConduit;
  public static ItemPowerConduit itemPowerConduit;
  public static ItemLiquidConduit itemLiquidConduit;

  // Machines
  public static BlockStirlingGenerator blockStirlingGenerator;
  public static BlockSolarPanel blockSolarPanel;
  public static BlockReservoir blockReservoir;
  public static BlockAlloySmelter blockAlloySmelter;
  public static BlockCapacitorBank blockCapacitorBank;
  public static BlockCrusher blockCrusher;
  public static BlockHyperCube blockHyperCube;
  public static BlockPowerMonitor blockPowerMonitor;

  public static BlockElectricLight blockElectricLight;
  public static BlockLightNode blockLightNode;

  public static ItemYetaWrench itemYetaWench;
  public static ItemMJReader itemMJReader;

  public static ITrigger triggerNoEnergy;
  public static ITrigger triggerHasEnergy;
  public static ITrigger triggerFullEnergy;
  public static ITrigger triggerIsCharging;
  public static ITrigger triggerFinishedCharging;

  @EventHandler
  public void preInit(FMLPreInitializationEvent event) {

    Config.load(event);

    ConduitGeometryUtil.setupBounds((float) Config.conduitScale);

    itemBasicCapacitor = ItemCapacitor.create();
    itemAlloy = ItemAlloy.create();
    blockFusedQuartz = BlockFusedQuartz.create();
    itemFusedQuartzFrame = ItemFusedQuartzFrame.create();
    itemMachinePart = ItemMachinePart.create();
    itemPowderIngot = ItemPowderIngot.create();
    itemMaterial = ItemMaterial.create();

    blockEnderIo = BlockEnderIO.create();
    itemEnderface = ItemEnderface.create();

    blockHyperCube = BlockHyperCube.create();

    blockPainter = BlockPainter.create();
    blockCustomFence = BlockCustomFence.create();
    blockCustomFenceGate = BlockCustomFenceGate.create();
    blockCustomWall = BlockCustomWall.create();
    blockCustomStair = BlockCustomStair.create();
    blockCustomSlab = new BlockCustomSlab(false);
    blockCustomDoubleSlab = new BlockCustomSlab(true);
    blockCustomSlab.init();
    blockCustomDoubleSlab.init();

    blockStirlingGenerator = BlockStirlingGenerator.create();
    blockSolarPanel = BlockSolarPanel.create();
    blockReservoir = BlockReservoir.create();
    blockAlloySmelter = BlockAlloySmelter.create();
    blockCapacitorBank = BlockCapacitorBank.create();
    blockCrusher = BlockCrusher.create();
    blockPowerMonitor = BlockPowerMonitor.create();

    blockConduitBundle = BlockConduitBundle.create();
    blockConduitFacade = BlockConduitFacade.create();
    itemConduitFacade = ItemConduitFacade.create();

    itemRedstoneConduit = ItemRedstoneConduit.create();
    itemPowerConduit = ItemPowerConduit.create();
    itemLiquidConduit = ItemLiquidConduit.create();

    blockElectricLight = BlockElectricLight.create();
    blockLightNode = BlockLightNode.create();

    itemYetaWench = ItemYetaWrench.create();
    itemMJReader = ItemMJReader.create();

    MaterialRecipes.registerOresInDictionary();
  }

  @EventHandler
  public void load(FMLInitializationEvent event) {

    instance = this;

    NetworkRegistry.instance().registerGuiHandler(this, guiHandler);
    MinecraftForge.EVENT_BUS.register(this);

    PacketHandler.instance.addPacketProcessor(new RedstoneModePacketProcessor());

    EnderfaceRecipes.addRecipes();
    MaterialRecipes.addRecipes();
    ConduitRecipes.addRecipes();
    MachineRecipes.addRecipes();

    triggerNoEnergy = new TriggerEnderIO("enderIO.trigger.noEnergy", 0);
    triggerHasEnergy = new TriggerEnderIO("enderIO.trigger.hasEnergy", 1);
    triggerFullEnergy = new TriggerEnderIO("enderIO.trigger.fullEnergy", 2);
    triggerIsCharging = new TriggerEnderIO("enderIO.trigger.isCharging", 3);
    triggerFinishedCharging = new TriggerEnderIO("enderIO.trigger.finishedCharging", 4);

    ActionManager.registerTriggerProvider(new TriggerProviderEIO());

    proxy.load();
  }

  @EventHandler
  public void postInit(FMLPostInitializationEvent event) {

    CrusherRecipeManager.getInstance().loadRecipesFromConfig();
    MachineRecipeRegistry.instance.registerRecipe(ModObject.blockCrusher.unlocalisedName, new CrusherMachineRecipe());
    MaterialRecipes.addOreDictionaryRecipes();
    MachineRecipes.addOreDictionaryRecipes();
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
