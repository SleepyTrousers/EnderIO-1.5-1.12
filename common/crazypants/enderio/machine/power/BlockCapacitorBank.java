package crazypants.enderio.machine.power;

import java.util.Random;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Icon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import crazypants.enderio.EnderIO;
import crazypants.enderio.EnderIOTab;
import crazypants.enderio.ModObject;
import crazypants.enderio.conduit.TileConduitBundle;
import crazypants.enderio.machine.AbstractMachineBlock;
import crazypants.enderio.machine.reservoir.TileReservoir;
import crazypants.util.BlockCoord;
import crazypants.vecmath.Vector3d;

public class BlockCapacitorBank extends Block implements ITileEntityProvider {

  public static BlockCapacitorBank create() {
    BlockCapacitorBank res = new BlockCapacitorBank();
    res.init();
    return res;
  }

  Icon overlayIcon;
  Icon fillBarIcon;

  protected BlockCapacitorBank() {
    super(ModObject.blockCapacitorBank.actualId, new Material(MapColor.ironColor));
    setHardness(2.0F);
    setStepSound(soundMetalFootstep);
    setUnlocalizedName(ModObject.blockCapacitorBank.unlocalisedName);
    setCreativeTab(EnderIOTab.tabEnderIO);
  }

  protected void init() {
    LanguageRegistry.addName(this, ModObject.blockCapacitorBank.name);
    GameRegistry.registerBlock(this, ModObject.blockCapacitorBank.unlocalisedName);
    GameRegistry.registerTileEntity(TileCapacitorBank.class, ModObject.blockCapacitorBank.unlocalisedName + "TileEntity");
  }

  @Override
  public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer entityPlayer, int par6, float par7, float par8, float par9) {
    if (entityPlayer.isSneaking()) {
      return false;
    }
    // TODO: Print storage or open GUI?
    return true;
  }

  @SideOnly(Side.CLIENT)
  @Override
  public void registerIcons(IconRegister iconRegister) {
    blockIcon = iconRegister.registerIcon("enderio:capacitorBank");
    overlayIcon = iconRegister.registerIcon("enderio:capacitorBankOverlays");
    fillBarIcon = iconRegister.registerIcon("enderio:capacitorBankFillBar");
  }

  @Override
  public int getRenderType() {
    return -1;
  }
  
  @Override
  public boolean isOpaqueCube() {
    return false;
  }

  @Override
  public boolean renderAsNormalBlock() {
    return false;
  }

  @Override
  public TileEntity createNewTileEntity(World world) {
    return null;
  }

  @Override
  public TileEntity createTileEntity(World world, int metadata) {
    return new TileCapacitorBank();
  }
  
  @Override
  public boolean shouldSideBeRendered(IBlockAccess par1IBlockAccess, int par2, int par3, int par4, int par5) {
    int i1 = par1IBlockAccess.getBlockId(par2, par3, par4);
    return i1 == this.blockID ? false : super.shouldSideBeRendered(par1IBlockAccess, par2, par3, par4, par5);
  }
  
  @Override
  public void onBlockAdded(World world, int x, int y, int z) {
    if (world.isRemote) {
      return;
    }
    TileCapacitorBank tr = (TileCapacitorBank) world.getBlockTileEntity(x, y, z);
    tr.onBlockAdded();

  }

  @Override
  public void onNeighborBlockChange(World world, int x, int y, int z, int blockId) {
    if (world.isRemote) {
      return;
    }
    TileCapacitorBank te = (TileCapacitorBank) world.getBlockTileEntity(x, y, z);
    te.onNeighborBlockChange(blockId);
  }
  
  @Override
  @SideOnly(Side.CLIENT)
  public AxisAlignedBB getSelectedBoundingBoxFromPool(World world, int x, int y, int z) {
    TileEntity te = world.getBlockTileEntity(x, y, z);
    if (!(te instanceof TileCapacitorBank)) {
      return super.getSelectedBoundingBoxFromPool(world, x, y, z);
    }
    TileCapacitorBank tr = (TileCapacitorBank) te;
    if (!tr.isMultiblock()) {
      return super.getSelectedBoundingBoxFromPool(world, x, y, z);
    }

    Vector3d min = new Vector3d(Double.MAX_VALUE, Double.MAX_VALUE, Double.MAX_VALUE);
    Vector3d max = new Vector3d(-Double.MAX_VALUE, -Double.MAX_VALUE, -Double.MAX_VALUE);
    for (BlockCoord bc : tr.multiblock) {
      min.x = Math.min(min.x, bc.x);
      max.x = Math.max(max.x, bc.x + 1);
      min.y = Math.min(min.y, bc.y);
      max.y = Math.max(max.y, bc.y + 1);
      min.z = Math.min(min.z, bc.z);
      max.z = Math.max(max.z, bc.z + 1);
    }
    return AxisAlignedBB.getAABBPool().getAABB(min.x, min.y, min.z, max.x, max.y, max.z);
  }

}
