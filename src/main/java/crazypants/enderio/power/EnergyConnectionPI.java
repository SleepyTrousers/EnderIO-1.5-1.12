package crazypants.enderio.power;

import net.minecraftforge.common.util.ForgeDirection;

import cofh.api.energy.IEnergyConnection;

public class EnergyConnectionPI implements IPowerInterface {

    private IEnergyConnection delegate;

    public EnergyConnectionPI(IEnergyConnection delegate) {
        this.delegate = delegate;
    }

    @Override
    public Object getDelegate() {
        return delegate;
    }

    @Override
    public boolean canConduitConnect(ForgeDirection direction) {
        if (delegate != null && direction != null) {
            return delegate.canConnectEnergy(direction.getOpposite());
        }
        return false;
    }

    @Override
    public int getEnergyStored(ForgeDirection dir) {
        return 0;
    }

    @Override
    public int getMaxEnergyStored(ForgeDirection dir) {
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
