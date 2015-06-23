package crazypants.enderio.material;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlockWithMetadata;
import net.minecraft.item.ItemStack;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import crazypants.enderio.EnderIO;
import crazypants.enderio.EnderIOTab;
import crazypants.enderio.material.BlockFusedQuartz.Type;

public class ItemFusedQuartz extends ItemBlockWithMetadata {

  public ItemFusedQuartz(Block block) {
    super(block, block);
    setCreativeTab(EnderIOTab.tabEnderIO);
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
  public void addInformation(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, List par3List, boolean par4) {
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
