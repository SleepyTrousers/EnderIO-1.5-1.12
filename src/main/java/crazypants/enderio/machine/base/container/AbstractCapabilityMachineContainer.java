package crazypants.enderio.machine.base.container;

import java.awt.Point;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.api.common.util.IProgressTile;
import com.enderio.core.common.ContainerItemHandler;
import com.enderio.core.common.inventory.EnderInventory.Type;
import com.enderio.core.common.util.NullHelper;
import com.enderio.core.common.util.Util;

import crazypants.enderio.machine.base.container.SlotRangeHelper.IRangeProvider;
import crazypants.enderio.machine.base.container.SlotRangeHelper.SlotRange;
import crazypants.enderio.machine.base.te.AbstractCapabilityMachineEntity;
import crazypants.util.Prep;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

public abstract class AbstractCapabilityMachineContainer<E extends AbstractCapabilityMachineEntity> extends ContainerItemHandler<E> implements IRangeProvider {

  protected Slot upgradeSlot;
  private final SlotRangeHelper<? extends AbstractCapabilityMachineContainer<?>> rangeHelper = new SlotRangeHelper.Cap<>(this);

  public AbstractCapabilityMachineContainer(@Nonnull InventoryPlayer playerInv, @Nonnull E te, @Nullable EnumFacing facing) {
    super(playerInv, te, facing);
  }

  @Override
  protected void addSlots(@Nonnull InventoryPlayer playerInv) {
    addMachineSlots(playerInv);

    IItemHandler upgradeSlots = getOwner().getInventory().getView(Type.UPGRADE);
    if (upgradeSlots.getSlots() == 1) {
      addSlotToContainer(upgradeSlot = new SlotItemHandler(upgradeSlots, 0, getUpgradeOffset().x, getUpgradeOffset().y) {

        @Override
        public int getSlotStackLimit() {
          return 1;
        }

        @Override
        public boolean isItemValid(@Nonnull ItemStack itemStack) {
          return NullHelper.notnull(upgradeSlots.insertItem(0, itemStack, true), "insertItem returned null").getCount() < itemStack.getCount();
        }
      });
    }
  }

  @Override
  public @Nonnull Point getPlayerInventoryOffset() {
    return new Point(8, 84);
  }

  @Override
  public @Nonnull Point getUpgradeOffset() {
    return new Point(12, 60);
  }
  
  @Override
  public boolean canInteractWith(EntityPlayer playerIn) {
    return getOwner().canPlayerAccess(playerIn);
  }

  public Slot getUpgradeSlot() {
    return upgradeSlot;
  }

  /**
   * ATTN: Do not access any non-static field from this method. Your object has not yet been constructed when it is called!
   */
  protected abstract void addMachineSlots(InventoryPlayer playerInv);

  @Override
  public @Nonnull ItemStack transferStackInSlot(@Nonnull EntityPlayer entityPlayer, int slotNumber) {
    hasAlreadyJustSuccessfullyTransferedAStack = false;
    ItemStack copystack = Prep.getEmpty();
    Slot slot = inventorySlots.get(slotNumber);
    if (slot != null && slot.getHasStack()) {
      ItemStack origStack = slot.getStack();
      IItemHandler handler = ((SlotItemHandler)slot).getItemHandler();
      if (Prep.isValid(origStack)) {
        copystack = origStack.copy();

        boolean merged = false;
        for (SlotRange range : rangeHelper.getTargetSlotsForTransfer(slotNumber, slot)) {
          if (mergeItemStack(origStack, range.getStart(), range.getEnd(), range.isReverse())) {
            while (mergeItemStack(origStack, range.getStart(), range.getEnd(), range.isReverse())) {
            }
            merged = true;
            break;
          }
        }

        if (!merged) {
          return Prep.getEmpty();
        }

        if (handler == getOwner().getInventory().getView(Type.OUTPUT)) {
          slot.onSlotChange(origStack, copystack);
        }

        if (Prep.isInvalid(origStack)) {
          slot.putStack(Prep.getEmpty());
        } else {
          slot.onSlotChanged();
        }

        if (origStack.getCount() == copystack.getCount()) {
          return Prep.getEmpty();
        }

        slot.onTake(entityPlayer, origStack);
      }
    }

    hasAlreadyJustSuccessfullyTransferedAStack = true;
    return copystack;
  }

  private boolean hasAlreadyJustSuccessfullyTransferedAStack = false;

  @Override
  protected void retrySlotClick(int slotId, int clickedButton, boolean mode, @Nonnull EntityPlayer playerIn) {
    if (!hasAlreadyJustSuccessfullyTransferedAStack) {
      this.slotClick(slotId, clickedButton, ClickType.QUICK_MOVE, playerIn);
    } else {
      hasAlreadyJustSuccessfullyTransferedAStack = false;
    }
  }

  @Override
  public SlotRange getPlayerInventorySlotRange(boolean reverse) {
    return new SlotRange(startPlayerSlot, endHotBarSlot, reverse);
  }

  @Override
  public SlotRange getPlayerInventoryWithoutHotbarSlotRange() {
    return new SlotRange(startPlayerSlot, endPlayerSlot, false);
  }

  @Override
  public SlotRange getPlayerHotbarSlotRange() {
    return new SlotRange(startHotBarSlot, endHotBarSlot, false);
  }

  protected int getProgressScaled(int scale) {
    if (getOwner() instanceof IProgressTile) {
      Util.getProgressScaled(scale, (IProgressTile) getOwner());
    }
    return 0;
  }

  private int guiID = -1;

  public void setGuiID(int id) {
    guiID = id;
  }

  public int getGuiID() {
    return guiID;
  }

}
