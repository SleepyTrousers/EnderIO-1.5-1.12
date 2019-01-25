package crazypants.enderio.machines.machine.mine;

import javax.annotation.Nonnull;

import com.enderio.core.common.inventory.EnderInventory.Type;
import com.enderio.core.common.inventory.Filters;
import com.enderio.core.common.inventory.InventorySlot;
import com.enderio.core.common.util.BlockCoord;
import com.enderio.core.common.util.blockiterators.CubicBlockIterator;

import crazypants.enderio.api.capacitor.ICapacitorKey;
import crazypants.enderio.base.init.ModObject;
import crazypants.enderio.base.machine.base.te.AbstractCapabilityPoweredMachineEntity;
import crazypants.enderio.machines.init.MachineObject;
import crazypants.enderio.util.Prep;
import info.loenwind.autosave.annotations.Storable;
import info.loenwind.autosave.annotations.Store;
import info.loenwind.autosave.util.NBTAction;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;

@Storable
public class TileMine extends AbstractCapabilityPoweredMachineEntity {

  protected enum InputSlot {
    SHAFT,
    TOOL
  }

  protected TileMine(@Nonnull ICapacitorKey maxEnergyRecieved, @Nonnull ICapacitorKey maxEnergyStored, @Nonnull ICapacitorKey maxEnergyUsed) {
    super(maxEnergyRecieved, maxEnergyStored, maxEnergyUsed);

    getInventory().add(Type.INPUT, InputSlot.SHAFT, new InventorySlot(new Filters.PredicateItemStack() {
      @Override
      public boolean doApply(@Nonnull ItemStack input) {
        return MachineObject.block_alloy_smelter.getItemNN() == input.getItem(); // TODO
      }
    }, Filters.ALWAYS_TRUE));
    getInventory().add(Type.INPUT, InputSlot.TOOL, new InventorySlot(new Filters.PredicateItemStack() {
      @Override
      public boolean doApply(@Nonnull ItemStack input) {
        return ModObject.itemDarkSteelPickaxe.getItemNN() == input.getItem();
      }
    }, Filters.ALWAYS_TRUE));

  }

  /**
   * Has the shaft been formed?
   */
  @Store({ NBTAction.CLIENT, NBTAction.SAVE })
  private boolean formed = false;
  /**
   * An iterator over the shaft cube. null while no checks are running
   */
  private CubicBlockIterator shaft = null;
  /**
   * A BlockPos in the shaft cube that needs to be re-check next tick. null while no checks are running or if the last position that was checked was fine
   */
  private BlockPos shaftNextCheck = null;
  /**
   * true if the shaft checker wants to put a shaft block down but there's none in the inventory
   */
  private boolean needShaftBlocks = false;

  public void onBlockRemoved(@Nonnull BlockPos childPos) {
    formed = false;
    shaft = null;
  }

  @Override
  public boolean isActive() {
    return false;
  }

  private boolean form() {
    if (needShaftBlocks && getInventory().getSlot(InputSlot.SHAFT).isEmpty()) {
      return false;
    }
    needShaftBlocks = false;
    if (shaft == null) {
      shaft = new CubicBlockIterator(pos.north(2).west(2).down(), BlockCoord.withY(pos.north(-2).west(-2), 0));
      shaftNextCheck = null;
    }
    if (shaftNextCheck == null && shaft.hasNext()) {
      shaftNextCheck = shaft.next();
    }
    if (shaftNextCheck != null) {
      IBlockState blockState = world.getBlockState(shaftNextCheck);
      if (blockState.getBlock() == MachineObject.block_alloy_smelter.getBlockNN()) { // TODO
        shaftNextCheck = null;
      } else if (blockState.getBlock().isAir(blockState, world, shaftNextCheck.toImmutable())) {
        ItemStack extractItem = getInventory().getSlot(InputSlot.SHAFT).extractItem(0, 1, false);
        if (Prep.isValid(extractItem)) {
          world.setBlockState(shaftNextCheck.toImmutable(), MachineObject.block_alloy_smelter.getBlockNN().getDefaultState());
          shaftNextCheck = null;
        } else {
          needShaftBlocks = true;
        }
      } else {
        // TODO try break block
      }
    }
    if (shaftNextCheck == null && !shaft.hasNext()) {
      shaft = null;
      return formed = true;
    }
    return false;
  }

}
