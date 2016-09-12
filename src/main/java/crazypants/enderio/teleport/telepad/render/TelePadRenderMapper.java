package crazypants.enderio.teleport.telepad.render;

import java.util.Collections;
import java.util.EnumMap;
import java.util.List;

import javax.annotation.Nonnull;

import org.apache.commons.lang3.tuple.Pair;

import crazypants.enderio.EnderIO;
import crazypants.enderio.render.IBlockStateWrapper;
import crazypants.enderio.render.ICacheKey;
import crazypants.enderio.render.IRenderMapper;
import crazypants.enderio.render.property.EnumRenderMode;
import crazypants.enderio.render.property.IOMode.EnumIOMode;
import crazypants.enderio.render.util.ItemQuadCollector;
import crazypants.enderio.render.util.QuadCollector;
import crazypants.enderio.teleport.telepad.BlockTelePad;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class TelePadRenderMapper implements IRenderMapper.IBlockRenderMapper.IRenderLayerAware, IRenderMapper.IItemRenderMapper.IItemStateMapper {

  public static final TelePadRenderMapper instance = new TelePadRenderMapper();

  private static final EnumRenderMode GLASS_TOP_MODEL = EnumRenderMode.FRONT_ON;
  private static final EnumRenderMode FULL_MODEL = EnumRenderMode.FRONT_EAST;
  private static final EnumRenderMode SINGLE_MODEL = EnumRenderMode.FRONT;
  private static final EnumRenderMode SINGLE_MODEL_INVENTORY = EnumRenderMode.FRONT_SOUTH;

  protected TelePadRenderMapper() {
  }

  @Override
  @SideOnly(Side.CLIENT)
  public List<IBlockState> mapBlockRender(IBlockStateWrapper state, IBlockAccess world, BlockPos pos, BlockRenderLayer blockLayer,
                                          QuadCollector quadCollector) {

    if (state.getBlock() == EnderIO.blockTelePad) {
      if (blockLayer == BlockRenderLayer.SOLID) {
        BlockType type = state.getValue(BlockTelePad.BLOCK_TYPE);
        if (type == BlockType.SINGLE) {
          return Collections.singletonList(state.getState().withProperty(EnumRenderMode.RENDER, SINGLE_MODEL));
        } else if (type == BlockType.MASTER) {
          return Collections.singletonList(state.getState().withProperty(EnumRenderMode.RENDER, FULL_MODEL));
        } else {
          return null;
        }
      }
      return null;
    } else if (blockLayer == BlockRenderLayer.CUTOUT) {
      return Collections.singletonList(state.getState().withProperty(EnumRenderMode.RENDER, SINGLE_MODEL));
    }

    return null;
  }

  @SuppressWarnings("deprecation")
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
  public @Nonnull ICacheKey getCacheKey(@Nonnull Block block, @Nonnull ItemStack stack, @Nonnull ICacheKey cacheKey) {
    return cacheKey;
  }

}
