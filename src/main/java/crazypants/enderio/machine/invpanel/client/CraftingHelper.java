package crazypants.enderio.machine.invpanel.client;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

import com.enderio.core.common.util.ItemUtil;

import crazypants.enderio.machine.invpanel.GuiInventoryPanel;
import crazypants.enderio.machine.invpanel.InventoryPanelContainer;
import crazypants.enderio.machine.invpanel.PacketFetchItem;
import crazypants.enderio.machine.invpanel.StoredCraftingRecipe;
import crazypants.enderio.network.PacketHandler;

public class CraftingHelper {

    final ItemStack[][] ingredients;

    protected CraftingHelper(ItemStack[][] ingredients) {
        this.ingredients = ingredients;
    }

    public static CraftingHelper createFromRecipe(StoredCraftingRecipe recipe) {
        ItemStack[][] ingredients = new ItemStack[9][];
        for (int idx = 0; idx < 9; idx++) {
            ItemStack stack = recipe.get(idx);
            if (stack != null) {
                ingredients[idx] = new ItemStack[] { stack };
            }
        }
        return new CraftingHelperNEI(ingredients);
    }

    public static CraftingHelper createFromSlots(List<Slot> slots) {
        if (slots.size() != 9) {
            return null;
        }
        ItemStack[][] ingredients = new ItemStack[9][];
        int count = 0;
        for (int idx = 0; idx < 9; idx++) {
            Slot slot = slots.get(idx);
            ItemStack stack = slot.getStack();
            if (stack != null) {
                stack = stack.copy();
                stack.stackSize = 1;
                ingredients[idx] = new ItemStack[] { stack };
                count++;
            }
        }
        if (count > 0) {
            return new CraftingHelperNEI(ingredients);
        }
        return null;
    }

    public void install() {}

    public void remove() {}

    public void refill(GuiInventoryPanel gui, int amount) {
        InventoryPanelContainer container = gui.getContainer();
        InventoryDatabaseClient db = gui.getDatabase();
        List<Slot> craftingGrid = container.getCraftingGridSlots();
        int slotsToProcess = (1 << 9) - 1;
        boolean madeProgress;
        int maxAmount = 64;
        do {
            Candidate[] candidates = new Candidate[9];
            for (int idx = 0; idx < 9; idx++) {
                if ((slotsToProcess & (1 << idx)) != 0) {
                    ItemStack[] pstack = ingredients[idx];
                    Slot slot = craftingGrid.get(idx);
                    ItemStack stack = slot.getStack();
                    if (pstack == null) {
                        if (stack != null) {
                            return;
                        }
                    } else {
                        Candidate candidate;
                        if (stack != null) {
                            if (!isStackCompatible(pstack, stack)) {
                                return;
                            }
                            candidate = findCandidates(stack, gui, db, candidates);
                        } else {
                            candidate = findAllCandidates(pstack, gui, db, candidates);
                        }
                        if (candidate == null) {
                            return;
                        }
                        candidate.used++;
                        candidates[idx] = candidate;
                    }
                }
            }
            int targetAmount = maxAmount;
            int currentAmount = 0;
            for (int idx = 0; idx < 9; idx++) {
                Candidate candidate = candidates[idx];
                if (candidate != null) {
                    Slot slot = craftingGrid.get(idx);
                    int current = getSlotStackSize(slot);
                    int maxStackSize = candidate.stack.getMaxStackSize();
                    currentAmount = Math.max(currentAmount, current);
                    if (candidate.stack.isStackable() && maxStackSize > 1) {
                        targetAmount = Math
                                .min(targetAmount, current + Math.min(maxStackSize, candidate.getAvailable()));
                    }
                }
            }
            targetAmount = Math.min(targetAmount, currentAmount + amount);
            madeProgress = false;
            for (int idx = 0; idx < 9; idx++) {
                final int mask = 1 << idx;
                Candidate candidate = candidates[idx];
                if (candidate != null) {
                    Slot slot = craftingGrid.get(idx);
                    for (Slot srcSlot : candidate.sourceSlots) {
                        int current = getSlotStackSize(slot);
                        if (current >= targetAmount) {
                            break;
                        }
                        if (container.moveItems(
                                srcSlot.slotNumber,
                                slot.slotNumber,
                                slot.slotNumber + 1,
                                targetAmount - current)) {
                            slotsToProcess &= ~mask;
                            madeProgress = true;
                        }
                    }
                    int current = getSlotStackSize(slot);
                    if (candidate.entry != null) {
                        if (current < targetAmount) {
                            int toMove = Math.min(candidate.entry.getCount(), targetAmount - current);
                            PacketHandler.INSTANCE.sendToServer(
                                    new PacketFetchItem(db.getGeneration(), candidate.entry, slot.slotNumber, toMove));
                            slotsToProcess &= ~mask;
                            current += toMove;
                        }
                    }
                    if (current > 0) {
                        maxAmount = Math.min(maxAmount, current);
                    }
                }
            }
        } while (madeProgress && slotsToProcess != 0);
    }

