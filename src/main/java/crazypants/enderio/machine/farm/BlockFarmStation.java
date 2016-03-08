package crazypants.enderio.machine.farm;

import java.util.Random;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import crazypants.enderio.GuiHandler;
import crazypants.enderio.ModObject;
import crazypants.enderio.machine.AbstractMachineBlock;
import crazypants.enderio.machine.AbstractMachineEntity;
import crazypants.enderio.machine.IoMode;
import crazypants.enderio.machine.MachineRenderMapper;
import crazypants.enderio.network.PacketHandler;
import crazypants.enderio.render.BlockStateWrapper;
import crazypants.enderio.render.IOMode;
import crazypants.enderio.render.IRenderMapper;

public class BlockFarmStation extends AbstractMachineBlock<TileFarmStation> {

  @SideOnly(Side.CLIENT)
  private static MachineRenderMapper FARM_MACHINE_RENDER_MAPPER;

  public static BlockFarmStation create() {
    PacketHandler.INSTANCE.registerMessage(PacketFarmAction.class, PacketFarmAction.class, PacketHandler.nextID(), Side.CLIENT);
    PacketHandler.INSTANCE.registerMessage(PacketUpdateNotification.class, PacketUpdateNotification.class, PacketHandler.nextID(), Side.CLIENT);
    PacketHandler.INSTANCE.registerMessage(PacketFarmLockedSlot.class, PacketFarmLockedSlot.class, PacketHandler.nextID(), Side.SERVER);
    BlockFarmStation result = new BlockFarmStation();
    result.init();
    return result;
  }

  protected BlockFarmStation() {
    super(ModObject.blockFarmStation, TileFarmStation.class);
  }

  @Override
  public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
    TileEntity te = world.getTileEntity(new BlockPos(x, y, z));
    if (te instanceof TileFarmStation) {
      return new FarmStationContainer(player.inventory, (TileFarmStation) te);
    }
    return null;
  }

  @Override
  public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
    TileEntity te = world.getTileEntity(new BlockPos(x, y, z));
    if (te instanceof TileFarmStation) {
      return new GuiFarmStation(player.inventory, (TileFarmStation) te);
    }
    return null;
  }

  @Override
  protected int getGuiId() {
    return GuiHandler.GUI_ID_FARM_STATATION;
  }

  @Override
  @SideOnly(Side.CLIENT)
  public IOMode.EnumIOMode mapIOMode(IoMode mode, EnumFacing side) {
    if (side == EnumFacing.UP || side == EnumFacing.DOWN) {
      switch (mode) {
      case NONE:
        return IOMode.EnumIOMode.NONE;
      case PULL:
        return IOMode.EnumIOMode.PULL;
      case PUSH:
        return IOMode.EnumIOMode.PUSH;
      case PUSH_PULL:
        return IOMode.EnumIOMode.PUSHPULL;
      case DISABLED:
        return IOMode.EnumIOMode.DISABLED;
      }
    } else {
      switch (mode) {
      case NONE:
        return IOMode.EnumIOMode.NONE;
      case PULL:
        return IOMode.EnumIOMode.PULLSIDES;
      case PUSH:
        return IOMode.EnumIOMode.PUSHSIDES;
      case PUSH_PULL:
        return IOMode.EnumIOMode.PUSHPULLSIDES;
      case DISABLED:
        return IOMode.EnumIOMode.DISABLEDSIDES;
      }
    }
    throw new RuntimeException("Hey, leave our enums alone!");
  }

  @Override
  public boolean shouldSideBeRendered(IBlockAccess worldIn, BlockPos pos, EnumFacing side) {
    return true;
  }

  @Override
  public boolean isOpaqueCube() {
    return false;
  }

  @Override
  public void randomDisplayTick(World world, BlockPos pos, IBlockState state, Random rand) {
  }

  @Override
  public String getUnlocalizedNameForTooltip(ItemStack stack) {
    return getUnlocalizedName();
  }

  @Override
  @SideOnly(Side.CLIENT)
  public IRenderMapper getRenderMapper() {
    if (FARM_MACHINE_RENDER_MAPPER == null) {
      FARM_MACHINE_RENDER_MAPPER = new FarmingStationRenderMapper();
    }
    return FARM_MACHINE_RENDER_MAPPER;
  }

  @Override
  @SideOnly(Side.CLIENT)
  public boolean canRenderInLayer(EnumWorldBlockLayer layer) {
    return EnumWorldBlockLayer.TRANSLUCENT == layer || EnumWorldBlockLayer.CUTOUT == layer;
  }

  @Override
  public IBlockState getExtendedState(IBlockState state, IBlockAccess world, BlockPos pos) {
    BlockStateWrapper extendedState = (BlockStateWrapper) super.getExtendedState(state, world, pos);
    TileEntity tileEntity = extendedState.getTileEntity();
    if (tileEntity instanceof AbstractMachineEntity) {
      extendedState.setCacheKey(MinecraftForgeClient.getRenderLayer(), ((AbstractMachineEntity) tileEntity).isActive());
    }
    return extendedState;
  }

}
