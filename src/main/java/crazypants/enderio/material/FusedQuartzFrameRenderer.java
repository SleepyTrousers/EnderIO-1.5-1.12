package crazypants.enderio.material;

import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.IItemRenderer;

import org.lwjgl.opengl.GL11;

public class FusedQuartzFrameRenderer implements IItemRenderer {

    @Override
    public boolean handleRenderType(ItemStack item, ItemRenderType type) {
        return type == ItemRenderType.ENTITY || type == ItemRenderType.EQUIPPED
                || type == ItemRenderType.INVENTORY
                || type == ItemRenderType.EQUIPPED_FIRST_PERSON;
    }

    @Override
    public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper) {
        return true;
    }

    @Override
    public void renderItem(ItemRenderType type, ItemStack item, Object... data) {
        RenderBlocks renderBlocks = (RenderBlocks) data[0];
        if (type == ItemRenderType.INVENTORY) {
            renderToInventory(item, renderBlocks);
        } else if (type == ItemRenderType.EQUIPPED) {
            renderEquipped(item, renderBlocks);
        } else if (type == ItemRenderType.ENTITY) {
            renderEntity(item, renderBlocks);
        } else if (type == ItemRenderType.EQUIPPED_FIRST_PERSON) {
            renderEntity(item, renderBlocks);
        }
    }

    private void renderEntity(ItemStack item, RenderBlocks renderBlocks) {
        renderToInventory(item, renderBlocks);
    }

    private void renderEquipped(ItemStack item, RenderBlocks renderBlocks) {
        renderToInventory(item, renderBlocks);
    }

    private void renderToInventory(ItemStack item, RenderBlocks renderBlocks) {
        renderFrame(item);
    }

    private void renderFrame(ItemStack item) {
        GL11.glDisable(GL11.GL_LIGHTING);
        FusedQuartzRenderer r = new FusedQuartzRenderer();
        r.renderFrameItem(item);
        GL11.glEnable(GL11.GL_LIGHTING);
    }
}
