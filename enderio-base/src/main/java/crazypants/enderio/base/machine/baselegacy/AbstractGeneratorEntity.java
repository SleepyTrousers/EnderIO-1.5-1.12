package crazypants.enderio.base.machine.baselegacy;

import javax.annotation.Nonnull;

import crazypants.enderio.api.capacitor.ICapacitorKey;
import crazypants.enderio.base.machine.base.te.ICap;
import crazypants.enderio.base.power.forge.tile.ILegacyPoweredTile;
import crazypants.enderio.base.power.forge.tile.InternalGeneratorTileWrapper;
import info.loenwind.autosave.annotations.Storable;
import net.minecraftforge.energy.CapabilityEnergy;

import static crazypants.enderio.base.capacitor.CapacitorKey.NO_POWER;

@Storable
public abstract class AbstractGeneratorEntity extends AbstractPoweredMachineEntity implements ILegacyPoweredTile {

  protected AbstractGeneratorEntity(@Nonnull SlotDefinition slotDefinition, @Nonnull ICapacitorKey maxEnergyStored, @Nonnull ICapacitorKey maxEnergyUsed) {
    this(slotDefinition, NO_POWER, maxEnergyStored, maxEnergyUsed);
  }

  protected AbstractGeneratorEntity(@Nonnull SlotDefinition slotDefinition, @Nonnull ICapacitorKey maxEnergySent, @Nonnull ICapacitorKey maxEnergyStored,
      @Nonnull ICapacitorKey maxEnergyUsed) {
    super(slotDefinition, maxEnergySent, maxEnergyStored, maxEnergyUsed);
    addICap(CapabilityEnergy.ENERGY, ICap.facedOnly(facingIn -> InternalGeneratorTileWrapper.get(this, facingIn)));
  }

  /**
   * Determines how much power a generator may push out to all its neighbors per tick. This does take into account the configured value and the available amount
   * of energy.
   * <p>
   * Note that this is not sided as the PowerDistributor only has a single amount parameter and I didn't want to change to much when I added this method.
   * --Henry
   * 
   * @return a positive integer
   */
  public int getMaxEnergySent() {
    return Math.min(getEnergyStored(), maxEnergyRecieved != NO_POWER ? maxEnergyRecieved.get(getCapacitorData()) : (getPowerUsePerTick() * 2));
  }

}
