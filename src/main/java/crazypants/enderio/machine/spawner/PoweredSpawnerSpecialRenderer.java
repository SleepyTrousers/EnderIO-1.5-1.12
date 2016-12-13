package crazypants.enderio.machine.spawner;

import javax.annotation.Nonnull;

import com.enderio.core.client.render.ManagedTESR;
import com.enderio.core.client.render.RenderUtil;
import com.enderio.core.common.vecmath.Vector3f;

import crazypants.enderio.ModObject;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class PoweredSpawnerSpecialRenderer extends ManagedTESR<TilePoweredSpawner> {

  public PoweredSpawnerSpecialRenderer() {
    super(ModObject.blockPoweredSpawner.getBlock());
  }

  @Override
  protected boolean shouldRender(@Nonnull TilePoweredSpawner te, @Nonnull IBlockState blockState, int renderPass) {
    return te.isActive();
  }

  @Override
  protected void renderTileEntity(@Nonnull TilePoweredSpawner te, @Nonnull IBlockState blockState, float partialTicks, int destroyStage) {
    renderMob(te, partialTicks);
    if (!te.getNotification().isEmpty()) {
      float offset = 0;
      for (SpawnerNotification note : te.getNotification()) {
        RenderUtil.drawBillboardedText(new Vector3f(0.5, 1.5 + offset, 0.5), note.getDisplayString(), 0.25f);
        offset += 0.375f;
      }
    }
  }

  private void renderMob(TilePoweredSpawner te, float partialTicks) {
    Entity entity = te.getCachedEntity();

    if (entity != null) {
      float f = 0.53125F;
      float f1 = Math.max(entity.width, entity.height);

      if (f1 > 1.0D) {
        f /= f1;
      }

      GlStateManager.pushMatrix();
      GlStateManager.translate(0.5F, 0.4F, 0.5F);
      GlStateManager.rotate((float) (te.getPrevMobRotation() + (te.getMobRotation() - te.getPrevMobRotation()) * partialTicks) * 10.0F, 0.0F, 1.0F, 0.0F);
      GlStateManager.translate(0.0F, -0.2F, 0.0F);
      GlStateManager.rotate(-30.0F, 1.0F, 0.0F, 0.0F);
      GlStateManager.scale(f, f, f);
      Minecraft.getMinecraft().getRenderManager().doRenderEntity(entity, 0.0D, 0.0D, 0.0D, 0.0F, partialTicks, false);
      GlStateManager.popMatrix();
    }
  }

}
