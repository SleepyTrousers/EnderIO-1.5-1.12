package crazypants.enderio.base.machine.baselegacy;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import crazypants.enderio.api.capacitor.ICapacitorKey;
import crazypants.enderio.base.power.forge.tile.ILegacyPoweredTile;
import crazypants.enderio.base.power.forge.tile.InternalGeneratorTileWrapper;
import info.loenwind.autosave.annotations.Storable;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;

import static crazypants.enderio.base.capacitor.CapacitorKey.NO_POWER;

@Storable
public abstract class AbstractGeneratorEntity extends AbstractPoweredMachineEntity implements ILegacyPoweredTile {

  protected AbstractGeneratorEntity(@Nonnull SlotDefinition slotDefinition, @Nonnull ICapacitorKey maxEnergyStored, @Nonnull ICapacitorKey maxEnergyUsed) {
    super(slotDefinition, NO_POWER, maxEnergyStored, maxEnergyUsed);
  }

  @Override
  public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facingIn) {
    if (capability == CapabilityEnergy.ENERGY) {
      return CapabilityEnergy.ENERGY.cast(InternalGeneratorTileWrapper.get(this, facingIn));
    }
    return super.getCapability(capability, facingIn);
  }

}
