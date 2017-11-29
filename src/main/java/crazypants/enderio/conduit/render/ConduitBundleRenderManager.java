package crazypants.enderio.conduit.render;

import crazypants.enderio.base.conduit.geom.ConduitConnectorType;
import crazypants.enderio.base.conduit.registry.ConduitRegistry;
import crazypants.enderio.base.conduit.registry.ConduitRegistry.ConduitInfo;
import crazypants.enderio.base.render.registry.TextureRegistry;
import crazypants.enderio.base.render.registry.TextureRegistry.TextureSupplier;
import crazypants.enderio.conduit.TileConduitBundle;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ConduitBundleRenderManager {

  public static final ConduitBundleRenderManager instance = new ConduitBundleRenderManager();

  private final ConduitBundleRenderer cbr = new ConduitBundleRenderer();

  public static final TextureSupplier connectorIconExternal = TextureRegistry.registerTexture("blocks/conduitConnector");

  public static final TextureSupplier connectorIcon = TextureRegistry.registerTexture("blocks/conduitConnector"); // TODO: is this even used?

  public static final TextureSupplier wireFrameIcon = TextureRegistry.registerTexture("blocks/wireFrame");

  public void init(FMLPreInitializationEvent event) {
    ClientRegistry.bindTileEntitySpecialRenderer(TileConduitBundle.class, cbr);
  }

  public void init(FMLPostInitializationEvent event) {
    for (ConduitInfo conduitInfo : ConduitRegistry.getAll()) {
      for (ConduitRenderer renderer : conduitInfo.getRenderers()) {
        renderer.initIcons();
        cbr.registerRenderer(renderer);
      }
    }
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
