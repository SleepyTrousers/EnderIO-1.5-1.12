package crazypants.enderio.machine.transceiver.gui;

import java.awt.Point;
import java.util.Map.Entry;
import java.util.Set;

import crazypants.enderio.machine.AbstractMachineContainer;
import crazypants.enderio.machine.transceiver.TileTransceiver;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;

public class ContainerTransceiver extends AbstractMachineContainer {

  private static final Point INV_OFFSET = new Point(47, 84);
  
  public ContainerTransceiver(InventoryPlayer inventory, TileTransceiver te) {
    super(inventory, te);
  }

  @Override
  protected void addMachineSlots(InventoryPlayer playerInv) {    
  }
  
  public void setPlayerInventoryVisible(boolean visible) {
    Set<Entry<Slot, Point>> entries = playerSlotLocations.entrySet();    
    for(Entry<Slot, Point> entry : entries) {
      entry.getKey().xDisplayPosition = visible ? entry.getValue().x : -3000;
      entry.getKey().yDisplayPosition = visible ? entry.getValue().y : -3000;
    }
  }

  @Override
  public Point getPlayerInventoryOffset() {
    //return INV_OFFSET;
    return new Point(47, 84);
  }
  
  

}
