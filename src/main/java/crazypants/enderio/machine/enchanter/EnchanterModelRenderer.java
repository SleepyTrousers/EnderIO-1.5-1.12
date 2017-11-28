package crazypants.enderio.machine.enchanter;

import static crazypants.enderio.machine.MachineObject.blockEnchanter;

import java.util.List;

import javax.annotation.Nonnull;

import com.enderio.core.client.render.ManagedTESR;
import com.enderio.core.client.render.RenderUtil;

import crazypants.enderio.machine.MachineObject;
import crazypants.enderio.render.property.EnumRenderMode;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockModelShapes;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.client.model.pipeline.LightUtil;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class EnchanterModelRenderer extends ManagedTESR<TileEnchanter> {

  public EnchanterModelRenderer() {
    super(blockEnchanter.getBlock());
  }

  @Nonnull
  private static final String TEXTURE = "enderio:textures/blocks/book_stand.png";

  private EnchanterModel model = new EnchanterModel();

  @Override
  protected void renderTileEntity(@Nonnull TileEnchanter te, @Nonnull IBlockState blockState, float partialTicks, int destroyStage) {
    renderModel(te.getFacing());
  }

  @Override
  protected void renderItem() {
    renderBase();
    renderModel(EnumFacing.NORTH);
  }

  private void renderBase() {
    BlockRendererDispatcher blockrendererdispatcher = Minecraft.getMinecraft().getBlockRendererDispatcher();
    BlockModelShapes modelShapes = blockrendererdispatcher.getBlockModelShapes();
    IBakedModel bakedModel = modelShapes
        .getModelForState(MachineObject.blockEnchanter.getBlock().getDefaultState().withProperty(EnumRenderMode.RENDER, EnumRenderMode.FRONT));

    RenderUtil.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
    GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
    GlStateManager.pushMatrix();

    Tessellator tessellator = Tessellator.getInstance();
    VertexBuffer vertexbuffer = tessellator.getBuffer();
    vertexbuffer.begin(7, DefaultVertexFormats.ITEM);

    for (EnumFacing enumfacing : EnumFacing.values()) {
      this.renderQuads(vertexbuffer, bakedModel.getQuads((IBlockState) null, enumfacing, 0L));
    }

    this.renderQuads(vertexbuffer, bakedModel.getQuads((IBlockState) null, (EnumFacing) null, 0L));
    tessellator.draw();

    GlStateManager.popMatrix();
  }

  private void renderQuads(VertexBuffer renderer, List<BakedQuad> quads) {
    for (BakedQuad quad : quads) {
      LightUtil.renderQuadColor(renderer, quad, -1);
    }
  }

  private void renderModel(EnumFacing facing) {

    GlStateManager.pushMatrix();

    GlStateManager.translate(0.5, 1.5, 0.5);
    GlStateManager.rotate(180, 1, 0, 0);

    GlStateManager.rotate(facing.getHorizontalIndex() * 90f, 0, 1, 0);

    RenderUtil.bindTexture(TEXTURE);
    model.render((Entity) null, 0.0F, 0.0F, -0.1F, 0.0F, 0.0F, 0.0625F - 0.006f);

    GlStateManager.popMatrix();
  }

}
