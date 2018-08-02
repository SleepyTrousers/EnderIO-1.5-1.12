package crazypants.enderio.machines.machine.wireless;

import java.util.Locale;

import javax.annotation.Nonnull;

import com.enderio.core.api.client.gui.IResourceTooltipProvider;
import com.enderio.core.client.render.BoundingBox;
import com.enderio.core.common.util.NullHelper;

import crazypants.enderio.api.IModObject;
import crazypants.enderio.base.render.IHaveRenderers;
import crazypants.enderio.machines.config.config.ChargerConfig;
import crazypants.enderio.machines.init.MachineObject;
import crazypants.enderio.util.ClientUtil;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

public class BlockEnhancedWirelessCharger extends BlockNormalWirelessCharger implements IResourceTooltipProvider, IHaveRenderers {

  static enum E implements IStringSerializable {
    DISH,
    CONNECTOR;

    @Override
    public @Nonnull String getName() {
      return NullHelper.notnullJ(name().toLowerCase(Locale.ENGLISH), "String.toLowerCase()");
    }
  }

  public static final @Nonnull PropertyEnum<E> PUP = PropertyEnum.create("up", E.class);
  public static final @Nonnull PropertyEnum<E> PEAST = PropertyEnum.create("east", E.class);
  public static final @Nonnull PropertyEnum<E> PWEST = PropertyEnum.create("west", E.class);
  public static final @Nonnull PropertyEnum<E> PNORTH = PropertyEnum.create("north", E.class);
  public static final @Nonnull PropertyEnum<E> PSOUTH = PropertyEnum.create("south", E.class);

  public static BlockEnhancedWirelessCharger create(@Nonnull IModObject modObject) {
    BlockEnhancedWirelessCharger res = new BlockEnhancedWirelessCharger(modObject);
    res.init();
    return res;
  }

  private BlockEnhancedWirelessCharger(@Nonnull IModObject modObject) {
    super(modObject);
    setDefaultState(getBlockState().getBaseState().withProperty(PUP, E.DISH).withProperty(PEAST, E.DISH).withProperty(PWEST, E.DISH)
        .withProperty(PNORTH, E.DISH).withProperty(PSOUTH, E.DISH));
  }

  @Override
  @Nonnull
  protected BlockStateContainer createBlockState() {
    return new BlockStateContainer(this, new IProperty[] { PUP, PEAST, PWEST, PNORTH, PSOUTH });
  }

  private boolean canConnect(@Nonnull IBlockAccess world, @Nonnull BlockPos pos, @Nonnull EnumFacing dir) {
    IBlockState other = world.getBlockState(pos.offset(dir));
    return other.getBlock() == MachineObject.block_wireless_charger_extension.getBlockNN() && other.getValue(BlockAntenna.BASE) == dir.getOpposite();
  }

  @Override
  @Nonnull
  public IBlockState getActualState(@Nonnull IBlockState state, @Nonnull IBlockAccess world, @Nonnull BlockPos pos) {
    return state //
        .withProperty(PUP, canConnect(world, pos, EnumFacing.UP) ? E.CONNECTOR : E.DISH)
        .withProperty(PEAST, canConnect(world, pos, EnumFacing.EAST) ? E.CONNECTOR : E.DISH)
        .withProperty(PWEST, canConnect(world, pos, EnumFacing.WEST) ? E.CONNECTOR : E.DISH)
        .withProperty(PNORTH, canConnect(world, pos, EnumFacing.NORTH) ? E.CONNECTOR : E.DISH)
        .withProperty(PSOUTH, canConnect(world, pos, EnumFacing.SOUTH) ? E.CONNECTOR : E.DISH);
  }

  @Override
  public void registerRenderers(@Nonnull IModObject modObject) {
    ClientUtil.registerDefaultItemRenderer(MachineObject.block_enhanced_wireless_charger);
  }

  @Override
  protected @Nonnull BoundingBox getChargingStrength(@Nonnull IBlockState state, @Nonnull BlockPos pos) {
    int r = ChargerConfig.wirelessRangeEnhancedAntenna.get();
    int x0 = pos.getX() - r, y0 = pos.getY() - r, z0 = pos.getZ() - r, x1 = pos.getX() + 1 + r, y1 = pos.getY() + 1 + r, z1 = pos.getZ() + 1 + r;
    if (state.getValue(PUP) == E.CONNECTOR) {
      y1 += ChargerConfig.wirelessRangeEnhancedAntennaExtension.get();
    }
    if (state.getValue(PEAST) == E.CONNECTOR) {
      x1 += ChargerConfig.wirelessRangeEnhancedAntennaExtension.get();
    }
    if (state.getValue(PWEST) == E.CONNECTOR) {
      x0 -= ChargerConfig.wirelessRangeEnhancedAntennaExtension.get();
    }
    if (state.getValue(PNORTH) == E.CONNECTOR) {
      z0 -= ChargerConfig.wirelessRangeEnhancedAntennaExtension.get();
    }
    if (state.getValue(PSOUTH) == E.CONNECTOR) {
      z1 += ChargerConfig.wirelessRangeEnhancedAntennaExtension.get();
    }
    return new BoundingBox(x0, y0, z0, x1, y1, z1);
  }

}
