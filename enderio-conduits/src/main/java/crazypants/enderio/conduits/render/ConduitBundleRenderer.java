package crazypants.enderio.conduits.render;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Nonnull;

import org.lwjgl.opengl.GL11;

import com.enderio.core.client.render.BoundingBox;
import com.enderio.core.client.render.RenderUtil;
import com.enderio.core.common.util.NNList;
import com.enderio.core.common.util.NullHelper;

import crazypants.enderio.base.conduit.ConnectionMode;
import crazypants.enderio.base.conduit.IClientConduit;
import crazypants.enderio.base.conduit.IClientConduit.WithDefaultRendering;
import crazypants.enderio.base.conduit.IConduit;
import crazypants.enderio.base.conduit.IConduitBundle;
import crazypants.enderio.base.conduit.IConduitRenderer;
import crazypants.enderio.base.conduit.IConduitTexture;
import crazypants.enderio.base.conduit.geom.CollidableComponent;
import crazypants.enderio.base.conduit.geom.ConduitConnectorType;
import crazypants.enderio.base.conduit.geom.ConduitGeometryUtil;
import crazypants.enderio.base.paint.YetaUtil;
import crazypants.enderio.base.render.IBlockStateWrapper;
import crazypants.enderio.conduits.conduit.TileConduitBundle;
import crazypants.enderio.conduits.config.ConduitConfig;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.EnumSkyBlock;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ConduitBundleRenderer extends TileEntitySpecialRenderer<TileConduitBundle> {

  private final @Nonnull List<IConduitRenderer> conduitRenderers = new ArrayList<IConduitRenderer>();
  private final @Nonnull List<IConduitRenderer> dynamicCnduitRenderers = new ArrayList<IConduitRenderer>();
  private final @Nonnull DefaultConduitRenderer dcr = new DefaultConduitRenderer();

  public ConduitBundleRenderer() {
  }

  public void registerRenderer(IConduitRenderer renderer) {
    conduitRenderers.add(renderer);
    if (renderer.isDynamic()) {
      dynamicCnduitRenderers.add(renderer);
    }
  }

  // TESR rendering

  @Override
  public void render(@Nonnull TileConduitBundle te, double x, double y, double z, float partialTick, int b, float alpha) {

    IConduitBundle bundle = te;
    EntityPlayerSP player = Minecraft.getMinecraft().player;
    if (bundle.hasFacade() && !YetaUtil.isFacadeHidden(bundle, player)) {
      final IBlockState paintSource = bundle.getPaintSource();
      if (paintSource != null && paintSource.isOpaqueCube()) {
        return;
      }
    }

    NNList<RenderPair> renderers = getDynamicRenderersForConduitBundle(bundle, player);
    if (renderers.isEmpty()) {
      return;
    }

    float brightness = bundle.getEntity().getWorld().getLightFor(EnumSkyBlock.SKY, bundle.getLocation());

    RenderUtil.setupLightmapCoords(te.getPos(), te.getWorld());
    RenderUtil.bindBlockTexture();
    RenderHelper.disableStandardItemLighting();
    GlStateManager.disableLighting();
    GlStateManager.enableNormalize();
    GlStateManager.enableBlend();
    GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
    GlStateManager.shadeModel(GL11.GL_SMOOTH);

    GlStateManager.pushMatrix();
    GlStateManager.translate(x, y, z);

    Tessellator tessellator = Tessellator.getInstance();
    BufferBuilder tes = tessellator.getBuffer();
    tes.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);

    for (RenderPair pair : renderers) {
      pair.getRenderer().renderDynamicEntity(this, bundle, pair.getConduit(), x, y, z, partialTick, brightness);
    }

    Tessellator.getInstance().draw();
    GlStateManager.disableNormalize();
    GlStateManager.disableBlend();
    GlStateManager.shadeModel(GL11.GL_FLAT);
    RenderHelper.enableStandardItemLighting();
    GlStateManager.popMatrix();
  }

  // ------------ Block Model building

  public @Nonnull List<BakedQuad> getGeneralQuads(@Nonnull IBlockStateWrapper state, BlockRenderLayer layer) {

    List<BakedQuad> result = new ArrayList<BakedQuad>();
    TileEntity tileEntity = state.getTileEntity();

    if (tileEntity instanceof TileConduitBundle) {
      IConduitBundle bundle = (IConduitBundle) tileEntity;
      if (layer == null) {
        addBreakingQuads(bundle, result);
      } else {
        float brightness;
        if (!ConduitConfig.updateLightingWhenHidingFacades.get() && bundle.hasFacade()) {
          brightness = 15 << 20 | 15 << 4;
        } else {
          brightness = bundle.getEntity().getWorld().getLightFor(EnumSkyBlock.SKY, bundle.getLocation());
        }

        // TODO: check if this is the client thread, if not, make a copy of the bundle and its conduits in a thread-safe way
        addConduitQuads(state, bundle, brightness, layer, result);
      }
    }

    return result;
  }

  private void addConduitQuads(@Nonnull IBlockStateWrapper state, @Nonnull IConduitBundle bundle, float brightness, @Nonnull BlockRenderLayer layer,
      @Nonnull List<BakedQuad> quads) {

    // Conduits
    Set<EnumFacing> externals = new HashSet<EnumFacing>();
    List<BoundingBox> wireBounds = new ArrayList<BoundingBox>();

    if (layer == BlockRenderLayer.CUTOUT && bundle.hasFacade() && state.getYetaDisplayMode().isHideFacades()) {
      wireBounds.add(BoundingBox.UNIT_CUBE);
    }

    for (IClientConduit c : bundle.getClientConduits()) {
      if (c instanceof IClientConduit.WithDefaultRendering) {
        IClientConduit.WithDefaultRendering con = (IClientConduit.WithDefaultRendering) c;
        if (state.getYetaDisplayMode().renderConduit(con)) {
          IConduitRenderer renderer = getRendererForConduit(con);
          if (renderer.canRenderInLayer(con, layer)) {
            renderer.addBakedQuads(this, bundle, con, brightness, layer, quads);
          }
          Set<EnumFacing> extCons = con.getExternalConnections();
          for (EnumFacing dir : extCons) {
            if (dir != null && con.getConnectionMode(dir) != ConnectionMode.DISABLED && con.getConnectionMode(dir) != ConnectionMode.NOT_SET) {
              externals.add(dir);
            }
          }
        } else if (layer == BlockRenderLayer.CUTOUT) {
          Collection<CollidableComponent> components = con.getCollidableComponents();
          for (CollidableComponent component : components) {
            if (component != null) {
              addWireBounds(wireBounds, component);
            }
          }
        }
      }
    }

    // Internal connectors between conduits
    List<CollidableComponent> connectors = bundle.getConnectors();
    for (CollidableComponent component : connectors) {
      if (component != null) {
        if (component.conduitType != null) {
          IClientConduit.WithDefaultRendering conduit = (IClientConduit.WithDefaultRendering) bundle.getConduit(component.conduitType);
          if (conduit != null) {
            IConduitRenderer renderer = getRendererForConduit(conduit);
            if (state.getYetaDisplayMode().renderConduit(component.conduitType)) {
              if (renderer.getCoreLayer() == layer) {
                IConduitTexture tex = conduit.getTextureForState(component);
                BakedQuadBuilder.addBakedQuads(quads, component.bound, tex.getUv(), tex.getSprite());
              }
            } else if (layer == BlockRenderLayer.CUTOUT) {
              addWireBounds(wireBounds, component);
            }
          }

        } else if (layer == BlockRenderLayer.SOLID && state.getYetaDisplayMode().getDisplayMode().isAll()) {
          TextureAtlasSprite tex = ConduitBundleRenderManager.instance.getConnectorIcon(component.data);
          BakedQuadBuilder.addBakedQuads(quads, component.bound, tex);
        }
      }
    }

    // render these after the 'normal' conduits so help with proper blending
    for (BoundingBox wireBound : wireBounds) {
      if (wireBound != null) {
        BakedQuadBuilder.addBakedQuads(quads, wireBound, ConduitBundleRenderManager.instance.getWireFrameIcon());
      }
    }

    // External connection terminations
    if (layer == BlockRenderLayer.SOLID) {
      for (EnumFacing dir : externals) {
        if (dir != null) {
          addQuadsForExternalConnection(dir, quads);
        }
      }
    }

    // Dummy rendering for empty bundles (shouldn't we just remove these from the world?)
    if (layer == BlockRenderLayer.CUTOUT && bundle.getClientConduits().isEmpty() && !bundle.hasFacade()) {
      BakedQuadBuilder.addBakedQuads(quads, BoundingBox.UNIT_CUBE.scale(.10), ConduitBundleRenderManager.instance.getWireFrameIcon());
      BakedQuadBuilder.addBakedQuads(quads, BoundingBox.UNIT_CUBE.scale(.15), ConduitBundleRenderManager.instance.getWireFrameIcon());
      BakedQuadBuilder.addBakedQuads(quads, BoundingBox.UNIT_CUBE.scale(.20), ConduitBundleRenderManager.instance.getWireFrameIcon());
      BakedQuadBuilder.addBakedQuads(quads, BoundingBox.UNIT_CUBE.scale(.25), ConduitBundleRenderManager.instance.getWireFrameIcon());
    }

  }

  private boolean breakingAnimOnWholeConduit = false;

  private void addBreakingQuads(@Nonnull IConduitBundle bundle, @Nonnull List<BakedQuad> quads) {

    Class<? extends IConduit> conduitType = null;
    RayTraceResult hit = Minecraft.getMinecraft().objectMouseOver;
    if (NullHelper.untrust(hit) != null && (hit.hitInfo instanceof CollidableComponent)) {
      conduitType = ((CollidableComponent) hit.hitInfo).conduitType;
    }

    if (breakingAnimOnWholeConduit) {
      for (IClientConduit c : bundle.getClientConduits()) {
        if (conduitType == c.getClass() || conduitType == c.getBaseConduitType()) {
          IConduitRenderer renderer = getRendererForConduit(c);
          renderer.addBakedQuads(this, bundle, (IClientConduit.WithDefaultRendering) c, 1, BlockRenderLayer.CUTOUT, quads);
        }
      }
    }

    List<CollidableComponent> connectors = bundle.getConnectors();
    for (CollidableComponent component : connectors) {
      if (component != null) {
        if (component.conduitType == conduitType || conduitType == null) {
          IClientConduit.WithDefaultRendering conduit = (IClientConduit.WithDefaultRendering) bundle.getConduit(component.conduitType);
          if (conduit != null) {
            IConduitTexture tex = conduit.getTextureForState(component);
            BakedQuadBuilder.addBakedQuads(quads, component.bound, tex.getSprite());
          }
        }
      }
    }

  }

  private void addWireBounds(@Nonnull List<BoundingBox> wireBounds, @Nonnull CollidableComponent component) {
    if (component.isDirectional()) {
      final EnumFacing componentDirection = component.getDirection();
      double sx = componentDirection.getFrontOffsetX() != 0 ? 1 : 0.7;
      double sy = componentDirection.getFrontOffsetY() != 0 ? 1 : 0.7;
      double sz = componentDirection.getFrontOffsetZ() != 0 ? 1 : 0.7;
      wireBounds.add(component.bound.scale(sx, sy, sz));
    } else {
      wireBounds.add(component.bound);
    }
  }

  private void addQuadsForExternalConnection(@Nonnull EnumFacing dir, @Nonnull List<BakedQuad> quads) {
    TextureAtlasSprite tex = ConduitBundleRenderManager.instance.getConnectorIcon(ConduitConnectorType.EXTERNAL);
    BoundingBox[] bbs = ConduitGeometryUtil.instance.getExternalConnectorBoundingBoxes(dir);
    for (BoundingBox bb : bbs) {
      if (bb != null) {
        BakedQuadBuilder.addBakedQuads(quads, bb, tex);
      }
    }
  }

  public @Nonnull IConduitRenderer getRendererForConduit(@Nonnull IConduit conduit) {
    for (IConduitRenderer renderer : conduitRenderers) {
      if (renderer.isRendererForConduit(conduit)) {
        return renderer;
      }
    }
    return dcr;
  }

  protected @Nonnull NNList<RenderPair> getDynamicRenderersForConduitBundle(IConduitBundle b, EntityPlayerSP player) {
    NNList<RenderPair> result = null;

    for (IConduitRenderer conduitRenderer : dynamicCnduitRenderers) {
      for (IClientConduit conduit : b.getClientConduits()) {
        if (conduit instanceof IClientConduit.WithDefaultRendering && conduitRenderer != null && conduitRenderer.isRendererForConduit(conduit)
            && YetaUtil.renderConduit(player, conduit)) {
          if (result == null) {
            result = new NNList<>();
          }
          result.add(new RenderPair(conduitRenderer, (IClientConduit.WithDefaultRendering) conduit));
        }
      }
    }

    return result != null ? result : NNList.emptyList();
  }

  static class RenderPair {
    private final @Nonnull IClientConduit.WithDefaultRendering conduit;
    private final @Nonnull IConduitRenderer renderer;

    RenderPair(@Nonnull IConduitRenderer renderer, @Nonnull WithDefaultRendering conduit) {
      this.renderer = renderer;
      this.conduit = conduit;
    }

    @Nonnull
    IClientConduit.WithDefaultRendering getConduit() {
      return conduit;
    }

    @Nonnull
    IConduitRenderer getRenderer() {
      return renderer;
    }
  }

}
