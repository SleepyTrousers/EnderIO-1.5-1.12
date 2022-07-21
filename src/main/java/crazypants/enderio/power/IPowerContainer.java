package crazypants.enderio.power;

import com.enderio.core.common.util.BlockCoord;

public interface IPowerContainer {

    int getEnergyStored();

    void setEnergyStored(int storedEnergy);

    BlockCoord getLocation();
}
