package crazypants.enderio.render;

import java.util.Collection;

import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.world.IBlockAccess;

import com.google.common.collect.ImmutableMap;

/**
 * This blockstate wrapper allows the block to give more information to the smart model. This allows it to talk to the block properly (with world and pos) and
 * to talk to the tile entity.
 * <p>
 * This is to be added in the getExtendedState() call.
 * <p>
 * Note: Careful! This happens in a render thread!
 */
@Deprecated
public class BlockStateWrapper implements IBlockStateWrapper {

  private final IBlockState state;
  private final IBlockAccess world;
  private final BlockPos pos;
  private long cacheKey;

  public BlockStateWrapper(IBlockState state, IBlockAccess world, BlockPos pos) {
    this(state, world, pos, null);
  }

  public BlockStateWrapper(IBlockState state, IBlockAccess world, BlockPos pos, Object cacheKey) {
    this.world = world;
    this.state = state;
    this.pos = pos;
    this.cacheKey = cacheKey == null ? 0 : cacheKey.hashCode();
  }

  
  @SuppressWarnings("rawtypes")
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

  @SuppressWarnings("rawtypes")
  @Override
  public ImmutableMap<IProperty, Comparable> getProperties() {
    return state.getProperties();
  }

  @Override
  public Block getBlock() {
    return state.getBlock();
  }

  @Override
  public BlockPos getPos() {
    return pos;
  }

  @Override
  public TileEntity getTileEntity() {
    return world.getTileEntity(pos);
  }

  @Override
  public IBlockAccess getWorld() {
    return world;
  }

  @Override
  public IBlockState getState() {
    return state;
  }

  public long getCacheKey() {
    return cacheKey;
  }

  @Override
  public String toString() {
    return "BlockStateWrapper [" + state + "]";
  }

  @Override
  public IBlockStateWrapper addCacheKey(Object addlCacheKey) {
    this.cacheKey = this.cacheKey ^ addlCacheKey.hashCode();
    return this;
  }

  public void bakeModel() {
  }
}
