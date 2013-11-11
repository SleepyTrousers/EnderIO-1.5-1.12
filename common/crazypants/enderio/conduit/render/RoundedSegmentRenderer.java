package crazypants.enderio.conduit.render;

import static crazypants.util.ForgeDirectionOffsets.offsetScaled;
import static net.minecraftforge.common.ForgeDirection.SOUTH;

import java.util.List;

import net.minecraft.client.renderer.Tessellator;
import net.minecraftforge.common.ForgeDirection;
import crazypants.enderio.conduit.geom.ConduitGeometryUtil;
import crazypants.enderio.conduit.geom.Offsets;
import crazypants.enderio.conduit.geom.Offsets.Axis;
import crazypants.render.BoundingBox;
import crazypants.vecmath.Matrix4d;
import crazypants.vecmath.Vector2f;
import crazypants.vecmath.Vector3d;
import crazypants.vecmath.Vector3f;
import crazypants.vecmath.Vertex;

public class RoundedSegmentRenderer {

  private static final int NUM_VERTICES = 16;
  private static Vertex[][] DIR_COORDS = new Vertex[ForgeDirection.VALID_DIRECTIONS.length][];
  private static Vertex[][] DIR_STUB_COORDS = new Vertex[ForgeDirection.VALID_DIRECTIONS.length][];
  private static Vertex[][] DIR_STUB_CAPS = new Vertex[ForgeDirection.VALID_DIRECTIONS.length][];

  private static final Vector3d REF_TRANS = new Vector3d(0.5, 0.5, 0.5);

  static {
    double circ = ConduitGeometryUtil.WIDTH * 0.7;

    double halfLength = 0.25;
    createDirectionSegments(DIR_COORDS, circ, halfLength);

    halfLength = ConduitGeometryUtil.STUB_WIDTH / 2 + ConduitGeometryUtil.CORE_BOUNDS.sizeX() / 4;
    createDirectionSegments(DIR_STUB_COORDS, circ, halfLength);

    createCaps(DIR_STUB_CAPS, circ, halfLength);

  }

  private static void createDirectionSegments(Vertex[][] segments, double circ, double halfLength) {
    Vertex[] refCoords = createUnitSectionQuads(NUM_VERTICES, -halfLength, halfLength);

    createSegmentsForDirections(segments, circ, halfLength, refCoords);
  }

  private static void createCaps(Vertex[][] segments, double circ, double halfLength) {
    Vertex[] refCrossSection = createUnitCrossSection(0, 0, halfLength, 16, 0);

    Vertex center = new Vertex(new Vector3d(0, 0, halfLength), new Vector3f(0, 0, 1), new Vector2f(0.5f, 0.5f));

    Vertex[] refCoords = new Vertex[refCrossSection.length * 4];
    int index = 0;
    for (int i = 0; i < refCrossSection.length; i++) {
      refCoords[index] = new Vertex(center);
      refCoords[index + 1] = new Vertex(refCrossSection[i]);

      int next = i + 1;
      if(next >= refCrossSection.length - 1) {
        next = 0;
      }
      refCoords[index + 2] = new Vertex(refCrossSection[next]);
      refCoords[index + 3] = new Vertex(center);
      index += 4;
    }

    createSegmentsForDirections(segments, circ, halfLength, refCoords);

  }

  private static void createSegmentsForDirections(Vertex[][] segments, double circ, double halfLength, Vertex[] refCoords) {
    for (Vertex coord : refCoords) {
      coord.xyz.x = coord.xyz.x * circ;
      coord.xyz.y = coord.xyz.y * circ;
    }
    Matrix4d rotMat = new Matrix4d();
    rotMat.setIdentity();
    rotMat.setTranslation(REF_TRANS);

    segments[SOUTH.ordinal()] = xformCoords(refCoords, rotMat, offsetScaled(ForgeDirection.SOUTH, halfLength));

    rotMat.makeRotationY(Math.PI);
    rotMat.setTranslation(REF_TRANS);
    segments[ForgeDirection.NORTH.ordinal()] = xformCoords(refCoords, rotMat, offsetScaled(ForgeDirection.NORTH, halfLength));

    rotMat.makeRotationY(Math.PI / 2);
    rotMat.setTranslation(REF_TRANS);
    segments[ForgeDirection.EAST.ordinal()] = xformCoords(refCoords, rotMat, offsetScaled(ForgeDirection.EAST, halfLength));

    rotMat.makeRotationY(-Math.PI / 2);
    rotMat.setTranslation(REF_TRANS);
    segments[ForgeDirection.WEST.ordinal()] = xformCoords(refCoords, rotMat, offsetScaled(ForgeDirection.WEST, halfLength));

    rotMat.makeRotationX(-Math.PI / 2);
    rotMat.setTranslation(REF_TRANS);
    segments[ForgeDirection.UP.ordinal()] = xformCoords(refCoords, rotMat, offsetScaled(ForgeDirection.UP, halfLength));

    rotMat.makeRotationX(Math.PI / 2);
    rotMat.setTranslation(REF_TRANS);
    segments[ForgeDirection.DOWN.ordinal()] = xformCoords(refCoords, rotMat, offsetScaled(ForgeDirection.DOWN, halfLength));
  }

