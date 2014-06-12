package crazypants.enderio.machine.still;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.client.IItemRenderer.ItemRenderType;
import net.minecraftforge.client.IItemRenderer.ItemRendererHelper;
import net.minecraftforge.common.util.ForgeDirection;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import crazypants.enderio.machine.AbstractMachineBlock;
import crazypants.enderio.machine.IoMode;
import crazypants.enderio.machine.generator.combustion.TranslatedCubeRenderer;
import crazypants.render.BoundingBox;
import crazypants.render.CustomCubeRenderer;
import crazypants.render.CustomRenderBlocks;
import crazypants.render.IRenderFace;
import crazypants.render.VertexTransform;
import crazypants.util.ForgeDirectionOffsets;
import crazypants.vecmath.Vector3d;
import crazypants.vecmath.Vector3f;
import crazypants.vecmath.Vertex;

public class VatRenderer implements ISimpleBlockRenderingHandler, IItemRenderer {

  private VertXForm xform = new VertXForm();

  private CustomCubeRenderer ccr = new CustomCubeRenderer();

  private OverlayRenderer overlayRenderer = new OverlayRenderer();

  private TileVat vat;
  
  @Override
  public boolean handleRenderType(ItemStack item, ItemRenderType type) { 
    return true;
  }

  @Override
  public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper) {
    return true;
  }

  @Override
  public void renderItem(ItemRenderType type, ItemStack item, Object... data) {
    renderInventoryBlock(Block.getBlockFromItem(item.getItem()), item.getItemDamage(), 0, (RenderBlocks)data[0]);    
  }

  @Override
  public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer) {

    short facing = 3;
    boolean active = false;
    if(world != null) {
      TileEntity te = world.getTileEntity(x, y, z);
      if(te instanceof TileVat) {
        vat = (TileVat) te;
        facing = vat.facing;
        active = vat.isActive();
      } else {
        vat = null;
      }
    }

    float fudge = 1f;

    //-x side
    BoundingBox bb = BoundingBox.UNIT_CUBE.scale(0.334, fudge,fudge);
    bb = bb.translate(0.5f - (0.334f/2),0,0);
    TranslatedCubeRenderer.instance.renderBoundingBox(x, y, z, block, bb, xform);

    bb = BoundingBox.UNIT_CUBE.scale(0.334, fudge,fudge);
    TranslatedCubeRenderer.instance.renderBoundingBox(x, y, z, block, bb, xform);

    bb = BoundingBox.UNIT_CUBE.scale(0.334, fudge,fudge);
    bb = bb.translate(-0.5f + (0.334f/2),0,0);
    TranslatedCubeRenderer.instance.renderBoundingBox(x, y, z, block, bb, xform);

    if(vat != null) {
      ccr.renderBlock(world, block, x, y, z, overlayRenderer);
    }
    vat = null;

    return true;
  }


  @Override
  public boolean shouldRender3DInInventory(int modelId) {
    return true;
  }

  @Override
  public void renderInventoryBlock(Block block, int metadata, int modelId, RenderBlocks renderer) {
    Tessellator tes = Tessellator.instance;    
    GL11.glDisable(GL11.GL_LIGHTING);   
    
    OpenGlHelper.setActiveTexture(OpenGlHelper.lightmapTexUnit);
    GL11.glDisable(GL11.GL_TEXTURE_2D);    
    OpenGlHelper.setActiveTexture(OpenGlHelper.defaultTexUnit);
    
    tes.startDrawingQuads();    
    renderWorldBlock(null, 0, 0, 0, block, 0, renderer);
    tes.draw();
    
    OpenGlHelper.setActiveTexture(OpenGlHelper.lightmapTexUnit);
    GL11.glEnable(GL11.GL_TEXTURE_2D);    
    OpenGlHelper.setActiveTexture(OpenGlHelper.defaultTexUnit);    
    
    GL11.glEnable(GL11.GL_LIGHTING);
  }

  @Override
  public int getRenderId() {
    return BlockVat.renderId;
  }

  private static class VertXForm implements VertexTransform {

    public VertXForm() {
    }

    @Override
    public void apply(Vertex vertex) {
     apply(vertex.xyz);
    }

    @Override
    public void apply(Vector3d vec) {
      if(vec.x > 0.9 || vec.x < 0.1) {
        vec.z -= 0.5;
        vec.z *= 0.42;
        vec.z += 0.5;
      }
    }

    @Override
    public void applyToNormal(Vector3f vec) {
    }

  }

  private class OverlayRenderer implements IRenderFace {

    @Override
    public void renderFace(CustomRenderBlocks rb, ForgeDirection face, Block par1Block, double x, double y, double z, IIcon texture, List<Vertex> refVertices,
        boolean translateToXyz) {

      if(vat != null && par1Block instanceof AbstractMachineBlock) {
        Vector3d offset = ForgeDirectionOffsets.offsetScaled(face, 0.01);
        Tessellator.instance.addTranslation((float)offset.x, (float)offset.y, (float)offset.z);

        IoMode mode = vat.getIoMode(face);
        IIcon tex = ((AbstractMachineBlock)par1Block).getOverlayIconForMode(mode);
        if(tex != null) {
          ccr.getCustomRenderBlocks().doDefaultRenderFace(face,par1Block,x,y,z, tex);
        }
        Tessellator.instance.addTranslation(-(float)offset.x, -(float)offset.y, -(float)offset.z);
      }

    }

  }
}
