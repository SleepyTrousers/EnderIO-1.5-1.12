package crazypants.enderio.machines.machine.obelisk.inhibitor;

import javax.annotation.Nonnull;

import crazypants.enderio.base.machine.baselegacy.SlotDefinition;
import crazypants.enderio.machines.init.MachineObject;
import crazypants.enderio.machines.machine.obelisk.base.AbstractRangedObeliskEntity;
import info.loenwind.autosave.annotations.Storable;
import net.minecraft.item.ItemStack;

import static crazypants.enderio.machines.capacitor.CapacitorKey.INHIBITOR_POWER_BUFFER;
import static crazypants.enderio.machines.capacitor.CapacitorKey.INHIBITOR_POWER_INTAKE;
import static crazypants.enderio.machines.capacitor.CapacitorKey.INHIBITOR_POWER_USE;
import static crazypants.enderio.machines.capacitor.CapacitorKey.INHIBITOR_RANGE;

@Storable
public class TileInhibitorObelisk extends AbstractRangedObeliskEntity {

  public TileInhibitorObelisk() {
    super(new SlotDefinition(0, 0, 1), INHIBITOR_POWER_INTAKE, INHIBITOR_POWER_BUFFER, INHIBITOR_POWER_USE);
  }

  @Override
  public @Nonnull String getMachineName() {
    return MachineObject.block_inhibitor_obelisk.getUnlocalisedName();
  }

  @Override
  public boolean isMachineItemValidForSlot(int i, @Nonnull ItemStack itemstack) {
    return false;
  }

  @Override
  public boolean isActive() {
    return hasPower() && redstoneCheckPassed;
  }

  @Override
  protected boolean processTasks(boolean redstoneCheck) {
    return false;
  }

  @Override
  public float getRange() {
    return INHIBITOR_RANGE.getFloat(getCapacitorData());
  }

  @Override
  public void onCapacitorDataChange() {
    super.onCapacitorDataChange();
    BlockInhibitorObelisk.instance.activeInhibitors.put(getLocation(), getBounds());
  }

  @Override
  public void validate() {
    super.validate();
    BlockInhibitorObelisk.instance.activeInhibitors.put(getLocation(), getBounds());
  }

  @Override
  public void invalidate() {
    super.invalidate();
    BlockInhibitorObelisk.instance.activeInhibitors.remove(getLocation());
  }

  @Override
  public void onChunkUnload() {
    super.onChunkUnload();
    BlockInhibitorObelisk.instance.activeInhibitors.remove(getLocation());
  }

  @Override
  public boolean canWork() {
    return true;
  }

}
