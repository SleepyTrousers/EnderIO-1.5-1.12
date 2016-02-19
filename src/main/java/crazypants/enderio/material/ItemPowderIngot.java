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

public class ItemPowderIngot extends Item {
  
  public static ItemPowderIngot create() {
    ItemPowderIngot mp = new ItemPowderIngot();
    mp.init();
    return mp;
  }

  private ItemPowderIngot() {
    setHasSubtypes(true);
    setMaxDamage(0);
    setCreativeTab(EnderIOTab.tabEnderIO);
    setUnlocalizedName(ModObject.itemPowderIngot.unlocalisedName);

  }

  private void init() {
    GameRegistry.registerItem(this, ModObject.itemPowderIngot.unlocalisedName);
  }

//  @Override
//  @SideOnly(Side.CLIENT)
//  public void registerIcons(IIconRegister IIconRegister) {
//    int numParts = PowderIngot.values().length;
//    for (int i = 0; i < numParts; i++) {
//      icons[i] = IIconRegister.registerIcon(PowderIngot.values()[i].iconKey);
//    }
//  }

  @Override
  public String getUnlocalizedName(ItemStack par1ItemStack) {
    int i = MathHelper.clamp_int(par1ItemStack.getItemDamage(), 0, PowderIngot.values().length - 1);
    return PowderIngot.values()[i].unlocalisedName;
  }

  @Override
  @SideOnly(Side.CLIENT)
  public void getSubItems(Item par1, CreativeTabs par2CreativeTabs, List<ItemStack> par3List) {
    for (int j = 0; j < PowderIngot.values().length; ++j) {
      if(PowderIngot.values()[j].isDependancyMet()) {
        par3List.add(new ItemStack(par1, 1, j));
      }
    }
  }

}
