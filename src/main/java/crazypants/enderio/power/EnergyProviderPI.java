package crazypants.enderio.power;

import net.minecraftforge.common.util.ForgeDirection;

import cofh.api.energy.IEnergyProvider;

public class EnergyProviderPI implements IPowerInterface {

    private IEnergyProvider rfPower;

    public EnergyProviderPI(IEnergyProvider powerReceptor) {
        rfPower = powerReceptor;
    }

    @Override
    public Object getDelegate() {
        return rfPower;
    }

    @Override
    public boolean canConduitConnect(ForgeDirection direction) {
        if (rfPower != null && direction != null) {
            return rfPower.canConnectEnergy(direction.getOpposite());
        }
        return false;
    }

    @Override
    public int getEnergyStored(ForgeDirection dir) {
        if (rfPower != null && dir != null) {
            return rfPower.getEnergyStored(dir);
        }
        return 0;
    }

    @Override
    public int getMaxEnergyStored(ForgeDirection dir) {
        if (rfPower != null && dir != null) {
            return rfPower.getMaxEnergyStored(dir);
        }
        return 0;
    }

    @Override
    public int getPowerRequest(ForgeDirection dir) {
        return 0;
    }

    @Override
    public int getMinEnergyReceived(ForgeDirection dir) {
        return 0;
    }

    @Override
    public int recieveEnergy(ForgeDirection opposite, int canOffer) {
        return 0;
    }

    @Override
    public boolean isOutputOnly() {
        return true;
    }

    @Override
    public boolean isInputOnly() {
        return false;
    }
}
