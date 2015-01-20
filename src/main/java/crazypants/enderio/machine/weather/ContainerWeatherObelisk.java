package crazypants.enderio.machine.weather;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import crazypants.enderio.machine.gui.AbstractMachineContainer;
import crazypants.enderio.machine.weather.TileWeatherObelisk.Task;

public class ContainerWeatherObelisk extends AbstractMachineContainer {

  private int lastPowerUsed;
  private int lastTask;

  public ContainerWeatherObelisk(InventoryPlayer playerInv, TileWeatherObelisk te) {
    super(playerInv, te);
  }

  @Override
  protected void addMachineSlots(InventoryPlayer playerInv) {
    addSlotToContainer(new Slot(tileEntity, 0, 80, 62) {
      @Override
      public boolean isItemValid(ItemStack itemStack) {
        return tileEntity.isItemValidForSlot(0, itemStack);
      }
    });
  }

  @SideOnly(Side.CLIENT)
  public void updateProgressBar(int par1, int par2) {
    TileWeatherObelisk te = (TileWeatherObelisk) getTileEntity();
    if(par1 == 0) {
      te.powerUsed = par2;
    }
    if(par1 == 1) {
      te.activeTask = par2 == -1 ? null : Task.values()[par2];
    }
  }

  public void detectAndSendChanges() {
    super.detectAndSendChanges();
    TileWeatherObelisk te = (TileWeatherObelisk) getTileEntity();

    for (int i = 0; i < this.crafters.size(); ++i) {
      ICrafting icrafting = (ICrafting) this.crafters.get(i);

      int powerUsed = te.powerUsed;
      if(powerUsed != lastPowerUsed) {
        icrafting.sendProgressBarUpdate(this, 0, powerUsed);
      }
      int taskid = te.activeTask == null ? -1 : te.activeTask.ordinal();
      if(taskid != lastTask) {
        icrafting.sendProgressBarUpdate(this, 1, taskid);
      }
    }

    this.lastPowerUsed = te.powerUsed;
    this.lastTask = te.activeTask == null ? -1 : te.activeTask.ordinal();
  }
}
