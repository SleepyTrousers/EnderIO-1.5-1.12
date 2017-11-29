package crazypants.enderio.base.block.darksteel;

import java.util.List;

import javax.annotation.Nonnull;

import crazypants.enderio.base.Lang;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlastResistantItemBlock extends ItemBlock {

  public BlastResistantItemBlock(Block block) {
    super(block);
  }

  @Override
  @SideOnly(Side.CLIENT)
  public void addInformation(@Nonnull ItemStack par1ItemStack, @Nonnull EntityPlayer par2EntityPlayer, @Nonnull List<String> par3List, boolean par4) {
    par3List.add(Lang.BLOCK_BLAST_RESISTANT.get());
  }

}
