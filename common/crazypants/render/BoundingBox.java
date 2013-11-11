package crazypants.render;

import java.util.ArrayList;
import java.util.List;

import net.minecraftforge.common.ForgeDirection;
import crazypants.util.BlockCoord;
import crazypants.vecmath.Vector2f;
import crazypants.vecmath.Vector3d;
import crazypants.vecmath.Vector3f;
import crazypants.vecmath.Vertex;

public final class BoundingBox {

  public static final BoundingBox UNIT_CUBE = new BoundingBox(0, 0, 0, 1, 1, 1);

  public final float minX;
  public final float minY;
  public final float minZ;
  public final float maxX;
  public final float maxY;
  public final float maxZ;

  public BoundingBox(float minX, float minY, float minZ, float maxX, float maxY, float maxZ) {
    this.minX = minX;
    this.minY = minY;
    this.minZ = minZ;
    this.maxX = maxX;
    this.maxY = maxY;
    this.maxZ = maxZ;
  }

  public BoundingBox(double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
    this.minX = (float) minX;
    this.minY = (float) minY;
    this.minZ = (float) minZ;
    this.maxX = (float) maxX;
    this.maxY = (float) maxY;
    this.maxZ = (float) maxZ;
  }

  public BoundingBox(Vector3d min, Vector3d max) {
    this(min.x, min.y, min.z, max.x, max.y, max.z);
  }

  public BoundingBox(BoundingBox copy) {
    this(copy.minX, copy.minY, copy.minZ, copy.maxX, copy.maxY, copy.maxZ);
  }

  public BoundingBox(BlockCoord bc) {
    this(bc.x, bc.y, bc.z, bc.x + 1, bc.y + 1, bc.z + 1);
  }

  public BoundingBox expandBy(BoundingBox other) {
    return new BoundingBox(
        Math.min(minX, other.minX), Math.min(minY, other.minY), Math.min(minZ, other.minZ),
        Math.max(maxX, other.maxX), Math.max(maxY, other.maxY), Math.max(maxZ, other.maxZ));
  }

  public boolean contains(BoundingBox other) {
    return minX >= other.minX && minY <= other.minY && minZ <= other.minZ && maxX >= other.maxX && maxY <= other.maxY && maxZ <= other.maxZ;
  }

  public boolean intersects(BoundingBox other) {
    return other.maxX > this.minX && other.minX < this.maxX ? (other.maxY > this.minY && other.minY < this.maxY ? other.maxZ > this.minZ
        && other.minZ < this.maxZ : false) : false;
  }

  public boolean isValid() {
    return minX < maxX && minY < maxY && minZ < maxZ;
  }

  public BoundingBox scale(double x, double y, double z) {
    return scale((float) x, (float) y, (float) z);
  }

  public BoundingBox scale(float x, float y, float z) {
    x = 1 - x;
    y = 1 - y;
    z = 1 - z;
    float w = ((maxX - minX) * x) / 2;
    float h = ((maxY - minY) * y) / 2;
    float d = ((maxZ - minZ) * z) / 2;
    return new BoundingBox(minX + w, minY + h, minZ + d, maxX - w, maxY - h, maxZ - d);
  }

  public BoundingBox translate(float x, float y, float z) {
    return new BoundingBox(minX + x, minY + y, minZ + z, maxX + x, maxY + y, maxZ + z);
  }

  public BoundingBox translate(Vector3d translation) {
    return translate((float) translation.x, (float) translation.y, (float) translation.z);
  }

  public BoundingBox translate(Vector3f vec) {
    return translate(vec.x, vec.y, vec.z);
  }

  public BoundingBox transform(VertexTransform iTransformation) {
    Vector3d min = new Vector3d(minX, minY, minZ);
    Vector3d max = new Vector3d(maxX, maxY, maxZ);

    iTransformation.apply(min);
    iTransformation.apply(max);

    return new BoundingBox(
        Math.min(min.x, max.x), Math.min(min.y, max.y), Math.min(min.z, max.z),
        Math.max(min.x, max.x), Math.max(min.y, max.y), Math.max(min.z, max.z));

  }

