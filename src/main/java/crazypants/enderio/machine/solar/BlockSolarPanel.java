package crazypants.enderio.machine.solar;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import buildcraft.api.tools.IToolWrench;
import crazypants.enderio.Config;
import crazypants.enderio.ModObject;
import crazypants.enderio.conduit.ConduitUtil;
import crazypants.enderio.enderface.BlockEio;

public class BlockSolarPanel extends BlockEio {

  public static BlockSolarPanel create() {
    BlockSolarPanel result = new BlockSolarPanel();
    result.init();
    return result;
  }

  private static final float BLOCK_HEIGHT = 0.15f;

  IIcon sideIcon;

  private BlockSolarPanel() {
    super(ModObject.blockSolarPanel.unlocalisedName, TileEntitySolarPanel.class);
    if(!Config.photovoltaicCellEnabled) {
      setCreativeTab(null);
    }
    setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, BLOCK_HEIGHT, 1.0F);
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
  public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer entityPlayer, int par6, float par7, float par8, float par9) {
    if(ConduitUtil.isToolEquipped(entityPlayer) && entityPlayer.isSneaking()) {
      if(entityPlayer.getCurrentEquippedItem().getItem() instanceof IToolWrench) {
        IToolWrench wrench = (IToolWrench) entityPlayer.getCurrentEquippedItem().getItem();
        if(wrench.canWrench(entityPlayer, x, y, z)) {
          removedByPlayer(world, entityPlayer, x, y, z);
          if(!world.isRemote && !entityPlayer.capabilities.isCreativeMode) {
            dropBlockAsItem(world, x, y, z, 0, 0);
          }
          if(entityPlayer.getCurrentEquippedItem().getItem() instanceof IToolWrench) {
            ((IToolWrench) entityPlayer.getCurrentEquippedItem().getItem()).wrenchUsed(entityPlayer, x, y, z);
          }
          return true;
        }
      }
    }
    return false;
  }

  @Override
  public IIcon getIcon(int side, int meta) {
    if(side == ForgeDirection.UP.ordinal()) {
      return blockIcon;
    }
    return sideIcon;
  }

  @Override
  public void onNeighborBlockChange(World world, int x, int y, int z, Block par5) {
    TileEntity te = world.getTileEntity(x, y, z);
    if(te instanceof TileEntitySolarPanel) {
      ((TileEntitySolarPanel) te).onNeighborBlockChange();
    }
  }

  @Override
  public void registerBlockIcons(IIconRegister IIconRegister) {
    blockIcon = IIconRegister.registerIcon("enderio:solarPanelTop");
    sideIcon = IIconRegister.registerIcon("enderio:solarPanelSide");
  }

  @Override
  public void setBlockBoundsBasedOnState(IBlockAccess par1IBlockAccess, int par2, int par3, int par4) {
    setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, BLOCK_HEIGHT, 1.0F);
  }

  @Override
  public void setBlockBoundsForItemRender() {
    setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, BLOCK_HEIGHT, 1.0F);
  }

  @Override
  public void addCollisionBoxesToList(World par1World, int par2, int par3, int par4, AxisAlignedBB par5AxisAlignedBB, List par6List, Entity par7Entity) {
    setBlockBoundsBasedOnState(par1World, par2, par3, par4);
    super.addCollisionBoxesToList(par1World, par2, par3, par4, par5AxisAlignedBB, par6List, par7Entity);
  }

}
