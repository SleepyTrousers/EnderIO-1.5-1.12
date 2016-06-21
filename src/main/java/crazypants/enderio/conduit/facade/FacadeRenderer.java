package crazypants.enderio.conduit.facade;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.IItemRenderer;

import org.lwjgl.opengl.GL11;

import com.enderio.core.client.render.RenderUtil;

import crazypants.enderio.EnderIO;
import crazypants.enderio.machine.painter.PainterUtil;

public class FacadeRenderer implements IItemRenderer {

  @Override
  public boolean handleRenderType(ItemStack item, ItemRenderType type) {
    return type == ItemRenderType.ENTITY || type == ItemRenderType.EQUIPPED || type == ItemRenderType.INVENTORY || type == ItemRenderType.EQUIPPED_FIRST_PERSON;
  }

  @Override
  public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper) {
    return true;
  }

  @Override
  public void renderItem(ItemRenderType type, ItemStack item, Object... data) {
    if(type == ItemRenderType.INVENTORY) {
      RenderBlocks renderBlocks = (RenderBlocks) data[0];
      renderToInventory(item, renderBlocks);
    } else if(type == ItemRenderType.EQUIPPED || type == ItemRenderType.EQUIPPED_FIRST_PERSON) {
      renderEquipped(item, (RenderBlocks) data[0]);
    } else if(type == ItemRenderType.ENTITY) {
      renderEntity(item, (RenderBlocks) data[0]);
    } else {
      System.out.println("FacadeRenderer.renderItem: Unsupported render type");
    }
  }

  private void renderEntity(ItemStack item, RenderBlocks renderBlocks) {
    GL11.glPushMatrix();
    GL11.glScalef(0.5f, 0.5f, 0.5f);
    renderToInventory(item, renderBlocks);
    GL11.glPopMatrix();
  }

  private void renderEquipped(ItemStack item, RenderBlocks renderBlocks) {
    renderToInventory(item, renderBlocks);
  }

  private void renderToInventory(ItemStack item, RenderBlocks renderBlocks) {

    Block block = PainterUtil.getSourceBlock(item);
    if(block != null) {
      // Render the facade block

      RenderUtil.bindBlockTexture();
      if(!block.isOpaqueCube()) {
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
      }

      if("appeng.block.solids.BlockSkyStone".equals(block.getClass().getName())) {
        //Yes, this is a horrible hack, but stumped as to why it is rendered invisible if this isn't done.
        renderBlocks.setOverrideBlockTexture(block.getIcon(0, PainterUtil.getSourceBlockMetadata(item)));
        renderBlocks.renderBlockAsItem(Blocks.stone, 0, 1.0F);
      } else {
        renderBlocks.renderBlockAsItem(block, PainterUtil.getSourceBlockMetadata(item), 1.0F);
      }

      // then the 'overlay' that marks it as a facade
      GL11.glDepthFunc(GL11.GL_LEQUAL);
      GL11.glDisable(GL11.GL_LIGHTING);
      GL11.glDepthMask(false);
      GL11.glEnable(GL11.GL_BLEND);
      GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

      GL11.glEnable(GL11.GL_POLYGON_OFFSET_FILL);
      GL11.glPolygonOffset(-1.0f, -1.0f);

      RenderUtil.bindItemTexture();
      renderBlocks.setOverrideBlockTexture(EnderIO.itemConduitFacade.getOverlayIcon());
      renderBlocks.renderBlockAsItem(Blocks.stone, item.getItemDamage(), 1.0F);

      GL11.glDisable(GL11.GL_POLYGON_OFFSET_FILL);

      renderBlocks.clearOverrideBlockTexture();
      GL11.glDisable(GL11.GL_BLEND);
      GL11.glDepthMask(true);
      GL11.glEnable(GL11.GL_LIGHTING);
      GL11.glDepthFunc(GL11.GL_LEQUAL);

    } else {
      renderBlocks.setOverrideBlockTexture(EnderIO.itemConduitFacade.getIconFromDamage(item.getItemDamage()));
      renderBlocks.renderBlockAsItem(Blocks.stone, 0, 1.0F);
      renderBlocks.clearOverrideBlockTexture();
    }

  }

}
