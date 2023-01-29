package crazypants.enderio.power;

import net.minecraftforge.common.util.ForgeDirection;

public interface IPowerStorage {

    IPowerStorage getController();

    long getEnergyStoredL();

    long getMaxEnergyStoredL();

    /**
     * If false this connection will be treated the same a regular powered block. No power will be drawn over the
     * connection and it will not be used to balance capacitor bank levels
     *
     * @param direction
     * @return
     */
    boolean isNetworkControlledIo(ForgeDirection direction);

    boolean isOutputEnabled(ForgeDirection direction);

    boolean isInputEnabled(ForgeDirection direction);

    int getMaxOutput();

    int getMaxInput();

    void addEnergy(int amount);

    boolean isCreative();
}
