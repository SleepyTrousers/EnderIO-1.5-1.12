package crazypants.enderio.conduit.render;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import com.enderio.core.client.render.BoundingBox;
import com.enderio.core.client.render.RenderUtil;
import com.enderio.core.common.util.BlockCoord;

import crazypants.enderio.conduit.ConduitDisplayMode;
import crazypants.enderio.conduit.ConduitUtil;
import crazypants.enderio.conduit.ConnectionMode;
import crazypants.enderio.conduit.IConduit;
import crazypants.enderio.conduit.IConduitBundle;
import crazypants.enderio.conduit.TileConduitBundle;
import crazypants.enderio.conduit.geom.CollidableComponent;
import crazypants.enderio.conduit.geom.ConduitConnectorType;
import crazypants.enderio.conduit.geom.ConduitGeometryUtil;
import crazypants.enderio.conduit.liquid.AdvancedLiquidConduitRenderer;
import crazypants.enderio.conduit.liquid.LiquidConduitRenderer;
import crazypants.enderio.conduit.power.PowerConduitRenderer;
import crazypants.enderio.conduit.redstone.InsulatedRedstoneConduitRenderer;
import crazypants.enderio.conduit.redstone.RedstoneSwitchRenderer;
import crazypants.enderio.config.Config;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.EnumSkyBlock;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ConduitBundleRenderer extends TileEntitySpecialRenderer<TileConduitBundle> {

  private final List<ConduitRenderer> conduitRenderers = new ArrayList<ConduitRenderer>();
  private final DefaultConduitRenderer dcr = new DefaultConduitRenderer();

  public ConduitBundleRenderer() {
    conduitRenderers.add(RedstoneSwitchRenderer.getInstance());
    conduitRenderers.add(new AdvancedLiquidConduitRenderer());
    conduitRenderers.add(LiquidConduitRenderer.create());
    conduitRenderers.add(new PowerConduitRenderer());
    conduitRenderers.add(new InsulatedRedstoneConduitRenderer());
  }

  // TESR rendering

  @Override
  public void renderTileEntityAt(TileConduitBundle te, double x, double y, double z, float partialTick, int b) {
    IConduitBundle bundle = te;
    EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;
    if (bundle.hasFacade() && bundle.getFacadeId().isOpaqueCube() && !ConduitUtil.isFacadeHidden(bundle, player)) {
      return;
    }
    float brightness = -1;
    for (IConduit con : bundle.getConduits()) {
      if (ConduitUtil.renderConduit(player, con)) {
        ConduitRenderer renderer = getRendererForConduit(con);
        if (renderer.isDynamic()) {
          if (brightness == -1) {
            BlockCoord loc = bundle.getLocation();
            brightness = bundle.getEntity().getWorld().getLightFor(EnumSkyBlock.SKY, loc.getBlockPos());

            RenderUtil.bindBlockTexture();

            GL11.glPushAttrib(GL11.GL_ENABLE_BIT);
            GL11.glPushAttrib(GL11.GL_LIGHTING_BIT);
            GL11.glEnable(GL12.GL_RESCALE_NORMAL);
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            GL11.glShadeModel(GL11.GL_SMOOTH);

            GL11.glPushMatrix();
            GL11.glTranslated(x, y, z);

            Tessellator tessellator = Tessellator.getInstance();
            WorldRenderer tes = tessellator.getWorldRenderer();
            tes.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
          }
          renderer.renderDynamicEntity(this, bundle, con, x, y, z, partialTick, brightness);

        }
      }
    }

    if (brightness != -1) {
      Tessellator.getInstance().draw();

      GL11.glShadeModel(GL11.GL_FLAT);
      GL11.glPopMatrix();
      GL11.glPopAttrib();
      GL11.glPopAttrib();
    }
  }

  // ------------ Block Model building

  public List<BakedQuad> getFaceQuads(ConduitRenderState state, EnumFacing facing) {
    if (!state.getRenderFacade()) {
      return Collections.emptyList();
    }
    return Collections.emptyList();
  }

  public List<BakedQuad> getGeneralQuads(ConduitRenderState state) {

    if (!state.getRenderConduit()) {
      return Collections.emptyList();
    }

    List<BakedQuad> result = new ArrayList<BakedQuad>();
    IConduitBundle bundle = state.getBundle();
    float brightness;
    if (!Config.updateLightingWhenHidingFacades && bundle.hasFacade() && !state.getRenderFacade()) {
      brightness = 15 << 20 | 15 << 4;
    } else {
      brightness = bundle.getEntity().getWorld().getLightFor(EnumSkyBlock.SKY, bundle.getLocation().getBlockPos());
    }
    renderConduits(bundle, brightness, result);

    return result;
  }

  public void renderConduits(IConduitBundle bundle, float brightness, List<BakedQuad> quads) {

    // Conduits
    Set<EnumFacing> externals = new HashSet<EnumFacing>();
    EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;

    List<BoundingBox> wireBounds = new ArrayList<BoundingBox>();

    for (IConduit con : bundle.getConduits()) {

      if (ConduitUtil.renderConduit(player, con)) {
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
          wireBounds.add(component.bound);
        }
      }
    }

    // Internal conectors between conduits
    List<CollidableComponent> connectors = bundle.getConnectors();    
    for (CollidableComponent component : connectors) {
      if (component.conduitType != null) {
        IConduit conduit = bundle.getConduit(component.conduitType);
        if (conduit != null) {
          if (ConduitUtil.renderConduit(player, component.conduitType)) {                   
            RenderUtil.addBakedQuads(component.bound, conduit.getTextureForState(component), quads);
          } else {
            wireBounds.add(component.bound);
          }
        }

      } else if (ConduitUtil.getDisplayMode(player) == ConduitDisplayMode.ALL) {
        TextureAtlasSprite tex = ConduitBundleRenderManager.instance.getConnectorIcon(component.data);
        RenderUtil.addBakedQuads(component.bound, tex, quads);
      }
    }
    
//    // render these after the 'normal' conduits so help with proper blending
//    for (BoundingBox wireBound : wireBounds) {
//      CubeRenderer.render(wireBound, EnderIO.blockConduitFacade.getIcon(0, 0));
//    }

    // External connection terminations
    for (EnumFacing dir : externals) {
      addQuadsForExternalConnection(dir, quads);
    }

  }

  private void addQuadsForExternalConnection(EnumFacing dir, List<BakedQuad> quads) {
    TextureAtlasSprite tex = ConduitBundleRenderManager.instance.getConnectorIcon(ConduitConnectorType.EXTERNAL);
    BoundingBox[] bbs = ConduitGeometryUtil.instance.getExternalConnectorBoundingBoxes(dir);
    for (BoundingBox bb : bbs) {
      RenderUtil.addBakedQuads(bb, tex, quads);
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
