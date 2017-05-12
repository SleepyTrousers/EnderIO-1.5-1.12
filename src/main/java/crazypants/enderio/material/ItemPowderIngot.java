package crazypants.enderio.material;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import crazypants.enderio.EnderIOTab;
import crazypants.enderio.IModObject;
import crazypants.enderio.render.IHaveRenderers;
import crazypants.util.ClientUtil;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemPowderIngot extends Item implements IHaveRenderers {

  public static ItemPowderIngot create(IModObject modObject) {
    ItemPowderIngot mp = new ItemPowderIngot(modObject);
    mp.init();
    return mp;
  }

  private ItemPowderIngot(IModObject modObject) {
    setHasSubtypes(true);
    setMaxDamage(0);
    setCreativeTab(EnderIOTab.tabEnderIOMaterials);
    setUnlocalizedName(modObject.getUnlocalisedName());
    setRegistryName(modObject.getUnlocalisedName());
  }

  private void init() {
    GameRegistry.register(this);
  }

  @SuppressWarnings("null")
  @Override
  @SideOnly(Side.CLIENT)
  public void registerRenderers() {
    List<ResourceLocation> names = PowderIngot.resources();
    ModelBakery.registerItemVariants(this, names.toArray(new ResourceLocation[names.size()]));
    for (PowderIngot c : PowderIngot.values()) {
      ClientUtil.regRenderer(this, c.ordinal(), c.getBaseName());
    }
  }

  @Override
  public @Nonnull String getUnlocalizedName(@Nonnull ItemStack par1ItemStack) {
    int i = MathHelper.clamp(par1ItemStack.getItemDamage(), 0, PowderIngot.values().length - 1);
    return PowderIngot.values()[i].getUnlocalisedName();
  }

  @Override
  @SideOnly(Side.CLIENT)
  public void getSubItems(@Nonnull Item itemIn, @Nullable CreativeTabs tab, @Nonnull NonNullList<ItemStack> subItems) {
    for (int j = 0; j < PowderIngot.values().length; ++j) {
      if (PowderIngot.values()[j].isDependancyMet()) {
        subItems.add(new ItemStack(itemIn, 1, j));
      }
    }
  }

}
