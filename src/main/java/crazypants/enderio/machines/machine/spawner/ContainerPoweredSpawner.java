package crazypants.enderio.machines.machine.spawner;

import java.util.List;

import javax.annotation.Nonnull;

import com.enderio.core.client.gui.widget.GhostBackgroundItemSlot;
import com.enderio.core.client.gui.widget.GhostSlot;

import crazypants.enderio.base.init.ModObject;
import crazypants.enderio.base.machine.gui.AbstractMachineContainer;
import crazypants.enderio.machines.machine.tank.InventorySlot;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class ContainerPoweredSpawner extends AbstractMachineContainer<TilePoweredSpawner> implements IPoweredSpawnerRemoteExec.Container {

  private Slot slotInput;
  private Slot slotOutput;

  public ContainerPoweredSpawner(@Nonnull InventoryPlayer playerInv, @Nonnull TilePoweredSpawner te) {
    super(playerInv, te);
  }

  @Override
  protected void addMachineSlots(@Nonnull InventoryPlayer playerInv) {
    slotInput = addSlotToContainer(new InventorySlot(getInv(), 0, 54, 42));
    slotOutput = addSlotToContainer(new Slot(getInv(), 1, 105, 42) {
      @Override
      public boolean isItemValid(@Nonnull ItemStack itemStack) {
        return false;
      }
    });
  }

  public void createGhostSlots(List<GhostSlot> slots) {
    final GhostBackgroundItemSlot ghostBackgroundItemSlot = new GhostBackgroundItemSlot(ModObject.itemSoulVial.getItemNN(), getSlotFromInventory(0));
    ghostBackgroundItemSlot.y = 42;
    slots.add(ghostBackgroundItemSlot);
  }

  public void setSlotVisibility(boolean visible) {
    slotInput.yPos = visible ? 42 : -3000;
    slotOutput.yPos = visible ? 42 : -3000;
  }

  @Override
  public IMessage doSetSpawnMode(boolean isSpawn) {
    getTe().setSpawnMode(isSpawn);
    return null;
  }

}
