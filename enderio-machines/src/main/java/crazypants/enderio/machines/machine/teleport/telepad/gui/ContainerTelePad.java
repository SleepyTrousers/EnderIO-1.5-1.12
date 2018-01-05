package crazypants.enderio.machines.machine.teleport.telepad.gui;

import java.awt.Point;
import java.util.List;

import javax.annotation.Nonnull;

import com.enderio.core.client.gui.widget.GhostBackgroundItemSlot;
import com.enderio.core.client.gui.widget.GhostSlot;
import com.enderio.core.common.ContainerEnderCap;
import com.enderio.core.common.inventory.EnderInventory;
import com.enderio.core.common.inventory.EnderInventory.Type;
import com.enderio.core.common.inventory.EnderSlot;

import crazypants.enderio.machines.machine.teleport.telepad.TileTelePad;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;

import static crazypants.enderio.base.init.ModObject.itemLocationPrintout;

public class ContainerTelePad extends ContainerEnderCap<EnderInventory, TileTelePad> {

  public ContainerTelePad(@Nonnull InventoryPlayer playerInv, @Nonnull TileTelePad te) {
    super(playerInv, te.getInventory(), te);
  }

  @Override
  public @Nonnull Point getPlayerInventoryOffset() {
    return new Point(8, 138);
  }

  @Override
  protected void addSlots() {
    addSlotToContainer(new EnderSlot(Type.INPUT, getItemHandler(), "INPUT", 153, 47));
    addSlotToContainer(new EnderSlot(Type.OUTPUT, getItemHandler(), "OUTPUT", 153, 84));
  }

  public void createGhostSlots(List<GhostSlot> slots) {
    final Slot slot = inventorySlots.get(0);
    if (slot != null) {
      slots.add(new GhostBackgroundItemSlot(itemLocationPrintout.getItemNN(), slot));
    }
  }

}
