package crazypants.enderio.machine.invpanel.sensor;

import com.enderio.core.client.gui.widget.GhostSlot;

import crazypants.enderio.base.machine.gui.AbstractMachineContainer;
import crazypants.enderio.base.network.PacketHandler;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;

import java.util.List;

public class ContainerSensor extends AbstractMachineContainer<TileInventoryPanelSensor> {

  public ContainerSensor(InventoryPlayer playerInv, TileInventoryPanelSensor te) {
    super(playerInv, te);
  }

  @Override
  protected void addMachineSlots(InventoryPlayer playerInv) {
  }

  public void addGhostSlots(List<GhostSlot> ghostSlots) {
    ghostSlots.add(new DummySlot(0, 80, 9));
  }
  
  public class DummySlot extends GhostSlot {

    public DummySlot(int slotIndex, int x, int y) {
      this.slot = slotIndex;
      this.x = x;
      this.y = y;
    }

    @Override
    public ItemStack getStack() {
      return getInv().getItemToCheck();
    }

    @Override
    public void putStack(ItemStack stack) {
      if (stack != null) {
        stack = stack.copy();
        stack.setCount(1);
      }
      getInv().setItemToCheck(stack);
      PacketHandler.INSTANCE.sendToServer(new PacketItemToCheck(getInv()));
    }
  }
}
