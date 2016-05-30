package crazypants.enderio.machine.farm.farmers;

import com.enderio.core.common.util.BlockCoord;

import crazypants.enderio.machine.farm.TileFarmStation;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

public class NetherWartFarmer extends CustomSeedFarmer {

  public NetherWartFarmer() {
    super(Blocks.NETHER_WART, 3, new ItemStack(Items.NETHER_WART));
  }

  @Override
  public boolean prepareBlock(TileFarmStation farm, BlockCoord bc, Block block, IBlockState meta) {

    if (!farm.isOpen(bc.getBlockPos())) {
      return false;
    }
    return plantFromInventory(farm, bc);
  }

}
