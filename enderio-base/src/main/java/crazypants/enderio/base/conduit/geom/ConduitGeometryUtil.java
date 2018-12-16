package crazypants.enderio.base.conduit.geom;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.api.client.render.VertexTransform;
import com.enderio.core.client.render.BoundingBox;
import com.enderio.core.client.render.RenderUtil;
import com.enderio.core.client.render.VertexRotation;
import com.enderio.core.client.render.VertexTransformComposite;
import com.enderio.core.client.render.VertexTranslation;
import com.enderio.core.common.util.ForgeDirectionOffsets;
import com.enderio.core.common.util.NNList;
import com.enderio.core.common.util.NNList.NNIterator;
import com.enderio.core.common.vecmath.VecmathUtil;
import com.enderio.core.common.vecmath.Vector3d;
import com.enderio.core.common.vecmath.Vector3f;
import com.enderio.core.common.vecmath.Vector4f;
import com.enderio.core.common.vecmath.Vertex;

import crazypants.enderio.base.EnderIO;
import crazypants.enderio.base.conduit.IConduit;
import crazypants.enderio.base.config.config.PersonalConfig;
import crazypants.enderio.base.events.EnderIOLifecycleEvent;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import static com.enderio.core.common.util.ForgeDirectionOffsets.offsetScaled;

@EventBusSubscriber(modid = EnderIO.MODID)
public final class ConduitGeometryUtil {

  private static @Nonnull ConduitGeometryUtil instance = new ConduitGeometryUtil(3 / 16f);

  public static @Nonnull ConduitGeometryUtil getInstance() {
    return instance;
  }

  private final float CONNECTOR_DEPTH = 0.05f;

  private final float WIDTH;
  private final float HEIGHT;

  private final float HWIDTH;
  private final float HHEIGHT;

  // All values are for a single conduit core
  private final @Nonnull BoundingBox CORE_BOUNDS;

  private final @Nonnull Map<EnumFacing, BoundingBox[]> EXTERNAL_CONNECTOR_BOUNDS = new EnumMap<>(EnumFacing.class);

  @SubscribeEvent
  public static void preInit(EnderIOLifecycleEvent.Config.Post event) {
    instance = new ConduitGeometryUtil((1 / 16f) * PersonalConfig.conduitPixels.get());
  }

