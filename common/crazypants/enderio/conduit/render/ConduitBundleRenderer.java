package crazypants.enderio.conduit.render;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Icon;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.ForgeDirection;
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
import crazypants.enderio.conduit.geom.ConduitGeometryUtil;
import crazypants.render.BoundingBox;
import crazypants.render.CubeRenderer;

public class ConduitBundleRenderer implements ISimpleBlockRenderingHandler {

  public ConduitBundleRenderer(float conduitScale) {
  }

  private void doRenderTileEntityAt(TileEntity te, double x, double y, double z, float partialTick) {
    IConduitBundle bundle = (IConduitBundle) te;
    EntityClientPlayerMP player = Minecraft.getMinecraft().thePlayer;

    // Lighting calcuations to allow for self illumination    
    float val = te.worldObj.getLightBrightnessForSkyBlocks(te.xCoord, te.yCoord, te.zCoord, 0);
    renderTileEntityAt(bundle, x, y, z, partialTick, val);
  }

  public void renderTileEntityAt(IConduitBundle bundle, double x, double y, double z, float partialTick, float brightness) {

    Tessellator tessellator = Tessellator.instance;
    tessellator.setColorOpaque_F(1, 1, 1);
    tessellator.addTranslation((float) x, (float) y, (float) z);

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
            tessellator.setBrightness((int) (brightness));
            CubeRenderer.render(component.bound, conduit.getTextureForState(component), true);
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
    }

    Tessellator.instance.setColorRGBA_F(1, 1, 1, 1f);
    // External connection terminations
    for (ForgeDirection dir : externals) {
      renderExternalConnection(dir);
    }

    tessellator.addTranslation(-(float) x, -(float) y, -(float) z);

  }

  private void renderExternalConnection(ForgeDirection dir) {
    Icon tex = EnderIO.blockConduitBundle.getConnectorIcon();
    BoundingBox[] bbs = ConduitGeometryUtil.instance.getExternalConnectorBoundingBoxes(dir);
    for (BoundingBox bb : bbs) {
      CubeRenderer.render(bb, tex, true);
    }
  }

  @Override
  public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks rb) {

    IConduitBundle bundle = (IConduitBundle) world.getBlockTileEntity(x, y, z);
    EntityClientPlayerMP player = Minecraft.getMinecraft().thePlayer;

    boolean renderConduit = true;
    if(bundle.hasFacade()) {

      int facadeId = bundle.getFacadeId();
      if(ConduitUtil.isFacadeHidden(bundle, player)) {
        bundle.setFacadeId(0, false);
        bundle.setFacadeRenderAs(FacadeRenderState.WIRE_FRAME);
      } else {
        bundle.setFacadeRenderAs(FacadeRenderState.FULL);
        renderConduit = false;
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

    if(renderConduit) {
      doRenderTileEntityAt(bundle.getEntity(), x, y, z, 0);
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