  private static Vertex[] xformCoords(Vertex[] refCoords, Matrix4d rotMat, Vector3d trans) {
    Vertex[] res = new Vertex[refCoords.length];
    for (int i = 0; i < res.length; i++) {
      res[i] = new Vertex(refCoords[i]);
      res[i].transform(rotMat);
      res[i].translate(trans);
    }
    return res;
  }

  private static Vertex[] xformCoords(List<Vertex> refCoords, Matrix4d rotMat, Vector3d trans) {
    Vertex[] res = new Vertex[refCoords.size()];
    for (int i = 0; i < res.length; i++) {
      res[i] = new Vertex(refCoords.get(i));
      res[i].transform(rotMat);
      res[i].translate(trans);
    }
    return res;
  }

  public static Vertex[] createUnitCrossSection(double xOffset, double yOffset, double zOffset, int numCoords, int u) {

    Vertex[] crossSection = new Vertex[numCoords];

    double angle = 0;
    double inc = (Math.PI * 2) / (crossSection.length - 1);
    for (int i = 0; i < crossSection.length; i++) {
      double x = Math.cos(angle) * 0.5;
      double y = Math.sin(angle) * 0.5;
      angle += inc;
      crossSection[i] = new Vertex();
      crossSection[i].setXYZ(xOffset + x, yOffset + y, zOffset);
      crossSection[i].setNormal(x, y, 0);
      crossSection[i].setUV(u, y + 0.5);
    }
    return crossSection;

  }

  public static void renderSegment(ForgeDirection dir, BoundingBox bounds, float minU, float maxU, float minV, float maxV, boolean isStub) {
    float uScale = maxU - minU;
    float vScale = maxV - minV;

    Vector3d offset = calcOffset(dir, bounds);

    Tessellator tes = Tessellator.instance;
    Vertex[] coords;
    if(isStub) {
      coords = DIR_STUB_COORDS[dir.ordinal()];
    } else {
      coords = DIR_COORDS[dir.ordinal()];
    }

    for (Vertex coord : coords) {
      double u = minU + (coord.uv.x * uScale);
      double v = minV + (coord.uv.y * vScale);
      tes.setNormal(coord.normal.x, coord.normal.y, coord.normal.z);
      tes.addVertexWithUV(offset.x + coord.xyz.x, offset.y + coord.xyz.y, offset.z + coord.xyz.z, u, v);
    }

    if(isStub) {
      coords = DIR_STUB_CAPS[dir.ordinal()];
      for (Vertex coord : coords) {
        double u = minU + (coord.uv.x * uScale);
        double v = minV + (coord.uv.y * vScale);
        tes.setNormal(coord.normal.x, coord.normal.y, coord.normal.z);
        tes.addVertexWithUV(offset.x + coord.xyz.x, offset.y + coord.xyz.y, offset.z + coord.xyz.z, u, v);
      }
    }
  }

  private static Vector3d calcOffset(ForgeDirection dir, BoundingBox bounds) {
    Vector3d res = new Vector3d();
    Vector3d center = bounds.getCenter();
    Axis axis = Offsets.getAxisForDir(dir);
    if(axis == Axis.X) {
      res.set(0, center.y - REF_TRANS.y, center.z - REF_TRANS.z);
    } else if(axis == Axis.Y) {
      res.set(center.x - REF_TRANS.x, 0, center.z - REF_TRANS.z);
    } else if(axis == Axis.Z) {
      res.set(center.x - REF_TRANS.x, center.y - REF_TRANS.y, 0);
    }
    return res;
  }

  public static Vertex[] createUnitSectionQuads(int numCoords, double minZ, double maxZ) {

    Vertex[] z0 = createUnitCrossSection(0, 0, minZ, numCoords + 1, 0);
    Vertex[] z1 = createUnitCrossSection(0, 0, maxZ, numCoords + 1, 1);

    Vertex[] result = new Vertex[numCoords * 4];
    for (int i = 0; i < numCoords; i++) {
      int index = i * 4;
      result[index] = new Vertex(z0[i]);
      result[index + 1] = new Vertex(z0[i + 1]);
      result[index + 2] = new Vertex(z1[i + 1]);
      result[index + 3] = new Vertex(z1[i]);
    }
    return result;

  }

}
