package crazypants.enderio.conduit.facade;

import net.minecraft.block.Block;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import crazypants.enderio.BlockEio;
import crazypants.enderio.ModObject;
import crazypants.enderio.conduit.IConduitBundle;
import crazypants.enderio.machine.painter.IPaintedBlock;

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

  @Override
  @SideOnly(Side.CLIENT)
  public void registerBlockIcons(IIconRegister IIconRegister) {
    blockIcon = IIconRegister.registerIcon("enderio:conduitFacade");
  }

  @Override
  @SideOnly(Side.CLIENT)
  public IIcon getIcon(IBlockAccess ba, int x, int y, int z, int side) {
    TileEntity te = ba.getTileEntity(x, y, z);
    if(!(te instanceof IConduitBundle)) {
      return blockIcon;
    }
    IConduitBundle cb = (IConduitBundle) te;
    Block block = cb.getFacadeId();
    if(block != null) {
      int meta = cb.getFacadeMetadata();
      return block.getIcon(side, meta);
    }
    return blockIcon;
  }

  @Override
  @SideOnly(Side.CLIENT)
  public IIcon getIcon(int par1, int par2) {
    if(blockOverride != null) {
      return blockOverride.getIcon(par1, par2);
    }
    return blockIcon;
  }

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
  public int colorMultiplier(IBlockAccess par1IBlockAccess, int par2, int par3, int par4) {
    if(blockOverride != null) {
      try { //work around for Issue #589
        return blockOverride.colorMultiplier(par1IBlockAccess, par2, par3, par4);
      } catch (Exception e) {
      }
    }
    return super.colorMultiplier(par1IBlockAccess, par2, par3, par4);
  }

  public Block getIconOverrideBlock() {
    return blockOverride;
  }

  @Override
  @SideOnly(Side.CLIENT)
  public int getRenderColor(int par1) {
    if(blockOverride != null) {
      return blockOverride.getRenderColor(par1);
    } else {
      return super.getRenderColor(par1);
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
  public int getDamageValue(World par1World, int x, int y, int z) {
    Mimic m = getMimic(par1World, x, y, z);
    if(m != null) {
      return m.meta;
    }
    return 0;
  }

  private Mimic getMimic(IBlockAccess ba, int x, int y, int z) {
    TileEntity te = ba.getTileEntity(x, y, z);
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
