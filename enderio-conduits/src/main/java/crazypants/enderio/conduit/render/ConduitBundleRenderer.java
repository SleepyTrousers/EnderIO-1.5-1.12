package crazypants.enderio.conduit.render;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Nonnull;

import org.lwjgl.opengl.GL11;

import com.enderio.core.client.render.BoundingBox;
import com.enderio.core.client.render.RenderUtil;
import com.enderio.core.common.util.NullHelper;

import crazypants.enderio.base.conduit.ConnectionMode;
import crazypants.enderio.base.conduit.IConduit;
import crazypants.enderio.base.conduit.IConduit.WithDefaultRendering;
import crazypants.enderio.base.conduit.IConduitBundle;
import crazypants.enderio.base.conduit.IConduitRenderer;
import crazypants.enderio.base.conduit.geom.CollidableComponent;
import crazypants.enderio.base.conduit.geom.ConduitConnectorType;
import crazypants.enderio.base.conduit.geom.ConduitGeometryUtil;
import crazypants.enderio.base.paint.YetaUtil;
import crazypants.enderio.base.render.IBlockStateWrapper;
import crazypants.enderio.conduit.TileConduitBundle;
import crazypants.enderio.conduit.config.ConduitConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.EnumSkyBlock;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ConduitBundleRenderer extends TileEntitySpecialRenderer<TileConduitBundle> {

  private final @Nonnull List<IConduitRenderer> conduitRenderers = new ArrayList<IConduitRenderer>();
  private final @Nonnull DefaultConduitRenderer dcr = new DefaultConduitRenderer();

  public ConduitBundleRenderer() {
  }

  public void registerRenderer(IConduitRenderer renderer) {
    conduitRenderers.add(renderer);
  }

  // TESR rendering

  @Override
  public void render(@Nonnull TileConduitBundle te, double x, double y, double z, float partialTick, int b, float alpha) {

    IConduitBundle bundle = te;
    EntityPlayerSP player = Minecraft.getMinecraft().player;
    if (bundle.hasFacade() && bundle.getPaintSource().isOpaqueCube() && !YetaUtil.isFacadeHidden(bundle, player)) {
      return;
    }
    float brightness = -1;
    boolean hasDynamic = false;
    for (IConduit c : bundle.getConduits()) {
      // TODO Temporary work around
      IConduit.WithDefaultRendering con = (IConduit.WithDefaultRendering) c;
      if (YetaUtil.renderConduit(player, con)) {
        IConduitRenderer renderer = getRendererForConduit(con);
        if (renderer.isDynamic()) {
          if (!hasDynamic) {
            hasDynamic = true;
            BlockPos loc = bundle.getLocation();
            brightness = bundle.getEntity().getWorld().getLightFor(EnumSkyBlock.SKY, loc);

            RenderUtil.setupLightmapCoords(te.getPos(), te.getWorld());
            RenderUtil.bindBlockTexture();
            GlStateManager.enableNormalize();
            GlStateManager.enableBlend();
            GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            GlStateManager.shadeModel(GL11.GL_SMOOTH);

            GlStateManager.pushMatrix();
            GlStateManager.translate(x, y, z);

            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder tes = tessellator.getBuffer();
            tes.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);

          }
          renderer.renderDynamicEntity(this, bundle, con, x, y, z, partialTick, brightness);

        }
      }
    }

    if (hasDynamic) {
      Tessellator.getInstance().draw();
      GlStateManager.disableNormalize();
      GlStateManager.disableBlend();
      GlStateManager.shadeModel(GL11.GL_FLAT);
      GlStateManager.popMatrix();
    }
  }

  // ------------ Block Model building

  public @Nonnull List<BakedQuad> getGeneralQuads(@Nonnull IBlockStateWrapper state, BlockRenderLayer layer) {

    if (layer != null && layer != BlockRenderLayer.CUTOUT) {
      return Collections.emptyList();
    }

    List<BakedQuad> result = new ArrayList<BakedQuad>();
    IConduitBundle bundle = (IConduitBundle) state.getTileEntity();

    if (bundle != null) {
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

    if (bundle.hasFacade() && state.getYetaDisplayMode().isHideFacades()) {
      wireBounds.add(BoundingBox.UNIT_CUBE);
    }

    for (IConduit c : bundle.getConduits().toArray(new IConduit[0])) {
      // TODO Temporary Workaround
      IConduit.WithDefaultRendering con = (IConduit.WithDefaultRendering) c;
      if (state.getYetaDisplayMode().renderConduit(con)) {
        IConduitRenderer renderer = getRendererForConduit(con);
        renderer.addBakedQuads(this, bundle, con, brightness, layer, quads);
        Set<EnumFacing> extCons = con.getExternalConnections();
        for (EnumFacing dir : extCons) {
          if (con.getConnectionMode(dir) != ConnectionMode.DISABLED && con.getConnectionMode(dir) != ConnectionMode.NOT_SET) {
            externals.add(dir);
          }
        }
      } else if (con != null) {
        Collection<CollidableComponent> components = con.getCollidableComponents();
        for (CollidableComponent component : components) {
          addWireBounds(wireBounds, component);
        }
      }
    }

    // Internal connectors between conduits
    List<CollidableComponent> connectors = bundle.getConnectors();
    for (CollidableComponent component : connectors) {
      if (component != null) {
        if (component.conduitType != null) {
          // TODO Make an actual check before assuming a default render
          IConduit.WithDefaultRendering conduit = (IConduit.WithDefaultRendering) bundle.getConduit(component.conduitType);
          if (conduit != null) {
            if (state.getYetaDisplayMode().renderConduit(component.conduitType)) {
              TextureAtlasSprite tex = conduit.getTextureForState(component);
              if (tex == null) {
                tex = Minecraft.getMinecraft().getTextureMapBlocks().getMissingSprite();
              }
              BakedQuadBuilder.addBakedQuads(quads, component.bound, tex);
            } else {
              addWireBounds(wireBounds, component);
            }
          }

        } else if (state.getYetaDisplayMode().getDisplayMode().isAll()) {
          TextureAtlasSprite tex = ConduitBundleRenderManager.instance.getConnectorIcon(component.data);
          BakedQuadBuilder.addBakedQuads(quads, component.bound, tex);
        }
      }
    }

    // render these after the 'normal' conduits so help with proper blending
    for (BoundingBox wireBound : wireBounds) {
      BakedQuadBuilder.addBakedQuads(quads, wireBound, ConduitBundleRenderManager.instance.getWireFrameIcon());
    }

    // External connection terminations
    for (EnumFacing dir : externals) {
      addQuadsForExternalConnection(dir, quads);
    }

    if (quads.isEmpty() && !bundle.hasFacade()) {
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
      for (IConduit c : bundle.getConduits().toArray(new IConduit[0])) {
        if (conduitType == c.getClass() || conduitType == c.getBaseConduitType()) {
          IConduitRenderer renderer = getRendererForConduit(c);
          renderer.addBakedQuads(this, bundle, (WithDefaultRendering) c, 1, BlockRenderLayer.CUTOUT, quads);
        }
      }
    }

    List<CollidableComponent> connectors = bundle.getConnectors();
    for (CollidableComponent component : connectors) {
      if (component != null) {
        if (component.conduitType == conduitType || conduitType == null) {
          IConduit.WithDefaultRendering conduit = (IConduit.WithDefaultRendering) bundle.getConduit(component.conduitType);
          if (conduit != null) {
            TextureAtlasSprite tex = conduit.getTextureForState(component);
            BakedQuadBuilder.addBakedQuads(quads, component.bound, tex);
          }
        }
      }
    }

  }

  private void addWireBounds(@Nonnull List<BoundingBox> wireBounds, @Nonnull CollidableComponent component) {
    if (component.dir != null) {
      double sx = component.dir.getFrontOffsetX() != 0 ? 1 : 0.7;
      double sy = component.dir.getFrontOffsetY() != 0 ? 1 : 0.7;
      double sz = component.dir.getFrontOffsetZ() != 0 ? 1 : 0.7;
      wireBounds.add(component.bound.scale(sx, sy, sz));
    } else {
      wireBounds.add(component.bound);
    }
  }

  private void addQuadsForExternalConnection(@Nonnull EnumFacing dir, @Nonnull List<BakedQuad> quads) {
    TextureAtlasSprite tex = ConduitBundleRenderManager.instance.getConnectorIcon(ConduitConnectorType.EXTERNAL);
    BoundingBox[] bbs = ConduitGeometryUtil.instance.getExternalConnectorBoundingBoxes(dir);
    for (BoundingBox bb : bbs) {
      BakedQuadBuilder.addBakedQuads(quads, bb, tex);
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

}
