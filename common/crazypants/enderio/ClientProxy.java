package crazypants.enderio;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.World;
import net.minecraftforge.client.MinecraftForgeClient;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.registry.TickRegistry;
import cpw.mods.fml.relauncher.Side;
import crazypants.enderio.conduit.BlockConduitBundle;
import crazypants.enderio.conduit.IConduit;
import crazypants.enderio.conduit.TileConduitBundle;
import crazypants.enderio.conduit.facade.FacadeRenderer;
import crazypants.enderio.conduit.item.ItemConduit;
import crazypants.enderio.conduit.liquid.LiquidConduit;
import crazypants.enderio.conduit.liquid.LiquidConduitRenderer;
import crazypants.enderio.conduit.power.PowerConduit;
import crazypants.enderio.conduit.power.PowerConduitRenderer;
import crazypants.enderio.conduit.redstone.InsulatedRedstoneConduit;
import crazypants.enderio.conduit.redstone.InsulatedRedstoneConduitRenderer;
import crazypants.enderio.conduit.redstone.RedstoneConduit;
import crazypants.enderio.conduit.redstone.RedstoneSwitch;
import crazypants.enderio.conduit.redstone.RedstoneSwitchRenderer;
import crazypants.enderio.conduit.render.ConduitBundleRenderer;
import crazypants.enderio.conduit.render.ConduitRenderer;
import crazypants.enderio.conduit.render.DefaultConduitRenderer;
import crazypants.enderio.conduit.render.ItemConduitRenderer;
import crazypants.enderio.enderface.EnderIoRenderer;
import crazypants.enderio.enderface.TileEnderIO;
import crazypants.enderio.item.YetaWrenchOverlayRenderer;
import crazypants.enderio.item.YetaWrenchTickHandler;
import crazypants.enderio.machine.AbstractMachineBlock;
import crazypants.enderio.machine.hypercube.HyperCubeRenderer;
import crazypants.enderio.machine.hypercube.TileHyperCube;
import crazypants.enderio.machine.light.BlockElectricLight;
import crazypants.enderio.machine.light.ElectricLightRenderer;
import crazypants.enderio.machine.painter.BlockCustomFenceGate;
import crazypants.enderio.machine.painter.BlockCustomFenceGateRenderer;
import crazypants.enderio.machine.painter.PaintedItemRenderer;
import crazypants.enderio.machine.power.BlockCapacitorBank;
import crazypants.enderio.machine.power.CapBankRenderer2;
import crazypants.enderio.machine.power.CapacitorBankRenderer;
import crazypants.enderio.machine.reservoir.ReservoirRenderer;
import crazypants.enderio.machine.reservoir.TileReservoir;
import crazypants.enderio.material.BlockFusedQuartz;
import crazypants.enderio.material.FusedQuartzFrameRenderer;
import crazypants.enderio.material.FusedQuartzRenderer;
import crazypants.enderio.material.MachinePartRenderer;

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
    AbstractMachineBlock.initIcon();
    RedstoneConduit.initIcons();
    InsulatedRedstoneConduit.initIcons();
    RedstoneSwitch.initIcons();
    PowerConduit.initIcons();
    LiquidConduit.initIcons();
    ItemConduit.initIcons();
  }

  private List<ConduitRenderer> conduitRenderers = new ArrayList<ConduitRenderer>();

  private DefaultConduitRenderer dcr = new DefaultConduitRenderer();

  private ConduitBundleRenderer cbr;

  @Override
  public World getClientWorld() {
    return FMLClientHandler.instance().getClient().theWorld;
  }

  @Override
  public EntityPlayer getClientPlayer() {
    return Minecraft.getMinecraft().thePlayer;
  }

  public ConduitBundleRenderer getConduitBundleRenderer() {
    return cbr;
  }

  public void setCbr(ConduitBundleRenderer cbr) {
    this.cbr = cbr;
  }

  @Override
  public void load() {
    super.load();

    // Renderers

    BlockCustomFenceGateRenderer bcfgr = new BlockCustomFenceGateRenderer();
    BlockCustomFenceGate.renderId = RenderingRegistry.getNextAvailableRenderId();
    RenderingRegistry.registerBlockHandler(bcfgr);

    BlockFusedQuartz.renderId = RenderingRegistry.getNextAvailableRenderId();
    RenderingRegistry.registerBlockHandler(new FusedQuartzRenderer());

    BlockElectricLight.renderId = RenderingRegistry.getNextAvailableRenderId();
    RenderingRegistry.registerBlockHandler(new ElectricLightRenderer());

    CapacitorBankRenderer capr = new CapacitorBankRenderer();
    MinecraftForgeClient.registerItemRenderer(EnderIO.blockCapacitorBank.blockID, capr);

    BlockCapacitorBank.renderId = RenderingRegistry.getNextAvailableRenderId();
    CapBankRenderer2 cbr2 = new CapBankRenderer2();
    RenderingRegistry.registerBlockHandler(cbr2);

    FusedQuartzFrameRenderer fqfr = new FusedQuartzFrameRenderer();
    MinecraftForgeClient.registerItemRenderer(EnderIO.itemFusedQuartzFrame.itemID, fqfr);

    ItemConduitRenderer itemConRenderer = new ItemConduitRenderer();
    MinecraftForgeClient.registerItemRenderer(EnderIO.itemLiquidConduit.itemID, itemConRenderer);
    MinecraftForgeClient.registerItemRenderer(EnderIO.itemPowerConduit.itemID, itemConRenderer);
    MinecraftForgeClient.registerItemRenderer(EnderIO.itemRedstoneConduit.itemID, itemConRenderer);
    MinecraftForgeClient.registerItemRenderer(EnderIO.itemItemConduit.itemID, itemConRenderer);

    PaintedItemRenderer pir = new PaintedItemRenderer();
    MinecraftForgeClient.registerItemRenderer(EnderIO.blockCustomFence.blockID, pir);
    MinecraftForgeClient.registerItemRenderer(EnderIO.blockCustomFenceGate.blockID, pir);
    MinecraftForgeClient.registerItemRenderer(EnderIO.blockCustomWall.blockID, pir);
    MinecraftForgeClient.registerItemRenderer(EnderIO.blockCustomStair.blockID, pir);
    MinecraftForgeClient.registerItemRenderer(EnderIO.blockCustomSlab.blockID, pir);

    MinecraftForgeClient.registerItemRenderer(EnderIO.itemMachinePart.itemID, new MachinePartRenderer());

    MinecraftForgeClient.registerItemRenderer(EnderIO.itemConduitFacade.itemID, new FacadeRenderer());

    cbr = new ConduitBundleRenderer((float) Config.conduitScale);
    BlockConduitBundle.rendererId = RenderingRegistry.getNextAvailableRenderId();
    RenderingRegistry.registerBlockHandler(cbr);
    ClientRegistry.bindTileEntitySpecialRenderer(TileConduitBundle.class, cbr);

    conduitRenderers.add(RedstoneSwitchRenderer.getInstance());
    conduitRenderers.add(new LiquidConduitRenderer());
    conduitRenderers.add(new PowerConduitRenderer());
    conduitRenderers.add(new InsulatedRedstoneConduitRenderer());
    conduitRenderers.add(new crazypants.enderio.conduit.item.ItemConduitRenderer());

    EnderIoRenderer eior = new EnderIoRenderer();
    ClientRegistry.bindTileEntitySpecialRenderer(TileEnderIO.class, eior);
    MinecraftForgeClient.registerItemRenderer(EnderIO.blockEnderIo.blockID, eior);

    ClientRegistry.bindTileEntitySpecialRenderer(TileReservoir.class, new ReservoirRenderer(EnderIO.blockReservoir));

    HyperCubeRenderer hcr = new HyperCubeRenderer();
    ClientRegistry.bindTileEntitySpecialRenderer(TileHyperCube.class, hcr);
    MinecraftForgeClient.registerItemRenderer(EnderIO.blockHyperCube.blockID, hcr);

    new YetaWrenchOverlayRenderer(EnderIO.itemYetaWench);
    // Tick handler
    if(Config.useSneakMouseWheelYetaWrench) {
      TickRegistry.registerTickHandler(new YetaWrenchTickHandler(), Side.CLIENT);
    }

  }

  @Override
  public ConduitRenderer getRendererForConduit(IConduit conduit) {
    for (ConduitRenderer renderer : conduitRenderers) {
      if(renderer.isRendererForConduit(conduit)) {
        return renderer;
      }
    }
    return dcr;
  }

  @Override
  public double getReachDistanceForPlayer(EntityPlayer entityPlayer) {
    if(entityPlayer instanceof EntityPlayerMP) {
      return ((EntityPlayerMP) entityPlayer).theItemInWorldManager.getBlockReachDistance();
    }
    return super.getReachDistanceForPlayer(entityPlayer);
  }

}
