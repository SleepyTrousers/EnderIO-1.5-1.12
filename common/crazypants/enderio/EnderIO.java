package crazypants.enderio;

import java.util.logging.Level;

import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.MinecraftForge;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.Init;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.Mod.PostInit;
import cpw.mods.fml.common.Mod.PreInit;
import cpw.mods.fml.common.Mod.ServerStopped;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStoppedEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.TickRegistry;
import cpw.mods.fml.relauncher.Side;
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
import crazypants.enderio.machine.alloy.BlockAlloySmelter;
import crazypants.enderio.machine.generator.BlockStirlingGenerator;
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
import crazypants.enderio.material.ItemYetaWrench;
import crazypants.enderio.material.MaterialRecipes;

@Mod(name = "EnderIO", modid = "EnderIO", version = "0.1.16", dependencies = "required-after:Forge@[7.1,);required-after:FML@[5.0.5,)")
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

  public static BlockElectricLight blockElectricLight;
  public static BlockLightNode blockLightNode;

  public static ItemYetaWrench itemYetaWench;

  @PreInit
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

    ConduitGeometryUtil.setupBounds((float)Config.conduitScale);

    itemIndustrialBinder = ItemIndustrialBinder.create();
    itemBasicCapacitor = ItemCapacitor.create();
    itemAlloy = ItemAlloy.create();
    blockFusedQuartz = BlockFusedQuartz.create();
    itemFusedQuartzFrame = ItemFusedQuartzFrame.create();

    blockEnderIo = BlockEnderIO.create();
    itemEnderface = ItemEnderface.create();

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

    blockConduitBundle = BlockConduitBundle.create();
    blockConduitFacade = BlockConduitFacade.create();
    itemConduitFacade = ItemConduitFacade.create();

    itemRedstoneConduit = ItemRedstoneConduit.create();
    itemPowerConduit = ItemPowerConduit.create();
    itemLiquidConduit = ItemLiquidConduit.create();

    blockElectricLight = BlockElectricLight.create();
    blockLightNode = BlockLightNode.create();

    itemYetaWench = ItemYetaWrench.create();
  }

  @Init
  public void load(FMLInitializationEvent event) {

    instance = this;

    NetworkRegistry.instance().registerGuiHandler(this, guiHandler);
    MinecraftForge.EVENT_BUS.register(this);

    EnderfaceRecipes.addRecipes();
    MaterialRecipes.addRecipes();
    ConduitRecipes.addRecipes();
    MachineRecipes.addRecipes();

    proxy.load();
  }

  @PostInit
  public void postInit(FMLPostInitializationEvent event) {
    TickRegistry.registerTickHandler(new ClientTickHandler(), Side.CLIENT);
  }

  @ServerStopped
  public void serverStopped(FMLServerStoppedEvent event) {
    proxy.serverStopped();
  }

}
