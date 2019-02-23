package crazypants.enderio.machines.machine.mine;

import javax.annotation.Nonnull;

import com.enderio.core.common.inventory.EnderInventory.Type;
import com.enderio.core.common.inventory.Filters;
import com.enderio.core.common.inventory.InventorySlot;
import com.enderio.core.common.util.BlockCoord;
import com.enderio.core.common.util.NNList;
import com.enderio.core.common.util.blockiterators.AbstractBlockIterator;

import crazypants.enderio.api.capacitor.ICapacitorKey;
import crazypants.enderio.base.init.ModObject;
import crazypants.enderio.base.machine.base.te.AbstractCapabilityPoweredMachineEntity;
import crazypants.enderio.machines.init.MachineObject;
import crazypants.enderio.util.Prep;
import info.loenwind.autosave.annotations.Storable;
import info.loenwind.autosave.annotations.Store;
import info.loenwind.autosave.util.NBTAction;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;

import static crazypants.enderio.machines.capacitor.CapacitorKey.FARM_POWER_BUFFER;
import static crazypants.enderio.machines.capacitor.CapacitorKey.FARM_POWER_INTAKE;
import static crazypants.enderio.machines.capacitor.CapacitorKey.FARM_POWER_USE;

@Storable
public class TileMine extends AbstractCapabilityPoweredMachineEntity {

  protected enum InputSlot {
    SHAFT,
    TOOL
  }

  protected enum OutputSlot {
    SLOT0,
    SLOT1,
    SLOT2,
    SLOT3,
    SLOT4,
    SLOT5
  }

  public TileMine() {
    this(FARM_POWER_INTAKE, FARM_POWER_BUFFER, FARM_POWER_USE); // TODO
  }

  protected TileMine(@Nonnull ICapacitorKey maxEnergyRecieved, @Nonnull ICapacitorKey maxEnergyStored, @Nonnull ICapacitorKey maxEnergyUsed) {
    super(maxEnergyRecieved, maxEnergyStored, maxEnergyUsed);

    getInventory().add(Type.INPUT, InputSlot.SHAFT, new InventorySlot(new Filters.PredicateItemStack() {
      @Override
      public boolean doApply(@Nonnull ItemStack input) {
        return MachineObject.block_mine_shaft.getItemNN() == input.getItem();
      }
    }, Filters.ALWAYS_TRUE));
    getInventory().add(Type.INPUT, InputSlot.TOOL, new InventorySlot(new Filters.PredicateItemStack() {
      @Override
      public boolean doApply(@Nonnull ItemStack input) {
        return ModObject.itemDarkSteelPickaxe.getItemNN() == input.getItem();
      }
    }, Filters.ALWAYS_TRUE));

    NNList.of(OutputSlot.class).apply(slot -> getInventory().add(Type.OUTPUT, slot, new InventorySlot(Filters.ALWAYS_FALSE, Filters.ALWAYS_TRUE)));
  }

  /**
   * Has the shaft been formed?
   */
  @Store({ NBTAction.CLIENT, NBTAction.SAVE })
  private boolean formed = false;
  /**
   * An iterator over the shaft cube. null while no checks are running
   */
  private AbstractBlockIterator shaft = null;
  /**
   * A BlockPos in the shaft cube that needs to be re-check next tick. null while no checks are running or if the last position that was checked was fine
   */
  private BlockPos shaftNextCheck = null;
  /**
   * true if the shaft checker wants to put a shaft block down but there's none in the inventory
   */
  private boolean needShaftBlocks = false;

  @Store({ NBTAction.ITEM, NBTAction.SAVE })
  private final @Nonnull NNList<ItemStack> outputQueue = new NNList<>();

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
      shaft = new CubicBlockIteratorReversed(pos.north(2).west(2).down(), BlockCoord.withY(pos.north(-2).west(-2), 0));
      shaftNextCheck = null;
    }
    if (shaftNextCheck == null && shaft.hasNext()) {
      shaftNextCheck = shaft.next();
    }
    final BlockPos targetPos = shaftNextCheck;
    if (targetPos != null) {
      IBlockState blockState = world.getBlockState(targetPos);
      if (blockState.getBlock() == Blocks.BEDROCK) {
        shaftNextCheck = null;
      } else if (blockState.getBlock() == MachineObject.block_mine_shaft.getBlockNN()) {
        TileEntity tileEntity = world.getTileEntity(targetPos);
        if (tileEntity instanceof TileMineShaft) {
          // TODO check if parent is set and points to another valid mine. set permanent error in that case
          ((TileMineShaft) tileEntity).setParent(pos);
        } // else what?
        shaftNextCheck = null;
      } else if (blockState.getBlock().isAir(blockState, world, targetPos)) {
        ItemStack extractItem = getInventory().getSlot(InputSlot.SHAFT).extractItem(0, 1, false);
        if (Prep.isValid(extractItem)) {
          world.setBlockState(targetPos, MachineObject.block_mine_shaft.getBlockNN().getDefaultState());
          TileEntity tileEntity = world.getTileEntity(targetPos);
          if (tileEntity instanceof TileMineShaft) {
            ((TileMineShaft) tileEntity).setParent(pos);
          }
          shaftNextCheck = null;
        } else {
          needShaftBlocks = true;
        }
      } else {
        outputQueue.addAll(BlockBreakingUtil.breakBlock(world, targetPos, true));
      }
    }
    if (shaftNextCheck == null && !shaft.hasNext()) {
      shaft = null;
      return formed = true;
    }
    return false;
  }

  // World.isChunkGeneratedAt(blockpos >>4)

  @Override
  protected boolean processTasks(boolean redstoneCheck) {
    if (redstoneCheck && getEnergy().useEnergy()) {
      if (!formed) {
        form();
      } else if (outputQueue.size() < 10) {
        // do work
      }
    }
    if (!outputQueue.isEmpty()) {
      for (InventorySlot slot : outputSlots) {
        outputQueue.set(0, slot.insertItem(0, outputQueue.get(0), false));
        if (Prep.isInvalid(outputQueue.get(0))) {
          outputQueue.remove(0);
          break;
        }
      }
    }
    return super.processTasks(redstoneCheck);
  }

}