    private static int getSlotStackSize(Slot slot) {
        ItemStack stack = slot.getStack();
        return (stack != null) ? stack.stackSize : 0;
    }

    private static boolean isStackCompatible(ItemStack[] pstack, ItemStack stack) {
        for (ItemStack istack : pstack) {
            if (ItemUtil.areStackMergable(stack, istack)) {
                return true;
            }
        }
        return false;
    }

    private Candidate findAllCandidates(ItemStack[] pstack, GuiInventoryPanel gui, InventoryDatabaseClient db,
            Candidate[] candidates) {
        Candidate bestInventory = null;
        Candidate bestNetwork = null;
        for (ItemStack istack : pstack) {
            Candidate candidate = findCandidates(istack, gui, db, candidates);
            if (candidate.available > 0) {
                if (bestInventory == null || bestInventory.available < candidate.available) {
                    bestInventory = candidate;
                }
            }
            if (candidate.entry != null) {
                if (bestNetwork == null || bestNetwork.entry.getCount() < candidate.entry.getCount()) {
                    bestNetwork = candidate;
                }
            }
        }
        if (bestInventory != null) {
            return bestInventory;
        } else {
            return bestNetwork;
        }
    }

    private Candidate findCandidates(ItemStack stack, GuiInventoryPanel gui, InventoryDatabaseClient db,
            Candidate[] candidates) {
        for (Candidate candidate : candidates) {
            if (candidate != null && ItemUtil.areStackMergable(candidate.stack, stack)) {
                return candidate;
            }
        }
        Candidate candidate = new Candidate(stack);
        InventoryPanelContainer container = gui.getContainer();
        if (container.getInventoryPanel().isExtractionDisabled()) {
            findCandidates(candidate, stack, container.getReturnAreaSlots());
        }
        findCandidates(candidate, stack, container.getPlayerInventorySlots());
        findCandidates(candidate, stack, container.getPlayerHotbarSlots());
        if (candidate.available == 0 && db != null) {
            candidate.entry = db.lookupItem(stack, null, false);
            if (candidate.entry != null && candidate.entry.getCount() <= 0) {
                candidate.entry = null;
            }
        }
        return candidate;
    }

    private void findCandidates(Candidate candidates, ItemStack stack, Collection<Slot> slots) {
        for (Slot slot : slots) {
            ItemStack slotStack = slot.getStack();
            if (slotStack != null && ItemUtil.areStackMergable(slotStack, stack)) {
                candidates.sourceSlots.add(slot);
                candidates.available += slotStack.stackSize;
            }
        }
    }

    static class Candidate {

        final ItemStack stack;
        final ArrayList<Slot> sourceSlots = new ArrayList<Slot>();
        ItemEntry entry;
        int available;
        int used;

        public Candidate(ItemStack stack) {
            this.stack = stack;
        }

        public int getAvailable() {
            int avail = available;
            if (entry != null) {
                avail += entry.getCount();
            }
            if (avail > 0 && used > 1) {
                avail = Math.max(1, avail / used);
            }
            return avail;
        }
    }
}
