package crazypants.enderio.material;

import java.util.List;

import crazypants.enderio.EnderIO;
import crazypants.enderio.EnderIOTab;
import crazypants.enderio.material.BlockFusedQuartz.Type;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemFusedQuartz extends ItemBlock {

  public ItemFusedQuartz(Block block) {
    super(block);
    setCreativeTab(EnderIOTab.tabEnderIO);
    setMaxDamage(0);
    setHasSubtypes(true);
  }

  @Override
  public String getUnlocalizedName(ItemStack par1ItemStack) {
    int meta = par1ItemStack.getItemDamage();
    Type type = Type.byMeta(meta);
    return "enderio.blockFusedQuartz." + type.unlocalisedName;
  }

  @Override
  @SuppressWarnings({ "rawtypes", "unchecked" })
  @SideOnly(Side.CLIENT)
  public void getSubItems(Item par1, CreativeTabs par2CreativeTabs, List par3List) {
    for (int j = 0; j < BlockFusedQuartz.Type.values().length; ++j) {
      par3List.add(new ItemStack(par1, 1, j));
    }
  }

  @Override
  @SideOnly(Side.CLIENT)
  public void addInformation(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, List<String> par3List, boolean par4) {
    int meta = par1ItemStack.getItemDamage();
    Type type = Type.byMeta(meta);
    if (type.blastResistance) {
      par3List.add(EnderIO.lang.localize("blastResistant"));
    }
    if (type.enlightened) {
      par3List.add(EnderIO.lang.localize("lightEmitter"));
    }
    if (type.lightOpacity > 0) {
      par3List.add(EnderIO.lang.localize("lightBlocker"));
    }
  }
}
