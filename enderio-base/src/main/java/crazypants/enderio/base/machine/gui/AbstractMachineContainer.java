package crazypants.enderio.base.machine.gui;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.Nonnull;

import com.enderio.core.api.common.util.IProgressTile;
import com.enderio.core.common.ContainerEnder;
import com.enderio.core.common.util.Util;

import crazypants.enderio.base.gui.IconEIO;
import crazypants.enderio.base.machine.baselegacy.AbstractInventoryMachineEntity;
import crazypants.enderio.base.machine.baselegacy.AbstractInventoryMachineEntity.InventoryWrapper;
import crazypants.enderio.base.machine.baselegacy.SlotDefinition;
import crazypants.enderio.util.Prep;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public abstract class AbstractMachineContainer<E extends AbstractInventoryMachineEntity> extends ContainerEnder<InventoryWrapper> {

  protected Slot upgradeSlot;
  protected final @Nonnull E te;

  public AbstractMachineContainer(@Nonnull InventoryPlayer playerInv, @Nonnull E te) {
    super(playerInv, te.getAsInventory());
    this.te = te;
  }

  public @Nonnull E getTe() {
    return te;
  }

  @Override
  protected void addSlots(@Nonnull InventoryPlayer playerInv) {
    addMachineSlots(playerInv);

    if (getInv().getOwner().getSlotDefinition().getNumUpgradeSlots() == 1) {
      addSlotToContainer(
          upgradeSlot = new Slot(getInv(), getInv().getOwner().getSlotDefinition().getMinUpgradeSlot(), getUpgradeOffset().x, getUpgradeOffset().y) {

            @Override
            public int getSlotStackLimit() {
              return 1;
            }

            @Override
            public boolean isItemValid(@Nonnull ItemStack itemStack) {
              return te.isItemValidForSlot(te.getSlotDefinition().getMinUpgradeSlot(), itemStack);
            }

            @Override
            @SideOnly(Side.CLIENT)
            public @Nonnull ResourceLocation getBackgroundLocation() {
              return IconEIO.CAPACITOR.getMap().getTexture();
            }

            @Override
            @SideOnly(Side.CLIENT)
            public @Nonnull TextureAtlasSprite getBackgroundSprite() {
              return IconEIO.CAPACITOR.getAsTextureAtlasSprite();
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

  public Slot getUpgradeSlot() {
    return upgradeSlot;
  }

  /**
   * ATTN: Do not access any non-static field from this method. Your object has not yet been constructed when it is called!
   */
  protected abstract void addMachineSlots(@Nonnull InventoryPlayer playerInv);

  @Override
  public @Nonnull ItemStack transferStackInSlot(@Nonnull EntityPlayer entityPlayer, int slotNumber) {
    hasAlreadyJustSuccessfullyTransferedAStack = false;
    SlotDefinition slotDef = te.getSlotDefinition();

    ItemStack copystack = Prep.getEmpty();
    Slot slot = inventorySlots.get(slotNumber);
    if (slot != null && slot.getHasStack()) {
      ItemStack origStack = slot.getStack();
      if (Prep.isValid(origStack)) {
        copystack = origStack.copy();

        boolean merged = false;
        for (SlotRange range : getTargetSlotsForTransfer(slotNumber, slot)) {
          if (mergeItemStack(origStack, range.getStart(), range.getEnd(), range.reverse)) {
            while (mergeItemStack(origStack, range.getStart(), range.getEnd(), range.reverse)) {
            }
            merged = true;
            break;
          }
        }

        if (!merged) {
          return Prep.getEmpty();
        }

        if (slotDef.isOutputSlot(slot.getSlotIndex())) {
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

  // TODO 1.12
  // @Override
  // protected void retrySlotClick(int slotId, int clickedButton, boolean mode, @Nonnull EntityPlayer playerIn) {
  // if (!hasAlreadyJustSuccessfullyTransferedAStack) {
  // this.slotClick(slotId, clickedButton, ClickType.QUICK_MOVE, playerIn);
  // } else {
  // hasAlreadyJustSuccessfullyTransferedAStack = false;
  // }
  // }

  protected int getIndexOfFirstPlayerInvSlot(@Nonnull SlotDefinition slotDef) {
    return slotDef.getNumSlots();
  }

  protected SlotRange getPlayerInventorySlotRange(boolean reverse) {
    return new SlotRange(startPlayerSlot, endHotBarSlot, reverse);
  }

  protected SlotRange getPlayerInventoryWithoutHotbarSlotRange() {
    return new SlotRange(startPlayerSlot, endPlayerSlot, false);
  }

  protected SlotRange getPlayerHotbarSlotRange() {
    return new SlotRange(startHotBarSlot, endHotBarSlot, false);
  }

  protected void addInventorySlotRange(@Nonnull List<SlotRange> res, int start, int end) {
    SlotRange range = null;
    for (int i = start; i < end; i++) {
      Slot slotFromInventory = getSlotFromInventory(getInv(), i);
      int slotNumber = slotFromInventory.slotNumber;
      if (range == null) {
        range = new SlotRange(slotNumber, slotNumber + 1, false);
      } else if (range.getEnd() == slotNumber) {
        range = new SlotRange(range.getStart(), slotNumber + 1, false);
      } else {
        res.add(range);
        range = new SlotRange(slotNumber, slotNumber + 1, false);
      }
    }
    if (range != null) {
      res.add(range);
    }
  }

  protected void addInputSlotRanges(@Nonnull List<SlotRange> res) {
    SlotDefinition slotDef = te.getSlotDefinition();
    if (slotDef.getNumInputSlots() > 0) {
      addInventorySlotRange(res, slotDef.getMinInputSlot(), slotDef.getMaxInputSlot() + 1);
    }
  }

  protected void addUpgradeSlotRanges(@Nonnull List<SlotRange> res) {
    SlotDefinition slotDef = te.getSlotDefinition();
    if (slotDef.getNumUpgradeSlots() > 0) {
      addInventorySlotRange(res, slotDef.getMinUpgradeSlot(), slotDef.getMaxUpgradeSlot() + 1);
    }
  }

  protected void addPlayerSlotRanges(@Nonnull List<SlotRange> res, int slotIndex) {
    if (slotIndex < endPlayerSlot) {
      res.add(getPlayerHotbarSlotRange());
    }
    if (slotIndex >= startHotBarSlot && slotIndex < endHotBarSlot) {
      res.add(getPlayerInventoryWithoutHotbarSlotRange());
    }
  }

  protected @Nonnull List<SlotRange> getTargetSlotsForTransfer(int slotNumber, @Nonnull Slot slot) {
    if (slot.inventory == getInv()) {
      SlotDefinition slotDef = te.getSlotDefinition();
      if (slotDef.isInputSlot(slot.getSlotIndex()) || slotDef.isUpgradeSlot(slot.getSlotIndex())) {
        return Collections.singletonList(getPlayerInventorySlotRange(false));
      }
      if (slotDef.isOutputSlot(slot.getSlotIndex())) {
        return Collections.singletonList(getPlayerInventorySlotRange(true));
      }
    } else if (slotNumber >= startPlayerSlot) {
      List<SlotRange> res = new ArrayList<SlotRange>();
      addUpgradeSlotRanges(res);
      addInputSlotRanges(res);
      addPlayerSlotRanges(res, slotNumber);
      return res;
    }
    return Collections.emptyList();
  }

  protected int getProgressScaled(int scale) {
    if (te instanceof IProgressTile) {
      return Util.getProgressScaled(scale, (IProgressTile) te);
    }
    return 0;
  }

  public static class SlotRange {
    private final int start;
    private final int end;
    final boolean reverse;

    public SlotRange(int start, int end, boolean reverse) {
      this.start = start;
      this.end = end;
      this.reverse = reverse;
    }

    public int getStart() {
      return start;
    }

    public int getEnd() {
      return end;
    }
  }

  private int guiID = -1;

  public void setGuiID(int id) {
    guiID = id;
  }

  public int getGuiID() {
    return guiID;
  }

  @Override
  public void detectAndSendChanges() {
    super.detectAndSendChanges();
    // getInv() is not the te, so we have to do super's work for the real te
    final SPacketUpdateTileEntity updatePacket = te.getUpdatePacket();
    if (updatePacket != null) {
      for (IContainerListener containerListener : listeners) {
        if (containerListener instanceof EntityPlayerMP) {
          ((EntityPlayerMP) containerListener).connection.sendPacket(updatePacket);
        }
      }
    }
  }

}
