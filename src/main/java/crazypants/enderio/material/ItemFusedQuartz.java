package crazypants.enderio.material;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlockWithMetadata;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import crazypants.enderio.EnderIOTab;
import crazypants.util.Lang;

public class ItemFusedQuartz extends ItemBlockWithMetadata {

  public ItemFusedQuartz(Block block) {
    super(block, block);
    setCreativeTab(EnderIOTab.tabEnderIO);
  }

  @Override
  public String getUnlocalizedName(ItemStack par1ItemStack) {
    int meta = par1ItemStack.getItemDamage();
    meta = MathHelper.clamp_int(meta, 0, BlockFusedQuartz.Type.values().length - 1);
    return "enderio.blockFusedQuartz." + BlockFusedQuartz.Type.values()[meta].unlocalisedName;
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
    meta = MathHelper.clamp_int(meta, 0, BlockFusedQuartz.Type.values().length - 1);
    if(meta == 0 || meta == 2) {
      par3List.add(Lang.localize("blastResistant"));
    }
    if(meta > 1) {
      par3List.add(Lang.localize("lightEmitter"));
    }
  }


}