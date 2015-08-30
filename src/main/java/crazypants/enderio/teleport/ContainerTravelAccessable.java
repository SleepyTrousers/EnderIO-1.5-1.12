package crazypants.enderio.teleport;

import java.awt.Point;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import com.enderio.core.client.gui.widget.TemplateSlot;
import com.enderio.core.common.ContainerEnder;
import com.enderio.core.common.util.ArrayInventory;

import crazypants.enderio.api.teleport.ITravelAccessable;
import crazypants.enderio.teleport.ContainerTravelAccessable.PasswordInventory;

public class ContainerTravelAccessable extends ContainerEnder<PasswordInventory> {

  ITravelAccessable ta;
  TileEntity te;
  World world;

  public ContainerTravelAccessable(InventoryPlayer playerInv, final ITravelAccessable travelAccessable, World world) {
    super(playerInv, new PasswordInventory(travelAccessable, world, true));
    ta = travelAccessable;
    this.world = world;
    if (ta instanceof TileEntity) {
      te = ((TileEntity) ta);
    }

    getInv().te = te;
  }

  @Override
  protected void addSlots(InventoryPlayer playerInv) {
    int x = 44;
    int y = 73;
    for (int i = 0; i < 5; i++) {
      addSlotToContainer(new TemplateSlot(getInv(), i, x, y));
      x += 18;
    }

    ArrayInventory arrInv = new PasswordInventory(getInv().ta, getInv().world, false);
    x = 125;
    y = 10;
    addSlotToContainer(new TemplateSlot(arrInv, 0, x, y));
  }
  
  @Override
  public Point getPlayerInventoryOffset() {
    return new Point(8, 103);
  }

  @Override
  public ItemStack transferStackInSlot(EntityPlayer entityPlayer, int slotIndex) {
    return null;
  }

  static class PasswordInventory extends ArrayInventory {

    boolean isAuth;
    World world;
    TileEntity te;
    ITravelAccessable ta;

    public PasswordInventory(ITravelAccessable ta, World world, boolean isAuth) {
      super(isAuth ? ta.getPassword() : new ItemStack[] { ta.getItemLabel() });
      this.isAuth = isAuth;
      this.world = world;
      if (ta instanceof TileEntity) {
        te = (TileEntity) ta;
      }
      this.ta = ta;
    }

    @Override
    public void markDirty() {
      super.markDirty();
      if (!world.isRemote && te != null) {
        if (isAuth) {
          ((ITravelAccessable) te).clearAuthorisedUsers();
        }
        world.markBlockForUpdate(te.xCoord, te.yCoord, te.zCoord);
      }
    }

    @Override
    public int getSizeInventory() {
      return items.length;
    }

    @Override
    public ItemStack getStackInSlot(int i) {
      if (i < 0 || i >= items.length) {
        return null;
      }
      return items[i];
    }

    @Override
    public ItemStack decrStackSize(int fromSlot, int amount) {
      ItemStack item = items[fromSlot];
      items[fromSlot] = null;
      if (item == null) {
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
      if (itemstack != null) {
        items[i] = itemstack.copy();
        items[i].stackSize = 0;
      } else {
        items[i] = null;
      }
      ((ITravelAccessable) te).setItemLabel(items[i]);
    }

    @Override
    public String getInventoryName() {
      return "Password";
    }

    @Override
    public int getInventoryStackLimit() {
      return 0;
    }

  }

}
