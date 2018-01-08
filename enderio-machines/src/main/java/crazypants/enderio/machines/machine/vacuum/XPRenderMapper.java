package crazypants.enderio.machines.machine.vacuum;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;

import javax.annotation.Nonnull;

import org.apache.commons.lang3.tuple.Pair;

import crazypants.enderio.base.render.IBlockStateWrapper;
import crazypants.enderio.base.render.ICacheKey;
import crazypants.enderio.base.render.IRenderMapper;
import crazypants.enderio.base.render.property.EnumRenderMode;
import crazypants.enderio.base.render.property.IOMode.EnumIOMode;
import crazypants.enderio.base.render.util.ItemQuadCollector;
import crazypants.enderio.base.render.util.QuadCollector;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class XPRenderMapper implements IRenderMapper.IBlockRenderMapper, IRenderMapper.IItemRenderMapper.IItemStateMapper {

  public static final @Nonnull XPRenderMapper instance = new XPRenderMapper();

  @Override
  @SideOnly(Side.CLIENT)
  public List<IBlockState> mapBlockRender(@Nonnull IBlockStateWrapper state, @Nonnull IBlockAccess world, @Nonnull BlockPos pos, BlockRenderLayer blockLayer,
      @Nonnull QuadCollector quadCollector) {
    TileEntity tileEntity = state.getTileEntity();
    Block block = state.getBlock();

    if ((tileEntity instanceof TileXPVacuum) && (block instanceof BlockXPVacuum)) {
      return render(state.getState(), world, pos, blockLayer, (TileXPVacuum) tileEntity, (BlockXPVacuum) block);
    }
    return null;
  }

  @SideOnly(Side.CLIENT)
  protected List<IBlockState> render(IBlockState state, IBlockAccess world, BlockPos pos, BlockRenderLayer blockLayer, TileXPVacuum tileEntity,
      BlockXPVacuum block) {
    List<IBlockState> states = new ArrayList<IBlockState>();

    EnumFacing facing = EnumFacing.NORTH;

    boolean formed = tileEntity.isFormed();

    if (formed) {
      states.add(state.withProperty(EnumRenderMode.RENDER, EnumRenderMode.FRONT_ON.rotate(facing)));
    } else {
      states.add(state.withProperty(EnumRenderMode.RENDER, EnumRenderMode.FRONT.rotate(facing)));
    }

    return states;
  }

  @Override
  @SideOnly(Side.CLIENT)
  public List<Pair<IBlockState, ItemStack>> mapItemRender(@Nonnull Block block, @Nonnull ItemStack stack, @Nonnull ItemQuadCollector itemQuadCollector) {
    List<Pair<IBlockState, ItemStack>> states = new ArrayList<Pair<IBlockState, ItemStack>>();
    states.add(Pair.of(block.getStateFromMeta(stack.getMetadata()).withProperty(EnumRenderMode.RENDER, EnumRenderMode.FRONT_ON_SOUTH), stack));
    return states;
  }

  @Override
  @SideOnly(Side.CLIENT)
  public EnumMap<EnumFacing, EnumIOMode> mapOverlayLayer(@Nonnull IBlockStateWrapper state, @Nonnull IBlockAccess world, @Nonnull BlockPos pos,
      boolean isPainted) {
    return null;
  }

  @Override
  @SideOnly(Side.CLIENT)
  public @Nonnull ICacheKey getCacheKey(@Nonnull Block block, @Nonnull ItemStack stack, @Nonnull ICacheKey cacheKey) {
    return cacheKey;
  }

}
