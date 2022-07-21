package crazypants.enderio.xp;

import com.enderio.core.client.render.ColorUtil;
import com.enderio.core.client.render.RenderUtil;
import crazypants.enderio.gui.IconEIO;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import org.lwjgl.opengl.GL11;

public class ExperienceBarRenderer {

    public static void render(Gui gui, int x, int y, int length, ExperienceContainer xpCont) {
        render(gui, x, y, length, xpCont, -1);
    }

    public static void render(Gui gui, int x, int y, int length, ExperienceContainer xpCont, int required) {

        String text = xpCont.getExperienceLevel() + "";
        int color = 8453920;
        boolean shadow = true;
        if (required > 0) {
            text += "/" + required;
            if (required > xpCont.getExperienceLevel()) {
                color = ColorUtil.getRGB(1f, 0, 0.1f);
                shadow = false;
            }
        }
        FontRenderer fr = Minecraft.getMinecraft().fontRenderer;
        int strX = x + length / 2 - fr.getStringWidth(text) / 2;
        fr.drawString(text, strX, y - 11, color, shadow);

        RenderUtil.bindTexture(IconEIO.TEXTURE);
        GL11.glColor3f(1, 1, 1);
        int xpScaled = xpCont.getXpBarScaled(length - 2);

        // x, y, u, v, width, height
        // start of 'slot'
        gui.drawTexturedModalRect(x, y, 0, 91, 1, 5);
        gui.drawTexturedModalRect(x + 1, y, 1, 91, length - 2, 5);
        gui.drawTexturedModalRect(x + length - 1, y, 125, 91, 1, 5);

        RenderUtil.renderQuad2D(x + 1, y + 1, 0, xpScaled, 3, ColorUtil.getRGB(0, 127, 14));
    }
}