  /**
   * Returns the vertices of the corners for the specified face in counter
   * clockwise order.
   * 
   * @param face
   * @return
   */
  public List<Vertex> getCornersWithUvForFace(ForgeDirection face) {
    return getCornersWithUvForFace(face, 0, 1, 0, 1);
  }

  public List<Vertex> getCornersWithUvForFace(ForgeDirection face, float minU, float maxU, float minV, float maxV) {
    List<Vertex> result = new ArrayList<Vertex>(4);
    switch (face) {
    case NORTH:
      result.add(new Vertex(new Vector3d(maxX, minY, minZ), new Vector3f(0, 0, -1), new Vector2f(minU, minV)));
      result.add(new Vertex(new Vector3d(minX, minY, minZ), new Vector3f(0, 0, -1), new Vector2f(maxU, minV)));
      result.add(new Vertex(new Vector3d(minX, maxY, minZ), new Vector3f(0, 0, -1), new Vector2f(maxU, maxV)));
      result.add(new Vertex(new Vector3d(maxX, maxY, minZ), new Vector3f(0, 0, -1), new Vector2f(minU, maxV)));
      break;
    case SOUTH:
      result.add(new Vertex(new Vector3d(minX, minY, maxZ), new Vector3f(0, 0, 1), new Vector2f(maxU, minV)));
      result.add(new Vertex(new Vector3d(maxX, minY, maxZ), new Vector3f(0, 0, 1), new Vector2f(minU, minV)));
      result.add(new Vertex(new Vector3d(maxX, maxY, maxZ), new Vector3f(0, 0, 1), new Vector2f(minU, maxV)));
      result.add(new Vertex(new Vector3d(minX, maxY, maxZ), new Vector3f(0, 0, 1), new Vector2f(maxU, maxV)));
      break;
    case EAST:
      result.add(new Vertex(new Vector3d(maxX, maxY, minZ), new Vector3f(1, 0, 0), new Vector2f(maxU, maxV)));
      result.add(new Vertex(new Vector3d(maxX, maxY, maxZ), new Vector3f(1, 0, 0), new Vector2f(minU, maxV)));
      result.add(new Vertex(new Vector3d(maxX, minY, maxZ), new Vector3f(1, 0, 0), new Vector2f(minU, minV)));
      result.add(new Vertex(new Vector3d(maxX, minY, minZ), new Vector3f(1, 0, 0), new Vector2f(maxU, minV)));
      break;
    case WEST:
      result.add(new Vertex(new Vector3d(minX, minY, minZ), new Vector3f(-1, 0, 0), new Vector2f(maxU, minV)));
      result.add(new Vertex(new Vector3d(minX, minY, maxZ), new Vector3f(-1, 0, 0), new Vector2f(minU, minV)));
      result.add(new Vertex(new Vector3d(minX, maxY, maxZ), new Vector3f(-1, 0, 0), new Vector2f(minU, maxV)));
      result.add(new Vertex(new Vector3d(minX, maxY, minZ), new Vector3f(-1, 0, 0), new Vector2f(maxU, maxV)));
      break;
    case UP:
      result.add(new Vertex(new Vector3d(maxX, maxY, maxZ), new Vector3f(0, 1, 0), new Vector2f(minU, minV)));
      result.add(new Vertex(new Vector3d(maxX, maxY, minZ), new Vector3f(0, 1, 0), new Vector2f(minU, maxV)));
      result.add(new Vertex(new Vector3d(minX, maxY, minZ), new Vector3f(0, 1, 0), new Vector2f(maxU, maxV)));
      result.add(new Vertex(new Vector3d(minX, maxY, maxZ), new Vector3f(0, 1, 0), new Vector2f(maxU, minV)));
      break;
    case DOWN: //
    case UNKNOWN:
    default:
      result.add(new Vertex(new Vector3d(minX, minY, minZ), new Vector3f(0, -1, 0), new Vector2f(maxU, maxV)));
      result.add(new Vertex(new Vector3d(maxX, minY, minZ), new Vector3f(0, -1, 0), new Vector2f(minU, maxV)));
      result.add(new Vertex(new Vector3d(maxX, minY, maxZ), new Vector3f(0, -1, 0), new Vector2f(minU, minV)));
      result.add(new Vertex(new Vector3d(minX, minY, maxZ), new Vector3f(0, -1, 0), new Vector2f(maxU, minV)));
      break;
    }
    return result;
  }

