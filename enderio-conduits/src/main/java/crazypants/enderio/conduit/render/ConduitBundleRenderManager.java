package crazypants.enderio.conduit.render;

import javax.annotation.Nonnull;

import crazypants.enderio.base.conduit.geom.ConduitConnectorType;
import crazypants.enderio.base.render.registry.TextureRegistry;
import crazypants.enderio.base.render.registry.TextureRegistry.TextureSupplier;
import crazypants.enderio.conduit.TileConduitBundle;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ConduitBundleRenderManager {

  public static final @Nonnull ConduitBundleRenderManager instance = new ConduitBundleRenderManager();

  private final @Nonnull ConduitBundleRenderer cbr = new ConduitBundleRenderer();

  public static final @Nonnull TextureSupplier connectorIconExternal = TextureRegistry.registerTexture("blocks/conduit_connector");

  public static final @Nonnull TextureSupplier connectorIcon = TextureRegistry.registerTexture("blocks/conduit_connector"); // TODO: is this even used?

  public static final @Nonnull TextureSupplier wireFrameIcon = TextureRegistry.registerTexture("blocks/wire_frame");

  public void init(FMLPreInitializationEvent event) {
    ClientRegistry.bindTileEntitySpecialRenderer(TileConduitBundle.class, cbr);
  }

  public @Nonnull TextureAtlasSprite getConnectorIcon(Object data) {
    return (data == ConduitConnectorType.EXTERNAL ? connectorIconExternal : connectorIcon).get(TextureAtlasSprite.class);
  }

  public @Nonnull TextureAtlasSprite getWireFrameIcon() {
    return wireFrameIcon.get(TextureAtlasSprite.class);
  }

  public ConduitBundleRenderer getConduitBundleRenderer() {
    return cbr;
  }

}
