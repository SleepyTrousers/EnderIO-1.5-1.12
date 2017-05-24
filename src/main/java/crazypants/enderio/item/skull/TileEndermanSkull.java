package crazypants.enderio.item.skull;

import info.loenwind.autosave.annotations.Storable;
import info.loenwind.autosave.annotations.Store;
import crazypants.enderio.TileEntityEio;

@Storable
public class TileEndermanSkull extends TileEntityEio {

  @Store
  private float yaw;

  // Rendering data
  protected long lastTick = -1;
  protected int lookingAt = 0;

  public void setYaw(float yaw) {
    this.yaw = yaw;
  }

  public float getYaw() {
    return yaw;
  }

}
