package crazypants.enderio.base.integration.jei;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.common.util.NNList;
import com.enderio.core.common.util.NullHelper;
import com.google.common.collect.Maps;

import crazypants.enderio.base.Log;
import crazypants.enderio.base.handler.darksteel.DarkSteelRecipeManager;
import crazypants.enderio.base.handler.darksteel.DarkSteelRecipeManager.UpgradePath;
import crazypants.enderio.base.init.ModObject;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.recipe.IFocus;
import mezz.jei.api.recipe.IFocus.Mode;
import mezz.jei.api.recipe.IRecipeCategory;
import mezz.jei.api.recipe.IRecipeRegistryPlugin;
import mezz.jei.api.recipe.IRecipeWrapper;
import mezz.jei.api.recipe.IVanillaRecipeFactory;
import mezz.jei.api.recipe.VanillaRecipeCategoryUid;
import mezz.jei.plugins.vanilla.anvil.AnvilRecipeWrapper;
import net.minecraft.init.Blocks;
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

  private static final @Nonnull Map<ItemStackKey, List<UpgradePath>> allRecipes = NullHelper.notnullJ(
      DarkSteelRecipeManager.getAllRecipes(ItemHelper.getValidItems()).stream().collect(Collectors.groupingBy(rec -> new ItemStackKey(rec.getUpgrade()))),
      "Stream#collect");

  public static void register(IModRegistry registry) {
    registry.addRecipeCatalyst(new ItemStack(blockDarkSteelAnvil.getBlockNN()), VanillaRecipeCategoryUid.ANVIL);
    registry.addRecipeRegistryPlugin(new IRecipeRegistryPlugin() {
      
      @Override
      public @Nonnull <T extends IRecipeWrapper, V> List<T> getRecipeWrappers(@Nonnull IRecipeCategory<T> recipeCategory, @Nonnull IFocus<V> focus) {
        if (recipeCategory.getUid().equals(VanillaRecipeCategoryUid.ANVIL) && focus.getValue() instanceof ItemStack) {
          Map<ItemStackKey, List<UpgradePath>> recipes;
          ItemStack focusStack = (ItemStack) focus.getValue();
          if (focus.getMode() == Mode.INPUT) {
            if (focusStack.getItem() == Item.getItemFromBlock(Blocks.ANVIL) || focusStack.getItem() == ModObject.blockDarkSteelAnvil.getItem()) {
              // FIXME JEI provides us no way to easily check if this is ai catalyst lookup, so this is hardcoded for now
              recipes = allRecipes;
            } else {
              Set<UpgradePath> res = new HashSet<>();
              DarkSteelRecipeManager.getRecipes(res, new NNList<>(focusStack));
              recipes = res.stream().collect(Collectors.groupingBy(rec -> new ItemStackKey(rec.getUpgrade())));
            }
          } else {
            Map<ItemStackKey, List<UpgradePath>> temp = new HashMap<>();
            for (Entry<ItemStackKey, List<UpgradePath>> e : allRecipes.entrySet()) {
              temp.put(e.getKey(), e.getValue().stream().filter(u -> u.getOutput().getItem() == focusStack.getItem()).collect(Collectors.toList()));
            }
            recipes = temp;
          }
          if (recipes.isEmpty()) {
            return getWrappers(Collections.singletonList(allRecipes.get(new ItemStackKey(focusStack))));
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
      
      private @Nonnull List<ItemStack> extractRecipeElements(List<UpgradePath> recs, Function<UpgradePath, ItemStack> extractor) {
        return NullHelper.notnullJ(recs.stream().map(extractor).collect(Collectors.toList()), "Stream#collect");
      }
      
      private @Nonnull <T extends IRecipeWrapper> List<T> getWrappers(@Nonnull Map<ItemStackKey, List<UpgradePath>> recipes) {
        return getWrappers(recipes.values());
      }
      
      @SuppressWarnings("unchecked")
      private @Nonnull <T extends IRecipeWrapper> List<T> getWrappers(@Nonnull Collection<List<UpgradePath>> recipes) {
        final IVanillaRecipeFactory factory = registry.getJeiHelpers().getVanillaRecipeFactory();
        
        List<IRecipeWrapper> wrappers = new ArrayList<>();
        for (List<UpgradePath> recs : recipes) {
          if (recs.isEmpty()) {
            continue;
          }
          IRecipeWrapper w = factory.createAnvilRecipe(extractRecipeElements(recs, UpgradePath::getInput), extractRecipeElements(recs, UpgradePath::getUpgrade), extractRecipeElements(recs, UpgradePath::getOutput));
          wrappers.add(w);
        }
        return (List<T>) wrappers;
      }
      
      @Override
      public @Nonnull <V> List<String> getRecipeCategoryUids(@Nonnull IFocus<V> focus) {
        return new NNList<>(VanillaRecipeCategoryUid.ANVIL);
      }
    });
  }
}
