package crazypants.enderio.machines.machine.farm;

import java.util.Locale;
import java.util.Random;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import crazypants.enderio.base.EnderIO;
import crazypants.enderio.base.init.IModObject;
import crazypants.enderio.base.machine.base.block.AbstractMachineBlock;
import crazypants.enderio.base.machine.modes.IoMode;
import crazypants.enderio.base.paint.IPaintable;
import crazypants.enderio.base.render.IBlockStateWrapper;
import crazypants.enderio.base.render.IHaveTESR;
import crazypants.enderio.base.render.IRenderMapper;
import crazypants.enderio.base.render.IRenderMapper.IItemRenderMapper;
import crazypants.enderio.base.render.property.IOMode;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
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

  public static BlockFarmStation create(@Nonnull IModObject modObject) {
    BlockFarmStation result = new BlockFarmStation(modObject);
    result.init();
    return result;
  }

  protected BlockFarmStation(@Nonnull IModObject modObject) {
    super(modObject, TileFarmStation.class);
  }

  @Override
  public @Nullable Container getServerGuiElement(@Nonnull EntityPlayer player, @Nonnull World world, @Nonnull BlockPos pos, @Nullable EnumFacing facing,
      int param1, @Nonnull TileFarmStation te) {
    return new FarmStationContainer(player.inventory, te);
  }

  @Override
  @SideOnly(Side.CLIENT)
  public @Nullable GuiScreen getClientGuiElement(@Nonnull EntityPlayer player, @Nonnull World world, @Nonnull BlockPos pos, @Nullable EnumFacing facing,
      int param1, @Nonnull TileFarmStation te) {
    return new GuiFarmStation(player.inventory, te);
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
  public boolean shouldSideBeRendered(@Nonnull IBlockState bs, @Nonnull IBlockAccess worldIn, @Nonnull BlockPos pos, @Nonnull EnumFacing side) {
    return true;
  }

  @Override
  public boolean isOpaqueCube(@Nonnull IBlockState bs) {
    return false;
  }

  @Override
  public void randomDisplayTick(@Nonnull IBlockState bs, @Nonnull World world, @Nonnull BlockPos pos, @Nonnull Random rand) {
  }

  @Override
  public @Nonnull String getUnlocalizedNameForTooltip(@Nonnull ItemStack stack) {
    return getUnlocalizedName();
  }

  @Override
  @SideOnly(Side.CLIENT)
  public @Nonnull IItemRenderMapper getItemRenderMapper() {
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
  public void init(@Nonnull IModObject mo, @Nonnull FMLInitializationEvent event) {
    super.init(mo, event);
    permissionFarming = PermissionAPI.registerNode(EnderIO.DOMAIN + ".farm." + getUnlocalizedName().toLowerCase(Locale.ENGLISH), DefaultPermissionLevel.ALL,
        "Permission for the block " + getUnlocalizedName() + " of Ender IO to farm land. This includes tilling, planting, harvesting and fertilizing."
            + " Only the base block of a plant will be checked, not the dirt block below it or the additional plant blocks above it."
            + " Note: The GameProfile will be for the block owner, the EntityPlayer in the context will be the fake player.");
  }

}
