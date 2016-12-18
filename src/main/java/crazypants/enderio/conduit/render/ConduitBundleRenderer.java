package crazypants.enderio.conduit.render;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.lwjgl.opengl.GL11;

import com.enderio.core.client.render.BoundingBox;
import com.enderio.core.client.render.RenderUtil;
import com.enderio.core.common.util.BlockCoord;

import crazypants.enderio.conduit.ConnectionMode;
import crazypants.enderio.conduit.IConduit;
import crazypants.enderio.conduit.IConduitBundle;
import crazypants.enderio.conduit.TileConduitBundle;
import crazypants.enderio.conduit.geom.CollidableComponent;
import crazypants.enderio.conduit.geom.ConduitConnectorType;
import crazypants.enderio.conduit.geom.ConduitGeometryUtil;
import crazypants.enderio.config.Config;
import crazypants.enderio.paint.YetaUtil;
import crazypants.enderio.render.IBlockStateWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.EnumSkyBlock;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ConduitBundleRenderer extends TileEntitySpecialRenderer<TileConduitBundle> {

  private final List<ConduitRenderer> conduitRenderers = new ArrayList<ConduitRenderer>();
  private final DefaultConduitRenderer dcr = new DefaultConduitRenderer();

  public ConduitBundleRenderer() {    
  }
  
  public void registerRenderer(ConduitRenderer renderer) {
    conduitRenderers.add(renderer);
  }

  // TESR rendering

  @Override
  public void renderTileEntityAt(TileConduitBundle te, double x, double y, double z, float partialTick, int b) {
    

    IConduitBundle bundle = te;
    EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;
    if (bundle.hasFacade() && bundle.getPaintSource().isOpaqueCube() && !YetaUtil.isFacadeHidden(bundle, player)) {
      return;
    }
    float brightness = -1;
    boolean hasDynamic = false;
    for (IConduit con : bundle.getConduits()) {
      if (YetaUtil.renderConduit(player, con)) {
        ConduitRenderer renderer = getRendererForConduit(con);
        if (renderer.isDynamic()) {
          if (!hasDynamic) {
            hasDynamic = true;
            BlockCoord loc = bundle.getLocation();
            brightness = bundle.getEntity().getWorld().getLightFor(EnumSkyBlock.SKY, loc.getBlockPos());

            RenderUtil.setupLightmapCoords(te.getPos(), te.getWorld());            
            RenderUtil.bindBlockTexture();
            GlStateManager.enableNormalize();
            GlStateManager.enableBlend();            
            GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            GlStateManager.shadeModel(GL11.GL_SMOOTH);

            GlStateManager.pushMatrix();
            GlStateManager.translate(x, y, z);

            Tessellator tessellator = Tessellator.getInstance();
            VertexBuffer tes = tessellator.getBuffer();
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

  public List<BakedQuad> getGeneralQuads(IBlockStateWrapper state, BlockRenderLayer layer) {

    if(layer != BlockRenderLayer.CUTOUT) {
      return Collections.emptyList();
    }

    List<BakedQuad> result = new ArrayList<BakedQuad>();
    IConduitBundle bundle = (IConduitBundle) state.getTileEntity();
    float brightness;
    if (!Config.updateLightingWhenHidingFacades && bundle.hasFacade()) {
      brightness = 15 << 20 | 15 << 4;
    } else {
      brightness = bundle.getEntity().getWorld().getLightFor(EnumSkyBlock.SKY, bundle.getLocation().getBlockPos());
    }
    
    // TODO: check if this is the client thread, if not, make a copy of the bundle and its conduits in a thread-safe way
    addConduitQuads(state, bundle, brightness, result);

    return result;
  }

  private void addConduitQuads(IBlockStateWrapper state, IConduitBundle bundle, float brightness, List<BakedQuad> quads) {

    // Conduits
    Set<EnumFacing> externals = new HashSet<EnumFacing>();
    List<BoundingBox> wireBounds = new ArrayList<BoundingBox>();

    if (bundle.hasFacade() && state.getYetaDisplayMode().isHideFacades()) {
      wireBounds.add(BoundingBox.UNIT_CUBE);
    }

    for (IConduit con : bundle.getConduits().toArray(new IConduit[0])) {

      if (state.getYetaDisplayMode().renderConduit(con)) {
        ConduitRenderer renderer = getRendererForConduit(con);
        renderer.addBakedQuads(this, bundle, con, brightness, quads);
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

    // Internal conectors between conduits
    List<CollidableComponent> connectors = bundle.getConnectors();
    for (CollidableComponent component : connectors) {
      if (component != null) {
        if (component.conduitType != null) {
          IConduit conduit = bundle.getConduit(component.conduitType);
          if (conduit != null) {
            if (state.getYetaDisplayMode().renderConduit(component.conduitType)) {
              BakedQuadBuilder.addBakedQuads(quads, component.bound, conduit.getTextureForState(component));
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

    if (quads.isEmpty()) {
      BakedQuadBuilder.addBakedQuads(quads, BoundingBox.UNIT_CUBE.scale(.25), ConduitBundleRenderManager.instance.getWireFrameIcon());
      BakedQuadBuilder.addBakedQuads(quads, BoundingBox.UNIT_CUBE.scale(.50), ConduitBundleRenderManager.instance.getWireFrameIcon());
      BakedQuadBuilder.addBakedQuads(quads, BoundingBox.UNIT_CUBE.scale(.75), ConduitBundleRenderManager.instance.getWireFrameIcon());
      BakedQuadBuilder.addBakedQuads(quads, BoundingBox.UNIT_CUBE, ConduitBundleRenderManager.instance.getWireFrameIcon());
    }

  }

  private void addWireBounds(List<BoundingBox> wireBounds, CollidableComponent component) {
    if(component.dir != null) {              
      double sx = component.dir.getFrontOffsetX() != 0 ? 1 : 0.7;
      double sy = component.dir.getFrontOffsetY() != 0 ? 1 : 0.7;
      double sz = component.dir.getFrontOffsetZ() != 0 ? 1 : 0.7;                            
      wireBounds.add(component.bound.scale(sx, sy, sz));
    } else {
      wireBounds.add(component.bound);
    }
  }

  private void addQuadsForExternalConnection(EnumFacing dir, List<BakedQuad> quads) {
    TextureAtlasSprite tex = ConduitBundleRenderManager.instance.getConnectorIcon(ConduitConnectorType.EXTERNAL);
    BoundingBox[] bbs = ConduitGeometryUtil.instance.getExternalConnectorBoundingBoxes(dir);
    for (BoundingBox bb : bbs) {
      BakedQuadBuilder.addBakedQuads(quads, bb, tex);
    }
  }

  public ConduitRenderer getRendererForConduit(IConduit conduit) {
    for (ConduitRenderer renderer : conduitRenderers) {
      if (renderer.isRendererForConduit(conduit)) {
        return renderer;
      }
    }
    return dcr;
  }

}
