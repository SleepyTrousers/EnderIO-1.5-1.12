package crazypants.enderio.machine.cobbleworks;

import java.awt.Point;
import java.util.List;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import crazypants.enderio.machine.gui.AbstractMachineContainer;
import crazypants.enderio.network.PacketHandler;
import crazypants.gui.GhostSlot;

public class ContainerCobbleworks extends AbstractMachineContainer {

  public ContainerCobbleworks(InventoryPlayer playerInv, TileCobbleworks te) {
    super(playerInv, te);
  }

  @Override
  public Point getPlayerInventoryOffset() {
    return new Point(37, 84);
  }

  @Override
  public Point getUpgradeOffset() {
    return new Point(12, 60);
  }

  private final int COL_COBBLE = 43;
  private final int COL_INPUT_1 = 43;
  private final int COL_OUTPUT_1 = 73;
  private final int COL_OFFSET = 54;

  private final int ROW_INPUT = 35;
  private final int ROW_COBBLE = 8;
  private final int ROW_OUTPUT_1 = 8;
  private final int ROW_OFFSET = 18;

  @Override
  protected void addMachineSlots(InventoryPlayer playerInv) {
    TileCobbleworks te = (TileCobbleworks) tileEntity;

    addSlotToContainer(new OutputSlot(tileEntity, te.outputSlotNo(0, 0), COL_COBBLE, ROW_COBBLE));

    for (int work = 1; work <= te.WORKS; work++) {
      addSlotToContainer(new InputSlot(tileEntity, te.inputSlotNo(work), COL_INPUT_1 + (work - 1) * COL_OFFSET, ROW_INPUT));
      for (int slot = 1; slot <= te.SLOTS_PER_WORK; slot++) {
        addSlotToContainer(new OutputSlot(tileEntity, te.outputSlotNo(work, slot), COL_OUTPUT_1 + (work - 1) * COL_OFFSET,
            ROW_OUTPUT_1 + (slot - 1) * ROW_OFFSET));
      }
    }
  }

  private class InputSlot extends Slot {
    private final int slot;

    public InputSlot(IInventory par1iInventory, int par2, int par3, int par4) {
      super(par1iInventory, par2, par3, par4);
      slot = par2;
    }

    @Override
    public int getSlotStackLimit() {
      return 1;
    }

    @Override
    public boolean isItemValid(ItemStack itemStack) {
      return getTileEntity().isItemValidForSlot(slot, itemStack);
    }
  }

  private class OutputSlot extends Slot {

    public OutputSlot(IInventory par1iInventory, int par2, int par3, int par4) {
      super(par1iInventory, par2, par3, par4);
    }

    @Override
    public boolean isItemValid(ItemStack itemStack) {
      return false;
    }
  }

}
