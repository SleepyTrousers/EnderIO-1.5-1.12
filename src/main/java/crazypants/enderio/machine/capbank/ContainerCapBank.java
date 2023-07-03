package crazypants.enderio.machine.capbank;

import java.awt.Point;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import crazypants.enderio.machine.capbank.network.InventoryImpl;
import crazypants.enderio.machine.gui.ContainerEnderTileEntity;
import crazypants.util.BaublesUtil;
import crazypants.util.ShadowInventory;

public class ContainerCapBank extends ContainerEnderTileEntity<TileCapBank> {

    private InventoryImpl inv;

    // Note: Modifying the Baubles inventory on the client side of an integrated
    // server is a bad idea because Baubles actually shares the object between
    // server and client. However, by not doing so, we prevent the callback hooks
    // to be called in the client thread. It doesn't seem to matter now, but if we
    // ever get reports of problems, we need to find a solution to synthesize
    // these calls.
    // In a multiplayer client this works well, but we need to prevent Baubles
    // from calling those callbacks when the server syncs the slot contents to the
    // client (packet 30) initially. They are still called every time the server
    // updates the slots (which is wrong), but the original Baubles inventory
    // works this way, so...
    private IInventory baubles;

    public ContainerCapBank(InventoryPlayer playerInv, TileCapBank cb) {
        super(playerInv, cb);
    }

    public boolean hasBaublesSlots() {
        return baubles != null;
    }

    @Override
    protected void addSlots(final InventoryPlayer playerInv) {
        if (getInv().getNetwork() != null && getInv().getNetwork().getInventory() != null) {
            inv = getInv().getNetwork().getInventory();
        } else {
            inv = new InventoryImpl();
        }

        baubles = BaublesUtil.instance().getBaubles(playerInv.player);

        if (baubles != null && BaublesUtil.WhoAmI.whoAmI(playerInv.player.worldObj) == BaublesUtil.WhoAmI.SPCLIENT) {
            baubles = new ShadowInventory(baubles);
        }

        int armorOffset = 21;
        for (int i = 0; i < 4; i++) {
            addSlotToContainer(new SlotImpl(inv, i, 59 + armorOffset + i * 20, 59));
        }

        // armor slots
        for (int i = 0; i < 4; ++i) {
            final int k = i;
            addSlotToContainer(
                    new Slot(playerInv, playerInv.getSizeInventory() - 1 - i, -15 + armorOffset, 12 + i * 18) {

                        @Override
                        public int getSlotStackLimit() {
                            return 1;
                        }

                        @Override
                        public boolean isItemValid(ItemStack par1ItemStack) {
                            if (par1ItemStack == null) {
                                return false;
                            }
                            return par1ItemStack.getItem().isValidArmor(par1ItemStack, k, playerInv.player);
                        }

                        @Override
                        @SideOnly(Side.CLIENT)
                        public IIcon getBackgroundIconIndex() {
                            return ItemArmor.func_94602_b(k);
                        }
                    });
        }

        if (hasBaublesSlots()) {
            for (int i = 0; i < baubles.getSizeInventory(); i++) {
                addSlotToContainer(new Slot(baubles, i, -15 + armorOffset, 84 + i * 18) {

                    @Override
                    public boolean isItemValid(ItemStack par1ItemStack) {
                        return inventory.isItemValidForSlot(getSlotIndex(), par1ItemStack);
                    }
                });
            }
        }
    }

    public void updateInventory() {
        if (getInv().getNetwork() != null && getInv().getNetwork().getInventory() != null) {
            inv.setCapBank(getInv().getNetwork().getInventory().getCapBank());
        }
    }

