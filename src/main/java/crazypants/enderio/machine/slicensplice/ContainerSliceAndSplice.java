package crazypants.enderio.machine.slicensplice;

import java.awt.Point;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import crazypants.enderio.machine.AbstractMachineContainer;
import crazypants.enderio.machine.AbstractMachineEntity;

public class ContainerSliceAndSplice extends AbstractMachineContainer {

  private static final Point[] inputLocs = new Point[] {      
      new Point(44,40),
      new Point(62,40),
      new Point(80,40),
      new Point(44,58),
      new Point(62,58),
      new Point(80,58),
      new Point(54,16),
      new Point(72,16)
  };
  
  public ContainerSliceAndSplice(InventoryPlayer playerInv, AbstractMachineEntity te) {
    super(playerInv, te);
  }

  @Override
  protected void addMachineSlots(InventoryPlayer playerInv) { 
    
    for(int i=0;i<inputLocs.length;i++) {
      Point p = inputLocs[i];
      final int slot = i; 
      addSlotToContainer(new Slot(tileEntity, i, p.x, p.y) {
        @Override
        public boolean isItemValid(ItemStack itemStack) {
          return tileEntity.isItemValidForSlot(slot, itemStack);
        }
      });
    }
    
    
    addSlotToContainer(new Slot(tileEntity, 8, 134, 48) {
      @Override
      public boolean isItemValid(ItemStack par1ItemStack) {
        return false;
      }
    });
    
  }

}
