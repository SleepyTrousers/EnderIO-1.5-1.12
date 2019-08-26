package crazypants.enderio.machines.machine.crafter;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.client.gui.GhostSlotHandler;
import com.enderio.core.client.gui.widget.GhostSlot;
import com.enderio.core.common.ContainerEnderCap;
import com.enderio.core.common.inventory.EnderInventory;
import com.enderio.core.common.inventory.EnderInventory.Type;
import com.enderio.core.common.inventory.EnderSlot;

import crazypants.enderio.base.network.GuiPacket;
import crazypants.enderio.base.network.IRemoteExec;
import crazypants.enderio.base.network.PacketHandler;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class ContainerCrafter<T extends TileCrafter> extends ContainerEnderCap<EnderInventory, TileCrafter> implements IRemoteExec.IContainer {

  public static class Normal extends ContainerCrafter<TileCrafter> {
    public Normal(@Nonnull InventoryPlayer playerInv, @Nonnull TileCrafter te) {
      super(playerInv, te);
    }

    @Override
    protected void addSlots() {
      super.addSlots();
      addSlotToContainer(new EnderSlot(getItemHandler().getView(Type.UPGRADE), "cap", 6, 60));
    }

  }

  public static class Simple extends ContainerCrafter<TileCrafter.Simple> {
    public Simple(@Nonnull InventoryPlayer playerInv, @Nonnull TileCrafter.Simple te) {
      super(playerInv, te);
    }
  }

  @SuppressWarnings("unchecked")
  @Nonnull
  public static <E extends TileCrafter> ContainerCrafter<E> create(@Nonnull InventoryPlayer playerInv, @Nonnull E te) {
    if (te instanceof TileCrafter.Simple) {
      return (ContainerCrafter<E>) new Simple(playerInv, (TileCrafter.Simple) te);
    } else {
      return (ContainerCrafter<E>) new Normal(playerInv, te);
    }
  }

  public static final int EXEC_SET_BUFFER = 0;

  private final List<DummySlot> dummySlots = new ArrayList<DummySlot>();

  public ContainerCrafter(@Nonnull InventoryPlayer playerInv, @Nonnull T te) {
    super(playerInv, te.getInventory(), te);
  }

  @Override
  protected void addSlots() {
    int topY = 16;
    int leftX = 113;
    int index = 0;

    for (int row = 0; row < 3; ++row) {
      for (int col = 0; col < 3; ++col) {
        int x = leftX + col * 18;
        int y = topY + row * 18;
        addSlotToContainer(new EnderSlot(getTileEntityNN().getInventory().getView(Type.INPUT), "INPUT" + index, x, y));
        index++;
      }
    }
    addSlotToContainer(new EnderSlot(getTileEntityNN().getInventory().getView(Type.OUTPUT), "OUTPUT", 172, 34) {
      @Override
      public boolean isItemValid(@Nullable ItemStack itemStack) {
        return false;
      }
    });
  }

  public void addCrafterSlots(@Nonnull GhostSlotHandler ghostSlots) {
    dummySlots.clear();
    int topY = 16;
    int leftX = 31;
    int index = 0;

    for (int row = 0; row < 3; ++row) {
      for (int col = 0; col < 3; ++col) {
        int x = leftX + col * 18;
        int y = topY + row * 18;
        final DummySlot dummySlot = new DummySlot(index, x, y);
        ghostSlots.getGhostSlots().add(dummySlot);
        dummySlots.add(dummySlot);
        index++;
      }
    }

    ghostSlots.getGhostSlots().add(new DummySlot(9, 90, 34));
  }

  @Override
  @Nonnull
  public Point getPlayerInventoryOffset() {
    return new Point(30, 84);
  }

  public List<? extends GhostSlot> getDummySlots() {
    return dummySlots;
  }

  @Override
  public IMessage networkExec(int id, @Nonnull GuiPacket message) {
    switch (id) {
    case EXEC_SET_BUFFER:
      getTileEntityNN().setBufferStacks(message.getBoolean(0));
      break;
    default:
      break;
    }
    return null;
  }

  private int guiId = -1;

  @Override
  public void setGuiID(int id) {
    guiId = id;
  }

  @Override
  public int getGuiID() {
    return guiId;
  }

  public class DummySlot extends GhostSlot {
    final int slotIndex;

    public DummySlot(int slotIndex, int x, int y) {
      this.slotIndex = slotIndex;
      this.setX(x);
      this.setY(y);
    }

    @Override
    @Nonnull
    public ItemStack getStack() {
      return getTileEntityNN().craftingGrid.getStackInSlot(slotIndex);
    }

    @Override
    public void putStack(@Nonnull ItemStack stack, int realsize) {
      if (slotIndex >= 9) {
        return;
      }
      if (!stack.isEmpty()) {
        stack = stack.copy();
        stack.setCount(1);
      }
      PacketHandler.INSTANCE.sendToServer(PacketCrafter.setSlot(getTileEntityNN(), slotIndex, stack));
    }
  }

}
