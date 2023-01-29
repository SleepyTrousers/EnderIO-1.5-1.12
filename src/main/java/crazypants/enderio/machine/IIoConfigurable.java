package crazypants.enderio.machine;

import net.minecraftforge.common.util.ForgeDirection;

import com.enderio.core.common.util.BlockCoord;

public interface IIoConfigurable {

    public IoMode toggleIoModeForFace(ForgeDirection faceHit);

    public boolean supportsMode(ForgeDirection faceHit, IoMode mode);

    public void setIoMode(ForgeDirection faceHit, IoMode mode);

    public IoMode getIoMode(ForgeDirection face);

    public void clearAllIoModes();

    BlockCoord getLocation();
}
