package crazypants.enderio.machine.weather;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import crazypants.enderio.machine.gui.AbstractMachineContainer;

public class ContainerWeatherObelisk extends AbstractMachineContainer {

  public static final int MAX_SCALE = 31;
  
  private int lastPowerUsed;

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
      te.progress = par2;
    }
  }

  public void detectAndSendChanges() {
    super.detectAndSendChanges();
    TileWeatherObelisk te = (TileWeatherObelisk) getTileEntity();

    for (int i = 0; i < this.crafters.size(); ++i) {
      ICrafting icrafting = (ICrafting) this.crafters.get(i);

      int powerUsed = te.powerUsed;
      if(powerUsed != lastPowerUsed) {
        icrafting.sendProgressBarUpdate(this, 0, te.getProgressScaled(MAX_SCALE));
      }
    }

    this.lastPowerUsed = te.powerUsed;
  }
}
