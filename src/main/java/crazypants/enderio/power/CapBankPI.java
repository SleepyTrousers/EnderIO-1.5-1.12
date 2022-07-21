package crazypants.enderio.power;

import crazypants.enderio.machine.capbank.TileCapBank;

public class CapBankPI extends EnergyReceiverPI {

    public CapBankPI(TileCapBank powerReceptor) {
        super(powerReceptor);
    }

    @Override
    public boolean isInputOnly() {
        return false;
    }

    @Override
    public boolean isOutputOnly() {
        return false;
    }
}
