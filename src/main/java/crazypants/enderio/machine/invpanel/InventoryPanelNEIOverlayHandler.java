package crazypants.enderio.machine.invpanel;

import codechicken.lib.inventory.InventoryUtils;
import codechicken.nei.LayoutManager;
import codechicken.nei.OffsetPositioner;
import codechicken.nei.PositionedStack;
import codechicken.nei.api.DefaultOverlayRenderer;
import codechicken.nei.api.IOverlayHandler;
import codechicken.nei.api.IStackPositioner;
import codechicken.nei.recipe.IRecipeHandler;
import crazypants.enderio.network.PacketHandler;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class InventoryPanelNEIOverlayHandler implements IOverlayHandler {

  private static final int NEI_OFFSET_X = 25;
  private static final int NEI_OFFSET_Y = 6;

  private static final int CRAFTING_GRID_OFFSET_X = InventoryPanelContainer.CRAFTING_GRID_X - NEI_OFFSET_X;
  private static final int CRAFTING_GRID_OFFSET_Y = InventoryPanelContainer.CRAFTING_GRID_Y - NEI_OFFSET_Y;

  private final IStackPositioner positioner = new OffsetPositioner(CRAFTING_GRID_OFFSET_X, CRAFTING_GRID_OFFSET_Y);

  @Override
  public void overlayRecipe(GuiContainer gui, IRecipeHandler recipe, int recipeIndex, boolean shift) {
    GuiInventoryPanel guiInvPanel = (GuiInventoryPanel) gui;
    System.out.println("InventoryPanelNEIOverlayHandler.overlayRecipe " + recipe + " " + recipeIndex + " " + shift);
    List<PositionedStack> ingredients = recipe.getIngredientStacks(recipeIndex);

    for(PositionedStack stack : ingredients) {
      System.out.println("stack={x="+stack.relx+",y="+stack.rely+",item="+stack.item+"}");
    }

    LayoutManager.overlayRenderer = new DefaultOverlayRenderer(ingredients, positioner);

    if(shift) {
      shift = clearIngredients(guiInvPanel);
    }

    PositionedStack[] slots = mapSlots(ingredients, guiInvPanel.getContainer());
    if(slots != null) {
      CraftingHelper helper = new CraftingHelper(slots);
      guiInvPanel.setCraftingHelper(helper);
      if(shift) {
        helper.refill(guiInvPanel, 64);
      }
    } else {
      guiInvPanel.setCraftingHelper(null);
    }
  }

  private boolean clearIngredients(GuiInventoryPanel gui) {
    InventoryPanelContainer c = gui.getContainer();
    for(Slot slot : c.getCraftingGridSlots()) {
      if(slot.getHasStack()) {
        c.moveItemsToReturnArea(slot.slotNumber);
        if(slot.getHasStack())
          return false;
      }
    }
    return true;
  }

  private PositionedStack[] mapSlots(List<PositionedStack> ingredients, InventoryPanelContainer c) {
    PositionedStack[] slots = new PositionedStack[9];
    List<Slot> craftingGrid = c.getCraftingGridSlots();
    int found = 0;
    for(PositionedStack pstack : ingredients) {
      for(int idx = 0; idx < 9 ; idx++) {
        Slot slot = craftingGrid.get(idx);
        if(slot.xDisplayPosition == pstack.relx + CRAFTING_GRID_OFFSET_X && slot.yDisplayPosition == pstack.rely + CRAFTING_GRID_OFFSET_Y) {
          slots[idx] = pstack;
          found++;
          break;
        }
      }
    }
    if(found != ingredients.size()) {
      return null;
    }
    return slots;
  }

  static class CraftingHelper implements ICraftingHelper {
    final PositionedStack[] ingredients;

    public CraftingHelper(PositionedStack[] ingredients) {
      this.ingredients = ingredients;
    }

    @Override
    public void refill(GuiInventoryPanel gui, int amount) {
      InventoryPanelContainer container = gui.getContainer();
      List<Slot> craftingGrid = container.getCraftingGridSlots();
      int slotsToProcess = (1<<9)-1;
      boolean madeProgress;
      int maxAmount = 64;
      do {
        Candidate[] candidates = new Candidate[9];
        for(int idx = 0; idx < 9; idx++) {
          if((slotsToProcess & (1 << idx)) != 0) {
            PositionedStack pstack = ingredients[idx];
            Slot slot = craftingGrid.get(idx);
            ItemStack stack = slot.getStack();
            if(pstack == null) {
              if(stack != null) {
                return;
              }
            } else {
              Candidate candidate;
              if(stack != null) {
                if(!isStackCompatible(pstack, stack)) {
                  return;
                }
                candidate = findCandidates(stack, gui, candidates);
              } else {
                candidate = findAllCandidates(pstack, gui, candidates);
              }

              if(candidate == null) {
                return;
              }
              candidate.used++;
              candidates[idx] = candidate;
            }
          }
        }

        int targetAmount = maxAmount;
        int currentAmount = 0;
        for(int idx = 0; idx < 9; idx++) {
          Candidate candidate = candidates[idx];
          if(candidate != null) {
            Slot slot = craftingGrid.get(idx);
            int current = getSlotStackSize(slot);
            int maxStackSize = candidate.stack.getMaxStackSize();
            currentAmount = Math.max(currentAmount, current);
            if(candidate.stack.isStackable() && maxStackSize > 1) {
              targetAmount = Math.min(targetAmount, current + Math.min(maxStackSize, candidate.getAvailable()));
            }
          }
        }

        targetAmount = Math.min(targetAmount, currentAmount + amount);
        madeProgress = false;
        for(int idx = 0; idx < 9; idx++) {
          final int mask = 1 << idx;
          Candidate candidate = candidates[idx];
          if(candidate != null) {
            Slot slot = craftingGrid.get(idx);
            for(Slot srcSlot : candidate.sourceSlots) {
              int current = getSlotStackSize(slot);
              if(current >= targetAmount) {
                break;
              }
              if(container.moveItems(srcSlot.slotNumber, slot.slotNumber, slot.slotNumber+1, targetAmount - current)) {
                slotsToProcess &= ~mask;
                madeProgress = true;
              }
            }

            int current = getSlotStackSize(slot);
            if(candidate.entry != null) {
              if(current < targetAmount) {
                int toMove = Math.min(candidate.entry.getCount(), targetAmount - current);
                System.out.println("fetching items from DB");
                PacketHandler.INSTANCE.sendToServer(new PacketFetchItem(candidate.entry, slot.slotNumber, toMove));
                slotsToProcess &= ~mask;
                current += toMove;
              }
            }

            if(current > 0) {
              maxAmount = Math.min(maxAmount, current);
            }
          }
        }
      }while(madeProgress && slotsToProcess != 0);
    }

    private static int getSlotStackSize(Slot slot) {
      ItemStack stack = slot.getStack();
      return (stack != null) ? stack.stackSize : 0;
    }

    private static boolean isStackCompatible(PositionedStack pstack, ItemStack stack) {
      for(ItemStack istack : pstack.items) {
        if(InventoryUtils.canStack(stack, istack)) {
          return true;
        }
      }
      return false;
    }

    private Candidate findAllCandidates(PositionedStack pstack, GuiInventoryPanel gui, Candidate[] candidates) {
      Candidate bestInventory = null;
      Candidate bestNetwork = null;
      for(ItemStack istack : pstack.items) {
        Candidate candidate = findCandidates(istack, gui, candidates);
        if(candidate.available > 0) {
          if(bestInventory == null || bestInventory.available < candidate.available) {
            bestInventory = candidate;
          }
        }
        if(candidate.entry != null) {
          if(bestNetwork == null || bestNetwork.entry.getCount() < candidate.entry.getCount()) {
            bestNetwork = candidate;
          }
        }
      }
      if(bestInventory != null) {
        return bestInventory;
      } else {
        return bestNetwork;
      }
    }

    private Candidate findCandidates(ItemStack stack, GuiInventoryPanel gui, Candidate[] candidates) {
      for(Candidate candidate : candidates) {
        if(candidate != null && InventoryUtils.canStack(candidate.stack, stack)) {
          return candidate;
        }
      }
      Candidate candidate = new Candidate(stack);
      InventoryPanelContainer container = gui.getContainer();
      findCandidates(candidate, stack, container.getReturnAreaSlots());
      findCandidates(candidate, stack, container.getPlayerInventorySlots());
      if(candidate.available == 0) {
        InventoryDatabaseClient db = gui.getDatabase();
        candidate.entry = db.lookupItem(stack, null, false);
        System.out.println("Looked up DB entry: stack=" + stack + " entry="+candidate.entry);
        if(candidate.entry != null && candidate.entry.getCount() <= 0) {
          candidate.entry = null;
        }
      }
      return candidate;
    }

    private void findCandidates(Candidate candidates, ItemStack stack, Collection<Slot> slots) {
      for(Slot slot : slots) {
        ItemStack slotStack = slot.getStack();
        if(slotStack != null && InventoryUtils.canStack(slotStack, stack)) {
          candidates.sourceSlots.add(slot);
          candidates.available += slotStack.stackSize;
        }
      }
    }
  }

  static class Candidate {
    final ItemStack stack;
    final ArrayList<Slot> sourceSlots = new ArrayList<Slot>();
    InventoryDatabaseClient.ItemEntry entry;
    int available;
    int used;

    public Candidate(ItemStack stack) {
      this.stack = stack;
    }

    public int getAvailable() {
      int avail = available;
      if(entry != null) {
        avail += entry.getCount();
      }
      if(avail > 0 && used > 1) {
        avail = Math.max(1, avail / used);
      }
      return avail;
    }
  }
}
