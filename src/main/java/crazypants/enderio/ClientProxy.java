package crazypants.enderio;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.world.World;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.common.MinecraftForge;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.FMLCommonHandler;
import crazypants.enderio.enderface.EnderIoRenderer;
import crazypants.enderio.enderface.TileEnderIO;
import crazypants.enderio.machine.light.BlockElectricLight;
import crazypants.enderio.machine.light.ElectricLightRenderer;
import crazypants.enderio.machine.power.BlockCapacitorBank;
import crazypants.enderio.machine.power.CapBankRenderer2;
import crazypants.enderio.machine.power.CapacitorBankRenderer;
import crazypants.enderio.machine.reservoir.ReservoirRenderer;
import crazypants.enderio.machine.reservoir.TileReservoir;
import crazypants.enderio.material.BlockFusedQuartz;
import crazypants.enderio.material.FusedQuartzFrameRenderer;
import crazypants.enderio.material.FusedQuartzRenderer;
import crazypants.enderio.material.MachinePartRenderer;
import crazypants.enderio.teleport.TileTravelAnchor;
import crazypants.enderio.teleport.TravelController;
import crazypants.enderio.teleport.TravelEntitySpecialRenderer;

public class ClientProxy extends CommonProxy {

  // @formatter:off
  public static int[][] sideAndFacingToSpriteOffset = new int[][] {

      { 3, 2, 0, 0, 0, 0 },
      { 2, 3, 1, 1, 1, 1 },
      { 1, 1, 3, 2, 5, 4 },
      { 0, 0, 2, 3, 4, 5 },
      { 4, 5, 4, 5, 3, 2 },
      { 5, 4, 5, 4, 2, 3 } };
  // @formatter:on

  static {
    //    AbstractMachineBlock.initIcon();
    //    RedstoneConduit.initIcons();
    //    InsulatedRedstoneConduit.initIcons();
    //    RedstoneSwitch.initIcons();
    //    PowerConduit.initIcons();
    //    LiquidConduit.initIcons();
    //    AdvancedLiquidConduit.initIcons();
    //    ItemConduit.initIcons();
    //    MeConduit.initIcons();
  }

  //  private List<ConduitRenderer> conduitRenderers = new ArrayList<ConduitRenderer>();
  //
  //  private DefaultConduitRenderer dcr = new DefaultConduitRenderer();
  //
  //  private ConduitBundleRenderer cbr;

  @Override
  public World getClientWorld() {
    return FMLClientHandler.instance().getClient().theWorld;
  }

  @Override
  public EntityPlayer getClientPlayer() {
    return Minecraft.getMinecraft().thePlayer;
  }

  //  public ConduitBundleRenderer getConduitBundleRenderer() {
  //    return cbr;
  //  }
  //
  //  public void setCbr(ConduitBundleRenderer cbr) {
  //    this.cbr = cbr;
  //  }

  @Override
  public void load() {
    super.load();

    // Renderers

    //    BlockCustomFenceGateRenderer bcfgr = new BlockCustomFenceGateRenderer();
    //    BlockCustomFenceGate.renderId = RenderingRegistry.getNextAvailableRenderId();
    //    RenderingRegistry.registerBlockHandler(bcfgr);
    //
    BlockFusedQuartz.renderId = RenderingRegistry.getNextAvailableRenderId();
    RenderingRegistry.registerBlockHandler(new FusedQuartzRenderer());

    BlockElectricLight.renderId = RenderingRegistry.getNextAvailableRenderId();
    RenderingRegistry.registerBlockHandler(new ElectricLightRenderer());

    CapacitorBankRenderer capr = new CapacitorBankRenderer();
    MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(EnderIO.blockCapacitorBank), capr);

