package crazypants.enderio.base.item.darksteel;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nonnull;

import crazypants.enderio.api.upgrades.IEquipmentData;
import crazypants.enderio.util.NNPair;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelShield;
import net.minecraft.client.renderer.BannerTextures;
import net.minecraft.client.renderer.BannerTextures.Cache;
import net.minecraft.client.renderer.GlStateManager;
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

  private static final @Nonnull Map<String, NNPair<ResourceLocation, BannerTextures.Cache>> TEX = new HashMap<>();

  private final @Nonnull TileEntityBanner banner = new TileEntityBanner();
  private final @Nonnull ModelShield modelShield = new ModelShield();

  private DarkShieldRenderer() {
  }

  @Override
  public void renderByItem(@Nonnull ItemStack p_192838_1_, float partialTicks) {
    Item item = p_192838_1_.getItem();
    if (item instanceof ItemDarkSteelShield) {

      final IEquipmentData data = ((ItemDarkSteelShield) item).getEquipmentData();
      String shieldBase = data.getTextureShieldBase();
      NNPair<ResourceLocation, Cache> tex = TEX.computeIfAbsent(shieldBase, base -> NNPair.of(new ResourceLocation(base + "_base_nopattern.png"),
          new BannerTextures.Cache("D" + data.getArmorMaterial().getName(), new ResourceLocation(base + "_base.png"), "textures/entity/shield/")));

      if (p_192838_1_.getSubCompound("BlockEntityTag") != null) {
        banner.setItemValues(p_192838_1_, true);
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
      GlStateManager.popMatrix();
    } else {
      throw new RuntimeException(p_192838_1_ + " is not one of our shields. Why should we render it?");
    }
  }

}
