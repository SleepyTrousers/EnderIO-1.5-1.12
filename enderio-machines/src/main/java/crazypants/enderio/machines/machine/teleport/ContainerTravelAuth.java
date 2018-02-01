package crazypants.enderio.machines.machine.teleport;

import java.awt.Point;
import java.util.List;

import javax.annotation.Nonnull;

import com.enderio.core.client.gui.widget.GhostSlot;
import com.enderio.core.common.ContainerEnder;
import com.enderio.core.common.util.ArrayInventory;

import crazypants.enderio.machines.machine.teleport.ContainerTravelAuth.AuthInventory;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;

public class ContainerTravelAuth extends ContainerEnder<AuthInventory> {

  static class AuthInventory extends ArrayInventory {
    private ContainerTravelAuth container;

    private AuthInventory() {
      super(new ItemStack[] { ItemStack.EMPTY, ItemStack.EMPTY, ItemStack.EMPTY, ItemStack.EMPTY, ItemStack.EMPTY });
    }

    private void setContainer(ContainerTravelAuth container) {
      this.container = container;
    }

    @Override
    public void markDirty() {
      super.markDirty();
      container.dirty = true;
    }

    @Override
    public int getSizeInventory() {
      return items.length;
    }

    @Override
    public @Nonnull ItemStack getStackInSlot(int i) {
      if (i < 0 || i >= items.length) {
        return ItemStack.EMPTY;
      }
      return super.getStackInSlot(i);
    }

    @Override
    public @Nonnull ItemStack decrStackSize(int fromSlot, int amount) {
      ItemStack item = super.getStackInSlot(fromSlot);
      items[fromSlot] = ItemStack.EMPTY;
      if (item.isEmpty()) {
        return ItemStack.EMPTY;
      }
      item.setCount(0);
      return item;
    }

    @Override
    public void setInventorySlotContents(int i, @Nonnull ItemStack itemstack) {
      if (!itemstack.isEmpty()) {
        items[i] = itemstack.copy();
        items[i].setCount(1);
      } else {
        items[i] = ItemStack.EMPTY;
      }
    }

    @Override
    public @Nonnull String getName() {
      return "Password";
    }

    @Override
    public int getInventoryStackLimit() {
      return 1;
    }

    public @Nonnull ItemStack[] getInventory() {
      return this.items;
    }
  }

  boolean dirty = false;

  public ContainerTravelAuth(@Nonnull InventoryPlayer playerInv) {
    super(playerInv, new AuthInventory());
    getInv().setContainer(this);
  }

  @Override
  protected void addSlots(@Nonnull InventoryPlayer playerInv) {
  }

  public void addGhostSlots(List<GhostSlot> ghostSlots) {
    int x = 44;
    int y = 28;
    for (int i = 0; i < 5; i++) {
      ghostSlots.add(new AuthGhostSlot(getInv(), i, x, y));
      x += 18;
    }
  }

  @Override
  public @Nonnull Point getPlayerInventoryOffset() {
    Point p = super.getPlayerInventoryOffset();
    p.translate(0, -1);
    return p;
  }

  @Override
  public @Nonnull ItemStack transferStackInSlot(@Nonnull EntityPlayer par1EntityPlayer, int par2) {
    return ItemStack.EMPTY;
  }

  private static class AuthGhostSlot extends GhostSlot {

    private AuthInventory inv;

    public AuthGhostSlot(AuthInventory ta, int slotIndex, int x, int y) {
      this.setSlot(slotIndex);
      this.setX(x);
      this.setY(y);
      this.setDisplayStdOverlay(false);
      this.setGrayOut(true);
      this.setStackSizeLimit(1);
      this.inv = ta;
    }

    @Override
    public @Nonnull ItemStack getStack() {
      ItemStack stack = inv.getStackInSlot(getSlot());
      return stack;
    }

    @Override
    public void putStack(@Nonnull ItemStack stack, int realsize) {
      inv.setInventorySlotContents(getSlot(), stack);
    }

  }

}
