package crazypants.enderio.machine.solar;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Icon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import buildcraft.api.tools.IToolWrench;
import cpw.mods.fml.common.registry.GameRegistry;
import crazypants.enderio.Config;
import crazypants.enderio.EnderIOTab;
import crazypants.enderio.ModObject;
import crazypants.enderio.conduit.ConduitUtil;

public class BlockSolarPanel extends Block implements ITileEntityProvider {

  public static BlockSolarPanel create() {
    BlockSolarPanel result = new BlockSolarPanel();
    result.init();
    return result;
  }

  private static final float BLOCK_HEIGHT = 0.15f;

  Icon sideIcon;

  private BlockSolarPanel() {
    super(ModObject.blockSolarPanel.id, Material.ground);
    setHardness(0.5F);
    setStepSound(Block.soundStoneFootstep);
    setUnlocalizedName("enderio." + ModObject.blockSolarPanel.name());
    if(Config.photovoltaicCellEnabled) {
      setCreativeTab(EnderIOTab.tabEnderIO);
    } else {
      setCreativeTab(null);
    }
    setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, BLOCK_HEIGHT, 1.0F);
  }

  private void init() {
    GameRegistry.registerBlock(this, ModObject.blockSolarPanel.unlocalisedName);
    GameRegistry.registerTileEntity(TileEntitySolarPanel.class, ModObject.blockSolarPanel.unlocalisedName + "TileEntity");
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
  public TileEntity createNewTileEntity(World world) {
    return new TileEntitySolarPanel();
  }

  @Override
  public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer entityPlayer, int par6, float par7, float par8, float par9) {
    if(ConduitUtil.isToolEquipped(entityPlayer) && entityPlayer.isSneaking()) {
      if(entityPlayer.getCurrentEquippedItem().getItem() instanceof IToolWrench) {
        IToolWrench wrench = (IToolWrench) entityPlayer.getCurrentEquippedItem().getItem();
        if(wrench.canWrench(entityPlayer, x, y, z)) {
          removeBlockByPlayer(world, entityPlayer, x, y, z);
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
  public Icon getIcon(int side, int meta) {
    if(side == ForgeDirection.UP.ordinal()) {
      return blockIcon;
    }
    return sideIcon;
  }

  @Override
  public void onNeighborBlockChange(World world, int x, int y, int z, int par5) {
    TileEntity te = world.getBlockTileEntity(x, y, z);
    if(te instanceof TileEntitySolarPanel) {
      ((TileEntitySolarPanel) te).onNeighborBlockChange();
    }
  }

  @Override
  public void registerIcons(IconRegister iconRegister) {
    blockIcon = iconRegister.registerIcon("enderio:solarPanelTop");
    sideIcon = iconRegister.registerIcon("enderio:solarPanelSide");
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
