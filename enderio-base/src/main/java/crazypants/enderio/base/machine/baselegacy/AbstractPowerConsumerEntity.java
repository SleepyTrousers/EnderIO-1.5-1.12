package crazypants.enderio.base.machine.baselegacy;

import javax.annotation.Nonnull;

import crazypants.enderio.api.capacitor.ICapacitorKey;
import crazypants.enderio.base.machine.base.te.ICap;
import crazypants.enderio.base.power.PowerHandlerUtil;
import crazypants.enderio.base.power.forge.tile.ILegacyPoweredTile;
import crazypants.enderio.base.power.forge.tile.InternalRecieverTileWrapper;
import info.loenwind.autosave.annotations.Storable;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.energy.CapabilityEnergy;

@Storable
public abstract class AbstractPowerConsumerEntity extends AbstractPoweredMachineEntity implements ILegacyPoweredTile.Receiver {

  protected AbstractPowerConsumerEntity(@Nonnull SlotDefinition slotDefinition, @Nonnull ICapacitorKey maxEnergyRecieved,
      @Nonnull ICapacitorKey maxEnergyStored, @Nonnull ICapacitorKey maxEnergyUsed) {
    super(slotDefinition, maxEnergyRecieved, maxEnergyStored, maxEnergyUsed);
    addICap(CapabilityEnergy.ENERGY, ICap.facedOnly(facingIn -> InternalRecieverTileWrapper.get(this, facingIn)));
  }

  @Override
  public int receiveEnergy(EnumFacing from, int maxReceive, boolean simulate) {
    if (isSideDisabled(from)) {
      return 0;
    }
    return PowerHandlerUtil.recieveInternal(this, maxReceive, from, simulate);
  }

  @Override
  public int getMaxEnergyRecieved(EnumFacing dir) {
    if (isSideDisabled(dir)) {
      return 0;
    }
    return maxEnergyRecieved.get(getCapacitorData());
  }

}
