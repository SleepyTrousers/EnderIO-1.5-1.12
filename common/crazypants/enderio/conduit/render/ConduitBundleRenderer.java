package crazypants.enderio.conduit.render;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Icon;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.ForgeDirection;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import crazypants.enderio.EnderIO;
import crazypants.enderio.ModObject;
import crazypants.enderio.conduit.BlockConduitBundle;
import crazypants.enderio.conduit.ConduitDisplayMode;
import crazypants.enderio.conduit.ConduitUtil;
import crazypants.enderio.conduit.ConnectionMode;
import crazypants.enderio.conduit.IConduit;
import crazypants.enderio.conduit.IConduitBundle;
import crazypants.enderio.conduit.IConduitBundle.FacadeRenderState;
import crazypants.enderio.conduit.facade.BlockConduitFacade;
import crazypants.enderio.conduit.geom.CollidableComponent;
import crazypants.render.BoundingBox;
import crazypants.render.CubeRenderer;
import crazypants.render.RenderUtil;

public class ConduitBundleRenderer extends TileEntitySpecialRenderer implements ISimpleBlockRenderingHandler {

  private Map<ForgeDirection, BoundingBox[]> connectorBounds = new HashMap<ForgeDirection, BoundingBox[]>();

  public static final float CONNECTOR_DEPTH = 0.05f;

