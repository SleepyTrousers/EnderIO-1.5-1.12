package crazypants.enderio.machines.machine.generator.lava;

import java.awt.Point;

import javax.annotation.Nonnull;

import com.enderio.core.common.ContainerEnderCap;
import com.enderio.core.common.inventory.EnderInventory;
import com.enderio.core.common.inventory.EnderInventory.Type;
import com.enderio.core.common.inventory.EnderSlot;

import net.minecraft.entity.player.InventoryPlayer;

public class ContainerLavaGenerator<T extends TileLavaGenerator> extends ContainerEnderCap<EnderInventory, TileLavaGenerator> {

  public ContainerLavaGenerator(@Nonnull InventoryPlayer playerInv, @Nonnull T te) {
    super(playerInv, te.getInventory(), te);
  }

  @Override
  protected void addSlots() {
    addSlotToContainer(new EnderSlot(getItemHandler().getView(Type.UPGRADE), "cap", 12, 60));
  }

  @Override
  public @Nonnull Point getPlayerInventoryOffset() {
    return new Point(8, 84);
  }

}