    @Override
    public Point getPlayerInventoryOffset() {
        Point p = super.getPlayerInventoryOffset();
        p.translate(21, 0);
        return p;
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer entityPlayer, int slotIndex) {

        int startPlayerSlot = 4;
        int endPlayerSlot = startPlayerSlot + 26;
        int startHotBarSlot = endPlayerSlot + 1;
        int endHotBarSlot = startHotBarSlot + 9;
        int startBaublesSlot = endHotBarSlot + 1;
        int endBaublesSlot = baubles == null ? 0 : startBaublesSlot + baubles.getSizeInventory();

        ItemStack copystack = null;
        Slot slot = (Slot) inventorySlots.get(slotIndex);
        if (slot != null && slot.getHasStack()) {

            ItemStack origStack = slot.getStack();
            copystack = origStack.copy();

            // Note: Merging into Baubles slots is disabled because the used vanilla
            // merge method does not check if the item can go into the slot or not.

            if (slotIndex < 4) {
                // merge from machine input slots to inventory
                if (!mergeItemStackIntoArmor(entityPlayer, origStack, slotIndex)
                        && /*
                            * !(baubles != null && mergeItemStack(origStack, startBaublesSlot, endBaublesSlot, false))
                            * &&
                            */ !mergeItemStack(origStack, startPlayerSlot, endHotBarSlot, false)) {
                    return null;
                }

            } else {
                // Check from inv-> charge then inv->hotbar or hotbar->inv
                if (slotIndex >= startPlayerSlot) {
                    if (!inv.isItemValidForSlot(0, origStack) || !mergeItemStack(origStack, 0, 4, false)) {

                        if (slotIndex <= endPlayerSlot) {
                            if (
                            /*
                             * !(baubles != null && mergeItemStack(origStack, startBaublesSlot, endBaublesSlot, false))
                             * &&
                             */ !mergeItemStack(origStack, startHotBarSlot, endHotBarSlot, false)) {
                                return null;
                            }
                        } else if (slotIndex >= startHotBarSlot && slotIndex <= endHotBarSlot) {
                            if (
                            /*
                             * !(baubles != null && mergeItemStack(origStack, startBaublesSlot, endBaublesSlot, false))
                             * &&
                             */ !mergeItemStack(origStack, startPlayerSlot, endPlayerSlot, false)) {
                                return null;
                            }
                        } else if (slotIndex >= startBaublesSlot && slotIndex <= endBaublesSlot) {
                            if (!mergeItemStack(origStack, startHotBarSlot, endHotBarSlot, false)
                                    && !mergeItemStack(origStack, startPlayerSlot, endPlayerSlot, false)) {
                                return null;
                            }
                        }
                    }
                }
            }

            if (origStack.stackSize == 0) {
                slot.putStack((ItemStack) null);
            } else {
                slot.onSlotChanged();
            }

            slot.onSlotChanged();

            if (origStack.stackSize == copystack.stackSize) {
                return null;
            }

            slot.onPickupFromSlot(entityPlayer, origStack);
        }

        return copystack;
    }

    private boolean mergeItemStackIntoArmor(EntityPlayer entityPlayer, ItemStack origStack, int slotIndex) {
        if (origStack == null || !(origStack.getItem() instanceof ItemArmor)) {
            return false;
        }
        ItemArmor armor = (ItemArmor) origStack.getItem();
        int index = 3 - armor.armorType;
        ItemStack[] ai = entityPlayer.inventory.armorInventory;
        if (ai[index] == null) {
            ai[index] = origStack.copy();
            origStack.stackSize = 0;
            return true;
        }
        return false;
    }

    private static class SlotImpl extends Slot {

        public SlotImpl(IInventory inv, int idx, int x, int y) {
            super(inv, idx, x, y);
        }

        @Override
        public boolean isItemValid(ItemStack itemStack) {
            return inventory.isItemValidForSlot(getSlotIndex(), itemStack);
        }
    }

    /**
     * called when the content of slots is synced from the server to the client (packet 30)
     */
    @Override
    @SideOnly(Side.CLIENT)
    public void putStacksInSlots(ItemStack[] p_75131_1_) {
        if (hasBaublesSlots() && BaublesUtil.WhoAmI.whoAmI(getInv().getWorldObj()) == BaublesUtil.WhoAmI.MPCLIENT) {
            try {
                BaublesUtil.instance().disableCallbacks(baubles, true);
                super.putStacksInSlots(p_75131_1_);
            } finally {
                BaublesUtil.instance().disableCallbacks(baubles, false);
            }
        } else {
            super.putStacksInSlots(p_75131_1_);
        }
    }
}
