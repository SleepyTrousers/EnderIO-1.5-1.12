package crazypants.enderio.machine.generator;

import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.util.ForgeDirection;
import buildcraft.api.power.IPowerEmitter;
import crazypants.enderio.ModObject;
import crazypants.enderio.machine.AbstractMachineEntity;
import crazypants.enderio.machine.SlotDefinition;

public class TileCombustionGenerator extends AbstractMachineEntity implements IPowerEmitter {

  public TileCombustionGenerator() {
    super(new SlotDefinition(-1, -1, -1, -1, -1, -1));
  }

  @Override
  public String getInventoryName() {
    return ModObject.blockCombustionGenerator.unlocalisedName;
  }

  @Override
  public boolean hasCustomInventoryName() {
    return false;
  }

  @Override
  public String getMachineName() {
    return ModObject.blockCombustionGenerator.unlocalisedName;
  }

  @Override
  public boolean canEmitPowerFrom(ForgeDirection side) {
    return true;
  }

  @Override
  protected boolean isMachineItemValidForSlot(int i, ItemStack itemstack) {
    return false;
  }

  @Override
  public boolean isActive() {
    return false;
  }

  @Override
  public float getProgress() {
    return 0;
  }

  @Override
  protected boolean processTasks(boolean redstoneCheckPassed) {
    return false;
  }
}
