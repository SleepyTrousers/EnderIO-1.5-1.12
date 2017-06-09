package crazypants.enderio.conduit.geom;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nonnull;

import com.enderio.core.client.render.BoundingBox;
import com.enderio.core.common.util.ForgeDirectionOffsets;
import com.enderio.core.common.vecmath.VecmathUtil;
import com.enderio.core.common.vecmath.Vector3d;

import crazypants.enderio.conduit.IConduit;
import net.minecraft.util.EnumFacing;

public class ConduitGeometryUtil {

  public static final ConduitGeometryUtil instance = new ConduitGeometryUtil();

  public static float STUB_WIDTH = 0.2f;
  public static float STUB_HEIGHT = 0.2f;

  public static float WIDTH;
  public static float HEIGHT;

  public static float HWIDTH;
  public static float HHEIGHT;

  // All values are for a single conduit core
  public static Vector3d CORE_MIN;
  public static Vector3d CORE_MAX;
  public static BoundingBox CORE_BOUNDS;

  public static final float CONNECTOR_DEPTH = 0.05f;

  private static Map<EnumFacing, BoundingBox[]> EXTERNAL_CONNECTOR_BOUNDS = new HashMap<EnumFacing, BoundingBox[]>();

  static {
    setupBounds(0.5f);
  }

  public static void setupBounds(float scale) {
    float size = 0.075f + (0.175f * scale);

    WIDTH = size;
    HEIGHT = size;
    HWIDTH = WIDTH / 2;
    HHEIGHT = HEIGHT / 2;

    final Vector3d core_min = new Vector3d(0.5f - HWIDTH, 0.5 - HHEIGHT, 0.5 - HWIDTH);
    CORE_MIN = core_min;
    final Vector3d core_max = new Vector3d(CORE_MIN.x + WIDTH, CORE_MIN.y + HEIGHT, CORE_MIN.z + WIDTH);
    CORE_MAX = core_max;
    CORE_BOUNDS = new BoundingBox(core_min, core_max);

    float connectorWidth = 0.25f + (scale * 0.5f);
    for (EnumFacing dir : EnumFacing.VALUES) {
      EXTERNAL_CONNECTOR_BOUNDS.put(dir, createExternalConnector(dir, CONNECTOR_DEPTH, connectorWidth));
    }
  }

  private static BoundingBox[] createExternalConnector(EnumFacing dir, float connectorDepth, float connectorWidth) {

    BoundingBox[] res = new BoundingBox[2];

    float cMin = 0.5f - connectorWidth / 2;
    float cMax = 0.5f + connectorWidth / 2;
    float dMin = 1 - connectorDepth / 2;
    float dMax = 1;

    res[0] = createConnectorComponent(dir, cMin, cMax, dMin, dMax);

    cMin = 0.5f - connectorWidth / 3;
    cMax = 0.5f + connectorWidth / 3;
    dMin = 1 - connectorDepth;
    dMax = 1 - connectorDepth / 2;

    res[1] = createConnectorComponent(dir, cMin, cMax, dMin, dMax);

    return res;
  }

  private static BoundingBox createConnectorComponent(EnumFacing dir, float cornerMin, float cornerMax, float depthMin, float depthMax) {
    float minX = (1 - Math.abs(dir.getFrontOffsetX())) * cornerMin + dir.getFrontOffsetX() * depthMin;
    float minY = (1 - Math.abs(dir.getFrontOffsetY())) * cornerMin + dir.getFrontOffsetY() * depthMin;
    float minZ = (1 - Math.abs(dir.getFrontOffsetZ())) * cornerMin + dir.getFrontOffsetZ() * depthMin;

    float maxX = (1 - Math.abs(dir.getFrontOffsetX())) * cornerMax + (dir.getFrontOffsetX() * depthMax);
    float maxY = (1 - Math.abs(dir.getFrontOffsetY())) * cornerMax + (dir.getFrontOffsetY() * depthMax);
    float maxZ = (1 - Math.abs(dir.getFrontOffsetZ())) * cornerMax + (dir.getFrontOffsetZ() * depthMax);

    minX = fix(minX);
    minY = fix(minY);
    minZ = fix(minZ);
    maxX = fix(maxX);
    maxY = fix(maxY);
    maxZ = fix(maxZ);

    BoundingBox bb = new BoundingBox(minX, minY, minZ, maxX, maxY, maxZ);
    bb = bb.fixMinMax();

    return bb;
  }

