package crazypants.enderio.machines.machine.wired;

import java.util.List;

import javax.annotation.Nonnull;

import org.apache.commons.lang3.tuple.Triple;

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

  // JEI wants this data without giving us a chance to instantiate a container
  public static int FIRST_RECIPE_SLOT = 0;
  public static int NUM_RECIPE_SLOT = 1;
  public static int FIRST_INVENTORY_SLOT = 1 + 1 + 1; // input + output + upgrade
  public static int NUM_INVENTORY_SLOT = 4 * 9;

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
    getValidPair(ItemHelper.getValidItems()).apply(new Callback<Triple<ItemStack, ItemStack, Integer>>() {
      @Override
      public void apply(@Nonnull Triple<ItemStack, ItemStack, Integer> e) {
        empties.add(e.getLeft());
        fulls.add(e.getMiddle());
      }
    });

    ghostSlots.removeAllByClass(GhostBackgroundItemSlot.class); // JEI will cause initGui to be re-run after closing the recipe view, causing duplicate ghost
                                                                // slots
    final GhostBackgroundItemSlot ghost0 = new GhostBackgroundItemSlot(empties, getSlotFromInventory(0));
    ghost0.displayStdOverlay = true;
    ghostSlots.add(ghost0);
    final GhostBackgroundItemSlot ghost1 = new GhostBackgroundItemSlot(fulls, getSlotFromInventory(1));
    ghost1.displayStdOverlay = true;
    ghostSlots.add(ghost1);
  }

  public static NNList<Triple<ItemStack, ItemStack, Integer>> getValidPair(List<ItemStack> validItems) {
    NNList<Triple<ItemStack, ItemStack, Integer>> result = new NNList<>();
    for (ItemStack stack : validItems) {
      if (PowerHandlerUtil.getCapability(stack, null) != null) {
        ItemStack copy = stack.copy();
        IEnergyStorage emptyCap = PowerHandlerUtil.getCapability(copy, null);
        if (emptyCap != null) {
          int extracted = 1, maxloop = 200;
          while (extracted > 0 && emptyCap.canExtract() && maxloop-- > 0) {
            extracted = emptyCap.extractEnergy(Integer.MAX_VALUE, false);
          }
          if (emptyCap.canReceive() && emptyCap.getEnergyStored() < emptyCap.getMaxEnergyStored()) {
            ItemStack empty = copy.copy();
            int added = emptyCap.receiveEnergy(Integer.MAX_VALUE, false);
            int power = added;
            maxloop = 200;
            while (added > 0 && maxloop-- > 0) {
              power += added = emptyCap.receiveEnergy(Integer.MAX_VALUE, false);
            }
            result.add(Triple.of(empty, copy, power));
          }
        }
      }
    }
    return result;
  }

}
