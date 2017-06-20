package crazypants.enderio.machine.capbank.render;

import com.enderio.core.client.render.ManagedTESR;
import crazypants.enderio.machine.capbank.InfoDisplayType;
import crazypants.enderio.machine.capbank.TileCapBank;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import java.util.EnumMap;
import java.util.Map;

import static crazypants.enderio.ModObject.blockCapBank;

@SideOnly(Side.CLIENT)
public class CapBankRenderer extends ManagedTESR<TileCapBank> {
 
  private static final Map<InfoDisplayType, IInfoRenderer> infoRenderers = new EnumMap<InfoDisplayType, IInfoRenderer>(InfoDisplayType.class);
  static {
    infoRenderers.put(InfoDisplayType.LEVEL_BAR, new FillGauge());
    infoRenderers.put(InfoDisplayType.IO, new IoDisplay());
    infoRenderers.put(InfoDisplayType.NONE, new IInfoRenderer() {
      @Override
      public void render(TileCapBank cb, EnumFacing dir, float partialTick) {
      }
    });
  }

  public CapBankRenderer() {
    super(blockCapBank.getBlock());
  }

  //---- Info Display

  @Override
  protected boolean shouldRender(@Nonnull TileCapBank te, @Nonnull IBlockState blockState, int renderPass) {
    return te.hasDisplayTypes();
  }

  @Override
  protected void renderTileEntity(@Nonnull TileCapBank te, @Nonnull IBlockState blockState, float partialTicks, int destroyStage) {
    RenderHelper.enableStandardItemLighting();

    for (EnumFacing dir : EnumFacing.VALUES) {
      infoRenderers.get(te.getDisplayType(dir)).render(te, dir, partialTicks);
    }
  }

}
