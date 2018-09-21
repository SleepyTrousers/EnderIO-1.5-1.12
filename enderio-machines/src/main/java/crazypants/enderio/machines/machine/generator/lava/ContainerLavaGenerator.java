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

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

import static crazypants.enderio.machines.config.config.LavaGenConfig.cobbleEnabled;
import static crazypants.enderio.machines.config.config.LavaGenConfig.obsidianEnabled;
import static crazypants.enderio.machines.config.config.LavaGenConfig.outputEnabled;
import static crazypants.enderio.machines.config.config.LavaGenConfig.stoneEnabled;

public class ContainerLavaGenerator<T extends TileLavaGenerator> extends ContainerEnderCap<EnderInventory, TileLavaGenerator> {

  public ContainerLavaGenerator(@Nonnull InventoryPlayer playerInv, @Nonnull T te) {
    super(playerInv, te.getInventory(), te);
  }

  @Override
  protected void addSlots() {
    addSlotToContainer(new EnderSlot(getItemHandler().getView(Type.UPGRADE), "cap", 12, 60));
    addSlotToContainer(new EnderSlot(Type.OUTPUT, getItemHandler(), TileLavaGenerator.OUTPUT_COB, 134, 54));
    addSlotToContainer(new EnderSlot(Type.OUTPUT, getItemHandler(), TileLavaGenerator.OUTPUT_STO, 134, 54 - 18));
    addSlotToContainer(new EnderSlot(Type.OUTPUT, getItemHandler(), TileLavaGenerator.OUTPUT_OBS, 134, 54 - 18 - 18));
  }

  @Override
  public @Nonnull Point getPlayerInventoryOffset() {
    return new Point(8, 84);
  }

  public void createGhostSlots(List<GhostSlot> slots) {
    Slot slot = inventorySlots.get(1);
    if (slot != null) {
      slots.add(new GhostBackgroundItemSlot(new ItemStack(outputEnabled.get() && cobbleEnabled.get() ? Blocks.COBBLESTONE : Blocks.BARRIER), slot));
    }
    slot = inventorySlots.get(2);
    if (slot != null) {
      slots.add(new GhostBackgroundItemSlot(new ItemStack(outputEnabled.get() && stoneEnabled.get() ? Blocks.STONE : Blocks.BARRIER), slot));
    }
    slot = inventorySlots.get(3);
    if (slot != null) {
      slots.add(new GhostBackgroundItemSlot(new ItemStack(outputEnabled.get() && obsidianEnabled.get() ? Blocks.OBSIDIAN : Blocks.BARRIER), slot));
    }
  }

}
