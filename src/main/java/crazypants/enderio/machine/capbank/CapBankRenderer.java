package crazypants.enderio.machine.capbank;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.common.util.ForgeDirection;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import crazypants.enderio.EnderIO;
import crazypants.render.ConnectedTextureRenderer;
import crazypants.render.CubeRenderer;
import crazypants.render.CustomCubeRenderer;
import crazypants.render.RenderUtil;

public class CapBankRenderer implements ISimpleBlockRenderingHandler, IItemRenderer {

  private ConnectedTextureRenderer connectedTexRenderer;

  public CapBankRenderer() {
    connectedTexRenderer = new ConnectedTextureRenderer();
    connectedTexRenderer.setMatchMeta(true);
  }

  //------- Block

  @Override
  public void renderInventoryBlock(Block block, int metadata, int modelId, RenderBlocks renderer) {
  }

  @Override
  public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer) {

    int meta = world.getBlockMetadata(x, y, z);
    meta = MathHelper.clamp_int(meta, 0, CapBankType.types().size() - 1);
    CapBankType type = CapBankType.getTypeFromMeta(meta);
    if(!type.isMultiblock()) {
      connectedTexRenderer.setForceAllEdges(true);
    } else {
      connectedTexRenderer.setForceAllEdges(false);
    }
    connectedTexRenderer.setEdgeTexture(EnderIO.blockCapBank.getBorderIcon(0, meta));
    CustomCubeRenderer.instance.renderBlock(world, block, x, y, z, connectedTexRenderer);
    return true;
  }

  @Override
  public boolean shouldRender3DInInventory(int modelId) {
    return true;
  }

  @Override
  public int getRenderId() {
    return BlockCapBank.renderId;
  }

  //------- Item 
  @Override
  public boolean handleRenderType(ItemStack item, ItemRenderType type) {
    return true;
  }

  @Override
  public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper) {
    return true;
  }

  @Override
  public void renderItem(ItemRenderType type, ItemStack item, Object... data) {

    RenderUtil.bindBlockTexture();
    Tessellator tes = Tessellator.instance;

    tes.startDrawingQuads();
    CubeRenderer.render(EnderIO.blockCapBank, item.getItemDamage());
    tes.draw();

    GL11.glEnable(GL11.GL_POLYGON_OFFSET_FILL);
    GL11.glPolygonOffset(-1.0f, -1.0f);

    tes.startDrawingQuads();
    tes.setColorRGBA_F(1, 1, 1, 1);
    renderBorder(null, 0, 0, 0, item.getItemDamage());
    tes.draw();

    GL11.glDisable(GL11.GL_POLYGON_OFFSET_FILL);

  }

  private void renderBorder(IBlockAccess blockAccess, int x, int y, int z, int meta) {
    IIcon texture = EnderIO.blockCapBank.getBorderIcon(0, meta);
    for (ForgeDirection face : ForgeDirection.VALID_DIRECTIONS) {
      RenderUtil.renderConnectedTextureFace(blockAccess, x, y, z, face, texture,
          blockAccess == null, false, false);
    }
  }

}
