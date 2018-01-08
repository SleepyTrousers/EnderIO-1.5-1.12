package crazypants.enderio.base.machine.interfaces;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import crazypants.enderio.base.machine.modes.IoMode;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;

public interface IIoConfigurable {

  public @Nonnull IoMode toggleIoModeForFace(@Nullable EnumFacing faceHit);

  public boolean supportsMode(@Nullable EnumFacing faceHit, @Nullable IoMode mode);

  public void setIoMode(@Nullable EnumFacing faceHit, @Nullable IoMode mode);

  public @Nonnull IoMode getIoMode(@Nullable EnumFacing face);

  public void clearAllIoModes();
  
  @Nonnull
  BlockPos getLocation();

}
