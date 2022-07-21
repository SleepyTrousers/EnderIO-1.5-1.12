package crazypants.enderio.machine.spawner;

import crazypants.enderio.EnderIO;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.IItemRenderer;
import org.lwjgl.opengl.GL11;

public class BrokenSpawnerRenderer implements IItemRenderer {

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
        GL11.glPushAttrib(GL11.GL_ENABLE_BIT);
        GL11.glEnable(GL11.GL_ALPHA_TEST);

        if (type == ItemRenderType.ENTITY) {
            GL11.glPushMatrix();
            GL11.glScalef(0.5f, 0.5f, 0.5f);
        }

        RenderBlocks rb = (RenderBlocks) data[0];
        rb.setOverrideBlockTexture(EnderIO.itemBrokenSpawner.getIconFromDamage(0));
        rb.renderBlockAsItem(Blocks.stone, 0, 1);
        rb.setOverrideBlockTexture(null);

        if (type == ItemRenderType.ENTITY) {
            GL11.glPopMatrix();
        }

        GL11.glDisable(GL11.GL_ALPHA_TEST);
        GL11.glPopAttrib();
    }
}
