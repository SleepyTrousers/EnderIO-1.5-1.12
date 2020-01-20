package crazypants.enderio.machines.machine.wired;

import java.awt.Point;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.commons.lang3.tuple.Triple;

import com.enderio.core.client.gui.widget.GhostBackgroundItemSlot;
import com.enderio.core.client.gui.widget.GhostSlot;
import com.enderio.core.common.util.ArrayInventory;
import com.enderio.core.common.util.NNList;
import com.enderio.core.common.util.NNList.Callback;

import crazypants.enderio.base.Log;
import crazypants.enderio.base.integration.baubles.BaublesUtil;
import crazypants.enderio.base.integration.jei.ItemHelper;
import crazypants.enderio.base.machine.gui.AbstractMachineContainer;
import crazypants.enderio.base.power.PowerHandlerUtil;
import crazypants.enderio.machines.machine.tank.InventorySlot;
import crazypants.enderio.util.ShadowInventory;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.inventory.EntityEquipmentSlot.Type;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public abstract class ContainerWiredCharger extends AbstractMachineContainer<TileWiredCharger> {

  // JEI wants this data without giving us a chance to instantiate a container
  public static int FIRST_RECIPE_SLOT = 0;
  public static int NUM_RECIPE_SLOT = 1;
  public static int FIRST_INVENTORY_SLOT = 1 + 1 + 1; // input + output + upgrade
  public static int NUM_INVENTORY_SLOT = 4 * 9;

  protected IInventory baubles;

  @Nonnull
  public static ContainerWiredCharger create(@Nonnull InventoryPlayer playerInv, @Nonnull TileWiredCharger te, int baubleSlots) {
    return new ContainerWiredCharger(playerInv, te) {

      @Override
      protected int getBaublesSize() {
        return baubleSlots;
      }

    };
  }

  public ContainerWiredCharger(@Nonnull InventoryPlayer playerInv, @Nonnull TileWiredCharger te) {
    super(playerInv, te);
  }

  @Override
  protected void addMachineSlots(@Nonnull InventoryPlayer playerInv) {
    addSlotToContainer(new InventorySlot(getInv(), 0, 75, 28));
    addSlotToContainer(new InventorySlot(getInv(), 1, 126, 28));

    baubles = BaublesUtil.instance().getBaubles(playerInv.player);

    if (baubles != null && BaublesUtil.WhoAmI.whoAmI(playerInv.player.world) == BaublesUtil.WhoAmI.SPCLIENT) {
      baubles = new ShadowInventory(baubles);
    }

    if (hasBaublesSlots() && (baubles == null)) {
      baubles = new ArrayInventory(getBaublesSize()) {
        @Override
        public boolean isItemValidForSlot(int i, @Nonnull ItemStack itemstack) {
          return false;
        }

        @Override
        public @Nonnull ItemStack getStackInSlot(int slot) {
          return new ItemStack(Blocks.BARRIER);
        }

        @Override
        public @Nonnull ItemStack decrStackSize(int slot, int amount) {
          return ItemStack.EMPTY;
        }

        @Override
        public @Nonnull ItemStack removeStackFromSlot(int index) {
          return ItemStack.EMPTY;
        }

      };
    }

    int baublesOffset = 196;

    for (final EntityEquipmentSlot slt : EntityEquipmentSlot.values()) {
      if (slt.getSlotType() == Type.ARMOR) {
        addSlotToContainer(new Slot(playerInv, 36 + slt.getIndex(), 6, 66 - slt.getIndex() * 18) {

          @Override
          public int getSlotStackLimit() {
            return 1;
          }

          @Override
          public boolean isItemValid(@Nonnull ItemStack par1ItemStack) {
            if (par1ItemStack.isEmpty()) {
              return false;
            }
            return par1ItemStack.getItem().isValidArmor(par1ItemStack, slt, playerInv.player);
          }

          @Override
          @SideOnly(Side.CLIENT)
          public String getSlotTexture() {
            return ItemArmor.EMPTY_SLOT_NAMES[slt.getIndex()];
          }
        });
      }
    }

    addSlotToContainer(new Slot(playerInv, 40, 6, 12 + 18 * 4) {
      @Override
      @Nullable
      @SideOnly(Side.CLIENT)
      public String getSlotTexture() {
        return "minecraft:items/empty_armor_slot_shield";
      }
    });

    if (hasBaublesSlots() && (baubles != null)) {
      for (int i = 0; i < baubles.getSizeInventory(); i++) {
        addSlotToContainer(new Slot(baubles, i, baublesOffset, 12 + i * 18) {
          @Override
          public boolean isItemValid(@Nonnull ItemStack par1ItemStack) {
            return inventory.isItemValidForSlot(getSlotIndex(), par1ItemStack);
          }

          @Override
          public boolean canTakeStack(@Nonnull EntityPlayer playerIn) {
            ItemStack stackInSlot = inventory.getStackInSlot(getSlotIndex());
            if (stackInSlot.getItem() == Item.getItemFromBlock(Blocks.BARRIER)) {
              return false;
            }
            return super.canTakeStack(playerIn);
          }
        });
      }

    }
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
    ghost0.setDisplayStdOverlay(true);
    ghostSlots.add(ghost0);
    final GhostBackgroundItemSlot ghost1 = new GhostBackgroundItemSlot(fulls, getSlotFromInventory(1));
    ghost1.setDisplayStdOverlay(true);
    ghostSlots.add(ghost1);
  }

  public static NNList<Triple<ItemStack, ItemStack, Integer>> getValidPair(List<ItemStack> validItems) {
    NNList<Triple<ItemStack, ItemStack, Integer>> result = new NNList<>();
    for (ItemStack stack : validItems) {
      try {
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
      } catch (Exception e) {
        Log.LOGGER.error("An itemstack (" + stack + ") threw an exception during energy detection. This is a bug in that mod!", e);
      }
    }
    return result;
  }

  @Override
  public @Nonnull ItemStack transferStackInSlot(@Nonnull EntityPlayer entityPlayer, int slotIndex) {
    int otherSlots = 4 + 5; // charging + armor + off-hand
    int startBaublesSlot = otherSlots;
    int endBaublesSlot = hasBaublesSlots() ? 0 : startBaublesSlot + getBaublesSize();

    ItemStack copystack = ItemStack.EMPTY;
    Slot slot = inventorySlots.get(slotIndex);
    if (slot != null && slot.getHasStack()) {

      ItemStack origStack = slot.getStack();
      copystack = origStack.copy();

      // Note: Merging into Baubles slots is disabled because the used vanilla
      // merge method does not check if the item can go into the slot or not.

      if (slotIndex < 4) {
        // merge from machine input slots to inventory
        if (!mergeItemStackIntoArmor(entityPlayer, origStack, slotIndex)
            && /*
                * !(baubles != null && mergeItemStack(origStack, startBaublesSlot, endBaublesSlot, false)) &&
                */!mergeItemStack(origStack, startPlayerSlot, endHotBarSlot, false)) {
          return ItemStack.EMPTY;
        }

      } else {
        // Check from inv-> charge then inv->hotbar or hotbar->inv
        if (!getInv().isItemValidForSlot(0, origStack) || !mergeItemStack(origStack, 0, 4, false)) {

          if (slotIndex >= startBaublesSlot && slotIndex < endBaublesSlot) {
            if (!mergeItemStack(origStack, startHotBarSlot, endHotBarSlot, false) && !mergeItemStack(origStack, startPlayerSlot, endPlayerSlot, false)) {
              return ItemStack.EMPTY;
            }
          } else if (slotIndex < endPlayerSlot) {
            if (/*
                 * !(baubles != null && mergeItemStack(origStack, startBaublesSlot, endBaublesSlot, false)) &&
                 */!mergeItemStack(origStack, startHotBarSlot, endHotBarSlot, false)) {
              return ItemStack.EMPTY;
            }
          } else if (slotIndex >= startHotBarSlot && slotIndex < endHotBarSlot) {
            if (/*
                 * !(baubles != null && mergeItemStack(origStack, startBaublesSlot, endBaublesSlot, false)) &&
                 */!mergeItemStack(origStack, startPlayerSlot, endPlayerSlot, false)) {
              return ItemStack.EMPTY;
            }
          }

        }
      }

      if (origStack.getCount() == 0) {
        slot.putStack(ItemStack.EMPTY);
      } else {
        slot.onSlotChanged();
      }

      slot.onSlotChanged();

      if (origStack.getCount() == copystack.getCount()) {
        return ItemStack.EMPTY;
      }

      return slot.onTake(entityPlayer, origStack);
    }

    return copystack;
  }

  // BAUBLES

  public boolean hasBaublesSlots() {
    return getBaublesSize() > 0;
  }

  protected abstract int getBaublesSize();

  private boolean mergeItemStackIntoArmor(EntityPlayer entityPlayer, ItemStack origStack, int slotIndex) {
    if (origStack == null || EntityLiving.getSlotForItemStack(origStack).getSlotType() != EntityEquipmentSlot.Type.ARMOR) {
      return false;
    }
    int index = EntityLiving.getSlotForItemStack(origStack).getIndex();
    NonNullList<ItemStack> ai = entityPlayer.inventory.armorInventory;
    if (ai.get(index).isEmpty()) {
      ai.set(index, origStack.copy());
      origStack.setCount(0);
      return true;
    }
    return false;
  }

  @Override
  public @Nonnull Point getPlayerInventoryOffset() {
    return new Point(29, 84);
  }

  @Override
  @Nonnull
  public Point getUpgradeOffset() {
    return new Point(33, 60);
  }

}
