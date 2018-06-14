package crazypants.enderio.machines.integration.jei;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.common.util.NNList;

import crazypants.enderio.base.paint.PaintUtil;
import crazypants.enderio.base.recipe.IMachineRecipe;
import crazypants.enderio.base.recipe.MachineRecipeRegistry;
import crazypants.enderio.base.recipe.painter.AbstractPainterTemplate;
import crazypants.enderio.machines.integration.jei.PainterRecipeCategory.PainterRecipeWrapper;
import crazypants.enderio.util.Prep;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.ingredients.IIngredientRegistry;
import mezz.jei.api.recipe.IFocus;
import mezz.jei.api.recipe.IFocus.Mode;
import mezz.jei.api.recipe.IRecipeCategory;
import mezz.jei.api.recipe.IRecipeRegistryPlugin;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

public class PainterRegistryPlugin implements IRecipeRegistryPlugin {

  private static final WrapperSorter SORTER = new WrapperSorter();

  private static final class WrapperSorter implements Comparator<PainterRecipeWrapper> {
    // get at least some order for our recipes...
    @Override
    public int compare(PainterRecipeWrapper o1, PainterRecipeWrapper o2) {
      int compare = Integer.compare(Item.getIdFromItem(o1.target.getItem()), Item.getIdFromItem(o2.target.getItem()));
      if (compare == 0) {
        compare = Integer.compare(Item.getIdFromItem(o1.paints.get(0).getItem()), Item.getIdFromItem(o2.paints.get(0).getItem()));
      }
      return compare;
    }
  }

  private final @Nonnull NNList<ItemStack> VALID_TARGETS = new NNList<>();
  private final @Nonnull NNList<ItemStack> VALID_PAINTS = new NNList<>();
  private final @Nonnull NNList<ItemStack> SHORT_PAINTS = new NNList<>();
  private final @Nonnull List<PainterRecipeWrapper> staticRecipes;
  private final @Nonnull IIngredientRegistry ingredientRegistry;

  private boolean contains(NNList<ItemStack> list, @Nullable ItemStack itemStack) {
    if (itemStack != null && !itemStack.isEmpty()) {
      for (ItemStack thing : list) {
        if (is(thing, itemStack)) {
          return true;
        }
      }
    }
    return false;
  }

  private boolean is(ItemStack stack0, ItemStack stack1) {
    return stack0.getItem() == stack1.getItem()
        && (!stack0.getHasSubtypes() || stack0.getItemDamage() == OreDictionary.WILDCARD_VALUE || stack0.getItemDamage() == stack1.getItemDamage());
  }

  PainterRegistryPlugin(@Nonnull IModRegistry registry) {
    ingredientRegistry = registry.getIngredientRegistry();
    Collection<ItemStack> validItems = ingredientRegistry.getAllIngredients(ItemStack.class);
    Map<String, IMachineRecipe> recipes = MachineRecipeRegistry.instance.getRecipesForMachine(MachineRecipeRegistry.PAINTER);

    ITEM: for (ItemStack itemStack : validItems) {
      if (itemStack != null) {
        for (IMachineRecipe rec : recipes.values()) {
          if (rec instanceof AbstractPainterTemplate<?>) {
            AbstractPainterTemplate<?> recipe = (AbstractPainterTemplate<?>) rec;
            if (recipe.isValidTarget(itemStack)) {
              VALID_TARGETS.add(itemStack);
              continue ITEM;
            }
          }
        }
      }
    }

    // ITEM: for (ItemStack itemStack : validItems) {
    // if (itemStack != null) {
    // for (IMachineRecipe rec : recipes.values()) {
    // if (rec instanceof AbstractPainterTemplate<?>) {
    // AbstractPainterTemplate<?> recipe = (AbstractPainterTemplate<?>) rec;
    // if (recipe.isPartialRecipe(itemStack, Prep.getEmpty())) {
    // VALID_PAINTS.add(itemStack);
    // continue ITEM;
    // }
    // }
    // }
    // }
    // }

    SHORT_PAINTS.add(new ItemStack(Items.WATER_BUCKET));
    SHORT_PAINTS.add(new ItemStack(Blocks.STONE));
    SHORT_PAINTS.add(new ItemStack(Blocks.RED_FLOWER));
    staticRecipes = buildStaticRecipes();
  }

  private boolean isValidPaint(Map<String, IMachineRecipe> recipes, ItemStack itemStack) {
    if (itemStack != null) {
      if (contains(VALID_PAINTS, itemStack)) {
        return true;
      }
      if (recipes == null) {
        recipes = MachineRecipeRegistry.instance.getRecipesForMachine(MachineRecipeRegistry.PAINTER);
      }
      for (IMachineRecipe rec : recipes.values()) {
        if (rec instanceof AbstractPainterTemplate<?>) {
          AbstractPainterTemplate<?> recipe = (AbstractPainterTemplate<?>) rec;
          if (recipe.isPartialRecipe(itemStack, Prep.getEmpty())) {
            VALID_PAINTS.add(itemStack);
            return true;
          }
        }
      }
    }
    return false;
  }

