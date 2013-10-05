package crazypants.enderio.machine.light;

import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;
import crazypants.enderio.ModObject;

public class BlockLightNode extends Block implements ITileEntityProvider {

  public static BlockLightNode create() {
    BlockLightNode result = new BlockLightNode();
    result.init();
    return result;
  }

  public BlockLightNode() {
    super(ModObject.blockLightNode.id, Material.rock);
    setHardness(2.0F);
    setStepSound(soundGlassFootstep);
    setUnlocalizedName(ModObject.blockLightNode.unlocalisedName);
    setLightOpacity(0);
    setLightValue(0);
    // setBlockBounds(0.45f, 0.45F, 0.45f, 0.55f, 0.55f, 0.55f);
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
    int blockID = iblockaccess.getBlockId(x, y, z);
    if(blockID == this.blockID) {
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
  public boolean isBlockReplaceable(World world, int x, int y, int z) {
    return true;
  }

  @Override
  public void breakBlock(World world, int x, int y, int z, int par5, int par6) {
    TileLightNode te = (TileLightNode) world.getBlockTileEntity(x, y, z);
    if(te != null) {
      te.onBlockRemoved();
    }
    world.removeBlockTileEntity(x, y, z);
  }

  @Override
  public int getLightValue(IBlockAccess world, int x, int y, int z) {
    Block block = blocksList[world.getBlockId(x, y, z)];
    if(block != null && block.blockID != blockID) {
      return block.getLightValue(world, x, y, z);
    }
    int onVal = 15;
    // TileEntity te = world.getBlockTileEntity(x, y, z);
    // if(te instanceof TileLightNode && ((TileLightNode)te).isDiagnal) {
    // System.out.println("BlockLightNode.getLightValue: ");
    // onVal = 5;
    // }
    return world.getBlockMetadata(x, y, z) > 0 ? onVal : 0;
  }

  @Override
  public void onNeighborBlockChange(World world, int x, int y, int z, int par5) {
    TileLightNode te = (TileLightNode) world.getBlockTileEntity(x, y, z);
    if(te != null) {
      te.onNeighbourChanged();
    }
  }

  protected void init() {
    LanguageRegistry.addName(this, ModObject.blockLightNode.name);
    GameRegistry.registerBlock(this, ModObject.blockLightNode.unlocalisedName);
    GameRegistry.registerTileEntity(TileLightNode.class, ModObject.blockLightNode.unlocalisedName + "TileEntity");
  }

  @Override
  public void registerIcons(IconRegister iconRegister) {
    blockIcon = iconRegister.registerIcon("enderio:blockElectricLightFace");
  }

  @Override
  public TileEntity createNewTileEntity(World world) {
    return new TileLightNode();
  }

}
