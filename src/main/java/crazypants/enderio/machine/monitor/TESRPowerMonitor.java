package crazypants.enderio.machine.monitor;

import com.enderio.core.api.client.render.VertexTransform;
import com.enderio.core.client.render.BoundingBox;
import com.enderio.core.client.render.ManagedTESR;
import com.enderio.core.client.render.RenderUtil;
import com.enderio.core.client.render.VertexRotationFacing;
import com.enderio.core.common.vecmath.Vector3d;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nonnull;

import static crazypants.enderio.ModObject.blockPowerMonitorv2;

@SideOnly(Side.CLIENT)
public class TESRPowerMonitor extends ManagedTESR<TilePowerMonitor> {

  public TESRPowerMonitor() {
    super(blockPowerMonitorv2.getBlock());
  }

  private static final float px = 1f / 16f;
  private static BoundingBox bb1 = new BoundingBox(1 * px, 1 * px, 14.75f * px, 15 * px, 15 * px, 14.75f * px); // screen
  private static BoundingBox bb2 = new BoundingBox(0 * px, 0 * px, 16.00f * px, 16 * px, 16 * px, 16.00f * px); // screen, painted

  @Override
  protected boolean shouldRender(@Nonnull TilePowerMonitor te, @Nonnull IBlockState blockState, int renderPass) {
    return te.isAdvanced();
  }

  @Override
  protected void renderTileEntity(@Nonnull TilePowerMonitor te, @Nonnull IBlockState blockState, float partialTicks, int destroyStage) {
    boolean isPainted = te.getPaintSource() != null;

    VertexRotationFacing xform = new VertexRotationFacing(te.getFacing());
    xform.setCenter(new Vector3d(0.5, 0.5, 0.5));
    xform.setRotation(EnumFacing.SOUTH);
    te.bindTexture();
    Helper helper = threadLocalHelper.get();

    VertexBuffer tes = Tessellator.getInstance().getBuffer();
    tes.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR_NORMAL);
    if (isPainted) {
      helper.setupVertices(bb2, xform);
      helper.renderSingleFace(tes, EnumFacing.SOUTH, 0 * px, 14 * px, 0 * px, 14 * px, xform, Helper.stdBrightness, false);
    } else {
      helper.setupVertices(bb1, xform);
      helper.renderSingleFace(tes, EnumFacing.SOUTH, 1 * px, 15 * px, 1 * px, 15 * px, xform, Helper.stdBrightness, false);
    }

