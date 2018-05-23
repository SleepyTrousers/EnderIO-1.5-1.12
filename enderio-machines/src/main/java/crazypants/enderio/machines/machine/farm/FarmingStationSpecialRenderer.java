package crazypants.enderio.machines.machine.farm;

import javax.annotation.Nonnull;

import com.enderio.core.client.render.ManagedTESR;
import com.enderio.core.client.render.RenderUtil;
import com.enderio.core.common.vecmath.Vector3f;

import crazypants.enderio.api.farm.FarmNotification;
import crazypants.enderio.base.EnderIO;
import crazypants.enderio.machines.config.config.FarmConfig;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import static crazypants.enderio.machines.init.MachineObject.block_farm_station;

@SideOnly(Side.CLIENT)
public class FarmingStationSpecialRenderer extends ManagedTESR<TileFarmStation> {

  public FarmingStationSpecialRenderer() {
    super(block_farm_station.getBlock());
  }

  @Override
  protected boolean shouldRender(@Nonnull TileFarmStation te, @Nonnull IBlockState blockState, int renderPass) {
    return !te.getNotification().isEmpty() && !FarmConfig.disableFarmNotification.get();
  }

  @Override
  protected void renderTileEntity(@Nonnull TileFarmStation te, @Nonnull IBlockState blockState, float partialTicks, int destroyStage) {
    float offset = 0;
    GlStateManager.disableDepth();
    for (FarmNotification note : te.getNotification()) {
      RenderUtil.drawBillboardedText(new Vector3f(0.5, 1.5 + offset, 0.5), EnderIO.lang.localizeExact(note.getUnlocalizedName()), 0.25f);
      offset += 0.375f;
    }
    GlStateManager.enableDepth();
  }

}
