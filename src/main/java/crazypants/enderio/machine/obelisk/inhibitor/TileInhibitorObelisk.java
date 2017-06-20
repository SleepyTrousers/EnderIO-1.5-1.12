package crazypants.enderio.machine.obelisk.inhibitor;

import javax.annotation.Nonnull;

import crazypants.enderio.ModObject;
import crazypants.enderio.machine.obelisk.AbstractRangedTileEntity;
import info.loenwind.autosave.annotations.Storable;
import net.minecraft.item.ItemStack;

import static crazypants.enderio.capacitor.CapacitorKey.AVERSION_RANGE;

@Storable
public class TileInhibitorObelisk extends AbstractRangedTileEntity {

  public TileInhibitorObelisk() {
    super(new SlotDefinition(0, 0, 1), ModObject.blockInhibitorObelisk);
  }

  @Override
  public @Nonnull String getMachineName() {
    return ModObject.blockInhibitorObelisk.getUnlocalisedName();
  }

  @Override
  public boolean isMachineItemValidForSlot(int i, ItemStack itemstack) {
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
    return AVERSION_RANGE.getFloat(getCapacitorData()) / 2;
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

}
