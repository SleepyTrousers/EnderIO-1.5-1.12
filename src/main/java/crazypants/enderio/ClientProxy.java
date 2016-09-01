package crazypants.enderio;

import java.util.List;

import com.enderio.core.client.handlers.SpecialTooltipHandler;
import com.enderio.core.common.vecmath.Vector4f;

import crazypants.enderio.conduit.render.ConduitBundleRenderManager;
import crazypants.enderio.config.Config;
import crazypants.enderio.diagnostics.DebugCommand;
import crazypants.enderio.enderface.EnderIoRenderer;
import crazypants.enderio.enderface.TileEnderIO;
import crazypants.enderio.fluid.Buckets;
import crazypants.enderio.gui.TooltipHandlerBurnTime;
import crazypants.enderio.gui.TooltipHandlerFluid;
import crazypants.enderio.gui.TooltipHandlerGrinding;
import crazypants.enderio.item.ConduitProbeOverlayRenderer;
import crazypants.enderio.item.KeyTracker;
import crazypants.enderio.item.ToolTickHandler;
import crazypants.enderio.item.YetaWrenchOverlayRenderer;
import crazypants.enderio.item.darksteel.DarkSteelItems;
import crazypants.enderio.item.darksteel.SoundDetector;
import crazypants.enderio.item.darksteel.upgrade.UpgradeRenderDispatcher;
import crazypants.enderio.item.skull.EndermanSkullRenderer;
import crazypants.enderio.item.skull.TileEndermanSkull;
import crazypants.enderio.machine.capbank.TileCapBank;
import crazypants.enderio.machine.capbank.network.ClientNetworkManager;
import crazypants.enderio.machine.capbank.render.CapBankRenderer;
import crazypants.enderio.machine.enchanter.EnchanterModelRenderer;
import crazypants.enderio.machine.enchanter.TileEnchanter;
import crazypants.enderio.machine.farm.FarmingStationSpecialRenderer;
import crazypants.enderio.machine.farm.TileFarmStation;
import crazypants.enderio.machine.gauge.TESRGauge;
import crazypants.enderio.machine.gauge.TileGauge;
import crazypants.enderio.machine.generator.zombie.TileZombieGenerator;
import crazypants.enderio.machine.generator.zombie.ZombieGeneratorRenderer;
import crazypants.enderio.machine.killera.KillerJoeRenderer;
import crazypants.enderio.machine.killera.TileKillerJoe;
import crazypants.enderio.machine.monitor.TESRPowerMonitor;
import crazypants.enderio.machine.monitor.TilePowerMonitor;
import crazypants.enderio.machine.obelisk.render.ObeliskRenderManager;
import crazypants.enderio.machine.ranged.MarkerParticle;
import crazypants.enderio.machine.reservoir.ReservoirRenderer;
import crazypants.enderio.machine.reservoir.TileReservoir;
import crazypants.enderio.machine.soul.SoulBinderTESR;
import crazypants.enderio.machine.soul.TileSoulBinder;
import crazypants.enderio.machine.tank.TankFluidRenderer;
import crazypants.enderio.machine.tank.TileTank;
import crazypants.enderio.machine.transceiver.TileTransceiver;
import crazypants.enderio.machine.transceiver.render.TransceiverRenderer;
import crazypants.enderio.paint.YetaUtil;
import crazypants.enderio.paint.render.PaintRegistry;
import crazypants.enderio.render.ItemModelRegistry;
import crazypants.enderio.render.SmartModelAttacher;
import crazypants.enderio.teleport.TravelController;
import crazypants.enderio.teleport.anchor.TileTravelAnchor;
import crazypants.enderio.teleport.anchor.TravelEntitySpecialRenderer;
import crazypants.enderio.teleport.telepad.ItemLocationPrintout;
import crazypants.enderio.teleport.telepad.TeleportEntityRenderHandler;
import crazypants.enderio.teleport.telepad.TileTelePad;
import crazypants.enderio.teleport.telepad.render.TelePadSpecialRenderer;
import crazypants.util.ClientUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.MobEffects;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

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

  private boolean checkedNei = false;
  private boolean neiInstalled = false;

  @Override
  public World getClientWorld() {
    return FMLClientHandler.instance().getClient().theWorld;
  }

  @Override
  public boolean isAnEiInstalled() {
    if (checkedNei) {
      return neiInstalled;
    }
    try {
      Class.forName("crazypants.enderio.nei.EnchanterRecipeHandler");
      neiInstalled = true;
    } catch (Exception e) {
      neiInstalled = false;
    }
    if(!neiInstalled) {
      try {
        Class.forName("crazypants.enderio.jei.AlloyRecipeCategory");
        neiInstalled = true;
      } catch (Exception e) {
        neiInstalled = false;
      }
    }
    checkedNei = true;
    return neiInstalled;
  }

  @Override
  public EntityPlayer getClientPlayer() {
    return Minecraft.getMinecraft().thePlayer;
  }

  @Override
  public void loadIcons() {
    SmartModelAttacher.create();
    PaintRegistry.create();
  }

  @Override
  public void preInit() {
    super.preInit();

    SpecialTooltipHandler tt = SpecialTooltipHandler.INSTANCE;
    tt.addCallback(new TooltipHandlerGrinding());
    tt.addCallback(new TooltipHandlerBurnTime());
    if (Config.addFuelTooltipsToAllFluidContainers) {
      tt.addCallback(new TooltipHandlerFluid());
    }

    //conduits
    ConduitBundleRenderManager.instance.registerRenderers();

    // Fluids
    EnderIO.fluids.registerRenderers();

    // Items of blocks that use smart rendering
    SmartModelAttacher.registerBlockItemModels();

    // Blocks
    if (EnderIO.blockDarkIronBars != null) {
      ClientUtil.registerRenderer(Item.getItemFromBlock(EnderIO.blockDarkIronBars), ModObject.blockDarkIronBars.getUnlocalisedName());
    }
    registerRenderers(EnderIO.blockDarkSteelAnvil);
    if (EnderIO.blockDarkSteelLadder != null) {
      ClientUtil.registerRenderer(Item.getItemFromBlock(EnderIO.blockDarkSteelLadder), ModObject.blockDarkSteelLadder.getUnlocalisedName());
    }
    registerRenderers(EnderIO.blockIngotStorage);
    registerRenderers(EnderIO.blockEndermanSkull);
    registerRenderers(EnderIO.blockElectricLight);

    ClientUtil.registerDefaultItemRenderer(EnderIO.blockTravelPlatform);
    ClientUtil.registerDefaultItemRenderer(EnderIO.blockWirelessCharger);
    ClientUtil.registerDefaultItemRenderer(EnderIO.blockVacuumChest);
    ClientUtil.registerDefaultItemRenderer(EnderIO.blockReinforcedObsidian);
    ClientUtil.registerDefaultItemRenderer(EnderIO.blockDialingDevice);

    //ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), 0, new ModelResourceLocation(getRegistryName(), "inventory"));


    ClientUtil.registerRenderer(Item.getItemFromBlock(EnderIO.blockExitRail), ModObject.blockExitRail.getUnlocalisedName());
    ObeliskRenderManager.INSTANCE.registerRenderers();

    // Tile Renderers
    if (EnderIO.blockEnchanter != null) {
      EnchanterModelRenderer emr = new EnchanterModelRenderer();
      ClientRegistry.bindTileEntitySpecialRenderer(TileEnchanter.class, emr);
      ForgeHooksClient.registerTESRItemStack(Item.getItemFromBlock(EnderIO.blockEnchanter), 0, TileEnchanter.class);
    }
    if (EnderIO.blockFarmStation != null) {
      ClientRegistry.bindTileEntitySpecialRenderer(TileFarmStation.class, new FarmingStationSpecialRenderer());
    }
    if (EnderIO.blockZombieGenerator != null) {
      ClientRegistry.bindTileEntitySpecialRenderer(TileZombieGenerator.class, new ZombieGeneratorRenderer());
    }
    if (EnderIO.blockKillerJoe != null) {
      ClientRegistry.bindTileEntitySpecialRenderer(TileKillerJoe.class, new KillerJoeRenderer());
    }
    if (EnderIO.blockCapBank != null) {
      ClientRegistry.bindTileEntitySpecialRenderer(TileCapBank.class, new CapBankRenderer());
    }
    if (EnderIO.blockEnderIo != null) {
      ClientRegistry.bindTileEntitySpecialRenderer(TileEnderIO.class, new EnderIoRenderer());
      ClientUtil.registerRenderer(Item.getItemFromBlock(EnderIO.blockEnderIo), ModObject.blockEnderIo.getUnlocalisedName());
    }
    if (EnderIO.blockReservoir != null) {
      ClientRegistry.bindTileEntitySpecialRenderer(TileReservoir.class, new ReservoirRenderer(EnderIO.blockReservoir));
    }
    if (EnderIO.blockTank != null) {
      ClientRegistry.bindTileEntitySpecialRenderer(TileTank.class, new TankFluidRenderer());
    }
    if (EnderIO.blockEndermanSkull != null) {
      ClientRegistry.bindTileEntitySpecialRenderer(TileEndermanSkull.class, new EndermanSkullRenderer());
    }
    if (Config.transceiverEnabled) {
      ClientRegistry.bindTileEntitySpecialRenderer(TileTransceiver.class, new TransceiverRenderer());
    }
    ClientRegistry.bindTileEntitySpecialRenderer(TileTravelAnchor.class, new TravelEntitySpecialRenderer<TileTravelAnchor>());

    ClientRegistry.bindTileEntitySpecialRenderer(TileTelePad.class, new TelePadSpecialRenderer());

    ClientRegistry.bindTileEntitySpecialRenderer(TilePowerMonitor.class, new TESRPowerMonitor());

    ClientRegistry.bindTileEntitySpecialRenderer(TileGauge.class, new TESRGauge());

    ClientRegistry.bindTileEntitySpecialRenderer(TileSoulBinder.class, new SoulBinderTESR());

    // Overlays
    new YetaWrenchOverlayRenderer();
    new ConduitProbeOverlayRenderer();

    // Items
    ClientUtil.registerRenderer(EnderIO.itemYetaWench, ModObject.itemYetaWrench.getUnlocalisedName());
    ClientUtil.registerRenderer(EnderIO.itemEnderface, ModObject.itemEnderface.getUnlocalisedName());
    EnderIO.itemAlloy.registerRenderers();
    EnderIO.itemBasicCapacitor.registerRenderers();
    EnderIO.itemPowderIngot.registerRenderers();
    if (EnderIO.itemFrankenSkull != null) {
      EnderIO.itemFrankenSkull.registerRenderers();
    }
    registerRenderers(EnderIO.itemMachinePart);
    registerRenderers(EnderIO.itemMaterial);
    registerRenderers(EnderIO.itemEnderFood);
    registerRenderers(EnderIO.itemBasicFilterUpgrade);
    registerRenderers(EnderIO.itemExtractSpeedUpgrade);
    registerRenderers(EnderIO.itemFunctionUpgrade);
    registerRenderers(EnderIO.itemFunctionUpgrade);
    registerRenderers(EnderIO.itemSoulVessel);
    registerRenderers(EnderIO.itemConduitProbe);
    registerRenderers(EnderIO.itemPowerConduit);
    registerRenderers(EnderIO.itemLiquidConduit);
    registerRenderers(EnderIO.itemItemConduit);
    registerRenderers(EnderIO.itemRedstoneConduit);
    registerRenderers(EnderIO.itemOCConduit);
    ClientUtil.registerRenderer(EnderIO.itemlocationPrintout, ItemLocationPrintout.NAME);
    ClientUtil.registerRenderer(EnderIO.itemTravelStaff, ModObject.itemTravelStaff.getUnlocalisedName());
    ClientUtil.registerRenderer(EnderIO.itemRodOfReturn, ModObject.itemRodOfReturn.getUnlocalisedName());
    ClientUtil.registerRenderer(EnderIO.itemXpTransfer, ModObject.itemXpTransfer.getUnlocalisedName());
    ClientUtil.registerRenderer(EnderIO.itemBrokenSpawner, ModObject.itemBrokenSpawner.getUnlocalisedName());
    ClientUtil.registerRenderer(EnderIO.itemExistingItemFilter, ModObject.itemExistingItemFilter.getUnlocalisedName());
    ClientUtil.registerRenderer(EnderIO.itemModItemFilter, ModObject.itemModItemFilter.getUnlocalisedName());
    ClientUtil.registerRenderer(EnderIO.itemPowerItemFilter, ModObject.itemPowerItemFilter.getUnlocalisedName());
    ClientUtil.registerRenderer(EnderIO.itemConduitProbe, ModObject.itemConduitProbe.getUnlocalisedName());
    ClientUtil.registerRenderer(EnderIO.itemCoordSelector, ModObject.itemCoordSelector.getUnlocalisedName());
    DarkSteelItems.onClientPreInit();
    Buckets.registerRenderers();
    EnderIO.itemRemoteInvAccess.registerRenderers();

    // Item Models
    ItemModelRegistry.create();
    // ItemModelRegistry.registerRotating("enderCrystal", 2);

    // Listeners
    if (Config.useSneakMouseWheelYetaWrench) {
      ToolTickHandler th = new ToolTickHandler();
      MinecraftForge.EVENT_BUS.register(th);
    }
    MinecraftForge.EVENT_BUS.register(TravelController.instance);
    MinecraftForge.EVENT_BUS.register(KeyTracker.instance);
    MinecraftForge.EVENT_BUS.register(SoundDetector.instance);
    MinecraftForge.EVENT_BUS.register(UpgradeRenderDispatcher.instance);
    MinecraftForge.EVENT_BUS.register(new TeleportEntityRenderHandler());
  }

  @Override
  public void init() {
    super.init();
    SmartModelAttacher.registerColoredBlocksAndItems();
    MinecraftForge.EVENT_BUS.register(ClientNetworkManager.getInstance());
  }

  private void registerRenderers(IHaveRenderers bob) {
    if (bob != null) {
      bob.registerRenderers();
    }
  }

  @Override
  public double getReachDistanceForPlayer(EntityPlayer entityPlayer) {
    if (entityPlayer instanceof EntityPlayerMP) {
      return ((EntityPlayerMP) entityPlayer).interactionManager.getBlockReachDistance();
    }
    return super.getReachDistanceForPlayer(entityPlayer);
  }

  @Override
  public void setInstantConfusionOnPlayer(EntityPlayer ent, int duration) {
    ent.addPotionEffect(new PotionEffect(MobEffects.NAUSEA, duration, 1, true, true));
    Minecraft.getMinecraft().thePlayer.timeInPortal = 1;
  }

  @Override
  public long getTickCount() {
    return clientTickCount;
  }

  @Override
  protected void onClientTick() {
    if (!Minecraft.getMinecraft().isGamePaused() && Minecraft.getMinecraft().theWorld != null) {
      ++clientTickCount;
      YetaUtil.onClientTick();
    }
  }

  @Override
  public void markBlock(World worldObj, BlockPos pos, Vector4f color) {
    Minecraft.getMinecraft().effectRenderer.addEffect(new MarkerParticle(worldObj, pos, color));
  }

  @Override
  protected void registerCommands() {
    ClientCommandHandler.instance.registerCommand(DebugCommand.CLIENT);
  }

  @Override
  public boolean isDedicatedServer() {
    return false;
  }

  @Override
  public CreativeTabs getCreativeTab(ItemStack stack) {
    return stack == null || stack.getItem() == null ? null : stack.getItem().getCreativeTab();
  }

  @Override
  public void getSubItems(Item itemIn, CreativeTabs tab, List<ItemStack> subItems) {
    itemIn.getSubItems(itemIn, tab, subItems);
  }

}
