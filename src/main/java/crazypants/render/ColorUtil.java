package crazypants.render;

import java.awt.Color;

import crazypants.vecmath.Vector3f;
import crazypants.vecmath.Vector4f;

public final class ColorUtil {

  public static Vector4f toFloat(Color color) {
    float[] rgba = color.getComponents(null);
    return new Vector4f(rgba[0], rgba[1], rgba[2], rgba[3]);
  }

  public static Vector3f toFloat(int rgb) {
    int r = rgb >> 16 & 255;
    int g = rgb >> 8 & 255;
    int b = rgb & 255;
    return new Vector3f(r / 255F, g / 255F, b / 255F);
  }

  public static int getRGB(Color color) {
    return getRGB(color.getRed(), color.getGreen(), color.getBlue());
  }

  public static int getRGBA(Color color) {
    return getRGBA(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
  }

  public static int getARGB(Color color) {
    return getRGBA(color.getAlpha(), color.getRed(), color.getGreen(), color.getBlue());
  }

  public static int getRGB(Vector3f rgb) {
    return getRGB(rgb.x, rgb.y, rgb.z);
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

  private ColorUtil() {
  }

}
