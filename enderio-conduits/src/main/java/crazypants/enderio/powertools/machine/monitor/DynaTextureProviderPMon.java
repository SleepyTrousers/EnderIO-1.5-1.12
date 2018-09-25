package crazypants.enderio.powertools.machine.monitor;

import java.awt.image.BufferedImage;

import javax.annotation.Nonnull;

import crazypants.enderio.base.EnderIO;
import crazypants.enderio.base.render.util.DynaTextureProvider;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class DynaTextureProviderPMon extends DynaTextureProvider {

  public static final int TEXSIZE = 32;

  protected static final @Nonnull ResourceLocation pmon_screen = new ResourceLocation(EnderIO.DOMAIN, "textures/blocks/block_pm_on_screen.png");
  protected static final @Nonnull int[] pmon_screen_data = new int[TEXSIZE * TEXSIZE];
  protected static final @Nonnull ResourceLocation pmon_color = new ResourceLocation(EnderIO.DOMAIN, "textures/blocks/block_pm_on_color.png");
  protected static final @Nonnull int[] pmon_color_data = new int[TEXSIZE * TEXSIZE];
  protected static final @Nonnull ResourceLocation pmon_fallback = new ResourceLocation(EnderIO.DOMAIN, "textures/blocks/block_pm_on.png");

  protected final @Nonnull IDataProvider owner;

  public DynaTextureProviderPMon(@Nonnull IDataProvider owner) {
    super(TEXSIZE, pmon_fallback);
    this.owner = owner;
    loadStaticTextureData();
    updateTexture();
  }

  protected static boolean texturesLoaded = false;

  protected void loadStaticTextureData() {
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
    }
  }

  @Override
  public void updateTextureData() {
    int[][] minmax = owner.getIconValues();

    for (int x = 0; x < TEXSIZE; x++) {
      for (int y = 0; y < TEXSIZE; y++) {
        imageData[y * TEXSIZE + x] = (x > 27 || TEXSIZE - y > minmax[1][x] * 23 / 63 + 5 || TEXSIZE - y < minmax[0][x] * 23 / 63 + 5 ? pmon_screen_data
            : pmon_color_data)[y * TEXSIZE + x];
      }
    }
  }

  public static interface IDataProvider {

    @Nonnull
    BlockPos getLocation();

    @Nonnull
    int[][] getIconValues();

  }

}
