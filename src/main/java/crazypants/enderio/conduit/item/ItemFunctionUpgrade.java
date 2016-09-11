package crazypants.enderio.conduit.item;

import java.util.List;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;

import com.enderio.core.api.client.gui.IResourceTooltipProvider;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import crazypants.enderio.EnderIOTab;
import crazypants.enderio.ModObject;

public class ItemFunctionUpgrade extends Item implements IResourceTooltipProvider {

  private static final FunctionUpgrade UPGRADES[] = FunctionUpgrade.values();

  private final IIcon[] icons;

  public static ItemFunctionUpgrade create() {
    ItemFunctionUpgrade result = new ItemFunctionUpgrade();
    result.init();
    return result;
  }

  protected ItemFunctionUpgrade() {
    setCreativeTab(EnderIOTab.tabEnderIO);
    setUnlocalizedName(ModObject.itemFunctionUpgrade.unlocalisedName);
    setHasSubtypes(true);
    setMaxDamage(0);
    setMaxStackSize(64);

    icons = new IIcon[UPGRADES.length];
  }

  protected void init() {
    GameRegistry.registerItem(this, ModObject.itemFunctionUpgrade.unlocalisedName);
  }

  @Override
  @SideOnly(Side.CLIENT)
  public IIcon getIconFromDamage(int damage) {
    damage = MathHelper.clamp_int(damage, 0, icons.length - 1);
    return icons[damage];
  }

  @Override
  @SideOnly(Side.CLIENT)
  public void registerIcons(IIconRegister iconRegister) {
    for (int i = 0; i < UPGRADES.length; i++) {
      icons[i] = iconRegister.registerIcon(UPGRADES[i].iconName);
    }
  }

  @Override
  public String getUnlocalizedName(ItemStack par1ItemStack) {
    return getFunctionUpgrade(par1ItemStack).unlocName;
  }

  @Override
  @SuppressWarnings({ "rawtypes", "unchecked" })
  @SideOnly(Side.CLIENT)
  public void getSubItems(Item par1, CreativeTabs par2CreativeTabs, List par3List) {
    for (int j = 0; j < UPGRADES.length; ++j) {
      par3List.add(new ItemStack(par1, 1, j));
    }
  }

  public FunctionUpgrade getFunctionUpgrade(ItemStack par1ItemStack) {
    int i = MathHelper.clamp_int(par1ItemStack.getItemDamage(), 0, UPGRADES.length - 1);
    return UPGRADES[i];
  }

  @Override
  public String getUnlocalizedNameForTooltip(ItemStack itemStack) {
    return getUnlocalizedName(itemStack);
  }

}
