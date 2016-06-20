package crazypants.enderio.machine.tank;

import java.util.ArrayList;
import java.util.List;

import crazypants.enderio.machine.ItemTankHelper;
import crazypants.enderio.machine.MachineRenderMapper;
import crazypants.enderio.render.EnumRenderMode;
import crazypants.enderio.render.HalfBakedQuad.HalfBakedList;
import crazypants.enderio.render.IBlockStateWrapper;
import crazypants.enderio.render.IRenderMapper;
import crazypants.enderio.render.IRenderMapper.IItemRenderMapper;
import crazypants.enderio.render.TankRenderHelper;
import crazypants.enderio.render.pipeline.ItemQuadCollector;
import crazypants.enderio.render.pipeline.QuadCollector;
import crazypants.enderio.tool.SmartTank;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

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
    result.addBlockState(block.getStateFromMeta(stack.getMetadata()).withProperty(EnumRenderMode.RENDER, EnumRenderMode.FRONT_ON), stack, true);
    return result;
  }

}
