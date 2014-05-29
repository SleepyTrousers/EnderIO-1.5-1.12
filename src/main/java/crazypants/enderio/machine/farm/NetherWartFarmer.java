package crazypants.enderio.machine.farm;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import crazypants.util.BlockCoord;

public class NetherWartFarmer extends SeedFarmer {

  public NetherWartFarmer() {
    super(Blocks.nether_wart, 3, new ItemStack(Items.nether_wart));
  }

  @Override
  public boolean prepareBlock(TileFarmStation farm, BlockCoord bc, Block block, int meta) {

    if(!farm.isAir(bc)) {
      return false;
    }
    World worldObj = farm.getWorldObj();
    BlockCoord grn = bc.getLocation(ForgeDirection.DOWN);
    Block blk = worldObj.getBlock(grn.x, grn.y, grn.z);
    
    return plantFromInventory(farm, bc);
  }


}
