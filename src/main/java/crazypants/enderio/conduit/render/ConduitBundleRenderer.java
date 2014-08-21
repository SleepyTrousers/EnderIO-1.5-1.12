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
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.util.ForgeDirection;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import crazypants.enderio.EnderIO;
import crazypants.enderio.conduit.BlockConduitBundle;
import crazypants.enderio.conduit.ConduitDisplayMode;
import crazypants.enderio.conduit.ConduitUtil;
import crazypants.enderio.conduit.ConnectionMode;
import crazypants.enderio.conduit.IConduit;
import crazypants.enderio.conduit.IConduitBundle;
import crazypants.enderio.conduit.TileConduitBundle;
import crazypants.enderio.conduit.IConduitBundle.FacadeRenderState;
import crazypants.enderio.conduit.facade.BlockConduitFacade;
import crazypants.enderio.conduit.geom.CollidableComponent;
import crazypants.enderio.conduit.geom.ConduitGeometryUtil;
import crazypants.enderio.config.Config;
import crazypants.render.BoundingBox;
import crazypants.render.CubeRenderer;
import crazypants.render.RenderUtil;
import crazypants.util.BlockCoord;
import crazypants.util.IBlockAccessWrapper;

public class ConduitBundleRenderer extends TileEntitySpecialRenderer implements ISimpleBlockRenderingHandler {

  public ConduitBundleRenderer(float conduitScale) {
  }

  @Override
  public void renderTileEntityAt(TileEntity te, double x, double y, double z, float partialTick) {
    IConduitBundle bundle = (IConduitBundle) te;
    EntityClientPlayerMP player = Minecraft.getMinecraft().thePlayer;
    if(bundle.hasFacade() && !ConduitUtil.isFacadeHidden(bundle, player)) {
      return;
    }

    float brightness = -1;
    for (IConduit con : bundle.getConduits()) {
      if(ConduitUtil.renderConduit(player, con)) {
        ConduitRenderer renderer = EnderIO.proxy.getRendererForConduit(con);
        if(renderer.isDynamic()) {
          if(brightness == -1) {
            BlockCoord loc = bundle.getBlockCoord();
            brightness = bundle.getEntity().getWorldObj().getLightBrightnessForSkyBlocks(loc.x, loc.y, loc.z, 0);

            RenderUtil.bindBlockTexture();

            GL11.glPushAttrib(GL11.GL_ENABLE_BIT);
            GL11.glPushAttrib(GL11.GL_LIGHTING_BIT);
            GL11.glEnable(GL12.GL_RESCALE_NORMAL);
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            GL11.glShadeModel(GL11.GL_SMOOTH);

            GL11.glPushMatrix();
            GL11.glTranslated(x, y, z);

            Tessellator.instance.startDrawingQuads();
          }
          renderer.renderDynamicEntity(this, bundle, con, x, y, z, partialTick, brightness);

        }
      }
    }

    if(brightness != -1) {
      Tessellator.instance.draw();

      GL11.glShadeModel(GL11.GL_FLAT);
      GL11.glPopMatrix();
      GL11.glPopAttrib();
      GL11.glPopAttrib();
    }

  }

  @Override
  public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks rb) {

    IConduitBundle bundle = (IConduitBundle) world.getTileEntity(x, y, z);
    EntityClientPlayerMP player = Minecraft.getMinecraft().thePlayer;

    boolean renderConduit = renderFacade(x, y, z, rb, bundle, player);    

    if(renderConduit) {
      BlockCoord loc = bundle.getBlockCoord();
      float brightness;
      if(!Config.updateLightingWhenHidingFacades && bundle.hasFacade() && ConduitUtil.isFacadeHidden(bundle, player)) {
        brightness = 15 << 20 | 15 << 4;
      } else {
        brightness = bundle.getEntity().getWorldObj().getLightBrightnessForSkyBlocks(loc.x, loc.y, loc.z, 0);
      }
      renderConduits(bundle, x, y, z, 0, brightness);
    }

    return true;
  }

  private boolean renderFacade(int x, int y, int z, RenderBlocks rb, IConduitBundle bundle, EntityClientPlayerMP player) {
    boolean res = true;
    if(bundle.hasFacade()) {

      Block facadeId = bundle.getFacadeId();
      if(ConduitUtil.isFacadeHidden(bundle, player)) {
        bundle.setFacadeId(null, false);
        bundle.setFacadeRenderAs(FacadeRenderState.WIRE_FRAME);
        
        BlockConduitFacade facb = EnderIO.blockConduitFacade;
        facb.setBlockOverride(bundle);
        facb.setBlockBounds(0, 0, 0, 1, 1, 1);
        rb.setRenderBoundsFromBlock(facb);
        rb.renderStandardBlock(facb, x, y, z);
        facb.setBlockOverride(null);
        
        bundle.setFacadeId(facadeId, false);

        
      } else if(facadeId != null){
        bundle.setFacadeRenderAs(FacadeRenderState.FULL);
        res = false;
        
        IBlockAccess origBa = rb.blockAccess;
        rb.blockAccess = new FacadeAccessWrapper(origBa);
        rb.renderBlockByRenderType(facadeId, x, y, z);        
        rb.blockAccess = origBa;        
      }


    } else {
      bundle.setFacadeRenderAs(FacadeRenderState.NONE);
    }
    return res;
  }

  public void renderConduits(IConduitBundle bundle, double x, double y, double z, float partialTick, float brightness) {

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
        IIcon tex = EnderIO.blockConduitBundle.getConnectorIcon();
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
    IIcon tex = EnderIO.blockConduitBundle.getConnectorIcon();
    BoundingBox[] bbs = ConduitGeometryUtil.instance.getExternalConnectorBoundingBoxes(dir);
    for (BoundingBox bb : bbs) {
      CubeRenderer.render(bb, tex, true);
    }
  }

  @Override
  public boolean shouldRender3DInInventory(int modelId) {
    return false;
  }

  @Override
  public void renderInventoryBlock(Block block, int metadata, int modelID, RenderBlocks renderer) {
  }

  @Override
  public int getRenderId() {
    return BlockConduitBundle.rendererId;
  }
  
  private class FacadeAccessWrapper extends IBlockAccessWrapper {

    public FacadeAccessWrapper(IBlockAccess ba) {
      super(ba);
    }

    @Override
    public Block getBlock(int x, int y, int z) {
      Block res = super.getBlock(x, y, z);
      if(res == EnderIO.blockConduitBundle) {
        TileEntity te = getTileEntity(x, y, z);
        if(te instanceof TileConduitBundle) {
          TileConduitBundle tcb = (TileConduitBundle)te;
          Block fac = tcb.getFacadeId();
          if(fac != null) {            
            res = fac;
          }
        }
      }
      return res; 
    }

    @Override
    @SideOnly(Side.CLIENT)
    public int getLightBrightnessForSkyBlocks(int var1, int var2, int var3, int var4) {
      return wrapped.getLightBrightnessForSkyBlocks(var1 , var2, var3, var4);
    }

    @Override
    public int getBlockMetadata(int x, int y, int z) {
      Block block = super.getBlock(x, y, z);
      if(block == EnderIO.blockConduitBundle) {
        TileEntity te = getTileEntity(x, y, z);
        if(te instanceof TileConduitBundle) {
          TileConduitBundle tcb = (TileConduitBundle)te;
          Block fac = tcb.getFacadeId();
          if(fac != null) {            
            return tcb.getFacadeMetadata();
          }
        }
      }
      return super.getBlockMetadata(x, y, z);
    }
    
  }

}
