package crazypants.enderio.invpanel.integration.jei;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.common.util.NNList;
import crazypants.enderio.base.EnderIO;
import crazypants.enderio.invpanel.client.CraftingHelper;
import crazypants.enderio.invpanel.client.InventoryDatabaseClient;
import crazypants.enderio.invpanel.client.ItemEntry;
import crazypants.enderio.invpanel.init.InvpanelObject;
import crazypants.enderio.invpanel.invpanel.InventoryPanelContainer;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.gui.IGuiIngredient;
import mezz.jei.api.gui.IGuiItemStackGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.recipe.IStackHelper;
import mezz.jei.api.recipe.VanillaRecipeCategoryUid;
import mezz.jei.api.recipe.transfer.IRecipeTransferError;
import mezz.jei.api.recipe.transfer.IRecipeTransferHandler;
import mezz.jei.api.recipe.transfer.IRecipeTransferRegistry;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

import static crazypants.enderio.invpanel.init.InvpanelObject.blockInventoryPanel;
import static crazypants.enderio.invpanel.invpanel.InventoryPanelContainer.FIRST_INVENTORY_SLOT;
import static crazypants.enderio.invpanel.invpanel.InventoryPanelContainer.NUM_INVENTORY_SLOT;


/**
 * Notes:
 * <p>
 * This doesn't quite follow JEI's mechanics, we also do partial transfers here.
 * Then we cannot check beforehand if a recipe can be transfered. And we don't
 * mix inventory and remote inventories. We also don't report failures, or if we
 * could transfer something at all.
 *
 */
public class InventoryPanelRecipeTransferHandler implements IRecipeTransferHandler {


    public static void register(IModRegistry registry) {
        IRecipeTransferRegistry recipeTransferRegistry = registry.getRecipeTransferRegistry();
        recipeTransferRegistry.addRecipeTransferHandler(new InventoryPanelRecipeTransferHandler(registry), VanillaRecipeCategoryUid.CRAFTING);
        registry.addRecipeCatalyst(new ItemStack(blockInventoryPanel.getBlockNN()), VanillaRecipeCategoryUid.CRAFTING);
    }

    private final IModRegistry registry;

    private InventoryPanelRecipeTransferHandler(IModRegistry registry) {
        this.registry = registry;
    }

    @Override
    public Class getContainerClass() {
        return InventoryPanelContainer.class;
    }

    @Override
    @Nullable
    public IRecipeTransferError transferRecipe(@Nonnull Container container, @Nonnull IRecipeLayout recipeLayout, @Nonnull EntityPlayer player,
                                               boolean maxTransfer, boolean doTransfer) {

        if (!(container instanceof InventoryPanelContainer)) {
            return registry.getJeiHelpers().recipeTransferHandlerHelper().createInternalError();
        }


        InventoryPanelContainer invPanelContainer = (InventoryPanelContainer) container;
        if(doTransfer) {
            if (!invPanelContainer.clearCraftingGrid()) {
                return registry.getJeiHelpers().recipeTransferHandlerHelper().createUserErrorWithTooltip("Could not clear crafting grid");
            }
        }

        //do we have what we need?
        InventoryDatabaseClient db = invPanelContainer.getTe().getDatabaseClient();
        if (db == null) {
            return registry.getJeiHelpers().recipeTransferHandlerHelper().createInternalError();
        }

        List<Integer> missingItemSlots = new ArrayList<Integer>();
        NNList<ItemStack>[] ingredients = new NNList[9];

        IGuiItemStackGroup itemStacks = recipeLayout.getItemStacks();
        Map<Integer, ? extends IGuiIngredient<ItemStack>> guiIngredients = itemStacks.getGuiIngredients();
        for (int i = 0; i < 9; i++) {
            if (guiIngredients.containsKey(i + 1)) {
                List<ItemStack> allIng = guiIngredients.get(i + 1).getAllIngredients();

                if (!allIng.isEmpty()) {
                    if (containerContainsIngredient(invPanelContainer, allIng) || dbContainsIngredient(db, allIng)) {
                        ingredients[i] = new NNList<ItemStack>(allIng);
                    } else {
                        missingItemSlots.add(i + 1);
                    }
                }
            }

        }

        if(missingItemSlots.isEmpty()) {
            if(doTransfer) {
                new CraftingHelper(ingredients).refill(invPanelContainer, maxTransfer ? 64 : 1);
            }
            return null;
        }

        return registry.getJeiHelpers().recipeTransferHandlerHelper().createUserErrorForSlots(EnderIO.lang.localizeExact("jei.tooltip.error.recipe.transfer.missing"), missingItemSlots);
    }

    private static List<Slot> getInventorySlots(InventoryPanelContainer invPanelContainer) {
        List<Slot> slots = new ArrayList<Slot>();
        for (int i = FIRST_INVENTORY_SLOT; i < FIRST_INVENTORY_SLOT + NUM_INVENTORY_SLOT; i++) {
            Slot slot = invPanelContainer.getSlot(i);
            slots.add(slot);
        }
        return slots;
    }

    private boolean containerContainsIngredient(InventoryPanelContainer invPanelContainer, List<ItemStack> allIng) {

        List<Slot> slots = getInventorySlots(invPanelContainer);
        List<ItemStack> available = new ArrayList<ItemStack>();
        for (Slot slot : slots) {
            if (slot.getHasStack()) {
                available.add(slot.getStack());
            }
        }

        IStackHelper sh = registry.getJeiHelpers().getStackHelper();
        return sh.containsAnyStack(available, allIng) != null;
    }

    private boolean dbContainsIngredient(InventoryDatabaseClient db, List<ItemStack> allIng) {
        for (ItemStack ing : allIng) {
            ItemEntry lookup = db.lookupItem(ing, null, false);
            //System.out.println(ing.getMaxStackSize());
            if (lookup != null && lookup.getCount() >= ing.getCount()) {
                return true;
            }
        }
        return false;
    }


}