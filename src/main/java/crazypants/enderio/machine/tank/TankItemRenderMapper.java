package crazypants.enderio.machine.tank;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import crazypants.enderio.machine.MachineRenderMapper;
import crazypants.enderio.render.HalfBakedQuad.HalfBakedList;
import crazypants.enderio.render.IRenderMapper.IItemRenderMapper;
import crazypants.enderio.render.TankRenderHelper;
import crazypants.enderio.render.pipeline.ItemQuadCollector;
import crazypants.enderio.tool.SmartTank;

public class TankItemRenderMapper extends MachineRenderMapper implements IItemRenderMapper.IDynamicOverlayMapper {

  public static final TankItemRenderMapper instance = new TankItemRenderMapper();

  private TankItemRenderMapper() {
    super(null);
  }

  @Override
  @SideOnly(Side.CLIENT)
  public ItemQuadCollector mapItemDynamicOverlayRender(Block block, ItemStack stack) {
    if (stack.hasTagCompound()) {
      SmartTank tank = TileTank.loadTank(stack.getTagCompound());
      HalfBakedList buffer = TankRenderHelper.mkTank(tank, 0.01, 0.01, 15.99, false);
      if (buffer != null) {
        List<BakedQuad> quads = new ArrayList<BakedQuad>();
        buffer.bake(quads);
        ItemQuadCollector result = new ItemQuadCollector();
        result.addQuads(null, quads);
        return result;
      }
    }
    return null;
  }

}
