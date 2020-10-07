package crazypants.enderio.invpanel.integration.jei;

import mezz.jei.api.*;
import mezz.jei.api.recipe.IRecipeCategoryRegistration;
import net.minecraftforge.fml.common.ProgressManager;

import javax.annotation.Nonnull;

@JEIPlugin
public class InventoryPanelPlugin implements IModPlugin {

    @Override
    public void registerCategories(@Nonnull IRecipeCategoryRegistration registry) {
        IJeiHelpers jeiHelpers = registry.getJeiHelpers();
    }

    @Override
    public void register(@Nonnull IModRegistry registry) {
        ProgressManager.ProgressBar bar = ProgressManager.push("Ender IO Machines", 1, true);

        IGuiHelper guiHelper = registry.getJeiHelpers().getGuiHelper();

        bar.step("Inventory Panel");
        InventoryPanelRecipeTransferHandler.register(registry);

        ProgressManager.pop(bar);
    }
}