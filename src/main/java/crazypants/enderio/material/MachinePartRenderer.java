package crazypants.enderio.material;

import crazypants.enderio.EnderIO;
import crazypants.enderio.Log;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.IItemRenderer;
import org.lwjgl.opengl.GL11;

public class MachinePartRenderer implements IItemRenderer {

    private ItemRenderer itemRenderer = new ItemRenderer(Minecraft.getMinecraft());
    private RenderItem renderItem = new RenderItem();
    private boolean loggedError = false;

    public MachinePartRenderer() {}

    @Override
    public boolean handleRenderType(ItemStack item, ItemRenderType type) {
        if (item != null && item.getItemDamage() != MachinePart.BASIC_GEAR.ordinal()) {
            return type == ItemRenderType.ENTITY
                    || type == ItemRenderType.EQUIPPED
                    || type == ItemRenderType.INVENTORY
                    || type == ItemRenderType.EQUIPPED_FIRST_PERSON;
        } else {
            return false;
        }
    }

    @Override
    public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper) {
        return true;
    }

    @Override
    public void renderItem(ItemRenderType type, ItemStack item, Object... data) {
        if (type == ItemRenderType.INVENTORY) {
            RenderBlocks renderBlocks = (RenderBlocks) data[0];
            renderToInventory(item, renderBlocks);
        } else if (type == ItemRenderType.EQUIPPED || type == ItemRenderType.EQUIPPED_FIRST_PERSON) {
            renderEquipped(item, (RenderBlocks) data[0]);
        } else if (type == ItemRenderType.ENTITY) {
            renderEntity(item, (RenderBlocks) data[0]);
        } else {
            if (loggedError) {
                Log.warn("MachinePartRenderer.renderItem: Unsupported render type");
                loggedError = true;
            }
        }
    }

    private void renderEntity(ItemStack item, RenderBlocks renderBlocks) {
        GL11.glPushMatrix();
        GL11.glScalef(0.5f, 0.5f, 0.5f);
        renderToInventory(item, renderBlocks);
        GL11.glPopMatrix();
    }

    private void renderEquipped(ItemStack item, RenderBlocks renderBlocks) {
        GL11.glPushMatrix();
        GL11.glTranslatef(0.5F, 0.5F, 0.5F);
        renderToInventory(item, renderBlocks);
        GL11.glPopMatrix();
    }

    private void renderToInventory(ItemStack item, RenderBlocks renderBlocks) {
        GL11.glEnable(GL11.GL_ALPHA_TEST);
        renderBlocks.setOverrideBlockTexture(EnderIO.itemMachinePart.getIconFromDamage(item.getItemDamage()));
        renderBlocks.renderBlockAsItem(Blocks.stone, 0, 1.0F);
        renderBlocks.clearOverrideBlockTexture();
        GL11.glDisable(GL11.GL_ALPHA_TEST);
    }
}
