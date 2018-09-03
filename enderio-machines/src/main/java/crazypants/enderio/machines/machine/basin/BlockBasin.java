package crazypants.enderio.machines.machine.basin;

import javax.annotation.Nullable;

import crazypants.enderio.api.IModObject;
import crazypants.enderio.base.machine.base.block.AbstractCapabilityMachineBlock;
import crazypants.enderio.base.render.IBlockStateWrapper;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockBasin extends AbstractCapabilityMachineBlock<TileBasin> {

  public static BlockBasin create(IModObject mo) {
    BlockBasin ret = new BlockBasin(mo);
    ret.init();
    return ret;
  }
  
  protected BlockBasin(IModObject mo) {
    super(mo);
  }

  @Override
  protected void setBlockStateWrapperCache(IBlockStateWrapper blockStateWrapper, IBlockAccess world, BlockPos pos, TileBasin tileEntity) {}

  @Override
  public @Nullable Container getServerGuiElement(EntityPlayer player, World world, BlockPos pos, EnumFacing facing, int param1, TileBasin te) {
    return new ContainerBasin(player.inventory, te);
  }

  @Override
  public @Nullable GuiScreen getClientGuiElement(EntityPlayer player, World world, BlockPos pos, EnumFacing facing, int param1, TileBasin te) {
    return new GuiBasin(player.inventory, te);
  }
}
