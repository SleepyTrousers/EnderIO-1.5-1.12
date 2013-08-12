package crazypants.render;

import java.nio.FloatBuffer;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;

import org.lwjgl.opengl.GL11;

import crazypants.vecmath.Matrix4d;
import crazypants.vecmath.Vector3d;


public class RenderUtil {

  public static final Vector3d UP_V = new Vector3d(0, 1, 0);

  public static final Vector3d ZERO_V = new Vector3d(0, 0, 0);

  private static final FloatBuffer MATRIX_BUFFER = GLAllocation.createDirectFloatBuffer(16);

  public static void loadMatrix(Matrix4d mat) {
    MATRIX_BUFFER.rewind();
    MATRIX_BUFFER.put((float) mat.m00);
    MATRIX_BUFFER.put((float) mat.m01);
    MATRIX_BUFFER.put((float) mat.m02);
    MATRIX_BUFFER.put((float) mat.m03);
    MATRIX_BUFFER.put((float) mat.m10);
    MATRIX_BUFFER.put((float) mat.m11);
    MATRIX_BUFFER.put((float) mat.m12);
    MATRIX_BUFFER.put((float) mat.m13);
    MATRIX_BUFFER.put((float) mat.m20);
    MATRIX_BUFFER.put((float) mat.m21);
    MATRIX_BUFFER.put((float) mat.m22);
    MATRIX_BUFFER.put((float) mat.m23);
    MATRIX_BUFFER.put((float) mat.m30);
    MATRIX_BUFFER.put((float) mat.m31);
    MATRIX_BUFFER.put((float) mat.m32);
    MATRIX_BUFFER.put((float) mat.m33);
    MATRIX_BUFFER.rewind();
    GL11.glLoadMatrix(MATRIX_BUFFER);
  }

  public static TextureManager engine() {
    return Minecraft.getMinecraft().renderEngine;
  }

  public static final ResourceLocation BLOCK_TEX = TextureMap.field_110575_b;
  public static final ResourceLocation ITEM_TEX = TextureMap.field_110576_c;
  public static final ResourceLocation GLINT_TEX = new ResourceLocation("textures/misc/enchanted_item_glint.png");

  public static void bindItemTexture(ItemStack stack) {
    engine().func_110577_a(stack.getItemSpriteNumber() == 0 ? BLOCK_TEX : ITEM_TEX);
  }

  public static void bindItemTexture() {
    engine().func_110577_a(ITEM_TEX);
  }

  public static void bindBlockTexture() {
    engine().func_110577_a(BLOCK_TEX);
  }

  public static void bindGlintTexture() {
    engine().func_110577_a(BLOCK_TEX);
  }

  public static void bindTexture(String string) {
    engine().func_110577_a(new ResourceLocation(string));
  }

  public static void bindTexture(ResourceLocation tex) {
    engine().func_110577_a(tex);
  }

  public static FontRenderer fontRenderer() {
    return Minecraft.getMinecraft().fontRenderer;
  }

  public static float claculateTotalBrightnessForLocation(World worldObj, int xCoord, int yCoord, int zCoord) {
    int i = worldObj.getLightBrightnessForSkyBlocks(xCoord, yCoord, zCoord, 0);
    int j = i % 65536;
    float fromSun = worldObj.getSunBrightness(1);
    float fromLights = j / 255f;
    int k = i / 65536;
    float recievedPercent = worldObj.getLightBrightness(xCoord, yCoord, zCoord);

    float val = (fromSun + fromLights) * recievedPercent;
    val = MathHelper.clamp_float(val, 0, 1);
    return val;
  }

  public static float getColorMultiplierForFace(ForgeDirection face) {
    if (face == ForgeDirection.UP) {
      return 1;
    }
    if (face == ForgeDirection.DOWN) {
      return 0.5f;
    }
    if (face.offsetX != 0) {
      return 0.6f;
    }
    return 0.8f; // z
  }

  public static int setTesselatorBrightness(IBlockAccess world, int x, int y, int z) {
    Block block = Block.blocksList[world.getBlockId(x, y, z)];
    int res = block == null ? world.getLightBrightnessForSkyBlocks(x, y, z, 0) : block.getMixedBrightnessForBlock(world, x, y, z);
    Tessellator.instance.setBrightness(res);
    Tessellator.instance.setColorRGBA_F(1, 1, 1, 1);
    return res;
  }

}
