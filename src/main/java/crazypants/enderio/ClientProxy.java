package crazypants.enderio;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.common.MinecraftForge;

import com.enderio.core.client.handlers.SpecialTooltipHandler;
import com.enderio.core.client.render.IconUtil;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import crazypants.enderio.conduit.BlockConduitBundle;
import crazypants.enderio.conduit.IConduit;
import crazypants.enderio.conduit.TileConduitBundle;
import crazypants.enderio.conduit.facade.FacadeRenderer;
import crazypants.enderio.conduit.gas.GasConduit;
import crazypants.enderio.conduit.gas.GasConduitRenderer;
import crazypants.enderio.conduit.gas.GasUtil;
import crazypants.enderio.conduit.item.ItemConduit;
import crazypants.enderio.conduit.liquid.AbstractEnderLiquidConduit;
import crazypants.enderio.conduit.liquid.AdvancedEnderLiquidConduit;
import crazypants.enderio.conduit.liquid.AdvancedLiquidConduit;
import crazypants.enderio.conduit.liquid.AdvancedLiquidConduitRenderer;
import crazypants.enderio.conduit.liquid.EnderLiquidConduit;
import crazypants.enderio.conduit.liquid.EnderLiquidConduitRenderer;
import crazypants.enderio.conduit.liquid.LiquidConduit;
import crazypants.enderio.conduit.liquid.LiquidConduitRenderer;
import crazypants.enderio.conduit.me.MEConduit;
import crazypants.enderio.conduit.me.MEUtil;
import crazypants.enderio.conduit.oc.OCConduit;
import crazypants.enderio.conduit.oc.OCConduitRenderer;
import crazypants.enderio.conduit.oc.OCUtil;
import crazypants.enderio.conduit.power.PowerConduit;
import crazypants.enderio.conduit.power.PowerConduitRenderer;
import crazypants.enderio.conduit.power.endergy.PowerConduitEndergy;
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
import crazypants.enderio.gui.TooltipHandlerBurnTime;
import crazypants.enderio.gui.TooltipHandlerFluid;
import crazypants.enderio.gui.TooltipHandlerGrinding;
import crazypants.enderio.item.ConduitProbeOverlayRenderer;
import crazypants.enderio.item.KeyTracker;
import crazypants.enderio.item.ToolTickHandler;
import crazypants.enderio.item.YetaWrenchOverlayRenderer;
import crazypants.enderio.item.darksteel.DarkSteelItems;
import crazypants.enderio.item.darksteel.SoundDetector;
import crazypants.enderio.item.darksteel.SoundEntity;
import crazypants.enderio.item.darksteel.SoundRenderer;
import crazypants.enderio.item.skull.BlockEndermanSkull;
import crazypants.enderio.item.skull.EndermanSkullRenderer;
import crazypants.enderio.machine.AbstractMachineBlock;
import crazypants.enderio.machine.AbstractMachineRenderer;
import crazypants.enderio.machine.TechneMachineRenderer;
import crazypants.enderio.machine.capbank.BlockCapBank;
import crazypants.enderio.machine.capbank.TileCapBank;
import crazypants.enderio.machine.capbank.render.CapBankRenderer;
import crazypants.enderio.machine.enchanter.EnchanterModelRenderer;
import crazypants.enderio.machine.enchanter.TileEnchanter;
import crazypants.enderio.machine.farm.BlockFarmStation;
import crazypants.enderio.machine.farm.FarmingStationRenderer;
import crazypants.enderio.machine.farm.FarmingStationSpecialRenderer;
import crazypants.enderio.machine.farm.TileFarmStation;
import crazypants.enderio.machine.generator.combustion.BlockCombustionGenerator;
import crazypants.enderio.machine.generator.combustion.TileCombustionGenerator;
import crazypants.enderio.machine.generator.zombie.TileZombieGenerator;
import crazypants.enderio.machine.generator.zombie.ZombieGeneratorRenderer;
import crazypants.enderio.machine.hypercube.HyperCubeRenderer;
import crazypants.enderio.machine.hypercube.TileHyperCube;
import crazypants.enderio.machine.killera.KillerJoeRenderer;
import crazypants.enderio.machine.killera.TileKillerJoe;
import crazypants.enderio.machine.light.BlockElectricLight;
import crazypants.enderio.machine.light.ElectricLightRenderer;
import crazypants.enderio.machine.obelisk.BlockObeliskAbstract;
import crazypants.enderio.machine.obelisk.ObeliskRenderer;
import crazypants.enderio.machine.obelisk.ObeliskSpecialRenderer;
import crazypants.enderio.machine.obelisk.attractor.TileAttractor;
import crazypants.enderio.machine.obelisk.aversion.AversionObeliskRenderer;
import crazypants.enderio.machine.obelisk.aversion.TileAversionObelisk;
import crazypants.enderio.machine.obelisk.weather.TileWeatherObelisk;
import crazypants.enderio.machine.obelisk.weather.WeatherObeliskSpecialRenderer;
import crazypants.enderio.machine.obelisk.xp.TileExperienceObelisk;
import crazypants.enderio.machine.painter.BlockPaintedFenceGate;
import crazypants.enderio.machine.painter.BlockPaintedFenceGateRenderer;
import crazypants.enderio.machine.painter.BlockPaintedGlowstone;
import crazypants.enderio.machine.painter.PaintedBlockRenderer;
import crazypants.enderio.machine.painter.PaintedItemRenderer;
import crazypants.enderio.machine.power.BlockCapacitorBank;
import crazypants.enderio.machine.power.CapBankRenderer2;
import crazypants.enderio.machine.power.CapacitorBankRenderer;
import crazypants.enderio.machine.ranged.RangeEntity;
import crazypants.enderio.machine.ranged.RangeRenerer;
import crazypants.enderio.machine.reservoir.ReservoirRenderer;
import crazypants.enderio.machine.reservoir.TileReservoir;
import crazypants.enderio.machine.solar.BlockSolarPanel;
import crazypants.enderio.machine.solar.SolarPanelRenderer;
import crazypants.enderio.machine.soul.BlockSoulBinder;
import crazypants.enderio.machine.soul.SoulBinderRenderer;
import crazypants.enderio.machine.spawner.BrokenSpawnerRenderer;
import crazypants.enderio.machine.tank.TankFluidRenderer;
import crazypants.enderio.machine.tank.TankItemRenderer;
import crazypants.enderio.machine.tank.TileTank;
import crazypants.enderio.machine.transceiver.TileTransceiver;
import crazypants.enderio.machine.transceiver.render.TransceiverRenderer;
import crazypants.enderio.machine.vacuum.BlockVacuumChest;
import crazypants.enderio.machine.vacuum.VacuumChestRenderer;
import crazypants.enderio.machine.vat.BlockVat;
import crazypants.enderio.machine.vat.TileVat;
import crazypants.enderio.material.BlockFusedQuartz;
import crazypants.enderio.material.FusedQuartzFrameRenderer;
import crazypants.enderio.material.FusedQuartzRenderer;
import crazypants.enderio.material.MachinePartRenderer;
import crazypants.enderio.material.Material;
import crazypants.enderio.teleport.TravelController;
import crazypants.enderio.teleport.anchor.BlockTravelAnchor;
import crazypants.enderio.teleport.anchor.TileTravelAnchor;
import crazypants.enderio.teleport.anchor.TravelEntitySpecialRenderer;
import crazypants.enderio.teleport.telepad.BlockTelePad;
import crazypants.enderio.teleport.telepad.TelePadRenderer;
import crazypants.enderio.teleport.telepad.TelePadSpecialRenderer;
import crazypants.enderio.teleport.telepad.TeleportEntityRenderHandler;
import crazypants.enderio.teleport.telepad.TileTelePad;

