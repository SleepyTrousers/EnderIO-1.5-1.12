package crazypants.enderio.machines.machine.teleport;

import java.awt.Point;
import java.util.List;

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
    
    private AuthInventory(ItemStack[] inv) {
      super(inv);
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
    public ItemStack getStackInSlot(int i) {
      if(i < 0 || i >= items.length) {
        return ItemStack.EMPTY;
      }
      return super.getStackInSlot(i);
    }

    @Override
    public ItemStack decrStackSize(int fromSlot, int amount) {
      ItemStack item = super.getStackInSlot(fromSlot);
      items[fromSlot] = ItemStack.EMPTY;
      if(item.isEmpty()) {
        return ItemStack.EMPTY;
      }
      item.setCount(0);
      return item;
    }

    @Override
    public void setInventorySlotContents(int i, ItemStack itemstack) {
      if(!itemstack.isEmpty()) {
        items[i] = itemstack.copy();
        items[i].setCount(0);
      } else {
        items[i] = ItemStack.EMPTY;
      }
    }

    @Override
    public String getName() {
      return "Password";
    }

    @Override
    public int getInventoryStackLimit() {
      return 0;
    }

    public ItemStack[] getInventory() {
      return this.items;
    }
  }

  boolean dirty = false;

  public ContainerTravelAuth(InventoryPlayer playerInv) {
    super(playerInv, new AuthInventory(new ItemStack[5]));
    getInv().setContainer(this);
  }

  @Override
  protected void addSlots(InventoryPlayer playerInv) {
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
  public Point getPlayerInventoryOffset() {
    Point p = super.getPlayerInventoryOffset();
    p.translate(0, -1);
    return p;
  }

  @Override
  public ItemStack transferStackInSlot(EntityPlayer par1EntityPlayer, int par2) {
    return ItemStack.EMPTY;
  }

  private static class AuthGhostSlot extends GhostSlot {

    private AuthInventory inv;

    public AuthGhostSlot(AuthInventory ta, int slotIndex, int x, int y) {
      this.slot = slotIndex;
      this.x = x;
      this.y = y;
      this.displayStdOverlay = false;
      this.grayOut = true;
      this.stackSizeLimit = 1;
      this.inv = ta;
    }

    @Override
    public ItemStack getStack() {
      ItemStack stack = inv.getStackInSlot(slot);
      return stack;
    }

    @Override
    public void putStack(ItemStack stack) {
      inv.setInventorySlotContents(slot, stack);
    }

  }

}
