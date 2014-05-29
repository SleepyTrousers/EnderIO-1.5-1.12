package crazypants.enderio.machine.crafter;

import java.awt.Point;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import crazypants.enderio.machine.AbstractMachineContainer;
import crazypants.enderio.machine.AbstractMachineEntity;
import crazypants.gui.TemplateSlot;

public class ContainerCrafter extends AbstractMachineContainer {

  private TileCrafter crafter;
  private DummyCraftingGrid craftingGrid;

  public ContainerCrafter(InventoryPlayer playerInv, TileCrafter te) {
    super(playerInv, te);
    addCrafterSlots();
  }

  private void addCrafterSlots() {
    crafter = (TileCrafter) tileEntity;
    craftingGrid = crafter.craftingGrid;

    int topY = 16;
    int leftX = 31;
    int index = 0;

    for (int row = 0; row < 3; ++row) {
      for (int col = 0; col < 3; ++col) {
        int x = leftX + col * 18;
        int y = topY + row * 18;
        addSlotToContainer(new DummySlot(craftingGrid, index, x, y));
        index++;
      }
    }
    addSlotToContainer(new DummySlot(craftingGrid, 9, 90, 34));

  }

  @Override
  protected Point getPlayerInventoryOffset() {
    return new Point(30, 84);
  }

  protected Point getUpgradeOffset() {
    return new Point(6, 60);
  }

  @Override
  protected void addMachineSlots(InventoryPlayer playerInv) {

    int topY = 16;
    int leftX = 31;
    int index = 0;

    leftX = 113;
    index = 0;
    for (int row = 0; row < 3; ++row) {
      for (int col = 0; col < 3; ++col) {
        int x = leftX + col * 18;
        int y = topY + row * 18;
        addSlotToContainer(new InputSlot(tileEntity, index, x, y));
        index++;
      }
    }
    addSlotToContainer(new Slot(tileEntity, 9, 172, 34) {
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

      ItemStack refStack = crafter.craftingGrid.getStackInSlot(slotNumber);
      if(refStack == null || itemStack == null) {
        return false;
      }
      return refStack.isItemEqual(itemStack);
    }

  }

  private class DummySlot extends TemplateSlot {

    public DummySlot(IInventory inventory, int slotIndex, int x, int y) {
      super(inventory, slotIndex, x, y);
    }

    @Override
    public boolean isItemValid(ItemStack itemStack) {
      return true;
    }

    @Override
    public void putStack(ItemStack par1ItemStack) {
      if(par1ItemStack != null) {
        par1ItemStack.stackSize = 0;
      }
      inventory.setInventorySlotContents(slotIndex, par1ItemStack);
      onSlotChanged();
    }

    @Override
    public void onSlotChanged() {
      super.onSlotChanged();
      crafter.updateCraftingOutput();    
    }

  }

}
