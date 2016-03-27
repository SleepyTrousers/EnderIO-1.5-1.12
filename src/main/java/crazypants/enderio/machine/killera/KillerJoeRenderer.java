package crazypants.enderio.machine.killera;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.enderio.core.client.render.RenderUtil;

import crazypants.enderio.render.HalfBakedQuad.HalfBakedList;
import crazypants.enderio.render.TankRenderHelper;

@SideOnly(Side.CLIENT)
public class KillerJoeRenderer extends TileEntitySpecialRenderer<TileKillerJoe> {

  @Override
  public void renderTileEntityAt(TileKillerJoe te, double x, double y, double z, float tick, int b) {

    if(te != null) {
      if (MinecraftForgeClient.getRenderPass() == 0 && te.getStackInSlot(0) != null) {
        RenderUtil.setupLightmapCoords(te.getPos(), te.getWorld());
        GlStateManager.pushMatrix();
        GlStateManager.translate((float) x, (float) y, (float) z);
        renderSword(te.facing, te.getStackInSlot(0), te.getSwingProgress(tick), false); // TODO 1.9 hand
        GlStateManager.popMatrix();
      } else if (MinecraftForgeClient.getRenderPass() == 1) {
        HalfBakedList buffer = TankRenderHelper.mkTank(te.fuelTank, 2.51, 1, 14, false);
        if (buffer != null) {
          RenderUtil.setupLightmapCoords(te.getPos(), te.getWorld());
          GlStateManager.pushMatrix();
          GlStateManager.translate((float) x, (float) y, (float) z);
          buffer.render();
          GlStateManager.popMatrix();
        }
      }
    }

  }

  private void renderSword(EnumFacing facing, ItemStack sword, float swingProgress, boolean leftHand) {

    //Sword
    GlStateManager.pushMatrix();

    // rotate to facing direction
    GlStateManager.translate(0.5f, 0, 0.5f);
    float offset = 270f;
    if(facing.getFrontOffsetX() != 0) {
      offset *= -1;
    }
    GlStateManager.rotate((facing.getHorizontalIndex() * 90F) + offset, 0F, 1F, 0F);
    GlStateManager.translate(-0.5f, 0, -0.5F);

    // rotate swing progress
    GlStateManager.pushMatrix();
    if(swingProgress > 0) {
      float f6 = MathHelper.sin(swingProgress * swingProgress * (float) Math.PI);
      float f7 = MathHelper.sin(MathHelper.sqrt_float(swingProgress) * (float) Math.PI);
      GlStateManager.rotate(f6 * 5.0F, 1.0F, 0.0F, 0.0F);
      GlStateManager.rotate(-f7 * 30.0F, 0.0F, 0.0F, 1.0F);
    }

    // translate to side of jar
    GlStateManager.translate(13.6f / 16f, 0.6f, (leftHand ? 1.5f : 14.5f) / 16f);

    // scale to size
    GlStateManager.pushMatrix();
    float scale = 0.75f;    
    GlStateManager.scale(scale, scale, scale);

    // render
    @SuppressWarnings("deprecation")
    final net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType none = net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType.NONE;
    Minecraft.getMinecraft().getRenderItem().renderItem(sword, none);

    // cleanup
    GlStateManager.popMatrix();
    GlStateManager.popMatrix();
    GlStateManager.popMatrix();
    
  }

}
