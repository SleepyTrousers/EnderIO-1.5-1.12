package crazypants.enderio.conduit.render;

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
import crazypants.enderio.conduit.oc.OCConduit;
import crazypants.enderio.conduit.oc.OCConduitRenderer;
import crazypants.enderio.conduit.power.PowerConduit;
import crazypants.enderio.conduit.power.PowerConduitRenderer;
import crazypants.enderio.conduit.redstone.InsulatedRedstoneConduit;
import crazypants.enderio.conduit.redstone.InsulatedRedstoneConduitRenderer;
import crazypants.enderio.render.TextureRegistry;
import crazypants.enderio.render.TextureRegistry.TextureSupplier;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ConduitBundleRenderManager {

  public static final ConduitBundleRenderManager instance = new ConduitBundleRenderManager();

  private final ConduitBundleRenderer cbr = new ConduitBundleRenderer();

  public static final TextureSupplier connectorIconExternal = TextureRegistry.registerTexture("blocks/conduitConnector");

  public static final TextureSupplier connectorIcon = TextureRegistry.registerTexture("blocks/conduitConnectorExternal");

  public static final TextureSupplier wireFrameIcon = TextureRegistry.registerTexture("blocks/wireFrame");

  public void registerRenderers() {
    
    InsulatedRedstoneConduit.initIcons();    
    PowerConduit.initIcons();
    LiquidConduit.initIcons();
    AdvancedLiquidConduit.initIcons();
    EnderLiquidConduit.initIcons();
    ItemConduit.initIcons();
    OCConduit.initIcons();
            
    cbr.registerRenderer(new AdvancedLiquidConduitRenderer());
    cbr.registerRenderer(LiquidConduitRenderer.create());
    cbr.registerRenderer(new PowerConduitRenderer());
    cbr.registerRenderer(new InsulatedRedstoneConduitRenderer());
    cbr.registerRenderer(new ItemConduitRenderer());  
    cbr.registerRenderer(new EnderLiquidConduitRenderer());
    cbr.registerRenderer(new OCConduitRenderer());

    ClientRegistry.bindTileEntitySpecialRenderer(TileConduitBundle.class, cbr);
  }

  public TextureAtlasSprite getConnectorIcon(Object data) {
    return (data == ConduitConnectorType.EXTERNAL ? connectorIconExternal : connectorIcon).get(TextureAtlasSprite.class);
  }

  public TextureAtlasSprite getWireFrameIcon() {    
    return wireFrameIcon.get(TextureAtlasSprite.class);
  }

  public ConduitBundleRenderer getConduitBundleRenderer() {
    return cbr;
  }

}
