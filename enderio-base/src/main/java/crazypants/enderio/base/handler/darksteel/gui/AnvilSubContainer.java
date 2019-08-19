package crazypants.enderio.base.handler.darksteel.gui;

import java.util.Map;

import javax.annotation.Nonnull;

import org.apache.commons.lang3.StringUtils;

import com.enderio.core.common.util.BlockCoord;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.inventory.InventoryCraftResult;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemEnchantedBook;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class AnvilSubContainer {

  private class OutputSlot extends HidingSlot {
    private OutputSlot(@Nonnull IInventory inventoryIn, int index, int xPosition, int yPosition) {
      super(inventoryIn, index, xPosition, yPosition);
    }

    @Override
    public boolean isItemValid(@Nonnull ItemStack stack) {
      return false;
    }

    @Override
    public boolean canTakeStack(@Nonnull EntityPlayer playerIn) {
      return (playerIn.capabilities.isCreativeMode || playerIn.experienceLevel >= maximumCost) && maximumCost > 0 && getHasStack();
    }

    @Override
    public @Nonnull ItemStack onTake(@Nonnull EntityPlayer thePlayer, @Nonnull ItemStack stack) {
      if (!thePlayer.capabilities.isCreativeMode) {
        thePlayer.addExperienceLevel(-maximumCost);
      }

      inputSlots.setInventorySlotContents(0, ItemStack.EMPTY);

      if (materialCost > 0) {
        ItemStack itemstack = inputSlots.getStackInSlot(1);
        itemstack.shrink(materialCost);
        inputSlots.setInventorySlotContents(1, itemstack);
      } else {
        inputSlots.setInventorySlotContents(1, ItemStack.EMPTY);
      }

      maximumCost = 0;

      if (!thePlayer.world.isRemote) {
        thePlayer.world.playEvent(1030, BlockCoord.get(thePlayer), 0);
      }

      return stack;
    }
  }

  private class HidingSlot extends Slot {
    private HidingSlot(@Nonnull IInventory inventoryIn, int index, int xPosition, int yPosition) {
      super(inventoryIn, index, xPosition, yPosition);
    }

    @Override
    public boolean isEnabled() {
      return parent.activeTab.isAnvil();
    }
  }

  private final @Nonnull DSUContainer parent;

  private final @Nonnull IInventory outputSlot = new InventoryCraftResult();
  private final @Nonnull IInventory inputSlots = new InventoryBasic("Repair", true, 2) {
    @Override
    public void markDirty() {
      super.markDirty();
      updateRepairOutput();
    }
  };
  public int maximumCost = 0;
  public int materialCost = 0;
  private @Nonnull String repairedItemName = "";
  final @Nonnull EntityPlayer player;

  AnvilSubContainer(@Nonnull DSUContainer parent, @Nonnull EntityPlayer player) {
    this.parent = parent;
    this.player = player;

  }

  void addSlots() {
    parent.addSlotToContainer(new HidingSlot(inputSlots, 0, 27, 47));
    parent.addSlotToContainer(new HidingSlot(inputSlots, 1, 76, 47));
    parent.addSlotToContainer(new OutputSlot(outputSlot, 2, 134, 47));
  }

  public void updateRepairOutput() {
    ItemStack itemstack = inputSlots.getStackInSlot(0);
    maximumCost = 1;
    int i = 0;
    int j = 0;
    int k = 0;

    if (itemstack.isEmpty()) {
      this.outputSlot.setInventorySlotContents(0, ItemStack.EMPTY);
      this.maximumCost = 0;
    } else {
      ItemStack itemstack1 = itemstack.copy();
      ItemStack itemstack2 = inputSlots.getStackInSlot(1);
      Map<Enchantment, Integer> map = EnchantmentHelper.getEnchantments(itemstack1);
      j = j + itemstack.getRepairCost() + (itemstack2.isEmpty() ? 0 : itemstack2.getRepairCost());
      this.materialCost = 0;
      boolean flag = false;

      if (!itemstack2.isEmpty()) {
        // TODO: We need to supply a fake ContainerRepair here or fire the event ourselves
        if (!net.minecraftforge.common.ForgeHooks.onAnvilChange(null, itemstack, itemstack2, outputSlot, repairedItemName, j))
          return;
        flag = itemstack2.getItem() == Items.ENCHANTED_BOOK && !ItemEnchantedBook.getEnchantments(itemstack2).hasNoTags();

        if (itemstack1.isItemStackDamageable() && itemstack1.getItem().getIsRepairable(itemstack, itemstack2)) {
          int l2 = Math.min(itemstack1.getItemDamage(), itemstack1.getMaxDamage() / 4);

          if (l2 <= 0) {
            outputSlot.setInventorySlotContents(0, ItemStack.EMPTY);
            maximumCost = 0;
            return;
          }

          int i3;

          for (i3 = 0; l2 > 0 && i3 < itemstack2.getCount(); ++i3) {
            int j3 = itemstack1.getItemDamage() - l2;
            itemstack1.setItemDamage(j3);
            ++i;
            l2 = Math.min(itemstack1.getItemDamage(), itemstack1.getMaxDamage() / 4);
          }

          this.materialCost = i3;
        } else {
          if (!flag && (itemstack1.getItem() != itemstack2.getItem() || !itemstack1.isItemStackDamageable())) {
            outputSlot.setInventorySlotContents(0, ItemStack.EMPTY);
            maximumCost = 0;
            return;
          }

          if (itemstack1.isItemStackDamageable() && !flag) {
            int l = itemstack.getMaxDamage() - itemstack.getItemDamage();
            int i1 = itemstack2.getMaxDamage() - itemstack2.getItemDamage();
            int j1 = i1 + itemstack1.getMaxDamage() * 12 / 100;
            int k1 = l + j1;
            int l1 = itemstack1.getMaxDamage() - k1;

            if (l1 < 0) {
              l1 = 0;
            }

            if (l1 < itemstack1.getItemDamage()) {
              itemstack1.setItemDamage(l1);
              i += 2;
            }
          }

          Map<Enchantment, Integer> map1 = EnchantmentHelper.getEnchantments(itemstack2);
          boolean flag2 = false;
          boolean flag3 = false;

          for (Enchantment enchantment1 : map1.keySet()) {
            if (enchantment1 != null) {
              int i2 = map.containsKey(enchantment1) ? map.get(enchantment1).intValue() : 0;
              int j2 = map1.get(enchantment1).intValue();
              j2 = i2 == j2 ? j2 + 1 : Math.max(j2, i2);
              boolean flag1 = enchantment1.canApply(itemstack);

              if (player.capabilities.isCreativeMode || itemstack.getItem() == Items.ENCHANTED_BOOK) {
                flag1 = true;
              }

              for (Enchantment enchantment : map.keySet()) {
                if (enchantment != null && enchantment != enchantment1 && !enchantment1.isCompatibleWith(enchantment)) {
                  flag1 = false;
                  ++i;
                }
              }

              if (!flag1) {
                flag3 = true;
              } else {
                flag2 = true;

                if (j2 > enchantment1.getMaxLevel()) {
                  j2 = enchantment1.getMaxLevel();
                }

                map.put(enchantment1, Integer.valueOf(j2));
                int k3 = 0;

                switch (enchantment1.getRarity()) {
                case COMMON:
                  k3 = 1;
                  break;
                case UNCOMMON:
                  k3 = 2;
                  break;
                case RARE:
                  k3 = 4;
                  break;
                case VERY_RARE:
                  k3 = 8;
                }

                if (flag) {
                  k3 = Math.max(1, k3 / 2);
                }

                i += k3 * j2;

                if (itemstack.getCount() > 1) {
                  i = 40;
                }
              }
            }
          }

          if (flag3 && !flag2) {
            outputSlot.setInventorySlotContents(0, ItemStack.EMPTY);
            maximumCost = 0;
            return;
          }
        }
      }

      if (StringUtils.isBlank(this.repairedItemName)) {
        if (itemstack.hasDisplayName()) {
          k = 1;
          i += k;
          itemstack1.clearCustomName();
        }
      } else if (!repairedItemName.equals(itemstack.getDisplayName())) {
        k = 1;
        i += k;
        itemstack1.setStackDisplayName(repairedItemName);
      }
      if (flag && !itemstack1.getItem().isBookEnchantable(itemstack1, itemstack2))
        itemstack1 = ItemStack.EMPTY;

      this.maximumCost = j + i;

      if (i <= 0) {
        itemstack1 = ItemStack.EMPTY;
      }

      if (k == i && k > 0 && maximumCost >= 40) {
        maximumCost = 39;
      }

      if (maximumCost >= 40 && !player.capabilities.isCreativeMode) {
        itemstack1 = ItemStack.EMPTY;
      }

      if (!itemstack1.isEmpty()) {
        int k2 = itemstack1.getRepairCost();

        if (!itemstack2.isEmpty() && k2 < itemstack2.getRepairCost()) {
          k2 = itemstack2.getRepairCost();
        }

        if (k != i || k == 0) {
          k2 = k2 * 2 + 1;
        }

        itemstack1.setRepairCost(k2);
        EnchantmentHelper.setEnchantments(map, itemstack1);
      }

      outputSlot.setInventorySlotContents(0, itemstack1);
      parent.detectAndSendChanges();
    }
  }

  public void addListener(IContainerListener listener) {
    listener.sendWindowProperty(parent, 0, maximumCost);
  }

  @SideOnly(Side.CLIENT)
  public void updateProgressBar(int id, int data) {
    if (id == 0) {
      maximumCost = data;
    }
  }

  public void onContainerClosed(@Nonnull EntityPlayer playerIn) {
    if (!player.world.isRemote) {
      parent.clearContainer(playerIn, player.world, this.inputSlots);
    }
  }

  public void updateItemName(@Nonnull String newName) {
    repairedItemName = newName;

    if (!outputSlot.getStackInSlot(0).isEmpty()) {
      ItemStack itemstack = outputSlot.getStackInSlot(0);

      if (StringUtils.isBlank(newName)) {
        itemstack.clearCustomName();
      } else {
        itemstack.setStackDisplayName(repairedItemName);
      }
    }

    updateRepairOutput();
  }

}
