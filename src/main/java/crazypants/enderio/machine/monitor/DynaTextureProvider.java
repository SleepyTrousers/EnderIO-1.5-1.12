package crazypants.enderio.machine.monitor;

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

import crazypants.enderio.EnderIO;
import crazypants.enderio.Log;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class DynaTextureProvider {

  protected static final int TEXSIZE = 32;

  protected static final @Nonnull Queue<ResourceLocation> toFree = new ConcurrentLinkedQueue<ResourceLocation>();
  protected static final @Nonnull List<DynaTextureProvider> instances = new ArrayList<DynaTextureProvider>();

  protected static final @Nonnull ResourceLocation pmon_screen = new ResourceLocation(EnderIO.DOMAIN, "textures/blocks/blockPMonScreen.png");
  protected static final @Nonnull int[] pmon_screen_data = new int[TEXSIZE * TEXSIZE];
  protected static final @Nonnull ResourceLocation pmon_color = new ResourceLocation(EnderIO.DOMAIN, "textures/blocks/blockPMonColor.png");
  protected static final @Nonnull int[] pmon_color_data = new int[TEXSIZE * TEXSIZE];
  protected static final ResourceLocation pmon_fallback = new ResourceLocation(EnderIO.DOMAIN, "textures/blocks/blockPMon.png");

  protected final @Nonnull TilePowerMonitor owner;
  protected final @Nonnull String id;
  protected @Nullable ResourceLocation resourceLocation;
  protected @Nonnull final int[] imageData;

  protected final @Nonnull DynamicTexture dynamicTexture;
  protected final @Nonnull TextureManager textureManager;
  protected final @Nonnull IResourceManager resourceManager;

  @SuppressWarnings("null")
  public DynaTextureProvider(@Nonnull TilePowerMonitor owner) {
    this.owner = owner;
    this.textureManager = Minecraft.getMinecraft().getTextureManager();
    this.resourceManager = Minecraft.getMinecraft().getResourceManager();

    this.id = EnderIO.DOMAIN + "pmon/" + owner.getPos().toLong();

    this.dynamicTexture = new DynamicTexture(TEXSIZE, TEXSIZE);
    this.imageData = this.dynamicTexture.getTextureData();
    this.resourceLocation = textureManager.getDynamicTextureLocation(id, this.dynamicTexture);

    for (int i = 0; i < this.imageData.length; ++i) {
      this.imageData[i] = 0;
    }
    loadTextures();
    updateTexture();
    instances.add(this);
  }

  protected static boolean texturesLoaded = false;

  protected void loadTextures() {
    if (!texturesLoaded) {
      BufferedImage pmon_screen_image = getTexture(pmon_screen);
      if (pmon_screen_image != null) {
        pmon_screen_image = resize(pmon_screen_image, TEXSIZE);
        pmon_screen_image.getRGB(0, 0, TEXSIZE, TEXSIZE, pmon_screen_data, 0, TEXSIZE);
      }
      BufferedImage pmon_color_image = getTexture(pmon_color);
      if (pmon_color_image != null) {
        pmon_color_image = resize(pmon_color_image, TEXSIZE);
        pmon_color_image.getRGB(0, 0, TEXSIZE, TEXSIZE, pmon_color_data, 0, TEXSIZE);
      }
      texturesLoaded = true;
      MinecraftForge.EVENT_BUS.register(new Unloader());
    }
  }

  protected static BufferedImage resize(BufferedImage image, int size) {
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

  protected BufferedImage getTexture(@Nonnull ResourceLocation blockResource) {
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

  public void updateTexture() {
    if (resourceLocation != null) {
      int[][] minmax = owner.getIconValues();

      for (int x = 0; x < TEXSIZE; x++) {
        for (int y = 0; y < TEXSIZE; y++) {
          imageData[y * TEXSIZE + x] = (x > 27 || TEXSIZE - y > minmax[1][x] * 23 / 63 + 5 || TEXSIZE - y < minmax[0][x] * 23 / 63 + 5 ? pmon_screen_data
              : pmon_color_data)[y * TEXSIZE + x];
        }
      }

      dynamicTexture.updateDynamicTexture();
    }
  }

  public void bindTexture() {
    if (resourceLocation != null) {
      textureManager.bindTexture(resourceLocation);
    } else {
      textureManager.bindTexture(pmon_fallback);
    }
  }

  public void free() {
    if (resourceLocation != null) {
      textureManager.deleteTexture(resourceLocation);
      resourceLocation = null;
    }
    ResourceLocation r = toFree.poll();
    while (r != null) {
      textureManager.deleteTexture(r);
      r = toFree.poll();
    }
  }

  @Override
  protected void finalize() throws Throwable {
    if (resourceLocation != null) {
      toFree.add(resourceLocation);
      resourceLocation = null;
    }
    super.finalize();
  }

  public static class Unloader {
    @SuppressWarnings({ "static-method"})
    @SubscribeEvent
    public void unload(WorldEvent.Unload event) {
      if (event.getWorld() instanceof WorldClient) {
        for (DynaTextureProvider instance : instances) {
          instance.free();
        }
        instances.clear();
      }
    }
  }

}
