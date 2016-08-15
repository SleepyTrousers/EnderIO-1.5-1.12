package crazypants.enderio.teleport.telepad;

import java.util.Random;

import org.lwjgl.opengl.GL11;

import crazypants.enderio.EnderIO;
import crazypants.enderio.render.EnumRenderMode;
import crazypants.enderio.teleport.anchor.TravelEntitySpecialRenderer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import static crazypants.enderio.config.Config.telepadIsTravelAnchor;

@SideOnly(Side.CLIENT)
public class TelePadSpecialRenderer extends TravelEntitySpecialRenderer<TileTelePad> {

  private final IBlockState blade, lights, glass;

  public TelePadSpecialRenderer() {
    super();
    blade = EnderIO.blockTelePad.getDefaultState().withProperty(EnumRenderMode.RENDER, EnumRenderMode.FRONT_WEST);
    lights = EnderIO.blockTelePad.getDefaultState().withProperty(EnumRenderMode.RENDER, EnumRenderMode.FRONT_ON_WEST);
    glass = EnderIO.blockTelePad.getDefaultState().withProperty(EnumRenderMode.RENDER, EnumRenderMode.FRONT_ON);

  }

  @Override
  public void renderTileEntityAt(TileTelePad tileentity, double x, double y, double z, float partialTicks, int destroyStage) {
    if (tileentity.isMaster()) {
      if (MinecraftForgeClient.getRenderPass() == 0) {
        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y, z);

        RenderHelper.disableStandardItemLighting();

        if (!tileentity.active()) {
          GlStateManager.enableLighting();
        }

        render(tileentity, lights);

        Random rand = new Random(tileentity.getPos().toLong());
        for (int i = 0; i < 3; i++) {
          GlStateManager.pushMatrix();
          GlStateManager.translate(0.5, 0, 0.5);
          GlStateManager.rotate(tileentity.bladeRots[i] + rand.nextInt(360) + (tileentity.spinSpeed * partialTicks * ((i * 2) + 20)), 0, 1, 0);
          GlStateManager.translate(-0.5, i * 2f / 16f, -0.5);
          render(tileentity, blade);
          GlStateManager.popMatrix();
        }

        RenderHelper.enableStandardItemLighting();
        GlStateManager.popMatrix();
      } else {
        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y, z);
        RenderHelper.disableStandardItemLighting();

        render(tileentity, glass);

        RenderHelper.enableStandardItemLighting();
        GlStateManager.popMatrix();
        if (telepadIsTravelAnchor) {
          super.renderTileEntityAt(tileentity, x, y, z, partialTicks, destroyStage);
        }
      }
    }
  }

  public void render(TileEntity tileEntity, final IBlockState state) {
    final BlockPos pos = tileEntity.getPos();
    final Tessellator tessellator = Tessellator.getInstance();
    final VertexBuffer vertexBuffer = tessellator.getBuffer();
    final BlockRendererDispatcher blockrendererdispatcher = Minecraft.getMinecraft().getBlockRendererDispatcher();
    final IBakedModel ibakedmodel = blockrendererdispatcher.getBlockModelShapes().getModelForState(state);
    bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);

    if (Minecraft.isAmbientOcclusionEnabled()) {
      GlStateManager.shadeModel(GL11.GL_SMOOTH);
    } else {
      GlStateManager.shadeModel(GL11.GL_FLAT);
    }

    vertexBuffer.begin(GL11.GL_QUADS, DefaultVertexFormats.BLOCK);

    vertexBuffer.setTranslation(-pos.getX(), -pos.getY(), -pos.getZ());
    blockrendererdispatcher.getBlockModelRenderer().renderModel(tileEntity.getWorld(), ibakedmodel, state, pos, vertexBuffer, false);
    vertexBuffer.setTranslation(0, 0, 0);
    tessellator.draw();
  }

}
