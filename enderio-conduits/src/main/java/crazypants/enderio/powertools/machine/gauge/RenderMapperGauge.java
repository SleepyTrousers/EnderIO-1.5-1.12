package crazypants.enderio.powertools.machine.gauge;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.Nonnull;

import org.apache.commons.lang3.tuple.Pair;

import crazypants.enderio.base.EnderIO;
import crazypants.enderio.base.render.IRenderMapper.IItemRenderMapper;
import crazypants.enderio.base.render.util.ItemQuadCollector;
import crazypants.enderio.powertools.machine.capbank.render.FillGaugeBakery;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.item.ItemStack;

import static crazypants.enderio.powertools.init.PowerToolObject.block_gauge;

public class RenderMapperGauge implements IItemRenderMapper.IItemStateMapper, IItemRenderMapper.IDynamicOverlayMapper {

  public static final @Nonnull RenderMapperGauge instance = new RenderMapperGauge();

  private RenderMapperGauge() {
  }

  @Override
  public ItemQuadCollector mapItemDynamicOverlayRender(@Nonnull Block block, @Nonnull ItemStack stack) {
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
  public List<Pair<IBlockState, ItemStack>> mapItemRender(@Nonnull Block block, @Nonnull ItemStack stack, @Nonnull ItemQuadCollector itemQuadCollector) {
    return Collections.singletonList(Pair.of(block_gauge.getBlockNN().getDefaultState(), stack));
  }

}
