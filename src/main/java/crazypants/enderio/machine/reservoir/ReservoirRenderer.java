package crazypants.enderio.machine.reservoir;

import org.lwjgl.opengl.GL11;

import com.enderio.core.client.render.BoundingBox;
import com.enderio.core.client.render.RenderUtil;
import com.enderio.core.common.util.ForgeDirectionOffsets;
import com.enderio.core.common.vecmath.Vector3d;
import com.enderio.core.common.vecmath.Vector3f;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ReservoirRenderer extends TileEntitySpecialRenderer<TileReservoir>  {

  private ResourceLocation texName = null;
  private TextureAtlasSprite tex = null;

  private final BlockReservoir block;

  public ReservoirRenderer(BlockReservoir res) {
    block = res;
    MinecraftForge.EVENT_BUS.register(this);;
  }

  @SideOnly(Side.CLIENT)
  @SubscribeEvent
  public void onIconLoad(TextureStitchEvent.Post event) {
    tex = null;
  }

  @Override
  public void renderTileEntityAt(TileReservoir tileentity, double x, double y, double z, float f, int b) {

    TileReservoir res = tileentity;
    if (res.haveRendered(tileentity.getWorld().getTotalWorldTime(), f)) {
      return;
    }

    float fullness = res.getFilledRatio();
    if (fullness <= 0 && !res.isAutoEject()) {
      return;
    }

    float val = RenderUtil.claculateTotalBrightnessForLocation(tileentity.getWorld(), tileentity.getPos());
    Minecraft.getMinecraft().entityRenderer.disableLightmap();

    GL11.glPushMatrix();
    GL11.glPushAttrib(GL11.GL_ENABLE_BIT);
    GL11.glEnable(GL11.GL_CULL_FACE);
    GL11.glDisable(GL11.GL_LIGHTING);

    GL11.glEnable(GL11.GL_BLEND);
    GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

    Vector3f offset = res.getOffsetFromController();

    GL11.glTranslatef((float) x + offset.x, (float) y + offset.y, (float) z + offset.z);

    BoundingBox bb = res.getLiquidRenderBounds();

    if (res.isAutoEject()) {

      // switch
      RenderUtil.bindBlockTexture();

      Tessellator tessellator = Tessellator.getInstance();
      WorldRenderer tes = tessellator.getWorldRenderer();
      tes.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);

      GlStateManager.color(val, val, val, 1);
      for (EnumFacing dir : EnumFacing.VALUES) {
        drawSwitch(dir, bb);
      }
      tessellator.draw();
    }

    if (fullness > 0) {
      RenderUtil.bindTexture(getLiquidSheet());

      float margin = 0.01f;

      TextureAtlasSprite tex = getLiquidTexture();
      float maxV = tex.getMinV() + ((tex.getMaxV() - tex.getMinV()) * fullness);

      GlStateManager.color(val, val, val);

      RenderUtil.renderBoundingBox(bb, tex);

      RenderUtil.renderBoundingBox(new BoundingBox(bb.minX + margin, bb.minY + margin, bb.minZ + margin, bb.maxX - margin,
          bb.minY + (fullness * (Math.abs(bb.maxY - bb.minY))) - margin, bb.maxZ - margin), tex.getMinU(), tex.getMaxU(), tex.getMinV(), maxV);
    }

    GL11.glPopAttrib();
    GL11.glPopMatrix();

    Minecraft.getMinecraft().entityRenderer.enableLightmap();

  }

  private Vector3d forward = new Vector3d();
  private Vector3d left = new Vector3d();
  private Vector3d up = new Vector3d();
  private Vector3d offset = new Vector3d();

  private void drawSwitch(EnumFacing dir, BoundingBox bb) {

    Vector3d cent = bb.getCenter();
    offset.set(cent);

    boolean isUp = dir.getFrontOffsetY() != 0;
    forward.set(ForgeDirectionOffsets.forDir(dir));
    forward.scale(0.5);
    forward.x *= bb.sizeX();
    forward.y *= bb.sizeY();
    forward.z *= bb.sizeZ();

    offset.add(forward);

    if (dir.getFrontOffsetY() == 0) {
      offset.y += bb.sizeY() * 0.25;
    }
    if (dir.getFrontOffsetX() == 0) {
      offset.x -= (isUp ? dir.getFrontOffsetY() : dir.getFrontOffsetZ()) * bb.sizeX() * 0.25;
    }
    if (dir.getFrontOffsetZ() == 0) {
      offset.z += (isUp ? -dir.getFrontOffsetY() : dir.getFrontOffsetX()) * bb.sizeZ() * 0.25;
    }

    left.set(isUp ? -dir.getFrontOffsetY() : -dir.getFrontOffsetZ(), 0, dir.getFrontOffsetX());

    if (isUp) {
      up.set(0, 0, -1);
    } else {
      up.set(0, 1, 0);
    }

    forward.scale(0.5);
    left.scale(0.125);
    up.scale(0.125);

    TextureAtlasSprite icon = block.switchIcon;
    if (icon != null) {

      WorldRenderer tes = Tessellator.getInstance().getWorldRenderer();
      tes.pos(offset.x + left.x - up.x, offset.y + left.y - up.y, offset.z + left.z - up.z).tex(icon.getMinU(), icon.getMaxV()).endVertex();
      tes.pos(offset.x - left.x - up.x, offset.y - left.y - up.y, offset.z - left.z - up.z).tex(icon.getMaxU(), icon.getMaxV()).endVertex();
      tes.pos(offset.x - left.x + up.x, offset.y - left.y + up.y, offset.z - left.z + up.z).tex(icon.getMaxU(), icon.getMinV()).endVertex();
      tes.pos(offset.x + left.x + up.x, offset.y + left.y + up.y, offset.z + left.z + up.z).tex(icon.getMinU(), icon.getMinV()).endVertex();
    }

  }

  private ResourceLocation getLiquidSheet() {
    if (texName == null) {
      texName = TextureMap.locationBlocksTexture;
    }
    return texName;
  }

  private TextureAtlasSprite getLiquidTexture() {
    if (tex == null) {
      tex = RenderUtil.getStillTexture(FluidRegistry.WATER);
    }
    return tex;
  }

}
