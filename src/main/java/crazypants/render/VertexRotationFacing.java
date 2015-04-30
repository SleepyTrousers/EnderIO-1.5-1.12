package crazypants.render;

import net.minecraftforge.common.util.ForgeDirection;
import com.enderio.core.common.vecmath.Vector3d;

public class VertexRotationFacing extends VertexRotation {

  private static final double ROTATION_AMOUNT = Math.PI / 2;

  private ForgeDirection defaultDir;

  public VertexRotationFacing(ForgeDirection defaultDir) {
    super(0, new Vector3d(0, 0.5, 0), new Vector3d(0, 0, 0));
    this.defaultDir = defaultDir;
  }

  public void setRotation(ForgeDirection dir) {
    if(dir == defaultDir) {
      setAngle(0);
    } else if(dir == defaultDir.getOpposite()) {
      setAngle(ROTATION_AMOUNT * 2);
    } else if(dir == defaultDir.getRotation(ForgeDirection.DOWN)) {
      setAngle(ROTATION_AMOUNT);
    } else {
      setAngle(ROTATION_AMOUNT * 3);
    }
  }
}
