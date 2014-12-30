package crazypants.enderio.machine.monitor;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.tileentity.TileEntity;

public class ContainerPowerMonitor extends Container {

  public ContainerPowerMonitor() {    
  }
  
  @Override
  public boolean canInteractWith(EntityPlayer arg0) {
    return true;
  }

}
