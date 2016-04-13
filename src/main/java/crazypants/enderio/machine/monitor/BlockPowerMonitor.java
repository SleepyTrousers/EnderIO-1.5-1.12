package crazypants.enderio.machine.monitor;

import javax.annotation.Nonnull;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;

import com.enderio.core.common.util.DyeColor;

import crazypants.enderio.GuiHandler;
import crazypants.enderio.ModObject;
import crazypants.enderio.machine.AbstractMachineBlock;
import crazypants.enderio.machine.ContainerNoInv;
import crazypants.enderio.network.PacketHandler;
import crazypants.enderio.paint.IPaintable;
import crazypants.enderio.render.IBlockStateWrapper;

public class BlockPowerMonitor extends AbstractMachineBlock<TilePowerMonitor> implements IPaintable.ISolidBlockPaintableBlock, IPaintable.IWrenchHideablePaint {

  public static BlockPowerMonitor create() {

    PacketHandler.INSTANCE.registerMessage(PacketPowerMonitor.class, PacketPowerMonitor.class, PacketHandler.nextID(), Side.SERVER);
    PacketHandler.INSTANCE.registerMessage(PacketPowerInfo.class, PacketPowerInfo.class, PacketHandler.nextID(), Side.CLIENT);

    BlockPowerMonitor result = new BlockPowerMonitor();
    result.init();
    return result;
  }

  protected BlockPowerMonitor() {
    super(ModObject.blockPowerMonitor, TilePowerMonitor.class);
  }

  @Override
  public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
    TilePowerMonitor te = getTileEntity(world, new BlockPos(x, y, z));
    if (te != null) {
      return new ContainerNoInv(te);
    }
    return null;
  }

  @Override
  public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
    TilePowerMonitor te = getTileEntity(world, new BlockPos(x, y, z));
    if (te != null) {
      return new GuiPowerMonitor(player.inventory, te);
    }
    return null;

  }

  @Override
  protected int getGuiId() {
    return GuiHandler.GUI_ID_POWER_MONITOR;
  }

  @Override
  public boolean canProvidePower() {
    return true;
  }

  @Override
  public int getWeakPower(IBlockAccess world, BlockPos pos, IBlockState state, EnumFacing side) {
    TilePowerMonitor te = getTileEntity(world, pos);
    if (te != null) {
      return te.getRednetOutputValue(side, DyeColor.RED.ordinal());
    }
    return 0;
  }

  @Override
  protected void setBlockStateWrapperCache(@Nonnull IBlockStateWrapper blockStateWrapper, @Nonnull IBlockAccess world, @Nonnull BlockPos pos,
      @Nonnull TilePowerMonitor tileEntity) {
    blockStateWrapper.addCacheKey(tileEntity.getFacing());
  }

}
