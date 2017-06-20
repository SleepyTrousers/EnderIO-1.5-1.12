package crazypants.enderio.machine.tank;

import crazypants.enderio.fluid.SmartTank;
import crazypants.enderio.machine.ItemTankHelper;
import crazypants.enderio.machine.MachineRenderMapper;
import crazypants.enderio.render.IBlockStateWrapper;
import crazypants.enderio.render.ICacheKey;
import crazypants.enderio.render.IRenderMapper;
import crazypants.enderio.render.IRenderMapper.IItemRenderMapper;
import crazypants.enderio.render.property.EnumRenderMode;
import crazypants.enderio.render.util.HalfBakedQuad.HalfBakedList;
import crazypants.enderio.render.util.ItemQuadCollector;
import crazypants.enderio.render.util.QuadCollector;
import crazypants.enderio.render.util.TankRenderHelper;
import crazypants.util.NbtValue;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class TankItemRenderMapper extends MachineRenderMapper implements IItemRenderMapper.IDynamicOverlayMapper,
    IRenderMapper.IBlockRenderMapper.IRenderLayerAware {

  public static final TankItemRenderMapper instance = new TankItemRenderMapper();

  private TankItemRenderMapper() {
    super(null);
  }

  @Override
  @SideOnly(Side.CLIENT)
  public List<IBlockState> mapBlockRender(IBlockStateWrapper state, IBlockAccess world, BlockPos pos, BlockRenderLayer blockLayer,
                                          QuadCollector quadCollector) {
    List<IBlockState> states = new ArrayList<IBlockState>();

    if (blockLayer == BlockRenderLayer.CUTOUT) {
      states.add(state.getState().withProperty(EnumRenderMode.RENDER, EnumRenderMode.FRONT));
    } else if (blockLayer == BlockRenderLayer.TRANSLUCENT) {
      states.add(state.getState().withProperty(EnumRenderMode.RENDER, EnumRenderMode.FRONT_ON));
    }

    return states;
  }

  @Override
  @SideOnly(Side.CLIENT)
  public ItemQuadCollector mapItemDynamicOverlayRender(Block block, ItemStack stack) {
    ItemQuadCollector result = new ItemQuadCollector();
    if (stack.hasTagCompound()) {
      SmartTank tank = ItemTankHelper.getTank(stack);
      HalfBakedList buffer = TankRenderHelper.mkTank(tank, 0.5, 0.5, 15.5, false);
      if (buffer != null) {
        List<BakedQuad> quads = new ArrayList<BakedQuad>();
        buffer.bake(quads);
        result.addQuads(null, quads);
      }
    }
    if (!NbtValue.FAKE.hasTag(stack)) {
      result.addBlockState(block.getStateFromMeta(stack.getMetadata()).withProperty(EnumRenderMode.RENDER, EnumRenderMode.FRONT_ON), stack, true);
    }
    return result;
  }

  @Override
  public List<Pair<IBlockState, ItemStack>> mapItemRender(Block block, ItemStack stack, ItemQuadCollector itemQuadCollector) {
    if (!NbtValue.FAKE.hasTag(stack)) {
      return super.mapItemRender(block, stack, itemQuadCollector);
    } else {
      return null;
    }
  }

  @Override
  @SideOnly(Side.CLIENT)
  public @Nonnull ICacheKey getCacheKey(@Nonnull Block block, @Nonnull ItemStack stack, @Nonnull ICacheKey cacheKey) {
    if (NbtValue.FAKE.hasTag(stack)) {
      cacheKey.addCacheKey(0x7FF71337);
    }
    return cacheKey;
  }

}
