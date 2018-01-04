package crazypants.enderio.machines.machine.transceiver.gui;

import java.awt.Point;
import java.util.Map.Entry;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import crazypants.enderio.base.machine.baselegacy.SlotDefinition;
import crazypants.enderio.base.machine.gui.AbstractMachineContainer;
import crazypants.enderio.machines.machine.transceiver.TileTransceiver;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class ContainerTransceiver extends AbstractMachineContainer<TileTransceiver> implements ITransceiverRemoteExec.Container {

  public static final int GUI_WIDTH = 256;

  static final @Nonnull Point PLAYER_INV_OFFSET = new Point(47, 86);

  static final @Nonnull Point ITEM_INV_OFFSET = new Point(54, 30);

  static final @Nonnull Point HIDDEN_OFFSET = new Point(-3000, -3000);

  static final @Nonnull Point FILTER_OFFSET = new Point(PLAYER_INV_OFFSET.x, 30);

  public ContainerTransceiver(@Nonnull InventoryPlayer inventory, @Nonnull TileTransceiver te) {
    super(inventory, te);
  }

  @Override
  protected void addMachineSlots(@Nonnull InventoryPlayer playerInv) {

    int i;
    for (i = 0; i < 8; i++) {
      addSlotToContainer(new Slot(getInv(), i, 0, 0) {
        @Override
        public boolean isItemValid(@Nonnull ItemStack itemstack) {
          return getInv().isItemValidForSlot(getSlotIndex(), itemstack);
        }
      });
    }
    for (; i < 16; i++) {
      addSlotToContainer(new Slot(getInv(), i, 0, 0) {
        @Override
        public boolean isItemValid(@Nullable ItemStack p_75214_1_) {
          return false;
        }
      });
    }
    setItemSlotLocations(getItemInventoryOffset());
  }

  public void setPlayerInventoryVisible(boolean visible) {
    Set<Entry<Slot, Point>> entries = playerSlotLocations.entrySet();
    for (Entry<Slot, Point> entry : entries) {
      entry.getKey().xPos = visible ? entry.getValue().x : -3000;
      entry.getKey().yPos = visible ? entry.getValue().y : -3000;
    }
  }

  public void setBufferSlotsVisible(boolean visible) {
    Point itemOffset = visible ? getItemInventoryOffset() : HIDDEN_OFFSET;
    setItemSlotLocations(itemOffset);
  }

  private void setItemSlotLocations(Point offset) {
    int i;
    int x = offset.x;
    int y = offset.y;
    for (i = 0; i < 4; i++) {
      inventorySlots.get(i).xPos = x;
      inventorySlots.get(i).yPos = y;
      x += 18;
    }
    x = offset.x;
    y = offset.y + 18;
    for (; i < 8; i++) {
      inventorySlots.get(i).xPos = x;
      inventorySlots.get(i).yPos = y;
      x += 18;
    }

    x = offset.x + (18 * 4) + getItemBufferSpacing();
    y = offset.y;
    for (; i < 12; i++) {
      inventorySlots.get(i).xPos = x;
      inventorySlots.get(i).yPos = y;
      x += 18;
    }
    x = offset.x + (18 * 4) + getItemBufferSpacing();
    y = offset.y + 18;
    for (; i < 16; i++) {
      inventorySlots.get(i).xPos = x;
      inventorySlots.get(i).yPos = y;
      x += 18;
    }
  }

  @Override
  public @Nonnull Point getPlayerInventoryOffset() {
    return PLAYER_INV_OFFSET;
  }

  public Point getItemInventoryOffset() {
    return ITEM_INV_OFFSET;
  }

  public Point getFilterOffset() {
    return FILTER_OFFSET;
  }

  public int getItemBufferSpacing() {
    return 5;
  }

  @Override
  protected int getIndexOfFirstPlayerInvSlot(@Nonnull SlotDefinition slotDef) {
    return slotDef.getNumSlots();
  }

  @Override
  public IMessage doSetBufferStacks(boolean bufferStacks) {
    getTe().setBufferStacks(bufferStacks);
    return null;
  }

}
