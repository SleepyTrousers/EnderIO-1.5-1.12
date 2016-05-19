package crazypants.enderio.machine.obelisk.inhibitor;

import com.enderio.core.client.render.BoundingBox;

import static crazypants.enderio.capacitor.CapacitorKey.AVERSION_RANGE;

import crazypants.enderio.ModObject;
import crazypants.enderio.machine.SlotDefinition;
import crazypants.enderio.machine.obelisk.AbstractRangedTileEntity;
import info.loenwind.autosave.annotations.Storable;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;

@Storable
public class TileInhibitorObelisk extends AbstractRangedTileEntity {

  public TileInhibitorObelisk() {
    super(new SlotDefinition(0, 0, 1), ModObject.blockInhibitorObelisk);
  }

  @Override
  public String getMachineName() {
    return ModObject.blockInhibitorObelisk.getUnlocalisedName();
  }

  @Override
  protected boolean isMachineItemValidForSlot(int i, ItemStack itemstack) {
    return false;
  }

  @Override
  public boolean isActive() {
    return hasPower();
  }

  @Override
  protected boolean processTasks(boolean redstoneCheck) {
    return false;
  }

  @Override
  public World getRangeWorldObj() {
    return getWorld();
  }

  @Override
  public float getRange() {
    return AVERSION_RANGE.getFloat(getCapacitorData()) / 2;
  }

  @Override
  public void onCapacitorDataChange() {    
    super.onCapacitorDataChange();
    BlockInhibitorObelisk.instance.activeInhibitors.put(getLocation(), getRange());
  }  

  @Override
  public void validate() {
    super.validate();
    BlockInhibitorObelisk.instance.activeInhibitors.put(getLocation(), getRange());
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
  public BoundingBox getRangeBox() {
    return new BoundingBox(new AxisAlignedBB(getPos(), getPos().add(1, 1, 1)).expand(getRange() / 2d, getRange() / 2d, getRange() / 2d)
        .expand(0.01, 0.01, 0.01).offset(-getPos().getX(), -getPos().getY(), -getPos().getZ()));
  }

}
