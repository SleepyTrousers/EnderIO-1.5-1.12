package crazypants.enderio.conduit.facade;

import net.minecraft.block.Block;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Icon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import crazypants.enderio.ModObject;
import crazypants.enderio.conduit.IConduitBundle;

public class BlockConduitFacade extends Block {

  public static BlockConduitFacade create() {
    BlockConduitFacade result = new BlockConduitFacade();
    result.init();
    return result;
  }

  private Block blockOverride;

  private BlockConduitFacade() {
    super(ModObject.blockConduitFacade.id, new Material(MapColor.stoneColor));
    setHardness(0.5F);
    setStepSound(Block.soundStoneFootstep);
    setUnlocalizedName(ModObject.blockConduitFacade.unlocalisedName);
    setCreativeTab(null);
  }

  private void init() {
    LanguageRegistry.addName(this, "Utility for Rendering DO NOT USE");
    GameRegistry.registerBlock(this, ModObject.blockConduitFacade.unlocalisedName);
  }

  @Override
  public void registerIcons(IconRegister iconRegister) {
    blockIcon = iconRegister.registerIcon("enderio:conduitFacade");
  }

  @Override
  @SideOnly(Side.CLIENT)
  public Icon getBlockTexture(IBlockAccess ba, int x, int y, int z, int side) {
    TileEntity te = ba.getBlockTileEntity(x, y, z);
    if(!(te instanceof IConduitBundle)) {
      return blockIcon;
    }
    IConduitBundle cb = (IConduitBundle) te;
    int id = cb.getFacadeId();
    int meta = cb.getFacadeMetadata();
    if(id <= 0 || id == blockID) {
      return blockIcon;
    }
    Block block = Block.blocksList[id];
    if(block != null) {
      return block.getIcon(side, meta);
    }
    return blockIcon;
  }

  @Override
  @SideOnly(Side.CLIENT)
  public Icon getIcon(int par1, int par2) {
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
      return blockOverride.colorMultiplier(par1IBlockAccess, par2, par3, par4);
    } else {
      return super.colorMultiplier(par1IBlockAccess, par2, par3, par4);
    }
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

    int id = cb.getFacadeId();
    int meta = cb.getFacadeMetadata();
    if(id <= 0 || id == blockID) {
      return;
    }
    blockOverride = Block.blocksList[id];
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
    TileEntity te = ba.getBlockTileEntity(x, y, z);
    if(!(te instanceof IConduitBundle)) {
      // System.out.println("BlockConduitFacade.getMimic: Not a conduit bundle");
      return null;
    }
    IConduitBundle cb = (IConduitBundle) te;
    int id = cb.getFacadeId();
    int meta = cb.getFacadeMetadata();

    if(id <= 0) {
      return null;
    }

    return new Mimic(id, meta);
  }

  class Mimic {
    int id;
    int meta;
    Block block;

    private Mimic(int id, int meta) {
      super();
      this.id = id;
      this.meta = meta;
      this.block = Block.blocksList[id];
    }

  }

}
