package crazypants.enderio.machine.light;

import java.util.List;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import crazypants.enderio.EnderIOTab;
import crazypants.enderio.ModObject;
import crazypants.enderio.conduit.IConduitBundle;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.client.particle.EffectRenderer;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Icon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;

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
    //setBlockBounds(0.45f, 0.45F, 0.45f, 0.55f, 0.55f, 0.55f);
    setBlockBounds(0,0,0,0,0,0);
  }

  @Override
  public boolean renderAsNormalBlock() {
    return false;
  }

  @Override
  public void setBlockBoundsBasedOnState(IBlockAccess par1iBlockAccess, int par2, int par3, int par4) {
    setBlockBounds(0,0,0,0,0,0);
  }

  @Override
  public void setBlockBoundsForItemRender() {
    setBlockBounds(0,0,0,0,0,0);
  }

  @Override
  public boolean isBlockSolid(IBlockAccess par1iBlockAccess, int par2, int par3, int par4, int par5) {
    return false;
  }

  @Override
  public boolean isBlockSolidOnSide(World world, int x, int y, int z, ForgeDirection side) {
    return false;
  }

  @Override
  public boolean isBlockReplaceable(World world, int x, int y, int z) {
    return true;
  }

//  @Override
//  public MovingObjectPosition collisionRayTrace(World world, int x, int y,
//      int z, Vec3 origin, Vec3 direction) {
//    return null;
//  }

//  @Override
//  public AxisAlignedBB getSelectedBoundingBoxFromPool(World world, int x, int
//      y, int z) {
//    return null;
//  }

  public boolean canCollideCheck(int par1, boolean par2) {
    return false;
  }

  public boolean isCollidable() {
    return false;
  }

  @Override
  public void addCollisionBoxesToList(World world, int x, int y, int z,
      AxisAlignedBB axisalignedbb, @SuppressWarnings("rawtypes") List arraylist,
      Entity par7Entity) {
    setBlockBounds(0,0,0,0,0,0);
    super.addCollisionBoxesToList(world, x, y, z, axisalignedbb, arraylist,
        par7Entity);
    setBlockBounds(0,0,0,0,0,0);
  }
  

  @Override
  public AxisAlignedBB getCollisionBoundingBoxFromPool(World par1World, int x, int y, int z) {
    return AxisAlignedBB.getBoundingBox(x, y ,z , x, y , z);
  }

  @Override
  @SideOnly(Side.CLIENT)
  public boolean addBlockHitEffects(World worldObj, MovingObjectPosition target, EffectRenderer effectRenderer) {
    return true;
  }

  @Override
  @SideOnly(Side.CLIENT)
  public boolean addBlockDestroyEffects(World world, int x, int y, int z, int meta, EffectRenderer effectRenderer) {
    return true;
  }

  @Override
  public void breakBlock(World world, int x, int y, int z, int par5, int par6) {
    TileLightNode te = (TileLightNode) world.getBlockTileEntity(x, y, z);
    if (te != null) {
      te.onBlockRemoved();
    }
    world.removeBlockTileEntity(x, y, z);
  }

  @Override
  public int getLightValue(IBlockAccess world, int x, int y, int z) {
    Block block = blocksList[world.getBlockId(x, y, z)];
    if (block != null && block != this) {
      return block.getLightValue(world, x, y, z);
    }
    return world.getBlockMetadata(x, y, z) > 0 ? 15 : 0;
  }

  @Override
  public void onNeighborBlockChange(World world, int x, int y, int z, int par5) {
    TileLightNode te = (TileLightNode) world.getBlockTileEntity(x, y, z);
    if (te != null) {
      te.onNeighbourChanged();
    }
  }

  protected void init() {
    LanguageRegistry.addName(this, ModObject.blockLightNode.name);
    GameRegistry.registerBlock(this, ModObject.blockLightNode.unlocalisedName);
    GameRegistry.registerTileEntity(TileLightNode.class, ModObject.blockLightNode.unlocalisedName + "TileEntity");
  }

  @Override
  public boolean isOpaqueCube() {
    return false;
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