  /**
   * Returns the vertices of the corners for the specified face in counter
   * clockwise order.
   * 
   * @param face
   * @return
   */
  public List<Vector3f> getCornersForFace(ForgeDirection face) {
    List<Vector3f> result = new ArrayList<Vector3f>(4);
    switch (face) {
    case NORTH:
      result.add(new Vector3f(maxX, minY, minZ));
      result.add(new Vector3f(minX, minY, minZ));
      result.add(new Vector3f(minX, maxY, minZ));
      result.add(new Vector3f(maxX, maxY, minZ));
      break;
    case SOUTH:
      result.add(new Vector3f(minX, minY, maxZ));
      result.add(new Vector3f(maxX, minY, maxZ));
      result.add(new Vector3f(maxX, maxY, maxZ));
      result.add(new Vector3f(minX, maxY, maxZ));
      break;
    case EAST:
      result.add(new Vector3f(maxX, maxY, minZ));
      result.add(new Vector3f(maxX, maxY, maxZ));
      result.add(new Vector3f(maxX, minY, maxZ));
      result.add(new Vector3f(maxX, minY, minZ));
      break;
    case WEST:
      result.add(new Vector3f(minX, minY, minZ));
      result.add(new Vector3f(minX, minY, maxZ));
      result.add(new Vector3f(minX, maxY, maxZ));
      result.add(new Vector3f(minX, maxY, minZ));
      break;
    case UP:
      result.add(new Vector3f(maxX, maxY, maxZ));
      result.add(new Vector3f(maxX, maxY, minZ));
      result.add(new Vector3f(minX, maxY, minZ));
      result.add(new Vector3f(minX, maxY, maxZ));
      break;
    case DOWN:
    case UNKNOWN:
    default:
      result.add(new Vector3f(minX, minY, minZ));
      result.add(new Vector3f(maxX, minY, minZ));
      result.add(new Vector3f(maxX, minY, maxZ));
      result.add(new Vector3f(minX, minY, maxZ));
      break;
    }
    return result;
  }

  public Vector3d getCenter() {
    return new Vector3d(minX + (maxX - minX) / 2, minY + (maxY - minY) / 2, minZ + (maxZ - minZ) / 2);
  }

  public float sizeX() {
    return Math.abs(maxX - minX);
  }

  public float sizeY() {
    return Math.abs(maxY - minY);
  }

  public float sizeZ() {
    return Math.abs(maxZ - minZ);
  }

  public Vector3d getMin() {
    return new Vector3d(minX, minY, minZ);
  }

  public Vector3d getMax() {
    return new Vector3d(maxX, maxY, maxZ);
  }

  @Override
  public String toString() {
    return "BoundingBox [minX=" + minX + ", minY=" + minY + ", minZ=" + minZ + ", maxX=" + maxX + ", maxY=" + maxY + ", maxZ=" + maxZ + "]";
  }

  public BoundingBox fixMinMax() {
    float mnX = minX;
    float mnY = minY;
    float mnZ = minZ;
    float mxX = maxX;
    float mxY = maxY;
    float mxZ = maxZ;
    boolean mod = false;
    if(minX > maxX) {
      mnX = maxX;
      mxX = minX;
      mod = true;
    }
    if(minY > maxY) {
      mnY = maxY;
      mxY = minY;
      mod = true;
    }
    if(minZ > maxZ) {
      mnZ = maxZ;
      mxZ = minZ;
      mod = true;
    }
    if(!mod) {
      return this;
    }
    return new BoundingBox(mnX, mnY, mnZ, mxX, mxY, mxZ);
  }

}
