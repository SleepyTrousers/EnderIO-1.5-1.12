package crazypants.enderio.teleport.telepad;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;

public class ContainerTelePad extends Container {
  
  InventoryPlayer playerInv;

  public ContainerTelePad(InventoryPlayer playerInv) {
    
    this.playerInv = playerInv;
    
    // add players inventory
    for (int i = 0; i < 3; ++i) {
      for (int j = 0; j < 9; ++j) {
        addSlotToContainer(new Slot(playerInv, j + i * 9 + 9, 8 + j * 18, 118 + i * 18));
      }
    }

    for (int i = 0; i < 9; ++i) {
      addSlotToContainer(new Slot(playerInv, i, 8 + i * 18, 176));
    }
  }
  
  @Override
  public boolean canInteractWith(EntityPlayer p_75145_1_) {
    return true;
  }
}