@SideOnly(Side.CLIENT)
public class ClientProxy extends CommonProxy {

  // @formatter:off
  public static int[][] sideAndFacingToSpriteOffset = new int[][] {
    { 3, 2, 0, 0, 0, 0 },
    { 2, 3, 1, 1, 1, 1 },
    { 1, 1, 3, 2, 5, 4 },
    { 0, 0, 2, 3, 4, 5 },
    { 4, 5, 4, 5, 3, 2 },
    { 5, 4, 5, 4, 2, 3 }
  };
  // @formatter:on

  private final List<ConduitRenderer> conduitRenderers = new ArrayList<ConduitRenderer>();

  private final DefaultConduitRenderer dcr = new DefaultConduitRenderer();

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
  public void loadIcons() {
    RedstoneConduit.initIcons();
    InsulatedRedstoneConduit.initIcons();
    RedstoneSwitch.initIcons();
    PowerConduit.initIcons();
    PowerConduitEndergy.initIcons();
    LiquidConduit.initIcons();
    AdvancedLiquidConduit.initIcons();
    AbstractEnderLiquidConduit.initIcons();
    EnderLiquidConduit.initIcons();
    AdvancedEnderLiquidConduit.initIcons();
    ItemConduit.initIcons();
    if(GasUtil.isGasConduitEnabled()) {
      GasConduit.initIcons();
    }
    if(MEUtil.isMEEnabled()) {
      MEConduit.initIcons();
    }
    if (OCUtil.isOCEnabled()) {
      OCConduit.initIcons();
    }
  }

