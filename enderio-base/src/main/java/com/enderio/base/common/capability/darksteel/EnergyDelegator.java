package com.enderio.base.common.capability.darksteel;

import com.enderio.base.common.capability.EIOCapabilities;
import com.enderio.base.common.item.darksteel.upgrades.EmpoweredUpgrade;
import com.enderio.core.common.capability.MultiCapabilityProvider;
import net.minecraftforge.energy.EnergyStorage;
import net.minecraftforge.energy.IEnergyStorage;

import java.util.Optional;

public class EnergyDelegator implements IEnergyStorage {

    private final MultiCapabilityProvider prov;

    private static final EnergyStorage NULL_DELEGATE = new EnergyStorage(0);

    public EnergyDelegator(MultiCapabilityProvider prov) {
       this.prov = prov;
    }

    @Override
    public int receiveEnergy(int maxReceive, boolean simulate) {
        return getDelegate().receiveEnergy(maxReceive, simulate);
    }

    @Override
    public int extractEnergy(int maxExtract, boolean simulate) {
        return getDelegate().extractEnergy(maxExtract, simulate);
    }

    @Override
    public int getEnergyStored() {
        return getDelegate().getEnergyStored();
    }

    @Override
    public int getMaxEnergyStored() {
        return getDelegate().getMaxEnergyStored();
    }

    @Override
    public boolean canExtract() {
        return getDelegate().canExtract();
    }

    @Override
    public boolean canReceive() {
        return getDelegate().canReceive();
    }

    private IEnergyStorage getDelegate() {
        Optional<IDarkSteelUpgradable> cap = prov.getCapability(EIOCapabilities.DARK_STEEL_UPGRADABLE).resolve();
        if (cap.isPresent()) {
            Optional<EmpoweredUpgrade> energyUp = cap.get().getUpgradeAs(EmpoweredUpgrade.NAME, EmpoweredUpgrade.class);
            if (energyUp.isPresent()) {
                return energyUp.get().getStorage();
            }
        }
        return NULL_DELEGATE;
    }
}
