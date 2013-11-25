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
  private ItemFilter inputFilter;
  private ItemFilter outputFilter;

  private List<Point> slotLocations = new ArrayList<Point>();

  public ExternalConnectionContainer(InventoryPlayer playerInv, IConduitBundle bundle, ForgeDirection dir) {
    this.playerInv = playerInv;
    this.bundle = bundle;
    this.dir = dir;

    itemConduit = bundle.getConduit(IItemConduit.class);

    int topY = 62;
    if(itemConduit != null) {
      inputFilter = itemConduit.getInputFilter(dir);
      int leftX = 16;
      int index = 0;
      for (int row = 0; row < 2; ++row) {
        for (int col = 0; col < 5; ++col) {
          int x = leftX + col * 18;
          int y = topY + row * 18;
          addSlotToContainer(new TemplateSlot(inputFilter, index, x, y));
          slotLocations.add(new Point(x, y));
          index++;
        }
      }

      outputFilter = itemConduit.getOutputFilter(dir);
      leftX = 16;
      index = 0;
      for (int row = 0; row < 2; ++row) {
        for (int col = 0; col < 5; ++col) {
          int x = leftX + col * 18;
          int y = topY + row * 18;
          addSlotToContainer(new TemplateSlot(outputFilter, index, x, y));
          slotLocations.add(new Point(x, y));
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
        slotLocations.add(new Point(x, y));
      }
    }

    int y = 171;
    for (int i = 0; i < 9; ++i) {
      int x = 8 + i * 18;
      addSlotToContainer(new Slot(playerInv, i, x, y));
      slotLocations.add(new Point(x, y));
    }

  }

  public void setInputSlotsVisible(boolean visible) {
    int startIndex = 0;
    int endIndex = 10;
    setSlotsVisible(visible, startIndex, endIndex);
  }

  public void setOutputSlotsVisible(boolean visible) {
    int startIndex = 10;
    int endIndex = 20;
    setSlotsVisible(visible, startIndex, endIndex);
  }

  public void setInventorySlotsVisible(boolean visible) {
    int startIndex = 20;
    int endIndex = inventorySlots.size();
    setSlotsVisible(visible, startIndex, endIndex);
  }

  private void setSlotsVisible(boolean visible, int startIndex, int endIndex) {
    for (int i = startIndex; i < endIndex; i++) {
      Slot s = (Slot) inventorySlots.get(i);
      if(visible) {
        s.xDisplayPosition = slotLocations.get(i).x;
        s.yDisplayPosition = slotLocations.get(i).y;
      } else {
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
      if(par1 < 20) {
        System.out.println("ExternalConnectionContainer.slotClick: Setting filters");
        itemConduit.setInputFilter(dir, inputFilter);
        itemConduit.setOutputFilter(dir, outputFilter);
      }
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
