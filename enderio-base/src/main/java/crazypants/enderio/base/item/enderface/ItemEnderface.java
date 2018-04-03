package crazypants.enderio.base.item.enderface;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import crazypants.enderio.base.init.IModObject;
import crazypants.enderio.base.render.IHaveRenderers;
import crazypants.enderio.util.Prep;
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
    return (stack.getItemDamage() & ~0xF) == 0;
  }

  @Override
  @SideOnly(Side.CLIENT)
  public void getSubItems(@Nullable CreativeTabs tab, @Nonnull NonNullList<ItemStack> subItems) {
  }

  @Override
  public void registerRenderers(@Nonnull IModObject modObject) {
    registerVariant(modObject, 0, "none");
    registerVariant(modObject, 1, "items");
    registerVariant(modObject, 2, "materials");
    registerVariant(modObject, 3, "machines");
    registerVariant(modObject, 4, "mobs");
    registerVariant(modObject, 5, "conduits");
  }
  
  private void registerVariant(@Nonnull IModObject mo, int meta, String name) {
    ModelLoader.setCustomModelResourceLocation(this, meta, new ModelResourceLocation(mo.getRegistryName(), "variant=" + name));
    // Non-glint version
    ModelLoader.setCustomModelResourceLocation(this, meta | (1 << 4), new ModelResourceLocation(mo.getRegistryName(), "variant=" + name));
  }
}
