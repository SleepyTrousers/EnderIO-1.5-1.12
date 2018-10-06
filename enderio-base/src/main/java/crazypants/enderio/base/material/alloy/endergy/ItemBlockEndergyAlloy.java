package crazypants.enderio.base.material.alloy.endergy;

import javax.annotation.Nonnull;

import com.enderio.core.common.util.NNList;
import com.enderio.core.common.util.NNList.Callback;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

public class ItemBlockEndergyAlloy extends ItemBlock {

  public ItemBlockEndergyAlloy(@Nonnull Block block) {
    super(block);
    setHasSubtypes(true);
    setMaxDamage(0);
  }

  @Override
  public @Nonnull String getUnlocalizedName(@Nonnull ItemStack stack) {
    return getUnlocalizedName() + "." + AlloyEndergy.getTypeFromMeta(stack.getItemDamage()).getBaseName();
  }

  @Override
  public int getMetadata(int damage) {
    return damage;
  }

  @Override
  public void getSubItems(@Nonnull CreativeTabs tab, @Nonnull final NonNullList<ItemStack> list) {
    if (isInCreativeTab(tab)) {
      NNList.of(AlloyEndergy.class).apply(new Callback<AlloyEndergy>() {
        @Override
        public void apply(@Nonnull AlloyEndergy alloy) {
          list.add(new ItemStack(ItemBlockEndergyAlloy.this, 1, AlloyEndergy.getMetaFromType(alloy)));
        }
      });
    }
  }

}
