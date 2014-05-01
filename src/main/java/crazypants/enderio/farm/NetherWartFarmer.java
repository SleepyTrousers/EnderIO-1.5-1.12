package crazypants.enderio.farm;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import crazypants.util.BlockCoord;

public class NetherWartFarmer extends DefaultSeedFarmer {

  public NetherWartFarmer() {
    super(Blocks.nether_wart, 3, new ItemStack(Items.nether_wart));
  }

  @Override
  public boolean prepareBlock(TileFarmStation farm, BlockCoord bc, Block block, int meta) {
    if(block != Blocks.air) {
      return false;
    }
    World worldObj = farm.getWorldObj();
    BlockCoord grn = bc.getLocation(ForgeDirection.DOWN);
    Block blk = worldObj.getBlock(grn.x, grn.y, grn.z);
    if(blk == Blocks.soul_sand) {
      System.out.println("NetherWartFarmer.prepareBlock: !!");
    }

    return plantFromInvenetory(farm, bc);
  }


}
