package crazypants.enderio.power;

import net.minecraftforge.common.util.ForgeDirection;

import cofh.api.energy.IEnergyReceiver;

public class EnergyReceiverPI implements IPowerInterface {

    private IEnergyReceiver rfPower;

    public EnergyReceiverPI(IEnergyReceiver powerReceptor) {
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
        if (rfPower != null && dir != null && rfPower.canConnectEnergy(dir)) {
            return rfPower.receiveEnergy(dir, 99999999, true);
        }
        return 0;
    }

    public static int getPowerRequest(ForgeDirection dir, IEnergyReceiver handler) {
        if (handler != null && dir != null && handler.canConnectEnergy(dir)) {
            return handler.receiveEnergy(dir, 99999999, true);
        }
        return 0;
    }

    @Override
    public int getMinEnergyReceived(ForgeDirection dir) {
        return 0;
    }

    @Override
    public int recieveEnergy(ForgeDirection opposite, int canOffer) {
        if (rfPower != null && opposite != null) {
            return rfPower.receiveEnergy(opposite, canOffer, false);
        }
        return 0;
    }

    @Override
    public boolean isOutputOnly() {
        return false;
    }

    @Override
    public boolean isInputOnly() {
        return true;
    }
}
