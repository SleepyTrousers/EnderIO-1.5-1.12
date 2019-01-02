package crazypants.enderio.base.integration.jei;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;

import com.enderio.core.common.util.NNList;
import com.enderio.core.common.util.NullHelper;

import crazypants.enderio.api.upgrades.IDarkSteelItem;
import crazypants.enderio.base.Log;
import crazypants.enderio.base.handler.darksteel.DarkSteelRecipeManager;
import crazypants.enderio.base.handler.darksteel.DarkSteelRecipeManager.UpgradePath;
import crazypants.enderio.base.handler.darksteel.UpgradeRegistry;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.ISubtypeRegistry;
import mezz.jei.api.recipe.IFocus;
import mezz.jei.api.recipe.IFocus.Mode;
import mezz.jei.api.recipe.IRecipeCategory;
import mezz.jei.api.recipe.IRecipeRegistryPlugin;
import mezz.jei.api.recipe.IRecipeWrapper;
import mezz.jei.api.recipe.IVanillaRecipeFactory;
import mezz.jei.api.recipe.VanillaRecipeCategoryUid;
import mezz.jei.plugins.vanilla.anvil.AnvilRecipeWrapper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

import static crazypants.enderio.base.init.ModObject.blockDarkSteelAnvil;

public class DarkSteelUpgradeRecipeCategory {
  
  private static class ItemStackKey {
    
    final @Nonnull ItemStack wrapped;
    
    ItemStackKey(@Nonnull ItemStack stack) {
      this.wrapped = stack;
    }
    
    @Override
    public boolean equals(Object obj) {
      if (obj == null || obj.getClass() != ItemStackKey.class) {
        return false;
      }
      ItemStack stack = ((ItemStackKey) obj).wrapped;
      return ItemStack.areItemsEqual(wrapped, stack) && ItemStack.areItemStackTagsEqual(wrapped, stack);
    }
    
    @Override
    public int hashCode() {
      return Objects.hash(wrapped.getItem(), wrapped.getItemDamage(), wrapped.getTagCompound());
    }
  }

  private static final @Nonnull List<UpgradePath> allRecipes = DarkSteelRecipeManager.getAllRecipes(ItemHelper.getValidItems());

  public static void registerSubtypes(ISubtypeRegistry subtypeRegistry) {
    DarkSteelUpgradeSubtypeInterpreter dsusi = new DarkSteelUpgradeSubtypeInterpreter();
    Set<Item> items = new HashSet<Item>();
    for (ItemStack stack : ItemHelper.getValidItems()) {
      if (stack.getItem() instanceof IDarkSteelItem) {
        items.add(stack.getItem());
      }
    }
    for (Item item : items) {
      if (item != null) {
        subtypeRegistry.registerSubtypeInterpreter(item, dsusi);
      }
    }
  }

  public static void register(IModRegistry registry) {
    registry.addRecipeCatalyst(new ItemStack(blockDarkSteelAnvil.getBlockNN()), VanillaRecipeCategoryUid.ANVIL);
    registry.addRecipeRegistryPlugin(new IRecipeRegistryPlugin() {
      
      @SuppressWarnings("unchecked")
      @Override
      public @Nonnull <T extends IRecipeWrapper, V> List<T> getRecipeWrappers(@Nonnull IRecipeCategory<T> recipeCategory, @Nonnull IFocus<V> focus) {
        if (recipeCategory.getUid().equals(VanillaRecipeCategoryUid.ANVIL) && focus.getValue() instanceof ItemStack) {
          Set<UpgradePath> recipes = new HashSet<>();
          ItemStack focusStack = (ItemStack) focus.getValue();
          if (focus.getMode() == Mode.INPUT) {
            DarkSteelRecipeManager.getRecipes(recipes, new NNList<>(focusStack));
            recipes.addAll(NullHelper.notnullJ(
                allRecipes.stream().filter(rec -> ItemStack.areItemsEqual(rec.getUpgrade(), focusStack)).collect(Collectors.toSet()),
                "Stream#collect"));
          } else {
            Map<ItemStackKey, List<UpgradePath>> byUpgrade = NullHelper.notnullJ(
                allRecipes.stream().collect(Collectors.groupingBy(rec -> new ItemStackKey(rec.getUpgrade()))),
                "Stream#collect");
            
            final IVanillaRecipeFactory factory = registry.getJeiHelpers().getVanillaRecipeFactory();

            List<IRecipeWrapper> wrappers = new ArrayList<>();
            for (Entry<ItemStackKey, List<UpgradePath>> e : byUpgrade.entrySet()) {
              List<UpgradePath> recs = e.getValue();
              ItemStack upgrade = e.getKey().wrapped;
              List<ItemStack> upgradesRepeated = new ArrayList<>();
              for (int i = 0; i < recs.size(); i++) {
                upgradesRepeated.add(upgrade);
              }
              IRecipeWrapper w = factory.createAnvilRecipe(recs.get(0).getInput(), upgradesRepeated, 
                  NullHelper.notnullJ(recs.stream().map(UpgradePath::getOutput).collect(Collectors.toList()), "Stream#collect"));
              try {
                // Hack pending https://github.com/mezz/JustEnoughItems/pull/1419
                // Force the wrapper's input list to be all items instead of just the first
                ReflectionHelper.<List<List<ItemStack>>, AnvilRecipeWrapper>getPrivateValue(AnvilRecipeWrapper.class, (AnvilRecipeWrapper) w, "inputs").set(0, recs.stream().map(UpgradePath::getInput).collect(Collectors.toList()));
              } catch (Exception ex) {
                // Something changed in JEI internals, we can just fall back to the first input
                Log.LOGGER.debug("Error modifying AnvilRecipeWrapper, falling back to single input...", ex);
              }
              wrappers.add(w);
            }
            return (List<T>) wrappers;
          }
          return getWrappers(recipes);
        }
        return NNList.emptyList();
      }
      
      @Override
      public @Nonnull <T extends IRecipeWrapper> List<T> getRecipeWrappers(@Nonnull IRecipeCategory<T> recipeCategory) {
        if (recipeCategory.getUid().equals(VanillaRecipeCategoryUid.ANVIL)) {
          return getWrappers(allRecipes);
        }
        return NNList.emptyList();
      }
      
      @SuppressWarnings("unchecked")
      private @Nonnull <T extends IRecipeWrapper> List<T> getWrappers(@Nonnull Collection<UpgradePath> recipes) {
        final IVanillaRecipeFactory factory = registry.getJeiHelpers().getVanillaRecipeFactory();
        return (List<T>) NullHelper.notnullJ(recipes.stream()
            .filter(rec -> UpgradeRegistry.getUpgrades().stream().anyMatch(u -> u.isUpgradeItem(rec.getUpgrade()) && u.canAddToItem(rec.getInput(), (IDarkSteelItem) rec.getInput().getItem())))
            .map(rec -> factory.createAnvilRecipe(rec.getInput(), new NNList<>(rec.getUpgrade()), new NNList<>(rec.getOutput())))
            .collect(Collectors.toList()), "Stream#collect");
      }
      
      @Override
      public @Nonnull <V> List<String> getRecipeCategoryUids(@Nonnull IFocus<V> focus) {
        return new NNList<>(VanillaRecipeCategoryUid.ANVIL);
      }
    });
  }
}
