package crazypants.enderio;

import java.util.ArrayList;
import java.util.List;

import com.enderio.core.client.handlers.SpecialTooltipHandler;

import crazypants.enderio.conduit.IConduit;
import crazypants.enderio.conduit.TileConduitBundle;
import crazypants.enderio.conduit.render.ConduitBundleRenderer;
import crazypants.enderio.conduit.render.ConduitRenderer;
import crazypants.enderio.conduit.render.DefaultConduitRenderer;
import crazypants.enderio.config.Config;
import crazypants.enderio.enderface.EnderIoRenderer;
import crazypants.enderio.enderface.TileEnderIO;
import crazypants.enderio.gui.TooltipHandlerBurnTime;
import crazypants.enderio.gui.TooltipHandlerFluid;
import crazypants.enderio.gui.TooltipHandlerGrinding;
import crazypants.enderio.item.KeyTracker;
import crazypants.enderio.item.ToolTickHandler;
import crazypants.enderio.item.YetaWrenchOverlayRenderer;
import crazypants.enderio.item.darksteel.DarkSteelItems;
import crazypants.enderio.item.darksteel.SoundDetector;
import crazypants.enderio.item.darksteel.SoundEntity;
import crazypants.enderio.item.darksteel.SoundRenderer;
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
import crazypants.enderio.machine.ranged.RangeEntity;
import crazypants.enderio.machine.ranged.RangeRenerer;
import crazypants.enderio.machine.tank.TankFluidRenderer;
import crazypants.enderio.machine.tank.TileTank;
import crazypants.enderio.teleport.TravelController;
import crazypants.enderio.teleport.anchor.TileTravelAnchor;
import crazypants.enderio.teleport.anchor.TravelEntitySpecialRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
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
//    RedstoneConduit.initIcons();
//    InsulatedRedstoneConduit.initIcons();
//    RedstoneSwitch.initIcons();
//    PowerConduit.initIcons();
//    LiquidConduit.initIcons();
//    AdvancedLiquidConduit.initIcons();
//    EnderLiquidConduit.initIcons();
//    ItemConduit.initIcons();
//    if(GasUtil.isGasConduitEnabled()) {
//      GasConduit.initIcons();
//    }
//    if(MEUtil.isMEEnabled()) {
//      MEConduit.initIcons();
//    }
//    if (OCUtil.isOCEnabled()) {
//      OCConduit.initIcons();
//    }
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

    EnchanterModelRenderer emr = new EnchanterModelRenderer();
    ClientRegistry.bindTileEntitySpecialRenderer(TileEnchanter.class, emr);

    ClientRegistry.bindTileEntitySpecialRenderer(TileFarmStation.class, new FarmingStationSpecialRenderer());

    ZombieGeneratorRenderer zgr = new ZombieGeneratorRenderer();
    ClientRegistry.bindTileEntitySpecialRenderer(TileZombieGenerator.class, zgr);

    KillerJoeRenderer kjr = new KillerJoeRenderer();
    ClientRegistry.bindTileEntitySpecialRenderer(TileKillerJoe.class, kjr);
    
//    OBJLoader.instance.addDomain(EnderIO.MODID.toLowerCase());
//    Item item = Item.getItemFromBlock(EnderIO.blockTransceiver);
//    ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation(EnderIO.MODID.toLowerCase() + ":" + "models/transceiver.obj", "inventory"));

//    regRendererRes()
    
    if(EnderIO.blockCapBank != null) {
      CapBankRenderer newCbr = new CapBankRenderer();
      ClientRegistry.bindTileEntitySpecialRenderer(TileCapBank.class, newCbr);
    }

//    TelePadRenderer telePadRenderer = new TelePadRenderer();
//    ClientRegistry.bindTileEntitySpecialRenderer(TileTelePad.class, new TelePadSpecialRenderer(telePadRenderer));

    cbr = new ConduitBundleRenderer((float) Config.conduitScale);
    ClientRegistry.bindTileEntitySpecialRenderer(TileConduitBundle.class, cbr);

    ClientRegistry.bindTileEntitySpecialRenderer(TileTravelAnchor.class, new TravelEntitySpecialRenderer());

