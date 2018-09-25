package crazypants.enderio.base.render.util;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.imageio.ImageIO;

import crazypants.enderio.base.EnderIO;
import crazypants.enderio.base.Log;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public abstract class DynaTextureProvider {

  protected static final @Nonnull Queue<ResourceLocation> toFree = new ConcurrentLinkedQueue<>();
  protected static final @Nonnull List<DynaTextureProvider> instances = new ArrayList<>();

  protected final @Nonnull ResourceLocation resourceLocation;
  protected final @Nonnull int[] imageData;
  protected boolean valid = true;

  protected final @Nonnull ResourceLocation fallBackTexture;

  protected final @Nonnull DynamicTexture dynamicTexture;
  protected final @Nonnull TextureManager textureManager;
  protected final @Nonnull IResourceManager resourceManager;

  public DynaTextureProvider(int textureSize, @Nonnull ResourceLocation fallBackTexture) {
    this.textureManager = Minecraft.getMinecraft().getTextureManager();
    this.resourceManager = Minecraft.getMinecraft().getResourceManager();
    this.fallBackTexture = fallBackTexture;

    this.dynamicTexture = new DynamicTexture(textureSize, textureSize);
    this.imageData = this.dynamicTexture.getTextureData();
    this.resourceLocation = textureManager.getDynamicTextureLocation(EnderIO.DOMAIN, this.dynamicTexture);

    for (int i = 0; i < this.imageData.length; ++i) {
      this.imageData[i] = 0;
    }
    instances.add(this);
  }

  public void updateTexture() {
    if (valid) {
      updateTextureData();
      dynamicTexture.updateDynamicTexture();
    }
  }

  abstract protected void updateTextureData();

  public void bindTexture() {
    if (valid) {
      textureManager.bindTexture(resourceLocation);
    } else {
      textureManager.bindTexture(fallBackTexture);
    }
  }

  public void free() {
    if (valid) {
      textureManager.deleteTexture(resourceLocation);
      valid = false;
    }
    ResourceLocation r = toFree.poll();
    while (r != null) {
      textureManager.deleteTexture(r);
      r = toFree.poll();
    }
  }

  @Override
  protected void finalize() throws Throwable {
    if (valid) {
      toFree.add(resourceLocation);
      valid = false;
    }
    super.finalize();
  }

  protected @Nullable BufferedImage getTexture(@Nonnull ResourceLocation blockResource) {
    try {
      IResource iResource = resourceManager.getResource(blockResource);
      BufferedImage image = ImageIO.read(iResource.getInputStream());
      iResource.close();
      return image;
    } catch (IOException e) {
      Log.error("Failed to load " + blockResource + ": " + e);
    }
    return null;
  }

  protected @Nonnull BufferedImage resize(@Nonnull BufferedImage image, int size) {
    if (image.getWidth() != size) {
      BufferedImage resized = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
      Graphics2D g = resized.createGraphics();
      g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
      g.drawImage(image, 0, 0, size, size, 0, 0, image.getWidth(), image.getHeight(), null);
      g.dispose();
      return resized;
    } else {
      return image;
    }
  }

  @SideOnly(Side.CLIENT)
  @EventBusSubscriber(modid = EnderIO.MODID, value = Side.CLIENT)
  public static class Unloader {
    @SuppressWarnings({ "static-method" })
    @SubscribeEvent
    public static void unload(WorldEvent.Unload event) {
      if (event.getWorld() instanceof WorldClient) {
        for (DynaTextureProvider instance : instances) {
          instance.free();
        }
        instances.clear();
      }
    }
  }

}
