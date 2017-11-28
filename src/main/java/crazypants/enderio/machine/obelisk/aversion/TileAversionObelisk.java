package crazypants.enderio.machine.obelisk.aversion;

import crazypants.enderio.machine.MachineObject;
import crazypants.enderio.machine.baselegacy.SlotDefinition;
import crazypants.enderio.machine.obelisk.spawn.TileEntityAbstractSpawningObelisk;
import info.loenwind.autosave.annotations.Storable;
import net.minecraft.entity.EntityLivingBase;

import javax.annotation.Nonnull;

import static crazypants.enderio.capacitor.CapacitorKey.*;

@Storable
public class TileAversionObelisk extends TileEntityAbstractSpawningObelisk {

  public TileAversionObelisk() {
    super(new SlotDefinition(12, 0), LEGACY_ENERGY_INTAKE,LEGACY_ENERGY_BUFFER, LEGACY_ENERGY_USE);
  }
  
  @Override
  public @Nonnull String getMachineName() {
    return MachineObject.block_aversion_obelisk.getUnlocalisedName();
  }

  @Override
  public Result isSpawnPrevented(EntityLivingBase mob) {
    return (redstoneCheckPassed && hasPower() && isMobInRange(mob) && isMobInFilter(mob)) ? Result.DENY : Result.NEXT;
  }

  @Override
  public SpawnObeliskAction getSpawnObeliskAction() {
    return SpawnObeliskAction.AVERT;
  }

}