  @Override
  public void load() {
    super.load();

    SpecialTooltipHandler tt = SpecialTooltipHandler.INSTANCE;
    tt.addCallback(new TooltipHandlerGrinding());
    tt.addCallback(new TooltipHandlerBurnTime());
    if (Config.addFuelTooltipsToAllFluidContainers) {
      tt.addCallback(new TooltipHandlerFluid());
    }

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
    MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(EnderIO.blockBuffer), machRen);

    MinecraftForgeClient.registerItemRenderer(EnderIO.itemBrokenSpawner, new BrokenSpawnerRenderer());

    BlockSolarPanel.renderId = RenderingRegistry.getNextAvailableRenderId();
    RenderingRegistry.registerBlockHandler(new SolarPanelRenderer());

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

    ObeliskRenderer defaultObeliskRenderer = new ObeliskRenderer();
    BlockObeliskAbstract.defaultObeliskRenderId = RenderingRegistry.getNextAvailableRenderId();
    RenderingRegistry.registerBlockHandler(defaultObeliskRenderer);

    ObeliskSpecialRenderer<TileAttractor> attRen = new ObeliskSpecialRenderer<TileAttractor>(new ItemStack(EnderIO.itemMaterial, 1,
        Material.ATTRACTOR_CRYSTAL.ordinal()), defaultObeliskRenderer);
    ClientRegistry.bindTileEntitySpecialRenderer(TileAttractor.class, attRen);
    MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(EnderIO.blockAttractor), attRen);

    AversionObeliskRenderer sgr = new AversionObeliskRenderer(defaultObeliskRenderer);
    ClientRegistry.bindTileEntitySpecialRenderer(TileAversionObelisk.class, sgr);
    MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(EnderIO.blockSpawnGuard), sgr);

    ObeliskSpecialRenderer<TileExperienceObelisk> eor = new ObeliskSpecialRenderer<TileExperienceObelisk>(new ItemStack(EnderIO.itemXpTransfer),
        defaultObeliskRenderer);
    ClientRegistry.bindTileEntitySpecialRenderer(TileExperienceObelisk.class, eor);
    MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(EnderIO.blockExperianceOblisk), eor);

    ObeliskSpecialRenderer<TileWeatherObelisk> twr = new WeatherObeliskSpecialRenderer(new ItemStack(Items.fireworks), defaultObeliskRenderer);
    ClientRegistry.bindTileEntitySpecialRenderer(TileWeatherObelisk.class, twr);
    MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(EnderIO.blockWeatherObelisk), twr);

    BlockCombustionGenerator.renderId = RenderingRegistry.getNextAvailableRenderId();
    TechneMachineRenderer<TileCombustionGenerator> cr = new TechneMachineRenderer<TileCombustionGenerator>(EnderIO.blockCombustionGenerator, "models/combustionGen");
    RenderingRegistry.registerBlockHandler(cr);

    ZombieGeneratorRenderer zgr = new ZombieGeneratorRenderer();
    ClientRegistry.bindTileEntitySpecialRenderer(TileZombieGenerator.class, zgr);
    MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(EnderIO.blockZombieGenerator), zgr);
    MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(EnderIO.blockFrankenZombieGenerator), zgr);
    MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(EnderIO.blockEnderGenerator), zgr);

    KillerJoeRenderer kjr = new KillerJoeRenderer();
    ClientRegistry.bindTileEntitySpecialRenderer(TileKillerJoe.class, kjr);
    MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(EnderIO.blockKillerJoe), kjr);

    BlockVat.renderId = RenderingRegistry.getNextAvailableRenderId();
    TechneMachineRenderer<TileVat> vr = new TechneMachineRenderer<TileVat>(EnderIO.blockVat, "models/vat");
    RenderingRegistry.registerBlockHandler(vr);

    FusedQuartzFrameRenderer fqfr = new FusedQuartzFrameRenderer();
    MinecraftForgeClient.registerItemRenderer(EnderIO.itemFusedQuartzFrame, fqfr);

    BlockElectricLight.renderId = RenderingRegistry.getNextAvailableRenderId();
    RenderingRegistry.registerBlockHandler(new ElectricLightRenderer());

    if(EnderIO.blockCapBank != null) {
      BlockCapBank.renderId = RenderingRegistry.getNextAvailableRenderId();
      CapBankRenderer newCbr = new CapBankRenderer();
      RenderingRegistry.registerBlockHandler(newCbr);
      MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(EnderIO.blockCapBank), newCbr);
      ClientRegistry.bindTileEntitySpecialRenderer(TileCapBank.class, newCbr);
    }

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
    MinecraftForgeClient.registerItemRenderer(EnderIO.itemPowerConduitEndergy, itemConRenderer);
    MinecraftForgeClient.registerItemRenderer(EnderIO.itemRedstoneConduit, itemConRenderer);
    MinecraftForgeClient.registerItemRenderer(EnderIO.itemItemConduit, itemConRenderer);
    MinecraftForgeClient.registerItemRenderer(EnderIO.itemGasConduit, itemConRenderer);
    MinecraftForgeClient.registerItemRenderer(EnderIO.itemMEConduit, itemConRenderer);
    MinecraftForgeClient.registerItemRenderer(EnderIO.itemOCConduit, itemConRenderer);

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

    BlockTelePad.renderId = RenderingRegistry.getNextAvailableRenderId();
    TelePadRenderer telePadRenderer = new TelePadRenderer();
    RenderingRegistry.registerBlockHandler(telePadRenderer);
    ClientRegistry.bindTileEntitySpecialRenderer(TileTelePad.class, new TelePadSpecialRenderer(telePadRenderer));

    MinecraftForgeClient.registerItemRenderer(EnderIO.itemMachinePart, new MachinePartRenderer());
    MinecraftForgeClient.registerItemRenderer(EnderIO.itemConduitFacade, new FacadeRenderer());

    cbr = new ConduitBundleRenderer((float) Config.conduitScale);
    BlockConduitBundle.rendererId = RenderingRegistry.getNextAvailableRenderId();
    RenderingRegistry.registerBlockHandler(cbr);
    ClientRegistry.bindTileEntitySpecialRenderer(TileConduitBundle.class, cbr);

    ClientRegistry.bindTileEntitySpecialRenderer(TileTravelAnchor.class, new TravelEntitySpecialRenderer());

    BlockEndermanSkull.renderId = RenderingRegistry.getNextAvailableRenderId();
    EndermanSkullRenderer esk = new EndermanSkullRenderer();
    RenderingRegistry.registerBlockHandler(esk);
    MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(EnderIO.blockEndermanSkull), esk);

    conduitRenderers.add(RedstoneSwitchRenderer.getInstance());
    conduitRenderers.add(new AdvancedLiquidConduitRenderer());
    conduitRenderers.add(LiquidConduitRenderer.create());
    conduitRenderers.add(new PowerConduitRenderer());
    conduitRenderers.add(new InsulatedRedstoneConduitRenderer());
    conduitRenderers.add(new EnderLiquidConduitRenderer());
    conduitRenderers.add(new crazypants.enderio.conduit.item.ItemConduitRenderer());
    if(GasUtil.isGasConduitEnabled()) {
      conduitRenderers.add(new GasConduitRenderer());
    }
    if (OCUtil.isOCEnabled()) {
      conduitRenderers.add(new OCConduitRenderer());
    }

    EnderIoRenderer eior = new EnderIoRenderer();
    ClientRegistry.bindTileEntitySpecialRenderer(TileEnderIO.class, eior);
    MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(EnderIO.blockEnderIo), eior);

    ClientRegistry.bindTileEntitySpecialRenderer(TileReservoir.class, new ReservoirRenderer(EnderIO.blockReservoir));
    ClientRegistry.bindTileEntitySpecialRenderer(TileTank.class, new TankFluidRenderer());
    MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(EnderIO.blockTank), new TankItemRenderer());

    HyperCubeRenderer hcr = new HyperCubeRenderer();
    ClientRegistry.bindTileEntitySpecialRenderer(TileHyperCube.class, hcr);
    MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(EnderIO.blockHyperCube), hcr);

    if(Config.transceiverEnabled) {
      TransceiverRenderer tr = new TransceiverRenderer();
      ClientRegistry.bindTileEntitySpecialRenderer(TileTransceiver.class, tr);
      MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(EnderIO.blockTransceiver), tr);
    }

    new YetaWrenchOverlayRenderer();
    new ConduitProbeOverlayRenderer();
    if(Config.useSneakMouseWheelYetaWrench) {
      ToolTickHandler th = new ToolTickHandler();
      MinecraftForge.EVENT_BUS.register(th);
      FMLCommonHandler.instance().bus().register(th);
    }
    MinecraftForge.EVENT_BUS.register(TravelController.instance);
    FMLCommonHandler.instance().bus().register(TravelController.instance);

    DarkSteelItems.registerItemRenderer();

    FMLCommonHandler.instance().bus().register(KeyTracker.instance);

    RenderingRegistry.registerEntityRenderingHandler(SoundEntity.class, new SoundRenderer());
    RenderingRegistry.registerEntityRenderingHandler(RangeEntity.class, new RangeRenerer());

    MinecraftForge.EVENT_BUS.register(SoundDetector.instance);
    FMLCommonHandler.instance().bus().register(SoundDetector.instance);

    if(!Loader.isModLoaded("OpenBlocks")) {
      //We have registered liquid XP so we need to give it textures
      IconUtil.addIconProvider(new IconUtil.IIconProvider() {

        @Override
        public void registerIcons(IIconRegister register) {
          //NB: textures re-used with permission from OpenBlocks to maintain look
          IIcon flowing = register.registerIcon("enderio:xpjuiceflowing");
          IIcon still = register.registerIcon("enderio:xpjuicestill");
          EnderIO.fluidXpJuice.setIcons(still, flowing);
        }

        @Override
        public int getTextureType() {
          return 0;
        }

      });
    }

    MinecraftForge.EVENT_BUS.register(new TeleportEntityRenderHandler());
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

  @Override
  public void setInstantConfusionOnPlayer(EntityPlayer ent, int duration) {
    ent.addPotionEffect(new PotionEffect(Potion.confusion.getId(), duration, 1, true));
    Minecraft.getMinecraft().thePlayer.timeInPortal = 1;
  }

  @Override
  public long getTickCount() {
    return clientTickCount;
  }

  @Override
  protected void onClientTick() {
    if(!Minecraft.getMinecraft().isGamePaused() && Minecraft.getMinecraft().theWorld != null) {
      ++clientTickCount;
    }
  }

}
