package crazypants.enderio.machine.farm;

import com.enderio.core.client.render.ManagedTESR;
import com.enderio.core.client.render.RenderUtil;
import com.enderio.core.common.vecmath.Vector3f;
import crazypants.enderio.config.Config;
import net.minecraft.block.state.IBlockState;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;

import static crazypants.enderio.ModObject.blockFarmStation;

@SideOnly(Side.CLIENT)
public class FarmingStationSpecialRenderer extends ManagedTESR<TileFarmStation> {

  public FarmingStationSpecialRenderer() {
    super(blockFarmStation.getBlock());
  }

  @Override
  protected boolean shouldRender(@Nonnull TileFarmStation te, @Nonnull IBlockState blockState, int renderPass) {
    return !te.notification.isEmpty() && !Config.disableFarmNotification;
  }

  @Override
  protected void renderTileEntity(@Nonnull TileFarmStation te, @Nonnull IBlockState blockState, float partialTicks, int destroyStage) {
    float offset = 0;
    for (FarmNotification note : te.notification) {
      RenderUtil.drawBillboardedText(new Vector3f(0.5, 1.5 + offset, 0.5), note.getDisplayString(), 0.25f);
      offset += 0.375f;
    }
  }

}
