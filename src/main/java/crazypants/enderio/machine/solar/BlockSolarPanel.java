package crazypants.enderio.machine.solar;

import java.util.List;

import com.enderio.core.api.client.gui.IResourceTooltipProvider;

import crazypants.enderio.BlockEio;
import crazypants.enderio.EnderIO;
import crazypants.enderio.ModObject;
import crazypants.enderio.config.Config;
import crazypants.enderio.waila.IWailaInfoProvider;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class BlockSolarPanel extends BlockEio implements IResourceTooltipProvider, IWailaInfoProvider {

  public static int renderId;

  public static BlockSolarPanel create() {
    BlockSolarPanel result = new BlockSolarPanel();
    result.init();
    return result;
  }

  private static final float BLOCK_HEIGHT = 0.15f;
  
  private BlockSolarPanel() {
    super(ModObject.blockSolarPanel.unlocalisedName, TileEntitySolarPanel.class, BlockItemSolarPanel.class);
    if(!Config.photovoltaicCellEnabled) {
      setCreativeTab(null);
    }
    setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, BLOCK_HEIGHT, 1.0F);
  }

  @Override
  public int damageDropped(IBlockState bs) {
    return getMetaFromState(bs);
  }

  @Override
  public boolean isOpaqueCube() {
    return false;
  }

//  @Override
//  @SideOnly(Side.CLIENT)
//  public IIcon getIcon(int side, int meta) {
//    if(side == ForgeDirection.UP.ordinal()) {
//      return meta == 0 ? blockIcon : advancedIcon;
//    }
//    return meta == 0 ? sideIcon : advancedSideIcon;
//  }
//
//  public IIcon getBorderIcon(int i, int meta) {
//    return meta == 0 ? borderIcon : advancedBorderIcon;
//  }

  @Override
  public int getRenderType() {
    return renderId;
  }

  @Override
  public void onNeighborBlockChange(World world, BlockPos pos, IBlockState state, Block neighborBlock) {
    TileEntity te = world.getTileEntity(pos);
    if(te instanceof TileEntitySolarPanel) {
      ((TileEntitySolarPanel) te).onNeighborBlockChange();
    }
  }

//  @Override
//  @SideOnly(Side.CLIENT)
//  public void registerBlockIcons(IIconRegister register) {
//    blockIcon = register.registerIcon("enderio:solarPanelTop");
//    advancedIcon = register.registerIcon("enderio:solarPanelAdvancedTop");
//    sideIcon = register.registerIcon("enderio:solarPanelSide");
//    advancedSideIcon = register.registerIcon("enderio:solarPanelAdvancedSide");
//    borderIcon = register.registerIcon("enderio:solarPanelBorder");
//    advancedBorderIcon = register.registerIcon("enderio:solarPanelAdvancedBorder");
//  }

  
  
  @Override
  public void setBlockBoundsBasedOnState(IBlockAccess par1IBlockAccess, BlockPos pos) {
    setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, BLOCK_HEIGHT, 1.0F);
  }

  @Override
  public void setBlockBoundsForItemRender() {
    setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, BLOCK_HEIGHT, 1.0F);
  }

  @Override  
  public void addCollisionBoxesToList(World par1World, BlockPos pos, IBlockState state, AxisAlignedBB par5AxisAlignedBB, List<AxisAlignedBB> par6List, Entity par7Entity) {
    setBlockBoundsBasedOnState(par1World, pos);
    super.addCollisionBoxesToList(par1World, pos, state, par5AxisAlignedBB,par6List, par7Entity);
  }

  @Override
  public String getUnlocalizedNameForTooltip(ItemStack itemStack) {
    return getUnlocalizedName();
  }

  @Override
  public void getWailaInfo(List<String> tooltip, EntityPlayer player, World world, int x, int y, int z) {
    TileEntity te = world.getTileEntity(new BlockPos(x, y, z));
    if(te instanceof TileEntitySolarPanel) {
      TileEntitySolarPanel solar = (TileEntitySolarPanel) te;
      float efficiency = solar.calculateLightRatio();
      if(!solar.canSeeSun()) {
        tooltip.add(EnumChatFormatting.RED + EnderIO.lang.localize("tooltip.sunlightBlocked"));
      } else {
        tooltip.add(String.format("%s : %s%.0f%%", EnumChatFormatting.WHITE + EnderIO.lang.localize("tooltip.efficiency") + EnumChatFormatting.RESET,
            EnumChatFormatting.WHITE, efficiency * 100));
      }
    }
  }

  @Override
  public int getDefaultDisplayMask(World world, int x, int y, int z) {
    return 0;
  }
}
