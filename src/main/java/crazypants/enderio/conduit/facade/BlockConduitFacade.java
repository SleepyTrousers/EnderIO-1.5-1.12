package crazypants.enderio.conduit.facade;

import crazypants.enderio.BlockEio;
import crazypants.enderio.ModObject;
import crazypants.enderio.conduit.IConduitBundle;
import crazypants.enderio.machine.painter.IPaintedBlock;
import net.minecraft.block.Block;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockConduitFacade extends BlockEio implements IPaintedBlock {

  public static BlockConduitFacade create() {
    BlockConduitFacade result = new BlockConduitFacade();
    result.init();
    return result;
  }

  private Block blockOverride;

  private BlockConduitFacade() {
    super(ModObject.blockConduitFacade.unlocalisedName, null, new Material(MapColor.stoneColor));
    setStepSound(Block.soundTypeStone);
    setCreativeTab(null);
  }

//  @Override
//  @SideOnly(Side.CLIENT)
//  public IIcon getIcon(IBlockAccess ba, int x, int y, int z, int side) {
//    TileEntity te = ba.getTileEntity(x, y, z);
//    if(!(te instanceof IConduitBundle)) {
//      return blockIcon;
//    }
//    IConduitBundle cb = (IConduitBundle) te;
//    Block block = cb.getFacadeId();
//    if(block != null) {
//      int meta = cb.getFacadeMetadata();
//      return block.getIcon(side, meta);
//    }
//    return blockIcon;
//  }
//
//  @Override
//  @SideOnly(Side.CLIENT)
//  public IIcon getIcon(int par1, int par2) {
//    if(blockOverride != null) {
//      return blockOverride.getIcon(par1, par2);
//    }
//    return blockIcon;
//  }

  @Override
  @SideOnly(Side.CLIENT)
  public int getBlockColor() {
    if(blockOverride != null) {
      return blockOverride.getBlockColor();
    } else {
      return super.getBlockColor();
    }
  }
  
  @Override
  @SideOnly(Side.CLIENT)
  public int colorMultiplier(IBlockAccess par1IBlockAccess, BlockPos pos, int renderPass) {
    if(blockOverride != null) {
      try { //work around for Issue #589
        return blockOverride.colorMultiplier(par1IBlockAccess, pos, renderPass);
      } catch (Exception e) {
      }
    }
    return super.colorMultiplier(par1IBlockAccess, pos, renderPass);
  }

  public Block getIconOverrideBlock() {
    return blockOverride;
  }

  @Override
  @SideOnly(Side.CLIENT)
  public int getRenderColor(IBlockState bs) {
    if(blockOverride != null) {
      return blockOverride.getRenderColor(bs);
    } else {
      return super.getRenderColor(bs);
    }
  }

  public void setBlockOverride(IConduitBundle cb) {
    if(cb == null) {
      blockOverride = null;
      return;
    }

    Block block = cb.getFacadeId();
    int meta = cb.getFacadeMetadata();
    if(block == null || block == this) {
      return;
    }
    blockOverride = block;
  }

  @Override
  public int getDamageValue(World par1World, BlockPos pos) {
    Mimic m = getMimic(par1World, pos.getX(), pos.getY(), pos.getZ());
    if(m != null) {
      return m.meta;
    }
    return 0;
  }

  private Mimic getMimic(IBlockAccess ba, int x, int y, int z) {
    TileEntity te = ba.getTileEntity(new BlockPos(x, y, z));
    if(!(te instanceof IConduitBundle)) {
      return null;
    }
    IConduitBundle cb = (IConduitBundle) te;
    Block id = cb.getFacadeId();
    int meta = cb.getFacadeMetadata();

    if(id == null) {
      return null;
    }

    return new Mimic(id, meta);
  }

  class Mimic {

    int meta;
    Block block;

    private Mimic(Block block, int meta) {
      this.block = block;
      this.meta = meta;
    }

  }

}
