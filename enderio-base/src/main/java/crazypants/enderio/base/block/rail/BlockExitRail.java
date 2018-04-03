package crazypants.enderio.base.block.rail;

import javax.annotation.Nonnull;

import crazypants.enderio.base.init.IModObject;
import crazypants.enderio.base.render.IDefaultRenderers;
import net.minecraft.block.BlockRailBase;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.item.EntityMinecart.Type;
import net.minecraft.init.Blocks;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import static net.minecraft.block.BlockRailDetector.SHAPE;

public class BlockExitRail extends BlockRailBase implements IDefaultRenderers, IModObject.WithBlockItem {

  public static BlockExitRail create(@Nonnull IModObject modObject) {
    return new BlockExitRail(modObject);
  }

  public BlockExitRail(@Nonnull IModObject modObject) {
    super(false);
    this.setDefaultState(getBlockState().getBaseState().withProperty(SHAPE, BlockRailBase.EnumRailDirection.NORTH_SOUTH));
    setCreativeTab(CreativeTabs.TRANSPORTATION);
    modObject.apply(this);
  }

  @Override
  public @Nonnull IProperty<BlockRailBase.EnumRailDirection> getShapeProperty() {
    return SHAPE;
  }

  @Override
  public @Nonnull IBlockState getStateFromMeta(int meta) {
    return this.getDefaultState().withProperty(SHAPE, BlockRailBase.EnumRailDirection.byMetadata(meta & 7));
  }

  @Override
  public int getMetaFromState(@Nonnull IBlockState state) {
    return state.getValue(SHAPE).getMetadata();
  }

  @Override
  public @Nonnull IBlockState withRotation(@Nonnull IBlockState state, @Nonnull Rotation rot) {
    return Blocks.DETECTOR_RAIL.withRotation(state, rot);
  }

  @Override
  public @Nonnull IBlockState withMirror(@Nonnull IBlockState state, @Nonnull Mirror mirrorIn) {
    return Blocks.DETECTOR_RAIL.withMirror(state, mirrorIn);
  }

  @Override
  protected @Nonnull BlockStateContainer createBlockState() {
    return new BlockStateContainer(this, new IProperty[] { SHAPE });
  }

  @Override
  public boolean isFlexibleRail(@Nonnull IBlockAccess world, @Nonnull BlockPos pos) {
    return false;
  }

  @Override
  public float getRailMaxSpeed(@Nonnull World world, @Nonnull EntityMinecart cart, @Nonnull BlockPos pos) {
    return 0.05f;
  }

  @Override
  public void onMinecartPass(@Nonnull World world, @Nonnull EntityMinecart cart, @Nonnull BlockPos pos) {
    if (cart.getType() == Type.RIDEABLE) {
      if (cart.isBeingRidden()) {
        cart.removePassengers();
      }
      cart.killMinecart(DamageSource.GENERIC);
    }
  }

}
