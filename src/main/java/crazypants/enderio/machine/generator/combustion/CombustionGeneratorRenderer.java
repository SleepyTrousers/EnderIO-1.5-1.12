package crazypants.enderio.machine.generator.combustion;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.FluidTank;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import crazypants.enderio.Config;
import crazypants.enderio.EnderIO;
import crazypants.enderio.machine.AbstractMachineBlock;
import crazypants.enderio.machine.IoMode;
import crazypants.render.BoundingBox;
import crazypants.render.CubeRenderer;
import crazypants.render.CustomCubeRenderer;
import crazypants.render.CustomRenderBlocks;
import crazypants.render.IRenderFace;
import crazypants.render.RenderUtil;
import crazypants.util.ForgeDirectionOffsets;
import crazypants.vecmath.Vector3d;
import crazypants.vecmath.Vertex;

public class CombustionGeneratorRenderer extends TileEntitySpecialRenderer implements ISimpleBlockRenderingHandler {

  private CustomCubeRenderer ccr = new CustomCubeRenderer();

  private OverlayRenderer overlayRenderer = new OverlayRenderer();

  private FacingVertexTransform vt = new FacingVertexTransform();

  private TileCombustionGenerator gen;

  @Override
  public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer) {

    short facing = (short)ForgeDirection.EAST.ordinal();
    boolean active = false;
    if(world != null) {
      TileEntity te = world.getTileEntity(x, y, z);
      if(te instanceof TileCombustionGenerator) {
        gen = (TileCombustionGenerator) te;
        facing = gen.facing;
        active = gen.isActive();
      } else {
        gen = null;
      }
    }

    BoundingBox bb;
    boolean scaleX = facing != 4 && facing != 5;
    float scx;
    float scz;

    //middle chunk
    bb = BoundingBox.UNIT_CUBE;
    bb = bb.scale(1, 0.34, 1);
    vt.setFacing(facing);
    TranslatedCubeRenderer.instance.renderBoundingBox(x, y, z, block, bb, vt, null ,world != null);

    scaleX = facing != 4 && facing != 5;
    scx = scaleX ? 0.7f : 1;
    scz = scaleX ? 1 : 0.7f;

    //top 1/3
    bb = BoundingBox.UNIT_CUBE;
    bb = bb.scale(scx, 0.21, scz);
    bb = bb.translate(0, 0.26f, 0);
    TranslatedCubeRenderer.instance.renderBoundingBox(x, y, z, block, bb, vt, null ,world != null);


    //lower 1/3
    bb = BoundingBox.UNIT_CUBE;
    bb = bb.scale(scx, 0.21, scz);
    bb = bb.translate(0, -0.26f, 0);
    TranslatedCubeRenderer.instance.renderBoundingBox(x, y, z, block, bb, vt, null ,world != null);

    //top / bottom connectors
    bb = BoundingBox.UNIT_CUBE.scale(0.35, 1, 0.35);
    TranslatedCubeRenderer.instance.renderBoundingBox(x, y, z, block, bb, vt, null ,world != null);

    //tanks
    float size = 0.64f;
    bb = BoundingBox.UNIT_CUBE.scale(0.98, 1, 0.98);
    scx = scaleX ? size : 1;
    scz = scaleX ? 1 : size;
    bb = bb.scale(scx, 1, scz);

    float tx = scaleX ? 0.5f: 0;
    float tz = scaleX ? 0 : 0.5f;
    bb = bb.translate(tx, 0, tz);

    IIcon tex;
    if(Config.combustionGeneratorUseOpaqueModel) {
      tex = EnderIO.blockCombustionGenerator.getIcon(4,0);
    } else {
      tex = EnderIO.blockFusedQuartz.getDefaultFrameIcon(0);
    } 
    TranslatedCubeRenderer.instance.renderBoundingBox(x, y, z, block, bb, vt, tex, world != null);

    bb = bb.translate(-tx * 2, 0, -tz * 2);
    TranslatedCubeRenderer.instance.renderBoundingBox(x, y, z, block, bb, vt,tex, world != null);

    if(gen != null) {
      ccr.renderBlock(world, block, x, y, z, overlayRenderer);
    }
    gen = null;

    return true;
  }

  @Override
  public void renderTileEntityAt(TileEntity tileentity, double x, double y, double z, float var8) {

    TileCombustionGenerator gen = (TileCombustionGenerator) tileentity;
    if(gen.coolantTank.getFluidAmount() <= 0 && gen.fuelTank.getFluidAmount() <= 0) {
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
    tes.setTranslation(0, 0, 0);

    GL11.glPopAttrib();
    Minecraft.getMinecraft().entityRenderer.enableLightmap(0);

  }

  private void renderTank(TileCombustionGenerator gen, BoundingBox bb, FluidTank tank, boolean isLeft) {
    boolean scaleX = gen.getFacing() != 4 && gen.getFacing() != 5;
    float size = 0.34f;

    if(gen.getFacing() == 5 || gen.getFacing() == 2) {
      isLeft = !isLeft;
    }

    if(tank.getFluidAmount() > 0) {
      IIcon icon = tank.getFluid().getFluid().getStillIcon();
      if(icon != null) {
        float fullness = (float) (tank.getFluidAmount() - 1000) / (tank.getCapacity() - 1000);
        float scx = scaleX ? size : 1f;
        float scz = scaleX ? 1f : size;
        bb = bb.scale(scx, 0.97 * fullness, scz);

        float tx = scaleX ? 0.25f * 1.25f : 0;
        float tz = scaleX ? 0 : 0.25f * 1.25f;
        float ty = -(0.98f - (bb.maxY - bb.minY)) / 2;
        if(!isLeft) {
          tx = -tx;
          tz = -tz;
        }
        bb = bb.translate(tx, ty, tz);
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
    GL11.glDisable(GL11.GL_LIGHTING);
    tes.startDrawingQuads();
    renderWorldBlock(null, 0, 0, 0, block, 0, renderer);
    tes.draw();
    GL11.glEnable(GL11.GL_LIGHTING);
  }

  @Override
  public int getRenderId() {
    return BlockCombustionGenerator.renderId;
  }

  private class OverlayRenderer implements IRenderFace {

    @Override
    public void renderFace(CustomRenderBlocks rb, ForgeDirection face, Block par1Block, double x, double y, double z, IIcon texture, List<Vertex> refVertices,
        boolean translateToXyz) {

      if(gen != null && par1Block instanceof AbstractMachineBlock) {
        Vector3d offset = ForgeDirectionOffsets.offsetScaled(face, 0.01);
        Tessellator.instance.addTranslation((float) offset.x, (float) offset.y, (float) offset.z);

        IoMode mode = gen.getIoMode(face);
        IIcon tex = ((AbstractMachineBlock) par1Block).getOverlayIconForMode(mode);
        if(tex != null) {
          ccr.getCustomRenderBlocks().doDefaultRenderFace(face, par1Block, x, y, z, tex);
        }

        Tessellator.instance.addTranslation(-(float) offset.x, -(float) offset.y, -(float) offset.z);
      }

    }

  }

}
