package crazypants.enderio.base.item.darksteel;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nonnull;

import org.lwjgl.opengl.GL11;

import crazypants.enderio.api.upgrades.IEquipmentData;
import crazypants.enderio.util.NNPair;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelShield;
import net.minecraft.client.renderer.BannerTextures;
import net.minecraft.client.renderer.BannerTextures.Cache;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntityItemStackRenderer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityBanner;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public final class DarkShieldRenderer extends TileEntityItemStackRenderer {

  public static final @Nonnull DarkShieldRenderer INSTANCE = new DarkShieldRenderer();

  /**
   * net.minecraft.client.renderer.RenderItem.RES_ITEM_GLINT is private
   */
  private static final @Nonnull ResourceLocation RES_ITEM_GLINT = new ResourceLocation("textures/misc/enchanted_item_glint.png");

  private static final @Nonnull Map<String, NNPair<ResourceLocation, BannerTextures.Cache>> TEX = new HashMap<>();

  private final @Nonnull TileEntityBanner banner = new TileEntityBanner();
  private final @Nonnull ModelShield modelShield = new ModelShield();

  private DarkShieldRenderer() {
  }

  @Override
  public void renderByItem(@Nonnull ItemStack stack, float partialTicks) {
    Item item = stack.getItem();
    if (item instanceof ItemDarkSteelShield) {

      final IEquipmentData data = ((ItemDarkSteelShield) item).getEquipmentData();
      String shieldBase = data.getTextureShieldBase();
      NNPair<ResourceLocation, Cache> tex = TEX.computeIfAbsent(shieldBase, base -> NNPair.of(new ResourceLocation(base + "_base_nopattern.png"),
          new BannerTextures.Cache("D" + data.getArmorMaterial().getName(), new ResourceLocation(base + "_base.png"), "textures/entity/shield/")));

      if (stack.getSubCompound("BlockEntityTag") != null) {
        banner.setItemValues(stack, true);
        ResourceLocation resourceLocation = tex.getRight().getResourceLocation(banner.getPatternResourceLocation(), banner.getPatternList(),
            banner.getColorList());
        if (resourceLocation == null) {
          resourceLocation = tex.getLeft();
        }
        Minecraft.getMinecraft().getTextureManager().bindTexture(resourceLocation);
      } else {
        Minecraft.getMinecraft().getTextureManager().bindTexture(tex.getLeft());
      }

      GlStateManager.pushMatrix();
      GlStateManager.scale(1.0F, -1.0F, -1.0F);
      modelShield.render();

      // work around for https://bugs.mojang.com/browse/MC-69683
      if (stack.hasEffect()) {
        // see net.minecraft.client.renderer.RenderItem.renderEffect(IBakedModel)
        GlStateManager.depthMask(false);
        GlStateManager.depthFunc(GL11.GL_EQUAL);
        GlStateManager.disableLighting();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_COLOR, GlStateManager.DestFactor.ONE);
        Minecraft.getMinecraft().getTextureManager().bindTexture(RES_ITEM_GLINT);
        GlStateManager.matrixMode(GL11.GL_TEXTURE);
        GlStateManager.pushMatrix();
        GlStateManager.scale(.8F, .8F, .8F);
        GlStateManager.translate((Minecraft.getSystemTime() >>> 3) % 3000L / 3000.0F, 0.0F, 0.0F); // reduce speed before scaling to avoid jerkyness
        GlStateManager.rotate(-50.0F, 0.0F, 0.0F, 1.0F);
        GlStateManager.color(0.65f, 0.65f, 0.65f, 1); // don't be that bright
        modelShield.render();
        GlStateManager.popMatrix();
        GlStateManager.pushMatrix();
        GlStateManager.scale(.8F, .8F, .8F);
        GlStateManager.translate(-((Minecraft.getSystemTime() >> 2) % 4873L / 4873.0F), 0.0F, 0.0F); // reduce speed before scaling to avoid jerkyness
        GlStateManager.rotate(10.0F, 0.0F, 0.0F, 1.0F);
        modelShield.render();
        GlStateManager.popMatrix();
        GlStateManager.matrixMode(GL11.GL_MODELVIEW);
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        GlStateManager.enableLighting();
        GlStateManager.depthFunc(GL11.GL_LEQUAL);
        GlStateManager.depthMask(true);
        Minecraft.getMinecraft().getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
      }

      GlStateManager.popMatrix();
    } else {
      throw new RuntimeException(stack + " is not one of our shields. Why should we render it?");
    }
  }

}
