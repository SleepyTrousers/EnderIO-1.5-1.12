package crazypants.enderio;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.enderio.core.client.handlers.SpecialTooltipHandler;

import crazypants.enderio.conduit.render.ConduitBundleRenderManager;
import crazypants.enderio.config.Config;
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
import crazypants.enderio.item.darksteel.SoundEntity;
import crazypants.enderio.item.darksteel.SoundRenderer;
import crazypants.enderio.item.skull.EndermanSkullRenderer;
import crazypants.enderio.item.skull.TileEndermanSkull;
import crazypants.enderio.machine.capbank.TileCapBank;
import crazypants.enderio.machine.capbank.render.CapBankRenderer;
import crazypants.enderio.machine.enchanter.EnchanterModelRenderer;
import crazypants.enderio.machine.enchanter.TileEnchanter;
import crazypants.enderio.machine.farm.FarmingStationSpecialRenderer;
import crazypants.enderio.machine.farm.TileFarmStation;
import crazypants.enderio.machine.generator.zombie.TileZombieGenerator;
import crazypants.enderio.machine.generator.zombie.ZombieGeneratorRenderer;
import crazypants.enderio.machine.killera.KillerJoeRenderer;
import crazypants.enderio.machine.killera.TileKillerJoe;
import crazypants.enderio.machine.obelisk.render.ObeliskRenderManager;
import crazypants.enderio.machine.ranged.RangeEntity;
import crazypants.enderio.machine.ranged.RangeRenerer;
import crazypants.enderio.machine.reservoir.ReservoirRenderer;
import crazypants.enderio.machine.reservoir.TileReservoir;
import crazypants.enderio.machine.tank.TankFluidRenderer;
import crazypants.enderio.machine.tank.TileTank;
import crazypants.enderio.machine.transceiver.TileTransceiver;
import crazypants.enderio.machine.transceiver.render.TransceiverRenderer;
import crazypants.enderio.render.SmartModelAttacher;
import crazypants.enderio.teleport.TravelController;
import crazypants.enderio.teleport.anchor.TileTravelAnchor;
import crazypants.enderio.teleport.anchor.TravelEntitySpecialRenderer;
import crazypants.util.ClientUtil;

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
  public boolean isNeiInstalled() {
    if (checkedNei) {
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

  @Override
  public void loadIcons() {    
    SmartModelAttacher.create();
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
      ClientUtil.registerRenderer(Item.getItemFromBlock(EnderIO.blockDarkIronBars), ModObject.blockDarkIronBars.unlocalisedName);
    }
    if (EnderIO.blockDarkSteelAnvil != null) {
      EnderIO.blockDarkSteelAnvil.registerRenderers();
    }
    if (EnderIO.blockDarkSteelLadder != null) {
      ClientUtil.registerRenderer(Item.getItemFromBlock(EnderIO.blockDarkSteelLadder), ModObject.blockDarkSteelLadder.unlocalisedName);
    }   
    if (EnderIO.blockDarkSteelPressurePlate != null) {
      EnderIO.blockDarkSteelPressurePlate.registerRenderers();
    }
    if (EnderIO.blockIngotStorage != null) {
      EnderIO.blockIngotStorage.registerRenderers();
    }
    if (EnderIO.blockEndermanSkull != null) {
      EnderIO.blockEndermanSkull.registerRenderers();
    }
    ClientUtil.registerDefaultItemRenderer(EnderIO.blockTravelPlatform);
    ClientUtil.registerDefaultItemRenderer(EnderIO.blockWirelessCharger);
    ClientUtil.registerDefaultItemRenderer(EnderIO.blockVacuumChest);  
    ClientUtil.registerDefaultItemRenderer(EnderIO.blockReinforcedObsidian);
    ObeliskRenderManager.INSTANCE.registerRenderers();

    // Tile Renderers

    if (EnderIO.blockEnchanter != null) {
      EnchanterModelRenderer emr = new EnchanterModelRenderer();
      ClientRegistry.bindTileEntitySpecialRenderer(TileEnchanter.class, emr);      
      ForgeHooksClient.registerTESRItemStack(Item.getItemFromBlock(EnderIO.blockEnchanter), 0, TileEnchanter.class);
      ClientUtil.registerRenderer(Item.getItemFromBlock(EnderIO.blockEnchanter), ModObject.blockEnchanter.unlocalisedName);
    }
    if (EnderIO.blockFarmStation != null) {
      ClientRegistry.bindTileEntitySpecialRenderer(TileFarmStation.class, new FarmingStationSpecialRenderer());
    }
    if (EnderIO.blockZombieGenerator != null) {
      ZombieGeneratorRenderer zgr = new ZombieGeneratorRenderer();
      ClientRegistry.bindTileEntitySpecialRenderer(TileZombieGenerator.class, zgr);
      ForgeHooksClient.registerTESRItemStack(Item.getItemFromBlock(EnderIO.blockZombieGenerator), 0, TileZombieGenerator.class);
      ClientUtil.registerRenderer(Item.getItemFromBlock(EnderIO.blockZombieGenerator), ModObject.blockZombieGenerator.unlocalisedName);            
    }
    if (EnderIO.blockKillerJoe != null) {
      KillerJoeRenderer kjr = new KillerJoeRenderer();
      ClientRegistry.bindTileEntitySpecialRenderer(TileKillerJoe.class, kjr);
      ForgeHooksClient.registerTESRItemStack(Item.getItemFromBlock(EnderIO.blockKillerJoe), 0, TileKillerJoe.class);
      ClientUtil.registerRenderer(Item.getItemFromBlock(EnderIO.blockKillerJoe), ModObject.blockKillerJoe.unlocalisedName);
    }
    if (EnderIO.blockCapBank != null) {
      CapBankRenderer newCbr = new CapBankRenderer();
      ClientRegistry.bindTileEntitySpecialRenderer(TileCapBank.class, newCbr);
    }
    if (EnderIO.blockEnderIo != null) {
      EnderIoRenderer eior = new EnderIoRenderer();
      ClientRegistry.bindTileEntitySpecialRenderer(TileEnderIO.class, eior);      
      ClientUtil.registerRenderer(Item.getItemFromBlock(EnderIO.blockEnderIo), ModObject.blockEnderIo.unlocalisedName);
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
      TransceiverRenderer tr = new TransceiverRenderer();
      ClientRegistry.bindTileEntitySpecialRenderer(TileTransceiver.class, tr);
    }
    ClientRegistry.bindTileEntitySpecialRenderer(TileTravelAnchor.class, new TravelEntitySpecialRenderer());

    // OBJLoader.instance.addDomain(EnderIO.MODID.toLowerCase());
    // Item item = Item.getItemFromBlock(EnderIO.blockTransceiver);
    // ModelLoader.setCustomModelResourceLocation(item, 0, new
    // ModelResourceLocation(EnderIO.MODID.toLowerCase() + ":" +
    // "models/transceiver.obj", "inventory"));

    // TelePadRenderer telePadRenderer = new TelePadRenderer();
    // ClientRegistry.bindTileEntitySpecialRenderer(TileTelePad.class, new
    // TelePadSpecialRenderer(telePadRenderer));

    

    // Overlays
    new YetaWrenchOverlayRenderer();
    new ConduitProbeOverlayRenderer();

    // Items
    ClientUtil.registerRenderer(EnderIO.itemYetaWench, ModObject.itemYetaWrench.unlocalisedName);
    ClientUtil.registerRenderer(EnderIO.itemEnderface, ModObject.itemEnderface.unlocalisedName);
    EnderIO.itemAlloy.registerRenderers();
    EnderIO.itemBasicCapacitor.registerRenderers();
    EnderIO.itemPowderIngot.registerRenderers();
    if (EnderIO.itemFrankenSkull != null) {
      EnderIO.itemFrankenSkull.registerRenderers();
    }
    EnderIO.itemMachinePart.registerRenderers();
    EnderIO.itemMaterial.registerRenderers();
    EnderIO.itemEnderFood.registerRenderers();
    EnderIO.itemBasicFilterUpgrade.registerRenderers();
    EnderIO.itemExtractSpeedUpgrade.registerRenderers();
    if (EnderIO.itemFunctionUpgrade != null) {
      EnderIO.itemFunctionUpgrade.registerRenderers();
    }
    if (EnderIO.itemSoulVessel != null) {
      EnderIO.itemSoulVessel.registerRenderers();
    }
    if (EnderIO.itemPowerConduit != null) {
      EnderIO.itemPowerConduit.registerRenderers();
    }
    if (EnderIO.itemLiquidConduit != null) {
      EnderIO.itemLiquidConduit.registerRenderers();
    }
    if (EnderIO.itemItemConduit != null) {
      EnderIO.itemItemConduit.registerRenderers();
    }
    if (EnderIO.itemRedstoneConduit != null) {
      EnderIO.itemRedstoneConduit.registerRenderers();
    }
    ClientUtil.registerRenderer(EnderIO.itemTravelStaff, ModObject.itemTravelStaff.unlocalisedName);
    ClientUtil.registerRenderer(EnderIO.itemXpTransfer, ModObject.itemXpTransfer.unlocalisedName);
    ClientUtil.registerRenderer(EnderIO.itemBrokenSpawner, ModObject.itemBrokenSpawner.unlocalisedName);
    ClientUtil.registerRenderer(EnderIO.itemExistingItemFilter, ModObject.itemExistingItemFilter.unlocalisedName);
    ClientUtil.registerRenderer(EnderIO.itemModItemFilter, ModObject.itemModItemFilter.unlocalisedName);
    ClientUtil.registerRenderer(EnderIO.itemPowerItemFilter, ModObject.itemPowerItemFilter.unlocalisedName);
    ClientUtil.registerRenderer(EnderIO.itemConduitProbe, ModObject.itemConduitProbe.unlocalisedName);
    ClientUtil.registerRenderer(EnderIO.itemCoordSelector, ModObject.itemCoordSelector.unlocalisedName);
    DarkSteelItems.registerItemRenderers();
    Buckets.registerRenderers();

    // Entities
    RenderingRegistry.registerEntityRenderingHandler(SoundEntity.class, SoundRenderer.FACTORY);
    RenderingRegistry.registerEntityRenderingHandler(RangeEntity.class, RangeRenerer.FACTORY);

    // Listeners
    if (Config.useSneakMouseWheelYetaWrench) {
      ToolTickHandler th = new ToolTickHandler();
      MinecraftForge.EVENT_BUS.register(th);
    }
    MinecraftForge.EVENT_BUS.register(TravelController.instance);
    MinecraftForge.EVENT_BUS.register(KeyTracker.instance);
    MinecraftForge.EVENT_BUS.register(SoundDetector.instance);

  }

  @Override
  public double getReachDistanceForPlayer(EntityPlayer entityPlayer) {
    if (entityPlayer instanceof EntityPlayerMP) {
      return ((EntityPlayerMP) entityPlayer).theItemInWorldManager.getBlockReachDistance();
    }
    return super.getReachDistanceForPlayer(entityPlayer);
  }

  @Override
  public void setInstantConfusionOnPlayer(EntityPlayer ent, int duration) {
    ent.addPotionEffect(new PotionEffect(Potion.confusion.getId(), duration, 1, true, true));
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
    }
  }

}
