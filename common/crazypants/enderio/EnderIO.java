package crazypants.enderio;

import java.util.logging.Level;

import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.MinecraftForge;
import cpw.mods.fml.common.FMLLog;
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
import crazypants.enderio.machine.MachineRecipes;
import crazypants.enderio.machine.RedstoneModePacketProcessor;
import crazypants.enderio.machine.alloy.BlockAlloySmelter;
import crazypants.enderio.machine.crusher.BlockCrusher;
import crazypants.enderio.machine.crusher.CrusherRecipeManager;
import crazypants.enderio.machine.generator.BlockStirlingGenerator;
import crazypants.enderio.machine.hypercube.BlockHyperCube;
import crazypants.enderio.machine.hypercube.HyperCubeRegister;
import crazypants.enderio.machine.light.BlockElectricLight;
import crazypants.enderio.machine.light.BlockLightNode;
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
import crazypants.enderio.material.ItemIndustrialBinder;
import crazypants.enderio.material.ItemMJReader;
import crazypants.enderio.material.ItemMachinePart;
import crazypants.enderio.material.ItemPowderIngot;
import crazypants.enderio.material.ItemYetaWrench;
import crazypants.enderio.material.MaterialRecipes;

@Mod(name = "EnderIO", modid = "EnderIO", version = "0.1.25", dependencies = "required-after:Forge@[9.10.0.800,)")
@NetworkMod(clientSideRequired = true, serverSideRequired = true, channels = { "EnderIO" }, packetHandler = PacketHandler.class)
public class EnderIO {

  @Instance("EnderIO")
  public static EnderIO instance;

  @SidedProxy(clientSide = "crazypants.enderio.ClientProxy", serverSide = "crazypants.enderio.CommonProxy")
  public static CommonProxy proxy;

  public static GuiHandler guiHandler = new GuiHandler();

  // Materials
  public static ItemIndustrialBinder itemIndustrialBinder;
  public static ItemCapacitor itemBasicCapacitor;
  public static ItemAlloy itemAlloy;
  public static BlockFusedQuartz blockFusedQuartz;
  public static ItemFusedQuartzFrame itemFusedQuartzFrame;
  public static ItemMachinePart itemMachinePart;
  public static ItemPowderIngot itemPowderIngot;

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

  public static BlockElectricLight blockElectricLight;
  public static BlockLightNode blockLightNode;

  public static ItemYetaWrench itemYetaWench;
  public static ItemMJReader itemMJReader;
  
  @EventHandler
  public void preInit(FMLPreInitializationEvent event) {
    
    Configuration cfg = new Configuration(event.getSuggestedConfigurationFile());
    try {      
      cfg.load();
      Config.load(cfg);
    } catch (Exception e) {
      FMLLog.log(Level.SEVERE, e, "EnderIO has a problem loading it's configuration");
    } finally {
      if (cfg.hasChanged()) {
        cfg.save();
      }
    }

    ConduitGeometryUtil.setupBounds((float) Config.conduitScale);

    itemIndustrialBinder = ItemIndustrialBinder.create();
    itemBasicCapacitor = ItemCapacitor.create();
    itemAlloy = ItemAlloy.create();
    blockFusedQuartz = BlockFusedQuartz.create();
    itemFusedQuartzFrame = ItemFusedQuartzFrame.create();    
    itemMachinePart = ItemMachinePart.create();
    itemPowderIngot = ItemPowderIngot.create();
    
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
  }

  @EventHandler
  public void load(FMLInitializationEvent event) {
    
    instance = this;

    NetworkRegistry.instance().registerGuiHandler(this, guiHandler);
    MinecraftForge.EVENT_BUS.register(this);
    
    PacketHandler.instance.addPacketProcessor(new RedstoneModePacketProcessor());
    
    CrusherRecipeManager.addRecipes();
    EnderfaceRecipes.addRecipes();
    MaterialRecipes.addRecipes();
    ConduitRecipes.addRecipes();
    MachineRecipes.addRecipes();

    proxy.load();
  }

// This is AEs way of registering new grindables.  
//  @EventHandler
//  public void processIMC(FMLInterModComms.IMCEvent event) {
//    System.out.println("EnderIO.processIMC: !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
//    Splitter splitter = Splitter.on(",").trimResults();
//    for (FMLInterModComms.IMCMessage m : event.getMessages()) {
//      String[] array = (String[]) Iterables.toArray(splitter.split(m.getStringValue()), String.class);
//      Integer localInteger1;
//      Integer localInteger2;
//      if (m.key.equals("add-grindable")) {
//        System.out.println("EnderIO.processIMC: Got a grindable!!");
//        if (array.length != 5) {
//          FMLLog.warning("IMC failed - add-grindable expects itemid,meta,itemid,meta,effort", new Object[0]);
//        } else {
//          Integer inId = Ints.tryParse(array[0]);
//          Integer inMeta = Ints.tryParse(array[1]);
//          Integer outId = Ints.tryParse(array[2]);
//          Integer outMeta = Ints.tryParse(array[3]);
//          Integer effort = Ints.tryParse(array[4]);
//          if ((inId == null) || (inMeta == null) || (outId == null) || (outMeta == null) || (effort == null)) {
//            FMLLog.warning("IMC failed - add-grindable expects itemid,meta,itemid,meta,effort", new Object[0]);
//          } else {
//            ItemStack i = new ItemStack(inId.intValue(), 1, inMeta.intValue());
//            ItemStack o = new ItemStack(outId.intValue(), 1, outMeta.intValue());
//            System.out.println("EnderIO.processIMC: And could make a recipe out of it!!");
//          }
//        }
//      } else {
//        FMLLog.warning("IMC failed - " + m.key + " - is not a valid ICM for AE.", new Object[0]);
//      }
//    }    
//    System.out.println("EnderIO.processIMC: !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
//  }

  @EventHandler
  public void postInit(FMLPostInitializationEvent event) {  
  }
 
  
  @EventHandler
  public void serverStarted(FMLServerStartedEvent event) {
    System.out.println("EnderIO.serverStarted: !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
    HyperCubeRegister.load();
  }
  
  @EventHandler
  public void serverStopped(FMLServerStoppedEvent event) {
    System.out.println("EnderIO.serverStarted: !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
    HyperCubeRegister.unload();
  }
  


}
