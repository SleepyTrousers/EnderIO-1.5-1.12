package crazypants.enderio.integration.tic.modifiers;

import java.util.List;
import java.util.Optional;

import com.google.common.collect.ImmutableList;

import crazypants.enderio.base.handler.darksteel.UpgradeRegistry;
import crazypants.enderio.base.item.darksteel.upgrade.direct.DirectUpgrade;
import crazypants.enderio.integration.tic.traits.TraitPickup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import slimeknights.mantle.util.RecipeMatch;

public class TicModifiers {

  public static void register() {
    TraitPickup.instance.addRecipeMatch(new // RecipeMatch.ItemCombination(1, UpgradeRegistry.getUpgradeItem(DirectUpgrade.INSTANCE, false)));
    RecipeMatch(1, 0) {

      @Override
      public List<ItemStack> getInputs() {
        return ImmutableList.of(UpgradeRegistry.getUpgradeItem(DirectUpgrade.INSTANCE, false));
      }

      // Note: Only show the not enabled item in the recipe but accept both variants.

      @Override
      public Optional<Match> matches(NonNullList<ItemStack> stacks) {

        for (ItemStack stack : stacks) {
          if (stack != null && UpgradeRegistry.isUpgradeItem(DirectUpgrade.INSTANCE, stack, null)) {
            ItemStack found = stack.copy();
            found.setCount(1);
            return Optional.of(new Match(ImmutableList.of(found), 1));
          }
        }

        return Optional.empty();
      }
    });
  }

}
