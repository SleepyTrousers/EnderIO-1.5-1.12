package crazypants.enderio.teleport.anchor;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL14;

import com.enderio.core.client.render.RenderUtil;
import com.enderio.core.common.util.BlockCoord;
import com.enderio.core.common.util.Util;
import com.enderio.core.common.vecmath.Vector3d;
import com.enderio.core.common.vecmath.Vector3f;
import com.enderio.core.common.vecmath.Vector4f;

import crazypants.enderio.api.teleport.ITravelAccessable;
import crazypants.enderio.api.teleport.TravelSource;
import crazypants.enderio.teleport.TravelController;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class TravelEntitySpecialRenderer extends TileEntitySpecialRenderer<TileEntity> {

  private final Vector4f selectedColor = new Vector4f(1, 0.25f, 0, 0.5f);
  private final Vector4f itemBlend = new Vector4f(0.3f, 0.3f, 0.3f, 0.3f);
  private final Vector4f blockBlend = new Vector4f(0.6f, 0.6f, 0.6f, 0.4f);
  private final Vector4f selectedBlockBlend = new Vector4f(0.9f, 0.33f, 0.1f, 0.35f);

  public TravelEntitySpecialRenderer() {
  }

  @Override
  public void renderTileEntityAt(TileEntity tileentity, double x, double y, double z, float f, int destroyStage) {

    if(!TravelController.instance.showTargets()) {
      return;
    }

    ITravelAccessable ta = (ITravelAccessable) tileentity;

    BlockCoord onBlock = TravelController.instance.onBlockCoord;
    if(onBlock != null && onBlock.equals(ta.getLocation())) {
      return;
    }
    if(!ta.canSeeBlock(Minecraft.getMinecraft().thePlayer)) {
      return;
    }

    Vector3d eye = Util.getEyePositionEio(Minecraft.getMinecraft().thePlayer);
    Vector3d loc = new Vector3d(tileentity.getPos().getX() + 0.5, tileentity.getPos().getY()+ 0.5, tileentity.getPos().getZ()+ 0.5);
    double maxDistance = TravelController.instance.isTravelItemActiveForRendering(Minecraft.getMinecraft().thePlayer) ? TravelSource.STAFF.getMaxDistanceTravelledSq()
        : TravelSource.BLOCK.getMaxDistanceTravelledSq();
    if(eye.distanceSquared(loc) > maxDistance) {
      return;
    }

    double sf = TravelController.instance.getScaleForCandidate(loc);
    boolean highlight = TravelController.instance.isBlockSelected(ta.getLocation());

    TravelController.instance.addCandidate(ta.getLocation());

    Minecraft.getMinecraft().entityRenderer.disableLightmap();

    RenderUtil.bindBlockTexture();
    GlStateManager.enableRescaleNormal();
    GlStateManager.disableDepth();
    GlStateManager.disableLighting();
    GlStateManager.enableBlend();
    GlStateManager.color(1, 1, 1, 1);

    GlStateManager.pushMatrix();
    GlStateManager.translate(x, y, z);
    renderBlock(tileentity.getPos(), tileentity.getWorld(), sf, highlight);
    renderItemLabel(ta.getItemLabel(), sf);
    renderLabel(ta.getLabel(), sf, highlight);
    GlStateManager.popMatrix();

    GlStateManager.enableLighting();
    GlStateManager.enableDepth();
    GlStateManager.disableRescaleNormal();
    Minecraft.getMinecraft().entityRenderer.enableLightmap();

  }

  private void renderItemLabel(ItemStack itemLabel, double globalScale) {
    if (itemLabel != null && itemLabel.getItem() != null) {
      RenderManager renderManager = Minecraft.getMinecraft().getRenderManager();
      RenderItem itemRenderer = Minecraft.getMinecraft().getRenderItem();

      GlStateManager.pushMatrix();
      GlStateManager.translate(0.5f, 0.75f, 0.5f);
      // TODO: This doesn't work that well with 3D items, find a rotation that does
      GlStateManager.rotate(-renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
      GlStateManager.rotate((renderManager.options.thirdPersonView == 2 ? -1 : 1) * renderManager.playerViewX, 1.0F, 0.0F, 0.0F);
      GlStateManager.scale(globalScale, globalScale, globalScale);
      GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);

      RenderHelper.enableStandardItemLighting();

      IBakedModel bakedmodel = itemRenderer.getItemModelWithOverrides(itemLabel, (World) null, (EntityLivingBase) null);
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_CONSTANT_COLOR, GlStateManager.SourceFactor.ONE.factor,
          GlStateManager.DestFactor.ZERO.factor);
      GL14.glBlendColor(itemBlend.x, itemBlend.y, itemBlend.z, itemBlend.w);
      bakedmodel = ForgeHooksClient.handleCameraTransforms(bakedmodel, ItemCameraTransforms.TransformType.GUI, false);
      itemRenderer.renderItem(itemLabel, bakedmodel);

      RenderHelper.disableStandardItemLighting();

      GL14.glBlendColor(1, 1, 1, 1);
      GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
      GlStateManager.popMatrix();
    }
  }

  private void renderLabel(String toRender, double globalScale, boolean highlight) {
    if (toRender != null && toRender.trim().length() > 0) {
      GlStateManager.color(1, 1, 1, 1);
      Vector4f bgCol = RenderUtil.DEFAULT_TEXT_BG_COL;
      if (highlight) {
        bgCol = new Vector4f(selectedColor.x, selectedColor.y, selectedColor.z, selectedColor.w);
      }

      GlStateManager.pushMatrix();
      GlStateManager.translate(0.5f, 0.5f, 0.5f);
      GlStateManager.scale(globalScale, globalScale, globalScale);
      Vector3f pos = new Vector3f(0, 1.2f, 0);
      float size = 0.5f;
      RenderUtil.drawBillboardedText(pos, toRender, size, bgCol);
      GL11.glPopMatrix();
    }
  }

  public void renderBlock(BlockPos pos, IBlockAccess blockAccess, double globalScale, boolean highlight) {
    VertexBuffer tes = Tessellator.getInstance().getBuffer();
    BlockRendererDispatcher blockrendererdispatcher = Minecraft.getMinecraft().getBlockRendererDispatcher();

    GlStateManager.pushMatrix();
    GlStateManager.translate(0.5f, 0.5f, 0.5f);
    GlStateManager.scale(globalScale, globalScale, globalScale);
    GlStateManager.translate(-0.5f, -0.5f, -0.5f);
    IBlockState state = blockAccess.getBlockState(pos).getActualState(blockAccess, pos);
    IBakedModel ibakedmodel = blockrendererdispatcher.getModelForState(state);
    state = state.getBlock().getExtendedState(state, blockAccess, pos);

    tes.setTranslation(-pos.getX(), -pos.getY(), -pos.getZ());
    Vector4f color = highlight ? selectedBlockBlend : blockBlend;

    GlStateManager.color(1, 1, 1, 1);
    GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_CONSTANT_COLOR, GlStateManager.SourceFactor.ONE.factor,
        GlStateManager.DestFactor.ZERO.factor);
    GL14.glBlendColor(color.x, color.y, color.z, color.w);

    tes.begin(GL11.GL_QUADS, DefaultVertexFormats.BLOCK);
    for (BlockRenderLayer layer : BlockRenderLayer.values()) {
      if (state.getBlock().canRenderInLayer(state, layer)) {
        ForgeHooksClient.setRenderLayer(layer);
        blockrendererdispatcher.getBlockModelRenderer().renderModel(blockAccess, ibakedmodel, state, pos, tes, false);
      }
    }
    ForgeHooksClient.setRenderLayer(null);
    Tessellator.getInstance().draw();

    GL14.glBlendColor(1, 1, 1, 1);
    GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
    tes.setTranslation(0, 0, 0);

    GlStateManager.popMatrix();
  }

}
