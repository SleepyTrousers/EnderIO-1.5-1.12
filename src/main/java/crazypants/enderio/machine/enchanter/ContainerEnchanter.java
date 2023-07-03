package crazypants.enderio.machine.enchanter;

import java.util.List;

import net.minecraft.enchantment.EnchantmentData;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

import com.enderio.core.client.gui.widget.GhostBackgroundItemSlot;
import com.enderio.core.client.gui.widget.GhostSlot;

import crazypants.enderio.machine.gui.ContainerEnderTileEntity;

public class ContainerEnchanter extends ContainerEnderTileEntity<TileEnchanter> {

    public ContainerEnchanter(EntityPlayer player, InventoryPlayer playerInv, TileEnchanter te) {
        super(playerInv, te);
    }

    @Override
    protected void addSlots(InventoryPlayer playerInv) {

        final TileEnchanter te = getInv();

        addSlotToContainer(new Slot(te, 0, 27, 35) {

            @Override
            public int getSlotStackLimit() {
                return 1;
            }

            @Override
            public boolean isItemValid(ItemStack itemStack) {
                return te.isItemValidForSlot(0, itemStack);
            }

            @Override
            public void onSlotChanged() {
                updateOutput();
            }
        });

        addSlotToContainer(new Slot(te, 1, 76, 35) {

            @Override
            public boolean isItemValid(ItemStack itemStack) {
                return te.isItemValidForSlot(1, itemStack);
            }

            @Override
            public void onSlotChanged() {
                updateOutput();
            }
        });

        addSlotToContainer(new Slot(te, 2, 134, 35) {

            @Override
            public int getSlotStackLimit() {
                return 1;
            }

            @Override
            public boolean isItemValid(ItemStack itemStack) {
                return false;
            }

            @Override
            public void onPickupFromSlot(EntityPlayer player, ItemStack stack) {
                if (!player.capabilities.isCreativeMode) {
                    player.addExperienceLevel(-te.getCurrentEnchantmentCost());
                }
                EnchantmentData enchData = te.getCurrentEnchantmentData();
                EnchanterRecipe recipe = te.getCurrentEnchantmentRecipe();
                ItemStack curStack = te.getStackInSlot(1);
                if (recipe == null || enchData == null
                        || curStack == null
                        || enchData.enchantmentLevel >= curStack.stackSize) {
                    te.setInventorySlotContents(1, (ItemStack) null);
                } else {

                    curStack = curStack.copy();
                    curStack.stackSize -= recipe.getItemsPerLevel() * enchData.enchantmentLevel;
                    if (curStack.stackSize > 0) {
                        te.setInventorySlotContents(1, curStack);
                    } else {
                        te.setInventorySlotContents(1, null);
                    }
                    te.markDirty();
                }

                te.setInventorySlotContents(0, (ItemStack) null);
                if (!te.getWorldObj().isRemote) {
                    te.getWorldObj().playAuxSFX(1021, te.xCoord, te.yCoord, te.zCoord, 0);
                }
            }

            @Override
            public boolean canTakeStack(EntityPlayer player) {
                return playerHasEnoughLevels(player);
            }
        });
    }

    public void createGhostSlots(List<GhostSlot> slots) {
        slots.add(new GhostBackgroundItemSlot(Items.writable_book, 27, 35));
    }

    public boolean playerHasEnoughLevels(EntityPlayer player) {
        if (player.capabilities.isCreativeMode) {
            return true;
        }
        return player.experienceLevel >= getInv().getCurrentEnchantmentCost();
    }

    private void updateOutput() {
        ItemStack output = null;
        EnchantmentData enchantment = getInv().getCurrentEnchantmentData();
        if (enchantment != null) {
            output = new ItemStack(Items.enchanted_book);
            Items.enchanted_book.addEnchantment(output, enchantment);
        }
        getInv().setOutput(output);
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer par1EntityPlayer, int par2) {
        ItemStack copyStack = null;
        Slot slot = (Slot) inventorySlots.get(par2);

        if (slot != null && slot.getHasStack()) {
            ItemStack origStack = slot.getStack();
            copyStack = origStack.copy();

            if (par2 <= 2) {
                if (!mergeItemStack(origStack, 2, inventorySlots.size(), true)) {
                    return null;
                }
            } else {
                if (!getInv().isItemValidForSlot(0, origStack) || !mergeItemStack(origStack, 0, 1, false)) {
                    if (!getInv().isItemValidForSlot(1, origStack) || !mergeItemStack(origStack, 1, 2, false)) {
                        return null;
                    }
                }
            }
            if (origStack.stackSize == 0) {
                slot.putStack((ItemStack) null);
            } else {
                slot.onSlotChanged();
            }
            slot.onPickupFromSlot(par1EntityPlayer, origStack);
        }
        return copyStack;
    }
}
