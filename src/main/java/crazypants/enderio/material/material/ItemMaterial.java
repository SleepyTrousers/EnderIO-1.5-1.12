package crazypants.enderio.material.material;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.common.util.NNList;
import com.enderio.core.common.util.NNList.Callback;

import crazypants.enderio.EnderIOTab;
import crazypants.enderio.IModObject;
import crazypants.enderio.render.IHaveRenderers;
import crazypants.util.ClientUtil;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemMaterial extends Item implements IHaveRenderers {

  public static ItemMaterial create(@Nonnull IModObject modObject) {
    return new ItemMaterial(modObject).init(modObject);
  }

  private ItemMaterial(@Nonnull IModObject modObject) {
    setHasSubtypes(true);
    setMaxDamage(0);
    setCreativeTab(EnderIOTab.tabEnderIOMaterials);
    setRegistryName(modObject.getUnlocalisedName());
  }

  private ItemMaterial init(@Nonnull IModObject modObject) {
    GameRegistry.register(this);
    return this;
  }

  @Override
  @SideOnly(Side.CLIENT)
  public void registerRenderers(@Nonnull IModObject modObject) {
    NNList.of(Material.class).apply(new Callback<Material>() {
      @Override
      public void apply(@Nonnull Material alloy) {
        ClientUtil.regRenderer(ItemMaterial.this, Material.getMetaFromType(alloy), alloy.getBaseName());
      }
    });
  }

  @Override
  public @Nonnull String getUnlocalizedName(@Nonnull ItemStack stack) {
    return Material.getTypeFromMeta(stack.getItemDamage()).getBaseName();
  }

  @Override
  @SideOnly(Side.CLIENT)
  public void getSubItems(@Nonnull final Item item, @Nullable CreativeTabs par2CreativeTabs, @Nonnull final NonNullList<ItemStack> list) {
    Material.getActiveMaterials().apply(new Callback<Material>() {
      @Override
      public void apply(@Nonnull Material alloy) {
        list.add(new ItemStack(item, 1, Material.getMetaFromType(alloy)));
      }
    });
  }


  @Override
  @SideOnly(Side.CLIENT)
  public boolean hasEffect(@Nonnull ItemStack stack) {
    return Material.getTypeFromMeta(stack.getItemDamage()).hasEffect;
  }

}
