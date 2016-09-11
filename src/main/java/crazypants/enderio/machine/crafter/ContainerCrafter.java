package crazypants.enderio.machine.crafter;

import java.awt.Point;
import java.util.List;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

import com.enderio.core.client.gui.widget.GhostSlot;

import crazypants.enderio.machine.gui.AbstractMachineContainer;
import crazypants.enderio.network.PacketHandler;

public class ContainerCrafter extends AbstractMachineContainer<TileCrafter> {

  public ContainerCrafter(InventoryPlayer playerInv, TileCrafter te) {
    super(playerInv, te);
  }

  public void addCrafterSlots(List<GhostSlot> ghostSlots) {
    int topY = 16;
    int leftX = 31;
    int index = 0;

    for (int row = 0; row < 3; ++row) {
      for (int col = 0; col < 3; ++col) {
        int x = leftX + col * 18;
        int y = topY + row * 18;
        ghostSlots.add(new DummySlot(index, x, y));
        index++;
      }
    }

    ghostSlots.add(new DummySlot(9, 90, 34));
  }

  @Override
  public Point getPlayerInventoryOffset() {
    return new Point(30, 84);
  }

  @Override
  public Point getUpgradeOffset() {
    return new Point(6, 60);
  }

  @Override
  protected void addMachineSlots(InventoryPlayer playerInv) {
    int topY = 16;
    int leftX = 113;
    int index = 0;

    for (int row = 0; row < 3; ++row) {
      for (int col = 0; col < 3; ++col) {
        int x = leftX + col * 18;
        int y = topY + row * 18;
        addSlotToContainer(new InputSlot(getInv(), index, x, y));
        index++;
      }
    }
    addSlotToContainer(new Slot(getInv(), 9, 172, 34) {
      @Override
      public boolean isItemValid(ItemStack itemStack) {
        return false;
      }
    });
  }

  private class InputSlot extends Slot {

    public InputSlot(IInventory par1iInventory, int par2, int par3, int par4) {
      super(par1iInventory, par2, par3, par4);
    }

    @Override
    public boolean isItemValid(ItemStack itemStack) {

      ItemStack refStack = getInv().craftingGrid.getStackInSlot(slotNumber);
      if(refStack == null || itemStack == null) {
        return false;
      }
      return TileCrafter.compareDamageable(itemStack, refStack);
    }
  }

  private class DummySlot extends GhostSlot {
    private final int slotIndex;

    public DummySlot(int slotIndex, int x, int y) {
      this.slotIndex = slotIndex;
      this.x = x;
      this.y = y;
    }

    @Override
    public ItemStack getStack() {
      return getInv().craftingGrid.getStackInSlot(slotIndex);
    }

    @Override
    public void putStack(ItemStack stack) {
      if(slotIndex >= 9) {
        return;
      }
      if(stack != null) {
        stack = stack.copy();
        stack.stackSize = 1;
      }
      PacketHandler.INSTANCE.sendToServer(PacketCrafter.setSlot(getInv(), slotIndex, stack));
    }
  }
}
