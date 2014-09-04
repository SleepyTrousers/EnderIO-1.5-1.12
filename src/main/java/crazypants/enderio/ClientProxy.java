package crazypants.enderio;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.world.World;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.common.MinecraftForge;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.FMLCommonHandler;
import crazypants.enderio.conduit.BlockConduitBundle;
import crazypants.enderio.conduit.IConduit;
import crazypants.enderio.conduit.TileConduitBundle;
import crazypants.enderio.conduit.facade.FacadeRenderer;
import crazypants.enderio.conduit.gas.GasConduit;
import crazypants.enderio.conduit.gas.GasConduitRenderer;
import crazypants.enderio.conduit.item.ItemConduit;
import crazypants.enderio.conduit.liquid.AdvancedLiquidConduit;
import crazypants.enderio.conduit.liquid.AdvancedLiquidConduitRenderer;
import crazypants.enderio.conduit.liquid.EnderLiquidConduit;
import crazypants.enderio.conduit.liquid.EnderLiquidConduitRenderer;
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
import crazypants.enderio.config.Config;
import crazypants.enderio.enderface.EnderIoRenderer;
import crazypants.enderio.enderface.TileEnderIO;
import crazypants.enderio.gui.TooltipAddera;
import crazypants.enderio.item.KeyTracker;
import crazypants.enderio.item.YetaWrenchOverlayRenderer;
import crazypants.enderio.item.YetaWrenchTickHandler;
import crazypants.enderio.item.darksteel.PoweredItemRenderer;
import crazypants.enderio.item.darksteel.SoundDetector;
import crazypants.enderio.item.darksteel.SoundEntity;
import crazypants.enderio.item.darksteel.SoundRenderer;
import crazypants.enderio.item.skull.BlockEndermanSkull;
import crazypants.enderio.item.skull.EndermanSkullRenderer;
import crazypants.enderio.machine.AbstractMachineBlock;
import crazypants.enderio.machine.AbstractMachineRenderer;
import crazypants.enderio.machine.enchanter.EnchanterModelRenderer;
import crazypants.enderio.machine.enchanter.TileEnchanter;
import crazypants.enderio.machine.farm.BlockFarmStation;
import crazypants.enderio.machine.farm.FarmingStationRenderer;
import crazypants.enderio.machine.farm.FarmingStationSpecialRenderer;
import crazypants.enderio.machine.farm.TileFarmStation;
import crazypants.enderio.machine.generator.combustion.BlockCombustionGenerator;
import crazypants.enderio.machine.generator.combustion.CombustionGeneratorModelRenderer;
import crazypants.enderio.machine.generator.combustion.CombustionGeneratorRenderer;
import crazypants.enderio.machine.generator.combustion.TileCombustionGenerator;
import crazypants.enderio.machine.generator.zombie.TileZombieGenerator;
import crazypants.enderio.machine.generator.zombie.ZombieGeneratorRenderer;
import crazypants.enderio.machine.hypercube.HyperCubeRenderer;
import crazypants.enderio.machine.hypercube.TileHyperCube;
import crazypants.enderio.machine.killera.KillerJoeRenderer;
import crazypants.enderio.machine.killera.TileKillerJoe;
import crazypants.enderio.machine.light.BlockElectricLight;
import crazypants.enderio.machine.light.ElectricLightRenderer;
import crazypants.enderio.machine.painter.BlockPaintedFenceGate;
import crazypants.enderio.machine.painter.BlockPaintedFenceGateRenderer;
import crazypants.enderio.machine.painter.BlockPaintedGlowstone;
import crazypants.enderio.machine.painter.PaintedBlockRenderer;
import crazypants.enderio.machine.painter.PaintedItemRenderer;
import crazypants.enderio.machine.power.BlockCapacitorBank;
import crazypants.enderio.machine.power.CapBankRenderer2;
import crazypants.enderio.machine.power.CapacitorBankRenderer;
import crazypants.enderio.machine.reservoir.ReservoirRenderer;
import crazypants.enderio.machine.reservoir.TileReservoir;
import crazypants.enderio.machine.soul.BlockSoulBinder;
import crazypants.enderio.machine.soul.SoulBinderRenderer;
import crazypants.enderio.machine.spawner.BrokenSpawnerRenderer;
import crazypants.enderio.machine.still.BlockVat;
import crazypants.enderio.machine.still.VatRenderer;
import crazypants.enderio.machine.tank.TankFluidRenderer;
import crazypants.enderio.machine.tank.TankItemRenderer;
import crazypants.enderio.machine.tank.TileTank;
import crazypants.enderio.machine.vacuum.BlockVacuumChest;
import crazypants.enderio.machine.vacuum.VacuumChestRenderer;
import crazypants.enderio.material.BlockFusedQuartz;
import crazypants.enderio.material.FusedQuartzFrameRenderer;
import crazypants.enderio.material.FusedQuartzRenderer;
import crazypants.enderio.material.MachinePartRenderer;
import crazypants.enderio.teleport.BlockTravelAnchor;
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
    RedstoneConduit.initIcons();
    InsulatedRedstoneConduit.initIcons();
    RedstoneSwitch.initIcons();
    PowerConduit.initIcons();
    LiquidConduit.initIcons();
    AdvancedLiquidConduit.initIcons();
    EnderLiquidConduit.initIcons();
    ItemConduit.initIcons();
    GasConduit.initIcons();
  }

  private List<ConduitRenderer> conduitRenderers = new ArrayList<ConduitRenderer>();

  private DefaultConduitRenderer dcr = new DefaultConduitRenderer();

  private ConduitBundleRenderer cbr;

  private boolean checkedNei = false;
  private boolean neiInstalled = false;

  @Override
  public World getClientWorld() {
    return FMLClientHandler.instance().getClient().theWorld;
  }

  @Override
  public boolean isNeiInstalled() {
    if(checkedNei) {
      return neiInstalled;
    }
    try {
      Class.forName("crazypants.enderio.nei.EnchanterRecipeHandler");
      neiInstalled = true;
    } catch (Exception e) {
      neiInstalled = false;
    }
    checkedNei = true;
    return false;
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

    //make sure the tooltip stuff is registered
    TooltipAddera tta = TooltipAddera.instance;

    // Renderers

    AbstractMachineBlock.renderId = RenderingRegistry.getNextAvailableRenderId();
    AbstractMachineRenderer machRen = new AbstractMachineRenderer();
    RenderingRegistry.registerBlockHandler(machRen);
    MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(EnderIO.blockStirlingGenerator), machRen);
    MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(EnderIO.blockCrusher), machRen);
    MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(EnderIO.blockAlloySmelter), machRen);
    MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(EnderIO.blockPowerMonitor), machRen);
    MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(EnderIO.blockPainter), machRen);
    MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(EnderIO.blockCrafter), machRen);

    MinecraftForgeClient.registerItemRenderer(EnderIO.itemBrokenSpawner, new BrokenSpawnerRenderer());

    EnchanterModelRenderer emr = new EnchanterModelRenderer();
    ClientRegistry.bindTileEntitySpecialRenderer(TileEnchanter.class, emr);
    MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(EnderIO.blockEnchanter), emr);

    BlockFusedQuartz.renderId = RenderingRegistry.getNextAvailableRenderId();
    RenderingRegistry.registerBlockHandler(new FusedQuartzRenderer());

    BlockFarmStation.renderId = RenderingRegistry.getNextAvailableRenderId();
    RenderingRegistry.registerBlockHandler(new FarmingStationRenderer());
    ClientRegistry.bindTileEntitySpecialRenderer(TileFarmStation.class, new FarmingStationSpecialRenderer());
    
    BlockSoulBinder.renderId = RenderingRegistry.getNextAvailableRenderId();
    RenderingRegistry.registerBlockHandler(new SoulBinderRenderer());


    if(Config.useCombustionGenModel) {
      CombustionGeneratorModelRenderer cgmr = new CombustionGeneratorModelRenderer();
      ClientRegistry.bindTileEntitySpecialRenderer(TileCombustionGenerator.class, cgmr);
      MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(EnderIO.blockCombustionGenerator), cgmr);
    } else {
      BlockCombustionGenerator.renderId = RenderingRegistry.getNextAvailableRenderId();
      CombustionGeneratorRenderer cr = new CombustionGeneratorRenderer();
      RenderingRegistry.registerBlockHandler(cr);
      if(!Config.combustionGeneratorUseOpaqueModel) {
        ClientRegistry.bindTileEntitySpecialRenderer(TileCombustionGenerator.class, cr);
      }
    }

    ZombieGeneratorRenderer zgr = new ZombieGeneratorRenderer();
    ClientRegistry.bindTileEntitySpecialRenderer(TileZombieGenerator.class, zgr);
    MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(EnderIO.blockZombieGenerator), zgr);

    KillerJoeRenderer kjr = new KillerJoeRenderer();
    ClientRegistry.bindTileEntitySpecialRenderer(TileKillerJoe.class, kjr);
    MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(EnderIO.blockKillerJoe), kjr);

    BlockVat.renderId = RenderingRegistry.getNextAvailableRenderId();
    VatRenderer vr = new VatRenderer();
    RenderingRegistry.registerBlockHandler(vr);
    MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(EnderIO.blockVat), vr);

    FusedQuartzFrameRenderer fqfr = new FusedQuartzFrameRenderer();
    MinecraftForgeClient.registerItemRenderer(EnderIO.itemFusedQuartzFrame, fqfr);

    BlockElectricLight.renderId = RenderingRegistry.getNextAvailableRenderId();
    RenderingRegistry.registerBlockHandler(new ElectricLightRenderer());

    CapacitorBankRenderer capr = new CapacitorBankRenderer();
    MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(EnderIO.blockCapacitorBank), capr);

    BlockCapacitorBank.renderId = RenderingRegistry.getNextAvailableRenderId();
    CapBankRenderer2 cbr2 = new CapBankRenderer2();
    RenderingRegistry.registerBlockHandler(cbr2);

    BlockVacuumChest.renderId = RenderingRegistry.getNextAvailableRenderId();
    VacuumChestRenderer vcr = new VacuumChestRenderer();
    RenderingRegistry.registerBlockHandler(vcr);
    MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(EnderIO.blockVacuumChest), vcr);

    ItemConduitRenderer itemConRenderer = new ItemConduitRenderer();
    MinecraftForgeClient.registerItemRenderer(EnderIO.itemLiquidConduit, itemConRenderer);
    MinecraftForgeClient.registerItemRenderer(EnderIO.itemPowerConduit, itemConRenderer);
    MinecraftForgeClient.registerItemRenderer(EnderIO.itemRedstoneConduit, itemConRenderer);
    MinecraftForgeClient.registerItemRenderer(EnderIO.itemItemConduit, itemConRenderer);
    MinecraftForgeClient.registerItemRenderer(EnderIO.itemGasConduit, itemConRenderer);

    BlockPaintedFenceGateRenderer bcfgr = new BlockPaintedFenceGateRenderer();
    BlockPaintedFenceGate.renderId = RenderingRegistry.getNextAvailableRenderId();
    RenderingRegistry.registerBlockHandler(bcfgr);

    PaintedItemRenderer pir = new PaintedItemRenderer();
    MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(EnderIO.blockPaintedFence), pir);
    MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(EnderIO.blockPaintedFenceGate), pir);
    MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(EnderIO.blockPaintedWall), pir);
    MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(EnderIO.blockPaintedStair), pir);
    MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(EnderIO.blockPaintedSlab), pir);
    MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(EnderIO.blockPaintedGlowstone), pir);
    MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(EnderIO.blockPaintedCarpet), pir);
    MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(EnderIO.blockDarkSteelPressurePlate), pir);
    MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(EnderIO.blockTravelPlatform), pir);

    BlockPaintedGlowstone.renderId = RenderingRegistry.getNextAvailableRenderId();
    RenderingRegistry.registerBlockHandler(new PaintedBlockRenderer(BlockPaintedGlowstone.renderId, Blocks.glowstone));

    BlockTravelAnchor.renderId = RenderingRegistry.getNextAvailableRenderId();
    RenderingRegistry.registerBlockHandler(new PaintedBlockRenderer(BlockTravelAnchor.renderId, EnderIO.blockTravelPlatform));

    MinecraftForgeClient.registerItemRenderer(EnderIO.itemMachinePart, new MachinePartRenderer());
    MinecraftForgeClient.registerItemRenderer(EnderIO.itemConduitFacade, new FacadeRenderer());

    cbr = new ConduitBundleRenderer((float) Config.conduitScale);
    BlockConduitBundle.rendererId = RenderingRegistry.getNextAvailableRenderId();
    RenderingRegistry.registerBlockHandler(cbr);
    ClientRegistry.bindTileEntitySpecialRenderer(TileConduitBundle.class, cbr);

    ClientRegistry.bindTileEntitySpecialRenderer(TileTravelAnchor.class, new TravelEntitySpecialRenderer());

    BlockEndermanSkull.renderId = RenderingRegistry.getNextAvailableRenderId();
    RenderingRegistry.registerBlockHandler(new EndermanSkullRenderer());

    conduitRenderers.add(RedstoneSwitchRenderer.getInstance());
    conduitRenderers.add(new AdvancedLiquidConduitRenderer());
    conduitRenderers.add(new LiquidConduitRenderer());
    conduitRenderers.add(new PowerConduitRenderer());
    conduitRenderers.add(new InsulatedRedstoneConduitRenderer());
    conduitRenderers.add(new EnderLiquidConduitRenderer());
    conduitRenderers.add(new crazypants.enderio.conduit.item.ItemConduitRenderer());
    conduitRenderers.add(new GasConduitRenderer());

    EnderIoRenderer eior = new EnderIoRenderer();
    ClientRegistry.bindTileEntitySpecialRenderer(TileEnderIO.class, eior);
    MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(EnderIO.blockEnderIo), eior);

    ClientRegistry.bindTileEntitySpecialRenderer(TileReservoir.class, new ReservoirRenderer(EnderIO.blockReservoir));
    ClientRegistry.bindTileEntitySpecialRenderer(TileTank.class, new TankFluidRenderer());
    MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(EnderIO.blockTank), new TankItemRenderer());

    HyperCubeRenderer hcr = new HyperCubeRenderer();
    ClientRegistry.bindTileEntitySpecialRenderer(TileHyperCube.class, hcr);
    MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(EnderIO.blockHyperCube), hcr);

    new YetaWrenchOverlayRenderer(EnderIO.itemYetaWench);
    if(Config.useSneakMouseWheelYetaWrench) {
      YetaWrenchTickHandler th = new YetaWrenchTickHandler();
      MinecraftForge.EVENT_BUS.register(th);
      FMLCommonHandler.instance().bus().register(th);
    }
    MinecraftForge.EVENT_BUS.register(TravelController.instance);
    FMLCommonHandler.instance().bus().register(TravelController.instance);

    PoweredItemRenderer dsr = new PoweredItemRenderer();
    MinecraftForgeClient.registerItemRenderer(EnderIO.itemDarkSteelBoots, dsr);
    MinecraftForgeClient.registerItemRenderer(EnderIO.itemDarkSteelLeggings, dsr);
    MinecraftForgeClient.registerItemRenderer(EnderIO.itemDarkSteelChestplate, dsr);
    MinecraftForgeClient.registerItemRenderer(EnderIO.itemDarkSteelHelmet, dsr);
    MinecraftForgeClient.registerItemRenderer(EnderIO.itemDarkSteelSword, dsr);
    MinecraftForgeClient.registerItemRenderer(EnderIO.itemDarkSteelPickaxe, dsr);
    MinecraftForgeClient.registerItemRenderer(EnderIO.itemDarkSteelAxe, dsr);
    //Ensure it is loaded and registered
    KeyTracker.instance.isGlideActive();

    RenderingRegistry.registerEntityRenderingHandler(SoundEntity.class, new SoundRenderer());

    MinecraftForge.EVENT_BUS.register(SoundDetector.instance);
    FMLCommonHandler.instance().bus().register(SoundDetector.instance);

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
