package crazypants.enderio.handler.darksteel;

import javax.annotation.Nonnull;

import crazypants.enderio.integration.baubles.BaublesUtil;
import crazypants.enderio.render.IHasPlayerRenderer;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class UpgradeRenderDispatcher implements LayerRenderer<AbstractClientPlayer> {

  private final @Nonnull RenderPlayer renderPlayer;

  UpgradeRenderDispatcher(@Nonnull RenderPlayer renderPlayer) {
    this.renderPlayer = renderPlayer;
  }

  @Override
  public void doRenderLayer(@Nonnull AbstractClientPlayer entitylivingbaseIn, float p_177141_2_, float p_177141_3_, float partialTicks, float p_177141_5_,
      float p_177141_6_, float p_177141_7_, float scale) {
    for (ItemStack piece : entitylivingbaseIn.inventory.armorInventory) {
      if (piece.getItem() instanceof IDarkSteelItem) {
        for (IDarkSteelUpgrade upg : DarkSteelRecipeManager.instance.getUpgrades()) {
          if (upg.hasUpgrade(piece)) {
            IRenderUpgrade render = upg.getRender();
            if (render != null) {
              render.doRenderLayer(renderPlayer, piece, entitylivingbaseIn, p_177141_2_, p_177141_3_, partialTicks, p_177141_5_, p_177141_6_, p_177141_7_,
                  scale);
            }
          }
        }
      }
      if (piece.getItem() instanceof IHasPlayerRenderer) {
        IRenderUpgrade render = ((IHasPlayerRenderer) piece.getItem()).getRender();
        if (render != null) {
          render.doRenderLayer(renderPlayer, piece, entitylivingbaseIn, p_177141_2_, p_177141_3_, partialTicks, p_177141_5_, p_177141_6_, p_177141_7_, scale);
        }
      }
    }
    IInventory baubles = BaublesUtil.instance().getBaubles(entitylivingbaseIn);
    if (baubles != null) {
      for (int i = 0; i < baubles.getSizeInventory(); i++) {
        ItemStack piece = baubles.getStackInSlot(i);
        if (piece.getItem() instanceof IHasPlayerRenderer) {
          IRenderUpgrade render = ((IHasPlayerRenderer) piece.getItem()).getRender();
          if (render != null) {
            render.doRenderLayer(renderPlayer, piece, entitylivingbaseIn, p_177141_2_, p_177141_3_, partialTicks, p_177141_5_, p_177141_6_, p_177141_7_, scale);
          }
        }
      }
    }
  }

  @Override
  public boolean shouldCombineTextures() {
    return true;
  }

}
