package crazypants.enderio.machines.machine.spawner.creative;

import java.awt.Point;

import javax.annotation.Nonnull;

import com.enderio.core.common.ContainerEnderCap;
import com.enderio.core.common.inventory.EnderInventory;
import com.enderio.core.common.inventory.EnderInventory.Type;
import com.enderio.core.common.inventory.EnderSlot;

import net.minecraft.entity.player.InventoryPlayer;

public class ContainerCreativeSpawner extends ContainerEnderCap<EnderInventory, TileCreativeSpawner> {

  public ContainerCreativeSpawner(@Nonnull InventoryPlayer playerInv, @Nonnull TileCreativeSpawner te) {
    super(playerInv, te.getInventory(), te);
  }

  @Override
  protected void addSlots() {
    addSlotToContainer(new EnderSlot(getItemHandler().getView(Type.INPUT), TileCreativeSpawner.SLOT.VIAL, 26, 12));
    addSlotToContainer(new EnderSlot(getItemHandler().getView(Type.INPUT), TileCreativeSpawner.SLOT.PATTERN, 26, 30));
    addSlotToContainer(new EnderSlot(getItemHandler().getView(Type.INPUT), TileCreativeSpawner.SLOT.OFFERING, 26, 48));
  }

  @Override
  public @Nonnull Point getPlayerInventoryOffset() {
    return new Point(8, 84);
  }

}
