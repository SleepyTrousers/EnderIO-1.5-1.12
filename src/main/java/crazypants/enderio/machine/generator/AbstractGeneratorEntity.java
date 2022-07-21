package crazypants.enderio.machine.generator;

import crazypants.enderio.machine.AbstractPoweredMachineEntity;
import crazypants.enderio.machine.SlotDefinition;
import crazypants.enderio.power.IInternalPowerProvider;
import net.minecraftforge.common.util.ForgeDirection;

public abstract class AbstractGeneratorEntity extends AbstractPoweredMachineEntity implements IInternalPowerProvider {

    // RF API Power

    public AbstractGeneratorEntity(SlotDefinition slotDefinition) {
        super(slotDefinition);
    }

    @Override
    public int getEnergyStored(ForgeDirection from) {
        return getEnergyStored();
    }

    @Override
    public int getMaxEnergyStored(ForgeDirection from) {
        return getMaxEnergyStored();
    }

    @Override
    public int extractEnergy(ForgeDirection from, int maxExtract, boolean simulate) {
        return 0;
    }
}
