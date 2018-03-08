package crazypants.enderio.base.integration.jei;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;

import com.enderio.core.common.util.NNList;

import crazypants.enderio.base.handler.darksteel.DarkSteelRecipeManager;
import mezz.jei.api.ISubtypeRegistry.ISubtypeInterpreter;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class DarkSteelUpgradeSubtypeInterpreter implements ISubtypeInterpreter {

  @Override
  public @Nonnull String apply(ItemStack itemStack) {
    if (itemStack == null) {
      throw new NullPointerException("You want me to return something Nonnull for a null ItemStack? F.U.");
    }
    String result = DarkSteelRecipeManager.getUpgradesAsString(itemStack);
    Map<Enchantment, Integer> enchantments = EnchantmentHelper.getEnchantments(itemStack);
    final List<Enchantment> keyList = new NNList<>(enchantments.keySet());
    keyList.sort(new Comparator<Enchantment>() {
      @Override
      public int compare(Enchantment o1, Enchantment o2) {
        return safeString(o1).compareTo(safeString(o2));
      }
    });
    for (Enchantment enchantment : keyList) {
      result += "/" + safeString(enchantment);
    }
    return "DS:" + result;
  }

  private @Nonnull String safeString(Enchantment enchantment) {
    final ResourceLocation registryName = enchantment.getRegistryName();
    return registryName != null ? registryName.toString() : "";
  }

}