package crazypants.enderio.machines.machine.obelisk.inhibitor;

import javax.annotation.Nonnull;

import com.enderio.core.common.util.NullHelper;

import crazypants.enderio.base.EnderIO;
import crazypants.enderio.base.machine.baselegacy.SlotDefinition;
import crazypants.enderio.machines.init.MachineObject;
import crazypants.enderio.machines.machine.obelisk.base.AbstractRangedObeliskEntity;
import info.loenwind.autosave.annotations.Storable;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

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
  protected void processTasks(boolean redstoneCheck) {
    if (redstoneCheck) {
      usePower();
    }
  }

  @Override
  public float getRange() {
    return INHIBITOR_RANGE.getFloat(getCapacitorData());
  }

  private void register() {
    if (NullHelper.untrust(getPos()) != null && NullHelper.untrust(getWorld()) != null) {
      InhibitorHandler.register(getWorld(), getPos(), this);
    }
  }

  @Override
  public void onCapacitorDataChange() {
    super.onCapacitorDataChange();
    register();
  }

  @Override
  public void setWorld(@Nonnull World worldIn) {
    super.setWorld(worldIn);
    register();
  }

  @Override
  public void setPos(@Nonnull BlockPos posIn) {
    super.setPos(posIn);
    register();
  }

  @Override
  public void validate() {
    super.validate();
    register();
  }

  @Override
  public boolean canWork() {
    return true;
  }

  @Override
  protected @Nonnull String getDocumentationPage() {
    return EnderIO.DOMAIN + ":inhibitor_obelisk";
  }

}
