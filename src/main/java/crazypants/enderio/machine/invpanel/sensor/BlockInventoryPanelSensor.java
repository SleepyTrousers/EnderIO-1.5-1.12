package crazypants.enderio.machine.invpanel.sensor;

import com.enderio.core.api.client.gui.IResourceTooltipProvider;
import crazypants.enderio.GuiID;
import crazypants.enderio.machine.MachineObject;
import crazypants.enderio.network.PacketHandler;
import crazypants.enderio.paint.IPaintable;
import crazypants.enderio.render.IBlockStateWrapper;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import java.util.Random;

public class BlockInventoryPanelSensor extends AbstractMachineBlock<TileInventoryPanelSensor> implements IResourceTooltipProvider, IPaintable.ISolidBlockPaintableBlock,
IPaintable.IWrenchHideablePaint {

  
  public static BlockInventoryPanelSensor create() {
    
    PacketHandler.INSTANCE.registerMessage(PacketActive.class, PacketActive.class, PacketHandler.nextID(), Side.CLIENT);
    PacketHandler.INSTANCE.registerMessage(PacketItemToCheck.class, PacketItemToCheck.class, PacketHandler.nextID(), Side.SERVER);
    PacketHandler.INSTANCE.registerMessage(PacketItemCount.class, PacketItemCount.class, PacketHandler.nextID(), Side.SERVER);
    
    
    BlockInventoryPanelSensor result = new BlockInventoryPanelSensor();
    result.init();
    return result;
  }
  
  public BlockInventoryPanelSensor() {
    super(MachineObject.blockInventoryPanelSensor, TileInventoryPanelSensor.class);
  }
  
  @Override
  public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
    TileInventoryPanelSensor te = getTileEntity(world, new BlockPos(x, y, z));
    if (te != null) {
      return new ContainerSensor(player.inventory, te);
    }
    return null;
  }

  @Override
  public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
    TileInventoryPanelSensor te = getTileEntity(world, new BlockPos(x, y, z));
    if (te != null) {
      return new GuiSensor(player.inventory, te);
    }
    return null;
  }

  @Override
  public boolean isOpaqueCube(IBlockState bs) {
    return false;
  }

  @Override
  protected GuiID getGuiId() {
    return crazypants.enderio.GuiID.GUI_ID_INVENTORY_PANEL_SENSOR;
  }

  @Override
  protected void setBlockStateWrapperCache(@Nonnull IBlockStateWrapper blockStateWrapper, @Nonnull IBlockAccess world, @Nonnull BlockPos pos,
      @Nonnull TileInventoryPanelSensor tileEntity) {
    blockStateWrapper.addCacheKey(tileEntity.getFacing());
    blockStateWrapper.addCacheKey(tileEntity.isActive());
  }

  @SideOnly(Side.CLIENT)
  @Override
  public void randomDisplayTick(IBlockState bs, World world, BlockPos pos, Random rand) {
  }

  @Deprecated
  @Override
  public int getWeakPower(IBlockState blockStateIn, IBlockAccess blockAccess, BlockPos pos, EnumFacing side) {
    TileInventoryPanelSensor te = getTileEntity(blockAccess, pos);
    if (te != null) {
      int res = te.getIoMode(side.getOpposite()) != IoMode.DISABLED ? te.getRedstoneLevel() : 0;
      return res;
    }
    return super.getWeakPower(blockStateIn, blockAccess, pos, side);
  }

  @Override
  public boolean canProvidePower(IBlockState state) {
    return true;
  }

  @Override
  public boolean canConnectRedstone(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing side) {
    TileInventoryPanelSensor te = getTileEntitySafe(world, pos);
    if (te != null && side != null) {
      return te.getIoMode(side.getOpposite()) != IoMode.DISABLED;
    }
    return super.canConnectRedstone(state, world, pos, side);
  }
}
