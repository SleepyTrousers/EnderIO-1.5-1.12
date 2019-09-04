package crazypants.enderio.machines.machine.niard;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nonnull;

import com.enderio.core.common.vecmath.Vector4f;

import crazypants.enderio.base.Log;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class FluidColorUtil {

  private static final @Nonnull Map<ResourceLocation, Vector4f> CACHE = new HashMap<>();

  public static Vector4f getFluidColor(@Nonnull FluidStack stack, final Vector4f fallbackColor) {
    final ResourceLocation still = stack.getFluid().getStill(stack);
    if (still != null) {
      return CACHE.computeIfAbsent(still, rl -> {
        if (rl != null) {
          rl = new ResourceLocation(rl.getResourceDomain(), String.format("%s/%s%s", "textures", rl.getResourcePath(), ".png"));
          IResourceManager resourceManager = Minecraft.getMinecraft().getResourceManager();
          try (IResource iresource = resourceManager.getResource(rl)) {
            BufferedImage bufferedimage = TextureUtil.readBufferedImage(iresource.getInputStream());

            double rs = 0, gs = 0, bs = 0, as = 0;

            int size = bufferedimage.getWidth();
            for (int x = 0; x < size; x++) {
              for (int y = 0; y < size; y++) {
                final int color = bufferedimage.getRGB(x, y);
                final double r = ((color >> 16) & 0xFF) / 255f, g = ((color >> 8) & 0xFF) / 255f, b = (color & 0xFF) / 255f, a = ((color >> 24) & 0xFF) / 255f;
                rs += r * a * r * a;
                gs += g * a * g * a;
                bs += b * a * b * a;
                as += a * a;
              }
            }

            double r = Math.sqrt(rs / as);
            double g = Math.sqrt(gs / as);
            double b = Math.sqrt(bs / as);

            return new Vector4f(r, g, b, .4f);

          } catch (IOException e) {
            Log.warn("Could not load still texture for fluid " + stack + ". Using default color for range display instead.");
            e.printStackTrace();
          }
        }

        return fallbackColor;
      });
    }
    return fallbackColor;
  }

}
