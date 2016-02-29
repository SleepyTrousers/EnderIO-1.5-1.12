package crazypants.enderio.conduit.facade;

import crazypants.enderio.BlockEio;
import crazypants.enderio.ModObject;
import crazypants.enderio.TileEntityEio;
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

public class BlockConduitFacade extends BlockEio<TileEntityEio> implements IPaintedBlock {

  public static BlockConduitFacade create() {
    BlockConduitFacade result = new BlockConduitFacade();
    result.init();
    return result;
  }

  private IBlockState blockOverride;

  private BlockConduitFacade() {
    super(ModObject.blockConduitFacade.unlocalisedName, null, new Material(MapColor.stoneColor));
    setStepSound(Block.soundTypeStone);
    setCreativeTab(null);
  }

  @Override
  @SideOnly(Side.CLIENT)
  public int getBlockColor() {
    if(blockOverride != null) {
      return blockOverride.getBlock().getBlockColor();
    } else {
      return super.getBlockColor();
    }
  }
  
  @Override
  @SideOnly(Side.CLIENT)
  public int colorMultiplier(IBlockAccess par1IBlockAccess, BlockPos pos, int renderPass) {
    if(blockOverride != null) {
      try { //work around for Issue #589
        return blockOverride.getBlock().colorMultiplier(par1IBlockAccess, pos, renderPass);
      } catch (Exception e) {
      }
    }
    return super.colorMultiplier(par1IBlockAccess, pos, renderPass);
  }

  public IBlockState getIconOverrideBlock() {
    return blockOverride;
  }

  @Override
  @SideOnly(Side.CLIENT)
  public int getRenderColor(IBlockState bs) {
    if(blockOverride != null) {
      return blockOverride.getBlock().getRenderColor(bs);
    } else {
      return super.getRenderColor(bs);
    }
  }

  public void setBlockOverride(IConduitBundle cb) {
    if(cb == null) {
      blockOverride = null;
      return;
    }

    IBlockState bs = cb.getFacade();    
    if(bs == null || bs.getBlock() == this) {
      return;
    }
    blockOverride = bs;
  }

  @Override
  public int getDamageValue(World par1World, BlockPos pos) {
    Mimic m = getMimic(par1World, pos.getX(), pos.getY(), pos.getZ());
    if(m != null) {
      return m.getMeta();
    }
    return 0;
  }

  private Mimic getMimic(IBlockAccess ba, int x, int y, int z) {
    TileEntity te = ba.getTileEntity(new BlockPos(x, y, z));
    if(!(te instanceof IConduitBundle)) {
      return null;
    }
    IConduitBundle cb = (IConduitBundle) te;
    IBlockState bs = cb.getFacade();
    if(bs == null) {
      return null;
    }

    return new Mimic(bs);
  }

  class Mimic {
    
    IBlockState blockState;

    private Mimic(IBlockState block) {
      this.blockState = block;
    }
    
    int getMeta() {
      if(blockState == null) {
        return 0;
      }
      return blockState.getBlock().getMetaFromState(blockState);
    }

  }

}
