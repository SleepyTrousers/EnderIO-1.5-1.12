package crazypants.enderio.machine;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;

import javax.annotation.Nonnull;

import org.apache.commons.lang3.tuple.Pair;

import crazypants.enderio.paint.render.PaintedBlockAccessWrapper;
import crazypants.enderio.render.IBlockStateWrapper;
import crazypants.enderio.render.ICacheKey;
import crazypants.enderio.render.IRenderMapper;
import crazypants.enderio.render.dummy.BlockMachineBase;
import crazypants.enderio.render.property.EnumRenderMode;
import crazypants.enderio.render.property.EnumRenderPart;
import crazypants.enderio.render.property.IOMode.EnumIOMode;
import crazypants.enderio.render.util.ItemQuadCollector;
import crazypants.enderio.render.util.QuadCollector;
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

public class MachineRenderMapper implements IRenderMapper.IBlockRenderMapper, IRenderMapper.IItemRenderMapper.IItemStateMapper {

  protected final EnumRenderPart body;

  public MachineRenderMapper(EnumRenderPart body) {
    this.body = body;
  }

  @Override
  @SideOnly(Side.CLIENT)
  public List<IBlockState> mapBlockRender(IBlockStateWrapper state, IBlockAccess world, BlockPos pos, BlockRenderLayer blockLayer,
                                          QuadCollector quadCollector) {
    TileEntity tileEntity = getTileEntity(state, pos);
    Block block = state.getBlock();

    if ((tileEntity instanceof AbstractMachineEntity) && (block instanceof AbstractMachineBlock)) {
      return render(state.getState(), world, pos, blockLayer, (AbstractMachineEntity) tileEntity, (AbstractMachineBlock<?>) block);
    }
    return null;
  }

  private TileEntity getTileEntity(IBlockStateWrapper state, BlockPos pos) {
    IBlockAccess world = state.getWorld();
    if (world instanceof PaintedBlockAccessWrapper) {
      TileEntity te = ((PaintedBlockAccessWrapper) world).getRealTileEntity(pos);
      if (te instanceof AbstractMachineEntity) {
        return te;
      }
    }
    return state.getTileEntity();
  }

  @SideOnly(Side.CLIENT)
  protected List<IBlockState> render(IBlockState state, IBlockAccess world, BlockPos pos, BlockRenderLayer blockLayer, AbstractMachineEntity tileEntity,
                                     AbstractMachineBlock<?> block) {
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

  @SideOnly(Side.CLIENT)
  protected EnumMap<EnumFacing, EnumIOMode> renderIO(@Nonnull AbstractMachineEntity tileEntity, @Nonnull AbstractMachineBlock<?> block) {
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

  @SideOnly(Side.CLIENT)
  protected EnumMap<EnumFacing, EnumIOMode> renderPaintIO(@Nonnull AbstractMachineEntity tileEntity, @Nonnull AbstractMachineBlock<?> block) {
    return null;
  }

  @Override
  @SideOnly(Side.CLIENT)
  public List<Pair<IBlockState, ItemStack>> mapItemRender(Block block, ItemStack stack, ItemQuadCollector itemQuadCollector) {
    List<Pair<IBlockState, ItemStack>> states = new ArrayList<Pair<IBlockState, ItemStack>>();
    if (body != null) {
      states.add(Pair.of(BlockMachineBase.block.getDefaultState().withProperty(EnumRenderPart.SUB, body), stack));
    }
    states.add(Pair.of(block.getStateFromMeta(stack.getMetadata()).withProperty(EnumRenderMode.RENDER, EnumRenderMode.FRONT), stack));
    return states;
  }

  @Override
  @SideOnly(Side.CLIENT)
  public EnumMap<EnumFacing, EnumIOMode> mapOverlayLayer(IBlockStateWrapper state, IBlockAccess world, BlockPos pos, boolean isPainted) {
    TileEntity tileEntity = state.getTileEntity();
    Block block = state.getBlock();

    if ((tileEntity instanceof AbstractMachineEntity) && (block instanceof AbstractMachineBlock)) {
      if (isPainted) {
        return renderPaintIO((AbstractMachineEntity) tileEntity, (AbstractMachineBlock<?>) block);
      } else {
        return renderIO((AbstractMachineEntity) tileEntity, (AbstractMachineBlock<?>) block);
      }
    }
    return null;
  }

  @Override
  @SideOnly(Side.CLIENT)
  public @Nonnull ICacheKey getCacheKey(@Nonnull Block block, @Nonnull ItemStack stack, @Nonnull ICacheKey cacheKey) {
    return cacheKey;
  }

}
