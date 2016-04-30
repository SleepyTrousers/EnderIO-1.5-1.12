package crazypants.enderio.machine.soul;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockModelShapes;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.world.World;
import net.minecraftforge.client.ForgeHooksClient;

import org.lwjgl.opengl.GL11;

import com.enderio.core.client.render.RenderUtil;

import crazypants.enderio.render.EnumRenderMode;

public class SoulBinderTESR extends TileEntitySpecialRenderer<TileSoulBinder> {

  @Override
  public void renderTileEntityAt(TileSoulBinder te, double x, double y, double z, float partialTicks, int destroyStage) {
    if (te.isWorking() && te.getPaintSource() == null) {
      RenderUtil.setupLightmapCoords(te.getPos(), te.getWorld());
      GL11.glPushMatrix();
      GL11.glTranslatef((float) x, (float) y, (float) z);

      GL11.glPushMatrix();
      GL11.glTranslatef(0.5f, 0, 0.5f);
      GL11.glRotatef(360 * te.getProgress(), 0, 1, 0);
      GL11.glTranslatef(-0.5f, 0, -0.5f);

      GL11.glDisable(GL11.GL_LIGHTING);
      GlStateManager.color(1, 1, 1);
      RenderUtil.bindBlockTexture();

      EnumRenderMode renderMode = te.isActive() ? EnumRenderMode.FRONT_ON : EnumRenderMode.FRONT;
      renderBlockModel(te.getWorld(), te.getPos(),
          te.getWorld().getBlockState(te.getPos()).withProperty(EnumRenderMode.RENDER, renderMode.rotate(te.getFacing())), true);

      GL11.glEnable(GL11.GL_LIGHTING);

      GL11.glPopMatrix();
      GL11.glPopMatrix();
    }
  }

  public static void renderBlockModel(World world, BlockPos pos, IBlockState state, boolean translateToOrigin) {

    WorldRenderer wr = Tessellator.getInstance().getWorldRenderer();
    wr.begin(GL11.GL_QUADS, DefaultVertexFormats.BLOCK);
    if (translateToOrigin) {
      wr.setTranslation(-pos.getX(), -pos.getY(), -pos.getZ());
    }
    // TODO: Need to setup GL state correctly for each layer
    BlockRendererDispatcher blockrendererdispatcher = Minecraft.getMinecraft().getBlockRendererDispatcher();
    BlockModelShapes modelShapes = blockrendererdispatcher.getBlockModelShapes();
    IBakedModel ibakedmodel = modelShapes.getModelForState(state);
    for (EnumWorldBlockLayer layer : EnumWorldBlockLayer.values()) {
      ForgeHooksClient.setRenderLayer(layer);
      blockrendererdispatcher.getBlockModelRenderer().renderModel(world, ibakedmodel, state, pos, Tessellator.getInstance().getWorldRenderer());
    }
    if (translateToOrigin) {
      wr.setTranslation(0, 0, 0);
    }
    Tessellator.getInstance().draw();
  }

}
