package crazypants.enderio.endergy.alloy;

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

public class ItemEndergyAlloy extends Item implements IHaveRenderers {

  public static ItemEndergyAlloy create(@Nonnull IModObject modObject) {
    return new ItemEndergyAlloy(modObject);
  }

  private ItemEndergyAlloy(@Nonnull IModObject modObject) {
    setHasSubtypes(true);
    setMaxDamage(0);
    setCreativeTab(EnderIOTab.tabEnderIOMaterials);
    modObject.apply(this);
  }

  @Override
  @SideOnly(Side.CLIENT)
  public void registerRenderers(final @Nonnull IModObject modObject) {
    NNList.of(EndergyAlloy.class).apply(new Callback<EndergyAlloy>() {
      @Override
      public void apply(@Nonnull EndergyAlloy alloy) {
        ModelLoader.setCustomModelResourceLocation(ItemEndergyAlloy.this, EndergyAlloy.getMetaFromType(alloy),
            new ModelResourceLocation(modObject.getRegistryName(), "variant=" + alloy.getBaseName()));
      }
    });
  }

  @Override
  public @Nonnull String getUnlocalizedName(@Nonnull ItemStack stack) {
    return getUnlocalizedName() + "_" + EndergyAlloy.getTypeFromMeta(stack.getItemDamage()).getBaseName();
  }

  @Override
  @SideOnly(Side.CLIENT)
  public void getSubItems(@Nonnull CreativeTabs tab, @Nonnull final NonNullList<ItemStack> list) {
    if (isInCreativeTab(tab)) {
      NNList.of(EndergyAlloy.class).apply(new Callback<EndergyAlloy>() {
        @Override
        public void apply(@Nonnull EndergyAlloy alloy) {
          list.add(new ItemStack(ItemEndergyAlloy.this, 1, EndergyAlloy.getMetaFromType(alloy)));
        }
      });
    }
  }

}
