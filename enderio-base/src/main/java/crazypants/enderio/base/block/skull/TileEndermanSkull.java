package crazypants.enderio.base.block.skull;

import com.enderio.core.common.NBTAction;

import crazypants.enderio.base.TileEntityEio;
import info.loenwind.autosave.annotations.Storable;
import info.loenwind.autosave.annotations.Store;

@Storable
public class TileEndermanSkull extends TileEntityEio {

  @Store({ NBTAction.SAVE, NBTAction.CLIENT })
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
