package crazypants.enderio.conduit.item;

import javax.annotation.Nonnull;

import com.enderio.core.api.client.gui.IResourceTooltipProvider;

import crazypants.enderio.base.EnderIOTab;
import crazypants.enderio.base.init.IModObject;
import crazypants.enderio.base.render.IHaveRenderers;
import crazypants.enderio.util.ClientUtil;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemFunctionUpgrade extends Item implements IResourceTooltipProvider, IHaveRenderers {

  private static final FunctionUpgrade UPGRADES[] = FunctionUpgrade.values();

  public static ItemFunctionUpgrade create(@Nonnull IModObject modObject) {
    return new ItemFunctionUpgrade(modObject);
  }

  protected ItemFunctionUpgrade(@Nonnull IModObject modObject) {
    setCreativeTab(EnderIOTab.tabEnderIOItems);
    modObject.apply(this);
    setHasSubtypes(true);
    setMaxDamage(0);
    setMaxStackSize(64);

  }

  @Override
  @SideOnly(Side.CLIENT)
  public void registerRenderers(@Nonnull IModObject modObject) {
    for (FunctionUpgrade c : FunctionUpgrade.values()) {
      ClientUtil.regRenderer(this, c.ordinal(), c.baseName);
    }
  }

  @Override
  public @Nonnull String getUnlocalizedName(@Nonnull ItemStack par1ItemStack) {
    return getFunctionUpgrade(par1ItemStack).unlocName;
  }

  @Override
  @SuppressWarnings({ "rawtypes", "unchecked" })
  @SideOnly(Side.CLIENT)
  public void getSubItems(@Nonnull CreativeTabs par2CreativeTabs, @Nonnull NonNullList par3List) {
    if (isInCreativeTab(par2CreativeTabs)) {
      for (int j = 0; j < UPGRADES.length; ++j) {
        par3List.add(new ItemStack(this, 1, j));
      }
    }
  }

  public static FunctionUpgrade getFunctionUpgrade(@Nonnull ItemStack par1ItemStack) {
    int i = MathHelper.clamp(par1ItemStack.getItemDamage(), 0, UPGRADES.length - 1);
    return UPGRADES[i];
  }

  @Override
  public @Nonnull String getUnlocalizedNameForTooltip(@Nonnull ItemStack itemStack) {
    return getUnlocalizedName(itemStack);
  }

}
