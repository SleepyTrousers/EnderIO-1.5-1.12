package crazypants.enderio.machine.monitor.v2;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import org.lwjgl.opengl.GL11;

import com.enderio.core.api.client.render.VertexTransform;
import com.enderio.core.client.render.BoundingBox;
import com.enderio.core.client.render.RenderUtil;
import com.enderio.core.client.render.VertexRotationFacing;
import com.enderio.core.common.vecmath.Vector3d;


@SideOnly(Side.CLIENT)
public class TESRPMon extends TileEntitySpecialRenderer<TilePMon> {

  private static BoundingBox bb1 = BoundingBox.UNIT_CUBE.translate(0f, 0f, -(1.25f / 16f)); // screen

  private static final VertexRotationFacing xform = new VertexRotationFacing(EnumFacing.SOUTH);
  static {
    xform.setCenter(new Vector3d(0.5, 0.5, 0.5));
  }

  @Override
  public void renderTileEntityAt(TilePMon te, double x, double y, double z, float partialTicks, int destroyStage) {
    RenderUtil.setupLightmapCoords(te.getPos(), te.getWorld());
    GL11.glPushMatrix();
    GL11.glTranslatef((float) x, (float) y, (float) z);

    RenderUtil.bindBlockTexture();
    GlStateManager.enableBlend();
    GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
    GlStateManager.disableLighting();
    WorldRenderer tes = Tessellator.getInstance().getWorldRenderer();
    tes.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR_NORMAL);

    xform.setRotation(te.getFacing());
    te.bindTexture();
    Helper helper = H.get();
    helper.setupVertices(bb1, xform);
    GL11.glColor4f(1F, 1F, 1F, 1F);
    helper.renderSingleFace(tes, EnumFacing.SOUTH, 0f, 1f, 0f, 1f, xform, helper.stdBrightness, false);

    Tessellator.getInstance().draw();
    GL11.glPopMatrix();
  }

  ThreadLocal<Helper> H = new ThreadLocal<Helper>() {
    @Override
    protected Helper initialValue() {
      return new Helper();
    }
  };

  private static class Helper {

    final Vector3d[] verts = new Vector3d[8];
    final float[] stdBrightness = new float[6];
    final float[] stdBrightnessInside = new float[6];

    Helper() {
      for (int i = 0; i < verts.length; i++) {
        verts[i] = new Vector3d();
      }

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
          return dir.rotateY(); // getRotation(EnumFacing.DOWN);
        } else if (angle >= ROTATION_AMOUNT * 1.5 && angle < ROTATION_AMOUNT * 2.5) {
          return dir.getOpposite();
        } else if (angle >= ROTATION_AMOUNT * 2.5 && angle < ROTATION_AMOUNT * 3.5) {
          return dir.rotateYCCW(); // getRotation(EnumFacing.UP);
        }
      }
      return dir;
    }

    void addVecWithUV(WorldRenderer tes, Vector3d vec, double u, double v, float cm, EnumFacing normal) {
      tes.pos(vec.x, vec.y, vec.z).tex(u, v).color(cm, cm, cm, 1)
          .normal(normal.getDirectionVec().getX(), normal.getDirectionVec().getY(), normal.getDirectionVec().getZ()).endVertex();
    }

    void renderSingleFace(WorldRenderer tes, EnumFacing face, float minU, float maxU, float minV, float maxV, VertexTransform xForm, float[] brightnessPerSide,
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