  public ConduitBundleRenderer(float conduitScale) {
    float connectorWidth = 0.25f + (conduitScale * 0.5f);
    for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
      connectorBounds.put(dir, createConnector(dir, CONNECTOR_DEPTH, connectorWidth));
    }
  }

  public BoundingBox getExternalConnectorBoundsForDirection(ForgeDirection dir) {
    BoundingBox[] bbs = connectorBounds.get(dir);
    BoundingBox result = bbs[0];
    return result;
  }

  @Override
  public void renderTileEntityAt(TileEntity te, double x, double y, double z, float partialTick) {
    IConduitBundle bundle = (IConduitBundle) te;
    EntityClientPlayerMP player = Minecraft.getMinecraft().thePlayer;

    FacadeRenderState curRS = bundle.getFacadeRenderedAs();
    FacadeRenderState rs = ConduitUtil.getRequiredFacadeRenderState(bundle, player);

    int curLO = bundle.getLightOpacity();
    int shouldBeLO = rs == FacadeRenderState.FULL ? 255 : 0;
    if(curLO != shouldBeLO) {
      bundle.setLightOpacity(shouldBeLO);
      te.worldObj.updateAllLightTypes(te.xCoord, te.yCoord, te.zCoord);
    }
    if(curRS != rs) {
      te.worldObj.markBlockForRenderUpdate(te.xCoord, te.yCoord, te.zCoord);
    }

    if(curRS == FacadeRenderState.FULL) {
      return;
    }

    // Lighting calcuations to allow for self illumination
    float val = RenderUtil.claculateTotalBrightnessForLocation(te.worldObj, te.xCoord, te.yCoord, te.zCoord);
    renderTileEntityAt(bundle, x, y, z, partialTick, val);
  }

  public void renderTileEntityAt(IConduitBundle bundle, double x, double y, double z, float partialTick, float brightness) {

    RenderUtil.bindBlockTexture();
    Minecraft.getMinecraft().entityRenderer.disableLightmap(0);

    GL11.glPushAttrib(GL11.GL_ENABLE_BIT);
    GL11.glPushAttrib(GL11.GL_LIGHTING_BIT);
    GL11.glEnable(GL12.GL_RESCALE_NORMAL);
    GL11.glEnable(GL11.GL_BLEND);
    GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
    GL11.glShadeModel(GL11.GL_SMOOTH);

    GL11.glPushMatrix();
    GL11.glTranslated(x, y, z);

    Tessellator tessellator = Tessellator.instance;
    tessellator.startDrawingQuads();

    // Conduits
    Set<ForgeDirection> externals = new HashSet<ForgeDirection>();
    EntityClientPlayerMP player = Minecraft.getMinecraft().thePlayer;

    List<BoundingBox> wireBounds = new ArrayList<BoundingBox>();

    for (IConduit con : bundle.getConduits()) {

      if(ConduitUtil.renderConduit(player, con)) {
        ConduitRenderer renderer = EnderIO.proxy.getRendererForConduit(con);
        renderer.renderEntity(this, bundle, con, x, y, z, partialTick, brightness);
        Set<ForgeDirection> extCons = con.getExternalConnections();
        for (ForgeDirection dir : extCons) {
          if(con.getConectionMode(dir) != ConnectionMode.DISABLED && con.getConectionMode(dir) != ConnectionMode.NOT_SET) {
            externals.add(dir);
          }
        }
      } else if(con != null) {
        Collection<CollidableComponent> components = con.getCollidableComponents();
        for (CollidableComponent component : components) {
          wireBounds.add(component.bound);
        }
      }

    }

    // Internal conectors between conduits
    List<CollidableComponent> connectors = bundle.getConnectors();
    for (CollidableComponent component : connectors) {
      if(component.conduitType != null) {
        IConduit conduit = bundle.getConduit(component.conduitType);
        if(conduit != null) {
          if(ConduitUtil.renderConduit(player, component.conduitType)) {
            float selfIllum = Math.max(brightness, conduit.getSelfIlluminationForState(component));
            tessellator.setColorRGBA_F(selfIllum, selfIllum, selfIllum, 1);
            CubeRenderer.render(component.bound, conduit.getTextureForState(component));
          } else {
            wireBounds.add(component.bound);
          }
        }

      } else if(ConduitUtil.getDisplayMode(player) == ConduitDisplayMode.ALL) {
        Icon tex = EnderIO.blockConduitBundle.getConnectorIcon();
        CubeRenderer.render(component.bound, tex);
      }
    }

    //render these after the 'normal' conduits so help with proper blending
    for (BoundingBox wireBound : wireBounds) {
      Tessellator.instance.setColorRGBA_F(1, 1, 1, 0.25f);
      CubeRenderer.render(wireBound, EnderIO.blockConduitFacade.getIcon(0, 0));
      Tessellator.instance.setColorRGBA_F(1, 1, 1, 1f);
    }

    // External connection terminations
    for (ForgeDirection dir : externals) {
      renderExternalConnection(dir);
    }

    tessellator.draw();

    GL11.glShadeModel(GL11.GL_FLAT);
    GL11.glPopMatrix();
    GL11.glPopAttrib();
    GL11.glPopAttrib();
    Minecraft.getMinecraft().entityRenderer.enableLightmap(0);

  }

  private void renderExternalConnection(ForgeDirection dir) {
    Icon tex = EnderIO.blockConduitBundle.getConnectorIcon();
    BoundingBox[] bbs = connectorBounds.get(dir);
    for (BoundingBox bb : bbs) {
      CubeRenderer.render(bb, tex);
    }
  }

  private BoundingBox[] createConnector(ForgeDirection dir, float connectorDepth, float connectorWidth) {

    BoundingBox[] res = new BoundingBox[2];

    float cMin = 0.5f - connectorWidth / 2;
    float cMax = 0.5f + connectorWidth / 2;
    float dMin = 1 - connectorDepth / 2;
    float dMax = 1;

    res[0] = createConnectorComponent(dir, cMin, cMax, dMin, dMax);

    cMin = 0.5f - connectorWidth / 3;
    cMax = 0.5f + connectorWidth / 3;
    dMin = 1 - connectorDepth;
    dMax = 1 - connectorDepth / 2;

    res[1] = createConnectorComponent(dir, cMin, cMax, dMin, dMax);

    return res;
  }

  private static BoundingBox createConnectorComponent(ForgeDirection dir, float cornerMin, float cornerMax, float depthMin, float depthMax) {
    float minX = (1 - Math.abs(dir.offsetX)) * cornerMin + dir.offsetX * depthMin;
    float minY = (1 - Math.abs(dir.offsetY)) * cornerMin + dir.offsetY * depthMin;
    float minZ = (1 - Math.abs(dir.offsetZ)) * cornerMin + dir.offsetZ * depthMin;

    float maxX = (1 - Math.abs(dir.offsetX)) * cornerMax + (dir.offsetX * depthMax);
    float maxY = (1 - Math.abs(dir.offsetY)) * cornerMax + (dir.offsetY * depthMax);
    float maxZ = (1 - Math.abs(dir.offsetZ)) * cornerMax + (dir.offsetZ * depthMax);

    minX = fix(minX);
    minY = fix(minY);
    minZ = fix(minZ);
    maxX = fix(maxX);
    maxY = fix(maxY);
    maxZ = fix(maxZ);

    BoundingBox bb = new BoundingBox(minX, minY, minZ, maxX, maxY, maxZ);
    bb = bb.fixMinMax();

    return bb;
  }

  private static float fix(float val) {
    return val < 0 ? 1 + val : val;
  }

  @Override
  public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks rb) {

    IConduitBundle bundle = (IConduitBundle) world.getBlockTileEntity(x, y, z);
    EntityClientPlayerMP player = Minecraft.getMinecraft().thePlayer;

    if(bundle.hasFacade()) {

      int facadeId = bundle.getFacadeId();
      if(ConduitUtil.isFacadeHidden(bundle, player)) {
        bundle.setFacadeId(0, false);
        bundle.setFacadeRenderAs(FacadeRenderState.WIRE_FRAME);
      } else {
        bundle.setFacadeRenderAs(FacadeRenderState.FULL);
      }

      BlockConduitFacade facb = (BlockConduitFacade) Block.blocksList[ModObject.blockConduitFacade.actualId];
      facb.setBlockOverride(bundle);
      facb.setBlockBounds(0, 0, 0, 1, 1, 1);
      rb.setRenderBoundsFromBlock(facb);
      rb.renderStandardBlock(facb, x, y, z);
      facb.setBlockOverride(null);

      bundle.setFacadeId(facadeId, false);

    } else {
      bundle.setFacadeRenderAs(FacadeRenderState.NONE);
    }
    return true;
  }

  @Override
  public boolean shouldRender3DInInventory() {
    return false;
  }

  @Override
  public void renderInventoryBlock(Block block, int metadata, int modelID, RenderBlocks renderer) {
  }

  @Override
  public int getRenderId() {
    return BlockConduitBundle.rendererId;
  }

}
