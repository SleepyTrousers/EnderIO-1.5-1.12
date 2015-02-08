package crazypants.enderio.teleport.telepad;

import net.minecraft.block.Block;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import crazypants.enderio.BlockEio;
import crazypants.enderio.ModObject;

public class BlockTelePad extends BlockEio {

  public static BlockTelePad create() {
    BlockTelePad ret = new BlockTelePad();
    ret.init();
    return ret;
  }

  protected BlockTelePad() {
    super(ModObject.blockTelePad.unlocalisedName, TileTelePad.class);
  }

  @Override
  public void onNeighborChange(IBlockAccess world, int x, int y, int z, int tileX, int tileY, int tileZ) {
    super.onNeighborChange(world, x, y, z, tileX, tileY, tileZ);
    ((TileTelePad) world.getTileEntity(x, y, z)).updateConnectedState(true);
  }
}
