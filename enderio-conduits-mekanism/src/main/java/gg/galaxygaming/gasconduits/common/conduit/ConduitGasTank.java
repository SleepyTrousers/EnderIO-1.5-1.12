package gg.galaxygaming.gasconduits.common.conduit;

import mekanism.api.gas.GasTank;

public class ConduitGasTank extends GasTank {

    public ConduitGasTank(int capacity) {
        super(capacity);
    }

    public float getFilledRatio() {
        if (getStored() <= 0) {
            return 0;
        }
        if (getMaxGas() <= 0) {
            return -1;
        }
        return (float) getStored() / getMaxGas();
    }

    public boolean isFull() {
        return getStored() >= getMaxGas();
    }

    public void setAmount(int amount) {
        if (getGas() != null) {
            getGas().amount = amount;
        }
    }

    public void addAmount(int amount) {
        setAmount(getStored() + amount);
    }

    @Override
    public void setMaxGas(int capacity) {
        super.setMaxGas(capacity);
        if (getStored() > capacity) {
            setAmount(capacity);
        }
    }

    public boolean containsValidGas() {
        return getGasType() != null;
    }

    public boolean isEmpty() {
        return getStored() == 0;
    }
}