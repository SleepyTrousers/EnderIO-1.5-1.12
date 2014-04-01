package crazypants.enderio.machine.generator;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.FluidTank;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import crazypants.enderio.EnderIO;
import crazypants.render.BoundingBox;
import crazypants.render.CubeRenderer;
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
      textures[4] = EnderIO.blockCombustionGenerator.getIcon(world, x, y, z, ForgeDirection.EAST.ordinal());
      textures[5] = EnderIO.blockCombustionGenerator.getIcon(world, x, y, z, ForgeDirection.WEST.ordinal());
    } else {
      textures[0] = EnderIO.blockCombustionGenerator.getIcon(ForgeDirection.NORTH.ordinal(), 0);
      textures[1] = EnderIO.blockCombustionGenerator.getIcon(ForgeDirection.SOUTH.ordinal(), 0);
      textures[2] = EnderIO.blockCombustionGenerator.getIcon(ForgeDirection.UP.ordinal(), 0);
      textures[3] = EnderIO.blockCombustionGenerator.getIcon(ForgeDirection.DOWN.ordinal(), 0);
      textures[5] = EnderIO.blockCombustionGenerator.getIcon(ForgeDirection.EAST.ordinal(), 0);
      textures[4] = EnderIO.blockCombustionGenerator.getIcon(ForgeDirection.WEST.ordinal(), 0);
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



    //middle chunk
    BoundingBox bb = BoundingBox.UNIT_CUBE;
    bb = BoundingBox.UNIT_CUBE;
    bb = bb.scale(1, 0.34, 1);
    xform.set(x, y, z, facing);
    CubeRenderer.render(bb, textures, xform, cols);


    boolean scaleX = facing != 4 && facing != 5;
    float scx = scaleX ? 0.7f : 1;
    float scz = scaleX ? 1 : 0.7f;

    //change the front texture to blank for the top and bottom sections
    if(scaleX) {
      textures[0] = EnderIO.blockCombustionGenerator.getBackIcon();
      textures[1] = EnderIO.blockCombustionGenerator.getBackIcon();
    } else {
      textures[4] = EnderIO.blockCombustionGenerator.getBackIcon();
      textures[5] = EnderIO.blockCombustionGenerator.getBackIcon();
    }

    //top 1/3
    bb = BoundingBox.UNIT_CUBE;
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


    //top / bottom connectors
    bb = BoundingBox.UNIT_CUBE.scale(0.35,1,0.35);
    bb = bb.translate(x, y, z);
    CubeRenderer.render(bb, textures[2], null, cols, false);

    //tanks
    float size = 0.34f;
    bb = BoundingBox.UNIT_CUBE.scale(0.98, 0.98, 0.98);

    scx = scaleX ? size : 1;
    scz = scaleX ? 1 : size;
    bb = bb.scale(scx, 1, scz);

    float tx = scaleX ? size * 1.25f : 0;
    float tz = scaleX ? 0 : 0.25f * 1.25f;
    bb = bb.translate(x + tx, y, z + tz);

    IIcon tex = EnderIO.blockFusedQuartz.getDefaultFrameIcon(0);
    CubeRenderer.render(bb, tex, null, cols, false);

    bb = bb.translate(-tx * 2, 0, -tz * 2);
    CubeRenderer.render(bb, tex, null, cols, false);

    return true;
  }

  @Override
  public void renderTileEntityAt(TileEntity tileentity, double x, double y, double z, float var8) {

    TileCombustionGenerator gen = (TileCombustionGenerator) tileentity;
    if(gen.coolantTank.getFluidAmount() <= 0 && gen.fuelTank.getFluidAmount() <=0 ) {
      return;
    }


    Minecraft.getMinecraft().entityRenderer.disableLightmap(0);

    float val = RenderUtil.claculateTotalBrightnessForLocation(tileentity.getWorldObj(), tileentity.xCoord, tileentity.yCoord, tileentity.zCoord);
    GL11.glColor3f(val, val, val);

    GL11.glPushAttrib(GL11.GL_ENABLE_BIT);
    GL11.glEnable(GL11.GL_CULL_FACE);
    GL11.glDisable(GL11.GL_LIGHTING);
    GL11.glEnable(GL11.GL_BLEND);
    GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

    RenderUtil.bindBlockTexture();
    Tessellator tes = Tessellator.instance;
    tes.setTranslation(x, y, z);
    tes.startDrawingQuads();

    BoundingBox bb = BoundingBox.UNIT_CUBE.scale(0.96, 0.96, 0.96);
    renderTank(gen, bb, gen.coolantTank, true);
    renderTank(gen, bb, gen.fuelTank, false);
    tes.draw();
    tes.setTranslation(0,0,0);

    GL11.glPopAttrib();
    Minecraft.getMinecraft().entityRenderer.enableLightmap(0);

  }

  private void renderTank(TileCombustionGenerator gen, BoundingBox bb, FluidTank tank, boolean isLeft) {
    boolean scaleX = gen.getFacing() != 4 && gen.getFacing() != 5;
    float size = 0.34f;
    if(tank.getFluidAmount() > 0) {
      IIcon icon = tank.getFluid().getFluid().getStillIcon();
      if(icon != null) {
        float fullness = (float)(tank.getFluidAmount() - 1000) / (tank.getCapacity() - 1000);
        float scx = scaleX ? size : 1f;
        float scz = scaleX ? 1f : size;
        bb = bb.scale(scx, 0.97 * fullness, scz);

        float tx = scaleX ? size * 1.25f : 0;
        float tz = scaleX ? 0 : 0.25f * 1.25f;
        float ty = -(0.98f - (bb.maxY - bb.minY)) / 2;
        if(!isLeft) {
          tx = -tx;
          tz = -tz;
        }
        bb = bb.translate(tx, ty ,tz);
        CubeRenderer.render(bb, icon);
      }
    }
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
