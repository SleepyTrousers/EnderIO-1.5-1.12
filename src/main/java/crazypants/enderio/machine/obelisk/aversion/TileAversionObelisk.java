package crazypants.enderio.machine.obelisk.aversion;

import info.loenwind.autosave.annotations.Storable;
import net.minecraft.entity.EntityLivingBase;
import crazypants.enderio.ModObject;
import crazypants.enderio.machine.SlotDefinition;
import crazypants.enderio.machine.obelisk.spawn.TileEntityAbstractSpawningObelisk;
import static crazypants.enderio.capacitor.CapacitorKey.AVERSION_POWER_BUFFER;
import static crazypants.enderio.capacitor.CapacitorKey.AVERSION_POWER_INTAKE;
import static crazypants.enderio.capacitor.CapacitorKey.AVERSION_POWER_USE;

@Storable
public class TileAversionObelisk extends TileEntityAbstractSpawningObelisk {

  public TileAversionObelisk() {
    super(new SlotDefinition(12, 0), AVERSION_POWER_INTAKE, AVERSION_POWER_BUFFER, AVERSION_POWER_USE);
  }
  
  @Override
  public String getMachineName() {
    return ModObject.blockSpawnGuard.getUnlocalisedName();
  }

  @Override
  public Result isSpawnPrevented(EntityLivingBase mob) {
    return (redstoneCheckPassed && hasPower() && isMobInRange(mob) && isMobInFilter(mob)) ? Result.DENY : Result.NEXT;
  }

}
