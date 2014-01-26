package crazypants.enderio.machine.power;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.util.Icon;
import net.minecraftforge.common.ForgeDirection;
import crazypants.render.BoundingBox;
import crazypants.render.RenderUtil;
import crazypants.util.BlockCoord;
import crazypants.vecmath.Vector2f;
import crazypants.vecmath.Vector4d;

class GaugeBounds {

  private static final BlockCoord DEFAULT_BC = new BlockCoord(0, 0, 0);
  private static final BlockCoord[] DEFAULT_MB = new BlockCoord[] { DEFAULT_BC };

  static List<GaugeBounds> calculateGaugeBounds(BlockCoord me, BlockCoord[] mbIn) {

    BlockCoord myBC;
    BlockCoord[] mb;
    if(mbIn != null) {
      myBC = me;
      mb = mbIn;
    } else {
      myBC = me;
      DEFAULT_MB[0] = me;
      mb = DEFAULT_MB;
    }

    List<GaugeBounds> res = new ArrayList<GaugeBounds>();
    for (ForgeDirection face : ForgeDirection.VALID_DIRECTIONS) {
      if(face != ForgeDirection.UP && face != ForgeDirection.DOWN) {
        boolean isRight = isRightFace(me, mb, face);
        if(isRight) {
          res.add(new GaugeBounds(me, mb, face));
        }
      }
    }
    return res;
  }

  static boolean isRightFace(BlockCoord me, BlockCoord[] mb, ForgeDirection dir) {

    if(me == null || contains(mb, me.getLocation(dir))) {
      return false;
    }
    if(mb == null) {
      return true;
    }
    Vector4d uPlane = RenderUtil.getUPlaneForFace(dir);

    int myRightVal = (int) uPlane.x * me.x + (int) uPlane.y * me.y + (int) uPlane.z * me.z;
    int max = myRightVal;
    for (BlockCoord bc : mb) {
      int val = (int) uPlane.x * bc.x + (int) uPlane.y * bc.y + (int) uPlane.z * bc.z;
      if(val > max) {
        max = val;
      }
    }
    return myRightVal == max;
  }

  private static boolean contains(BlockCoord[] mb, BlockCoord location) {
    if(mb == null) {
      return false;
    }
    for (BlockCoord bc : mb) {
      if(location.equals(bc)) {
        return true;
      }
    }
    return false;
  }

  final BoundingBox bb;
  final VInfo vInfo;
  final ForgeDirection face;

  GaugeBounds(BlockCoord me, BlockCoord[] mb, ForgeDirection face) {
    this.face = face;
    vInfo = getVPosForFace(me, mb, face);

    Vector4d uPlane = RenderUtil.getUPlaneForFace(face);
    float scaleX = uPlane.x != 0 ? 0.25f : 1;
    float scaleY = uPlane.y != 0 ? 0.25f : 1;
    float scaleZ = uPlane.z != 0 ? 0.25f : 1;
    bb = BoundingBox.UNIT_CUBE.scale(scaleX, scaleY, scaleZ);
  }

  Vector2f getMinMaxU(Icon icon) {
    VPos yPos = vInfo.pos;
    float uWidth = icon.getMaxU() - icon.getMinU();
    float uOffset = yPos.uOffset * uWidth;
    float minU = icon.getMinU() + uOffset;
    float maxU = minU + (uWidth * 0.25f);
    return new Vector2f(minU, maxU);
  }

  private VInfo getVPosForFace(BlockCoord me, BlockCoord[] mb, ForgeDirection face) {
    int maxY = me.y;
    int minY = me.y;
    int vHeight = 1;
    for (BlockCoord bc : mb) {
      if(bc.x == me.x && bc.z == me.z && !containsLocaction(mb, bc.getLocation(face))) {
        maxY = Math.max(maxY, bc.y);
        minY = Math.min(minY, bc.y);
      }
    }
    if(maxY == me.y && minY == me.y) {
      return new VInfo(VPos.SINGLE_BLOCK, 1, 0);
    }
    int height = maxY - minY + 1;
    if(maxY > me.y) {
      return me.y > minY ? new VInfo(VPos.MIDDLE, height, me.y - minY) : new VInfo(VPos.BOTTOM, height, 0);
    }
    return new VInfo(VPos.TOP, height, height - 1);
  }

  private boolean containsLocaction(BlockCoord[] mb, BlockCoord location) {
    for (BlockCoord bc : mb) {
      if(location.equals(bc)) {
        return true;
      }
    }
    return false;
  }

  enum VPos {

    SINGLE_BLOCK(0, 10, 3),
    BOTTOM(0.5f, 13, 3),
    MIDDLE(0.75f, 16, 0),
    TOP(0.25f, 13, 0);

    final float uOffset;
    final int numFillPixels;
    final int fillOffset;

    private VPos(float uOffset, int numFillPixels, int fillOffset) {
      this.uOffset = uOffset;
      this.numFillPixels = numFillPixels;
      this.fillOffset = fillOffset;
    }

  }

  static class VInfo {
    VPos pos;
    int verticalHeight;
    int index;

    VInfo(VPos pos, int verticalHeight, int index) {
      this.pos = pos;
      this.verticalHeight = verticalHeight;
      this.index = index;
    }

  }

}