package crazypants.render;

import crazypants.util.BlockCoord;
import crazypants.vecmath.Vector3d;

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
    this.minX = (float)minX;
    this.minY = (float)minY;
    this.minZ = (float)minZ;
    this.maxX = (float)maxX;
    this.maxY = (float)maxY;
    this.maxZ = (float)maxZ;
  }
  
  public BoundingBox(Vector3d min, Vector3d max) {
    this(min.x, min.y, min.z, max.x, max.y, max.z);
  }
  
  public BoundingBox(BoundingBox copy) {
    this(copy.minX, copy.minY, copy.minZ, copy.maxX, copy.maxY, copy.maxZ);
  }
  
  public BoundingBox(BlockCoord bc) {
    this(bc.x,bc.y,bc.z,bc.x + 1,bc.y + 1,bc.z + 1);
  }

  public BoundingBox expandBy(BoundingBox other) {
    return new BoundingBox(
        Math.min(minX, other.minX), Math.min(minY, other.minY), Math.min(minZ, other.minZ),
        Math.max(maxX, other.maxX), Math.max(maxY, other.maxY), Math.max(maxZ, other.maxZ));
  }
  
  public boolean isValid() {
    return minX < maxX && minY < maxY && minZ < maxZ;
  }
  
  public BoundingBox scale(float x, float y, float z) {
    x = 1 - x;
    y = 1 - y;
    z = 1 - z;
    float w = ((maxX - minX) * x)/2;
    float h = ((maxY - minY) * y)/2;
    float d = ((maxZ - minZ) * z)/2;
    return new BoundingBox(minX + w, minY + h, minZ + d, maxX - w, maxY - h, maxZ - d);    
  }
  
  public BoundingBox translate(float x, float y, float z) {
    return new BoundingBox(minX + x, minY + y, minZ + z, maxX + x, maxY + y, maxZ + z);
  }
  
  public BoundingBox translate(Vector3d translation) {    
    return translate((float)translation.x, (float)translation.y,(float)translation.z);
  }
  
  public BoundingBox transform(VertexTransform iTransformation) {
    Vector3d min = new Vector3d(minX, minY,minZ);
    Vector3d max = new Vector3d(maxX, maxY,maxZ);
    
    iTransformation.apply(min);
    iTransformation.apply(max);
    
    return new BoundingBox(
        Math.min(min.x, max.x), Math.min(min.y, max.y), Math.min(min.z, max.z), 
        Math.max(min.x, max.x), Math.max(min.y, max.y), Math.max(min.z, max.z));
    
  }
  
  public Vector3d getCenter() {
    return new Vector3d(minX + (maxX - minX)/2, minY + (maxY - minY)/2,minZ + (maxZ - minZ)/2);
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
    return new Vector3d(minX,minY,minZ);
  }
  
  public Vector3d getMax() {    
    return new Vector3d(maxX,maxY,maxZ);
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
    return new BoundingBox(mnX,mnY,mnZ,mxX,mxY,mxZ);
  }

  


}
