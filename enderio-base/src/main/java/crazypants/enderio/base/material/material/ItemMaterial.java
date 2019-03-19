package crazypants.enderio.base.material.material;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.api.client.gui.IResourceTooltipProvider;
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

public class ItemMaterial extends Item implements IHaveRenderers, IResourceTooltipProvider {

  public static ItemMaterial create(@Nonnull IModObject modObject, @Nullable Block block) {
    return new ItemMaterial(modObject);
  }

  private ItemMaterial(@Nonnull IModObject modObject) {
    setHasSubtypes(true);
    setMaxDamage(0);
    setCreativeTab(EnderIOTab.tabEnderIOMaterials);
    modObject.apply(this);
  }

  @Override
  @SideOnly(Side.CLIENT)
  public void registerRenderers(final @Nonnull IModObject modObject) {
    NNList.of(Material.class).apply(new Callback<Material>() {
      @Override
      public void apply(@Nonnull Material alloy) {
        ModelLoader.setCustomModelResourceLocation(ItemMaterial.this, Material.getMetaFromType(alloy),
            new ModelResourceLocation(modObject.getRegistryName(), "variant=" + alloy.getBaseName()));
      }
    });
  }

  @Override
  public @Nonnull String getUnlocalizedName(@Nonnull ItemStack stack) {
    return getUnlocalizedName() + "." + Material.getTypeFromMeta(stack.getItemDamage()).getBaseName();
  }

  @Override
  public void getSubItems(@Nonnull CreativeTabs tab, @Nonnull final NonNullList<ItemStack> list) {
    if (isInCreativeTab(tab)) {
      Material.getActiveMaterials().apply(new Callback<Material>() {
        @Override
        public void apply(@Nonnull Material alloy) {
          list.add(new ItemStack(ItemMaterial.this, 1, Material.getMetaFromType(alloy)));
        }
      });
    }
  }

  @Override
  @SideOnly(Side.CLIENT)
  public boolean hasEffect(@Nonnull ItemStack stack) {
    return Material.getTypeFromMeta(stack.getItemDamage()).hasEffect;
  }

  @Override
  @Nonnull
  public String getUnlocalizedNameForTooltip(@Nonnull ItemStack stack) {
    return getUnlocalizedName(stack);
  }

}
