package crazypants.enderio.material;

import static net.minecraftforge.common.ForgeDirection.DOWN;
import static net.minecraftforge.common.ForgeDirection.EAST;
import static net.minecraftforge.common.ForgeDirection.NORTH;
import static net.minecraftforge.common.ForgeDirection.SOUTH;
import static net.minecraftforge.common.ForgeDirection.UP;
import static net.minecraftforge.common.ForgeDirection.WEST;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Icon;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.ForgeDirection;
import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import crazypants.enderio.EnderIO;
import crazypants.enderio.machine.painter.PainterUtil;
import crazypants.enderio.machine.painter.TileEntityCustomBlock;
import crazypants.render.RenderUtil;
import crazypants.util.BlockCoord;
import crazypants.util.ForgeDirectionOffsets;
import crazypants.vecmath.VecmathUtil;
import crazypants.vecmath.Vector2d;
import crazypants.vecmath.Vector3d;
import crazypants.vecmath.Vector4d;

public class FusedQuartzRenderer implements ISimpleBlockRenderingHandler {

  static int renderPass;

  @Override
  public void renderInventoryBlock(Block block, int metadata, int modelID, RenderBlocks renderer) {
    renderer.setOverrideBlockTexture(EnderIO.blockFusedQuartz.itemIcon);
    renderer.renderBlockAsItem(Block.glass, 0, 1);
    renderer.clearOverrideBlockTexture();
  }

  @Override
  public boolean shouldRender3DInInventory() {
    return true;
  }

  @Override
  public int getRenderId() {
    return BlockFusedQuartz.renderId;
  }

  @Override
  public boolean renderWorldBlock(IBlockAccess blockAccess, int x, int y, int z, Block block, int modelId, RenderBlocks renderer) {
    if (renderPass == 0) {
      
      RenderUtil.setTesselatorBrightness(blockAccess, x, y, z);      
      TileEntityCustomBlock tecb = null;
      TileEntity te = blockAccess.getBlockTileEntity(x, y, z);
      if(te instanceof TileEntityCustomBlock) {
        tecb = (TileEntityCustomBlock)te;
      }
      
      renderFrame(blockAccess, x, y, z, tecb, false);

    } else {
      renderer.renderStandardBlock(block, x, y, z);
    }
    return true;
  }
  
  public void renderFrameItem(ItemStack stack) {   
    RenderUtil.bindBlockTexture();
    Tessellator.instance.startDrawingQuads();
    TileEntityCustomBlock tecb = new TileEntityCustomBlock();
    tecb.setSourceBlockId(PainterUtil.getSourceBlockId(stack));
    tecb.setSourceBlockMetadata(PainterUtil.getSourceBlockMetadata(stack));   
    renderFrame(null,0,0,0,tecb,true);
    Tessellator.instance.draw();
    
  }

  private void renderFrame(IBlockAccess blockAccess, int x, int y, int z, TileEntityCustomBlock tecb, boolean forceAllEdges) {
    Icon texture = EnderIO.blockFusedQuartz.itemIcon;
    
    for (ForgeDirection face : ForgeDirection.VALID_DIRECTIONS) {      
    
      if(tecb != null && tecb.getSourceBlockId() > 0) {        
        texture = tecb.getSourceBlock().getIcon(face.ordinal(), tecb.getSourceBlockMetadata());
      }
      renderFrame(blockAccess, x, y, z, tecb, face, texture, forceAllEdges);
    }
  }

