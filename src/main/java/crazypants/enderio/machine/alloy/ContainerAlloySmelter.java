package crazypants.enderio.machine.alloy;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.AchievementList;

import com.enderio.core.common.util.Util;

import crazypants.enderio.machine.gui.AbstractMachineContainer;

public class ContainerAlloySmelter extends AbstractMachineContainer<TileAlloySmelter> {

    private final EntityPlayer thePlayer;

    public ContainerAlloySmelter(InventoryPlayer playerInv, TileAlloySmelter te) {
        super(playerInv, te);
        thePlayer = playerInv.player;
    }

    @Override
    protected void addMachineSlots(InventoryPlayer playerInv) {
        addSlotToContainer(new Slot(getInv(), 0, 54, 17) {

            @Override
            public boolean isItemValid(ItemStack itemStack) {
                return getInv().isItemValidForSlot(0, itemStack);
            }
        });
        addSlotToContainer(new Slot(getInv(), 1, 79, 7) {

            @Override
            public boolean isItemValid(ItemStack itemStack) {
                return getInv().isItemValidForSlot(1, itemStack);
            }
        });
        addSlotToContainer(new Slot(getInv(), 2, 103, 17) {

            @Override
            public boolean isItemValid(ItemStack itemStack) {
                return getInv().isItemValidForSlot(2, itemStack);
            }
        });
        addSlotToContainer(new SlotSmelter(getInv(), 3, 79, 57));
    }

    private class SlotSmelter extends Slot {

        int numResults = 0;

        public SlotSmelter(IInventory par1iInventory, int par2, int par3, int par4) {
            super(par1iInventory, par2, par3, par4);
        }

        @Override
        public ItemStack decrStackSize(int par1) {
            if (getHasStack()) {
                numResults += Math.min(par1, getStack().stackSize);
            }
            return super.decrStackSize(par1);
        }

        @Override
        public boolean isItemValid(ItemStack par1ItemStack) {
            return false;
        }

        @Override
        public void onPickupFromSlot(EntityPlayer par1EntityPlayer, ItemStack output) {
            onCrafting(output);
            super.onPickupFromSlot(par1EntityPlayer, output);
        }

        @Override
        protected void onCrafting(ItemStack par1ItemStack, int par2) {
            numResults += par2;
            onCrafting(par1ItemStack);
        }

        @Override
        protected void onCrafting(ItemStack output) {
            output.onCrafting(thePlayer.worldObj, thePlayer, numResults);
            if (!thePlayer.worldObj.isRemote) {
                ItemStack outputSized = output.copy();
                outputSized.stackSize = numResults;
                float experience = getInv().getExperienceForOutput(outputSized);
                Util.giveExperience(thePlayer, experience);
            }
            numResults = 0;

            if (output.getItem() == Items.iron_ingot) {
                thePlayer.addStat(AchievementList.acquireIron, 1);
            }
            if (output.getItem() == Items.cooked_fished) {
                thePlayer.addStat(AchievementList.cookFish, 1);
            }
        }
    }
}
