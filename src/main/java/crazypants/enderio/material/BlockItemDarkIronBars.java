package crazypants.enderio.material;

import java.util.List;

import crazypants.enderio.EnderIO;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockItemDarkIronBars extends ItemBlock {

  public BlockItemDarkIronBars(Block block) {
    super(block);    
  }
  
  @Override
  @SideOnly(Side.CLIENT)
  public void addInformation(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, List<String> par3List, boolean par4) {
    par3List.add(EnderIO.lang.localize("blastResistant"));    
  }
}
