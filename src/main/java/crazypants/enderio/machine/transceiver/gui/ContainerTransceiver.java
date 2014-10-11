package crazypants.enderio.machine.transceiver.gui;

import java.awt.Point;
import java.util.Map.Entry;
import java.util.Set;

import crazypants.enderio.machine.AbstractMachineContainer;
import crazypants.enderio.machine.transceiver.TileTransceiver;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ContainerTransceiver extends AbstractMachineContainer {

  static final Point PLAYER_INV_OFFSET = new Point(47, 84);

  static final Point ITEM_INV_OFFSET = new Point(92, 23);

  static final Point HIDDEN_OFFSET = new Point(-3000, -3000);

  public ContainerTransceiver(InventoryPlayer inventory, TileTransceiver te) {
    super(inventory, te);
  }

  @Override
  protected void addMachineSlots(InventoryPlayer playerInv) {
    int i;
    for (i = 0; i < 4; i++) {
      addSlotToContainer(new Slot(tileEntity, i, 0, 0));
    }
    for (; i < 8; i++) {
      addSlotToContainer(new Slot(tileEntity, i, 0, 0) {
        @Override
        public boolean isItemValid(ItemStack p_75214_1_) {
          return false;
        }
      });
    }
    setItemSlotLocations(ITEM_INV_OFFSET);

  }

  public void setPlayerInventory(boolean visible) {
    Set<Entry<Slot, Point>> entries = playerSlotLocations.entrySet();
    for (Entry<Slot, Point> entry : entries) {
      entry.getKey().xDisplayPosition = visible ? entry.getValue().x : -3000;
      entry.getKey().yDisplayPosition = visible ? entry.getValue().y : -3000;
    }
    Point itemOffset = visible ? ITEM_INV_OFFSET : HIDDEN_OFFSET;       
    setItemSlotLocations(itemOffset);
  }

  private void setItemSlotLocations(Point offset) {
    int i;
    int x = offset.x;
    int y = offset.y;
    for (i = 0; i < 4; i++) {
      ((Slot) inventorySlots.get(i)).xDisplayPosition = x;
      ((Slot) inventorySlots.get(i)).yDisplayPosition = y;
      x += 18;
    }
    x = offset.x;
    y = offset.y + getItemRowSpacing();
    for (; i < 8; i++) {
      ((Slot) inventorySlots.get(i)).xDisplayPosition = x;
      ((Slot) inventorySlots.get(i)).yDisplayPosition = y;
      x += 18;
    }
  }

  @Override
  public Point getPlayerInventoryOffset() {
    return PLAYER_INV_OFFSET;    
  }
  
  public Point getItemInventoryOffset() {
    return ITEM_INV_OFFSET;    
  }
  
  public int getItemRowSpacing() {
    return 20;
  }

}
