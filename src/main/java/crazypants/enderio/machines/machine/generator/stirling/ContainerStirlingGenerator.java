package crazypants.enderio.machines.machine.generator.stirling;

import javax.annotation.Nonnull;

import com.enderio.core.client.gui.widget.GhostBackgroundItemSlot;
import com.enderio.core.client.gui.widget.GhostSlot;
import com.enderio.core.common.util.NNList;
import com.enderio.core.common.util.NNList.Callback;

import crazypants.enderio.base.integration.jei.ItemHelper;
import crazypants.enderio.base.machine.fuel.ISolidFuelHandler;
import crazypants.enderio.base.machine.gui.AbstractMachineContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ContainerStirlingGenerator<T extends TileStirlingGenerator> extends AbstractMachineContainer<T> implements ISolidFuelHandler {

  public ContainerStirlingGenerator(@Nonnull InventoryPlayer playerInv, @Nonnull T te) {
    super(playerInv, te);
  }

  @Override
  protected void addMachineSlots(@Nonnull InventoryPlayer playerInv) {
    addSlotToContainer(new Slot(getInv(), 0, 80, 34) {
      @Override
      public boolean isItemValid(@Nonnull ItemStack itemStack) {
        return getInv().isItemValidForSlot(0, itemStack);
      }
    });
  }

  public void addGhostslots(NNList<GhostSlot> ghostSlots) {
    NNList<ItemStack> fuels = new NNList<>();
    ItemHelper.getValidItems().apply(new Callback<ItemStack>() {
      @Override
      public void apply(@Nonnull ItemStack e) {
        if (getTe().isMachineItemValidForSlot(0, e)) {
          fuels.add(e);
          if (e.getItem() == Items.LAVA_BUCKET || e.getItem() == Items.COAL) {
            // put an emphasis on the common fuels, especially the lava bucket---many players don't know about that one
            for (int i = 0; i < 30; i++) {
              fuels.add(e);
            }
          }
        }
      }
    });
    ghostSlots.add(new GhostBackgroundItemSlot(fuels, getSlotFromInventory(0)));
  }

  @Override
  public boolean isInGUI() {
    return true;
  }

  @Override
  public int getPowerUsePerTick() {
    return getTe().getPowerUsePerTick();
  }

  @Override
  public long getBurnTime(@Nonnull ItemStack itemstack) {
    return getTe().isMachineItemValidForSlot(0, itemstack) ? getTe().getBurnTime(itemstack) : -1;
  }

}