  @Override
  public @Nonnull <V> List<String> getRecipeCategoryUids(@Nonnull IFocus<V> focus) {
    V value = focus.getValue();
    if (value instanceof ItemStack) {
      ItemStack stack = (ItemStack) value;
      if (focus.getMode() == Mode.INPUT) {
        if (contains(VALID_TARGETS, stack) || isValidPaint(null, stack)) {
          return Collections.singletonList(PainterRecipeCategory.UID);
        }
      } else {
        if (PaintUtil.isPainted(stack) && PaintUtil.hasPaintSource(stack)) {
          return Collections.singletonList(PainterRecipeCategory.UID);
        }
      }
    }
    return Collections.emptyList();
  }

  @SuppressWarnings("unchecked")
  @Override
  public @Nonnull <T extends IRecipeWrapper, V> List<T> getRecipeWrappers(@Nonnull IRecipeCategory<T> recipeCategory, @Nonnull IFocus<V> focus) {
    if (recipeCategory instanceof PainterRecipeCategory) {
      V value = focus.getValue();
      if (value instanceof ItemStack) {
        ItemStack stack = (ItemStack) value;
        List<PainterRecipeWrapper> list1 = new ArrayList<>();
        List<PainterRecipeWrapper> list2 = new ArrayList<>();
        Map<String, IMachineRecipe> recipes = MachineRecipeRegistry.instance.getRecipesForMachine(MachineRecipeRegistry.PAINTER);
        if (focus.getMode() == Mode.INPUT) {

          if (contains(VALID_TARGETS, stack)) {
            ItemStack target = stack;
            for (IMachineRecipe rec : recipes.values()) {
              if (rec instanceof AbstractPainterTemplate<?>) {
                AbstractPainterTemplate<?> recipe = (AbstractPainterTemplate<?>) rec;
                if (recipe.isValidTarget(target)) {
                  PainterRecipeWrapper wrapper = new PainterRecipeWrapper(recipe, target, new ArrayList<ItemStack>(), new ArrayList<ItemStack>());
                  for (ItemStack paint : ingredientRegistry.getAllIngredients(ItemStack.class)) {
                    if (!is(target, paint) && paint != null && recipe.isRecipe(paint, target)) {
                      wrapper.results.add(recipe.getCompletedResult(paint, target));
                      wrapper.paints.add(paint);
                    }
                  }
                  if (!wrapper.results.isEmpty()) {
                    list1.add(wrapper);
                  }
                }
              }
            }
          }

          if (isValidPaint(recipes, stack)) {
            ItemStack paint = stack;
            for (IMachineRecipe rec : recipes.values()) {
              if (rec instanceof AbstractPainterTemplate<?>) {
                AbstractPainterTemplate<?> recipe = (AbstractPainterTemplate<?>) rec;
                for (ItemStack target : VALID_TARGETS) {
                  if (!is(target, paint) && target != null && recipe.isRecipe(paint, target)) {
                    list2.add(new PainterRecipeWrapper(recipe, target, paint, recipe.getCompletedResult(paint, target)));
                  }
                }
              }
            }
          }
        } else { // Mode.OUTPUT
          if (PaintUtil.isPainted(stack) && PaintUtil.hasPaintSource(stack)) {
            ItemStack target = PaintUtil.getOriginalStack(stack);
            ItemStack paint = PaintUtil.getPaintSource(stack);

            for (IMachineRecipe rec : recipes.values()) {
              if (rec instanceof AbstractPainterTemplate<?>) {
                AbstractPainterTemplate<?> recipe = (AbstractPainterTemplate<?>) rec;
                if (recipe.isRecipe(paint, target)) {
                  list1.add(new PainterRecipeWrapper(recipe, target, paint, recipe.getCompletedResult(paint, target)));
                }
              }
            }
          }
        }

        list1.sort(SORTER);
        list2.sort(SORTER);
        list1.addAll(list2);

        return (List<T>) list1;
      }

    }
    return Collections.emptyList();
  }

  @SuppressWarnings("unchecked")
  @Override
  public @Nonnull <T extends IRecipeWrapper> List<T> getRecipeWrappers(@Nonnull IRecipeCategory<T> recipeCategory) {
    if (recipeCategory instanceof PainterRecipeCategory) {
      return (List<T>) staticRecipes;
    }
    return Collections.emptyList();
  }

  private @Nonnull List<PainterRecipeWrapper> buildStaticRecipes() {
    List<PainterRecipeWrapper> list = new ArrayList<>();
    Map<String, IMachineRecipe> recipes = MachineRecipeRegistry.instance.getRecipesForMachine(MachineRecipeRegistry.PAINTER);

    for (IMachineRecipe rec : recipes.values()) {
      if (rec instanceof AbstractPainterTemplate<?>) {
        AbstractPainterTemplate<?> recipe = (AbstractPainterTemplate<?>) rec;
        for (ItemStack target : VALID_TARGETS) {
          if (target != null && recipe.isValidTarget(target)) {
            for (ItemStack paint : SHORT_PAINTS) {
              if (!is(target, paint) && paint != null && recipe.isRecipe(paint, target)) {
                list.add(new PainterRecipeWrapper(recipe, target, paint, recipe.getCompletedResult(paint, target)));
              }
            }
          }
        }
      }
    }

    list.sort(SORTER);
    return list;
  }

}
