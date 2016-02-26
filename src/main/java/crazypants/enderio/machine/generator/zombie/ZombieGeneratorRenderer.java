package crazypants.enderio.machine.generator.zombie;

import org.lwjgl.opengl.GL11;

import com.enderio.core.client.render.BoundingBox;
import com.enderio.core.client.render.RenderPassHelper;
import com.enderio.core.client.render.RenderUtil;
import com.enderio.core.common.util.ForgeDirectionOffsets;
import com.enderio.core.common.vecmath.Vector3d;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ZombieGeneratorRenderer extends TileEntitySpecialRenderer<TileZombieGenerator> {

  private static final String TEXTURE = "enderio:models/ZombieJar.png";

  private ModelZombieJar model = new ModelZombieJar();

  @Override
  public void renderTileEntityAt(TileZombieGenerator te, double x, double y, double z, float tick, int b) {

    if (te != null) {
      RenderUtil.setupLightmapCoords(te.getPos(), te.getWorld());
    }

    GL11.glPushMatrix();
    GL11.glTranslatef((float) x, (float) y, (float) z);
    if (RenderPassHelper.getEntityRenderPass() == 0 || te == null) {
      EnumFacing facing = EnumFacing.EAST;
      if (te != null) {
        facing = te.facing;
      }
      renderModel(facing);
    } else if (RenderPassHelper.getEntityRenderPass() == 1 && te != null) {
      renderFluid(te);
    }
    GL11.glPopMatrix();
  }

  protected void renderFluid(TileZombieGenerator gen) {
    FluidTank tank = gen.fuelTank;
    if (tank.getFluidAmount() <= 0) {
      return;
    }
    TextureAtlasSprite icon = RenderUtil.getStillTexture(tank.getFluid());
    if (icon != null) {
      RenderUtil.bindBlockTexture();

      double facingOffset = 0.075;

      BoundingBox bb = BoundingBox.UNIT_CUBE.scale(0.85, 0.96, 0.85);
      float fullness = (float) (tank.getFluidAmount()) / (tank.getCapacity());
      Vector3d absFac = ForgeDirectionOffsets.absolueOffset(gen.facing);

      double scaleX = absFac.x == 0 ? 0.95 : 1 - facingOffset / 2;
      // double scaleY = 0.85 * fullness;
      double scaleZ = absFac.z == 0 ? 0.95 : 1 - facingOffset / 2;

      bb = bb.scale(scaleX, 0.85 * fullness, scaleZ);

      float ty = -(0.85f - (bb.maxY - bb.minY)) / 2;
      Vector3d transOffset = ForgeDirectionOffsets.offsetScaled(gen.facing, -facingOffset);
      bb = bb.translate((float) transOffset.x, ty, (float) transOffset.z);

      // int brightness;
      // if(gen.getWorld() == null) {
      // brightness = 15 << 20 | 15 << 4;
      // } else {
      // brightness = gen.getWorld().getLightFor(EnumSkyBlock.SKY,
      // gen.getPos());
      // }

      GL11.glPushAttrib(GL11.GL_ENABLE_BIT);
      GL11.glEnable(GL11.GL_BLEND);
      GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
      GL11.glDisable(GL11.GL_LIGHTING);
      GL11.glDisable(GL11.GL_CULL_FACE);
      GL11.glDepthMask(false);

      // tes.setBrightness(brightness);
      RenderUtil.renderBoundingBox(bb, icon);

      GL11.glDepthMask(true);
      GL11.glPopAttrib();

    }
  }

  private void renderModel(EnumFacing facing) {

    GL11.glPushMatrix();

    GL11.glTranslatef(0.5F, 0, 0.5F);
    GL11.glRotatef(180F, 1F, 0F, 0F);
    GL11.glScalef(1.2f, 0.9f, 1.2f);

    GL11.glRotatef(facing.getHorizontalIndex() * -90F, 0F, 1F, 0F);

    RenderUtil.bindTexture(TEXTURE);
    model.render((Entity) null, 0.0F, 0.0F, -0.1F, 0.0F, 0.0F, 0.0625F);

    GL11.glTranslatef(-0.5F, 0, -0.5F);
    GL11.glPopMatrix();

  }

}
