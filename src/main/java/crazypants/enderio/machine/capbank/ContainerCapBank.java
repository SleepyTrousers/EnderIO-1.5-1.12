package crazypants.enderio.machine.capbank;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;

public class ContainerCapBank extends Container {

  public ContainerCapBank(Entity player, InventoryPlayer inventory, TileCapBank cb) {
  }

  @Override
  public boolean canInteractWith(EntityPlayer p_75145_1_) {
    return true;
  }

}
