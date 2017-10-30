/*package crazypants.enderio.machine.crafter;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.client.gui.GhostSlotHandler;
import com.enderio.core.client.gui.widget.GhostSlot;
import com.enderio.core.common.inventory.EnderInventory.Type;

import crazypants.enderio.machine.base.container.AbstractCapabilityMachineContainer;
import crazypants.enderio.machine.base.container.SlotRangeHelper;
import crazypants.enderio.network.GuiPacket;
import crazypants.enderio.network.IRemoteExec;
import crazypants.enderio.network.PacketHandler;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

public class ContainerCrafter extends AbstractCapabilityMachineContainer<TileCrafter> implements IRemoteExec.IContainer {

  public static final int EXEC_SET_BUFFER = 0;

  public ContainerCrafter(@Nonnull InventoryPlayer playerInv, @Nonnull TileCrafter te, EnumFacing facing) {
    super(playerInv, te, facing);
  }

  private final List<DummySlot> dummySlots = new ArrayList<DummySlot>();

  public void addCrafterSlots(GhostSlotHandler ghostSlots) {
    dummySlots.clear();
    int topY = 16;
    int leftX = 31;
    int index = 0;

    for (int row = 0; row < 3; ++row) {
      for (int col = 0; col < 3; ++col) {
        int x = leftX + col * 18;
        int y = topY + row * 18;
        final DummySlot dummySlot = new DummySlot(index, x, y);
        ghostSlots.addGhostSlot(dummySlot);
        dummySlots.add(dummySlot);
        index++;
      }
    }

    ghostSlots.addGhostSlot(new DummySlot(9, 90, 34));
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
        addSlotToContainer(new InputSlot(getOwner().getInventory().getView(Type.INPUT), index, x, y));
        index++;
      }
    }
    addSlotToContainer(new SlotItemHandler(getOwner().getInventory().getView(Type.OUTPUT), 0, 172, 34) {
      @Override
      public boolean isItemValid(@Nullable ItemStack itemStack) {
        return false;
      }
    });
  }

  private class InputSlot extends SlotItemHandler {

    public InputSlot(IItemHandler par1iInventory, int par2, int par3, int par4) {
      super(par1iInventory, par2, par3, par4);
    }

    @Override
    public boolean isItemValid(ItemStack itemStack) {

      ItemStack refStack = getOwner().craftingGrid.getStackInSlot(slotNumber);
      if (refStack.isEmpty() || itemStack.isEmpty()) {
        return false;
      }
      return TileCrafter.compareDamageable(itemStack, refStack);
    }
  }

  public List<DummySlot> getDummySlots() {
    return dummySlots;
  }

  public class DummySlot extends GhostSlot {
    private final int slotIndex;

    public DummySlot(int slotIndex, int x, int y) {
      this.slotIndex = slotIndex;
      this.x = x;
      this.y = y;
    }

    @Override
    public ItemStack getStack() {
      return getOwner().craftingGrid.getStackInSlot(slotIndex);
    }

    @Override
    public void putStack(ItemStack stack) {
      if (slotIndex >= 9) {
        return;
      }
      if (!stack.isEmpty()) {
        stack = stack.copy();
        stack.setCount(1);
      }
      PacketHandler.INSTANCE.sendToServer(PacketCrafter.setSlot(getOwner(), slotIndex, stack));
    }
  }

  @Override
  public SlotRangeHelper.SlotRange getPlayerInventorySlotRange(boolean reverse) {
    return super.getPlayerInventorySlotRange(reverse);
  }

  @Override
  public IMessage networkExec(int id, GuiPacket message) {
    switch (id) {
    case EXEC_SET_BUFFER:
      getOwner().setBufferStacks(message.getBoolean(0));
      break;
    default:
      break;
    }
    return null;
  }

}
*/