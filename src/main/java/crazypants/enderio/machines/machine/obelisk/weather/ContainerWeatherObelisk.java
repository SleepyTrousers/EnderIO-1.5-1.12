package crazypants.enderio.machines.machine.obelisk.weather;

import java.util.List;

import javax.annotation.Nonnull;

import com.enderio.core.client.gui.widget.GhostBackgroundItemSlot;
import com.enderio.core.client.gui.widget.GhostSlot;

import crazypants.enderio.base.machine.gui.AbstractMachineContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ContainerWeatherObelisk extends AbstractMachineContainer<TileWeatherObelisk> {

  public static final int MAX_SCALE = 31;

  public ContainerWeatherObelisk(@Nonnull InventoryPlayer playerInv, @Nonnull TileWeatherObelisk te) {
    super(playerInv, te);
  }

  private int slotno;

  @Override
  protected void addMachineSlots(@Nonnull InventoryPlayer playerInv) {
    slotno = inventorySlots.indexOf(addSlotToContainer(new Slot(getInv(), 0, 80, 11) {
      @Override
      public boolean isItemValid(@Nonnull ItemStack p_75214_1_) {
        return getInv().isItemValidForSlot(0, p_75214_1_);
      }
    }));
  }

  public void createGhostSlots(List<GhostSlot> slots) {
    slots.add(new GhostBackgroundItemSlot(new ItemStack(Items.FIREWORKS), inventorySlots.get(slotno)));
  }

}
