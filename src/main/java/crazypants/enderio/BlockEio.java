package crazypants.enderio;

import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import com.enderio.core.common.BlockEnder;

import crazypants.enderio.api.tool.ITool;
import crazypants.enderio.machine.AbstractMachineEntity;
import crazypants.enderio.tool.ToolUtil;

public abstract class BlockEio extends BlockEnder {

  protected BlockEio(String name, Class<? extends TileEntityEio> teClass) {
    super(name, teClass);
    setCreativeTab(EnderIOTab.tabEnderIO);
  }

  protected BlockEio(String name, Class<? extends TileEntityEio> teClass, Material mat) {
    super(name, teClass, mat);
    setCreativeTab(EnderIOTab.tabEnderIO);
  }

  @Override
  public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer entityPlayer, int side, float par7, float par8, float par9) {

    if(shouldWrench(world, x, y, z, entityPlayer, side) && ToolUtil.breakBlockWithTool(this, world, x, y, z, entityPlayer)) {
      return true;
    }

    TileEntity te = getTileEntityEio(world, x, y, z);
    if (te != null && !entityPlayer.isSneaking() && ToolUtil.getEquippedTool(entityPlayer) != null) {
      ((AbstractMachineEntity) te).toggleIoModeForFace(ForgeDirection.getOrientation(side));
      world.markBlockForUpdate(x, y, z);
      return true;
    }
    
    return super.onBlockActivated(world, x, y, z, entityPlayer, side, par7, par8, par9);
  }
}
