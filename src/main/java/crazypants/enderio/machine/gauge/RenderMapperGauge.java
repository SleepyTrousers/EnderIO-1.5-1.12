package crazypants.enderio.machine.gauge;

import static crazypants.enderio.machine.MachineObject.blockGauge;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.Nonnull;

import org.apache.commons.lang3.tuple.Pair;

import crazypants.enderio.EnderIO;
import crazypants.enderio.machine.capbank.render.FillGaugeBakery;
import crazypants.enderio.render.ICacheKey;
import crazypants.enderio.render.IRenderMapper.IItemRenderMapper;
import crazypants.enderio.render.util.ItemQuadCollector;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.item.ItemStack;

public class RenderMapperGauge implements IItemRenderMapper.IItemStateMapper, IItemRenderMapper.IDynamicOverlayMapper {

  public static final RenderMapperGauge instance = new RenderMapperGauge();

  private RenderMapperGauge() {
  }

  @Override
  @Nonnull
  public ICacheKey getCacheKey(@Nonnull Block block, @Nonnull ItemStack stack, @Nonnull ICacheKey cacheKey) {
    return cacheKey;
  }

  @Override
  public ItemQuadCollector mapItemDynamicOverlayRender(Block block, ItemStack stack) {
    double v = EnderIO.proxy.getTickCount() % 120;
    if (v > 60) {
      v = 120 - v;
    }
    double ratio = v / 60d;
    FillGaugeBakery gauge = new FillGaugeBakery(BlockGauge.gaugeIcon.get(TextureAtlasSprite.class), ratio);
    if (gauge.canRender()) {
      ItemQuadCollector result = new ItemQuadCollector();
      List<BakedQuad> quads = new ArrayList<BakedQuad>();
      gauge.bake(quads);
      result.addQuads(null, quads);
      return result;
    }
    return null;
  }

  @Override
  public List<Pair<IBlockState, ItemStack>> mapItemRender(Block block, ItemStack stack, ItemQuadCollector itemQuadCollector) {
    return Collections.singletonList(Pair.of(blockGauge.getBlock().getDefaultState(), stack));
  }

}
