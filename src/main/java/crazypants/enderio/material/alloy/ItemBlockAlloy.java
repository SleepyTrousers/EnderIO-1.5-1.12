package crazypants.enderio.material.alloy;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.common.util.NNList;
import com.enderio.core.common.util.NNList.Callback;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

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
  @SideOnly(Side.CLIENT)
  public void getSubItems(@Nonnull final Item item, @Nullable CreativeTabs par2CreativeTabs, @Nonnull final NonNullList<ItemStack> list) {
    NNList.of(Alloy.class).apply(new Callback<Alloy>() {
      @Override
      public void apply(@Nonnull Alloy alloy) {
        list.add(new ItemStack(item, 1, Alloy.getMetaFromType(alloy)));
      }
    });
  }

}
