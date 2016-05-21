package crazypants.enderio.material;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockItemIngotStorage extends ItemBlock {

  public BlockItemIngotStorage(Block block, String name) {
    super(block);
    setHasSubtypes(true);
    setMaxDamage(0);
    setRegistryName(name);
  }

  @Override
  public String getUnlocalizedName(ItemStack stack) {
    return "tile." + Alloy.values()[stack.getItemDamage()].unlocalisedName;
  }
  
  @Override
  public int getMetadata(int damage) {
    return damage;
  }
    
  @Override
  @SideOnly(Side.CLIENT)
  public void getSubItems(Item item, CreativeTabs tab, List<ItemStack> list) {
    for (Alloy alloy : Alloy.values()) {
      list.add(new ItemStack(this, 1, alloy.ordinal()));
    }
  }
}
