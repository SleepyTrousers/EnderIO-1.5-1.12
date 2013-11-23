package crazypants.enderio.conduit.gui;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraftforge.common.ForgeDirection;
import crazypants.enderio.conduit.IConduitBundle;

public class ExternalConnectionContainer extends Container {

  private InventoryPlayer playerInv;
  private IConduitBundle bundle;
  private ForgeDirection dir;

  public ExternalConnectionContainer(InventoryPlayer playerInv, IConduitBundle bundle, ForgeDirection dir) {
    this.playerInv = playerInv;
    this.bundle = bundle;
    this.dir = dir;
  }

  @Override
  public boolean canInteractWith(EntityPlayer entityplayer) {
    return true;
  }

}
