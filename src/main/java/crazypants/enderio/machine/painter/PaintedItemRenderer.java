package crazypants.enderio.machine.painter;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.IItemRenderer;

import org.lwjgl.opengl.GL11;

import com.enderio.core.client.render.RenderUtil;

import crazypants.enderio.EnderIO;

public class PaintedItemRenderer implements IItemRenderer {

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

    if(data != null && data.length > 0) {
      if(type == ItemRenderType.EQUIPPED_FIRST_PERSON || type == ItemRenderType.EQUIPPED) {
        renderEquipped(item, (RenderBlocks) data[0]);
      } else {
        renderToInventory(item, (RenderBlocks) data[0]);
      }
    }

  }

  public void renderEquipped(ItemStack item, RenderBlocks renderBlocks) {

    GL11.glPushMatrix();
    GL11.glTranslatef(0.5F, 0.5F, 0.5F);
    renderToInventory(item, renderBlocks);
    GL11.glPopMatrix();

  }

  public void renderToInventory(ItemStack item, RenderBlocks renderBlocks) {
    if(item.getItem() == Item.getItemFromBlock(EnderIO.blockPaintedGlowstone) || item.getItem() == Item.getItemFromBlock(EnderIO.blockTravelPlatform)) {
      Block block = PainterUtil.getSourceBlock(item);
      int meta;
      RenderUtil.bindBlockTexture();
      boolean renderOverlay = true;
      if(block != null) {
        meta = PainterUtil.getSourceBlockMetadata(item);
      } else {
        block = Block.getBlockFromItem(item.getItem());
        meta = item.getItemDamage();
        renderOverlay = false;
      }
      renderBlocks.renderBlockAsItem(block, meta, 1.0F);
      if(renderOverlay) {
        renderOverlay(block, meta, renderBlocks);
      }
      renderBlocks.clearOverrideBlockTexture();
      return;
    }

    Block block = PainterUtil.getSourceBlock(item);    
    if(block != null) {
      int meta = PainterUtil.getSourceBlockMetadata(item);
      if(block == EnderIO.blockFusedQuartz && meta == 1) {
        renderBlocks.setOverrideBlockTexture(EnderIO.blockPainter.getInvisibleIcon());
      } else {
        renderBlocks.setOverrideBlockTexture(renderBlocks.getBlockIconFromSideAndMetadata(block, 2, meta));
      }
    }
    Item i = item.getItem();
    if(i instanceof ItemBlock) {

      Block blk = ((ItemBlock) i).field_150939_a;
      int meta = item.getItemDamage();
      renderBlocks.renderBlockAsItem(blk, meta, 1.0f);
      renderOverlay(blk, meta, renderBlocks);

    }
    renderBlocks.clearOverrideBlockTexture();

  }

  protected void renderOverlay(Block block, int meta, RenderBlocks renderBlocks) {
    GL11.glPushAttrib(GL11.GL_ENABLE_BIT);

    GL11.glDisable(GL11.GL_LIGHTING);
    GL11.glEnable(GL11.GL_BLEND);
    GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

    GL11.glEnable(GL11.GL_POLYGON_OFFSET_FILL);
    GL11.glPolygonOffset(-1.0f, -1.0f);

    RenderUtil.bindItemTexture();
    renderBlocks.setOverrideBlockTexture(EnderIO.itemConduitFacade.getOverlayIcon());
    renderBlocks.renderBlockAsItem(block, meta, 1.0f);

    GL11.glPopAttrib();

  }

}