  private void renderFrame(IBlockAccess blockAccess, int x, int y, int z, TileEntityCustomBlock tecb, ForgeDirection face, Icon texture, boolean forceAllEdges) {

    if (!forceAllEdges && !EnderIO.blockFusedQuartz.shouldSideBeRendered(blockAccess, x + face.offsetX, y + face.offsetY, z + face.offsetZ,
        face.ordinal())) {
      return;
    }
    
    BlockCoord bc = new BlockCoord(x, y, z);

    List<Edge> edges = new ArrayList<Edge>(4);
    if (face.offsetY != 0) {
      edges.add(new Edge(bc, EAST));
      edges.add(new Edge(bc, WEST));
      edges.add(new Edge(bc, NORTH));
      edges.add(new Edge(bc, SOUTH));
    } else if (face.offsetX != 0) {
      edges.add(new Edge(bc, DOWN));
      edges.add(new Edge(bc, UP));
      edges.add(new Edge(bc, SOUTH));
      edges.add(new Edge(bc, NORTH));
    } else {
      edges.add(new Edge(bc, UP));
      edges.add(new Edge(bc, DOWN));
      edges.add(new Edge(bc, WEST));
      edges.add(new Edge(bc, EAST));
    }

    Tessellator tes = Tessellator.instance;
    float cm = RenderUtil.getColorMultiplierForFace(face);
    tes.setColorOpaque_F(cm, cm, cm);
    
    Vector2d uv = new Vector2d();
    int index = 0;
    boolean invertWinding;
    for (Edge edge : edges) {

      if (forceAllEdges || blockAccess.getBlockId(edge.bc.x, edge.bc.y, edge.bc.z) != EnderIO.blockFusedQuartz.blockID) {

        invertWinding = ForgeDirectionOffsets.isPositiveOffset(face);

        Vector3d edgeCenter = new Vector3d(x + 0.5, y + 0.5, z + 0.5);
        edgeCenter.add(ForgeDirectionOffsets.offsetScaled(edge.dir, 0.5));
        edgeCenter.add(ForgeDirectionOffsets.offsetScaled(face, 0.5));

        Vector3d edgeUp = new Vector3d();
        edgeUp.x = face.offsetX == 0 && edge.dir.offsetX == 0 ? 0.5 : 0;
        edgeUp.y = face.offsetY == 0 && edge.dir.offsetY == 0 ? 0.5 : 0;
        edgeUp.z = face.offsetZ == 0 && edge.dir.offsetZ == 0 ? 0.5 : 0;

        Vector3d in = ForgeDirectionOffsets.offsetScaled(edge.dir, -1 / 16d);
        Vector3d corner = new Vector3d();
        if ((invertWinding && index % 2 == 0) || (!invertWinding && index % 2 != 0)) {
          
          corner.set(edgeCenter);
          corner.add(edgeUp);
          getUvForCorner(uv, corner,x,y,z,face,texture);
          tes.addVertexWithUV(corner.x, corner.y, corner.z, uv.x, uv.y);
          
          corner.set(edgeCenter);
          corner.sub(edgeUp);
          getUvForCorner(uv, corner,x,y,z,face,texture);
          tes.addVertexWithUV(corner.x, corner.y, corner.z, uv.x, uv.y);
          
          corner.set(edgeCenter);
          corner.sub(edgeUp);
          corner.add(in);
          getUvForCorner(uv, corner,x,y,z,face,texture);
          tes.addVertexWithUV(corner.x, corner.y, corner.z, uv.x, uv.y);
          
          corner.set(edgeCenter);
          corner.add(edgeUp);
          corner.add(in);
          getUvForCorner(uv, corner,x,y,z,face,texture);
          tes.addVertexWithUV(corner.x, corner.y, corner.z, uv.x, uv.y);
          
        } else { //reverse winding
          
          corner.set(edgeCenter);
          corner.add(edgeUp);
          corner.add(in);
          getUvForCorner(uv, corner,x,y,z,face,texture);
          tes.addVertexWithUV(corner.x, corner.y, corner.z, uv.x, uv.y);
          
          corner.set(edgeCenter);
          corner.sub(edgeUp);
          corner.add(in);
          getUvForCorner(uv, corner,x,y,z,face,texture);
          tes.addVertexWithUV(corner.x, corner.y, corner.z, uv.x, uv.y);
          
          corner.set(edgeCenter);
          corner.sub(edgeUp);
          getUvForCorner(uv, corner,x,y,z,face,texture);
          tes.addVertexWithUV(corner.x, corner.y, corner.z, uv.x, uv.y);
          
          corner.set(edgeCenter);
          corner.add(edgeUp);
          getUvForCorner(uv, corner,x,y,z,face,texture);
          tes.addVertexWithUV(corner.x, corner.y, corner.z, uv.x, uv.y);
        }

      }
      index++;
    }
  }

  
  private void getUvForCorner(Vector2d uv, Vector3d corner, int x, int y, int z, ForgeDirection face, Icon icon) {
    Vector3d p = new Vector3d(corner);
    p.x -= x;
    p.y -= y;
    p.z -= z;    
    
    float uWidth = icon.getMaxU() - icon.getMinU();
    float vWidth = icon.getMaxV() - icon.getMinV();
    
    uv.x = VecmathUtil.distanceFromPointToPlane(getUPlaneForFace(face), p);
    uv.y = VecmathUtil.distanceFromPointToPlane(getVPlaneForFace(face), p);        
    
    uv.x = icon.getMinU() + (uv.x * uWidth);
    uv.y = icon.getMinV() + (uv.y * vWidth);    
    
  }
  
  private Vector4d getVPlaneForFace(ForgeDirection face) {
    switch (face) {    
    case DOWN:           
    case UP:
      return new Vector4d(0,0,1,0);      
    case EAST:               
    case WEST:      
    case NORTH:           
    case SOUTH:      
      return new Vector4d(0,-1,0,1);
    case UNKNOWN:           
    default:     
      break;
    }
    return null;
  }
  
  private Vector4d getUPlaneForFace(ForgeDirection face) {
    switch (face) {    
    case DOWN:           
    case UP:      
      return new Vector4d(1,0,0,0);
    case EAST:    
      return new Vector4d(0,0,-1,1);      
    case WEST:
      return new Vector4d(0,0,1,0);
    case NORTH:      
      return new Vector4d(-1,0,0,1);
    case SOUTH:      
      return new Vector4d(1,0,0,0);        
    case UNKNOWN:           
    default:     
      break;
    }
    return null;
  }


  private static class Edge {
    final ForgeDirection dir;
    final BlockCoord bc;

    public Edge(BlockCoord bc, ForgeDirection dir) {
      this.dir = dir;
      this.bc = bc.getLocation(dir);
    }

  }

}
