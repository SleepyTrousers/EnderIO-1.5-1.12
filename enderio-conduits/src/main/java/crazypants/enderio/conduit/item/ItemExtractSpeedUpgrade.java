package crazypants.enderio.conduit.item;

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

import javax.annotation.Nonnull;

/**
 * Allows item conduits to extract more quickly
 */
public class ItemExtractSpeedUpgrade extends Item implements IResourceTooltipProvider, IHaveRenderers  {

  private static final SpeedUpgrade UPGRADES[] = SpeedUpgrade.values();

  public static ItemExtractSpeedUpgrade create(@Nonnull IModObject modObject) {
    return new ItemExtractSpeedUpgrade(modObject);
  }

  protected ItemExtractSpeedUpgrade(@Nonnull IModObject modObject) {
    setCreativeTab(EnderIOTab.tabEnderIOItems);
    modObject.apply(this);
    setHasSubtypes(true);
    setMaxDamage(0);
    setMaxStackSize(64);
  }

  @Override
  @SideOnly(Side.CLIENT)
  public void registerRenderers(@Nonnull IModObject modObject) {
    for (SpeedUpgrade c : SpeedUpgrade.values()) {
      ClientUtil.regRenderer(this, c.ordinal(), c.baseName);
    }     
  }

  @Override
  public String getUnlocalizedName(@Nonnull ItemStack par1ItemStack) {
    return getSpeedUpgrade(par1ItemStack).unlocName;
  }

  @Override
  @SuppressWarnings({ "rawtypes", "unchecked" })
  @SideOnly(Side.CLIENT)
  public void getSubItems(Item par1, CreativeTabs par2CreativeTabs, NonNullList par3List) {
    for (int j = 0; j < UPGRADES.length; ++j) {
      par3List.add(new ItemStack(par1, 1, j));
    }
  }

  public static SpeedUpgrade getSpeedUpgrade(@Nonnull ItemStack par1ItemStack) {
    int i = MathHelper.clamp(par1ItemStack.getItemDamage(), 0, UPGRADES.length - 1);
    return UPGRADES[i];
  }

  @Override
  public String getUnlocalizedNameForTooltip(@Nonnull ItemStack itemStack) {
    return getUnlocalizedName(itemStack);
  }

}