    BlockCapacitorBank.renderId = RenderingRegistry.getNextAvailableRenderId();
    CapBankRenderer2 cbr2 = new CapBankRenderer2();
    RenderingRegistry.registerBlockHandler(cbr2);
    //
    FusedQuartzFrameRenderer fqfr = new FusedQuartzFrameRenderer();
    MinecraftForgeClient.registerItemRenderer(EnderIO.itemFusedQuartzFrame, fqfr);
    //
    //    ItemConduitRenderer itemConRenderer = new ItemConduitRenderer();
    //    MinecraftForgeClient.registerItemRenderer(EnderIO.itemLiquidConduit, itemConRenderer);
    //    MinecraftForgeClient.registerItemRenderer(EnderIO.itemPowerConduit, itemConRenderer);
    //    MinecraftForgeClient.registerItemRenderer(EnderIO.itemRedstoneConduit, itemConRenderer);
    //    MinecraftForgeClient.registerItemRenderer(EnderIO.itemItemConduit, itemConRenderer);
    //    MinecraftForgeClient.registerItemRenderer(EnderIO.itemMeConduit, itemConRenderer);
    //
    //    PaintedItemRenderer pir = new PaintedItemRenderer();
    //    MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(EnderIO.blockCustomFence), pir);
    //    MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(EnderIO.blockCustomFenceGate), pir);
    //    MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(EnderIO.blockCustomWall), pir);
    //    MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(EnderIO.blockCustomStair), pir);

    //TODO:1.7
    //MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(EnderIO.blockCustomSlab), pir);

    MinecraftForgeClient.registerItemRenderer(EnderIO.itemMachinePart, new MachinePartRenderer());
    //    MinecraftForgeClient.registerItemRenderer(EnderIO.itemConduitFacade, new FacadeRenderer());
    //
    //    cbr = new ConduitBundleRenderer((float) Config.conduitScale);
    //    BlockConduitBundle.rendererId = RenderingRegistry.getNextAvailableRenderId();
    //    RenderingRegistry.registerBlockHandler(cbr);
    //    ClientRegistry.bindTileEntitySpecialRenderer(TileConduitBundle.class, cbr);

    ClientRegistry.bindTileEntitySpecialRenderer(TileTravelAnchor.class, new TravelEntitySpecialRenderer());

    //    conduitRenderers.add(RedstoneSwitchRenderer.getInstance());
    //    conduitRenderers.add(new AdvancedLiquidConduitRenderer());
    //    conduitRenderers.add(new LiquidConduitRenderer());
    //    conduitRenderers.add(new PowerConduitRenderer());
    //    conduitRenderers.add(new InsulatedRedstoneConduitRenderer());
    //    conduitRenderers.add(new crazypants.enderio.conduit.item.ItemConduitRenderer());

    EnderIoRenderer eior = new EnderIoRenderer();
    ClientRegistry.bindTileEntitySpecialRenderer(TileEnderIO.class, eior);
    MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(EnderIO.blockEnderIo), eior);

    ClientRegistry.bindTileEntitySpecialRenderer(TileReservoir.class, new ReservoirRenderer(EnderIO.blockReservoir));
    //
    //    HyperCubeRenderer hcr = new HyperCubeRenderer();
    //    ClientRegistry.bindTileEntitySpecialRenderer(TileHyperCube.class, hcr);
    //    MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(EnderIO.blockHyperCube), hcr);

    //    new YetaWrenchOverlayRenderer(EnderIO.itemYetaWench);
    //    // Tick handler
    //    if(Config.useSneakMouseWheelYetaWrench) {
    //      MinecraftForge.EVENT_BUS.register(new YetaWrenchTickHandler());
    //    }
    MinecraftForge.EVENT_BUS.register(TravelController.instance);
    FMLCommonHandler.instance().bus().register(TravelController.instance);

  }

  //  @Override
  //  public ConduitRenderer getRendererForConduit(IConduit conduit) {
  //    for (ConduitRenderer renderer : conduitRenderers) {
  //      if(renderer.isRendererForConduit(conduit)) {
  //        return renderer;
  //      }
  //    }
  //    return dcr;
  //  }

  @Override
  public double getReachDistanceForPlayer(EntityPlayer entityPlayer) {
    if(entityPlayer instanceof EntityPlayerMP) {
      return ((EntityPlayerMP) entityPlayer).theItemInWorldManager.getBlockReachDistance();
    }
    return super.getReachDistanceForPlayer(entityPlayer);
  }

}
