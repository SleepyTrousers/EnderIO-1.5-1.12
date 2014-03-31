package crazypants.enderio.machine.generator;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import crazypants.enderio.EnderIO;
import crazypants.enderio.material.BlockFusedQuartz;
import crazypants.enderio.material.FusedQuartzRenderer;
import crazypants.render.BoundingBox;
import crazypants.render.CubeRenderer;
import crazypants.render.IconUtil;
import crazypants.render.RenderUtil;
import crazypants.render.VertexTransform;
import crazypants.vecmath.Vector3d;
import crazypants.vecmath.Vector3f;

public class CombustionGeneratorRenderer extends TileEntitySpecialRenderer implements ISimpleBlockRenderingHandler {

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
      textures[0] = EnderIO.blockCombustionGenerator.getIcon(world, x, y, z, ForgeDirection.NORTH.ordinal());
      textures[1] = EnderIO.blockCombustionGenerator.getIcon(world, x, y, z, ForgeDirection.SOUTH.ordinal());
      textures[2] = EnderIO.blockCombustionGenerator.getIcon(world, x, y, z, ForgeDirection.UP.ordinal());
      textures[3] = EnderIO.blockCombustionGenerator.getIcon(world, x, y, z, ForgeDirection.DOWN.ordinal());
      textures[4] = EnderIO.blockCombustionGenerator.getIcon(world, x, y, z, ForgeDirection.WEST.ordinal());
      textures[5] = EnderIO.blockCombustionGenerator.getIcon(world, x, y, z, ForgeDirection.EAST.ordinal());
    } else {
      textures[0] = EnderIO.blockCombustionGenerator.getIcon(ForgeDirection.NORTH.ordinal(), 0);
      textures[1] = EnderIO.blockCombustionGenerator.getIcon(ForgeDirection.SOUTH.ordinal(), 0);
      textures[2] = EnderIO.blockCombustionGenerator.getIcon(ForgeDirection.UP.ordinal(), 0);
      textures[3] = EnderIO.blockCombustionGenerator.getIcon(ForgeDirection.DOWN.ordinal(), 0);
      textures[4] = EnderIO.blockCombustionGenerator.getIcon(ForgeDirection.WEST.ordinal(), 0);
      textures[5] = EnderIO.blockCombustionGenerator.getIcon(ForgeDirection.EAST.ordinal(), 0);
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

    boolean scaleX = facing != 4 && facing != 5;
    float scx = scaleX ? 0.7f : 1;
    float scz = scaleX ? 1 : 0.7f;

    //top 1/3
    BoundingBox bb = BoundingBox.UNIT_CUBE;
    bb = bb.scale(scx, 0.25, scz);
    bb = bb.translate(0, 0.29f, 0);
    xform.set(x, y, z, facing);
    CubeRenderer.render(bb, textures, xform, cols);

    //lower 1/3
    bb = BoundingBox.UNIT_CUBE;
    bb = bb.scale(scx, 0.25, scz);
    bb = bb.translate(0, -0.29f, 0);
    xform.set(x, y, z, facing);
    CubeRenderer.render(bb, textures, xform, cols);
    
    
    IIcon tex = active ? EnderIO.blockCombustionGenerator.getFrontOn() : EnderIO.blockCombustionGenerator.getFrontOff(); 
  //middle chunk
    if(scaleX) {
      textures[0] = tex;
      textures[1] = tex;
    } else {
      textures[4] = tex;
      textures[5] = tex;
    }
    
    bb = BoundingBox.UNIT_CUBE;
    bb = bb.scale(1, 0.34, 1);
    xform.set(x, y, z, facing);
    CubeRenderer.render(bb, textures, xform, cols);
    

    //top / bottom connectors
    bb = BoundingBox.UNIT_CUBE.scale(0.35,1,0.35);    
    bb = bb.translate(x, y, z);
    //CubeRenderer.render(bb, EnderIO.blockCombustionGenerator.getBlankSideIcon(), null, cols, false);
    CubeRenderer.render(bb, textures[2], null, cols, false);
    
    
    
    //tanks
    float size = 0.34f;
    bb = BoundingBox.UNIT_CUBE.scale(0.98, 1, 0.98);

    scx = scaleX ? size : 1;
    scz = scaleX ? 1 : size;
    bb = bb.scale(scx, 1, scz);

    float tx = scaleX ? size * 1.25f : 0;
    float tz = scaleX ? 0 : 0.25f * 1.25f;
    bb = bb.translate(x + tx, y, z + tz);

    tex = EnderIO.blockFusedQuartz.getDefaultFrameIcon(0);
    CubeRenderer.render(bb, tex, null, cols, false);

    bb = bb.translate(-tx * 2, 0, -tz * 2);
    CubeRenderer.render(bb, tex, null, cols, false);

    return true;
  }

  @Override
  public void renderTileEntityAt(TileEntity var1, double var2, double var4, double var6, float var8) {

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

    renderer.setRenderBoundsFromBlock(Blocks.stone);
    renderer.setOverrideBlockTexture(EnderIO.blockFusedQuartz.getDefaultFrameIcon(0));
    renderer.renderBlockAsItem(Blocks.cobblestone, 0, 1);
    renderer.setOverrideBlockTexture(null);
  }

  @Override
  public int getRenderId() {
    return BlockCombustionGenerator.renderId;
  }

  private static class VertXForm implements VertexTransform {

    int x;
    int y;
    int z;
    boolean transX;

    public VertXForm() {
    }

    void set(int x, int y, int z, short facing) {
      this.x = x;
      this.y = y;
      this.z = z;
      transX = facing != 4 && facing != 5;
    }

    @Override
    public void apply(Vector3d vec) {
      if(vec.y > 0.9 || vec.y < 0.1) {
        if(transX) {
          vec.x -= 0.5;
          vec.x *= 0.6;
          vec.x += 0.5;
        } else {
          vec.z -= 0.5;
          vec.z *= 0.6;
          vec.z += 0.5;
        }
      }

      vec.y -= 0.5;
      vec.y *= 0.8;
      vec.y += 0.5;

      vec.add(x, y, z);
    }

    @Override
    public void applyToNormal(Vector3f vec) {
    }

  }

}
