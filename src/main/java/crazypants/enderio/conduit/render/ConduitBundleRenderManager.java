package crazypants.enderio.conduit.render;

import crazypants.enderio.EnderIO;
import crazypants.enderio.conduit.TileConduitBundle;
import crazypants.enderio.conduit.geom.ConduitConnectorType;
import crazypants.enderio.conduit.item.ItemConduit;
import crazypants.enderio.conduit.item.ItemConduitRenderer;
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
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ConduitBundleRenderManager {

  public static final ConduitBundleRenderManager instance = new ConduitBundleRenderManager();

  private final ConduitBundleRenderer cbr = new ConduitBundleRenderer();
  private ModelResourceLocation modelLocation = new ModelResourceLocation("enderio:blockConduitBundle");

  private TextureAtlasSprite connectorIconExternal;

  private TextureAtlasSprite connectorIcon;

  private TextureAtlasSprite wireFrameIcon;

  public void registerRenderers() {

    RedstoneConduit.initIcons();
    InsulatedRedstoneConduit.initIcons();
    RedstoneSwitch.initIcons();
    PowerConduit.initIcons();
    LiquidConduit.initIcons();
    AdvancedLiquidConduit.initIcons();
    EnderLiquidConduit.initIcons();
    ItemConduit.initIcons();
        
    cbr.registerRenderer(RedstoneSwitchRenderer.getInstance());
    cbr.registerRenderer(new AdvancedLiquidConduitRenderer());
    cbr.registerRenderer(LiquidConduitRenderer.create());
    cbr.registerRenderer(new PowerConduitRenderer());
    cbr.registerRenderer(new InsulatedRedstoneConduitRenderer());
    cbr.registerRenderer(new ItemConduitRenderer());  
    cbr.registerRenderer(new EnderLiquidConduitRenderer());

    ClientRegistry.bindTileEntitySpecialRenderer(TileConduitBundle.class, cbr);

    StateMapperBase ignoreState = new StateMapperBase() {
      @Override
      protected ModelResourceLocation getModelResourceLocation(IBlockState iBlockState) {
        return modelLocation;
      }
    };
    ModelLoader.setCustomStateMapper(EnderIO.blockConduitBundle, ignoreState);
    MinecraftForge.EVENT_BUS.register(this);
  }

  @SubscribeEvent
  public void onModelBakeEvent(ModelBakeEvent event) {
    IBakedModel defaultBakedModel = event.modelRegistry.getObject(modelLocation);
    ConduitBundleBakedModel model = new ConduitBundleBakedModel(defaultBakedModel);
    event.modelRegistry.putObject(modelLocation, model);
  }

  @SideOnly(Side.CLIENT)
  @SubscribeEvent
  public void onIconLoad(TextureStitchEvent.Pre event) {
    connectorIconExternal = event.map.registerSprite(new ResourceLocation(EnderIO.MODID, "blocks/conduitConnector"));
    connectorIcon = event.map.registerSprite(new ResourceLocation(EnderIO.MODID, "blocks/conduitConnectorExternal"));
    wireFrameIcon = event.map.registerSprite(new ResourceLocation(EnderIO.MODID, "blocks/wireFrame"));
  }

  public TextureAtlasSprite getConnectorIcon(Object data) {
    return data == ConduitConnectorType.EXTERNAL ? connectorIconExternal : connectorIcon;
  }

  public TextureAtlasSprite getWireFrameIcon() {    
    return wireFrameIcon;
  }

  public ConduitBundleRenderer getConduitBundleRenderer() {
    return cbr;
  }

}
