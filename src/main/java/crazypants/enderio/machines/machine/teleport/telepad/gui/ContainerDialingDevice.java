package crazypants.enderio.machines.machine.teleport.telepad.gui;

import java.awt.Point;
import java.util.List;

import javax.annotation.Nonnull;

import com.enderio.core.client.gui.widget.GhostBackgroundItemSlot;
import com.enderio.core.client.gui.widget.GhostSlot;
import com.enderio.core.common.ContainerEnderCap;

import crazypants.enderio.base.item.coordselector.TelepadTarget;
import crazypants.enderio.machines.machine.teleport.telepad.TileDialingDevice;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.SlotItemHandler;

import static crazypants.enderio.base.init.ModObject.itemLocationPrintout;

public class ContainerDialingDevice extends ContainerEnderCap<TileDialingDevice, TileDialingDevice> {

  public ContainerDialingDevice(@Nonnull InventoryPlayer playerInv, @Nonnull TileDialingDevice itemHandler) {
    super(playerInv, itemHandler, itemHandler);
  }

  @Override
  public @Nonnull Point getPlayerInventoryOffset() {
    return new Point(8, 138);
  }

  @Override
  protected void addSlots() {
    int x = 153;
    int y = 47;
    inventorySlots.indexOf(addSlotToContainer(new SlotItemHandler(getItemHandler(), 0, x, y) {
      @Override
      public boolean isItemValid(@Nonnull ItemStack itemStack) {
        return TelepadTarget.readFromNBT(itemStack) != null;
      }
    }));
    y = 84;
    addSlotToContainer(new SlotItemHandler(getItemHandler(), 1, x, y));
  }

  public void createGhostSlots(List<GhostSlot> slots) {
    final Slot slot = inventorySlots.get(0);
    if (slot != null) {
      slots.add(new GhostBackgroundItemSlot(itemLocationPrintout.getItemNN(), slot));
    }
  }

}
