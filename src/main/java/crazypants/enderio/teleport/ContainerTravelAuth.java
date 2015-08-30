package crazypants.enderio.teleport;

import java.awt.Point;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;

import com.enderio.core.client.gui.widget.TemplateSlot;
import com.enderio.core.common.ContainerEnder;
import com.enderio.core.common.util.ArrayInventory;

import crazypants.enderio.teleport.ContainerTravelAuth.AuthInventory;

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
        return null;
      }
      return items[i];
    }

    @Override
    public ItemStack decrStackSize(int fromSlot, int amount) {
      ItemStack item = items[fromSlot];
      items[fromSlot] = null;
      if(item == null) {
        return null;
      }
      item.stackSize = 0;
      return item;
    }

    @Override
    public ItemStack getStackInSlotOnClosing(int i) {
      return null;
    }

    @Override
    public void setInventorySlotContents(int i, ItemStack itemstack) {
      if(itemstack != null) {
        items[i] = itemstack.copy();
        items[i].stackSize = 0;
      } else {
        items[i] = null;
      }
    }

    @Override
    public String getInventoryName() {
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
    int x = 44;
    int y = 28;
    for (int i = 0; i < 5; i++) {
      addSlotToContainer(new TemplateSlot(getInv(), i, x, y));
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
    return null;
  }

}
