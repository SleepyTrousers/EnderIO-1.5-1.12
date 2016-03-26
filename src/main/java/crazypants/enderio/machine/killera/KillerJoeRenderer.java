package crazypants.enderio.machine.killera;

import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import org.lwjgl.opengl.GL11;

import com.enderio.core.client.render.BoundingBox;
import com.enderio.core.client.render.RenderUtil;
import com.enderio.core.common.vecmath.Vertex;

import crazypants.enderio.render.HalfBakedQuad;

@SideOnly(Side.CLIENT)
public class KillerJoeRenderer extends TileEntitySpecialRenderer<TileKillerJoe> {

  private static final String TEXTURE = "enderio:models/KillerJoe.png";

//  private static final ItemStack DEFAULT_SWORD = new ItemStack(Items.iron_sword);

  @Override
  public void renderTileEntityAt(TileKillerJoe te, double x, double y, double z, float tick, int b) {

    if(te != null) {
      RenderUtil.setupLightmapCoords(te.getPos(), te.getWorld());
    }
    GlStateManager.pushMatrix();    
    GlStateManager.translate((float) x, (float) y, (float) z);
    EnumFacing facing = EnumFacing.WEST;
    if (te != null) {
      facing = te.facing;
    }
    if(te != null) {
      renderSword(facing, te.getStackInSlot(0), te.getSwingProgress(tick), false); // TODO 1.9 hand
      renderFluid(te);
    }
    GlStateManager.popMatrix();

  }

  private void renderSword(EnumFacing facing, ItemStack sword, float swingProgress, boolean leftHand) {

    if(sword == null) {
      return;
    }

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
    Minecraft.getMinecraft().getRenderItem().renderItem(sword, TransformType.NONE);

    // cleanup
    GlStateManager.popMatrix();
    GlStateManager.popMatrix();
    GlStateManager.popMatrix();
    

  }

  protected void renderFluid(TileKillerJoe gen) {

    List<HalfBakedQuad> buffer = KillerJoeRenderMapper.mkTank(gen.fuelTank);
    if (buffer != null) {
      RenderUtil.bindBlockTexture();
      GlStateManager.enableBlend();
      GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
      GlStateManager.disableLighting();
      GlStateManager.depthMask(false);
      WorldRenderer tes = Tessellator.getInstance().getWorldRenderer();
      tes.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);
      for (HalfBakedQuad halfBakedQuad : buffer) {
        halfBakedQuad.render(tes);
      }
      Tessellator.getInstance().draw();
      GlStateManager.depthMask(true);
    }
  }

  public static void renderFace(WorldRenderer tes, BoundingBox bb, EnumFacing face, float minU, float maxU, float minV, float maxV, TextureAtlasSprite tex) {
    List<Vertex> corners = bb.getCornersWithUvForFace(face, minU, maxU, minV, maxV);
    for (Vertex v : corners) {
      tes.pos(v.x(), v.y(), v.z()).tex(tex.getInterpolatedU(v.u() * 16), tex.getInterpolatedV(v.v() * 16)).endVertex();
    }
  }

}
