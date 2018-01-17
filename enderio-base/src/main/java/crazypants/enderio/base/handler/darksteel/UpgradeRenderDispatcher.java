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
  public void doRenderLayer(@Nonnull AbstractClientPlayer player, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks,
      float netHeadYaw, float headPitch, float scale) {

    NNList.of(EntityEquipmentSlot.class).apply(new Callback<EntityEquipmentSlot>() {
      @Override
      public void apply(@Nonnull EntityEquipmentSlot slot) {
        ItemStack item = player.getItemStackFromSlot(slot);
        if (item.getItem() instanceof IDarkSteelItem) {
          for (IDarkSteelUpgrade upgrade : UpgradeRegistry.getUpgrades()) {
            if (upgrade instanceof IHasPlayerRenderer && upgrade.hasUpgrade(item)) {
              ((IHasPlayerRenderer) upgrade).getRender().doRenderLayer(renderPlayer, slot, item, player, limbSwing, limbSwingAmount, partialTicks, ageInTicks,
                  netHeadYaw, headPitch, scale);
            }
          }
        }
        if (item.getItem() instanceof IHasPlayerRenderer) {
          ((IHasPlayerRenderer) item.getItem()).getRender().doRenderLayer(renderPlayer, slot, item, player, limbSwing, limbSwingAmount, partialTicks,
              ageInTicks, netHeadYaw, headPitch, scale);
        }
      }
    });

    IInventory baubles = BaublesUtil.instance().getBaubles(player);
    if (baubles != null) {
      for (int i = 0; i < baubles.getSizeInventory(); i++) {
        ItemStack piece = baubles.getStackInSlot(i);
        if (piece.getItem() instanceof IHasPlayerRenderer) {
          ((IHasPlayerRenderer) piece.getItem()).getRender().doRenderLayer(renderPlayer, null, piece, player, limbSwing, limbSwingAmount, partialTicks,
              ageInTicks, netHeadYaw, headPitch, scale);
        }
      }
    }
  }

  @Override
  public boolean shouldCombineTextures() {
    return true;
  }

}