  private static float fix(float val) {
    return val < 0 ? 1 + val : val;
  }

  private Map<GeometryKey, BoundingBox> boundsCache = new HashMap<GeometryKey, BoundingBox>();

  private ConduitGeometryUtil() {
  }

  public BoundingBox getExternalConnectorBoundingBox(EnumFacing dir) {
    return getExternalConnectorBoundingBoxes(dir)[0];
  }

  public BoundingBox[] getExternalConnectorBoundingBoxes(EnumFacing dir) {
    return EXTERNAL_CONNECTOR_BOUNDS.get(dir);
  }

  public BoundingBox getBoundingBox(Class<? extends IConduit> type, EnumFacing dir, boolean isStub, Offset offset) {
    GeometryKey key = new GeometryKey(dir, isStub, offset, type);
    BoundingBox result = boundsCache.get(key);
    if (result == null) {
      result = createConduitBounds(type, key);
      boundsCache.put(key, result);
    }
    return result;
  }

  public @Nonnull Vector3d getTranslation(EnumFacing dir, Offset offset) {
    Vector3d result = new Vector3d(offset.xOffset, offset.yOffset, offset.zOffset);
    result.scale(WIDTH);
    return result;
  }

  public BoundingBox createBoundsForConnectionController(EnumFacing dir, Offset offset) {

    Vector3d nonUniformScale = ForgeDirectionOffsets.forDirCopy(dir);
    nonUniformScale.scale(0.5);

    nonUniformScale.x = 0.8 * (1 - Math.abs(nonUniformScale.x));
    nonUniformScale.y = 0.8 * (1 - Math.abs(nonUniformScale.y));
    nonUniformScale.z = 0.8 * (1 - Math.abs(nonUniformScale.z));

    BoundingBox bb = CORE_BOUNDS;
    bb = bb.scale(nonUniformScale.x, nonUniformScale.y, nonUniformScale.z);

    double offsetFromEnd = Math.min(bb.sizeX(), bb.sizeY());
    offsetFromEnd = Math.min(offsetFromEnd, bb.sizeZ());
    offsetFromEnd = Math.max(offsetFromEnd, 0.075);
    double transMag = 0.5 - (offsetFromEnd * 1.2);

    Vector3d trans = ForgeDirectionOffsets.forDirCopy(dir);
    trans.scale(transMag);
    bb = bb.translate(trans);
    bb = bb.translate(getTranslation(dir, offset));
    return bb;
  }

  private BoundingBox createConduitBounds(Class<? extends IConduit> type, GeometryKey key) {
    return createConduitBounds(type, key.dir, key.isStub, key.offset);
  }

  private BoundingBox createConduitBounds(Class<? extends IConduit> type, EnumFacing dir, boolean isStub, Offset offset) {
    BoundingBox bb = CORE_BOUNDS;

    Vector3d min = bb.getMin();
    Vector3d max = bb.getMax();

    if (dir != null) {
      switch (dir) {
      case WEST:
        min.x = isStub ? Math.max(0, bb.minX - STUB_WIDTH) : 0;
        max.x = bb.minX;
        break;
      case EAST:
        min.x = bb.maxX;
        max.x = isStub ? Math.min(1, bb.maxX + STUB_WIDTH) : 1;
        break;
      case DOWN:
        min.y = isStub ? Math.max(0, bb.minY - STUB_HEIGHT) : 0;
        max.y = bb.minY;
        break;
      case UP:
        max.y = isStub ? Math.min(1, bb.maxY + STUB_HEIGHT) : 1;
        min.y = bb.maxY;
        break;
      case NORTH:
        min.z = isStub ? Math.max(0.0F, bb.minZ - STUB_WIDTH) : 0;
        max.z = bb.minZ;
        break;
      case SOUTH:
        max.z = isStub ? Math.min(1F, bb.maxZ + STUB_WIDTH) : 1;
        min.z = bb.maxZ;
        break;
      default:
        break;
      }
    }

    Vector3d trans = getTranslation(dir, offset);
    min.add(trans);
    max.add(trans);
    bb = new BoundingBox(VecmathUtil.clamp(min, 0, 1), VecmathUtil.clamp(max, 0, 1));
    return bb;
  }

}
