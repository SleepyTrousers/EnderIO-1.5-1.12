package crazypants.enderio.conduit.render;

import com.enderio.core.client.render.BoundingBox;
import com.enderio.core.client.render.CubeRenderer;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.IItemRenderer;
import org.lwjgl.opengl.GL11;

public class ItemConduitRenderer implements IItemRenderer {

    private final BoundingBox bb;

    public ItemConduitRenderer() {
        float scale = 0.8f;
        bb = BoundingBox.UNIT_CUBE.scale(scale, scale, scale);
    }

    @Override
    public boolean handleRenderType(ItemStack item, ItemRenderType type) {
        return type == ItemRenderType.ENTITY
                || type == ItemRenderType.EQUIPPED
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
            GL11.glTranslatef(0, -0.1f, 0);
            renderToInventory(item, renderBlocks);
        } else if (type == ItemRenderType.EQUIPPED) {
            renderEquipped(item, renderBlocks);
        } else if (type == ItemRenderType.ENTITY) {
            renderEntity(item, renderBlocks);
        } else if (type == ItemRenderType.EQUIPPED_FIRST_PERSON) {
            GL11.glTranslatef(0.3f, 0.2f, 0);
            GL11.glScalef(1.25f, 1.25f, 1.25f);
            renderEntity(item, renderBlocks);
        } else {
            System.out.println("ItemConduitRenderer.renderItem: Unsupported render type");
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
        Tessellator.instance.startDrawingQuads();
        CubeRenderer.render(bb, item.getItem().getIconFromDamage(item.getItemDamage()));
        Tessellator.instance.draw();
    }
}
