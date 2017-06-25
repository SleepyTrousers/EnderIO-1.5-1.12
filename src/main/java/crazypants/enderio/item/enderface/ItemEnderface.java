package crazypants.enderio.item.enderface;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import crazypants.enderio.EnderIOTab;
import crazypants.enderio.init.IModObject;
import crazypants.enderio.render.IHaveRenderers;
import crazypants.util.Prep;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemEnderface extends Item implements IHaveRenderers {

  public static ItemEnderface create(@Nonnull IModObject modObject) {
    return new ItemEnderface(modObject);
  }

  protected ItemEnderface(@Nonnull IModObject modObject) {
    Prep.setNoCreativeTab(this);
    modObject.apply(this);
    setMaxStackSize(1);
  }

  @Override
  public boolean hasEffect(@Nonnull ItemStack stack) {
    return true;
  }

  @Override
  @SideOnly(Side.CLIENT)
  public void getSubItems(@Nonnull Item itemIn, @Nullable CreativeTabs tab, @Nonnull NonNullList<ItemStack> subItems) {
    if (tab == EnderIOTab.tabNoTab) {
      for (int i = 0; i < 4; i++) {
        subItems.add(new ItemStack(itemIn, 1, i));
      }
    }
  }

  @Override
  public void registerRenderers(@Nonnull IModObject modObject) {
    ModelLoader.setCustomModelResourceLocation(this, 0, new ModelResourceLocation(modObject.getRegistryName(), "variant=none"));
    ModelLoader.setCustomModelResourceLocation(this, 1, new ModelResourceLocation(modObject.getRegistryName(), "variant=items"));
    ModelLoader.setCustomModelResourceLocation(this, 2, new ModelResourceLocation(modObject.getRegistryName(), "variant=materials"));
    ModelLoader.setCustomModelResourceLocation(this, 3, new ModelResourceLocation(modObject.getRegistryName(), "variant=machines"));
  }

}
