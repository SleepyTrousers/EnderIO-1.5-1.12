package crazypants.enderio.machine.light;

import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Icon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import crazypants.enderio.EnderIOTab;
import crazypants.enderio.ModObject;
import crazypants.vecmath.Vector3f;

public class BlockElectricLight extends Block implements ITileEntityProvider {

  private static final float BLOCK_HEIGHT = 0.05f;
  private static final float BLOCK_WIDTH = 0.3f;

  private static final float BLOCK_EDGE_MAX = 0.5f + (BLOCK_WIDTH / 2);
  private static final float BLOCK_EDGE_MIN = 0.5f - (BLOCK_WIDTH / 2);

  public static int renderId;

  public static BlockElectricLight create() {
    BlockElectricLight result = new BlockElectricLight();
    result.init();
    return result;
  }

  private Icon blockIconOff;
  private Icon blockIconSide;

  public BlockElectricLight() {
    super(ModObject.blockElectricLight.id, Material.rock);
    setHardness(2.0F);
    setStepSound(soundGlassFootstep);
    setUnlocalizedName("enderio." + ModObject.blockElectricLight.name());
    setCreativeTab(EnderIOTab.tabEnderIO);
    setLightOpacity(0);
    setLightValue(0);
    setBlockBounds(BLOCK_EDGE_MIN, 0.0F, BLOCK_EDGE_MIN, BLOCK_EDGE_MAX, BLOCK_HEIGHT, BLOCK_EDGE_MAX);
  }

  protected void init() {
    GameRegistry.registerBlock(this, ModObject.blockElectricLight.unlocalisedName);
    GameRegistry.registerTileEntity(TileElectricLight.class, ModObject.blockElectricLight.unlocalisedName + "TileEntity");
  }

  @Override
  public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z) {
    return null;
  }

  @Override
  public void registerIcons(IconRegister iconRegister) {
    blockIcon = iconRegister.registerIcon("enderio:blockElectricLightFace");
    blockIconOff = iconRegister.registerIcon("enderio:blockElectricLightFaceOff");
    blockIconSide = iconRegister.registerIcon("enderio:conduitConnector");
  }

  @Override
  @SideOnly(Side.CLIENT)
  public Icon getBlockTexture(IBlockAccess blockAccess, int x, int y, int z, int side) {

    TileEntity te = blockAccess.getBlockTileEntity(x, y, z);
    if(te instanceof TileElectricLight) {
      ForgeDirection onFace = ((TileElectricLight) te).getFace();
      if(side == (onFace.offsetX == 0 ? onFace.getOpposite().ordinal() : onFace.ordinal())) {
        boolean on = blockAccess.getBlockMetadata(x, y, z) != 0;
        return on ? blockIcon : blockIconOff;
      }
      return blockIconSide;
    }
    return getIcon(side, 0);
  }

  @Override
  @SideOnly(Side.CLIENT)
  public Icon getIcon(int side, int par2) {
    if(side == ForgeDirection.DOWN.ordinal()) {
      return blockIcon;
    }
    return blockIconSide;
  }

  @Override
  public boolean renderAsNormalBlock() {
    return false;
  }

  @Override
  public int getRenderType() {
    return renderId;
  }

  @Override
  public int getLightValue(IBlockAccess world, int x, int y, int z) {
    Block block = blocksList[world.getBlockId(x, y, z)];
    if(block != null && block != this) {
      return block.getLightValue(world, x, y, z);
    }
    return world.getBlockMetadata(x, y, z) > 0 ? 15 : 0;
  }

  @Override
  public void setBlockBoundsBasedOnState(IBlockAccess blockAccess, int x, int y, int z) {
    ForgeDirection onFace = ForgeDirection.DOWN;
    TileEntity te = blockAccess.getBlockTileEntity(x, y, z);
    if(te instanceof TileElectricLight) {
      onFace = ((TileElectricLight) te).getFace();
    }

    Vector3f min = new Vector3f();
    Vector3f max = new Vector3f();
    switch (onFace) {
    case UP:
      min.set(BLOCK_EDGE_MIN, 1F - BLOCK_HEIGHT, BLOCK_EDGE_MIN);
      max.set(BLOCK_EDGE_MAX, 1F, BLOCK_EDGE_MAX);
      break;
    case DOWN:
      min.set(BLOCK_EDGE_MIN, 0.0F, BLOCK_EDGE_MIN);
      max.set(BLOCK_EDGE_MAX, BLOCK_HEIGHT, BLOCK_EDGE_MAX);
      break;
    case EAST:
      min.set(1 - BLOCK_HEIGHT, BLOCK_EDGE_MIN, BLOCK_EDGE_MIN);
      max.set(1, BLOCK_EDGE_MAX, BLOCK_EDGE_MAX);
      break;
    case WEST:
      min.set(0, BLOCK_EDGE_MIN, BLOCK_EDGE_MIN);
      max.set(BLOCK_HEIGHT, BLOCK_EDGE_MAX, BLOCK_EDGE_MAX);
      break;
    case NORTH:
      min.set(BLOCK_EDGE_MIN, BLOCK_EDGE_MIN, 0);
      max.set(BLOCK_EDGE_MAX, BLOCK_EDGE_MAX, BLOCK_HEIGHT);
      break;
    case SOUTH:
      min.set(BLOCK_EDGE_MIN, BLOCK_EDGE_MIN, 1 - BLOCK_HEIGHT);
      max.set(BLOCK_EDGE_MAX, BLOCK_EDGE_MAX, 1);
      break;
    case UNKNOWN:
    default:
      min.set(BLOCK_EDGE_MIN, 0.0F, BLOCK_EDGE_MIN);
      max.set(BLOCK_EDGE_MAX, BLOCK_HEIGHT, BLOCK_EDGE_MAX);
      break;
    }

    setBlockBounds(min.x, min.y, min.z, max.x, max.y, max.z);
  }

  @Override
  public void setBlockBoundsForItemRender() {
    setBlockBounds(BLOCK_EDGE_MIN, 0.0F, BLOCK_EDGE_MIN, BLOCK_EDGE_MAX, BLOCK_HEIGHT, BLOCK_EDGE_MAX);
  }

  @Override
  public int onBlockPlaced(World world, int x, int y, int z, int side, float par6, float par7, float par8, int meta) {
    return side;
  }

  @Override
  public void onPostBlockPlaced(World world, int x, int y, int z, int meta) {
    ForgeDirection onFace = ForgeDirection.values()[meta].getOpposite();
    TileEntity te = world.getBlockTileEntity(x, y, z);
    if(te instanceof TileElectricLight) {
      ((TileElectricLight) te).setFace(onFace);
    }
    world.setBlockMetadataWithNotify(x, y, z, 0, 0);
  }

  @Override
  public boolean isOpaqueCube() {
    return false;
  }

  @Override
  public void onNeighborBlockChange(World world, int x, int y, int z, int blockID) {
    TileEntity te = world.getBlockTileEntity(x, y, z);
    if(te instanceof TileElectricLight) {
      ((TileElectricLight) te).onNeighborBlockChange(blockID);
    }
  }

  @Override
  public void breakBlock(World world, int x, int y, int z, int par5, int par6) {
    TileElectricLight te = (TileElectricLight) world.getBlockTileEntity(x, y, z);
    if(te != null) {
      te.onBlockRemoved();
    }
    world.removeBlockTileEntity(x, y, z);
  }

  @Override
  public TileEntity createNewTileEntity(World world) {
    return new TileElectricLight();
  }

}
