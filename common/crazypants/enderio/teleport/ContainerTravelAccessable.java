package crazypants.enderio.teleport;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;

public class ContainerTravelAccessable extends Container {

  private ITravelAccessable ta;

  public ContainerTravelAccessable(InventoryPlayer playerInv, ITravelAccessable te) {
    ta = te;

    // add players inventory
    for (int i = 0; i < 3; ++i) {
      for (int j = 0; j < 9; ++j) {
        addSlotToContainer(new Slot(playerInv, j + i * 9 + 9, 8 + j * 18, 73 + i * 18));
      }
    }

    for (int i = 0; i < 9; ++i) {
      addSlotToContainer(new Slot(playerInv, i, 8 + i * 18, 131));
    }
  }

  @Override
  public boolean canInteractWith(EntityPlayer entityplayer) {
    return entityplayer != null && entityplayer.username != null && entityplayer.username.equals(ta.getPlacedBy());
  }

}
