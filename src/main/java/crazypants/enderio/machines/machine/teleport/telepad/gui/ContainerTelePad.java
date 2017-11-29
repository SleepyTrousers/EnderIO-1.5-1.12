package crazypants.enderio.machines.machine.teleport.telepad.gui;

import static crazypants.enderio.init.ModObject.itemLocationPrintout;

import java.awt.Point;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.client.gui.widget.GhostBackgroundItemSlot;
import com.enderio.core.client.gui.widget.GhostSlot;
import com.enderio.core.common.ContainerEnderCap;

import crazypants.enderio.item.coordselector.TelepadTarget;
import crazypants.enderio.machines.machine.teleport.telepad.TileTelePad;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.SlotItemHandler;

public class ContainerTelePad extends ContainerEnderCap<TileTelePad, TileTelePad> {

  public ContainerTelePad(@Nonnull InventoryPlayer playerInv, @Nonnull TileTelePad itemHandler) {
    super(playerInv, itemHandler, itemHandler);
  }

  @Override
  public Point getPlayerInventoryOffset() {
    return new Point(8, 138);
  }

  @Override
  protected void addSlots() {
    int x = 153;
    int y = 47;
    inventorySlots.indexOf(addSlotToContainer(new SlotItemHandler(getItemHandler(), 0, x, y) {
      @Override
      public boolean isItemValid(@Nullable ItemStack itemStack) {
        return TelepadTarget.readFromNBT(itemStack) != null;
      }
    }));
    y = 84;
    addSlotToContainer(new SlotItemHandler(getItemHandler(), 1, x, y));
  }

  public void createGhostSlots(List<GhostSlot> slots) {
    slots.add(new GhostBackgroundItemSlot(itemLocationPrintout.getItem(), inventorySlots.get(0)));
  }

}
