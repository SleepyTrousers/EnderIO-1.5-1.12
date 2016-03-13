package crazypants.enderio.machine.painter;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import crazypants.enderio.render.paint.IPaintable;

public class BlockItemPaintedBlock extends ItemBlock {

  public BlockItemPaintedBlock(Block block) {
    super(block);
    setHasSubtypes(true);
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
  @SideOnly(Side.CLIENT)
  public int getColorFromItemStack(ItemStack stack, int renderPass) {
    if (block instanceof IPaintable) {
      IBlockState paintSource = ((IPaintable) block).getPaintSource(block, stack);
      if (paintSource != null) {
        final ItemStack paintStack = new ItemStack(paintSource.getBlock(), 1, paintSource.getBlock().getMetaFromState(paintSource));
        return paintStack.getItem().getColorFromItemStack(paintStack, renderPass);

        // faster but less compatible:
        // return paintSource.getBlock().getRenderColor(paintSource);
      }
    }
    return super.getColorFromItemStack(stack, renderPass);
  }

}
