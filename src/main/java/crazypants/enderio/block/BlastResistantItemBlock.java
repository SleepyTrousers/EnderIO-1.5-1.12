package crazypants.enderio.block;

import java.util.List;

import crazypants.enderio.EnderIO;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlastResistantItemBlock extends ItemBlock {

  public BlastResistantItemBlock(Block block, String name) {
    super(block);
    setRegistryName(name);
  }

  @Override
  @SideOnly(Side.CLIENT)
  public void addInformation(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, List<String> par3List, boolean par4) {
    par3List.add(EnderIO.lang.localize("blastResistant"));    
  }
}
