package crazypants.enderio.machines.machine.mapomatic;

import java.awt.Point;

import javax.annotation.Nonnull;

import com.enderio.core.common.ContainerEnderCap;
import com.enderio.core.common.inventory.EnderInventory;
import com.enderio.core.common.inventory.EnderInventory.Type;
import com.enderio.core.common.inventory.EnderSlot;

import net.minecraft.entity.player.InventoryPlayer;

public class ContainerMapOMatic extends ContainerEnderCap<EnderInventory, TileMapOMatic> {

  public ContainerMapOMatic(@Nonnull InventoryPlayer playerInv, @Nonnull TileMapOMatic machine) {
    super(playerInv, machine.getInventory(), machine);
  }

  @Override
  protected void addSlots() {
    addSlotToContainer(new EnderSlot(getItemHandler().getView(Type.INPUT), "INPUT", 44, 17));
    addSlotToContainer(new EnderSlot(getItemHandler().getView(Type.INPUT), "PAPER", 62, 17));
    addSlotToContainer(new EnderSlot(getItemHandler().getView(Type.OUTPUT), "OUTPUT", 80, 17));
  }

  @Override
  @Nonnull
  public Point getPlayerInventoryOffset() {
    return new Point(8, 84);
  }
}
