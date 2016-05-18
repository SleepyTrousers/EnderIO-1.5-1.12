package crazypants.enderio.machine.obelisk.inhibitor;

import info.loenwind.autosave.annotations.Storable;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;

import com.enderio.core.client.render.BoundingBox;

import crazypants.enderio.ModObject;
import crazypants.enderio.machine.AbstractPowerConsumerEntity;
import crazypants.enderio.machine.SlotDefinition;
import crazypants.enderio.machine.ranged.IRanged;

@Storable
public class TileInhibitorObelisk extends AbstractPowerConsumerEntity implements IRanged {

  private float range = 32;

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
    return range;
  }

  public void setRange(float range) {
    this.range = range;
    BlockInhibitorObelisk.instance.activeInhibitors.put(getLocation(), range);
  }

  @Override
  public boolean isShowingRange() {
    return false;
  }

  @Override
  public void validate() {
    super.validate();
    BlockInhibitorObelisk.instance.activeInhibitors.put(getLocation(), range);
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
