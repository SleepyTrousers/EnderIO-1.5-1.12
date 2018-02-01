package crazypants.enderio.machine.invpanel.sensor;

import java.util.List;

import javax.annotation.Nonnull;

import com.enderio.core.client.gui.widget.GhostSlot;

import crazypants.enderio.base.machine.gui.AbstractMachineContainer;
import crazypants.enderio.base.network.PacketHandler;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;

public class ContainerSensor extends AbstractMachineContainer<TileInventoryPanelSensor> {

  public ContainerSensor(InventoryPlayer playerInv, TileInventoryPanelSensor te) {
    super(playerInv, te);
  }

  @Override
  protected void addMachineSlots(@Nonnull InventoryPlayer playerInv) {
  }

  public void addGhostSlots(List<GhostSlot> ghostSlots) {
    ghostSlots.add(new DummySlot(0, 80, 9));
  }

  public class DummySlot extends GhostSlot {

    public DummySlot(int slotIndex, int x, int y) {
      this.setSlot(slotIndex);
      this.setX(x);
      this.setY(y);
    }

    @Override
    public @Nonnull ItemStack getStack() {
      return getInv().getItemToCheck();
    }

    @Override
    public void putStack(@Nonnull ItemStack stack, int realsize) {
      stack = stack.copy();
      stack.setCount(1);
      getInv().setItemToCheck(stack);
      PacketHandler.INSTANCE.sendToServer(new PacketItemToCheck(getInv()));
    }
  }
}