//    conduitRenderers.add(RedstoneSwitchRenderer.getInstance());
//    conduitRenderers.add(new AdvancedLiquidConduitRenderer());
//    conduitRenderers.add(LiquidConduitRenderer.create());
//    conduitRenderers.add(new PowerConduitRenderer());
//    conduitRenderers.add(new InsulatedRedstoneConduitRenderer());
//    conduitRenderers.add(new EnderLiquidConduitRenderer());
//    conduitRenderers.add(new crazypants.enderio.conduit.item.ItemConduitRenderer());    
//    if (OCUtil.isOCEnabled()) {
//      conduitRenderers.add(new OCConduitRenderer());
//    }

    EnderIoRenderer eior = new EnderIoRenderer();
    ClientRegistry.bindTileEntitySpecialRenderer(TileEnderIO.class, eior);

//    ClientRegistry.bindTileEntitySpecialRenderer(TileReservoir.class, new ReservoirRenderer(EnderIO.blockReservoir));
    ClientRegistry.bindTileEntitySpecialRenderer(TileTank.class, new TankFluidRenderer());

//    if(Config.transceiverEnabled) {
//      TransceiverRenderer tr = new TransceiverRenderer();
//      ClientRegistry.bindTileEntitySpecialRenderer(TileTransceiver.class, tr);
//    }

    new YetaWrenchOverlayRenderer();
//    new ConduitProbeOverlayRenderer();
    if(Config.useSneakMouseWheelYetaWrench) {
      ToolTickHandler th = new ToolTickHandler();
      MinecraftForge.EVENT_BUS.register(th);      
    }
    MinecraftForge.EVENT_BUS.register(TravelController.instance);    

    DarkSteelItems.registerItemRenderer();

    MinecraftForge.EVENT_BUS.register(KeyTracker.instance);

    RenderingRegistry.registerEntityRenderingHandler(SoundEntity.class, SoundRenderer.FACTORY);
    RenderingRegistry.registerEntityRenderingHandler(RangeEntity.class, RangeRenerer.FACTORY);

    MinecraftForge.EVENT_BUS.register(SoundDetector.instance);    

//    if(!Loader.isModLoaded("OpenBlocks")) {
//      //We have registered liquid XP so we need to give it textures
//      IconUtil.addIconProvider(new IconUtil.IIconProvider() {
//
//        @Override
//        public void registerIcons(IIconRegister register) {
//          //NB: textures re-used with permission from OpenBlocks to maintain look
//          IIcon flowing = register.registerIcon("enderio:xpjuiceflowing");
//          IIcon still = register.registerIcon("enderio:xpjuicestill");
//          EnderIO.fluidXpJuice.setIcons(still, flowing);
//        }
//
//        @Override
//        public int getTextureType() {
//          return 0;
//        }
//
//      });
//    }

//    MinecraftForge.EVENT_BUS.register(new TeleportEntityRenderHandler());
  }
  
  private void regRenderer(Item item, int meta, String name) {
    regRenderer(item, meta, EnderIO.MODID, name);
  }

  private void regRenderer(Item item, int meta, String modId, String name) {    
    String resourceName;
    if (modId != null) {
      resourceName = modId + ":" + name;
    } else {
      resourceName = name;
    }
    regRendererRes(item, meta, resourceName);
  }

  private void regRendererRes(Item item, int meta, String resourceName) {
    RenderItem renderItem = Minecraft.getMinecraft().getRenderItem();
    renderItem.getItemModelMesher().register(item, meta, new ModelResourceLocation(resourceName, "inventory"));
  }

  private void regRenderer(Item item, String name) {
    regRenderer(item, 0, name);
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
    ent.addPotionEffect(new PotionEffect(Potion.confusion.getId(), duration, 1, true, true));
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
