package crazypants.enderio.machine.obelisk.inhibitor;

import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import crazypants.enderio.ModObject;
import crazypants.enderio.config.Config;
import crazypants.enderio.machine.AbstractPowerConsumerEntity;
import crazypants.enderio.machine.SlotDefinition;
import crazypants.enderio.machine.ranged.IRanged;
import crazypants.enderio.machine.ranged.RangeEntity;
import crazypants.enderio.power.BasicCapacitor;

public class TileInhibitorObelisk extends AbstractPowerConsumerEntity implements IRanged {

  private float range = 32;
  private boolean showingRange;
  private RangeEntity myEntity;
  private int powerPerTick;

  public TileInhibitorObelisk() {
    super(new SlotDefinition(0, 0, 1));
  }

  @Override
  public String getMachineName() {
    return ModObject.blockInhibitorObelisk.unlocalisedName;
  }

  @Override
  public boolean isShowingRange() {
    return showingRange;
  }

  @SideOnly(Side.CLIENT)
  public void setShowRange(boolean showRange) {
    if (myEntity == null) {
      myEntity = new RangeEntity(this);
      worldObj.spawnEntityInWorld(myEntity);
    }
    showingRange = showRange;
  }

  @Override
  protected boolean isMachineItemValidForSlot(int i, ItemStack itemstack) {
    return false;
  }

  @Override
  public boolean isActive() {
    return hasPower() && redstoneCheckPassed;
  }

  @Override
  protected boolean processTasks(boolean redstoneCheckPassed) {
    return false;
  }

  @Override
  public World getWorld() {
    return worldObj;
  }

  @Override
  public float getRange() {
    return range;
  }

  @Override
  public void onCapacitorTypeChange() {
    switch (getCapacitorType()) {
    case BASIC_CAPACITOR:
      range = Config.spawnGuardRangeLevelOne;
      powerPerTick = Config.spawnGuardPowerPerTickLevelOne;
      break;
    case ACTIVATED_CAPACITOR:
      range = Config.spawnGuardRangeLevelTwo;
      powerPerTick = Config.spawnGuardPowerPerTickLevelTwo;
      break;
    case ENDER_CAPACITOR:
      range = Config.spawnGuardRangeLevelThree;
      powerPerTick = Config.spawnGuardPowerPerTickLevelThree;
      break;
    }
    setCapacitor(new BasicCapacitor(powerPerTick * 8, getCapacitor().getMaxEnergyStored(), powerPerTick));
  }

  public void setRange(float range) {
    this.range = range;
    BlockInhibitorObelisk.instance.activeInhibitors.put(getLocation(), range);
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
}
