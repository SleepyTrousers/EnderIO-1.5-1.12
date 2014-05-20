package crazypants.enderio.machine.light;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import crazypants.enderio.BlockEio;
import crazypants.enderio.ModObject;

public class BlockLightNode extends BlockEio {

  public static BlockLightNode create() {
    BlockLightNode result = new BlockLightNode();
    result.init();
    return result;
  }

  public BlockLightNode() {
    super(ModObject.blockLightNode.unlocalisedName, TileLightNode.class);
    setCreativeTab(null);
    setBlockBounds(0, 0, 0, 0, 0, 0);
  }

  @Override
  public boolean renderAsNormalBlock() {
    return false;
  }

  @Override
  public boolean isOpaqueCube() {
    return false;
  }

  @Override
  public boolean isBlockSolid(IBlockAccess iblockaccess, int x, int y, int z, int l) {
    Block blockID = iblockaccess.getBlock(x, y, z);
    if(blockID == this) {
      return false;
    } else {

      return super.isBlockSolid(iblockaccess, x, y, z, l);
    }
  }

  @Override
  public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z) {
    return null;
  }

  @Override
  public boolean isReplaceable(IBlockAccess world, int x, int y, int z) {
    return true;
  }

  @Override
  public void breakBlock(World world, int x, int y, int z, Block par5, int par6) {
    TileLightNode te = (TileLightNode) world.getTileEntity(x, y, z);
    if(te != null) {
      te.onBlockRemoved();
    }
    world.removeTileEntity(x, y, z);
  }

  @Override
  public int getLightValue(IBlockAccess world, int x, int y, int z) {
    Block block = world.getBlock(x, y, z);
    if(block != null && block != this) {
      return block.getLightValue(world, x, y, z);
    }
    int onVal = 15;
    // TileEntity te = world.getTileEntity(x, y, z);
    // if(te instanceof TileLightNode && ((TileLightNode)te).isDiagnal) {
    // System.out.println("BlockLightNode.getLightValue: ");
    // onVal = 5;
    // }
    return world.getBlockMetadata(x, y, z) > 0 ? onVal : 0;
  }

  @Override
  public void onNeighborBlockChange(World world, int x, int y, int z, Block par5) {
    TileLightNode te = (TileLightNode) world.getTileEntity(x, y, z);
    if(te != null) {
      te.onNeighbourChanged();
    }
  }

  @Override
  public void registerBlockIcons(IIconRegister iIconRegister) {
    blockIcon = iIconRegister.registerIcon("enderio:blockElectricLightFace");
  }

}
