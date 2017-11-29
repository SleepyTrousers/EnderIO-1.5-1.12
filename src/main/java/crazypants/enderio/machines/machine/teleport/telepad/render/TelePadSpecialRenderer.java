package crazypants.enderio.machines.machine.teleport.telepad.render;

import static crazypants.enderio.config.Config.telepadIsTravelAnchor;
import static crazypants.enderio.machines.init.MachineObject.block_tele_pad;

import java.util.Random;

import javax.annotation.Nonnull;

import org.lwjgl.opengl.GL11;

import crazypants.enderio.machines.machine.teleport.anchor.TravelEntitySpecialRenderer;
import crazypants.enderio.machines.machine.teleport.telepad.TileTelePad;
import crazypants.enderio.render.property.EnumRenderMode;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class TelePadSpecialRenderer extends TravelEntitySpecialRenderer<TileTelePad> {

  private final IBlockState blade, lights, glass;

  public TelePadSpecialRenderer() {
    super(block_tele_pad.getBlock());
    blade = block_tele_pad.getBlock().getDefaultState().withProperty(EnumRenderMode.RENDER, EnumRenderMode.FRONT_WEST);
    lights = block_tele_pad.getBlock().getDefaultState().withProperty(EnumRenderMode.RENDER, EnumRenderMode.FRONT_ON_WEST);
    glass = block_tele_pad.getBlock().getDefaultState().withProperty(EnumRenderMode.RENDER, EnumRenderMode.FRONT_ON);

  }

  @Override
  public boolean shouldRender(@Nonnull TileTelePad te, @Nonnull IBlockState blockState, int renderPass) {
    return te.isMaster();
  }

  @Override
  public void renderTileEntity(@Nonnull TileTelePad te, @Nonnull IBlockState blockState, float partialTicks, int destroyStage) {
    if (MinecraftForgeClient.getRenderPass() == 0) {

      render(te, lights);

      Random rand = new Random(te.getPos().toLong());
      for (int i = 0; i < 3; i++) {
        GlStateManager.pushMatrix();
        GlStateManager.translate(0.5, 0, 0.5);
        GlStateManager.rotate(te.bladeRots[i] + rand.nextInt(360) + (te.spinSpeed * partialTicks * ((i * 2) + 20)), 0, 1, 0);
        GlStateManager.translate(-0.5, i * 2f / 16f, -0.5);
        render(te, blade);
        GlStateManager.popMatrix();
      }
    } else {
      RenderHelper.disableStandardItemLighting();

      render(te, glass);

      if (telepadIsTravelAnchor && super.shouldRender(te, blockState, 1)) {
        RenderHelper.enableStandardItemLighting();
        super.renderTileEntity(te, blockState, partialTicks, destroyStage);
      }
    }
  }

  public void render(TileEntity tileEntity, final IBlockState state) {
    final BlockPos pos = tileEntity.getPos();
    final Tessellator tessellator = Tessellator.getInstance();
    final VertexBuffer vertexBuffer = tessellator.getBuffer();
    final BlockRendererDispatcher blockrendererdispatcher = Minecraft.getMinecraft().getBlockRendererDispatcher();
    final IBakedModel ibakedmodel = blockrendererdispatcher.getBlockModelShapes().getModelForState(state);

    vertexBuffer.begin(GL11.GL_QUADS, DefaultVertexFormats.BLOCK);

    vertexBuffer.setTranslation(-pos.getX(), -pos.getY(), -pos.getZ());
    blockrendererdispatcher.getBlockModelRenderer().renderModel(tileEntity.getWorld(), ibakedmodel, state, pos, vertexBuffer, false);
    vertexBuffer.setTranslation(0, 0, 0);
    tessellator.draw();
  }

}
