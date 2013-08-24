package crazypants.vecmath;

public class CoordUV {

  public Vector3d coord;
  public Vector2f uv;
  
  public CoordUV() {    
  }
  
  public CoordUV(Vector3d coord, Vector2f uv) {    
    this.coord = coord;
    this.uv = uv;
  }
  
  public double x() {
    return coord.x;
  }
  
  public double y() {
    return coord.y;
  }
  
  public double z() {
    return coord.z;
  }
  
  public double u() {
    return uv.x;
  }
  
  public double v() {
    return uv.y;
  }
  
}
