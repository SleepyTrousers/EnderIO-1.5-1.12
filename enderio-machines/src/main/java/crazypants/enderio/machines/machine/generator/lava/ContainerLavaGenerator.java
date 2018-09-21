package crazypants.enderio.machines.machine.generator.lava;

import java.awt.Point;
import java.util.List;

import javax.annotation.Nonnull;

import com.enderio.core.client.gui.widget.GhostBackgroundItemSlot;
import com.enderio.core.client.gui.widget.GhostSlot;
import com.enderio.core.common.ContainerEnderCap;
import com.enderio.core.common.inventory.EnderInventory;
import com.enderio.core.common.inventory.EnderInventory.Type;
import com.enderio.core.common.inventory.EnderSlot;

import crazypants.enderio.machines.config.config.LavaGenConfig;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ContainerLavaGenerator<T extends TileLavaGenerator> extends ContainerEnderCap<EnderInventory, TileLavaGenerator> {

  public ContainerLavaGenerator(@Nonnull InventoryPlayer playerInv, @Nonnull T te) {
    super(playerInv, te.getInventory(), te);
  }

  @Override
  protected void addSlots() {
    addSlotToContainer(new EnderSlot(getItemHandler().getView(Type.UPGRADE), "cap", 12, 60));
    addSlotToContainer(new EnderSlot(Type.OUTPUT, getItemHandler(), "OUTPUT", 134, 52));
  }

  @Override
  public @Nonnull Point getPlayerInventoryOffset() {
    return new Point(8, 84);
  }

  public void createGhostSlots(List<GhostSlot> slots) {
    final Slot slot = inventorySlots.get(1);
    if (slot != null) {
      slots.add(new GhostBackgroundItemSlot(new ItemStack(LavaGenConfig.cobbleEnabled.get() ? Blocks.COBBLESTONE : Blocks.BARRIER), slot));
    }
  }

}
