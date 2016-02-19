package crazypants.enderio.material;

import java.util.List;

import crazypants.enderio.EnderIOTab;
import crazypants.enderio.ModObject;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemFrankenSkull extends Item {

  public static ItemFrankenSkull create() {
    ItemFrankenSkull alloy = new ItemFrankenSkull();
    alloy.init();
    return alloy;
  }

  private ItemFrankenSkull() {
    setHasSubtypes(true);
    setMaxDamage(0);
    setCreativeTab(EnderIOTab.tabEnderIO);
    setUnlocalizedName(ModObject.itemFrankenSkull.unlocalisedName);
  }

  private void init() {
    GameRegistry.registerItem(this, ModObject.itemFrankenSkull.unlocalisedName);
  }
  
//  @Override
//  @SideOnly(Side.CLIENT)
//  public void registerIcons(IIconRegister IIconRegister) {
//    for (int i = 0; i < icons.length; i++) {
//      icons[i] = IIconRegister.registerIcon(FrankenSkull.values()[i].iconKey);
//    }
//  }

  @Override
  public String getUnlocalizedName(ItemStack par1ItemStack) {
    int i = MathHelper.clamp_int(par1ItemStack.getItemDamage(), 0, FrankenSkull.values().length - 1);
    return FrankenSkull.values()[i].unlocalisedName;
  }

  @Override
  @SideOnly(Side.CLIENT)
  public void getSubItems(Item par1, CreativeTabs par2CreativeTabs, List<ItemStack> par3List) {
    for (int j = 0; j < FrankenSkull.values().length; ++j) {
      par3List.add(new ItemStack(par1, 1, j));
    }
  }

  @Override
  @SideOnly(Side.CLIENT)
  public boolean hasEffect(ItemStack par1ItemStack) {
    int meta = MathHelper.clamp_int(par1ItemStack.getItemDamage(), 0, FrankenSkull.values().length - 1);
    return FrankenSkull.values()[meta].isAnimated;
  }



}
