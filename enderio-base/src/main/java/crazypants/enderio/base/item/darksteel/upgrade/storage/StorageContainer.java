package crazypants.enderio.base.item.darksteel.upgrade.storage;

import javax.annotation.Nonnull;

import com.enderio.core.common.ContainerEnderCap;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.items.SlotItemHandler;

public class StorageContainer extends ContainerEnderCap<StorageCap, TileEntity> {

  private static final int X0 = 0;
  private static final int Y0 = 0;
  private static final int X_MAX = X0 + 9 * 18;

  public StorageContainer(@Nonnull InventoryPlayer playerInv, @Nonnull StorageCap itemHandler) {
    super(playerInv, itemHandler, null);
  }

  @Override
  protected void addSlots() {
    int x = X0, y = Y0;
    for (int i = 0; i < getItemHandler().getSlots(); i++) {
      addSlotToContainer(new SlotItemHandler(getItemHandler(), i, x, y));
      x += 18;
      if (x >= X_MAX) {
        x = X0;
        y += 18;
      }
    }
  }

  @Override
  public void onContainerClosed(@Nonnull EntityPlayer playerIn) {
    getItemHandler().onContentsChanged(0);
    super.onContainerClosed(playerIn);
  }

}
