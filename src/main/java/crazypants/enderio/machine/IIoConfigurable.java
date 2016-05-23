package crazypants.enderio.machine;

import javax.annotation.Nonnull;

import com.enderio.core.common.util.BlockCoord;

import net.minecraft.util.EnumFacing;

public interface IIoConfigurable {

  public @Nonnull IoMode toggleIoModeForFace(@Nonnull EnumFacing faceHit);

  public boolean supportsMode(@Nonnull EnumFacing faceHit, @Nonnull IoMode mode);

  public void setIoMode(@Nonnull EnumFacing faceHit, @Nonnull IoMode mode);

  public @Nonnull IoMode getIoMode(@Nonnull EnumFacing face);

  public void clearAllIoModes();

  @Deprecated
  BlockCoord getLocation();

}
