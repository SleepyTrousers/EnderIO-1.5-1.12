package crazypants.enderio.item.enderface;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import crazypants.enderio.init.IModObject;
import crazypants.enderio.render.IHaveRenderers;
import crazypants.util.ClientUtil;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemEnderface extends Item implements IHaveRenderers {

  public static ItemEnderface create(@Nonnull IModObject modObject) {
    ItemEnderface result = new ItemEnderface(modObject);
    result.init();
    return result;
  }

  protected ItemEnderface(@Nonnull IModObject modObject) {
    setCreativeTab(null);
    setUnlocalizedName(modObject.getUnlocalisedName());
    setRegistryName(modObject.getUnlocalisedName());
    setMaxStackSize(1);
  }

  protected void init() {
    GameRegistry.register(this);
  }

  @Override
  public boolean hasEffect(@Nonnull ItemStack stack) {
    return true;
  }

  @Override
  @SideOnly(Side.CLIENT)
  public void getSubItems(@Nonnull Item itemIn, @Nullable CreativeTabs tab, @Nonnull NonNullList<ItemStack> subItems) {
    if (tab != null) {
      for (int i = 0; i < 4; i++) {
        subItems.add(new ItemStack(itemIn, 1, i));
      }
    }
  }

  @Override
  public void registerRenderers(@Nonnull IModObject modObject) {
    ClientUtil.regRenderer(this, 0, modObject.getUnlocalisedName());
    ClientUtil.regRenderer(this, 1, modObject.getUnlocalisedName() + "_items");
    ClientUtil.regRenderer(this, 2, modObject.getUnlocalisedName() + "_materials");
    ClientUtil.regRenderer(this, 3, modObject.getUnlocalisedName() + "_machines");
  }

}
