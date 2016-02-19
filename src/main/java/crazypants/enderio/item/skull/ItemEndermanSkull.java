package crazypants.enderio.item.skull;

import java.util.List;

import crazypants.enderio.EnderIOTab;
import crazypants.enderio.item.skull.BlockEndermanSkull.SkullType;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemEndermanSkull extends ItemBlock {

  public ItemEndermanSkull(Block block) {
    super(block);
    setHasSubtypes(true);
    setMaxDamage(0);
    setCreativeTab(EnderIOTab.tabEnderIO);
  }

  @Override
  public String getUnlocalizedName(ItemStack par1ItemStack) {
    int meta = par1ItemStack.getItemDamage();
    meta = MathHelper.clamp_int(meta, 0, SkullType.values().length - 1);
    return "tile.blockEndermanSkull." + SkullType.values()[meta].name;
  }

  @Override  
  @SideOnly(Side.CLIENT)
  public void getSubItems(Item par1, CreativeTabs par2CreativeTabs, List<ItemStack> par3List) {
    for (int j = 0; j < SkullType.values().length; ++j) {
      if(!SkullType.values()[j].showEyes) {
        par3List.add(new ItemStack(par1, 1, j));
      }
    }
  }

}
