package crazypants.enderio.machines.machine.farm;

import java.util.Locale;
import java.util.Random;

import javax.annotation.Nonnull;

import crazypants.enderio.EnderIO;
import crazypants.enderio.GuiID;
import crazypants.enderio.init.IModObject;
import crazypants.enderio.machine.base.block.AbstractMachineBlock;
import crazypants.enderio.machine.modes.IoMode;
import crazypants.enderio.machines.init.MachineObject;
import crazypants.enderio.network.PacketHandler;
import crazypants.enderio.paint.IPaintable;
import crazypants.enderio.render.IBlockStateWrapper;
import crazypants.enderio.render.IHaveTESR;
import crazypants.enderio.render.IRenderMapper;
import crazypants.enderio.render.IRenderMapper.IItemRenderMapper;
import crazypants.enderio.render.property.IOMode;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.server.permission.DefaultPermissionLevel;
import net.minecraftforge.server.permission.PermissionAPI;

public class BlockFarmStation extends AbstractMachineBlock<TileFarmStation>
    implements IPaintable.INonSolidBlockPaintableBlock, IPaintable.IWrenchHideablePaint, IHaveTESR {

  public static BlockFarmStation create() {
    PacketHandler.INSTANCE.registerMessage(PacketFarmAction.class, PacketFarmAction.class, PacketHandler.nextID(), Side.CLIENT);
    PacketHandler.INSTANCE.registerMessage(PacketUpdateNotification.class, PacketUpdateNotification.class, PacketHandler.nextID(), Side.CLIENT);
    PacketHandler.INSTANCE.registerMessage(PacketFarmLockedSlot.class, PacketFarmLockedSlot.class, PacketHandler.nextID(), Side.SERVER);
    BlockFarmStation result = new BlockFarmStation();
    result.init();
    return result;
  }

  protected BlockFarmStation() {
    super(MachineObject.block_farm_station, TileFarmStation.class);
  }

  @Override
  public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
    @SuppressWarnings("null")
    TileFarmStation te = getTileEntity(world, new BlockPos(x, y, z));
    if (te != null) {
      return new FarmStationContainer(player.inventory, te);
    }
    return null;
  }

  @Override
  public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
    @SuppressWarnings("null")
    TileFarmStation te = getTileEntity(world, new BlockPos(x, y, z));
    if (te != null) {
      return new GuiFarmStation(player.inventory, te);
    }
    return null;
  }

  @Override
  protected GuiID getGuiId() {
    return GuiID.GUI_ID_FARM_STATATION;
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
  public boolean shouldSideBeRendered(IBlockState bs, IBlockAccess worldIn, BlockPos pos, EnumFacing side) {
    return true;
  }

  @Override
  public boolean isOpaqueCube(IBlockState bs) {
    return false;
  }

  @Override
  public void randomDisplayTick(IBlockState bs, World world, BlockPos pos, Random rand) {
  }

  @Override
  public String getUnlocalizedNameForTooltip(ItemStack stack) {
    return getUnlocalizedName();
  }

  @Override
  @SideOnly(Side.CLIENT)
  public IItemRenderMapper getItemRenderMapper() {
    return FarmingStationRenderMapper.instance;
  }

  @Override
  @SideOnly(Side.CLIENT)
  public IRenderMapper.IBlockRenderMapper getBlockRenderMapper() {
    return FarmingStationRenderMapper.instance;
  }

  @Override
  protected void setBlockStateWrapperCache(@Nonnull IBlockStateWrapper blockStateWrapper, @Nonnull IBlockAccess world, @Nonnull BlockPos pos,
      @Nonnull TileFarmStation tileEntity) {
    blockStateWrapper.addCacheKey(tileEntity.isActive());
  }

  @Override
  @SideOnly(Side.CLIENT)
  public void bindTileEntitySpecialRenderer() {
    ClientRegistry.bindTileEntitySpecialRenderer(TileFarmStation.class, new FarmingStationSpecialRenderer());
  }

  protected static @Nonnull String permissionFarming = "";

  @Override
  public void init(IModObject mo, FMLInitializationEvent event) {
    super.init(mo, event);
    permissionFarming = PermissionAPI.registerNode(EnderIO.DOMAIN + ".farm." + getUnlocalizedName().toLowerCase(Locale.ENGLISH), DefaultPermissionLevel.ALL,
        "Permission for the block " + getUnlocalizedName() + " of Ender IO to farm land. This includes tilling, planting, harvesting and fertilizing."
            + " Only the base block of a plant will be checked, not the dirt block below it or the additional plant blocks above it."
            + " Note: The GameProfile will be for the block owner, the EntityPlayer in the context will be the fake player.");
  }

}
