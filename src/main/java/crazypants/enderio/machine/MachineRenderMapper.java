package crazypants.enderio.machine;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;

import javax.annotation.Nonnull;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.world.IBlockAccess;

import org.apache.commons.lang3.tuple.Pair;

import crazypants.enderio.render.EnumRenderMode;
import crazypants.enderio.render.EnumRenderPart;
import crazypants.enderio.render.IBlockStateWrapper;
import crazypants.enderio.render.IOMode.EnumIOMode;
import crazypants.enderio.render.IRenderMapper;
import crazypants.enderio.render.dummy.BlockMachineBase;
import crazypants.enderio.render.pipeline.QuadCollector;

public class MachineRenderMapper implements IRenderMapper {

  protected final EnumRenderPart body;

  public MachineRenderMapper(EnumRenderPart body) {
    this.body = body;
  }

  @Override
  public List<IBlockState> mapBlockRender(IBlockStateWrapper state, IBlockAccess world, BlockPos pos, EnumWorldBlockLayer blockLayer,
      QuadCollector quadCollector) {
    TileEntity tileEntity = state.getTileEntity();
    Block block = state.getBlock();

    if ((tileEntity instanceof AbstractMachineEntity) && (block instanceof AbstractMachineBlock)) {
      return render(state.getState(), world, pos, blockLayer, (AbstractMachineEntity) tileEntity, (AbstractMachineBlock) block);
    }
    return null;
  }

  protected List<IBlockState> render(IBlockState state, IBlockAccess world, BlockPos pos, EnumWorldBlockLayer blockLayer, AbstractMachineEntity tileEntity, AbstractMachineBlock block) {
    List<IBlockState> states = new ArrayList<IBlockState>();

    EnumFacing facing = tileEntity.getFacing();
    boolean active = tileEntity.isActive();

    if (body != null) {
      states.add(BlockMachineBase.block.getDefaultState().withProperty(EnumRenderPart.SUB, body.rotate(facing)));
    }

    if (active) {
      states.add(state.withProperty(EnumRenderMode.RENDER, EnumRenderMode.FRONT_ON.rotate(facing)));
    } else {
      states.add(state.withProperty(EnumRenderMode.RENDER, EnumRenderMode.FRONT.rotate(facing)));
    }

    return states;
  }

  protected EnumMap<EnumFacing, EnumIOMode> renderIO(@Nonnull AbstractMachineEntity tileEntity, @Nonnull AbstractMachineBlock block) {
    EnumMap<EnumFacing, EnumIOMode> result = new EnumMap<EnumFacing, EnumIOMode>(EnumFacing.class);
    for (EnumFacing face : EnumFacing.values()) {
      IoMode ioMode = tileEntity.getIoMode(face);
      if (ioMode != IoMode.NONE) {
        @SuppressWarnings("rawtypes")
        EnumIOMode iOMode = block.mapIOMode(ioMode, face);
        result.put(face, iOMode);
      }
    }
    return result.isEmpty() ? null : result;
  }

  protected EnumMap<EnumFacing, EnumIOMode> renderPaintIO(@Nonnull AbstractMachineEntity tileEntity, @Nonnull AbstractMachineBlock block) {
    return null;
  }

  @Override
  public Pair<List<IBlockState>, List<IBakedModel>> mapItemRender(Block block, ItemStack stack) {
    List<IBlockState> states = new ArrayList<IBlockState>();
    if (body != null) {
      states.add(BlockMachineBase.block.getDefaultState().withProperty(EnumRenderPart.SUB, body));
    }
    states.add(block.getStateFromMeta(stack.getMetadata()).withProperty(EnumRenderMode.RENDER, EnumRenderMode.FRONT));
    return Pair.of(states, null);
  }

  @Override
  public Pair<List<IBlockState>, List<IBakedModel>> mapItemPaintOverlayRender(Block block, ItemStack stack) {
    return null;
  }

  @Override
  public EnumMap<EnumFacing, EnumIOMode> mapOverlayLayer(IBlockStateWrapper state, IBlockAccess world, BlockPos pos, boolean isPainted) {
    TileEntity tileEntity = state.getTileEntity();
    Block block = state.getBlock();

    if ((tileEntity instanceof AbstractMachineEntity) && (block instanceof AbstractMachineBlock)) {
      if (isPainted) {
        return renderPaintIO((AbstractMachineEntity) tileEntity, (AbstractMachineBlock) block);
      } else {
        return renderIO((AbstractMachineEntity) tileEntity, (AbstractMachineBlock) block);
      }
    }
    return null;
  }

}
