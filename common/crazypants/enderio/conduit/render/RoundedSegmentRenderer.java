package crazypants.enderio.conduit.render;

import static crazypants.util.ForgeDirectionOffsets.offsetScaled;
import static net.minecraftforge.common.ForgeDirection.SOUTH;

import java.util.ArrayList;
import java.util.List;

import crazypants.enderio.conduit.geom.ConduitGeometryUtil;
import crazypants.render.BoundingBox;
import crazypants.util.ForgeDirectionOffsets;
import crazypants.vecmath.Matrix4d;
import crazypants.vecmath.Vector2d;
import crazypants.vecmath.Vector3d;
import crazypants.vecmath.Vector3f;


import net.minecraft.client.renderer.Tessellator;
import net.minecraftforge.common.ForgeDirection;

public class RoundedSegmentRenderer {

  private static Coord[][] DIR_COORDS = new Coord[ForgeDirection.VALID_DIRECTIONS.length][];
  
  private static final Vector3d REF_TRANS = new Vector3d(0.5, 0.5, 0.5);  

  static {
    double circ = ConduitGeometryUtil.WIDTH * 0.7;
    
    Coord[] refCoords = createUnitSectionQuads(16, -0.25, 0.25);

    for (Coord coord : refCoords) {
      coord.xyz.x = coord.xyz.x * circ;
      coord.xyz.y = coord.xyz.y * circ;
    }
    Matrix4d rotMat = new Matrix4d();
    rotMat.setIdentity();    
    rotMat.setTranslation(REF_TRANS);

    DIR_COORDS[SOUTH.ordinal()] = xformCoords(refCoords, rotMat, offsetScaled(ForgeDirection.SOUTH, 0.25));

    rotMat.makeRotationY(Math.PI);    
    rotMat.setTranslation(REF_TRANS);
    DIR_COORDS[ForgeDirection.NORTH.ordinal()] = xformCoords(refCoords, rotMat, offsetScaled(ForgeDirection.NORTH, 0.25));

    rotMat.makeRotationY(Math.PI / 2);
    rotMat.setTranslation(REF_TRANS);
    DIR_COORDS[ForgeDirection.EAST.ordinal()] = xformCoords(refCoords, rotMat, offsetScaled(ForgeDirection.EAST, 0.25));
    
    rotMat.makeRotationY(-Math.PI / 2);
    rotMat.setTranslation(REF_TRANS);
    DIR_COORDS[ForgeDirection.WEST.ordinal()] = xformCoords(refCoords, rotMat, offsetScaled(ForgeDirection.WEST, 0.25));
    
    rotMat.makeRotationX(-Math.PI / 2);
    rotMat.setTranslation(REF_TRANS);
    DIR_COORDS[ForgeDirection.UP.ordinal()] = xformCoords(refCoords, rotMat, offsetScaled(ForgeDirection.UP, 0.25));
    
    rotMat.makeRotationX(Math.PI / 2);
    rotMat.setTranslation(REF_TRANS);
    DIR_COORDS[ForgeDirection.DOWN.ordinal()] = xformCoords(refCoords, rotMat, offsetScaled(ForgeDirection.DOWN, 0.25));

  }


  private static Coord[] xformCoords(Coord[] refCoords, Matrix4d rotMat, Vector3d trans) {
    Coord[] res = new Coord[refCoords.length];
    for (int i = 0; i < res.length; i++) {
      res[i] = new Coord(refCoords[i]);
      rotMat.transform(res[i].xyz);
      rotMat.transformNormal(res[i].normal);
      res[i].xyz.add(trans);
    }
    return res;
  }
  
  private static Coord[] xformCoords(List<Coord> refCoords, Matrix4d rotMat, Vector3d trans) {
    Coord[] res = new Coord[refCoords.size()];
    for (int i = 0; i < res.length; i++) {
      res[i] = new Coord(refCoords.get(i));
      rotMat.transform(res[i].xyz);
      rotMat.transformNormal(res[i].normal);
      res[i].xyz.add(trans);
    }
    return res;
  }

  public static Coord[] createUnitCrossSection(double xOffset, double yOffset, double zOffset, int numCoords, int u) {

    Coord[] crossSection = new Coord[numCoords];

    double angle = 0;
    double inc = (Math.PI * 2) / (crossSection.length - 1);
    for (int i = 0; i < crossSection.length; i++) {
      double x = Math.cos(angle) * 0.5;
      double y = Math.sin(angle) * 0.5;
      angle += inc;
      crossSection[i] = new Coord();
      crossSection[i].setXYZ(xOffset + x, yOffset + y, zOffset);
      crossSection[i].setNormal(x, y, 0);
      crossSection[i].setUV(u, y + 0.5);
    }
    return crossSection;

  }

  public static void renderSegment(ForgeDirection dir, BoundingBox bounds, float minU, float maxU, float minV, float maxV) {
    float uScale = maxU - minU;
    float vScale = maxV - minV;
    
    Vector3d offset = calcOffset(dir, bounds);
    
    Tessellator tes = Tessellator.instance;
    Coord[] coords = DIR_COORDS[dir.ordinal()];
    for (Coord coord : coords) {
      double u = minU + (coord.uv.x * uScale);
      double v = minV + (coord.uv.y * vScale);
      tes.setNormal(coord.normal.x, coord.normal.y, coord.normal.z);
      tes.addVertexWithUV(offset.x + coord.xyz.x, offset.y + coord.xyz.y, offset.z + coord.xyz.z, u, v);
    }
  }


  private static Vector3d calcOffset(ForgeDirection dir, BoundingBox bounds) {
    Vector3d res = new Vector3d();
    Vector3d center = bounds.getCenter();
    if(dir != ForgeDirection.UP && dir != ForgeDirection.DOWN) {
      res.set(0, center.y - REF_TRANS.y,0);
    } else {
      res.set(center.x - REF_TRANS.x, 0,0);
    }
    return res;
  }

  public static Coord[] createUnitSectionQuads(int numCoords, double minZ, double maxZ) {

    Coord[] z0 = createUnitCrossSection(0, 0, minZ, numCoords + 1, 0);
    Coord[] z1 = createUnitCrossSection(0, 0, maxZ, numCoords + 1, 1);

    Coord[] result = new Coord[numCoords * 4];
    for (int i = 0; i < numCoords; i++) {
      int index = i * 4;
      result[index] = new Coord(z0[i]);
      result[index + 1] = new Coord(z0[i + 1]);
      result[index + 2] = new Coord(z1[i + 1]);
      result[index + 3] = new Coord(z1[i]);
    }
    return result;

  }

  public static class Coord {

    Vector3d xyz = new Vector3d();
    Vector2d uv = new Vector2d();
    Vector3f normal = new Vector3f();

    Coord() {
    }

    Coord(Coord other) {
      xyz.set(other.xyz);
      uv.set(other.uv);
      normal.set(other.normal);
    }

    void setXYZ(double x, double y, double z) {
      xyz.set(x, y, z);
    }

    void setUV(double u, double v) {
      uv.set(u, v);
    }
    
    void setNormal(double x, double y, double z) {
      normal.set((float)x,(float)y,(float)z);
      normal.normalize();
    }

    @Override
    public String toString() {
      return "Coord [xyz=" + xyz + ", uv=" + uv + "]";
    }

  }

}
