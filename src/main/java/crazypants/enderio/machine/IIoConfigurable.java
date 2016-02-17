package crazypants.enderio.machine;

import com.enderio.core.common.util.BlockCoord;

import net.minecraft.util.EnumFacing;

public interface IIoConfigurable {

  public IoMode toggleIoModeForFace(EnumFacing faceHit);

  public boolean supportsMode(EnumFacing faceHit, IoMode mode);

  public void setIoMode(EnumFacing faceHit, IoMode mode);

  public IoMode getIoMode(EnumFacing face) ;

  public void clearAllIoModes();

  BlockCoord getLocation();

}
