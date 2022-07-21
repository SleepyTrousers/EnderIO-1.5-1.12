package crazypants.enderio.machine.invpanel.client;

import codechicken.nei.OffsetPositioner;
import codechicken.nei.PositionedStack;
import codechicken.nei.api.DefaultOverlayRenderer;
import codechicken.nei.api.IOverlayHandler;
import codechicken.nei.api.IStackPositioner;
import codechicken.nei.recipe.IRecipeHandler;
import crazypants.enderio.machine.invpanel.GuiInventoryPanel;
import crazypants.enderio.machine.invpanel.InventoryPanelContainer;
import java.util.List;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class InventoryPanelNEIOverlayHandler implements IOverlayHandler {

    public static final int NEI_OFFSET_X = 25;
    public static final int NEI_OFFSET_Y = 6;

    private static final int CRAFTING_GRID_OFFSET_X = InventoryPanelContainer.CRAFTING_GRID_X - NEI_OFFSET_X;
    private static final int CRAFTING_GRID_OFFSET_Y = InventoryPanelContainer.CRAFTING_GRID_Y - NEI_OFFSET_Y;

    public static final IStackPositioner positioner =
            new OffsetPositioner(CRAFTING_GRID_OFFSET_X, CRAFTING_GRID_OFFSET_Y);

    @Override
    public void overlayRecipe(GuiContainer gui, IRecipeHandler recipe, int recipeIndex, boolean shift) {
        GuiInventoryPanel guiInvPanel = (GuiInventoryPanel) gui;
        List<PositionedStack> ingredients = recipe.getIngredientStacks(recipeIndex);

        if (shift) {
            shift = guiInvPanel.getContainer().clearCraftingGrid();
        }

        ItemStack[][] slots = mapSlots(ingredients, guiInvPanel.getContainer());
        if (slots != null) {
            CraftingHelperNEI helper = new CraftingHelperNEI(slots);
            helper.overlayRenderer = new DefaultOverlayRenderer(ingredients, positioner);
            guiInvPanel.setCraftingHelper(helper);
            if (shift) {
                helper.refill(guiInvPanel, 64);
            }
        } else {
            guiInvPanel.setCraftingHelper(null);
        }
    }

    private ItemStack[][] mapSlots(List<PositionedStack> ingredients, InventoryPanelContainer c) {
        ItemStack[][] slots = new ItemStack[9][];
        List<Slot> craftingGrid = c.getCraftingGridSlots();
        int found = 0;
        for (PositionedStack pstack : ingredients) {
            for (int idx = 0; idx < 9; idx++) {
                Slot slot = craftingGrid.get(idx);
                if (slot.xDisplayPosition == pstack.relx + CRAFTING_GRID_OFFSET_X
                        && slot.yDisplayPosition == pstack.rely + CRAFTING_GRID_OFFSET_Y) {
                    slots[idx] = pstack.items;
                    found++;
                    break;
                }
            }
        }
        if (found != ingredients.size()) {
            return null;
        }
        return slots;
    }
}
