package crazypants.enderio.base.block.skull;

import javax.annotation.Nonnull;

import crazypants.enderio.base.EnderIOTab;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemEndermanSkull extends ItemBlock {

  public ItemEndermanSkull(@Nonnull Block block) {
    super(block);
    setHasSubtypes(true);
    setMaxDamage(0);
    setCreativeTab(EnderIOTab.tabEnderIO);
  }

  @Override
  public @Nonnull String getUnlocalizedName(@Nonnull ItemStack par1ItemStack) {
    int meta = par1ItemStack.getItemDamage();
    meta = MathHelper.clamp(meta, 0, SkullType.values().length - 1);
    return getUnlocalizedName() + "." + SkullType.values()[meta].getName();
  }

  @Override
  @SideOnly(Side.CLIENT)
  public void getSubItems(@Nonnull CreativeTabs tab, @Nonnull NonNullList<ItemStack> list) {
    if (isInCreativeTab(tab)) {
      for (int j = 0; j < SkullType.values().length; ++j) {
        if (!SkullType.values()[j].showEyes()) {
          list.add(new ItemStack(this, 1, j));
        }
      }
    }
  }

}
