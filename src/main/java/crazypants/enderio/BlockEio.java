package crazypants.enderio;

import com.enderio.core.common.BlockEnder;

import crazypants.enderio.api.tool.ITool;
import crazypants.enderio.machine.AbstractMachineEntity;
import crazypants.enderio.tool.ToolUtil;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public abstract class BlockEio<T extends TileEntityEio> extends BlockEnder<T> {

  protected BlockEio(String name, Class<T> teClass) {
    super(name, teClass);
    setCreativeTab(EnderIOTab.tabEnderIO);
  }

  protected BlockEio(String name, Class<T> teClass, Material mat) {
    super(name, teClass, mat);
    setCreativeTab(EnderIOTab.tabEnderIO);
  }
  
  

  @Override
  public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer entityPlayer, EnumFacing side, float hitX, float hitY, float hitZ) {  
    if(shouldWrench(world, pos, entityPlayer, side) && ToolUtil.breakBlockWithTool(this, world, pos, entityPlayer)) {
      return true;
    }
    TileEntity te = world.getTileEntity(pos);

    ITool tool = ToolUtil.getEquippedTool(entityPlayer);
    if(tool != null && !entityPlayer.isSneaking() && tool.canUse(entityPlayer.getCurrentEquippedItem(), entityPlayer, pos)) {
      if(te instanceof AbstractMachineEntity) {
        ((AbstractMachineEntity) te).toggleIoModeForFace(side);
        world.markBlockForUpdate(pos);
        return true;
      }
    }
    
    return super.onBlockActivated(world, pos, state, entityPlayer, side, hitX, hitY, hitZ);
  }
  
  protected boolean shouldWrench(World world, BlockPos pos, EntityPlayer entityPlayer, EnumFacing side) {
    return true;
  }
}
