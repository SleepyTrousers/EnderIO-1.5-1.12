package crazypants.enderio.machines.machine.ihopper;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.commons.lang3.tuple.Pair;

import crazypants.enderio.base.machine.modes.IoMode;
import crazypants.enderio.base.render.IBlockStateWrapper;
import crazypants.enderio.base.render.IRenderMapper;
import crazypants.enderio.base.render.property.EnumRenderMode;
import crazypants.enderio.base.render.property.IOMode;
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

public class ImpulseRenderMapper implements IRenderMapper.IBlockRenderMapper, IRenderMapper.IItemRenderMapper.IItemStateMapper {

  public static final @Nonnull ImpulseRenderMapper instance = new ImpulseRenderMapper();

  public ImpulseRenderMapper() {
  }

  @Override
  @SideOnly(Side.CLIENT)
  @Nullable
  public List<Pair<IBlockState, ItemStack>> mapItemRender(@Nonnull Block block, @Nonnull ItemStack stack, @Nonnull ItemQuadCollector itemQuadCollector) {
    List<Pair<IBlockState, ItemStack>> states = new ArrayList<Pair<IBlockState, ItemStack>>();
    states.add(Pair.of(block.getStateFromMeta(stack.getMetadata()).withProperty(EnumRenderMode.RENDER, EnumRenderMode.FRONT), stack));
    return states;
  }

  protected List<IBlockState> render(IBlockState state, IBlockAccess world, BlockPos pos, BlockRenderLayer blockLayer, TileImpulseHopper tileEntity,
      BlockImpulseHopper block) {
    List<IBlockState> states = new ArrayList<IBlockState>();

    states.add(state.withProperty(EnumRenderMode.RENDER, EnumRenderMode.FRONT));

    return states;
  }

  @Override
  @SideOnly(Side.CLIENT)
  @Nullable
  public List<IBlockState> mapBlockRender(@Nonnull IBlockStateWrapper state, @Nonnull IBlockAccess world, @Nonnull BlockPos pos, BlockRenderLayer blockLayer,
      @Nonnull QuadCollector quadCollector) {
    TileEntity te = state.getTileEntity();
    Block block = state.getBlock();

    if ((te instanceof TileImpulseHopper) && (block instanceof BlockImpulseHopper)) {
      return render(state.getState(), world, pos, blockLayer, (TileImpulseHopper) te, (BlockImpulseHopper) block);
    }

    return null;
  }

  @SideOnly(Side.CLIENT)
  protected EnumMap<EnumFacing, EnumIOMode> renderIO(@Nonnull TileImpulseHopper tileEntity, @Nonnull BlockImpulseHopper block) {
    EnumMap<EnumFacing, EnumIOMode> result = new EnumMap<EnumFacing, EnumIOMode>(EnumFacing.class);
    for (EnumFacing face : EnumFacing.values()) {
      IoMode ioMode = tileEntity.getIoMode(face);
      if (ioMode != IoMode.NONE) {
        EnumIOMode iOMode = mapIOMode(ioMode);
        result.put(face, iOMode);
      }
    }
    return result.isEmpty() ? null : result;
  }

  @SideOnly(Side.CLIENT)
  public IOMode.EnumIOMode mapIOMode(IoMode mode) {
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
    throw new RuntimeException("Hey, leave our enums alone!");
  }

  @Override
  @SideOnly(Side.CLIENT)
  public EnumMap<EnumFacing, EnumIOMode> mapOverlayLayer(@Nonnull IBlockStateWrapper state, @Nonnull IBlockAccess world, @Nonnull BlockPos pos,
      boolean isPainted) {
    TileEntity tileEntity = state.getTileEntity();
    Block block = state.getBlock();

    if ((tileEntity instanceof TileImpulseHopper) && (block instanceof BlockImpulseHopper)) {
      return renderIO((TileImpulseHopper) tileEntity, (BlockImpulseHopper) block);
    }
    return null;
  }

}
