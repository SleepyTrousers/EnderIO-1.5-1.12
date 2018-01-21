package crazypants.enderio.machines.machine.teleport.telepad.render;

import java.util.Collections;
import java.util.List;

import javax.annotation.Nonnull;

import org.apache.commons.lang3.tuple.Pair;

import crazypants.enderio.base.render.IBlockStateWrapper;
import crazypants.enderio.base.render.IRenderMapper;
import crazypants.enderio.base.render.property.EnumRenderMode;
import crazypants.enderio.base.render.util.ItemQuadCollector;
import crazypants.enderio.base.render.util.QuadCollector;
import crazypants.enderio.machines.machine.teleport.telepad.BlockTelePad;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import static crazypants.enderio.machines.init.MachineObject.block_tele_pad;

public class TelePadRenderMapper implements IRenderMapper.IBlockRenderMapper.IRenderLayerAware, IRenderMapper.IItemRenderMapper.IItemStateMapper {

  public static final @Nonnull TelePadRenderMapper instance = new TelePadRenderMapper();

  private static final @Nonnull EnumRenderMode FULL_MODEL = EnumRenderMode.FRONT_EAST;
  private static final @Nonnull EnumRenderMode SINGLE_MODEL = EnumRenderMode.FRONT;
  private static final @Nonnull EnumRenderMode SINGLE_MODEL_INVENTORY = EnumRenderMode.FRONT_SOUTH;

  protected TelePadRenderMapper() {
  }

  @Override
  @SideOnly(Side.CLIENT)
  public List<IBlockState> mapBlockRender(@Nonnull IBlockStateWrapper state, @Nonnull IBlockAccess world, @Nonnull BlockPos pos, BlockRenderLayer blockLayer,
      @Nonnull QuadCollector quadCollector) {

    if (state.getBlock() == block_tele_pad.getBlock()) {
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
  public List<Pair<IBlockState, ItemStack>> mapItemRender(@Nonnull Block block, @Nonnull ItemStack stack, @Nonnull ItemQuadCollector itemQuadCollector) {
    return Collections
        .singletonList(Pair.of(block.getStateFromMeta(stack.getMetadata()).withProperty(EnumRenderMode.RENDER, SINGLE_MODEL_INVENTORY), (ItemStack) null));
  }

}
