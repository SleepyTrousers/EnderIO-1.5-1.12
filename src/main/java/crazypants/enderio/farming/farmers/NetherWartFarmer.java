package crazypants.enderio.farming.farmers;

import crazypants.enderio.farming.IFarmer;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;

public class NetherWartFarmer extends CustomSeedFarmer {

  public NetherWartFarmer() {
    super(Blocks.NETHER_WART, 3, new ItemStack(Items.NETHER_WART));
  }

  @Override
  public boolean prepareBlock(IFarmer farm, BlockPos bc, Block block, IBlockState meta) {
    if (!farm.isOpen(bc)) {
      return false;
    }
    return plantFromInventory(farm, bc);
  }

}
