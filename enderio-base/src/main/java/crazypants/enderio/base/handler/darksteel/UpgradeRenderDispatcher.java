package crazypants.enderio.base.handler.darksteel;

import javax.annotation.Nonnull;

import com.enderio.core.common.util.NNList;
import com.enderio.core.common.util.NNList.Callback;

import crazypants.enderio.api.upgrades.IDarkSteelItem;
import crazypants.enderio.api.upgrades.IDarkSteelUpgrade;
import crazypants.enderio.api.upgrades.IHasPlayerRenderer;
import crazypants.enderio.base.integration.baubles.BaublesUtil;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.inventory.EntityEquipmentSlot;
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
  public void doRenderLayer(@Nonnull AbstractClientPlayer player, float p_177141_2_, float p_177141_3_, float partialTicks, float p_177141_5_,
      float p_177141_6_, float p_177141_7_, float scale) {

    NNList.of(EntityEquipmentSlot.class).apply(new Callback<EntityEquipmentSlot>() {
      @Override
      public void apply(@Nonnull EntityEquipmentSlot slot) {
        ItemStack item = player.getItemStackFromSlot(slot);
        if (item.getItem() instanceof IDarkSteelItem) {
          for (IDarkSteelUpgrade upgrade : UpgradeRegistry.getUpgrades()) {
            if (upgrade instanceof IHasPlayerRenderer && upgrade.hasUpgrade(item)) {
              ((IHasPlayerRenderer) upgrade).getRender().doRenderLayer(renderPlayer, item, player, p_177141_2_, p_177141_3_, partialTicks, p_177141_5_,
                  p_177141_6_, p_177141_7_, scale);
            }
          }
        }
        if (item.getItem() instanceof IHasPlayerRenderer) {
          ((IHasPlayerRenderer) item.getItem()).getRender().doRenderLayer(renderPlayer, item, player, p_177141_2_, p_177141_3_, partialTicks, p_177141_5_,
              p_177141_6_, p_177141_7_, scale);
        }
      }
    });

    IInventory baubles = BaublesUtil.instance().getBaubles(player);
    if (baubles != null) {
      for (int i = 0; i < baubles.getSizeInventory(); i++) {
        ItemStack piece = baubles.getStackInSlot(i);
        if (piece.getItem() instanceof IHasPlayerRenderer) {
          ((IHasPlayerRenderer) piece.getItem()).getRender().doRenderLayer(renderPlayer, piece, player, p_177141_2_, p_177141_3_, partialTicks, p_177141_5_,
              p_177141_6_, p_177141_7_, scale);
        }
      }
    }
  }

  @Override
  public boolean shouldCombineTextures() {
    return true;
  }

}
