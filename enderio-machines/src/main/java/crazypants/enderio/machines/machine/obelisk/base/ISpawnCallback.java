package crazypants.enderio.machines.machine.obelisk.base;

import javax.annotation.Nonnull;

import net.minecraft.entity.EntityLivingBase;

public interface ISpawnCallback {

  public enum Result {
    NEXT,
    DENY,
    DONE;
  }

  @Nonnull
  Result isSpawnPrevented(EntityLivingBase mob);

}