    Tessellator.getInstance().draw();
  }

  ThreadLocal<Helper> threadLocalHelper = new ThreadLocal<Helper>() {
    @Override
    protected Helper initialValue() {
      return new Helper();
    }
  };

  private static class Helper {

    final Vector3d[] verts = new Vector3d[8];
    final static float[] stdBrightness = new float[6];
    final static float[] stdBrightnessInside = new float[6];

    Helper() {
      for (int i = 0; i < verts.length; i++) {
        verts[i] = new Vector3d();
      }
    }

    static {
      for (EnumFacing dir : EnumFacing.values()) {
        stdBrightness[dir.ordinal()] = RenderUtil.getColorMultiplierForFace(dir);
        stdBrightnessInside[dir.ordinal()] = RenderUtil.getColorMultiplierForFace(dir) * .75f;
      }
    }

    void setupVertices(BoundingBox bound, VertexTransform xForm) {
      verts[0].set(bound.minX, bound.minY, bound.minZ);
      verts[1].set(bound.maxX, bound.minY, bound.minZ);
      verts[2].set(bound.maxX, bound.maxY, bound.minZ);
      verts[3].set(bound.minX, bound.maxY, bound.minZ);
      verts[4].set(bound.minX, bound.minY, bound.maxZ);
      verts[5].set(bound.maxX, bound.minY, bound.maxZ);
      verts[6].set(bound.maxX, bound.maxY, bound.maxZ);
      verts[7].set(bound.minX, bound.maxY, bound.maxZ);

      if (xForm != null) {
        for (Vector3d vec : verts) {
          xForm.apply(vec);
        }
      }
    }

    private static final double ROTATION_AMOUNT = Math.PI / 2;

    EnumFacing rotate(VertexTransform xForm, EnumFacing dir) {
      if (xForm instanceof VertexRotationFacing) {
        double angle = ((VertexRotationFacing) xForm).getAngle();
        if (angle < ROTATION_AMOUNT * 0.5 || angle >= ROTATION_AMOUNT * 3.5) {
          return dir;
        } else if (angle >= ROTATION_AMOUNT * 0.5 && angle < ROTATION_AMOUNT * 1.5) {
          return dir.rotateY();
        } else if (angle >= ROTATION_AMOUNT * 1.5 && angle < ROTATION_AMOUNT * 2.5) {
          return dir.getOpposite();
        } else if (angle >= ROTATION_AMOUNT * 2.5 && angle < ROTATION_AMOUNT * 3.5) {
          return dir.rotateYCCW();
        }
      }
      return dir;
    }

    void addVecWithUV(VertexBuffer tes, Vector3d vec, double u, double v, float cm, EnumFacing normal) {
      tes.pos(vec.x, vec.y, vec.z).tex(u, v).color(cm, cm, cm, 1)
          .normal(normal.getDirectionVec().getX(), normal.getDirectionVec().getY(), normal.getDirectionVec().getZ()).endVertex();
    }

    void renderSingleFace(VertexBuffer tes, EnumFacing face, float minU, float maxU, float minV, float maxV, VertexTransform xForm, float[] brightnessPerSide,
                          boolean inside) {
      EnumFacing normal = rotate(xForm, inside ? face.getOpposite() : face);

      float cm = brightnessPerSide != null ? brightnessPerSide[normal.ordinal()] : 1;

      if (inside) {
        switch (face) {
        case NORTH:
          addVecWithUV(tes, verts[0], maxU, maxV, cm, normal);
          addVecWithUV(tes, verts[1], minU, maxV, cm, normal);
          addVecWithUV(tes, verts[2], minU, minV, cm, normal);
          addVecWithUV(tes, verts[3], maxU, minV, cm, normal);
          break;
        case SOUTH:
          addVecWithUV(tes, verts[5], maxU, maxV, cm, normal);
          addVecWithUV(tes, verts[4], minU, maxV, cm, normal);
          addVecWithUV(tes, verts[7], minU, minV, cm, normal);
          addVecWithUV(tes, verts[6], maxU, minV, cm, normal);
          break;
        case UP:
          addVecWithUV(tes, verts[2], maxU, minV, cm, normal);
          addVecWithUV(tes, verts[6], maxU, maxV, cm, normal);
          addVecWithUV(tes, verts[7], minU, maxV, cm, normal);
          addVecWithUV(tes, verts[3], minU, minV, cm, normal);
          break;
        case DOWN:
          addVecWithUV(tes, verts[1], maxU, minV, cm, normal);
          addVecWithUV(tes, verts[0], minU, minV, cm, normal);
          addVecWithUV(tes, verts[4], minU, maxV, cm, normal);
          addVecWithUV(tes, verts[5], maxU, maxV, cm, normal);
          break;
        case EAST:
          addVecWithUV(tes, verts[6], minU, minV, cm, normal);
          addVecWithUV(tes, verts[2], maxU, minV, cm, normal);
          addVecWithUV(tes, verts[1], maxU, maxV, cm, normal);
          addVecWithUV(tes, verts[5], minU, maxV, cm, normal);
          break;
        case WEST:
          addVecWithUV(tes, verts[4], maxU, maxV, cm, normal);
          addVecWithUV(tes, verts[0], minU, maxV, cm, normal);
          addVecWithUV(tes, verts[3], minU, minV, cm, normal);
          addVecWithUV(tes, verts[7], maxU, minV, cm, normal);
          break;
        default:
          break;
        }
      } else {
        switch (face) {
        case NORTH:
          addVecWithUV(tes, verts[1], minU, maxV, cm, normal);
          addVecWithUV(tes, verts[0], maxU, maxV, cm, normal);
          addVecWithUV(tes, verts[3], maxU, minV, cm, normal);
          addVecWithUV(tes, verts[2], minU, minV, cm, normal);
          break;
        case SOUTH:
          addVecWithUV(tes, verts[4], minU, maxV, cm, normal);
          addVecWithUV(tes, verts[5], maxU, maxV, cm, normal);
          addVecWithUV(tes, verts[6], maxU, minV, cm, normal);
          addVecWithUV(tes, verts[7], minU, minV, cm, normal);
          break;
        case UP:
          addVecWithUV(tes, verts[6], maxU, maxV, cm, normal);
          addVecWithUV(tes, verts[2], maxU, minV, cm, normal);
          addVecWithUV(tes, verts[3], minU, minV, cm, normal);
          addVecWithUV(tes, verts[7], minU, maxV, cm, normal);
          break;
        case DOWN:
          addVecWithUV(tes, verts[0], minU, minV, cm, normal);
          addVecWithUV(tes, verts[1], maxU, minV, cm, normal);
          addVecWithUV(tes, verts[5], maxU, maxV, cm, normal);
          addVecWithUV(tes, verts[4], minU, maxV, cm, normal);
          break;
        case EAST:
          addVecWithUV(tes, verts[2], maxU, minV, cm, normal);
          addVecWithUV(tes, verts[6], minU, minV, cm, normal);
          addVecWithUV(tes, verts[5], minU, maxV, cm, normal);
          addVecWithUV(tes, verts[1], maxU, maxV, cm, normal);
          break;
        case WEST:
          addVecWithUV(tes, verts[0], minU, maxV, cm, normal);
          addVecWithUV(tes, verts[4], maxU, maxV, cm, normal);
          addVecWithUV(tes, verts[7], maxU, minV, cm, normal);
          addVecWithUV(tes, verts[3], minU, minV, cm, normal);
        default:
          break;
        }
      }
    }

  }

}
