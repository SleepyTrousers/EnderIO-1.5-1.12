package crazypants.enderio.base.block.skull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import crazypants.enderio.base.EnderIOTab;
import crazypants.enderio.base.ItemEIO;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.MathHelper;

public class ItemEndermanSkull extends ItemEIO {

  public ItemEndermanSkull(@Nonnull BlockEndermanSkull block) {
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
  public void getSubItems(@Nonnull CreativeTabs tab, @Nonnull NonNullList<ItemStack> list) {
    if (isInCreativeTab(tab)) {
      for (int j = 0; j < SkullType.values().length; ++j) {
        if (!SkullType.values()[j].showEyes()) {
          list.add(new ItemStack(this, 1, j));
        }
      }
    }
  }

  @Override
  @Nullable
  public EntityEquipmentSlot getEquipmentSlot(@Nonnull ItemStack stack) {
    return EntityEquipmentSlot.HEAD;
  }

}
