package crazypants.enderio.machine.still;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import crazypants.enderio.EnderIO;
import crazypants.enderio.machine.generator.TileCombustionGenerator;
import crazypants.render.BoundingBox;
import crazypants.render.CubeRenderer;
import crazypants.render.RenderUtil;
import crazypants.render.VertexTransform;
import crazypants.vecmath.Vector3d;
import crazypants.vecmath.Vector3f;

public class VatRenderer implements ISimpleBlockRenderingHandler {

  private VertXForm xform = new VertXForm();

  @Override
  public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer) {

    short facing = 3;
    boolean active = false;
    if(world != null) {
      TileEntity te = world.getTileEntity(x, y, z);
      if(te instanceof TileCombustionGenerator) {
        TileCombustionGenerator gen = (TileCombustionGenerator) te;
        facing = gen.facing;
        active = gen.isActive();
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

    float b = 1;
    if(world instanceof World) {
      b = RenderUtil.claculateTotalBrightnessForLocation((World) world, x, y, z);
    }

    float[] cols = new float[6];
    for (int i = 0; i < 6; i++) {
      float m = RenderUtil.getColorMultiplierForFace(ForgeDirection.values()[i]);
      cols[i] = b * m;
    }

    //-x side
    BoundingBox bb = BoundingBox.UNIT_CUBE.scale(0.333, 1,1);
    bb = bb.translate(0.5f - (0.3f/2),0,0);
    xform.set(x, y, z);
    CubeRenderer.render(bb, textures, xform, cols);

    bb = BoundingBox.UNIT_CUBE.scale(0.4, 1,1);
    xform.set(x, y, z);
    CubeRenderer.render(bb, textures, xform, cols);

    bb = BoundingBox.UNIT_CUBE.scale(0.333, 1,1);
    bb = bb.translate(-0.5f + (0.3f/2),0,0);
    xform.set(x, y, z);
    CubeRenderer.render(bb, textures, xform, cols);

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
}
