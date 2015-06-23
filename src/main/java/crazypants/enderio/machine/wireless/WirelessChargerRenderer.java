package crazypants.enderio.machine.wireless;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;

import com.enderio.core.api.client.render.VertexTransform;
import com.enderio.core.client.render.BoundingBox;
import com.enderio.core.client.render.CubeRenderer;
import com.enderio.core.client.render.VertexRotation;
import com.enderio.core.client.render.VertexTransformComposite;
import com.enderio.core.client.render.VertexTranslation;
import com.enderio.core.common.vecmath.Vector3d;

import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import crazypants.enderio.EnderIO;

public class WirelessChargerRenderer implements ISimpleBlockRenderingHandler {

  @Override
  public void renderInventoryBlock(Block block, int metadata, int modelId, RenderBlocks renderer) {
//    GL11.glPushMatrix();
//    GL11.glTranslatef(0, 0, 0.5f);
//    GL11.glRotatef(45, 0, 1, 0);
//    GL11.glTranslatef(0, 0, -0.5f);
    //GL11.glTranslatef(0.5f, 0, 0);
    
    Tessellator.instance.startDrawingQuads();
    renderWorldBlock(null, 0, 0, 0, null, 0, renderer);
    Tessellator.instance.draw();
//    GL11.glPopMatrix();
  }

  @Override
  public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer) {
    
    Tessellator tes = Tessellator.instance;
    
    IIcon baseIcon = EnderIO.blockWirelessCharger.getIcon(0, 0);
    
    tes.addTranslation(x, y, z);
    
    VertexTransform rot = new VertexRotation(Math.toRadians(45), new Vector3d(0,1,0), new Vector3d(0.5,0.5,0.5));
    VertexTransform trans = new VertexTranslation(0, -0.5 + 0.025, 0);
    
    VertexTransformComposite xform = new VertexTransformComposite(rot, trans);    
    CubeRenderer.render(BoundingBox.UNIT_CUBE.scale(0.7, 0.05, 0.7), baseIcon, xform, true);
    
    trans = new VertexTranslation(0, 0.5 - 0.025, 0);
    xform = new VertexTransformComposite(rot, trans);
    CubeRenderer.render(BoundingBox.UNIT_CUBE.scale(0.7, 0.05, 0.7), baseIcon, xform, true);
    
    CubeRenderer.render(BoundingBox.UNIT_CUBE.scale(0.2, 0.91, 0.2), EnderIO.blockWirelessCharger.getCenterOn(), true);
    
    trans = new VertexTranslation(0, 0, 0.5 - 0.01);
    xform = new VertexTransformComposite(trans);
    CubeRenderer.render(BoundingBox.UNIT_CUBE.scale(0.25, 0.25, 0.01), baseIcon, xform, true);
    CubeRenderer.render(BoundingBox.UNIT_CUBE.scale(0.01, 1, 0.01), baseIcon, xform, true);
    
    rot = new VertexRotation(Math.toRadians(90), new Vector3d(0,1,0), new Vector3d(0.5,0.5,0.5));
    xform = new VertexTransformComposite(trans, rot);
    CubeRenderer.render(BoundingBox.UNIT_CUBE.scale(0.25, 0.25, 0.01), baseIcon, xform, true);
    CubeRenderer.render(BoundingBox.UNIT_CUBE.scale(0.01, 1, 0.01), baseIcon, xform, true);
    
    rot = new VertexRotation(Math.toRadians(180), new Vector3d(0,1,0), new Vector3d(0.5,0.5,0.5));
    xform = new VertexTransformComposite(trans, rot);
    CubeRenderer.render(BoundingBox.UNIT_CUBE.scale(0.25, 0.25, 0.01), baseIcon, xform, true);
    CubeRenderer.render(BoundingBox.UNIT_CUBE.scale(0.01, 1, 0.01), baseIcon, xform, true);
    
    rot = new VertexRotation(Math.toRadians(270), new Vector3d(0,1,0), new Vector3d(0.5,0.5,0.5));
    xform = new VertexTransformComposite(trans, rot);
    CubeRenderer.render(BoundingBox.UNIT_CUBE.scale(0.25, 0.25, 0.01), baseIcon, xform, true);
    CubeRenderer.render(BoundingBox.UNIT_CUBE.scale(0.01, 1, 0.01), baseIcon, xform, true);
    
    
    tes.addTranslation(-x, -y, -z);
    
    return false;
  }

  @Override
  public boolean shouldRender3DInInventory(int modelId) {
    return true;
  }

  @Override
  public int getRenderId() {
    return BlockWirelessCharger.renderId;
  }

}
