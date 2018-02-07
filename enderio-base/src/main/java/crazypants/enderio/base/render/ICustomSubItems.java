package crazypants.enderio.base.render;

import javax.annotation.Nonnull;

import com.enderio.core.common.util.NNList;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public interface ICustomSubItems {

  @Nonnull
  NNList<ItemStack> getSubItems();

  default @Nonnull NNList<ItemStack> getSubItems(@Nonnull Block self, int min, int max) {
    NNList<ItemStack> result = new NNList<>();
    for (int i = min; i <= max; i++) {
      result.add(new ItemStack(self, 1, i));
    }
    return result;
  }

  default @Nonnull NNList<ItemStack> getSubItems(@Nonnull Block self, int max) {
    return getSubItems(self, 0, max);
  }

  default @Nonnull NNList<ItemStack> getSubItems(@Nonnull Item self, int min, int max) {
    NNList<ItemStack> result = new NNList<>();
    for (int i = min; i <= max; i++) {
      result.add(new ItemStack(self, 1, i));
    }
    return result;
  }

  default @Nonnull NNList<ItemStack> getSubItems(@Nonnull Item self, int max) {
    return getSubItems(self, 0, max);
  }

}
