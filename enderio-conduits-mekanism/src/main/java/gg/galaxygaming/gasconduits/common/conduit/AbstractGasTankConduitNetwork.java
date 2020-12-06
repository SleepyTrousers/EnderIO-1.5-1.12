package gg.galaxygaming.gasconduits.common.conduit;

import crazypants.enderio.conduits.conduit.AbstractConduitNetwork;
import javax.annotation.Nonnull;
import mekanism.api.gas.Gas;
import mekanism.api.gas.GasStack;

public class AbstractGasTankConduitNetwork<T extends AbstractGasTankConduit> extends AbstractConduitNetwork<IGasConduit, T> {

    protected GasStack gasType;
    protected boolean gasTypeLocked = false;

    protected AbstractGasTankConduitNetwork(@Nonnull Class<T> cl) {
        super(cl, IGasConduit.class);
    }

    public GasStack getGasType() {
        return gasType;
    }

    @Override
    public void addConduit(@Nonnull T con) {
        super.addConduit(con);
        con.setGasType(gasType);
        if (con.gasTypeLocked && !gasTypeLocked) {
            setGasTypeLocked(true);
        }
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
        getConduits().forEach(conduit -> conduit.setGasType(gasType));
        return true;
    }

    public void setGasTypeLocked(boolean gasTypeLocked) {
        if (this.gasTypeLocked == gasTypeLocked) {
            return;
        }
        this.gasTypeLocked = gasTypeLocked;
        getConduits().forEach(conduit -> conduit.setGasTypeLocked(gasTypeLocked));
    }

    public boolean canAcceptGas(GasStack acceptable) {
        return areGasesCompatible(gasType, acceptable);
    }

    public static boolean areGasesCompatible(GasStack a, GasStack b) {
        return a == null || b == null || a.isGasEqual(b);
    }

    public static boolean areGasesCompatible(Gas a, Gas b) {
        return a == null || b == null || a == b;
    }

    public static boolean areGasesCompatible(GasStack a, Gas b) {
        return a == null || areGasesCompatible(a.getGas(), b);
    }

    public int getTotalVolume() {
        int totalVolume = 0;
        for (T con : getConduits()) {
            totalVolume += con.getTank().getStored();
        }
        return totalVolume;
    }
}