package crazypants.enderio;

import java.util.List;
import java.util.logging.Level;

import com.google.common.collect.Lists;

import net.minecraft.world.World;
import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.ForgeChunkManager.Ticket;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
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
import crazypants.enderio.conduit.liquid.ItemLiquidConduit;
import crazypants.enderio.conduit.power.ItemPowerConduit;
import crazypants.enderio.conduit.redstone.ItemRedstoneConduit;
import crazypants.enderio.enderface.BlockEnderIO;
import crazypants.enderio.enderface.EnderfaceRecipes;
import crazypants.enderio.enderface.ItemEnderface;
import crazypants.enderio.machine.BlockElectricLight;
import crazypants.enderio.machine.MachineRecipes;
import crazypants.enderio.machine.alloy.BlockAlloySmelter;
import crazypants.enderio.machine.generator.BlockStirlingGenerator;
import crazypants.enderio.machine.painter.BlockCustomFence;
import crazypants.enderio.machine.painter.BlockCustomFenceGate;
import crazypants.enderio.machine.painter.BlockCustomStair;
import crazypants.enderio.machine.painter.BlockCustomWall;
import crazypants.enderio.machine.painter.BlockPainter;
import crazypants.enderio.machine.reservoir.BlockReservoir;
import crazypants.enderio.machine.solor.BlockSolarPanel;
import crazypants.enderio.material.BlockFusedQuartz;
import crazypants.enderio.material.ItemAlloy;
import crazypants.enderio.material.ItemCapacitor;
import crazypants.enderio.material.ItemFusedQuartzFrame;
import crazypants.enderio.material.ItemIndustrialBinder;
import crazypants.enderio.material.ItemYetaWrench;
import crazypants.enderio.material.MaterialRecipes;

@Mod(name = "EnderIO", modid = "EnderIO", version = "0.1.4", dependencies = "required-after:Forge@[9.10.0.800,)")
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
  public static BlockElectricLight blockElectricLight;
  public static BlockReservoir blockReservoir;
  public static BlockAlloySmelter blockAlloySmelter;

  public static ItemYetaWrench itemYetaWench;

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

    blockStirlingGenerator = BlockStirlingGenerator.create();
    blockSolarPanel = BlockSolarPanel.create();
    blockReservoir = BlockReservoir.create();
    blockAlloySmelter = BlockAlloySmelter.create();

    blockConduitBundle = BlockConduitBundle.create();
    blockConduitFacade = BlockConduitFacade.create();
    itemConduitFacade = ItemConduitFacade.create();

    itemRedstoneConduit = ItemRedstoneConduit.create();
    itemPowerConduit = ItemPowerConduit.create();
    itemLiquidConduit = ItemLiquidConduit.create();

    // blockElectricLight = BlockElectricLight.create();

    itemYetaWench = ItemYetaWrench.create();
  }

  @EventHandler
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

  @EventHandler
  public void postInit(FMLPostInitializationEvent event) {
    TickRegistry.registerTickHandler(new ClientTickHandler(), Side.CLIENT);
  }

  @EventHandler
  public void serverStopped(FMLServerStoppedEvent event) {
    proxy.serverStopped();
  }

}
