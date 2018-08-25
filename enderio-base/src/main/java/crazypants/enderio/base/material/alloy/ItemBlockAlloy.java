package crazypants.enderio.base.material.alloy;

import javax.annotation.Nonnull;

import com.enderio.core.common.util.NNList;
import com.enderio.core.common.util.NNList.Callback;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

public class ItemBlockAlloy extends ItemBlock {

  public ItemBlockAlloy(@Nonnull Block block) {
    super(block);
    setHasSubtypes(true);
    setMaxDamage(0);
  }

  @Override
  public @Nonnull String getUnlocalizedName(@Nonnull ItemStack stack) {
    return getUnlocalizedName() + "." + Alloy.getTypeFromMeta(stack.getItemDamage()).getBaseName();
  }

  @Override
  public int getMetadata(int damage) {
    return damage;
  }

  @Override
  public void getSubItems(@Nonnull CreativeTabs tab, @Nonnull final NonNullList<ItemStack> list) {
    if (isInCreativeTab(tab)) {
      NNList.of(Alloy.class).apply(new Callback<Alloy>() {
        @Override
        public void apply(@Nonnull Alloy alloy) {
          list.add(new ItemStack(ItemBlockAlloy.this, 1, Alloy.getMetaFromType(alloy)));
        }
      });
    }
  }

}
