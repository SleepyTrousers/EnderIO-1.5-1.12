package crazypants.enderio.powertools.machine.capbank;

import java.awt.Point;

import javax.annotation.Nonnull;

import com.enderio.core.common.ContainerEnderCap;
import com.enderio.core.common.inventory.EnderInventory;

import net.minecraft.entity.player.InventoryPlayer;

public class ContainerCapBank extends ContainerEnderCap<EnderInventory, TileCapBank> {

  public ContainerCapBank(@Nonnull InventoryPlayer playerInv, @Nonnull TileCapBank te) {
    super(playerInv, new EnderInventory(), te);
  }

  @Override
  protected void addSlots() {
  }

  @Override
  public @Nonnull Point getPlayerInventoryOffset() {
    return new Point(8, 84);
  }

}
