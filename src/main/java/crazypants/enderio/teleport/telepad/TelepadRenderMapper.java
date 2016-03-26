package crazypants.enderio.teleport.telepad;

import java.util.Collections;
import java.util.EnumMap;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import org.apache.commons.lang3.tuple.Pair;

import crazypants.enderio.render.EnumRenderMode;
import crazypants.enderio.render.IBlockStateWrapper;
import crazypants.enderio.render.ICacheKey;
import crazypants.enderio.render.IOMode.EnumIOMode;
import crazypants.enderio.render.IRenderMapper;
import crazypants.enderio.render.pipeline.ItemQuadCollector;
import crazypants.enderio.render.pipeline.QuadCollector;

public class TelepadRenderMapper implements IRenderMapper.IBlockRenderMapper.IRenderLayerAware, IRenderMapper.IItemRenderMapper.IItemStateMapper {

  public static final TelepadRenderMapper instance = new TelepadRenderMapper();

  private static final EnumRenderMode GLASS_TOP_MODEL = EnumRenderMode.FRONT_ON;
  private static final EnumRenderMode FULL_MODEL = EnumRenderMode.FRONT_EAST;
  private static final EnumRenderMode SINGLE_MODEL = EnumRenderMode.FRONT;
  private static final EnumRenderMode SINGLE_MODEL_INVENTORY = EnumRenderMode.FRONT_SOUTH;

  protected TelepadRenderMapper() {
  }

  @Override
  @SideOnly(Side.CLIENT)
  public List<IBlockState> mapBlockRender(IBlockStateWrapper state, IBlockAccess world, BlockPos pos, EnumWorldBlockLayer blockLayer,
      QuadCollector quadCollector) {
    TileEntity tileEntity = state.getTileEntity();

    if (tileEntity instanceof TileTelePad) {
      TileTelePad telePad = (TileTelePad) tileEntity;

      switch (blockLayer) {
      case SOLID:
        if (!telePad.inNetwork()) {
          return Collections.singletonList(state.getState().withProperty(EnumRenderMode.RENDER, SINGLE_MODEL));
        } else if (telePad.isMaster()) {
          return Collections.singletonList(state.getState().withProperty(EnumRenderMode.RENDER, FULL_MODEL));
        } else {
          return null;
        }
      case TRANSLUCENT:
        if (telePad.inNetwork() && telePad.isMaster()) {
          return Collections.singletonList(state.getState().withProperty(EnumRenderMode.RENDER, GLASS_TOP_MODEL));
        }
      default:
        return null;
      }

    }
    return null;
  }

  @Override
  @SideOnly(Side.CLIENT)
  public List<Pair<IBlockState, ItemStack>> mapItemRender(Block block, ItemStack stack, ItemQuadCollector itemQuadCollector) {
    return Collections.singletonList(Pair.of(block.getStateFromMeta(stack.getMetadata()).withProperty(EnumRenderMode.RENDER, SINGLE_MODEL_INVENTORY),
        (ItemStack) null));
  }

  @Override
  @SideOnly(Side.CLIENT)
  public EnumMap<EnumFacing, EnumIOMode> mapOverlayLayer(IBlockStateWrapper state, IBlockAccess world, BlockPos pos, boolean isPainted) {
    return null;
  }

  @Override
  @SideOnly(Side.CLIENT)
  public ICacheKey getCacheKey(Block block, ItemStack stack, ICacheKey cacheKey) {
    return cacheKey;
  }

}
