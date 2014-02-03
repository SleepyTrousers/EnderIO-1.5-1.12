package crazypants.enderio.material;

import java.util.List;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Icon;
import net.minecraft.util.MathHelper;
import crazypants.enderio.EnderIO;
import crazypants.enderio.EnderIOTab;
import crazypants.enderio.material.BlockFusedQuartz.Type;

public class ItemFusedQuartz extends ItemBlock {

  public ItemFusedQuartz(int par1) {
    super(par1);
    this.setHasSubtypes(true);
    setCreativeTab(EnderIOTab.tabEnderIO);
  }

  @Override
  public Icon getIconFromDamage(int par1) {
    return EnderIO.blockFusedQuartz.getItemIcon(par1);
  }

  @Override
  public int getMetadata(int par1) {
    return par1;
  }

  public String getUnlocalizedName(ItemStack par1ItemStack) {
    int meta = par1ItemStack.getItemDamage();
    meta = MathHelper.clamp_int(meta, 0, Type.values().length - 1);
    return "enderio.blockFusedQuartz." + Type.values()[meta].unlocalisedName;
  }

  @Override
  @SuppressWarnings({ "rawtypes", "unchecked" })
  public void getSubItems(int par1, CreativeTabs par2CreativeTabs, List par3List) {
    for (int j = 0; j < Type.values().length; ++j) {
      par3List.add(new ItemStack(par1, 1, j));
    }
  }
}