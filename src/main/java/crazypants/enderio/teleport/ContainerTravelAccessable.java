package crazypants.enderio.teleport;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import crazypants.enderio.api.teleport.ITravelAccessable;
import crazypants.gui.TemplateSlot;
import crazypants.util.ArrayInventory;

public class ContainerTravelAccessable extends Container {

  private ITravelAccessable ta;
  private TileEntity te;
  private World world;

  public ContainerTravelAccessable(InventoryPlayer playerInv, final ITravelAccessable travelAccessable, World world) {
    ta = travelAccessable;
    this.world = world;
    if(ta instanceof TileEntity) {
      te = ((TileEntity) ta);
    }

    ArrayInventory arrInv = new PasswordInventory(ta.getPassword(), true);    
    int x = 44;
    int y = 73;
    for (int i = 0; i < 5; i++) {
      addSlotToContainer(new TemplateSlot(arrInv, i, x, y));
      x += 18;
    }
    
    arrInv = new PasswordInventory(new ItemStack[] {ta.getItemLabel()}, false);    
    x = 125;
    y = 10;    
    addSlotToContainer(new TemplateSlot(arrInv, 0, x, y));
    

    // add players inventory
    for (int i = 0; i < 3; ++i) {
      for (int j = 0; j < 9; ++j) {
        addSlotToContainer(new Slot(playerInv, j + i * 9 + 9, 8 + j * 18, 103 + i * 18));
      }
    }

    for (int i = 0; i < 9; ++i) {
      addSlotToContainer(new Slot(playerInv, i, 8 + i * 18, 161));
    }
  }

  @Override
  public boolean canInteractWith(EntityPlayer entityplayer) {
    //return entityplayer != null && entityplayer.getUniqueID() != null && entityplayer.getUniqueID().toString().equals(ta.getPlacedBy());
    return true;
  }

  @Override
  public ItemStack transferStackInSlot(EntityPlayer entityPlayer, int slotIndex) {
    return null;
  }

  private class PasswordInventory extends ArrayInventory {

    boolean isAuth;
    
    public PasswordInventory(ItemStack[] items, boolean isAuth) {
      super(items);
      this.isAuth = isAuth;
    }
    
    @Override
    public void markDirty() {
      super.markDirty();
      if(!world.isRemote && te != null) {
        if(isAuth) {
          ta.clearAuthorisedUsers();
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
      ta.setItemLabel(items[i]);
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
