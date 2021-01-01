package crazypants.enderio.invpanel.integration.jei;

import javax.annotation.Nonnull;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.ISubtypeRegistry;
import mezz.jei.api.JEIPlugin;
import mezz.jei.api.recipe.IRecipeCategoryRegistration;
import net.minecraftforge.fml.common.ProgressManager;

@JEIPlugin
public class InventoryPanelPlugin implements IModPlugin {

  @Override
  public void registerItemSubtypes(@Nonnull ISubtypeRegistry subtypeRegistry) {
    InvPanelSubtypeInterpreter.registerSubtypes(subtypeRegistry);
  }

  @Override
  public void registerCategories(@Nonnull IRecipeCategoryRegistration registry) {
  }

  @Override
  public void register(@Nonnull IModRegistry registry) {
    ProgressManager.ProgressBar bar = ProgressManager.push("Ender IO Machines", 1, true);

    bar.step("Inventory Panel");
    InventoryPanelRecipeTransferHandler.register(registry);

    ProgressManager.pop(bar);
  }
}