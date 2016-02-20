package crazypants.enderio.machine.reservoir;

import com.enderio.core.api.client.gui.IResourceTooltipProvider;
import com.enderio.core.common.util.BlockCoord;
import com.enderio.core.common.util.FluidUtil;
import com.enderio.core.common.vecmath.Vector3d;

import crazypants.enderio.BlockEio;
import crazypants.enderio.ModObject;
import crazypants.enderio.tool.ToolUtil;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockReservoir extends BlockEio implements IResourceTooltipProvider {

  public static BlockReservoir create() {
    BlockReservoir result = new BlockReservoir();
    result.init();
    return result;
  }

//  private static enum MbFace {
//    TL("reservoirMbTl"),
//    TR("reservoirMbTr"),
//    BL("reservoirMbBl"),
//    BR("reservoirMbBr"),
//    T("reservoirMbT"),
//    B("reservoirMbB"),
//    L("reservoirMbL"),
//    R("reservoirMbR");
//
//    String iconName;
//
//    private MbFace(String iconName) {
//      this.iconName = iconName;
//    }
//
//  }

//  private IIcon[] mbIcons = new IIcon[8];
  
  //TODO: 1.8: load the sucker
  TextureAtlasSprite switchIcon;

  private BlockReservoir() {
    super(ModObject.blockReservoir.unlocalisedName, TileReservoir.class, Material.rock);
    setStepSound(Block.soundTypeStone);
  }

  
  
  @Override
  public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer entityPlayer, EnumFacing side, float hitX, float hitY,
      float hitZ) {

    TileEntity te;
    if (!entityPlayer.isSneaking() && entityPlayer.inventory.getCurrentItem() != null
        && (te = world.getTileEntity(pos)) instanceof TileReservoir) {
      TileReservoir tank = ((TileReservoir) te).getController();
      if (ToolUtil.isToolEquipped(entityPlayer) && tank.isMultiblock()) {
        tank.setAutoEject(!tank.isAutoEject());
        for (BlockCoord bc : tank.multiblock) {
          world.markBlockForUpdate(bc.getBlockPos());
        }
        return true;
      }
      if (FluidUtil.fillInternalTankFromPlayerHandItem(world, pos, entityPlayer, tank)) {
        return true;
      }
      if (FluidUtil.fillPlayerHandItemFromInternalTank(world, pos, entityPlayer, tank)) {
        return true;
      }
    }
    return super.onBlockActivated(world, pos, state, entityPlayer, side, hitX, hitY, hitZ);
  }

  @Override
  @SideOnly(Side.CLIENT)
  public AxisAlignedBB getSelectedBoundingBox(World world, BlockPos pos) {
    TileEntity te = world.getTileEntity(pos);
    if(!(te instanceof TileReservoir)) {
      return super.getSelectedBoundingBox(world, pos);
    }
    TileReservoir tr = (TileReservoir) te;
    if(!tr.isMultiblock()) {
      return super.getSelectedBoundingBox(world, pos);
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
    return new AxisAlignedBB(min.x, min.y, min.z, max.x, max.y, max.z);

  }

  @Override
  public boolean isOpaqueCube() {
    return false;
  }

  @Override
  public TileEntity createTileEntity(World world, IBlockState metadata) {
    return new TileReservoir();
  }


  @Override
  public void onBlockAdded(World world, BlockPos pos, IBlockState state) {
    if(world.isRemote) {
      return;
    }
    TileReservoir tr = (TileReservoir) world.getTileEntity(pos);
    tr.onBlockAdded();
  }

  @Override
  public void onNeighborBlockChange(World world, BlockPos pos, IBlockState state, Block block) {
    if(world.isRemote) {
      return;
    }
    TileEntity te = world.getTileEntity(pos);
    if(te instanceof TileReservoir) {
      ((TileReservoir) te).onNeighborBlockChange(block);
    }
  }

//  @Override
//  @SideOnly(Side.CLIENT)
//  public void registerBlockIcons(IIconRegister IIconRegister) {
//    blockIcon = IIconRegister.registerIcon("enderio:reservoir");
//    for (MbFace face : MbFace.values()) {
//      mbIcons[face.ordinal()] = IIconRegister.registerIcon("enderio:" + face.iconName);
//    }
//    switchIcon = IIconRegister.registerIcon("enderio:reservoirSwitch");
//  }

  
  
  @Override
  @SideOnly(Side.CLIENT)
  public boolean shouldSideBeRendered(IBlockAccess world, BlockPos pos, EnumFacing side) {
    if (pos.getY() < 0 || pos.getY() >= 256) { // getTileEntity is not safe for out of bounds coords
      return false;
    }

    TileEntity te = world.getTileEntity(pos);
    if(!(te instanceof TileReservoir)) {
      return true;
    }
    TileReservoir tr = (TileReservoir) te;
    if(!tr.isMultiblock()) {
      return true;
    }
    return false;
  }


//  @Override
//  @SideOnly(Side.CLIENT)
//  public IIcon getIcon(IBlockAccess world, int x, int y, int z, int blockSide) {
//    // used to render the block in the world
//    TileEntity te = world.getTileEntity(x, y, z);
//
//    if(!(te instanceof TileReservoir)) {
//      return blockIcon;
//    }
//    TileReservoir tr = (TileReservoir) te;
//    if(!tr.isMultiblock()) {
//      return blockIcon;
//    }
//
//    ForgeDirection side = ForgeDirection.getOrientation(blockSide);
//    Pos pos = tr.pos;
//
//    if(tr.front == side || tr.front == side.getOpposite()) { // 2x2 area
//
//      boolean isRight;
//      if(tr.isVertical()) { // to to flip right and left for back faces of
//        // vertical multiblocks
//        isRight = !pos.isRight(side);
//      } else {
//        isRight = pos.isRight(side);
//      }
//      if(pos.isTop) {
//        return isRight ? mbIcons[MbFace.TR.ordinal()] : mbIcons[MbFace.TL.ordinal()];
//      } else {
//        return isRight ? mbIcons[MbFace.BR.ordinal()] : mbIcons[MbFace.BL.ordinal()];
//      }
//
//    }
//    if(tr.up == side || tr.up == side.getOpposite()) { // up or down face
//      if(tr.isVertical()) {
//        if(tr.right.offsetX != 0) {
//          return pos.isRight ? mbIcons[MbFace.L.ordinal()] : mbIcons[MbFace.R.ordinal()];
//        } else {
//          return pos.isRight ? mbIcons[MbFace.T.ordinal()] : mbIcons[MbFace.B.ordinal()];
//        }
//      } else {
//        if(tr.up == side) {
//          return pos.isRight ? mbIcons[MbFace.L.ordinal()] : mbIcons[MbFace.R.ordinal()];
//        } else {
//          return pos.isRight ? mbIcons[MbFace.R.ordinal()] : mbIcons[MbFace.L.ordinal()];
//        }
//      }
//
//    } else {
//      if(tr.isVertical()) {
//        return pos.isTop ? mbIcons[MbFace.T.ordinal()] : mbIcons[MbFace.B.ordinal()];
//      } else {
//        return pos.isTop(side) ? mbIcons[MbFace.L.ordinal()] : mbIcons[MbFace.R.ordinal()];
//      }
//    }
//
//  }

  @Override
  public String getUnlocalizedNameForTooltip(ItemStack stack) {
    return getUnlocalizedName();
  }

}