  private @Nonnull BoundingBox[] createExternalConnector(@Nonnull EnumFacing dir, float connectorDepth, float connectorWidth) {

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

  private @Nonnull BoundingBox createConnectorComponent(@Nonnull EnumFacing dir, float cornerMin, float cornerMax, float depthMin, float depthMax) {
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

  private final @Nonnull Map<GeometryKey, BoundingBox> boundsCache = new HashMap<GeometryKey, BoundingBox>();

  private ConduitGeometryUtil(float size) {
    WIDTH = size;
    HEIGHT = size;
    HWIDTH = WIDTH / 2;
    HHEIGHT = HEIGHT / 2;

    final Vector3d core_min = new Vector3d(0.5f - HWIDTH, 0.5 - HHEIGHT, 0.5 - HWIDTH);
    final Vector3d core_max = new Vector3d(core_min.x + WIDTH, core_min.y + HEIGHT, core_min.z + WIDTH);
    CORE_BOUNDS = new BoundingBox(core_min, core_max);

    float connectorWidth = Math.min((2 / 16f) + (size * 3), 1);
    for (NNIterator<EnumFacing> itr = NNList.FACING.fastIterator(); itr.hasNext();) {
      EnumFacing dir = itr.next();
      EXTERNAL_CONNECTOR_BOUNDS.put(dir, createExternalConnector(dir, CONNECTOR_DEPTH, connectorWidth));
    }

    createIORingVerts();
  }

  @SuppressWarnings("null")
  public @Nonnull BoundingBox getExternalConnectorBoundingBox(@Nonnull EnumFacing dir) {
    return getExternalConnectorBoundingBoxes(dir)[0];
  }

  @SuppressWarnings("null")
  public @Nonnull BoundingBox[] getExternalConnectorBoundingBoxes(@Nonnull EnumFacing dir) {
    return EXTERNAL_CONNECTOR_BOUNDS.get(dir);
  }

  public @Nonnull BoundingBox getBoundingBox(@Nonnull Class<? extends IConduit> type, EnumFacing dir, @Nonnull Offset offset) {
    GeometryKey key = new GeometryKey(dir, offset, type);
    BoundingBox result = boundsCache.get(key);
    if (result == null) {
      result = createConduitBounds(type, key);
      boundsCache.put(key, result);
    }
    return result;
  }

  public @Nonnull Vector3d getTranslation(EnumFacing dir, @Nonnull Offset offset) {
    Vector3d result = new Vector3d(offset.xOffset, offset.yOffset, offset.zOffset);
    result.scale(WIDTH);
    return result;
  }

  public @Nonnull BoundingBox createBoundsForConnectionController(@Nonnull EnumFacing dir, @Nonnull Offset offset) {

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

  private @Nonnull BoundingBox createConduitBounds(@Nonnull Class<? extends IConduit> type, @Nonnull GeometryKey key) {
    return createConduitBounds(type, key.dir, key.offset);
  }

  private @Nonnull BoundingBox createConduitBounds(Class<? extends IConduit> type, EnumFacing dir, @Nonnull Offset offset) {
    BoundingBox bb = CORE_BOUNDS;

    Vector3d min = bb.getMin();
    Vector3d max = bb.getMax();

    if (dir != null) {
      switch (dir) {
      case WEST:
        min.x = 0;
        max.x = bb.minX;
        break;
      case EAST:
        min.x = bb.maxX;
        max.x = 1;
        break;
      case DOWN:
        min.y = 0;
        max.y = bb.minY;
        break;
      case UP:
        min.y = bb.maxY;
        max.y = 1;
        break;
      case NORTH:
        min.z = 0;
        max.z = bb.minZ;
        break;
      case SOUTH:
        min.z = bb.maxZ;
        max.z = 1;
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

  public float getHeight() {
    return HEIGHT;
  }

  // IO rings START

  private final @Nonnull EnumMap<EnumFacing, List<Vertex>> IO_RING_VERTS = new EnumMap<EnumFacing, List<Vertex>>(EnumFacing.class);

  private void createIORingVerts() {

    float scale = 0.9f;
    BoundingBox refBB = CORE_BOUNDS;
    refBB = refBB.scale(scale, scale, scale);
    refBB = refBB.scale(scale, 1, 1);

    double offset = (HWIDTH * scale * scale) + CONNECTOR_DEPTH;

    EnumFacing dir;
    Vector3d trans;

    VertexRotation vrot = new VertexRotation(Math.PI / 2, new Vector3d(0, 1, 0), new Vector3d(0.5, 0.5, 0.5));
    VertexTranslation vtrans = new VertexTranslation(0, 0, 0);
    VertexTransformComposite xform = new VertexTransformComposite(vrot, vtrans);

    dir = EnumFacing.SOUTH;
    trans = offsetScaled(dir, 0.5);
    trans.sub(offsetScaled(dir, offset));
    vtrans.set(trans);
    IO_RING_VERTS.put(dir, createVerticesForDir(refBB, xform));

    dir = EnumFacing.NORTH;
    vrot.setAngle(Math.PI + Math.PI / 2);
    trans = offsetScaled(dir, 0.5);
    trans.sub(offsetScaled(dir, offset));
    vtrans.set(trans);
    IO_RING_VERTS.put(dir, createVerticesForDir(refBB, xform));

    dir = EnumFacing.EAST;
    vrot.setAngle(Math.PI);
    trans = offsetScaled(dir, 0.5);
    trans.sub(offsetScaled(dir, offset));
    vtrans.set(trans);
    IO_RING_VERTS.put(dir, createVerticesForDir(refBB, xform));

    dir = EnumFacing.WEST;
    vrot.setAngle(0);
    trans = offsetScaled(dir, 0.5);
    trans.sub(offsetScaled(dir, offset));
    vtrans.set(trans);
    IO_RING_VERTS.put(dir, createVerticesForDir(refBB, xform));

    vrot.setAxis(new Vector3d(0, 0, 1));

    dir = EnumFacing.UP;
    vrot.setAngle(-Math.PI / 2);
    trans = offsetScaled(dir, 0.5);
    trans.sub(offsetScaled(dir, offset));
    vtrans.set(trans);
    IO_RING_VERTS.put(dir, createVerticesForDir(refBB, xform));

    dir = EnumFacing.DOWN;
    vrot.setAngle(Math.PI / 2);
    trans = offsetScaled(dir, 0.5);
    trans.sub(offsetScaled(dir, offset));
    vtrans.set(trans);
    IO_RING_VERTS.put(dir, createVerticesForDir(refBB, xform));

  }

  private @Nullable List<Vertex> createVerticesForDir(BoundingBox refBB, VertexTransform xform) {
    List<Vertex> result = new ArrayList<Vertex>(24);
    for (EnumFacing face : EnumFacing.VALUES) {
      if (face != null) {
        result.addAll(refBB.getCornersWithUvForFace(face));
      }
    }
    for (Vertex v : result) {
      xform.apply(v.xyz);
      Vector3f normal = v.normal;
      if (normal != null) {
        xform.applyToNormal(normal);
      }

    }
    return result;
  }

  @SideOnly(Side.CLIENT)
  public void addModeConnectorQuads(EnumFacing dir, @Nonnull Offset offset, @Nonnull TextureAtlasSprite tex, Vector4f color, @Nonnull List<BakedQuad> quads) {
    List<Vertex> verts = IO_RING_VERTS.get(dir);
    if (verts == null) {
      return;
    }
    Vector3d trans = getTranslation(dir, offset);
    List<Vertex> xFormed = new ArrayList<Vertex>(verts.size());
    for (Vertex v : verts) {
      Vertex xf = new Vertex(v);
      xf.xyz.add(trans);
      xFormed.add(xf);
    }
    RenderUtil.addBakedQuads(quads, xFormed, tex, color);
  }

  // IO rings END

}
