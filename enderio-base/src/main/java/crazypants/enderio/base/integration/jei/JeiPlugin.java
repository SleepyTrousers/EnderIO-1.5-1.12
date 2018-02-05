package crazypants.enderio.base.integration.jei;

import java.util.Collections;

import javax.annotation.Nonnull;

import com.enderio.core.common.util.NNList;

import crazypants.enderio.base.Log;
import crazypants.enderio.base.fluid.Fluids;
import crazypants.enderio.base.integration.jei.energy.EnergyIngredient;
import crazypants.enderio.base.integration.jei.energy.EnergyIngredientHelper;
import crazypants.enderio.base.integration.jei.energy.EnergyIngredientRenderer;
import crazypants.enderio.base.material.material.Material;
import mezz.jei.api.BlankModPlugin;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.IJeiHelpers;
import mezz.jei.api.IJeiRuntime;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.ISubtypeRegistry;
import mezz.jei.api.JEIPlugin;
import mezz.jei.api.ingredients.IModIngredientRegistration;
import mezz.jei.api.recipe.VanillaRecipeCategoryUid;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.ShapelessRecipes;

import static crazypants.enderio.base.init.ModObject.itemMaterial;

@JEIPlugin
public class JeiPlugin extends BlankModPlugin {

  private static IJeiRuntime jeiRuntime = null;

  @Override
  public void registerItemSubtypes(@Nonnull ISubtypeRegistry subtypeRegistry) {
    DarkSteelUpgradeRecipeCategory.registerSubtypes(subtypeRegistry);
    MobContainerSubtypeInterpreter.registerSubtypes(subtypeRegistry);
  }

  @Override
  public void register(@Nonnull IModRegistry registry) {

    IJeiHelpers jeiHelpers = registry.getJeiHelpers();
    IGuiHelper guiHelper = jeiHelpers.getGuiHelper();

    DarkSteelUpgradeRecipeCategory.register(registry, guiHelper);
    DescriptionRecipeCategory.register(registry);
    InfinityRecipeCategory.register(registry, guiHelper);

    registry.addAdvancedGuiHandlers(new AdvancedGuiHandlerEnderIO());

    // Add a couple of example recipes for the nut.dist stick as the custom recipe isn't picked up
    NNList<ItemStack> inputs = new NNList<ItemStack>();
    inputs.add(new ItemStack(Items.STICK));
    inputs.add(Fluids.NUTRIENT_DISTILLATION.getBucket());
    ShapelessRecipes res = new ShapelessRecipes(null, new ItemStack(itemMaterial.getItemNN(), 1, Material.NUTRITIOUS_STICK.ordinal()), inputs);
    registry.addRecipes(Collections.singletonList(res), VanillaRecipeCategoryUid.CRAFTING);

    if (!JeiAccessor.ALTERNATIVES.isEmpty()) {
      // These are lookups for the outputs, the real recipes with the same input create a different oredicted variant of the output item.
      registry.addRecipes(JeiAccessor.ALTERNATIVES, VanillaRecipeCategoryUid.CRAFTING);
      Log.debug("Provided " + JeiAccessor.ALTERNATIVES.size() + " synthetic crafting recipes to JEI");
    }
  }

  @Override
  public void onRuntimeAvailable(@Nonnull IJeiRuntime jeiRuntimeIn) {
    JeiPlugin.jeiRuntime = jeiRuntimeIn;
    JeiAccessor.jeiRuntimeAvailable = true;
  }

  public static void setFilterText(@Nonnull String filterText) {
    jeiRuntime.getItemListOverlay().setFilterText(filterText);
  }

  public static @Nonnull String getFilterText() {
    return jeiRuntime.getItemListOverlay().getFilterText();
  }

  @Override
  public void registerIngredients(@Nonnull IModIngredientRegistration ingredientRegistration) {
    ingredientRegistration.register(EnergyIngredient.class, Collections.singletonList(new EnergyIngredient()), new EnergyIngredientHelper(),
        EnergyIngredientRenderer.INSTANCE);
  }

}
