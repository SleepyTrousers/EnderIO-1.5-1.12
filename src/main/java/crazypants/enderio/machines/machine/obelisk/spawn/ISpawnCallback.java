package crazypants.enderio.machines.machine.obelisk.spawn;

import net.minecraft.entity.EntityLivingBase;

public interface ISpawnCallback {

  public enum Result {
    NEXT,
    DENY,
    DONE;
  }

  Result isSpawnPrevented(EntityLivingBase mob);

}