package crazypants.enderio.base.integration.jei;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;

import com.enderio.core.common.util.NNList;
import com.enderio.core.common.util.NNList.NNIterator;
import com.enderio.core.common.util.NullHelper;
import com.google.common.collect.Lists;

import crazypants.enderio.api.upgrades.IDarkSteelItem;
import crazypants.enderio.base.Log;
import crazypants.enderio.base.handler.darksteel.DarkSteelRecipeManager;
import crazypants.enderio.base.handler.darksteel.DarkSteelRecipeManager.UpgradePath;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.ISubtypeRegistry;
import mezz.jei.api.recipe.IRecipeWrapper;
import mezz.jei.api.recipe.IVanillaRecipeFactory;
import mezz.jei.api.recipe.VanillaRecipeCategoryUid;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import static crazypants.enderio.base.init.ModObject.blockDarkSteelAnvil;

public class DarkSteelUpgradeRecipeCategory {

  private static final List<UpgradePath> allRecipes = DarkSteelRecipeManager.getAllRecipes(ItemHelper.getValidItems());

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
    long start = System.nanoTime();

    registry.addRecipeCatalyst(new ItemStack(blockDarkSteelAnvil.getBlockNN()), VanillaRecipeCategoryUid.ANVIL);

    NNList<ItemStack> blacklist = new NNList<>();
    for (UpgradePath rec : allRecipes) {
      rec.getOutput().getItem().getSubItems(getCreativeTab(rec), blacklist);
    }

    NNList<ItemStack> seen = new NNList<>();
    for (UpgradePath rec : allRecipes) {
      if (!inList(blacklist, rec.getOutput()) && !inList(seen, rec.getOutput())) {
        seen.add(rec.getOutput());
      }
    }

    int enchantmentRecipes = registerBookEnchantmentRecipes(registry, seen);

    final IVanillaRecipeFactory factory = registry.getJeiHelpers().getVanillaRecipeFactory();
    Collection<IRecipeWrapper> anvilRecipes = NullHelper.notnullJ(allRecipes.stream()
        .map(rec -> factory.createAnvilRecipe(rec.getInput(), Collections.singletonList(rec.getUpgrade()), Collections.singletonList(rec.getOutput())))
        .collect(Collectors.toList()), "Stream#collect");
    registry.addRecipes(anvilRecipes, VanillaRecipeCategoryUid.ANVIL);

    Log.info(String.format(
        "DarkSteelUpgradeRecipeCategory: Added %d dark steel upgrade recipes and %d enchantment recipes for upgradable items to JEI in %.3f seconds.",
        allRecipes.size(), enchantmentRecipes, (System.nanoTime() - start) / 1000000000d));
  }

  @SuppressWarnings("null")
  private static @Nonnull CreativeTabs getCreativeTab(UpgradePath rec) {
    return rec.getOutput().getItem().getCreativeTab();
  }

  private static int registerBookEnchantmentRecipes(@Nonnull IModRegistry registry, @Nonnull NNList<ItemStack> ingredients) {
    int count = 0;
    Collection<Enchantment> enchantments = ForgeRegistries.ENCHANTMENTS.getValuesCollection();
    List<IRecipeWrapper> anvilRecipes = new ArrayList<>();
    IVanillaRecipeFactory factory = registry.getJeiHelpers().getVanillaRecipeFactory();
    for (ItemStack ingredient : ingredients) {
      if (ingredient.isItemEnchantable()) {
        for (Enchantment enchantment : enchantments) {
          if (enchantment.canApply(ingredient)) {
            Item item = ingredient.getItem();
            List<ItemStack> perLevelBooks = Lists.newArrayList();
            List<ItemStack> perLevelOutputs = Lists.newArrayList();
            for (int level = 1; level <= enchantment.getMaxLevel(); level++) {
              Map<Enchantment, Integer> enchMap = Collections.singletonMap(enchantment, level);
              ItemStack bookEnchant = new ItemStack(Items.ENCHANTED_BOOK);
              EnchantmentHelper.setEnchantments(enchMap, bookEnchant);
              if (item.isBookEnchantable(ingredient, bookEnchant)) {
                perLevelBooks.add(bookEnchant);
                ItemStack withEnchant = ingredient.copy();
                EnchantmentHelper.setEnchantments(enchMap, withEnchant);
                perLevelOutputs.add(withEnchant);
              }
            }
            if (!perLevelBooks.isEmpty() && !perLevelOutputs.isEmpty()) {
              anvilRecipes.add(factory.createAnvilRecipe(ingredient, perLevelBooks, perLevelOutputs));
              count++;
            }
          }
        }
      }
    }
    registry.addRecipes(anvilRecipes, VanillaRecipeCategoryUid.ANVIL);
    return count;
  }

  private static boolean inList(@Nonnull NNList<ItemStack> list, @Nonnull ItemStack stack) {
    for (NNIterator<ItemStack> itr = list.fastIterator(); itr.hasNext();) {
      if (ItemStack.areItemStacksEqual(itr.next(), stack)) {
        return true;
      }
    }
    return false;
  }

}
