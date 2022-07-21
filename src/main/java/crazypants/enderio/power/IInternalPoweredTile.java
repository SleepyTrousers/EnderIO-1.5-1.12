package crazypants.enderio.power;

import cofh.api.energy.IEnergyConnection;
import net.minecraftforge.common.util.ForgeDirection;

// IEnergyHandler,
public interface IInternalPoweredTile extends IPowerContainer, IEnergyConnection {

    int getMaxEnergyRecieved(ForgeDirection dir);

    int getMaxEnergyStored();

    /**
     * Should the power be displayed in WAILA or other places
     */
    boolean displayPower();
}
