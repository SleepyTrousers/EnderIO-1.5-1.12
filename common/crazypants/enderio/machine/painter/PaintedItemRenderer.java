package crazypants.enderio.machine.painter;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.IItemRenderer;

import org.lwjgl.opengl.GL11;

public class PaintedItemRenderer implements IItemRenderer {

  @Override
  public boolean handleRenderType(ItemStack item, ItemRenderType type) {
    return type == ItemRenderType.ENTITY || type == ItemRenderType.EQUIPPED || type == ItemRenderType.INVENTORY || type == ItemRenderType.EQUIPPED_FIRST_PERSON;
  }

  @Override
  public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper) {
    return type == ItemRenderType.INVENTORY || type == ItemRenderType.ENTITY;
  }

  @Override
  public void renderItem(ItemRenderType type, ItemStack item, Object... data) {

    if (type == ItemRenderType.INVENTORY) {
      RenderBlocks renderBlocks = (RenderBlocks) data[0];
      renderToInventory(item, renderBlocks);
    } else if (type == ItemRenderType.EQUIPPED) {      
      renderEquipped(item, (RenderBlocks) data[0]);
    } else if (type == ItemRenderType.ENTITY || type == ItemRenderType.EQUIPPED_FIRST_PERSON) {
      renderEntity(item, (RenderBlocks) data[0]);
    } else {
      System.out.println("CustomFenceRenderer.renderItem: Unsupported render type");
    }

  }
  
  public void renderEntity(ItemStack item, RenderBlocks renderBlocks) {
    Block block = PainterUtil.getSourceBlock(item);
    if (block != null) {
      renderBlocks.setOverrideBlockTexture(renderBlocks.getBlockIconFromSideAndMetadata(block, 2, item.getItemDamage()));
    }     
    renderBlocks.renderBlockAsItem(Block.blocksList[item.itemID], item.getItemDamage(), 1.0f);
    renderBlocks.clearOverrideBlockTexture();
  }

  public void renderEquipped(ItemStack item, RenderBlocks renderBlocks) {
    Block block = PainterUtil.getSourceBlock(item);
    if (block != null) {
      renderBlocks.setOverrideBlockTexture(renderBlocks.getBlockIconFromSideAndMetadata(block, 2, item.getItemDamage()));
    }
    GL11.glPushMatrix();
    GL11.glTranslatef(0.75F, 0.0F, 0.0F);
    renderBlocks.renderBlockAsItem(Block.blocksList[item.itemID], item.getItemDamage(), 1.0f);
    GL11.glPopMatrix();
    renderBlocks.clearOverrideBlockTexture();
  }

  public void renderToInventory(ItemStack item, RenderBlocks renderBlocks) {
    Block block = PainterUtil.getSourceBlock(item);
    if (block != null) {
      renderBlocks.setOverrideBlockTexture(renderBlocks.getBlockIconFromSideAndMetadata(block, 2, item.getItemDamage()));
    }    
    renderBlocks.renderBlockAsItem(Block.blocksList[item.itemID], item.getItemDamage(), 1.0f);
    renderBlocks.clearOverrideBlockTexture();
  }

}
