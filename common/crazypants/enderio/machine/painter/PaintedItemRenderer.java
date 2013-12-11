package crazypants.enderio.machine.painter;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.IItemRenderer;

import org.lwjgl.opengl.GL11;

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
    Block block = PainterUtil.getSourceBlock(item);

    if(block != null) {
      int meta = PainterUtil.getSourceBlockMetadata(item);
      renderBlocks.setOverrideBlockTexture(renderBlocks.getBlockIconFromSideAndMetadata(block, 2, meta));
    }

    GL11.glPushMatrix();
    GL11.glTranslatef(0.5F, 0.5F, 0.5F);
    renderBlocks.renderBlockAsItem(Block.blocksList[item.itemID], item.getItemDamage(), 1.0f);
    GL11.glPopMatrix();
    renderBlocks.clearOverrideBlockTexture();
  }

  public void renderToInventory(ItemStack item, RenderBlocks renderBlocks) {
    Block block = PainterUtil.getSourceBlock(item);
    if(block != null) {
      int meta = PainterUtil.getSourceBlockMetadata(item);
      renderBlocks.setOverrideBlockTexture(renderBlocks.getBlockIconFromSideAndMetadata(block, 2, meta));
    }
    renderBlocks.renderBlockAsItem(Block.blocksList[item.itemID], item.getItemDamage(), 1.0f);
    renderBlocks.clearOverrideBlockTexture();
  }

}
