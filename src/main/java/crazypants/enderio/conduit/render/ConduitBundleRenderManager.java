package crazypants.enderio.conduit.render;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
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

@SideOnly(Side.CLIENT)
public class ConduitBundleRenderManager {

  public static final ConduitBundleRenderManager instance = new ConduitBundleRenderManager();

  private final ConduitBundleRenderer cbr = new ConduitBundleRenderer();

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

    MinecraftForge.EVENT_BUS.register(this);
  }

  @SideOnly(Side.CLIENT)
  @SubscribeEvent
  public void onIconLoad(TextureStitchEvent.Pre event) {
    // TODO use registry
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
