package crazypants.enderio.machine.painter.blocks;

import java.util.List;

import crazypants.enderio.paint.PainterUtil2;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockItemPaintedBlock extends ItemBlock {

  public BlockItemPaintedBlock(Block block, String name) {
    super(block);
    setHasSubtypes(true);
    setRegistryName(name);
  }

  @Override
  public int getMetadata(int damage) {
    return damage;
  }

  @Override
  @SideOnly(Side.CLIENT)
  public void addInformation(ItemStack stack, EntityPlayer playerIn, List<String> tooltip, boolean advanced) {
    super.addInformation(stack, playerIn, tooltip, advanced);
    tooltip.add(PainterUtil2.getTooltTipText(stack));
  }

  @Override
  public String getUnlocalizedName(ItemStack stack) {
    if (block instanceof INamedSubBlocks) {
      return ((INamedSubBlocks) block).getUnlocalizedName(stack.getMetadata());
    } else {
      super.getUnlocalizedName(stack);
    }
    return this.block.getUnlocalizedName();
  }

  public static interface INamedSubBlocks {
    String getUnlocalizedName(int meta);
  }

}
