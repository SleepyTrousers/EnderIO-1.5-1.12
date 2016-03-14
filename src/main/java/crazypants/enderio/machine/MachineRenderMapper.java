package crazypants.enderio.machine;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IBlockAccess;

import org.apache.commons.lang3.tuple.Pair;

import crazypants.enderio.paint.IPaintable;
import crazypants.enderio.paint.IPaintable.IPaintableTileEntity;
import crazypants.enderio.paint.YetaUtil;
import crazypants.enderio.render.BlockStateWrapper;
import crazypants.enderio.render.EnumRenderMode;
import crazypants.enderio.render.EnumRenderPart;
import crazypants.enderio.render.IOMode;
import crazypants.enderio.render.IOMode.EnumIOMode;
import crazypants.enderio.render.IRenderMapper;
import crazypants.enderio.render.dummy.BlockMachineBase;
import crazypants.enderio.render.dummy.BlockMachineIO;

public class MachineRenderMapper implements IRenderMapper {

  protected final EnumRenderPart body;

  public MachineRenderMapper(EnumRenderPart body) {
    this.body = body;
  }

  @Override
  public Pair<List<IBlockState>, List<IBakedModel>> mapBlockRender(BlockStateWrapper state, IBlockAccess world, BlockPos pos) {
    TileEntity tileEntity = state.getTileEntity();
    Block block = state.getBlock();

    if ((tileEntity instanceof AbstractMachineEntity) && (block instanceof AbstractMachineBlock)) {
      return render(state.getState(), world, pos, tileEntity, block);
    }
    return null;
  }

  protected Pair<List<IBlockState>, List<IBakedModel>> render(IBlockState state, IBlockAccess world, BlockPos pos,
      TileEntity tileEntity, Block block) {
    List<IBlockState> states = new ArrayList<IBlockState>();

    EnumFacing facing = ((AbstractMachineEntity) tileEntity).getFacing();
    boolean active = ((AbstractMachineEntity) tileEntity).isActive();

    if (body != null) {
      states.add(BlockMachineBase.block.getDefaultState().withProperty(EnumRenderPart.SUB, body.rotate(facing)));
    }

    if (active) {
      states.add(state.withProperty(EnumRenderMode.RENDER, EnumRenderMode.FRONT_ON.rotate(facing)));
    } else {
      states.add(state.withProperty(EnumRenderMode.RENDER, EnumRenderMode.FRONT.rotate(facing)));
    }

    return Pair.of(states, null);
  }

  protected List<IBlockState> renderIO(TileEntity tileEntity, Block block) {
    List<IBlockState> states = new ArrayList<IBlockState>();
    for (EnumFacing face : EnumFacing.values()) {
      IoMode ioMode = ((AbstractMachineEntity) tileEntity).getIoMode(face);
      if (ioMode != IoMode.NONE) {
        @SuppressWarnings("rawtypes")
        EnumIOMode iOMode = ((AbstractMachineBlock) block).mapIOMode(ioMode, face);
        states.add(BlockMachineIO.block.getDefaultState().withProperty(IOMode.IO, IOMode.get(face, iOMode)));
      }
    }
    return states;
  }

  protected List<IBlockState> renderPaintIO(TileEntity tileEntity, Block block) {
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
  public List<IBlockState> mapOverlayLayer(BlockStateWrapper state, IBlockAccess world, BlockPos pos) {
    TileEntity tileEntity = state.getTileEntity();
    Block block = state.getBlock();

    if ((tileEntity instanceof AbstractMachineEntity) && (block instanceof AbstractMachineBlock)) {
      if ((tileEntity instanceof IPaintableTileEntity) && (block instanceof IPaintable.IWrenchHideablePaint)) {
        IPaintableTileEntity te = (IPaintableTileEntity) tileEntity;
        if (te.getPaintSource() != null && !YetaUtil.shouldHeldItemHideFacades()) {
          return renderPaintIO(tileEntity, block);
        }
      }
      return renderIO(tileEntity, block);
    }
    return null;
  }

  @Override
  public Pair<List<IBlockState>, List<IBakedModel>> mapItemPaintOverlayRender(Block block, ItemStack stack) {
    return null;
  }

}
