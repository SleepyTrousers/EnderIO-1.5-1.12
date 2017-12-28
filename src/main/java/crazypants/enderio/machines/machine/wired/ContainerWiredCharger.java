package crazypants.enderio.machines.machine.wired;

import javax.annotation.Nonnull;

import com.enderio.core.client.gui.widget.GhostBackgroundItemSlot;
import com.enderio.core.client.gui.widget.GhostSlot;
import com.enderio.core.common.util.NNList;
import com.enderio.core.common.util.NNList.Callback;

import crazypants.enderio.base.integration.jei.ItemHelper;
import crazypants.enderio.base.machine.gui.AbstractMachineContainer;
import crazypants.enderio.base.power.PowerHandlerUtil;
import crazypants.enderio.machines.machine.tank.InventorySlot;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.energy.IEnergyStorage;

public class ContainerWiredCharger extends AbstractMachineContainer<TileWiredCharger> {

  public ContainerWiredCharger(@Nonnull InventoryPlayer playerInv, @Nonnull TileWiredCharger te) {
    super(playerInv, te);
  }

  @Override
  protected void addMachineSlots(@Nonnull InventoryPlayer playerInv) {
    addSlotToContainer(new InventorySlot(getInv(), 0, 54, 28));
    addSlotToContainer(new InventorySlot(getInv(), 1, 105, 28));
  }

  public void addGhostslots(NNList<GhostSlot> ghostSlots) {
    NNList<ItemStack> empties = new NNList<>();
    NNList<ItemStack> fulls = new NNList<>();
    ItemHelper.getValidItems().apply(new Callback<ItemStack>() {
      @Override
      public void apply(@Nonnull ItemStack e) {
        if (PowerHandlerUtil.getCapability(e, null) != null) {
          ItemStack empty = e.copy();
          IEnergyStorage emptyCap = PowerHandlerUtil.getCapability(empty, null);
          if (emptyCap != null) {
            if (emptyCap.canExtract()) {
              emptyCap.extractEnergy(Integer.MAX_VALUE, false);
            }
            if (emptyCap.canReceive()) {
              empties.add(empty.copy());
              emptyCap.receiveEnergy(Integer.MAX_VALUE, false);
              fulls.add(empty);
            }
          }
        }
      }
    });

    final GhostBackgroundItemSlot ghost0 = new GhostBackgroundItemSlot(empties, getSlotFromInventory(0));
    ghost0.displayStdOverlay = true;
    ghostSlots.add(ghost0);
    final GhostBackgroundItemSlot ghost1 = new GhostBackgroundItemSlot(fulls, getSlotFromInventory(1));
    ghost1.displayStdOverlay = true;
    ghostSlots.add(ghost1);
  }

}
