package crazypants.enderio.machine.monitor;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;

public class ContainerPowerMonitor extends Container {

  public ContainerPowerMonitor() {    
  }
  
  @Override
  public boolean canInteractWith(EntityPlayer arg0) {
    return true;
  }

}
