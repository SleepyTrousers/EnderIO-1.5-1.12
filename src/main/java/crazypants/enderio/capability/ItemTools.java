package crazypants.enderio.capability;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import crazypants.enderio.machine.AbstractMachineEntity;
import crazypants.enderio.machine.IoMode;

public class ItemTools {

  @CapabilityInject(IItemHandler.class)
  public static Capability<IItemHandler> ITEM_HANDLER_CAPABILITY = null;

  private ItemTools() {
  }

  public static boolean doPush(IBlockAccess world, BlockPos pos) {
    boolean result = false;
    for (EnumFacing facing : EnumFacing.values()) {
      MoveResult moveResult = move(world, pos, facing, pos.offset(facing), facing.getOpposite());
      if (moveResult == MoveResult.SOURCE_EMPTY) {
        return false;
      } else if (moveResult == MoveResult.MOVED) {
        result = true;
      }
    }
    return result;
  }

  public static boolean doPull(IBlockAccess world, BlockPos pos) {
    boolean result = false;
    for (EnumFacing facing : EnumFacing.values()) {
      MoveResult moveResult = move(world, pos.offset(facing), facing.getOpposite(), pos, facing);
      if (moveResult == MoveResult.TARGET_FULL) {
        return false;
      } else if (moveResult == MoveResult.MOVED) {
        result = true;
      }
    }
    return result;
  }

  public static enum MoveResult {
    NO_ACTION,
    MOVED,
    TARGET_FULL,
    SOURCE_EMPTY;
  }

  public static MoveResult move(IBlockAccess world, BlockPos sourcePos, EnumFacing sourceFacing, BlockPos targetPos, EnumFacing targetFacing) {
    boolean movedSomething = false;
    TileEntity source = world.getTileEntity(sourcePos);
    if (source != null && source.hasWorldObj() && !source.getWorld().isRemote && canPullFrom(source, sourceFacing)) {
      TileEntity target = world.getTileEntity(targetPos);
      if (target != null && target.hasWorldObj() && canPutInto(target, targetFacing)) {
        IItemHandler sourceHandler = source.getCapability(ITEM_HANDLER_CAPABILITY, sourceFacing);
        if (sourceHandler != null && hasItems(sourceHandler)) {
          IItemHandler targetHandler = target.getCapability(ITEM_HANDLER_CAPABILITY, targetFacing);
          if (targetHandler != null && hasFreeSpace(targetHandler)) {
            for (int i = 0; i < sourceHandler.getSlots(); i++) {
              ItemStack removable = sourceHandler.extractItem(i, Integer.MAX_VALUE, true);
              if (removable != null && removable.stackSize > 0) {
                ItemStack unacceptable = ItemHandlerHelper.insertItemStacked(targetHandler, removable, true);
                int movable = removable.stackSize - (unacceptable == null ? 0 : unacceptable.stackSize);
                if (movable > 0) {
                  movedSomething = true;
                  ItemStack removed = sourceHandler.extractItem(i, movable, false);
                  if (removed != null && removed.stackSize > 0) {
                    ItemStack targetRejected = ItemHandlerHelper.insertItemStacked(targetHandler, removed, false);
                    if (targetRejected != null && targetRejected.stackSize > 0) {
                      ItemStack sourceRejected = ItemHandlerHelper.insertItemStacked(sourceHandler, removed, false);
                      if (sourceRejected != null && sourceRejected.stackSize > 0) {
                        EntityItem drop = new EntityItem(source.getWorld(), sourcePos.getX() + 0.5, sourcePos.getY() + 0.5, sourcePos.getZ() + 0.5,
                            sourceRejected);
                        source.getWorld().spawnEntityInWorld(drop);
                      }
                    }
                  }
                }
              }
            }
          } else {
            return MoveResult.TARGET_FULL;
          }
        } else {
          return MoveResult.SOURCE_EMPTY;
        }
      } else {
        return MoveResult.TARGET_FULL;
      }
    } else {
      return MoveResult.SOURCE_EMPTY;
    }
    return movedSomething ? MoveResult.MOVED : MoveResult.NO_ACTION;
  }

  public static boolean hasFreeSpace(IItemHandler handler) {
    for (int i = 0; i < handler.getSlots(); i++) {
      ItemStack stack = handler.getStackInSlot(i);
      if (stack == null || (stack.isStackable() == stack.stackSize < stack.getMaxStackSize())) {
        return true;
      }
    }
    return false;
  }

  public static boolean hasItems(IItemHandler handler) {
    for (int i = 0; i < handler.getSlots(); i++) {
      ItemStack stack = handler.getStackInSlot(i);
      if (stack != null && stack.stackSize > 0) {
        return true;
      }
    }
    return false;
  }

  public static boolean canPutInto(TileEntity tileEntity, EnumFacing facing) {
    if (tileEntity instanceof AbstractMachineEntity) {
      IoMode ioMode = ((AbstractMachineEntity) tileEntity).getIoMode(facing);
      return ioMode != IoMode.DISABLED && ioMode != IoMode.PUSH;
    }
    return true;
  }

  public static boolean canPullFrom(TileEntity tileEntity, EnumFacing facing) {
    if (tileEntity instanceof AbstractMachineEntity) {
      IoMode ioMode = ((AbstractMachineEntity) tileEntity).getIoMode(facing);
      return ioMode != IoMode.DISABLED && ioMode != IoMode.PULL;
    }
    return true;
  }

}
