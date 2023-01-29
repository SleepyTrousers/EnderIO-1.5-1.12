package crazypants.enderio.conduit.gas;

import mekanism.api.gas.GasStack;
import crazypants.enderio.conduit.AbstractConduitNetwork;

public class AbstractGasTankConduitNetwork<T extends AbstractGasTankConduit>
        extends AbstractConduitNetwork<IGasConduit, T> {

    protected GasStack gasType;

    protected AbstractGasTankConduitNetwork(Class<T> cl) {
        super(cl, IGasConduit.class);
    }

    public GasStack getGasType() {
        return gasType;
    }

    @Override
    public void addConduit(T con) {
        super.addConduit(con);
        con.setGasType(gasType);
    }

    public boolean setGasType(GasStack newType) {
        if (gasType != null && gasType.isGasEqual(newType)) {
            return false;
        }
        if (newType != null) {
            gasType = newType.copy();
            gasType.amount = 0;
        } else {
            gasType = null;
        }
        for (AbstractGasTankConduit conduit : conduits) {
            conduit.setGasType(gasType);
        }
        return true;
    }

    public boolean canAcceptGas(GasStack acceptable) {
        return areGassCompatable(gasType, acceptable);
    }

    public static boolean areGassCompatable(GasStack a, GasStack b) {
        if (a == null || b == null) {
            return true;
        }
        return a.isGasEqual(b);
    }

    public int getTotalVolume() {
        int totalVolume = 0;
        for (T con : conduits) {
            totalVolume += con.getTank().getStored();
        }
        return totalVolume;
    }
}
