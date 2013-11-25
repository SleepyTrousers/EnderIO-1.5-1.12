package crazypants.enderio.conduit.gui;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.ForgeDirection;
import crazypants.enderio.conduit.IConduitBundle;
import crazypants.enderio.conduit.item.IItemConduit;
import crazypants.enderio.conduit.item.ItemFilter;
import crazypants.gui.TemplateSlot;

public class ExternalConnectionContainer extends Container {

  private InventoryPlayer playerInv;
  private IConduitBundle bundle;
  private ForgeDirection dir;
  private IItemConduit itemConduit;
  private ItemFilter filter;

  private List<Point> points = new ArrayList<Point>();

  public ExternalConnectionContainer(InventoryPlayer playerInv, IConduitBundle bundle, ForgeDirection dir) {
    this.playerInv = playerInv;
    this.bundle = bundle;
    this.dir = dir;

    itemConduit = bundle.getConduit(IItemConduit.class);
    //    int left = 5;
    //    int top = 80;
    //    if(itemConduit != null) {
    //      filter = itemConduit.getInputFilter(dir);
    //      for (int i = 0; i < filter.getSizeInventory(); i++) {
    //        addSlotToContainer(new TemplateSlot(filter, i, left + (i * 20), top));
    //        points.add(new Point(left + (i * 20), top));
    //      }
    //    }

    int topY = 69;
    if(itemConduit != null) {
      filter = itemConduit.getInputFilter(dir);
      int leftX = 16;
      int index = 0;
      for (int row = 0; row < 2; ++row) {
        for (int col = 0; col < 5; ++col) {
          int x = leftX + col * 18;
          int y = topY + row * 18;
          addSlotToContainer(new TemplateSlot(filter, index, x, y));
          points.add(new Point(x, y));
          index++;
        }
      }
    }

    topY = 113;
    // add players inventory
    for (int i = 0; i < 3; ++i) {
      for (int j = 0; j < 9; ++j) {
        int x = 8 + j * 18;
        int y = topY + i * 18;
        addSlotToContainer(new Slot(playerInv, j + i * 9 + 9, x, y));
        points.add(new Point(x, y));
      }
    }

    int y = 171;
    for (int i = 0; i < 9; ++i) {
      int x = 8 + i * 18;
      addSlotToContainer(new Slot(playerInv, i, x, y));
      points.add(new Point(x, y));
    }

  }

  public void setSlotsVisible(boolean visible) {
    if(visible) {
      int i = 0;
      for (Object o : inventorySlots) {
        Slot s = (Slot) o;
        s.xDisplayPosition = points.get(i).x;
        s.yDisplayPosition = points.get(i).y;
        i++;
      }
    } else {
      for (Object o : inventorySlots) {
        Slot s = (Slot) o;
        s.xDisplayPosition = -3000;
        s.yDisplayPosition = -3000;
      }
    }
  }

  @Override
  public boolean canInteractWith(EntityPlayer entityplayer) {
    return true;
  }

  @Override
  public ItemStack slotClick(int par1, int par2, int par3, EntityPlayer par4EntityPlayer) {
    if(par4EntityPlayer.worldObj != null) {
      itemConduit.setInputFilter(dir, filter);
      if(par4EntityPlayer.worldObj.isRemote) {
        par4EntityPlayer.worldObj.markBlockForUpdate(bundle.getEntity().xCoord, bundle.getEntity().xCoord, bundle.getEntity().xCoord);
      }

    }
    return super.slotClick(par1, par2, par3, par4EntityPlayer);
  }

  @Override
  public ItemStack transferStackInSlot(EntityPlayer entityPlayer, int slotIndex) {
    return null;
  }

}
