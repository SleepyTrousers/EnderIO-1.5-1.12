package crazypants.enderio.base.handler.darksteel.gui;

import java.util.Map;

import javax.annotation.Nonnull;

import org.apache.commons.lang3.StringUtils;

import com.enderio.core.common.ContainerEnder;
import com.enderio.core.common.util.BlockCoord;

import crazypants.enderio.base.block.darksteel.anvil.BlockDarkSteelAnvil;
import crazypants.enderio.base.config.config.BlockConfig;
import crazypants.enderio.base.init.ModObject;
import crazypants.enderio.util.FuncUtil;
import net.minecraft.block.BlockAnvil;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.inventory.InventoryCraftResult;
import net.minecraft.item.ItemEnchantedBook;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AnvilUpdateEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class AnvilSubContainer {

  class OutputSlot extends HidingSlot {
    OutputSlot(IInventory inventoryIn, int index, int xPosition, int yPosition) {
      super(inventoryIn, index, xPosition, yPosition);
    }

    @Override
    public boolean isItemValid(@Nonnull ItemStack stack) {
      return false;
    }

    @Override
    public boolean canTakeStack(@Nonnull EntityPlayer playerIn) {
      return (playerIn.capabilities.isCreativeMode || playerIn.experienceLevel >= getMaximumCost()) && getMaximumCost() > 0 && getHasStack();
    }

    @Override
    public @Nonnull ItemStack onTake(@Nonnull EntityPlayer thePlayer, @Nonnull ItemStack stack) {
      if (!thePlayer.capabilities.isCreativeMode) {
        thePlayer.addExperienceLevel(-getMaximumCost());
      }

      int effect = handleAnvilDamage(stack);

      inputSlots.setInventorySlotContents(0, ItemStack.EMPTY);

      if (materialCost > 0) {
        ItemStack itemstack = inputSlots.getStackInSlot(1);
        itemstack.shrink(materialCost);
        inputSlots.setInventorySlotContents(1, itemstack);
      } else {
        inputSlots.setInventorySlotContents(1, ItemStack.EMPTY);
      }

      setMaximumCost(0);

      if (effect > 0) {
        thePlayer.world.playEvent(effect, BlockCoord.get(thePlayer), 0);
      }

      return stack;
    }
  }

  class HidingSlot extends ContainerEnder.BaseSlot {
    HidingSlot(IInventory inventoryIn, int index, int xPosition, int yPosition) {
      super(inventoryIn, index, xPosition, yPosition);
    }

    @Override
    public boolean isEnabled() {
      return parent.activeTab.isAnvil();
    }
  }

  static final int ANVIL_Y_OFFSET = 10;

  private final DSUContainer parent;

  private final IInventory outputSlot = new InventoryCraftResult();
  final InventoryBasic inputSlots = new InventoryBasic("Repair", true, 2) {
    @Override
    public void markDirty() {
      super.markDirty();
      updateRepairOutput();
    }
  };
  private int maximumCost = 0;
  private int materialCost = 0;
  private String repairedItemName = "";
  final EntityPlayer player;

  AnvilSubContainer(DSUContainer parent, EntityPlayer player) {
    this.parent = parent;
    this.player = player;

  }

  void addSlots() {
    parent.addSlotToContainer(new HidingSlot(inputSlots, 0, 27, 47 + 10));
    parent.addSlotToContainer(new HidingSlot(inputSlots, 1, 76, 47 + 10));
    parent.addSlotToContainer(new OutputSlot(outputSlot, 2, 134, 47 + 10));
  }

  // see net.minecraftforge.common.ForgeHooks.onAnvilChange
  private boolean onAnvilChange(ItemStack left, ItemStack right, int baseCost) {
    AnvilUpdateEvent e = new AnvilUpdateEvent(left, right, repairedItemName, baseCost);
    if (MinecraftForge.EVENT_BUS.post(e)) {
      return false;
    }
    if (e.getOutput().isEmpty()) {
      return true;
    } else {
      outputSlot.setInventorySlotContents(0, e.getOutput());
      maximumCost = e.getCost();
      materialCost = e.getMaterialCost();
      return false;
    }
  }

  public void updateRepairOutput() {
    ItemStack itemstack = inputSlots.getStackInSlot(0);
    maximumCost = 1;
    int cost = 0;
    int baseCost = 0;
    int renamingCost = 0;

    if (itemstack.isEmpty()) {
      abortCalculatingOutput();
    } else {
      ItemStack outputStack = itemstack.copy();
      ItemStack itemstack2 = inputSlots.getStackInSlot(1);
      Map<Enchantment, Integer> existingEnchantments = EnchantmentHelper.getEnchantments(outputStack);
      baseCost = baseCost + itemstack.getRepairCost() + (itemstack2.isEmpty() ? 0 : itemstack2.getRepairCost());
      materialCost = 0;
      boolean applyingBook = false;

      if (!itemstack2.isEmpty()) {
        if (!onAnvilChange(itemstack, itemstack2, baseCost)) {
          return;
        }
        if (itemstack.getCount() > 1) {
          abortCalculatingOutput();
          return;
        }
        applyingBook = itemstack2.getItem() == Items.ENCHANTED_BOOK && !ItemEnchantedBook.getEnchantments(itemstack2).hasNoTags();

        if (outputStack.isItemStackDamageable() && outputStack.getItem().getIsRepairable(itemstack, itemstack2)) {
          int itemDamageToRepair = Math.min(outputStack.getItemDamage(), outputStack.getMaxDamage() / 4);

          if (itemDamageToRepair <= 0) {
            abortCalculatingOutput();
            return;
          }

          for (materialCost = 0; itemDamageToRepair > 0 && materialCost < itemstack2.getCount(); ++materialCost) {
            outputStack.setItemDamage(outputStack.getItemDamage() - itemDamageToRepair);
            ++cost;
            itemDamageToRepair = Math.min(outputStack.getItemDamage(), outputStack.getMaxDamage() / 4);
          }

        } else {
          if (!applyingBook && (outputStack.getItem() != itemstack2.getItem() || !outputStack.isItemStackDamageable())) {
            abortCalculatingOutput();
            return;
          }

          if (outputStack.isItemStackDamageable() && !applyingBook) {
            int l = itemstack.getMaxDamage() - itemstack.getItemDamage();
            int i1 = itemstack2.getMaxDamage() - itemstack2.getItemDamage();
            int j1 = i1 + outputStack.getMaxDamage() * 12 / 100;
            int k1 = l + j1;
            int l1 = outputStack.getMaxDamage() - k1;

            if (l1 < 0) {
              l1 = 0;
            }

            if (l1 < outputStack.getItemDamage()) {
              outputStack.setItemDamage(l1);
              cost += 2;
            }
          }

          Map<Enchantment, Integer> bookEnchants = EnchantmentHelper.getEnchantments(itemstack2);
          boolean atLeastOneCanApply = false;
          boolean atLeastOneCannotApply = false;

          for (Enchantment enchantment : bookEnchants.keySet()) {
            if (enchantment != null) {
              int existingLevel = existingEnchantments.containsKey(enchantment) ? existingEnchantments.get(enchantment).intValue() : 0;
              int newLevel = bookEnchants.get(enchantment).intValue();
              newLevel = existingLevel == newLevel ? newLevel + 1 : Math.max(newLevel, existingLevel);
              boolean canApply = enchantment.canApply(itemstack);

              if (player.capabilities.isCreativeMode || itemstack.getItem() == Items.ENCHANTED_BOOK) {
                canApply = true;
              }

              for (Enchantment existingEnchantment : existingEnchantments.keySet()) {
                if (existingEnchantment != null && existingEnchantment != enchantment && !enchantment.isCompatibleWith(existingEnchantment)) {
                  canApply = false;
                  ++cost;
                }
              }

              if (!canApply) {
                atLeastOneCannotApply = true;
              } else {
                atLeastOneCanApply = true;

                if (newLevel > enchantment.getMaxLevel()) {
                  newLevel = enchantment.getMaxLevel();
                }

                existingEnchantments.put(enchantment, newLevel);
                int multiplier = 0;

                switch (enchantment.getRarity()) {
                case COMMON:
                  multiplier = 1;
                  break;
                case UNCOMMON:
                  multiplier = 2;
                  break;
                case RARE:
                  multiplier = 4;
                  break;
                case VERY_RARE:
                  multiplier = 8;
                }

                if (applyingBook) {
                  multiplier = Math.max(1, multiplier / 2);
                }

                cost += multiplier * newLevel;
              }
            }
          }

          if (atLeastOneCannotApply && !atLeastOneCanApply) {
            abortCalculatingOutput();
            return;
          }
        }
      }

      if (StringUtils.isBlank(repairedItemName)) {
        if (itemstack.hasDisplayName()) {
          renamingCost = 1;
          outputStack.clearCustomName();
        }
      } else if (!repairedItemName.equals(itemstack.getDisplayName())) {
        renamingCost = 1;
        outputStack.setStackDisplayName(repairedItemName);
      }
      if (applyingBook && !outputStack.getItem().isBookEnchantable(outputStack, itemstack2)) {
        abortCalculatingOutput();
        return;
      }

      if (cost <= 0 && renamingCost <= 0) {
        abortCalculatingOutput();
        return;
      }

      maximumCost = baseCost + cost + renamingCost;

      if (cost == 0 && renamingCost > 0 && maximumCost >= getMaxCost()) {
        maximumCost = (getMaxCost() - 1);
      }

      if (maximumCost >= getMaxCost() && !player.capabilities.isCreativeMode) {
        outputStack = ItemStack.EMPTY;
      }

      if (!outputStack.isEmpty()) {
        int newRepairCost = outputStack.getRepairCost();

        if (!itemstack2.isEmpty() && newRepairCost < itemstack2.getRepairCost()) {
          newRepairCost = itemstack2.getRepairCost();
        }

        if (renamingCost != cost || renamingCost == 0) {
          newRepairCost = newRepairCost * 2 + 1;
        }

        outputStack.setRepairCost(newRepairCost);
        EnchantmentHelper.setEnchantments(existingEnchantments, outputStack);
      }

      outputSlot.setInventorySlotContents(0, outputStack);
      parent.detectAndSendChanges();
    }
  }

  static int getMaxCost() {
    return BlockConfig.dsaMaxCost.get();
  }

  private void abortCalculatingOutput() {
    outputSlot.setInventorySlotContents(0, ItemStack.EMPTY);
    maximumCost = 0;
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

  public void onContainerClosed(EntityPlayer playerIn) {
    if (!player.world.isRemote) {
      parent.clearContainer(playerIn, player.world, inputSlots);
    }
  }

  public void updateItemName(String newName) {
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

  public int getMaximumCost() {
    return maximumCost;
  }

  public void setMaximumCost(int maximumCost) {
    this.maximumCost = maximumCost;
  }

  private int handleAnvilDamage(ItemStack stack) {

    if (player.world.isRemote) {
      return -1;
    }

    if (player.capabilities.isCreativeMode) {
      return FuncUtil.runIfNN(parent.block, BlockDarkSteelAnvil::getUseEvent, 1030);
    }

    IBlockState iblockstate = parent.target.read();

    if (iblockstate.getBlock() == Blocks.AIR) {
      return FuncUtil.runIfNN(parent.block, BlockDarkSteelAnvil::getUseEvent, 1030);
    }

    float breakChance = ForgeHooks.onAnvilRepair(player, stack, inputSlots.getStackInSlot(0), inputSlots.getStackInSlot(1));
    breakChance /= 0.12f; // normalize vanilla standard chance to 1.0

    float damageChance = FuncUtil.runIfNN(parent.block, BlockDarkSteelAnvil::getDamageChance, 1f);

    if (player.getRNG().nextFloat() < (breakChance * damageChance)) {
      int l = iblockstate.getValue(BlockAnvil.DAMAGE) + 1;
      if (l > 2) {
        if (iblockstate.getBlock() == ModObject.blockDarkSteelAnvil.getBlockNN()) {
          parent.target
              .write(ModObject.blockBrokenAnvil.getBlockNN().getDefaultState().withProperty(BlockAnvil.FACING, iblockstate.getValue(BlockAnvil.FACING)));
        } else {
          parent.target.write(Blocks.AIR.getDefaultState());
        }
        return FuncUtil.runIfNN(parent.block, BlockDarkSteelAnvil::getBreakEvent, 1029);
      } else {
        parent.target.write(iblockstate.withProperty(BlockAnvil.DAMAGE, l));
        return FuncUtil.runIfNN(parent.block, BlockDarkSteelAnvil::getUseEvent, 1030);
      }
    } else {
      return FuncUtil.runIfNN(parent.block, BlockDarkSteelAnvil::getUseEvent, 1030);
    }

  }

}
