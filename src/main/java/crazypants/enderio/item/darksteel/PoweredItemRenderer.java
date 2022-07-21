package crazypants.enderio.item.darksteel;

import cofh.api.energy.IEnergyContainerItem;
import com.enderio.core.client.render.ColorUtil;
import com.enderio.core.client.render.RenderUtil;
import com.enderio.core.common.vecmath.Vector4f;
import crazypants.enderio.item.darksteel.upgrade.EnergyUpgrade;
import java.awt.Color;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.IItemRenderer;
import org.lwjgl.opengl.GL11;

public class PoweredItemRenderer implements IItemRenderer {

    private RenderItem ri = new RenderItem();

    @Override
    public boolean handleRenderType(ItemStack item, ItemRenderType type) {
        return type == ItemRenderType.INVENTORY;
    }

    @Override
    public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper) {
        return false;
    }

    @Override
    public void renderItem(ItemRenderType type, ItemStack item, Object... data) {
        if (data != null && data.length > 0) {
            renderToInventory(item, (RenderBlocks) data[0]);
        }
    }

    public void renderToInventory(ItemStack item, RenderBlocks renderBlocks) {

        Minecraft mc = Minecraft.getMinecraft();
        ri.renderItemIntoGUI(mc.fontRenderer, mc.getTextureManager(), item, 0, 0, true);
        GL11.glDisable(GL11.GL_LIGHTING);

        if (isJustCrafted(item)) {
            return;
        }

        boolean hasEnergyUpgrade = EnergyUpgrade.loadFromItem(item) != null;
        int y = hasEnergyUpgrade ? 12 : 13;
        int bgH = hasEnergyUpgrade ? 4 : 2;
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        RenderUtil.renderQuad2D(2, y, 0, 13, bgH, ColorUtil.getRGB(Color.black));

        double maxDam = item.getMaxDamage();
        double dispDamage = item.getItemDamageForDisplay();
        y = hasEnergyUpgrade ? 14 : 13;
        renderBar(y, maxDam, dispDamage, Color.green, Color.red);

        if (hasEnergyUpgrade) {
            IEnergyContainerItem armor = (IEnergyContainerItem) item.getItem();
            maxDam = armor.getMaxEnergyStored(item);
            dispDamage = armor.getEnergyStored(item);
            y = 12;
            Color color = new Color(0x2D, 0xCE, 0xFA); // electric blue
            renderBar2(y, maxDam, maxDam - dispDamage, color, color);
        }

        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glEnable(GL11.GL_LIGHTING);
    }

    private boolean isJustCrafted(ItemStack item) {
        return EnergyUpgrade.loadFromItem(item) == null && item.getItemDamageForDisplay() == 0;
    }

    private void renderBar2(int y, double maxDam, double dispDamage, Color full, Color empty) {
        double ratio = dispDamage / maxDam;
        Vector4f fg = ColorUtil.toFloat(full);
        Vector4f ec = ColorUtil.toFloat(empty);
        fg.interpolate(ec, (float) ratio);
        Vector4f bg = ColorUtil.toFloat(Color.black);
        bg.interpolate(fg, 0.15f);

        int barLength = (int) Math.round(12.0 * (1 - ratio));

        RenderUtil.renderQuad2D(2, y, 0, 12, 1, bg);
        RenderUtil.renderQuad2D(2, y, 0, barLength, 1, fg);
    }

    private void renderBar(int y, double maxDam, double dispDamage, Color full, Color empty) {
        double ratio = dispDamage / maxDam;
        Vector4f fg = ColorUtil.toFloat(full);
        Vector4f ec = ColorUtil.toFloat(empty);

        fg.interpolate(ec, (float) ratio);

        Vector4f bg = new Vector4f(0.17, 0.3, 0.1, 0);

        int barLength = (int) Math.round(12.0 * (1 - ratio));
        RenderUtil.renderQuad2D(2, y, 0, 12, 1, bg);
        RenderUtil.renderQuad2D(2, y, 0, barLength, 1, fg);
    }

    private void renderBar(int y, double maxDam, double dispDamage) {
        int ratio = (int) Math.round(255.0D - dispDamage * 255.0D / maxDam);
        int fgCol = 255 - ratio << 16 | ratio << 8;
        int bgCol = (255 - ratio) / 4 << 16 | 16128;
        int barLength = (int) Math.round(12.0D - dispDamage * 12.0D / maxDam);
        RenderUtil.renderQuad2D(2, y, 0, 12, 1, bgCol);
        RenderUtil.renderQuad2D(2, y, 0, barLength, 1, fgCol);
    }
}
