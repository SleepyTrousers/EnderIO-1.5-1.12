package crazypants.enderio.teleport.telepad;

import java.awt.Point;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IInventory;

import com.enderio.core.common.ContainerEnder;

public class ContainerTelePad extends ContainerEnder<IInventory> {
  
  public ContainerTelePad(InventoryPlayer playerInv) {
    super(playerInv, null);
  }
  
  @Override
  public Point getPlayerInventoryOffset() {
    Point p = super.getPlayerInventoryOffset();
    p.translate(0, 34);
    return p;
  }
  
  @Override
  public boolean canInteractWith(EntityPlayer p_75145_1_) {
    return true;
  }
}
