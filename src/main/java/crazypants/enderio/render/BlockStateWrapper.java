package crazypants.enderio.render;

import java.util.Collection;

import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.world.IBlockAccess;

import com.google.common.collect.ImmutableMap;

public class BlockStateWrapper implements IBlockState {

  private final IBlockState state;
  private final IBlockAccess world;
  private final BlockPos pos;

  public BlockStateWrapper(IBlockState state, IBlockAccess world, BlockPos pos) {
    this.world = world;
    this.state = state;
    this.pos = pos;
  }

  @Override
  public Collection<IProperty> getPropertyNames() {
    return state.getPropertyNames();
  }

  @Override
  public <T extends Comparable<T>> T getValue(IProperty<T> property) {
    return state.getValue(property);
  }

  @Override
  public <T extends Comparable<T>, V extends T> IBlockState withProperty(IProperty<T> property, V value) {
    return new BlockStateWrapper(state.withProperty(property, value), world, pos);
  }

  @Override
  public <T extends Comparable<T>> IBlockState cycleProperty(IProperty<T> property) {
    return new BlockStateWrapper(state.cycleProperty(property), world, pos);
  }

  @Override
  public ImmutableMap<IProperty, Comparable> getProperties() {
    return state.getProperties();
  }

  @Override
  public Block getBlock() {
    return state.getBlock();
  }

  public BlockPos getPos() {
    return pos;
  }

  public TileEntity getTileEntity() {
    return world.getTileEntity(pos);
  }

  public IBlockAccess getWorld() {
    return world;
  }
}
