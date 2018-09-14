package crazypants.enderio.base.material.alloy;

import javax.annotation.Nonnull;

import com.enderio.core.common.util.NNList;
import com.enderio.core.common.util.NNList.Callback;

import crazypants.enderio.api.IModObject;
import crazypants.enderio.base.EnderIOTab;
import crazypants.enderio.base.render.IHaveRenderers;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemAlloy extends Item implements IHaveRenderers {

  public static ItemAlloy create(@Nonnull IModObject modObject) {
    return new ItemAlloy(modObject);
  }

  private ItemAlloy(@Nonnull IModObject modObject) {
    setHasSubtypes(true);
    setMaxDamage(0);
    setCreativeTab(EnderIOTab.tabEnderIOMaterials);
    modObject.apply(this);
  }

  @Override
  @SideOnly(Side.CLIENT)
  public void registerRenderers(final @Nonnull IModObject modObject) {
    NNList.of(Alloy.class).apply(new Callback<Alloy>() {
      @Override
      public void apply(@Nonnull Alloy alloy) {
        ModelLoader.setCustomModelResourceLocation(ItemAlloy.this, Alloy.getMetaFromType(alloy),
            new ModelResourceLocation(modObject.getRegistryName(), "variant=" + alloy.getBaseName()));
      }
    });
  }

  @Override
  public @Nonnull String getUnlocalizedName(@Nonnull ItemStack stack) {
    return getUnlocalizedName() + "_" + Alloy.getTypeFromMeta(stack.getItemDamage()).getBaseName();
  }

  @Override
  public void getSubItems(@Nonnull CreativeTabs tab, @Nonnull final NonNullList<ItemStack> list) {
    if (isInCreativeTab(tab)) {
      NNList.of(Alloy.class).apply(new Callback<Alloy>() {
        @Override
        public void apply(@Nonnull Alloy alloy) {
          list.add(new ItemStack(ItemAlloy.this, 1, Alloy.getMetaFromType(alloy)));
        }
      });
    }
  }

}
