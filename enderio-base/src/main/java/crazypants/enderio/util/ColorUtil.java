package crazypants.enderio.util;

import java.awt.Color;

import javax.annotation.Nonnull;

import net.minecraft.client.renderer.GlStateManager;

public final class ColorUtil {

  public static int getRGB(@Nonnull Color color) {
    return getRGB(color.getRed(), color.getGreen(), color.getBlue());
  }

  public static int getRGBA(@Nonnull Color color) {
    return getRGBA(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
  }

  public static int getARGB(@Nonnull Color color) {
    return getRGBA(color.getAlpha(), color.getRed(), color.getGreen(), color.getBlue());
  }

  public static int getRGB(float r, float g, float b) {
    return getRGB((int) (r * 255), (int) (g * 255), (int) (b * 255));
  }

  public static int getRGBA(float r, float g, float b, float a) {
    return getRGBA((int) (r * 255), (int) (g * 255), (int) (b * 255), (int) (a * 255));
  }

  public static int getARGB(float r, float g, float b, float a) {
    return getARGB((int) (a * 255), (int) (r * 255), (int) (g * 255), (int) (b * 255));
  }

  public static int getRGB(int r, int g, int b) {
    return (r & 0xFF) << 16 | (g & 0xFF) << 8 | (b & 0xFF);
  }

  public static int getARGB(int r, int g, int b, int a) {
    return (a & 0xFF) << 24 | (r & 0xFF) << 16 | (g & 0xFF) << 8 | (b & 0xFF);
  }

  public static int getRGBA(int r, int g, int b, int a) {
    return (r & 0xFF) << 24 | (g & 0xFF) << 16 | (b & 0xFF) << 8 | (a & 0xFF);
  }

  public static void setGLColorFromInt(int color) {
    float red = (color >> 16 & 255) / 255.0F;
    float green = (color >> 8 & 255) / 255.0F;
    float blue = (color & 255) / 255.0F;
    GlStateManager.color(red, green, blue, 1.0F);
  }

  public static int toHex(int r, int g, int b) {
    int hex = 0;
    hex = hex | ((r) << 16);
    hex = hex | ((g) << 8);
    hex = hex | ((b));
    return hex;
  }

  private ColorUtil() {
  }

}
