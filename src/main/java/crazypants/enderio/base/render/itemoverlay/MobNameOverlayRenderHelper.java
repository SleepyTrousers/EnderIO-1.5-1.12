package crazypants.enderio.base.render.itemoverlay;

import javax.annotation.Nonnull;

import org.lwjgl.input.Keyboard;

import crazypants.enderio.base.EnderIO;
import crazypants.enderio.util.CapturedMob;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class MobNameOverlayRenderHelper {

  @SideOnly(Side.CLIENT)
  public static void doItemOverlayIntoGUI(@Nonnull ItemStack stack, int xPosition, int yPosition) {
    if (EnderIO.proxy.getClientPlayer().isSneaking() || Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT)) {
      CapturedMob capturedMob = CapturedMob.create(stack);
      if (capturedMob != null) {
        String name = capturedMob.getDisplayName();
        int idx = (int) ((EnderIO.proxy.getTickCount() / 4) % name.length());
        name = (name + " " + name).substring(idx, idx + 3);
  
        FontRenderer fr = Minecraft.getMinecraft().getRenderManager().getFontRenderer();
  
        GlStateManager.disableLighting();
        GlStateManager.disableDepth();
        GlStateManager.disableBlend();
        GlStateManager.enableBlend();
        fr.drawStringWithShadow(name, xPosition + 8 - fr.getStringWidth(name) / 2, yPosition + 5, 0xFF0030B0);
        GlStateManager.enableLighting();
        GlStateManager.enableDepth();
        GlStateManager.enableBlend();
      }
    }
  }

}
