package crazypants.enderio.machine.interfaces;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.common.util.BlockCoord;

import crazypants.enderio.machine.modes.IoMode;
import net.minecraft.util.EnumFacing;

public interface IIoConfigurable {

  public @Nonnull IoMode toggleIoModeForFace(@Nullable EnumFacing faceHit);

  public boolean supportsMode(@Nullable EnumFacing faceHit, @Nullable IoMode mode);

  public void setIoMode(@Nullable EnumFacing faceHit, @Nullable IoMode mode);

  public @Nonnull IoMode getIoMode(@Nullable EnumFacing face);

  public void clearAllIoModes();
  
  BlockCoord getLocation();

}
