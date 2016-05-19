package crazypants.enderio.machine.obelisk;

import com.enderio.core.client.render.BoundingBox;

import crazypants.enderio.Log;
import crazypants.enderio.ModObject;
import crazypants.enderio.capacitor.ICapacitorKey;
import crazypants.enderio.machine.AbstractPowerConsumerEntity;
import crazypants.enderio.machine.SlotDefinition;
import crazypants.enderio.machine.ranged.IRanged;
import crazypants.enderio.machine.ranged.RangeEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public abstract class AbstractRangedTileEntity extends AbstractPowerConsumerEntity implements IRanged {
  
  private AxisAlignedBB bounds;
  private boolean showingRange;
  
  public AbstractRangedTileEntity(SlotDefinition slotDefinition, ICapacitorKey maxEnergyRecieved, ICapacitorKey maxEnergyStored, ICapacitorKey maxEnergyUsed) {
    super(slotDefinition, maxEnergyRecieved, maxEnergyStored, maxEnergyUsed);
  }

  public AbstractRangedTileEntity(SlotDefinition slotDefinition, ModObject modObject) {
    super(slotDefinition, modObject);  
  }
  
  @Override
  @SideOnly(Side.CLIENT)
  public boolean isShowingRange() {
    return showingRange;
  }

  @SideOnly(Side.CLIENT)
  public void setShowRange(boolean showRange) {
    if(showingRange == showRange) {
      return;
    }
    showingRange = showRange;
    if(showingRange) {
      Log.debug("AbstractRangedTileEntity.setShowRange: Spawned range entity into the world with a range of " + getRange());
      worldObj.spawnEntityInWorld(new RangeEntity(this));
    }
  }
  
  @Override
  public void onCapacitorDataChange() {
    super.onCapacitorDataChange();
    bounds = null;
  }
  
  @Override
  public BoundingBox getRangeBox() {
    mkBounds();
    return new BoundingBox(bounds.expand(0.01, 0.01, 0.01).offset(-getPos().getX(), -getPos().getY(), -getPos().getZ()));
  }
  
  protected void mkBounds() {
    if (bounds == null) {
      bounds = new AxisAlignedBB(getPos(), getPos().add(1, 1, 1)).expand(getRange() / 2d, getRange() / 2d, getRange() / 2d);
    }
  }

  public AxisAlignedBB getBounds() {
    return bounds;
  }

  public void setBounds(AxisAlignedBB bounds) {
    this.bounds = bounds;
  }

}
