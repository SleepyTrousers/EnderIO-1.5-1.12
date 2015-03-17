package crazypants.render;

import net.minecraftforge.common.util.ForgeDirection;
import crazypants.vecmath.Vector3d;

public class VertexRotationFacing extends VertexRotation {

  private ForgeDirection defaultDir;

  public VertexRotationFacing(ForgeDirection defaultDir) {
    super(0, new Vector3d(0, 0.5, 0), new Vector3d(0, 0, 0));
    this.defaultDir = defaultDir;
  }

  // No I have no idea why this value works.
  private double rotationAmount = 1.571;

  public void setRotation(ForgeDirection dir) {
    if(dir == defaultDir) {
      setAngle(0);
    } else if(dir == defaultDir.getOpposite()) {
      setAngle(rotationAmount * 2);
    } else if(dir == defaultDir.getRotation(ForgeDirection.DOWN)) {
      setAngle(rotationAmount);
    } else {
      setAngle(rotationAmount * 3);
    }
  }
}
