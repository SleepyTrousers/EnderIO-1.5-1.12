package crazypants.enderio.base.material.alloy.endergy;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.common.util.NNList;
import com.enderio.core.common.util.NNList.Callback;

import crazypants.enderio.api.IModObject;
import crazypants.enderio.base.EnderIOTab;
import crazypants.enderio.base.render.IHaveRenderers;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemEndergyAlloy extends Item implements IHaveRenderers {

  public static ItemEndergyAlloy create(@Nonnull IModObject modObject, @Nullable Block block) {
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
    NNList.of(AlloyEndergy.class).apply(new Callback<AlloyEndergy>() {
      @Override
      public void apply(@Nonnull AlloyEndergy alloy) {
        ModelLoader.setCustomModelResourceLocation(ItemEndergyAlloy.this, AlloyEndergy.getMetaFromType(alloy),
            new ModelResourceLocation(modObject.getRegistryName(), "variant=" + alloy.getBaseName()));
      }
    });
  }

  @Override
  public @Nonnull String getUnlocalizedName(@Nonnull ItemStack stack) {
    return getUnlocalizedName() + "_" + AlloyEndergy.getTypeFromMeta(stack.getItemDamage()).getBaseName();
  }

  @Override
  public void getSubItems(@Nonnull CreativeTabs tab, @Nonnull final NonNullList<ItemStack> list) {
    if (isInCreativeTab(tab)) {
      NNList.of(AlloyEndergy.class).apply(new Callback<AlloyEndergy>() {
        @Override
        public void apply(@Nonnull AlloyEndergy alloy) {
          list.add(new ItemStack(ItemEndergyAlloy.this, 1, AlloyEndergy.getMetaFromType(alloy)));
        }
      });
    }
  }

}
