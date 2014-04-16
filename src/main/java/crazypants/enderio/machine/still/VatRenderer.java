package crazypants.enderio.machine.still;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.util.ForgeDirection;
import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import crazypants.enderio.EnderIO;
import crazypants.enderio.machine.AbstractMachineBlock;
import crazypants.enderio.machine.IoMode;
import crazypants.render.BoundingBox;
import crazypants.render.CubeRenderer;
import crazypants.render.CustomCubeRenderer;
import crazypants.render.CustomRenderBlocks;
import crazypants.render.IRenderFace;
import crazypants.render.RenderUtil;
import crazypants.render.VertexTransform;
import crazypants.util.ForgeDirectionOffsets;
import crazypants.vecmath.Vector3d;
import crazypants.vecmath.Vector3f;
import crazypants.vecmath.Vertex;

public class VatRenderer implements ISimpleBlockRenderingHandler {

  private VertXForm xform = new VertXForm();

  private CustomCubeRenderer ccr = new CustomCubeRenderer();

  private OverlayRenderer overlayRenderer = new OverlayRenderer();

  private TileVat vat;

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


    IIcon[] textures = new IIcon[6];
    if(world != null) {
      textures[0] = EnderIO.blockVat.getIcon(world, x, y, z, ForgeDirection.NORTH.ordinal());
      textures[1] = EnderIO.blockVat.getIcon(world, x, y, z, ForgeDirection.NORTH.ordinal());
      textures[2] = EnderIO.blockVat.getIcon(world, x, y, z, ForgeDirection.UP.ordinal());
      textures[3] = EnderIO.blockVat.getIcon(world, x, y, z, ForgeDirection.DOWN.ordinal());
      textures[4] = EnderIO.blockVat.getIcon(world, x, y, z, ForgeDirection.NORTH.ordinal());
      textures[5] = EnderIO.blockVat.getIcon(world, x, y, z, ForgeDirection.NORTH.ordinal());
    } else {
      textures[0] = EnderIO.blockVat.getIcon(3, 0);
      textures[1] = EnderIO.blockVat.getIcon(3, 0);
      textures[2] = EnderIO.blockVat.getIcon(1, 0);
      textures[3] = EnderIO.blockVat.getIcon(0, 0);
      textures[4] = EnderIO.blockVat.getIcon(3, 0);
      textures[5] = EnderIO.blockVat.getIcon(3, 0);
    }

    Tessellator.instance.setBrightness(15 << 20 | 15 << 4);
    float b = 1;
    if(world != null) {
      b = RenderUtil.claculateTotalBrightnessForLocation(Minecraft.getMinecraft().theWorld, x, y, z);
    }

    float[] cols = new float[6];
    for (int i = 0; i < 6; i++) {
      float m = RenderUtil.getColorMultiplierForFace(ForgeDirection.values()[i]);
      cols[i] = b * m;
    }

    float fudge = 1f;

    //-x side
    BoundingBox bb = BoundingBox.UNIT_CUBE.scale(0.333, fudge,fudge);
    bb = bb.translate(0.5f - (0.333f/2),0,0);
    xform.set(x, y, z);
    CubeRenderer.render(bb, textures, xform, cols);

    bb = BoundingBox.UNIT_CUBE.scale(0.4, fudge,fudge);
    xform.set(x, y, z);
    CubeRenderer.render(bb, textures, xform, cols);

    bb = BoundingBox.UNIT_CUBE.scale(0.333, fudge,fudge);
    bb = bb.translate(-0.5f + (0.333f/2),0,0);
    xform.set(x, y, z);
    CubeRenderer.render(bb, textures, xform, cols);

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
    tes.startDrawingQuads();
    renderWorldBlock(null, 0, 0, 0, block, 0, renderer);
    tes.draw();
  }

  @Override
  public int getRenderId() {
    return BlockVat.renderId;
  }

  private static class VertXForm implements VertexTransform {

    int x;
    int y;
    int z;

    public VertXForm() {
    }

    void set(int x, int y, int z) {
      this.x = x;
      this.y = y;
      this.z = z;
    }

    @Override
    public void apply(Vector3d vec) {
      if(vec.x > 0.9 || vec.x < 0.1) {

        vec.z -= 0.5;
        vec.z *= 0.4;
        vec.z += 0.5;
      }
      vec.add(x, y, z);
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
