package crazypants.enderio.machine.transceiver.gui;

import java.awt.Point;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import crazypants.enderio.conduit.gui.item.InventoryFilterUpgrade;
import crazypants.enderio.machine.SlotDefinition;
import crazypants.enderio.machine.gui.AbstractMachineContainer;
import crazypants.enderio.machine.transceiver.TileTransceiver;

public class ContainerTransceiver extends AbstractMachineContainer {

  public static final int GUI_WIDTH = 256;

  static final Point PLAYER_INV_OFFSET = new Point(47, 86);

  static final Point ITEM_INV_OFFSET = new Point(54, 30);
  
  static final Point HIDDEN_OFFSET = new Point(-3000, -3000);

  static final Point FILTER_OFFSET = new Point(PLAYER_INV_OFFSET.x, 30);

  private TileTransceiver trans;

  private Map<Slot, Point> sendFilterLocs;
  private Map<Slot, Point> recFilterLocs;

  public ContainerTransceiver(InventoryPlayer inventory, TileTransceiver te) {
    super(inventory, te);
    trans = te;
  }

  @Override
  protected void addMachineSlots(InventoryPlayer playerInv) {

    trans = (TileTransceiver) tileEntity;

    int i;
    for (i = 0; i < 8; i++) {
      addSlotToContainer(new Slot(tileEntity, i, 0, 0) {
        @Override
        public boolean isItemValid(ItemStack itemstack) {
          return trans.isItemValidForSlot(getSlotIndex(), itemstack);
          //return true;
        }
      });
    }
    for (; i < 16; i++) {
      addSlotToContainer(new Slot(tileEntity, i, 0, 0) {
        @Override
        public boolean isItemValid(ItemStack p_75214_1_) {
          return false;
        }
      });
    }
    setItemSlotLocations(getItemInventoryOffset());

    List<Slot> sendFilterSlots = trans.getSendItemFilter().getSlots(0, 0);
    sendFilterLocs = new HashMap<Slot, Point>();
    for (Slot slot : sendFilterSlots) {
      addSlotToContainer(slot);
      sendFilterLocs.put(slot, new Point(slot.xDisplayPosition, slot.yDisplayPosition));
    }
    List<Slot> recFilterSlots = trans.getReceiveItemFilter().getSlots(0, 0);
    recFilterLocs = new HashMap<Slot, Point>();
    for (Slot slot : recFilterSlots) {
      addSlotToContainer(slot);
      recFilterLocs.put(slot, new Point(slot.xDisplayPosition, slot.yDisplayPosition));
    }
    setSendFilterSlotsVisible(false);
    setReceiveFilterSlotsVisible(false);

  }

  public void setPlayerInventoryVisible(boolean visible) {
    Set<Entry<Slot, Point>> entries = playerSlotLocations.entrySet();
    for (Entry<Slot, Point> entry : entries) {
      entry.getKey().xDisplayPosition = visible ? entry.getValue().x : -3000;
      entry.getKey().yDisplayPosition = visible ? entry.getValue().y : -3000;
    }
  }

  public void setBufferSlotsVisible(boolean visible) {
    Point itemOffset = visible ? getItemInventoryOffset() : HIDDEN_OFFSET;       
    setItemSlotLocations(itemOffset);
  }

  public void setSendFilterSlotsVisible(boolean visible) {
    setFilterSlotsVisible(sendFilterLocs, visible);
  }

  public void setReceiveFilterSlotsVisible(boolean visible) {
    setFilterSlotsVisible(recFilterLocs, visible);
  }

  public void setFilterSlotsVisible(Map<Slot, Point> slots, boolean visible) {
    Point offset = HIDDEN_OFFSET;
    if(visible) {
      offset = FILTER_OFFSET;
    }
    for (Entry<Slot, Point> entry : slots.entrySet()) {
      entry.getKey().xDisplayPosition = offset.x + entry.getValue().x + 1;
      entry.getKey().yDisplayPosition = offset.y + entry.getValue().y + 1;
    }
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
    y = offset.y + 18;
    for (; i < 8; i++) {      
      ((Slot) inventorySlots.get(i)).xDisplayPosition = x;
      ((Slot) inventorySlots.get(i)).yDisplayPosition = y;
      x += 18;
    }
    
    x = offset.x + (18 * 4) + getItemBufferSpacing();
    //y = offset.y + 18 + getItemRowSpacing();
    y = offset.y;
    for (; i < 12; i++) {
      ((Slot) inventorySlots.get(i)).xDisplayPosition = x;
      ((Slot) inventorySlots.get(i)).yDisplayPosition = y;
      x += 18;
    }
    x = offset.x + (18 * 4) + getItemBufferSpacing();
    y = offset.y + 18;
    for (; i < 16; i++) {
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
  
  public Point getFilterOffset() {
    return FILTER_OFFSET;
  }

  public int getItemBufferSpacing() {    
    return 5;
  }

  @Override
  protected int getIndexOfFirstPlayerInvSlot(SlotDefinition slotDef) {
    return slotDef.getNumSlots() + sendFilterLocs.size() + recFilterLocs.size();
  }

  public static class FilterSlot extends Slot {

    InventoryFilterUpgrade inv;

    public FilterSlot(InventoryFilterUpgrade inv) {
      super(inv, 0, 0, 0);
      this.inv = inv;
    }

    @Override
    public boolean isItemValid(ItemStack stack) {
      return inv.isItemValidForSlot(0, stack);
    }

  }

}
