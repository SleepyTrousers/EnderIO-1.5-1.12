package crazypants.enderio.machines.machine.basin;

import javax.annotation.Nonnull;

import com.enderio.core.common.inventory.EnderInventory.Type;
import com.enderio.core.common.inventory.EnderSlot;

import crazypants.enderio.base.machine.gui.AbstractCapabilityMachineContainer;
import net.minecraft.entity.player.InventoryPlayer;

public class ContainerBasin extends AbstractCapabilityMachineContainer<TileBasin> {

  public ContainerBasin(@Nonnull InventoryPlayer playerInv, @Nonnull TileBasin te) {
    super(playerInv, te);
  }

  @Override
  protected void addMachineSlots() {
    addSlotToContainer(new EnderSlot(Type.OUTPUT, getItemHandler(), "OUTPUT", 50, 50));
  }

}
