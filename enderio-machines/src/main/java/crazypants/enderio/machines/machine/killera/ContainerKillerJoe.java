package crazypants.enderio.machines.machine.killera;

import java.util.List;

import javax.annotation.Nonnull;

import com.enderio.core.client.gui.widget.GhostBackgroundItemSlot;
import com.enderio.core.client.gui.widget.GhostSlot;

import crazypants.enderio.base.machine.gui.AbstractMachineContainer;
import crazypants.enderio.machines.machine.tank.InventorySlot;
import net.minecraft.entity.player.InventoryPlayer;

public class ContainerKillerJoe extends AbstractMachineContainer<TileKillerJoe> {

  public ContainerKillerJoe(@Nonnull InventoryPlayer playerInv, @Nonnull TileKillerJoe te) {
    super(playerInv, te);
  }

  @Override
  protected void addMachineSlots(@Nonnull InventoryPlayer playerInv) {
    addSlotToContainer(new InventorySlot(getInv(), 0, 48, 24));
  }

  public void createGhostSlots(List<GhostSlot> slots) {
    slots.add(new GhostBackgroundItemSlot(TileKillerJoe.WEAPONS.getItemStacks(), getSlotFromInventory(0)));
  }

}
